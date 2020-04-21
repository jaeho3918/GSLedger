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
import com.gsgana.gsledger.ad.AdUtility
import com.gsgana.gsledger.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() { //class MainActivity : AppCompatActivity(), PurchasesUpdatedListener {

//    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val PREF_NAME = "01504f779d6c77df04"
    private lateinit var sf: SharedPreferences

    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var mBuilder: AdRequest.Builder
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private val ADFREE_NAME = "CQi7aLBQH7dR7qyrCG"
//    private lateinit var billingClient: BillingClient
//    private val sku3600 = "adfree_unlimited_entry"
//    private lateinit var flowParams: BillingFlowParams


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sf = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

//        this.intent.removeExtra(KEY)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        if(!(intent.getIntExtra(ADFREE_NAME,6) == 18 || sf.getInt(ADFREE_NAME,6) ==18)){
           AdUtility(applicationContext).setAds()
        }

        Handler().postDelayed(
            {
                loading.visibility = View.GONE
                homeViewPagerFragmentpage.visibility = View.VISIBLE
            }, 2100
        )
    }

}
