package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener

import com.gsgana.gsledger.adapters.ProductAdapter
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.ListFragmentBinding
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.list_fragment.*

class ListFragment : Fragment(), PurchasesUpdatedListener {

    private lateinit var product: Product
    private lateinit var binding: ListFragmentBinding

    private val PREF_NAME = "01504f779d6c77df04"
    private lateinit var sf: SharedPreferences
    private val ADFREE_NAME = "CQi7aLBQH7dR7qyrCG"

    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(
            activity!!,
            activity!!.intent.getCharArrayExtra(KEY)
        )
    }

    private lateinit var realData: Map<String, Double>
    private lateinit var adapter: ProductAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ListFragmentBinding.inflate(inflater, container, false)
        sf = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        realData = viewModel.getRealData().value ?: mapOf()

        adapter = ProductAdapter(requireContext(), realData)
        binding.productList.adapter = adapter


        binding.writeBtn.setOnClickListener {
            if (it.findNavController().currentDestination?.id == R.id.homeViewPagerFragment) {
                findNavController()
                    .navigate(R.id.action_homeViewPagerFragment_to_write1Fragment)
            }
        }

        binding.addList.setOnClickListener {
            if (it.findNavController().currentDestination?.id == R.id.homeViewPagerFragment) {
                findNavController()
                    .navigate(R.id.action_homeViewPagerFragment_to_write1Fragment)
            }
        }

        viewModel.getProducts().observe(viewLifecycleOwner) { result ->
            if (result.size == 0) {
                binding.addList.visibility= View.VISIBLE
                binding.productList.visibility = View.GONE
            }else{
                binding.addList.visibility= View.GONE
                binding.productList.visibility = View.VISIBLE
            }
            binding.getNum.text = result.size.toString()
            adapter.submitList(result)
        }
        return binding.root
    }

    interface Callback {
        fun add()
        fun del()
    }

    override fun onPurchasesUpdated(
        billingresult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        if (billingresult?.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.let {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        sf.edit().putInt(ADFREE_NAME, 18).apply()
                        Toast.makeText(
                            context,
                            resources.getString(R.string.adfreerestart),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        sf.edit().putInt(ADFREE_NAME, 6).apply()
                    }
                }
            }
        }
    }
}


