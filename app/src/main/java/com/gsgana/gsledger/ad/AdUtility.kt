package com.gsgana.gsledger.ad

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds

class AdUtility(private val context: Context) {
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var mBuilder: AdRequest.Builder
    private var doneOnce = true
    private val AD_UNIT_ID =
        "ca-app-pub-3940256099942544/8691691433"
    // 실제   "ca-app-pub-8453032642509497/3082833180"
    // 테스트 "ca-app-pub-3940256099942544/8691691433"
    fun setAds() {
        MobileAds.initialize(context)
        mInterstitialAd = InterstitialAd(context)
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


}