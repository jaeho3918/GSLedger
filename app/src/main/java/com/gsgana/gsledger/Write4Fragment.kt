package com.gsgana.gsledger

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gsgana.gsledger.databinding.FragmentWrite4Binding
import com.gsgana.gsledger.utilities.GRADE
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.PACKAGETYPE
import com.gsgana.gsledger.utilities.WEIGHTUNIT
import com.gsgana.gsledger.viewmodels.WriteViewModel
import java.util.*


class Write4Fragment : Fragment() {

    private lateinit var binding: FragmentWrite4Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel =
            ViewModelProviders.of(
                activity!!,
                InjectorUtils.provideWriteViewModelFactory(activity!!, null)
            )
                .get(WriteViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentWrite4Binding>(
            inflater, R.layout.fragment_write4, container, false
        )
        binding.layout4.setOnClickListener {
            findNavController().navigate(R.id.action_write4Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()
        }
        binding.layout4r.setOnClickListener {
            findNavController().navigate(R.id.action_write4Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()
        }

        binding.moveTo4.setOnClickListener { findNavController().navigate(R.id.action_write4Fragment_to_write5Fragment) }

        Toast.makeText(activity, viewModel.product.toString(), Toast.LENGTH_LONG).show()


        setSpinnerUi(binding, viewModel)
        setPickerUi(binding, viewModel)

        return binding.root
    }


    private fun setSpinnerUi(
        binding: FragmentWrite4Binding,
        viewModel: WriteViewModel,
        array: Array<String>? = null,
        table: Array<String>? = null
    ) {
        var adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, GRADE
            )
        binding.gradeSpinner.adapter = adapter
        binding.gradeSpinner.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.gradeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.gradeField.value = position
                    binding.gradePicker.visibility =
                        when (position) {
                            0 -> INVISIBLE
                            1 -> VISIBLE
                            2 -> VISIBLE
                            else -> INVISIBLE
                        }

                }
            }
        binding.gradeSpinner.setSelection(viewModel.gradeField.value ?: 0)

    }


    private fun setPickerUi(
        binding: FragmentWrite4Binding,
        viewModel: WriteViewModel
    ) {
        var cal = Calendar.getInstance()
        val _year = cal.get(Calendar.YEAR)
        val _month = cal.get(Calendar.MONTH)
        val _date = cal.get(Calendar.DATE)

        binding.datePicker1.init(
            _year, _month, _date
        ) { _: DatePicker, i: Int, i1: Int, i2: Int ->
            viewModel.dateField.value = "$i/${i1 + 1}/$i2"
        }
        viewModel.dateField.value = "$_year/${_month + 1}/$_date"

        val date = viewModel.dateField.value?.split("/")
        if (!date.isNullOrEmpty()) {
            binding.datePicker1.init(
                date?.get(0)!!.toInt(), (date?.get(1)!!.toInt() - 1), date?.get(2)!!.toInt()
            ) { _: DatePicker, i: Int, i1: Int, i2: Int ->
                viewModel.dateField.value = "$i/${i1 + 1}/$i2"
            }
        }

        binding.gradePicker.minValue = 1
        binding.gradePicker.maxValue = 70
        binding.gradePicker.wrapSelectorWheel = false
        binding.gradePicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.gradePicker.setOnValueChangedListener { _: NumberPicker?, _: Int, newVal: Int ->
            viewModel.gradeNumField.value = newVal
        }
        binding.gradePicker.value = viewModel.gradeNumField.value ?: 70

        val year = Calendar.getInstance().get(Calendar.YEAR)
        binding.yearSeriesPicker.minValue = 1992
        binding.yearSeriesPicker.maxValue = year //
        binding.yearSeriesPicker.wrapSelectorWheel = false
        binding.yearSeriesPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.yearSeriesPicker.setOnValueChangedListener { _: NumberPicker?, _: Int, newVal: Int ->
            viewModel.yearSeriesField.value = newVal.toString()
        }
        binding.yearSeriesPicker.value =
            viewModel.yearSeriesField.value?.toInt() ?: year

    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }
}
