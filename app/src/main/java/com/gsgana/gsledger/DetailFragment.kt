package com.gsgana.gsledger

import android.content.Context
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<DetailFragmentBinding>(
            inflater, R.layout.detail_fragment, container, false
        )

//        val preValue_id = args.id

        binding.viewModel = detailViewModel

        binding.lifecycleOwner = viewLifecycleOwner

        setSpinnerUi(binding, detailViewModel)

        setEditTextUi(binding, detailViewModel)

        setRadioBtnUi(binding, detailViewModel)

        subscribeUi(binding, detailViewModel)

        return binding.root
    }

    private fun subscribeUi(binding: DetailFragmentBinding, viewModel: DetailViewModel) {

        detailViewModel.product.observe(viewLifecycleOwner) {
            val preValue = detailViewModel.product.value!!

            //Set RadioButton of Brand/weightUnit/packageType/currency RadioButton based on previous product
            if (preValue.metal == 0) binding.metalGoldRbtn.isChecked = true
            else if (preValue.metal == 1) binding.metalSilverRbtn.isChecked = true

            if (preValue.type == 0) binding.typeCoinRbtn.isChecked = true
            else if (preValue.type == 1) binding.typeBarRbtn.isChecked = true


            //Set Spinner of Brand/weightUnit/packageType/currency Spinner based on previous product
//            binding.brandSpinner.setSelection(preValue.brand)
            binding.weightUnitSpinner.setSelection(preValue.weightUnit)
            binding.packageTypeSpinner.setSelection(preValue.packageType)
            binding.currencySpinner.setSelection(preValue.currency)

            //Set EditText of weight/ quantity/ price based on previous product
            binding.weightEditText.setText(String.format("%.2f",preValue.weight))
            binding.quantityEditText.setText(preValue.quantity.toString())
            binding.priceEditText.setText(String.format("%.2f",preValue.price))

            //Set EditData_Button of Edit Button
            binding.callback = object : Callback {
                override fun add() {
                    product = viewModel.product.value!!
                    product.brand = preValue.brand
                    product.metal = preValue.metal
                    product.type = preValue.type
                    product.packageType = preValue.packageType
                    product.quantity = preValue.quantity
                    product.weight = preValue.weight
                    product.weightUnit = preValue.weightUnit
                    product.currency = preValue.currency
                    product.price = preValue.price
                    product.buyDate = preValue.buyDate
                    product.editDate = preValue.editDate
                    product.memo = binding.memoEditText.text.toString()
                    detailViewModel.addProduct(product)
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

    private fun setSpinnerUi(binding: DetailFragmentBinding, viewModel: DetailViewModel) {
        //set Spinner Adapter//
        val brand_adapter =
            ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, BRAND_ARRAY)
        binding.brandSpinner.adapter = brand_adapter
        binding.brandSpinner.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.brandSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                viewModel.product.value?.brand = position
            }
        }

        val weightUnit_adapter =
            ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, WEIGHTUNIT)
        binding.weightUnitSpinner.adapter = weightUnit_adapter
        binding.weightUnitSpinner.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.weightUnitSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.product.value?.weightUnit = position
                }
            }

        val packageType_adapter =
            ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, PACKAGETYPE)
        binding.packageTypeSpinner.adapter = packageType_adapter
        binding.packageTypeSpinner.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.packageTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.product.value?.packageType = position
                }
            }

        val currency_adapter =
            ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, CURRENCY)
        binding.currencySpinner.adapter = currency_adapter
        binding.currencySpinner.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.currencySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.product.value?.currency = position
                }
            }
    }

    private fun setEditTextUi(binding: DetailFragmentBinding, viewModel: DetailViewModel) {
        //set EditText Adapter//
        binding.weightEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    viewModel.product.value?.weight =
                        s.toString().toFloat()
                } else {
                    viewModel.product.value?.weight = 0.0f
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )
        binding.priceEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    viewModel.product.value?.price = s.toString().toFloat()
                } else {
                    viewModel.product.value?.price = 0.0f
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )

        binding.quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    viewModel.product.value?.quantity =
                        s.toString().toInt()
                } else {
                    viewModel.product.value?.quantity = 0
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )
    }

    private fun setRadioBtnUi(binding: DetailFragmentBinding, viewModel: DetailViewModel) {
        binding.metalRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.metalGold_rbtn) {
                viewModel.product.value?.metal = 0
            } else if (checkedId == R.id.metalSilver_rbtn) {
                viewModel.product.value?.metal = 1
            }
        }
        binding.typeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.typeCoin_rbtn) {
                viewModel.product.value?.type = 0
            } else if (checkedId == R.id.typeBar_rbtn) {
                viewModel.product.value?.type = 1
            }
        }

    }

}
