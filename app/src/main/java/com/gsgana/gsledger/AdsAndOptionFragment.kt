package com.gsgana.gsledger

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.util.Linkify
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.billingclient.api.*
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import com.gsgana.gsledger.databinding.AdsAndOptionFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.ads_and_option_fragment.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class AdsAndOptionFragment : Fragment(), PurchasesUpdatedListener {
    private lateinit var binding: AdsAndOptionFragmentBinding
    private lateinit var googleSigninClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mAuth: FirebaseAuth
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"

    private lateinit var adapter: ArrayAdapter<String>

    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    private lateinit var sf: SharedPreferences
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val WEIGHT_NAME = "f79604050dfc500715"
    private val PREF_NAME = "01504f779d6c77df04"
    private val ADFREE_NAME = "CQi7aLBQH7dR7qyrCG"

    private lateinit var billingClient: BillingClient
    private val sku3600 = "adfree_unlimited_entry"

    private val ALERTSWITCH_NAME = "Ly6gWNc6kHb6hXf0yz"
    private val ALERTRANGE_NAME = "g6b6UQL9Prae7b5h2A"

    private val START_OPTION = "P6Uga62r9b5Ae7bQLh"

    private val DURATION: Long = 999

    private lateinit var objectAnimator: ObjectAnimator

    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(
            activity!!,
            activity!!.intent.getCharArrayExtra(KEY)
        ) //(activity!!, activity!!.intent.getCharArrayExtra(KEY))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sf = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        binding = AdsAndOptionFragmentBinding.inflate(inflater, container, false)

        setSpinner(binding, viewModel)

        binding.subscribeBtn.setOnClickListener {
            billingClient = BillingClient.newBuilder(context!!)
                .enablePendingPurchases()
                .setListener(this)
                .build()

            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {}

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
                                    val flowParams: BillingFlowParams =
                                        BillingFlowParams.newBuilder()
                                            .setSkuDetails(p1?.get(0))
                                            .build();
                                    val billingResponseCode =
                                        billingClient.launchBillingFlow(activity, flowParams)
                                    if (billingResponseCode.responseCode == BillingClient.BillingResponseCode.OK) {
//                                        Toast.makeText(context!!, p1?.get(0).toString(), Toast.LENGTH_LONG) .show()
                                    } else {
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
            builder.setPositiveButton(
                resources.getString(R.string.agree)
            ) { _, _ ->
                del_btn.text = ""
                del_btn.isEnabled = false
                del_progressBar.visibility = View.VISIBLE
                viewModel.deleteProducts()
//                mAuth = FirebaseAuth.getInstance()
//                mAuth.currentUser!!.delete()
//                gso =
//                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
//                googleSigninClient = GoogleSignIn.getClient(activity!!, gso)
//                googleSigninClient.signOut()
//                sf = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//                sf.edit()
//                    .clear()
//                    .apply()

                Handler().postDelayed({
                    activity!!.finish()
                }, 1800)
            }
            builder.show()
        }


        val mTransform = Linkify.TransformFilter { _: Matcher, _: String -> "" }

        val pattern1 = Pattern.compile("Privacy Policy")
        val pattern2 = Pattern.compile("개인정보보호정책")
        val pattern3 = Pattern.compile("Terms and Conditions")
        val pattern4 = Pattern.compile("이용약관")

        Linkify.addLinks(
            binding.agreeText,
            pattern1,
            "https://gsledger-29cad.firebaseapp.com/privacypolicy.html",
            null,
            mTransform
        )
        Linkify.addLinks(
            binding.agreeText,
            pattern2,
            "https://gsledger-29cad.firebaseapp.com/privacypolicy_kr.html",
            null,
            mTransform
        )
        Linkify.addLinks(
            binding.agreeText,
            pattern3,
            "https://gsledger-29cad.firebaseapp.com/termsandconditions.html",
            null,
            mTransform
        )
        Linkify.addLinks(
            binding.agreeText,
            pattern4,
            "https://gsledger-29cad.firebaseapp.com/termsandconditions_kr.html",
            null,
            mTransform
        )

        return binding.root
    }

    private fun setSpinner(
        binding: AdsAndOptionFragmentBinding,
        viewModel: HomeViewPagerViewModel?
    ) {

        adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, CURRENCYANDSYMBOL
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
                    sf.edit()?.putInt(CURR_NAME, position)?.apply()
                    val getData = viewModel?.getRealData()?.value?.toMutableMap()
                    if (!getData.isNullOrEmpty()) {
                        getData.set("currency", position.toDouble())
                        viewModel.setRealData(getData.toMap())
                        getData.clear()
                    }
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
                    sf.edit()?.putInt(WEIGHT_NAME, position)?.apply()
                    val getData = viewModel?.getRealData()?.value?.toMutableMap()
                    if (!getData.isNullOrEmpty()) {
                        getData.set("weightUnit", position.toDouble())
                        viewModel.setRealData(getData.toMap())
                        getData.clear()
                    }
                }
            }

        if (sf.getInt(START_OPTION, 6) == 6) {
            val country = Locale.getDefault().country

            when (country) {
                "US" -> {
                    binding.currencyOption.setSelection(0)
                    binding.weightUnitOption.setSelection(0)
                    sf.edit()?.putInt(CURR_NAME, 0)?.apply()
                    sf.edit()?.putInt(WEIGHT_NAME, 0)?.apply()
                }

                "KR" -> {
                    binding.currencyOption.setSelection(5)   // KRW
                    binding.weightUnitOption.setSelection(2) // KG
                    sf.edit()?.putInt(CURR_NAME, 5)?.apply()
                    sf.edit()?.putInt(WEIGHT_NAME, 2)?.apply()
                }

                "JP" -> {
                    binding.currencyOption.setSelection(4)   // KRW
                    binding.weightUnitOption.setSelection(2) // KG
                    sf.edit()?.putInt(CURR_NAME, 4)?.apply()
                    sf.edit()?.putInt(WEIGHT_NAME, 2)?.apply()
                }

                "IN" -> {
                    binding.currencyOption.setSelection(2)   // INR
                    binding.weightUnitOption.setSelection(2) // kg
                    sf.edit()?.putInt(CURR_NAME, 2)?.apply()
                    sf.edit()?.putInt(WEIGHT_NAME, 2)?.apply()
                }

                "GB" -> {
                    binding.currencyOption.setSelection(1)   // GBP
                    binding.weightUnitOption.setSelection(0) // oz
                    sf.edit()?.putInt(CURR_NAME, 1)?.apply()
                    sf.edit()?.putInt(WEIGHT_NAME, 0)?.apply()
                }

                "CN" -> {
                    binding.currencyOption.setSelection(6)   // CNY
                    binding.weightUnitOption.setSelection(2) // kg
                    sf.edit()?.putInt(CURR_NAME, 6)?.apply()
                    sf.edit()?.putInt(WEIGHT_NAME, 2)?.apply()
                }
            }
            sf.edit().putInt(START_OPTION, 18).apply()


            objectAnimator = ObjectAnimator.ofObject(
                binding.alertSwitchOption,
                "backgroundColor",
                ArgbEvaluator(),
                ContextCompat.getColor(context!!, R.color.white),
                ContextCompat.getColor(context!!, R.color.mu1_data_up)
            )

            objectAnimator.repeatCount = 5
            objectAnimator.repeatMode = ValueAnimator.REVERSE
            objectAnimator.duration = DURATION
            objectAnimator.start()


        } else {
            binding.currencyOption.setSelection(
                (viewModel?.getRealData()?.value?.get("currency") ?: 0.0).toInt()
            )
            binding.weightUnitOption.setSelection(
                (viewModel?.getRealData()?.value?.get("weightUnit") ?: 0.0).toInt()
            )
        }


        adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, ALERTSWITCH
            )

        var start = true

        binding.alertSwitchOption.adapter = adapter
        binding.alertSwitchOption.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.alertSwitchOption.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    sf.edit()?.putInt(ALERTSWITCH_NAME, position)?.apply()

                    if (position == 1) {
                        val topicList = when (sf.getInt(ALERTRANGE_NAME, 0)) {
                            0 -> listOf("Alpha", "Beta", "Gamma")
                            1 -> listOf("Beta", "Gamma")
                            2 -> listOf("Gamma")
                            else -> listOf("Alpha", "Beta", "Gamma")
                        }
                        topicList.forEach { topic ->
                            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                        }
                        FirebaseMessaging.getInstance().isAutoInitEnabled = true
                        binding.alertRangeOption.isEnabled = true

                    } else {
                        val topicList = listOf("Alpha", "Beta", "Gamma")
                        topicList.forEach { topic ->
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                        }
                        FirebaseMessaging.getInstance().isAutoInitEnabled = true
                        binding.alertRangeOption.isEnabled = false
                    }
                }
            }

        adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, ALERTRANGE
            )

        binding.alertRangeOption.adapter = adapter
        binding.alertRangeOption.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.alertRangeOption.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (sf.getInt(ALERTSWITCH_NAME, 0) == 1) {
                        sf.edit()?.putInt(ALERTRANGE_NAME, position)?.apply()
                        val topicListRemove = listOf("Alpha", "Beta", "Gamma")
                        topicListRemove.forEach { topic ->
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                        }
                        val topicList = when (sf.getInt(ALERTRANGE_NAME, 0)) {
                            0 -> listOf("Alpha", "Beta", "Gamma")
                            1 -> listOf("Beta", "Gamma")
                            2 -> listOf("Gamma")
                            else -> listOf("Alpha", "Beta", "Gamma")
                        }
                        topicList.forEach { topic ->
                            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                        }
                        FirebaseMessaging.getInstance().isAutoInitEnabled = true
                    }
                }
            }


        binding.alertSwitchOption.setSelection(sf.getInt(ALERTSWITCH_NAME, 0))
        if (sf.getInt(ALERTSWITCH_NAME, 0) == 0) {
            binding.alertRangeOption.isEnabled = false
        } else {
            binding.alertRangeOption.setSelection(sf.getInt(ALERTRANGE_NAME, 0))
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
//                    Toast.makeText(context!!,"Baaaaaaaaaaaaad", Toast.LENGTH_LONG) .show()
                    } else {
                        sf.edit().putInt(ADFREE_NAME, 6).apply()
                    }
                }
            }
        }
    }

    private fun getChart(label: String, reg: String, num: Int): Task<Map<String, ArrayList<*>>> {
        // Create the arguments to the callable function.

        val data = hashMapOf(
            "label" to label,
            "reg" to reg,
            "number" to num
        )

        return functions
            .getHttpsCallable("SG0ZRjr99Z1OAdeROjF1e6nS")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as Map<String, ArrayList<*>>
                result
            }
    }
}
