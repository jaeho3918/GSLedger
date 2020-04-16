package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
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
import androidx.navigation.fragment.findNavController
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.FragmentWrite6Binding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.WriteViewModel
import java.text.SimpleDateFormat


@Suppress("DEPRECATION")
class Write6Fragment : Fragment() {

    private lateinit var binding: FragmentWrite6Binding

    private lateinit var fm: FragmentManager
    private lateinit var product: Product

//    private lateinit var sharedPreferences: SharedPreferences
//    private val PREF_NAME = "01504f779d6c77df04"
//    private val BUY_NAME = "IYe6CjFgpBKVHdcXkC"
//    private lateinit var mformat: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        sharedPreferences = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//
//        mformat = SimpleDateFormat("yyyy/MM/dd")

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
            findNavController().navigate(R.id.action_write6Fragment_to_homeViewPagerFragment)
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
                product.price = viewModel.priceTest.toFloat()
                product.grade = GRADE[viewModel.gradeField.value ?: 0]
                product.gradeNum = viewModel.gradeNumField.value ?: 0
                product.buyDate = viewModel.dateField.value ?: ""
                product.reg = viewModel.regField.value ?: 0f
                product.memo = viewModel.memoField.value ?: ""
                product.prePrice =
                    (viewModel.priceTest.toFloat() * (viewModel.quantityField.value?.toFloat()
                        ?: 1f) * PACKAGENUM[viewModel.packageTypeField.value ?: 0])
                product.cur = viewModel.curField.value?.toFloat() ?: 0f
                product.year = viewModel.yearSeriesField.value ?: 0
                product.pre = viewModel.pre.value ?: 0f

//                if (sharedPreferences.getString(BUY_NAME, null).isNullOrEmpty()) {
//                    sharedPreferences.edit()
//                        .putString(BUY_NAME, viewModel.dateField.value)
//                        .apply()
//                } else if (mformat.parse(viewModel.dateField.value) < mformat.parse(
//                        sharedPreferences.getString(BUY_NAME, null)
//                    )
//                ) {
//                    sharedPreferences.edit()
//                        .putString(BUY_NAME, viewModel.dateField.value)
//                        .apply()
//                }

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
