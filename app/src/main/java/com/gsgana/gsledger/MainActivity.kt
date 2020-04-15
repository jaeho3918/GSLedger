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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.gsgana.gsledger.databinding.ActivityMainBinding
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.activity_main.*


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

//    private val viewModel: HomeViewPagerViewModel by viewModels {
//        InjectorUtils.provideHomeViewPagerViewModelFactory(this, this.intent.getCharArrayExtra(KEY))
//    }
//
//    private val viewModel: HomeViewPagerViewModel by viewModels {
//        InjectorUtils.provideHomeViewPagerViewModelFactory(this, charArrayOf(
//            "q"[0],
//            "0"[0],
//            "J"[0],
//            "2"[0],
//            "3"[0],
//            "o"[0],
//            "1"[0],
//            "m"[0],
//            "D"[0],
//            "E"[0],
//            "Y"[0],
//            "y"[0],
//            "X"[0],
//            "j"[0],
//            "3"[0],
//            "Q"[0],
//            "s"[0],
//            "h"[0],
//            "E"[0],
//            "l"[0],
//            "8"[0],
//            "n"[0],
//            "j"[0],
//            "Y"[0],
//            "B"[0],
//            "P"[0],
//            "J"[0],
//            "C"[0],
//            "E"[0]
//        ))
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            }, 1500
        )
    }
}
