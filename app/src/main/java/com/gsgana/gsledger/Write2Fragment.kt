package com.gsgana.gsledger

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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.databinding.FragmentWrite2Binding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.WriteViewModel


class Write2Fragment : Fragment() {

    private lateinit var binding: FragmentWrite2Binding

    private val key = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel =
            ViewModelProviders.of(activity!!, InjectorUtils.provideWriteViewModelFactory(activity!!,null))
                .get(WriteViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentWrite2Binding>(
            inflater, R.layout.fragment_write2, container, false
        )
        binding.layout2.setOnClickListener {
            findNavController().navigate(R.id.action_write2Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()
        }
        binding.layout2r.setOnClickListener {
            findNavController().navigate(R.id.action_write2Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()
        }

        binding.moveTo2.setOnClickListener {
            viewModel.product.brand = viewModel.brand.value!!
            findNavController().navigate(R.id.action_write2Fragment_to_write3Fragment)
        }
        binding.moveTo2r.setOnClickListener {
            findNavController().navigate(R.id.action_write2Fragment_to_write1Fragment)
        }

        if (viewModel.brandField1.value!! < 0) {
            setSpinnerUi(
                binding, viewModel, table = selectTable(
                    viewModel.metalField1.value!!,
                    viewModel.typeField1.value!!
                )
            )
        } else {
            setSpinnerUi(binding, viewModel, array = viewModel.write2Array)
        }
        return binding.root
    }

    private fun setSpinnerUi(
        binding: FragmentWrite2Binding,
        viewModel: WriteViewModel,
        array: Array<String>? = null,
        table: Array<String>? = null
    ) {
        var write2Array: Array<String>? = null

        if (!array.isNullOrEmpty()) {
            write2Array = array
            viewModel.write2Array = array
        } else if (!table.isNullOrEmpty()) {
            write2Array = table
            viewModel.write2Array = table
        }


        val brand_adpater =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, write2Array ?: arrayOf("ㅠㅠㅠㅠ")
            )
        binding.brandSpn.adapter = brand_adpater
        binding.brandSpn.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.brandSpn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.brand.value = write2Array?.get(position)
                viewModel.brandField1.value = position
            }
        }

        if (viewModel.brandField1.value!! > 0) {
            binding.brandSpn.setSelection(viewModel.brandField1.value!!)
        }
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    private fun selectTable(metal: Int, type: Int): Array<String> {
        when (metal) {
            0 -> when (type) {
                0 -> return GCTABLE
                1 -> return GBTABLE
            }
            1 -> when (type) {
                0 -> return SCTABLE
                1 -> return SBTABLE
            }
        }
        return GCTABLE
    }
}