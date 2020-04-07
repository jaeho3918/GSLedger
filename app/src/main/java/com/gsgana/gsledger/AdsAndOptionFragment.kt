package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.gsgana.gsledger.databinding.AdsAndOptionFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.WEIGHTUNIT
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel


class AdsAndOptionFragment : Fragment() {
    private val PREF_NAME = "01504f779d6c77df04"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val WEIGHT_NAME = "f79604050dfc500715"
    private lateinit var binding: AdsAndOptionFragmentBinding

    private val PL = "18xRWR1PDWW01PjjXI"
    private val UPDOWN = "17RD79dX7d1DWf0j0I"

    private lateinit var option: SharedPreferences
    private lateinit var adapter: ArrayAdapter<String>

    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(requireActivity(), null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = AdsAndOptionFragmentBinding.inflate(inflater, container, false)

        option = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        setSpinner(binding, viewModel)

        return binding.root
    }

    private fun setSpinner(
        binding: AdsAndOptionFragmentBinding,
        viewModel: HomeViewPagerViewModel?
    ) {

        adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, CURRENCY
            )

        binding.currencyOption.adapter = adapter
        binding.currencyOption.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.currencyOption.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    option.edit()?.putInt(CURR_NAME, position)?.apply()
                    val getData = viewModel?.realData?.value?.toMutableMap()
                    getData?.set("currency", position.toDouble())
                    viewModel?.realData?.value = getData?.toMap()
                    getData?.clear()
                }
            }


        adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, WEIGHTUNIT
            )

        binding.weightUnitOption.adapter = adapter
        binding.weightUnitOption.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.weightUnitOption.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    option.edit()?.putInt(WEIGHT_NAME, position)?.apply()
                    val getData = viewModel?.realData?.value?.toMutableMap()
                    getData?.set("weightUnit", position.toDouble())
                    viewModel?.realData?.value = getData?.toMap()
                    getData?.clear()
                }
            }


        binding.currencyOption.setSelection(viewModel!!.realData.value?.get("currency")!!.toInt())
        binding.weightUnitOption.setSelection(viewModel!!.realData.value?.get("weightUnit")!!.toInt())

    }

    interface Callback {
        fun add()
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }
}
