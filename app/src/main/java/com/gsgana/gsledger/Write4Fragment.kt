@file:Suppress("DEPRECATION")

package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.gsgana.gsledger.databinding.FragmentWrite4Binding
import com.gsgana.gsledger.databinding.FragmentWrite5Binding
import com.gsgana.gsledger.utilities.CURR_NAME
import com.gsgana.gsledger.utilities.GRADE
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.PREF_NAME
import com.gsgana.gsledger.viewmodels.WriteViewModel
import java.util.*


class Write4Fragment : Fragment() {

    private val NEW_LABEL = "RECSHenWYqdadfXOog"
    private val NEW_ENCRYPT = "X67LWGmYAc3rlCbmPe"
    private val NUMBER = "HYf75f2q2a36enW18b"
    private lateinit var sf: SharedPreferences

    private lateinit var binding: FragmentWrite4Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sf = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

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

        binding.moveTo4.setOnClickListener {

            binding.moveTo4.isEnabled = false
            binding.summitProgress.visibility = VISIBLE
            binding.moveTo4.text = ""
            viewModel.setcurrencyField(
                activity!!.getSharedPreferences(
                    PREF_NAME,
                    Context.MODE_PRIVATE
                ).getInt(CURR_NAME, 0)
            )

            getPriceData(
                viewModel, "1.1", sf.getString(NEW_LABEL, "")!!,
                sf.getString(NEW_ENCRYPT, "")!!,
                sf.getInt(NUMBER, 0)
            ).addOnSuccessListener { data ->
                viewModel.setmin(data["min"]!!.toFloat())
                viewModel.setmax(data["max"]!!.toFloat())
                findNavController().navigate(R.id.action_write4Fragment_to_write5Fragment)
            }
        }

        setSpinnerUi(binding, viewModel)
        setPickerUi(binding, viewModel)

        return binding.root
    }


    private fun setSpinnerUi(
        binding: FragmentWrite4Binding,
        viewModel: WriteViewModel
    ) {
        val adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, GRADE
            )
        binding.gradeSpinner.adapter = adapter
        binding.gradeSpinner.dropDownVerticalOffset = 53f.dipToPixels().toInt()
        binding.gradeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setgradeField(position)
                    binding.gradePicker.visibility =
                        when (position) {
                            0 -> INVISIBLE
                            1 -> VISIBLE
                            2 -> VISIBLE
                            3 -> VISIBLE
                            4 -> VISIBLE
                            else -> INVISIBLE
                        }
                    if (position != 0) {
                        viewModel.setgradeNumField(70)
                        binding.gradePicker.value = 70
                    }
                }
            }
        binding.gradeSpinner.setSelection(viewModel.getgradeField() ?: 0)

    }


    private fun setPickerUi(
        binding: FragmentWrite4Binding,
        viewModel: WriteViewModel
    ) {
        val cal = Calendar.getInstance()
        val _year = cal.get(Calendar.YEAR)
        val _month = cal.get(Calendar.MONTH)
        val _date = cal.get(Calendar.DATE)
        var buf_month: String
        cal.set(1993, 3, 18)

        val now = System.currentTimeMillis() - 1000
        binding.datePicker1.minDate = cal.timeInMillis
        binding.datePicker1.maxDate = now
        binding.datePicker1.init(
            _year, _month, _date
        ) { _: DatePicker, i: Int, i1: Int, i2: Int ->
            buf_month = if (i1 + 1 < 10) "0" + (i1 + 1).toString()
            else (i1 + 1).toString()
            viewModel.setdateField("$i/${buf_month}/$i2")
        }

        buf_month = if (_month + 1 < 10) "0" + (_month + 1).toString()
        else (_month + 1).toString()
        viewModel.setdateField("$_year/${buf_month}/$_date")

        val date = viewModel.getdateField()?.split("/")
        if (!date.isNullOrEmpty()) {
            binding.datePicker1.init(
                date[0].toInt(), (date[1].toInt() - 1), date[2].toInt()
            ) { _: DatePicker, i: Int, i1: Int, i2: Int ->
                viewModel.setdateField("$i/${i1 + 1}/$i2")
                binding.yearSeriesPicker.value = i
                viewModel.setyearSeriesField(i)
            }
        }

        binding.gradePicker.minValue = 1
        binding.gradePicker.maxValue = 70
        binding.gradePicker.wrapSelectorWheel = false
        binding.gradePicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.gradePicker.setOnValueChangedListener { _: NumberPicker?, _: Int, newVal: Int ->
            viewModel.setgradeNumField(newVal)
        }
        binding.gradePicker.value = viewModel.getgradeNumField() ?: 70

        val year = Calendar.getInstance().get(Calendar.YEAR)
        binding.yearSeriesPicker.minValue = 1992
        binding.yearSeriesPicker.maxValue = year //

        binding.yearSeriesPicker.value = year
        viewModel.setyearSeriesField(year)

        binding.yearSeriesPicker.wrapSelectorWheel = false
        binding.yearSeriesPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.yearSeriesPicker.setOnValueChangedListener { _: NumberPicker?, _: Int, newVal: Int ->
            viewModel.setyearSeriesField(newVal)
        }
    }

    private fun priceCalculate(binding: FragmentWrite5Binding): Float {
        var text1 = binding.priceEditText1.text.toString()
        var text2 = binding.priceEditText2.text.toString()

        if (text1 == "") text1 = "0"
        if (text2 == "") text2 = "0"

        return "${text1}.${text2}".toFloat()
    }

    private fun Float.dipToPixels(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            resources.displayMetrics
        )
    }
}
