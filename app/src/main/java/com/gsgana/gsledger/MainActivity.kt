package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import com.gsgana.gsledger.databinding.ActivityMainBinding
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity :
    AppCompatActivity() { //class MainActivity : AppCompatActivity(), PurchasesUpdatedListener {

    //    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val PREF_NAME = "01504f779d6c77df04"
    private lateinit var sf: SharedPreferences

    private val AD_ID = "ca-app-pub-3940256099942544/8691691433"
    // 실제   "ca-app-pub-8453032642509497/3082833180"
    //테스트 "ca-app-pub-3940256099942544/8691691433"

    private var doneOnce = true
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var mBuilder: AdRequest.Builder
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics


    val KEY = "Kd6c26TK65YSmkw6oU"
//    val TODAY_NAME = "0d07f05fd0c595f615"


    private val ADFREE_NAME = "CQi7aLBQH7dR7qyrCG"
//    private lateinit var billingClient: BillingClient
//    private val sku3600 = "adfree_unlimited_entry"
//    private lateinit var flowParams: BillingFlowParams

    override fun onCreate(savedInstanceState: Bundle?) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Fabric.with(this, Crashlytics())
        sf = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().subscribeToTopic("TEST")
//            .addOnCompleteListener { task ->
////                var msg = getString(R.string.msg_subscribed)
//                if (!task.isSuccessful) {
//                    task.result
////                    msg = getString(R.string.msg_subscribe_failed)
//                }
////                Log.d(TAG, msg)
////                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            }

        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        if (!(intent.getIntExtra(ADFREE_NAME, 6) == 18 || sf.getInt(ADFREE_NAME, 6) == 18)) {
            setAds()
            Handler().postDelayed(
                {
                    loading.visibility = View.GONE
                    homeViewPagerFragmentpage.visibility = View.VISIBLE
                }, 3600 // 3600
            )
        } else {
            Handler().postDelayed(
                {
                    loading.visibility = View.GONE
                    homeViewPagerFragmentpage.visibility = View.VISIBLE
                }, 1800
            )

        }
    }

    private fun setAds() {
        MobileAds.initialize(this)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = AD_ID
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

}
