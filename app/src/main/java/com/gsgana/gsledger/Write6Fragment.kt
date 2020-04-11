package com.gsgana.gsledger

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.FragmentWrite6Binding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.WriteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class Write6Fragment : Fragment() {

    private lateinit var binding: FragmentWrite6Binding

    private lateinit var fm: FragmentManager
    private lateinit var product: Product

//    private val AD_UNIT_ID =
//        "ca-app-pub-3940256099942544/8691691433" //"ca-app-pub-8453032642509497/3082833180"
//    private lateinit var mInterstitialAd: InterstitialAd
//    private lateinit var mBuilder: AdRequest.Builder
//    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
//    private var doneOnce = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        MobileAds.initialize(activity)
//        mInterstitialAd = InterstitialAd(activity)
//        mInterstitialAd.adUnitId = AD_UNIT_ID
//        mBuilder = AdRequest.Builder()
//        mInterstitialAd.loadAd(mBuilder.build())
//        mInterstitialAd.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                if (mInterstitialAd.isLoaded) {
//                    if (doneOnce) {
//                        mInterstitialAd.show()
//                        doneOnce = false
//                    }
//                }
//            }
//        }

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
            fm.popBackStack()
            findNavController().navigate(R.id.action_write6Fragment_to_homeViewPagerFragment)
        }

        binding.layout6r.setOnClickListener {
            viewModel.initProduct()
            fm.popBackStack()
            findNavController().navigate(R.id.action_write6Fragment_to_homeViewPagerFragment)
        }

//        binding.moveTo6.setOnClickListener {
//            viewModel.initProduct()
//            fm.popBackStack()
//            findNavController().navigate(R.id.action_write6Fragment_to_homeViewPagerFragment)
//        }


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
                viewModel.addProduct(product)
                viewModel.initProduct()

                fm.popBackStack()
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
