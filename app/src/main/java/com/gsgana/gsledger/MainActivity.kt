package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.android.billingclient.api.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.gsgana.gsledger.databinding.ActivityMainBinding
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {


    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val PREF_NAME = "01504f779d6c77df04"
    private lateinit var sf: SharedPreferences

    private val AD_UNIT_ID =
        "ca-app-pub-3940256099942544/8691691433"    // 실제   "ca-app-pub-8453032642509497/3082833180"
                                                    // 테스트 "ca-app-pub-3940256099942544/8691691433"
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var mBuilder: AdRequest.Builder
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private var doneOnce = true

    private lateinit var skuDetails1800 : SkuDetails

    private val skuID1800 = "gsledger_subscribe"
    private val skuID3600 = "adfree_unlimited_entry"


//    private val viewModel: HomeViewPagerViewModel by viewModels {
//        InjectorUtils.provideHomeViewPagerViewModelFactory(this, this.intent.getCharArrayExtra(KEY))
//    }

    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)











//        billingClient = BillingClient.newBuilder(this).build()
//        billingClient.startConnection(object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
//                    // The BillingClient is ready. You can query purchases here.
//                }
//            }
//            override fun onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//            }
//        })
//
//        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
//        val flowParams = BillingFlowParams.newBuilder()
//            .setSkuDetails(skuDetails)
//            .build()
//
//        val responseCode = billingClient.launchBillingFlow(this, flowParams)
//        suspend fun acknowledgePurchase() {
//            val acknowledgePurchaseParams =
//                AcknowledgePurchaseParams.newBuilder()
//                    .setPurchaseToken(/* token */)
//                    .setDeveloperPayload(/* payload */)
//                    .build()
//            val ackPurchaseResult = withContext(Dispatchers.IO) {
//                billingClient.acknowledgePurchase(acknowledgePurchaseParams)
//            }
//        }



//        this.intent.removeExtra(KEY)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        sf = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        MobileAds.initialize(this)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = AD_UNIT_ID
        mBuilder = AdRequest.Builder()
        mInterstitialAd.loadAd(mBuilder.build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                if (mInterstitialAd.isLoaded) {
                    if (doneOnce) {
//                        mInterstitialAd.show()
                        doneOnce = false
                    }
                }
            }
        }

        Handler().postDelayed(
            {
                loading.visibility = View.GONE
                homeViewPagerFragmentpage.visibility = View.VISIBLE
            }, 2100
        )
    }
}
