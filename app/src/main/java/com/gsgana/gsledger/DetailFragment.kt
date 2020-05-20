package com.gsgana.gsledger

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
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
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.functions.FirebaseFunctions
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.DetailFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.DetailViewModel
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.detail_fragment.*
import kotlinx.android.synthetic.main.marker_view.view.*


@Suppress("UNCHECKED_CAST")
class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()

    private val REAL_DB_PATH = "sYTVBn6F18VT6Ykw6L"
    private val databaseRef = FirebaseDatabase.getInstance()

    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    private lateinit var product: Product
    private val detailViewModel: DetailViewModel by viewModels {
        InjectorUtils.provideDetailViewModelFactory(requireActivity(), args.id)
    }

    private lateinit var binding: DetailFragmentBinding
    private var realData1: HashMap<String, Double>? = null

    private var pre: Float? = null

    private var preValue: Product? = null
    private var brand: String? = null
    private var buf_brand: String? = null
    private var buf_weight: String? = null


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

        subscribeUi(binding, detailViewModel, context)

        return binding.root
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun subscribeUi(
        binding: DetailFragmentBinding,
        viewModel: DetailViewModel,
        context: Context?
    ) {

        detailViewModel.getProduct().observe(viewLifecycleOwner) { product ->

            binding.viewModel = detailViewModel
            binding.lifecycleOwner = viewLifecycleOwner

            databaseRef.getReference(REAL_DB_PATH)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        realData1 = p0.value as HashMap<String, Double>
                        if (product.metal == 0) {
                            pre = (realData1!!["AU"] ?: 0.0).toFloat()
                        } else if (product.metal == 1) {
                            pre = (realData1!!["AG"] ?: 0.0).toFloat()
                        }
                        realData1!!["USD"] = 1.0
                    }
                })

            val grade = product.grade
            val gradeNum = product.gradeNum.toString()
            if (grade != "None") {
                if (grade.substring(0..3) == "PCGS") {
                    binding.certImage.setImageResource(R.drawable.ic_pg)
                    binding.productItemGrade.setTextColor(
                        resources.getColor(
                            R.color.font_pcgs,
                            null
                        )
                    )
                    binding.productItemGradeNum.setTextColor(
                        resources.getColor(
                            R.color.font_pcgs,
                            null
                        )
                    )

                } else if (grade.substring(0..2) == "NGC") {
                    binding.certImage.setImageResource(R.drawable.ic_ngc)
                    binding.productItemGrade.setTextColor(
                        resources.getColor(
                            R.color.font_ngc,
                            null
                        )
                    )
                    binding.productItemGradeNum.setTextColor(
                        resources.getColor(
                            R.color.font_ngc,
                            null
                        )
                    )
                }
                binding.certLabel.text = "${grade} ${gradeNum}"
            }

            preValue = product
            brand = product.brand.toLowerCase()
            val metal = METAL[product.metal].toLowerCase()
            val type = TYPE[product.type].toLowerCase()

            buf_brand = if (product.brand == "Default") {
                ""
            } else {
                product.brand
            }

            buf_weight = when (product.weight) {
                1f -> "1"
                0.05f -> "1/20"
                0.1f -> "1/10"
                0.4f -> "4/10"
                0.5f -> "1/2"
                else -> "1"
            }
            //
            product_item_brand.text =
                "${product.year} $buf_weight${WEIGHTUNIT[product.weightUnit]} ${METAL[product.metal]}${TYPE[product.type]} $buf_brand"

            binding.productItemEstimate.text = resources.getString(R.string.estimate)
            binding.productItemEstimate.visibility = View.VISIBLE

            brand = (brand ?: "default").toLowerCase().replace(" ", "").replace("'", "")
                .replace(".", "").replace("-", "")

            val imgId = "drawable".getResource(
                "${brand}_${metal[0]}${type[0]}",
                context!!
            )
            if (imgId == 0) {
                binding.itemImage.setImageResource(
                    "drawable".getResource(
                        "ic_default_${metal}${type}",
                        context
                    )
                )
            } else {
                binding.itemImage.setImageResource(imgId)
            }

            if (product.grade != "None") binding.gradeLayout.visibility = View.VISIBLE

            Handler().postDelayed(
                {
                    if (!realData1.isNullOrEmpty()) {
                        val product_currency = realData1!![CURRENCY[product.currency]]
                        val realData =
                            realData1!![METALCODE[product.metal]]!!.toFloat() * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                        val buyPrice = product.prePrice / product_currency!!

                        setPriceColor(
                            context,
                            (realData * realData1!![CURRENCY[product.currency]]!!),
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
                            (realData * realData1!![CURRENCY[product.currency]]!! / (PACKAGENUM[product.packageType] * product.quantity)),
                            "pricefloat",
                            binding.productItemPerprice
                        )

                        val grade = product.grade
                        val gradeNum = product.gradeNum.toString()
                        if (grade != "None") {
                            if (grade.substring(0..3) == "PCGS") {
                                binding.certImage.setImageResource(R.drawable.ic_pg)

                                binding.productItemGrade.setTextColor(
                                    context.getColor(
                                        R.color.font_pcgs
                                    ) ?: Color.parseColor("#4f9ccc")

                                )

                                binding.productItemGradeNum.setTextColor(
                                    context.getColor(
                                        R.color.font_pcgs
                                    ) ?: Color.parseColor("#4f9ccc")

                                )

                                binding.certLabel.text = "${grade} ${gradeNum}"


                            } else if (grade.substring(0..2) == "NGC") {


                                binding.productItemGrade.setTextColor(
                                    context.getColor(
                                        R.color.font_ngc
                                    ) ?: Color.parseColor("#95795b")
                                )

                                binding.productItemGradeNum.setTextColor(
                                    context.getColor(
                                        R.color.font_ngc
                                    ) ?: Color.parseColor("#95795b")
                                )

                                binding.certLabel.text = "${grade} ${gradeNum}"

                            }
                        }
                        val date_buf = product.buyDate.split("/").toMutableList()
                        if (date_buf[1].toInt() < 10) date_buf[1] = "0" + date_buf[1]
                        if (date_buf[2].toInt() < 10) date_buf[2] = "0" + date_buf[2]

                        getChart("${date_buf[0]}${date_buf[1]}${date_buf[2]}", product.metal)
                            .addOnSuccessListener { data ->
                                if (!data.isNullOrEmpty()) {
                                    viewModel.setChartDate(data["date"] as ArrayList<String>)
                                    getdetailChart(binding, viewModel, context, data, realData1!!)
                                }
                            }

                        binding.productItemPlCurrency1.text = CURRENCYSYMBOL[product.currency]
                        binding.productItemPlCurrency2.text = CURRENCYSYMBOL[product.currency]
                        binding.productItemProgress.visibility = View.GONE
                    }
                }, 800
            )


            //Set EditData_Button of Edit Button
            binding.callback = object : Callback {
                override fun add() {
                    val newproduct = viewModel.getProduct().value!!
                    newproduct.brand = preValue!!.brand
                    newproduct.metal = preValue!!.metal
                    newproduct.type = preValue!!.type
                    newproduct.packageType = preValue!!.packageType
                    newproduct.quantity = preValue!!.quantity
                    newproduct.weight = preValue!!.weight
                    newproduct.weightUnit = preValue!!.weightUnit
                    newproduct.currency = preValue!!.currency
                    newproduct.price = preValue!!.price
                    newproduct.buyDate = preValue!!.buyDate
                    newproduct.editDate = preValue!!.editDate
                    newproduct.memo = binding.productItemMemo.text.toString()
                    detailViewModel.addProduct(newproduct)
                    view?.findNavController()?.navigateUp()
                }

                override fun del() {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle(resources.getString(R.string.caution))
                    builder.setMessage(resources.getString(R.string.delProduct))
                    builder.setPositiveButton(
                        resources.getString(R.string.delete)
                    ) { _, _ ->
                        detailViewModel.delProduct(args.id)
                        view?.findNavController()?.navigateUp()
                    }
                    builder.setNegativeButton(
                        resources.getString(R.string.no)
                    ) { _, _ ->
                    }
                    builder.show()

                }
            }
        }
    }

    interface Callback {
        fun add()
        fun del()
    }

    private fun String.getResource(resName: String, context: Context): Int {
        val resContext: Context = context.createPackageContext(context.packageName, 0)
        val res: Resources = resContext.resources
        val id: Int = res.getIdentifier(resName, this, context.packageName)
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


    private fun getChart(buyDate: String, metal: Int): Task<Map<String, ArrayList<*>>> {
        val data = hashMapOf(
            "buyDate" to buyDate,
            "metal" to metal
        )
        return functions
            .getHttpsCallable("getDetailChart")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.

                val result = task.result?.data as Map<String, ArrayList<*>>
                result
            }
    }
}


