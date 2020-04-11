package com.gsgana.gsledger

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*

class BillingManager(val activity: Activity) : PurchasesUpdatedListener {

    private val PREF_NAME = "01504f779d6c77df04"
    private val ADSUBSCRIBE = "0e918EXwERGhVlvTyy2A"

    private val sharedPrefUtils = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    val billingClient = BillingClient.newBuilder(activity).enablePendingPurchases().setListener(this).build()

    fun makeBillingClient(): BillingClient = BillingClient.newBuilder(activity).enablePendingPurchases().setListener(this).build()

    fun processToPurchase() {
        val skuList = ArrayList<String>()
        skuList.add(ADSUBSCRIBE)
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                val params = SkuDetailsParams.newBuilder()
                params.setSkusList(skuList)
                params.setType(BillingClient.SkuType.INAPP)
                launchBillingFlow(params)
            }
        })
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
    }


    fun getPurchaseHistory() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP) { billingResult, purchaseHistoryRecordList ->
                    if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                        if(purchaseHistoryRecordList != null && purchaseHistoryRecordList.size>0){
                            sharedPrefUtils.edit().putString( ADSUBSCRIBE, "on")
                        }else{
                            sharedPrefUtils.edit().putString( ADSUBSCRIBE, "off")
                        }
                    }
                }
            }
        })
    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                sharedPrefUtils.edit().putString( ADSUBSCRIBE, "on")
                handlePurchase(purchase)
            }
        } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            sharedPrefUtils.edit().putString( ADSUBSCRIBE, "on")
        } else {
            // Handle any other error codes.
        }
    }

    private fun launchBillingFlow(params: SkuDetailsParams.Builder) {
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (skuDetailsList.size > 0) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList[0])
                        .build()
                    val responseCode = billingClient.launchBillingFlow(activity, flowParams)
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams
            .newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .setDeveloperPayload(purchase.developerPayload)
            .build()

        billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken -> }
    }
}