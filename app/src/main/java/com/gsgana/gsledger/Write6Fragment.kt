package com.gsgana.gsledger

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.FragmentWrite6Binding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import com.gsgana.gsledger.viewmodels.ListViewModel
import com.gsgana.gsledger.viewmodels.WriteViewModel


class Write6Fragment : Fragment() {

    private lateinit var binding: FragmentWrite6Binding

    private lateinit var fm: FragmentManager
    private lateinit var product: Product
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fm = parentFragmentManager

        val viewModel =
            ViewModelProviders.of(
                activity!!,
                InjectorUtils.provideWriteViewModelFactory(activity!!, null)
            )
                .get(WriteViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentWrite6Binding>(
            inflater, R.layout.fragment_write6, container, false
        )

        binding.layout6.setOnClickListener {
            viewModel.initProduct()
            findNavController().navigate(R.id.action_write6Fragment_to_homeViewPagerFragment)
        }

        binding.layout6r.setOnClickListener {
            viewModel.initProduct()
            fm.popBackStack()
            findNavController().navigate(R.id.action_write6Fragment_to_homeViewPagerFragment)
        }

        binding.moveTo6.setOnClickListener {
            viewModel.initProduct()
            fm.popBackStack()
            findNavController().navigate(R.id.action_write6Fragment_to_homeViewPagerFragment)
        }

        binding.moveTo6r.setOnClickListener {
            findNavController().navigate(R.id.action_write6Fragment_to_write5Fragment)
        }

        setTextUI(binding, viewModel)

        binding.callback = object : ListFragment.Callback {
            override fun add() {
                product = Product(null)
                product.brand = viewModel.brand.value.toString()
                product.metal = viewModel.metalField1.value ?: 0
                product.type = viewModel.typeField1.value ?: 0
                product.packageType = viewModel.packageTypeField.value ?: 0
                product.quantity = viewModel.quantityField.value?.toInt() ?: 0
                product.weight = viewModel.weightMerger.value?.toFloat() ?: 0f
                product.weightUnit = viewModel.weightUnitField.value ?: 0
                product.weightr = viewModel.weightUnit.value?.toFloat() ?: 1f
                product.currency = viewModel.currencyField.value ?: 0
                product.price = viewModel.price.value?.toFloat() ?: 0f
                product.buyDate = viewModel.dateField.value ?: ""
                product.reg = viewModel.regField.value ?: 0f
                product.memo = viewModel.memoField.value ?: ""
                product.prePrice = viewModel.totalPrice.value?.toFloat() ?: 0f

                viewModel.addProduct(product)
                viewModel.initProduct()
                findNavController().navigate(R.id.action_write6Fragment_to_homeViewPagerFragment)
            }

            override fun del() {
            }
        }

        return binding.root
    }

    private fun setTextUI(binding: FragmentWrite6Binding, viewModel: WriteViewModel) {
        binding.write6BrandText.text = viewModel.brand.value
        binding.write6YearText.text = viewModel.yearSeriesField.value
        binding.write6WeightText.text = viewModel.weightMerger.value
        binding.write6MetalText.text = METAL[viewModel.metalField1.value ?: 0]
        binding.write6TypeText.text = TYPE[viewModel.typeField1.value ?: 0]
        binding.write6WeightUnitText.text = WEIGHTUNIT[viewModel.weightUnitField.value ?: 0]
        binding.write6QuantityText.text = viewModel.quantityField.value.toString()
        binding.write6PackageText.text = PACKAGETYPE[viewModel.packageTypeField.value ?: 0]
        binding.write6CurrencyText.text = CURRENCY[viewModel.currencyField.value ?: 0]
        binding.write6PriceText.text = viewModel.priceMerger.value
        binding.memoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.memoField.value = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.memoEditText.setText(viewModel.memoField.value ?: "")
    }

    interface Callback {
        fun add()
    }
}
