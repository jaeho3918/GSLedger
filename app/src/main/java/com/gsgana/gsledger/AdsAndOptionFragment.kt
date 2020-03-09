package com.gsgana.gsledger

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.gsgana.gsledger.data.Option
import com.gsgana.gsledger.databinding.AdsAndOptionFragmentBinding
import com.gsgana.gsledger.databinding.FragmentWrite5Binding
import com.gsgana.gsledger.databinding.HomeViewPagerFragmentBinding
import com.gsgana.gsledger.databinding.StatFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.viewmodels.AdsAndOptionViewModel
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import com.gsgana.gsledger.viewmodels.WriteViewModel


class AdsAndOptionFragment : Fragment() {
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val PREF_NAME = "01504f779d6c77df04"
    private lateinit var binding: AdsAndOptionFragmentBinding

    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(requireActivity(), null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = AdsAndOptionFragmentBinding.inflate(inflater, container, false)

        setSpinner(binding, viewModel)

        return binding.root
    }

    private fun setSpinner(
        binding: AdsAndOptionFragmentBinding,
        viewModel: HomeViewPagerViewModel?
    ) {
        val sf = activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        var adapter =
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
                    sf?.edit()?.putInt(CURR_NAME, position)?.commit()
                    val getData = viewModel?.realData?.value?.toMutableMap()
                    getData?.set("currency", position.toDouble())

                    viewModel?.realData?.value = getData?.toMap()
                    getData?.clear()
                }
            }

        if (!viewModel?.realData?.value.isNullOrEmpty()) {
            binding.currencyOption.setSelection(viewModel?.realData?.value?.get("currency")!!.toInt())
        }
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
