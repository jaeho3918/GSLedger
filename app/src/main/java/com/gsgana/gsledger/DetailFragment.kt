package com.gsgana.gsledger

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.firebase.database.*
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.DetailFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.DetailViewModel
import kotlinx.android.synthetic.main.detail_fragment.*


class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()

    private val REAL_DB_PATH = "sYTVBn6F18VT6Ykw6L"
    private val databaseRef = FirebaseDatabase.getInstance().getReference(REAL_DB_PATH)

    private lateinit var product: Product
    private val detailViewModel: DetailViewModel by viewModels {
        InjectorUtils.provideDetailViewModelFactory(requireActivity(), args.id)
    }

    private lateinit var binding: DetailFragmentBinding
    private var data: HashMap<String, Double>? = null

    private var pre: Float? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate<DetailFragmentBinding>(
            inflater, R.layout.detail_fragment, container, false
        )

        binding.detailBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailFragment_to_homeViewPagerFragment)
        }

        subscribeUi(binding, detailViewModel, context!!)

        return binding.root
    }

    private fun subscribeUi(
        binding: DetailFragmentBinding,
        viewModel: DetailViewModel,
        context: Context
    ) {

        detailViewModel.getProduct().observe(viewLifecycleOwner) { product ->

            binding.viewModel = detailViewModel
            binding.lifecycleOwner = viewLifecycleOwner

            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    data = p0?.value as HashMap<String, Double>
                    if (product.metal == 0) {
                        pre = (data!!["AU"] ?: 0.0).toFloat()
                    } else if (product.metal == 1) {
                        pre = (data!!["AG"] ?: 0.0).toFloat()
                    }
                    data!!["USD"] = 1.0
                }
            })

            val preValue = product

            var brand = product.brand.toLowerCase()
            val metal = METAL[product.metal].toLowerCase()
            val type = TYPE[product.type].toLowerCase()

            val buf_brand = if (product.brand == "Default") {
                ""
            } else {
                product.brand
            }

            val buf_weight = when (product.weight) {
                1f -> "1"
                0.05f -> "1/20"
                0.1f -> "1/10"
                0.4f -> "4/10"
                0.5f -> "1/2"
                else -> "1"
            }
            //
            product_item_brand.text =
                product.year.toString() + " " + buf_weight + WEIGHTUNITBRAND[product.weightUnit] + " " + METAL[product.metal] + TYPE[product.type] + " " + buf_brand

            brand = (brand ?: "default").toLowerCase().replace(" ", "").replace("'", "")
                .replace(".", "").replace("-", "")

            val imgId = getResource(
                "drawable",
                "${brand}_${metal[0]}${type[0]}",
                context
            )
            if (imgId == 0) {
                binding.itemImage.setImageResource(
                    getResource(
                        "drawable",
                        "ic_default_${metal}${type}",
                        context!!
                    )
                )
            } else {
                binding.itemImage.setImageResource(imgId)
            }

            Handler().postDelayed(
                {
                    if (!data.isNullOrEmpty()) {
                        val product_currency = data!![CURRENCY[product.currency]]
                        val realData =
                            data!![METALCODE[product.metal]]!!.toFloat() * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                        val buyPrice = product.prePrice / product_currency!!
                        setPriceColor(
                            context,
                            (realData * data!![CURRENCY[product.currency]]!!),
                            "pricefloat",
                            binding.productItemCurrentPrice
                        )
                        setPriceColor(
                            context,
                            ((realData - buyPrice) / (buyPrice) * 100),
                            "pl",
                            binding.productItemPl
                        )

                        setPriceColor(
                            context,
                            (realData * data!![CURRENCY[product.currency]]!!/ (PACKAGENUM[product.packageType] * product.quantity)),
                            "pricefloat",
                            binding.productItemPerprice
                        )

                        binding.productItemProgress.visibility = View.GONE
                    }

                }, 800
            )


            //Set EditData_Button of Edit Button
            binding.callback = object : Callback {
                override fun add() {
                    val newproduct = viewModel.getProduct().value!!
                    newproduct.brand = preValue.brand
                    newproduct.metal = preValue.metal
                    newproduct.type = preValue.type
                    newproduct.packageType = preValue.packageType
                    newproduct.quantity = preValue.quantity
                    newproduct.weight = preValue.weight
                    newproduct.weightUnit = preValue.weightUnit
                    newproduct.currency = preValue.currency
                    newproduct.price = preValue.price
                    newproduct.buyDate = preValue.buyDate
                    newproduct.editDate = preValue.editDate
                    newproduct.memo = binding.productItemMemo.text.toString()
                    detailViewModel.addProduct(newproduct)
                    view?.findNavController()?.navigateUp()
                }

                override fun del() {

                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle("AlertDialog Title")
                    builder.setMessage("AlertDialog Content")
                    builder.setPositiveButton("Delete",
                        DialogInterface.OnClickListener { _, _ ->
                            detailViewModel.delProduct(args.id)
                            view?.findNavController()?.navigateUp()
                        })
                    builder.setNegativeButton("No",
                        DialogInterface.OnClickListener { _, _ ->
                        })
                    builder.show()

                }
            }
        }
    }

    interface Callback {
        fun add()
        fun del()
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    private fun getResource(type: String, resName: String, context: Context): Int {

        val resContext: Context = context.createPackageContext(context.packageName, 0)
        val res: Resources = resContext.resources
        val id: Int = res.getIdentifier(resName, type, context.packageName)

        return id
    }


    private fun setPriceColor(
        context: Context,
        price: Double,
        type: String,
        textView: TextView,
        style: Int = 0,
        plSwitch: Int = 1
    ): Int {

        if (plSwitch == 0) {
            textView.visibility = View.INVISIBLE
            return 0
        }
        val red = ContextCompat.getColor(context, R.color.mu1_data_down)
        val green = ContextCompat.getColor(context, R.color.mu1_data_up)
        val blue = ContextCompat.getColor(context, R.color.mu2_data_down)

        val string = when (type) {
            "priceint" -> {
                String.format("%,.0f", price)
            }

            "pricefloat" -> {
                String.format("%,.2f", price)
            }

            "pl" -> {
                when {
                    price > 0.01 -> {
                        if (style == 0) textView.setTextColor(green) else textView.setTextColor(
                            red
                        )
                        "(+" + String.format("%,.2f", price) + "%)"
                    }
                    price < -0.01 -> {
                        if (style == 0) textView.setTextColor(red) else textView.setTextColor(
                            blue
                        )
                        "(" + String.format("%,.2f", price) + "%)"
                    }
                    else -> "( 0.00%)"
                }
            }
            "pricepl" -> {
                when {
                    price > 1 -> {
                        if (style == 0) textView.setTextColor(green) else textView.setTextColor(
                            red
                        )
                        "+" + String.format("%,.0f", price)
                    }
                    price < -1 -> {
                        if (style == 0) textView.setTextColor(red) else textView.setTextColor(
                            blue
                        )
                        "" + String.format("%,.0f", price)
                    }
                    else -> "0"
                }
            }
            else -> {
                ""
            }
        }
        textView.text = string
        return 1
    }
}
