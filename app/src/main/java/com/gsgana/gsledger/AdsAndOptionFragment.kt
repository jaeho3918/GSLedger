package com.gsgana.gsledger

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.android.billingclient.api.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.databinding.AdsAndOptionFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.WEIGHTUNIT
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.ads_and_option_fragment.*


class AdsAndOptionFragment : Fragment(), PurchasesUpdatedListener {
    private val PREF_NAME = "01504f779d6c77df04"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val WEIGHT_NAME = "f79604050dfc500715"
    private lateinit var binding: AdsAndOptionFragmentBinding
    private lateinit var googleSigninClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mAuth: FirebaseAuth
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"

    private lateinit var option: SharedPreferences
    private lateinit var adapter: ArrayAdapter<String>

    private val ADFREE_NAME = "CQi7aLBQH7dR7qyrCG"
    private lateinit var billingClient: BillingClient
    private val sku3600 = "adfree_unlimited_entry"
    private lateinit var flowParams: BillingFlowParams
    private lateinit var sf : SharedPreferences

    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(activity!!, activity!!.intent.getCharArrayExtra(KEY)) //(activity!!, activity!!.intent.getCharArrayExtra(KEY))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        option = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        binding = AdsAndOptionFragmentBinding.inflate(inflater, container, false)

        setSpinner(binding, viewModel)

        binding.subscribeBtn.setOnClickListener {
            billingClient = BillingClient.newBuilder(context!!)
                .enablePendingPurchases()
                .setListener(this)
                .build()

            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
//                    Toast.makeText(context!!,"Baaaaaaaaaaaaad", Toast.LENGTH_LONG) .show()
                }

                override fun onBillingSetupFinished(p0: BillingResult?) {
                    if (p0?.responseCode == BillingClient.BillingResponseCode.OK) {
                        val skuList: List<String> = arrayListOf(sku3600)
                        val params: SkuDetailsParams.Builder = SkuDetailsParams.newBuilder()
                        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
                        billingClient.querySkuDetailsAsync(
                            params.build(), object : SkuDetailsResponseListener {
                                override fun onSkuDetailsResponse(
                                    p0: BillingResult?,
                                    p1: MutableList<SkuDetails>?
                                ) {
                                    val flowParams: BillingFlowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(p1?.get(0))
                                        .build();
                                    val billingResponseCode =
                                        billingClient.launchBillingFlow(activity, flowParams)
                                    if (billingResponseCode.responseCode == BillingClient.BillingResponseCode.OK) {
//                                        Toast.makeText(context!!, p1?.get(0).toString(), Toast.LENGTH_LONG) .show()
                                    }else{
//                                        Toast.makeText(context!!,"Baaaaaaaaaaaaad", Toast.LENGTH_LONG) .show()
                                    }
                                }
                            }
                        );
                    }
                }
            }
            )
        }

        binding.delBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getString(R.string.caution))
            builder.setMessage(resources.getString(R.string.delAgreeCheck))
            builder.setPositiveButton(resources.getString(R.string.agree)
            ) { _, _ ->
                del_btn.text = ""
                del_btn.isEnabled = false
                del_progressBar.visibility = View.VISIBLE
                viewModel.deleteProducts()
                mAuth = FirebaseAuth.getInstance()
                mAuth.currentUser!!.delete()
                gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                googleSigninClient = GoogleSignIn.getClient(activity!!, gso)
                googleSigninClient.signOut()
                option = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                option.edit()
                    .clear()
                    .apply()

                Handler().postDelayed({
                    activity!!.finish()
                }, 1800)
            }
            builder.show()
        }

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
                    val getData = viewModel?.getRealData()?.value?.toMutableMap()
                    getData?.set("currency", position.toDouble())
                    viewModel?.setRealData(getData!!.toMap())
                    viewModel?.setCurrencyOption(position)
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
                    val getData = viewModel?.getRealData()?.value?.toMutableMap()
                    getData?.set("weightUnit", position.toDouble())
                    viewModel?.setRealData(getData!!.toMap())
                    getData?.clear()
                }
            }


        binding.currencyOption.setSelection(
            (viewModel?.getRealData()?.value?.get("currency") ?: 0.0).toInt()
        )
        binding.weightUnitOption.setSelection(
            (viewModel?.getRealData()?.value?.get("weightUnit") ?: 0.0).toInt()
        )

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

        override fun onPurchasesUpdated(
        billingresult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        if (billingresult?.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.let {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        activity!!.intent.putExtra(ADFREE_NAME, 18)
                        option.edit().putInt(ADFREE_NAME,18).apply()
                        Toast.makeText(context,resources.getString(R.string.adfreerestart),Toast.LENGTH_LONG).show()
                    }
//                    Toast.makeText(context!!,"Baaaaaaaaaaaaad", Toast.LENGTH_LONG) .show()
                }
            }
        }
    }
}
