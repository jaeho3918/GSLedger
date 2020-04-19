package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.gsgana.gsledger.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), PurchasesUpdatedListener {


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

    private val ADFREE_NAME = "CQi7aLBQH7dR7qyrCG"
    private lateinit var billingClient: BillingClient
    private val sku1800 = "gsledger_subscribe"
    private val sku3600 = "adfree_unlimited_entry"
    private lateinit var flowParams: BillingFlowParams


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sf = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onBillingSetupFinished(p0: BillingResult?) {
                if (p0?.responseCode == BillingClient.BillingResponseCode.OK) {
                    val skuList: List<String> = arrayListOf(sku1800, sku3600)
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
                                    billingClient.launchBillingFlow(this@MainActivity, flowParams)
                                if (billingResponseCode.responseCode == BillingClient.BillingResponseCode.OK) {
                                    Toast.makeText(
                                        applicationContext,
                                        p0?.responseCode.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Baaaaaaaaaaaaad",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    );
                }
            }
        }
        )

//        this.intent.removeExtra(KEY)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        if(intent.getIntExtra(ADFREE_NAME,6) != 18 || sf.getInt(ADFREE_NAME,6)!=18){
            MobileAds.initialize(this)
            mInterstitialAd = InterstitialAd(this)
            mInterstitialAd.adUnitId = AD_UNIT_ID
            mBuilder = AdRequest.Builder()
            mInterstitialAd.loadAd(mBuilder.build())
            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    if (mInterstitialAd.isLoaded) {
                        if (doneOnce) {
                        mInterstitialAd.show()
                            doneOnce = false
                        }
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

//    suspend fun querySkuDetails(): List<SkuDetails>? {
//        val skuList = ArrayList<String>()
//        skuList.add("premium_upgrade")
//        skuList.add("gas")
//        val params = SkuDetailsParams.newBuilder()
//        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
//        val skuDetailsResult = withContext(Dispatchers.IO) {
//            billingClient.querySkuDetails(params.build())
//        }
//        return skuDetailsResult.skuDetailsList
//    }


    override fun onPurchasesUpdated(
        billingresult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        if (billingresult?.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.let {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        Toast.makeText(
                            applicationContext,
                            "adfree Goooooooooooooood",
                            Toast.LENGTH_LONG
                        ).show()
                        intent.putExtra(ADFREE_NAME, 18)
                    }
                }
            }
        }
    }
}
