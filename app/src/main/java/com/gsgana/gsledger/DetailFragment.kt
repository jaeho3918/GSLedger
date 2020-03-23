package com.gsgana.gsledger

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.common.io.Resources.getResource
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.DetailFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.DetailViewModel


class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()

    private lateinit var product: Product
    private val detailViewModel: DetailViewModel by viewModels {
        InjectorUtils.provideDetailViewModelFactory(requireActivity(), args.id)
    }

    private lateinit var binding: DetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate<DetailFragmentBinding>(
            inflater, R.layout.detail_fragment, container, false
        )

//        val preValue_id = args.id


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

            val preValue = product

            val brand = product.brand.toLowerCase().replace(" ", "")
            val metal = METAL[product.metal].toLowerCase()
            val type = TYPE[product.type].toLowerCase()
            val metalType = METAL[product!!.metal] + " " + TYPE[product!!.type]
            var weight = 0

            val imgId = getResource(
                "drawable",
                "${brand}_${metal}${type}",
                context
            )
            if (imgId == 0) {
                binding.itemImage.setImageResource(
                    getResource(
                        "drawable",
                        "default_goldbar",
                        context
                    )//"Default_${METAL[item.metal]}${TYPE[item.type]}"
                )
            } else {
                binding.itemImage.setImageResource(imgId)
            }


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
//                    newproduct.memo = binding.memoEditText.text.toString()
                    detailViewModel.addProduct(newproduct)
                    view?.findNavController()?.navigateUp()
                }

                override fun del() {
                    detailViewModel.delProduct(args.id)
                    view?.findNavController()?.navigateUp()
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
