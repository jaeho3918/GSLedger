package com.gsgana.gsledger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.gsgana.gsledger.databinding.FragmentWrite1Binding
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import com.gsgana.gsledger.viewmodels.WriteViewModel
import kotlinx.android.synthetic.main.fragment_write1.*


class Write1Fragment : Fragment() {

    private lateinit var binding: FragmentWrite1Binding
//    private val key = null
//    private val viewModel: WriteViewModel by viewModels {
//        InjectorUtils.provideWriteViewModelFactory(requireContext(), key)
//    }

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

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_write1, container, false
        )

        binding.layout1r.setOnClickListener {
            findNavController().navigate(R.id.action_write1Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()

        }
        binding.layout1.setOnClickListener {
            findNavController().navigate(R.id.action_write1Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()
        }

        binding.moveTo1.setOnClickListener {
            if (!(viewModel.metalField1.value == null || viewModel.typeField1.value == null)) {
                viewModel.product.metal = viewModel.metalField1.value!!
                viewModel.product.type = viewModel.typeField1.value!!
                findNavController().navigate(R.id.action_write1Fragment_to_write2Fragment)

            } else if (viewModel.metalField1.value == null) {
                Toast.makeText(context, "종류을 선택해주세요.", Toast.LENGTH_LONG).show()

            } else if (viewModel.typeField1.value == null) {
                Toast.makeText(context, "형태를 선택해주세요.", Toast.LENGTH_LONG).show()
            }
        }


        binding.select11.setOnClickListener {
            select1_1.switchState()
            if (select1_1.isIconEnabled) {
                select1_2.setIconEnabled(false)
                binding.label11.text = "GOLD"
                viewModel.metalField1.value = 0
                binding.label11.visibility = View.VISIBLE
                viewModel.brandField1.value = -1

            } else {
                select1_2.setIconEnabled(true)
                binding.label11.text = "SILVER"
                viewModel.metalField1.value = 1
                binding.label11.visibility = View.VISIBLE
                viewModel.brandField1.value = -1

            }
        }
        binding.select12.setOnClickListener {
            select1_2.switchState()
            if (select1_2.isIconEnabled) {
                select1_1.setIconEnabled(false)
                viewModel.metalField1.value = 1
                binding.label11.text = "SILVER"
                binding.label11.visibility = View.VISIBLE
                viewModel.brandField1.value = -1
            } else {
                select1_1.setIconEnabled(true)
                viewModel.metalField1.value = 0
                binding.label11.text = "GOLD"
                binding.label11.visibility = View.VISIBLE
                viewModel.brandField1.value = -1
            }
        }
        binding.select21.setOnClickListener {
            select2_1.switchState()
            if (select2_1.isIconEnabled) {
                select2_2.setIconEnabled(false)
                viewModel.typeField1.value = 0
                binding.label12.text = "COIN"
                binding.label12.visibility = View.VISIBLE
                viewModel.brandField1.value = -1
            } else {
                select2_2.setIconEnabled(true)
                viewModel.typeField1.value = 1
                binding.label12.text = "BAR"
                binding.label12.visibility = View.VISIBLE
                viewModel.brandField1.value = -1
            }
        }
        binding.select22.setOnClickListener {
            select2_2.switchState()
            if (select2_2.isIconEnabled) {
                select2_1.setIconEnabled(false)
                viewModel.typeField1.value = 1
                binding.label12.text = "BAR"
                binding.label12.visibility = View.VISIBLE
                viewModel.brandField1.value = -1
            } else {
                select2_1.setIconEnabled(true)
                viewModel.typeField1.value = 0
                binding.label12.text = "COIN"
                binding.label12.visibility = View.VISIBLE
                viewModel.brandField1.value = -1
            }
        }

        when (viewModel.metalField1.value) {
            0 -> {
                binding.select11.setIconEnabled(true)
                binding.label11.text = "GOLD"
                binding.label11.visibility = View.VISIBLE
            }
            1 -> {
                binding.select12.setIconEnabled(true)
                binding.label11.text = "SILVER"
                binding.label11.visibility = View.VISIBLE
            }
            else -> {
                binding.select11.setIconEnabled(false)
                binding.select12.setIconEnabled(false)
                binding.label11.visibility = View.GONE
            }
        }

        when (viewModel.typeField1.value) {
            0 -> {
                binding.select21.setIconEnabled(true)
                binding.label12.text = "COIN"
                binding.label12.visibility = View.VISIBLE
            }
            1 -> {
                binding.select22.setIconEnabled(true)
                binding.label12.text = "BAR"
                binding.label12.visibility = View.VISIBLE
            }
            else -> {
                binding.select21.setIconEnabled(false)
                binding.select22.setIconEnabled(false)
                binding.label12.visibility = View.GONE
            }
        }

        return binding.root
    }
}

