package com.gsgana.gsledger

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.FragmentWrite2Binding
import com.gsgana.gsledger.databinding.FragmentWrite3Binding
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.PACKAGETYPE
import com.gsgana.gsledger.utilities.WEIGHTUNIT
import com.gsgana.gsledger.viewmodels.WriteViewModel


class Write3Fragment : Fragment() {

    private lateinit var binding: FragmentWrite3Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel =
            ViewModelProviders.of(activity!!, InjectorUtils.provideWriteViewModelFactory(activity!!,null))
                .get(WriteViewModel::class.java)


        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_write3, container, false
        )
        binding.layout3.setOnClickListener {
            findNavController().navigate(R.id.action_write3Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()
        }
        binding.layout3r.setOnClickListener {
            findNavController().navigate(R.id.action_write3Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()
        }

        binding.moveTo3.setOnClickListener {
            findNavController().navigate(R.id.action_write3Fragment_to_write4Fragment)
        }

        binding.moveTo3r.setOnClickListener { findNavController().navigate(R.id.action_write3Fragment_to_write2Fragment) }

        setSpinnerUi(binding, viewModel)
        setEditTextUi(binding, viewModel)

        viewModel.weightCalculator.observe(this) {
            binding.weightCalculate1.text = it.toString()
        }




        return binding.root
    }

    private fun setSpinnerUi(
        binding: FragmentWrite3Binding,
        viewModel: WriteViewModel,
        array: Array<String>? = null,
        table: Array<String>? = null
    ) {
        if (viewModel.weightUnit.value != null) {
            if (viewModel.weightUnit.value!! > 0) {
                binding.weightUnitSpinner1.setSelection(viewModel.weightUnit.value!!.toInt())
            }
        }
        if (viewModel.packageType.value != null) {
            if (viewModel.packageType.value!! > 0) {
                binding.packageTypeSpinner1.setSelection(viewModel.packageType.value!!.toInt())
            }
        }
        var adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, WEIGHTUNIT
            )
        binding.weightUnitSpinner1.adapter = adapter
        binding.weightUnitSpinner1.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.weightUnitSpinner1.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.weightUnitField.value = position

                }
            }
        if (viewModel.weightUnitField.value!! > 0) {
            binding.weightUnitSpinner1.setSelection(viewModel.weightUnitField.value!!)
        }
        adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, PACKAGETYPE
            )

        binding.packageTypeSpinner1.adapter = adapter
        binding.packageTypeSpinner1.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.packageTypeSpinner1.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.product.packageType = position
                    viewModel.packageTypeField.value = position
                }
            }
        if (viewModel.packageTypeField.value!! > 0) {
            binding.packageTypeSpinner1.setSelection(viewModel.packageTypeField.value!!)
        }
    }

    private fun setEditTextUi(
        binding: FragmentWrite3Binding,
        viewModel: WriteViewModel,
        array: Array<String>? = null,
        table: Array<String>? = null
    ) {
        binding.weightEditText1.setText(viewModel.weightField1.value ?: "")
        binding.weightEditText2.setText(viewModel.weightField2.value ?: "")
        binding.quantityEditText1.setText(viewModel.quantityField.value ?: "1")

        binding.weightEditText1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    viewModel.weightField1.value = s.toString()
                } else {
                    viewModel.weightField1.value = ""
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )
        binding.weightEditText2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    viewModel.weightField2.value = s.toString()
                } else {
                    viewModel.weightField2.value = ""
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )
        binding.quantityEditText1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    viewModel.quantityField.value = s.toString()

                } else {
                    viewModel.quantityField.value = "1"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }
}
