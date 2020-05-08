package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.gsgana.gsledger.databinding.ActivityMainBinding
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


    private val ALERTSWITCH_NAME = "Ly6gWNc6kHb6hXf0yz"
    private val ALERTRANGE_NAME = "g6b6UQL9Prae7b5h2A"

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

        if (sf.getInt(ALERTSWITCH_NAME,0) == 1) {
            val topicList = when(sf.getInt(ALERTRANGE_NAME,0)){
                0-> listOf("Alpha","Beta","Gamma")
                1-> listOf("Beta","Gamma")
                2-> listOf("Gamma")
                else ->listOf("Alpha","Beta","Gamma")
            }
            topicList.forEach{topic ->
                FirebaseMessaging.getInstance().subscribeToTopic(topic)
            }
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
        }

        val test = intent.getCharArrayExtra(KEY)

        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        Handler().postDelayed(
            {
                loading.visibility = View.GONE
                homeViewPagerFragmentpage.visibility = View.VISIBLE
            }, 1800
        )
    }
}
