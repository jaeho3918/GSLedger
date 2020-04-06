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
import android.widget.Toast
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
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.METAL
import com.gsgana.gsledger.utilities.TYPE
import com.gsgana.gsledger.utilities.WEIGHTUNITBRAND
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
                    val data = p0?.value as HashMap<String, Double>
                    if (product.metal == 0) {
                        pre = (data["AU"] ?: 0.0).toFloat()
                    } else if (product.metal == 1) {
                        pre = (data["AG"] ?: 0.0).toFloat()
                    }
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
                    binding.productItemPl.text =
                        ((pre!! - product.pre) / (product.pre) * 100).toString()
                }, 500
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
//                            Toast.makeText(
//                                context,
//                                "예를 선택했습니다.",
//                                Toast.LENGTH_LONG
//                            ).show()
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
//
//    private fun setSpinnerUi(binding: DetailFragmentBinding, viewModel: DetailViewModel) {
//        //set Spinner Adapter//
//        val brand_adapter =
//            ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, BRAND_ARRAY)
//        binding.brandSpinner.adapter = brand_adapter
//        binding.brandSpinner.dropDownVerticalOffset = dipToPixels(53f).toInt()
//        binding.brandSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
////                viewModel.getProduct().value?.brand = position
//            }
//        }
//
//        val weightUnit_adapter =
//            ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, WEIGHTUNIT)
//        binding.weightUnitSpinner.adapter = weightUnit_adapter
//        binding.weightUnitSpinner.dropDownVerticalOffset = dipToPixels(53f).toInt()
//        binding.weightUnitSpinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) {}
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    viewModel.getProduct().value?.weightUnit = position
//                }
//            }
//
//        val packageType_adapter =
//            ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, PACKAGETYPE)
//        binding.packageTypeSpinner.adapter = packageType_adapter
//        binding.packageTypeSpinner.dropDownVerticalOffset = dipToPixels(53f).toInt()
//        binding.packageTypeSpinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) {}
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    viewModel.getProduct().value?.packageType = position
//                }
//            }
//
//        val currency_adapter =
//            ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, CURRENCY)
//        binding.currencySpinner.adapter = currency_adapter
//        binding.currencySpinner.dropDownVerticalOffset = dipToPixels(53f).toInt()
//        binding.currencySpinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) {}
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    viewModel.getProduct().value?.currency = position
//                }
//            }
//    }
//
//    private fun setEditTextUi(binding: DetailFragmentBinding, viewModel: DetailViewModel) {
//        //set EditText Adapter//
//        binding.weightEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString() != "") {
//                    viewModel.getProduct().value?.weight =
//                        s.toString().toFloat()
//                } else {
//                    viewModel.getProduct().value?.weight = 0.0f
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        }
//        )
//        binding.priceEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString() != "") {
//                    viewModel.getProduct().value?.price = s.toString().toFloat()
//                } else {
//                    viewModel.getProduct().value?.price = 0.0f
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        }
//        )
//
//        binding.quantityEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString() != "") {
//                    viewModel.getProduct().value?.quantity =
//                        s.toString().toInt()
//                } else {
//                    viewModel.getProduct().value?.quantity = 0
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        }
//        )
//    }
//
//    private fun setRadioBtnUi(binding: DetailFragmentBinding, viewModel: DetailViewModel) {
//        binding.metalRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//            if (checkedId == R.id.metalGold_rbtn) {
//                viewModel.getProduct().value?.metal = 0
//            } else if (checkedId == R.id.metalSilver_rbtn) {
//                viewModel.getProduct().value?.metal = 1
//            }
//        }
//        binding.typeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//            if (checkedId == R.id.typeCoin_rbtn) {
//                viewModel.getProduct().value?.type = 0
//            } else if (checkedId == R.id.typeBar_rbtn) {
//                viewModel.getProduct().value?.type = 1
//            }
//        }
//
//    }
}
