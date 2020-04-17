package com.gsgana.gsledger
//
//import android.util.Log
//import com.android.billingclient.api.*
//import com.android.billingclient.api.SkuDetailsResponseListener
//import com.google.android.gms.common.internal.service.Common
//import com.google.gson.Gson
//
//
//class Billing() {
//    private var billingClient: BillingClient? = null
//    fun initBilling() {
//        billingClient = BillingClient.newBuilder(mContext)
//            .enablePendingPurchases()
//            .setListener(purchasesUpdatedListener)
//            .build()
//        billingClient!!.startConnection(object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) { //연결 성공
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    billingHandler.sendEmptyMessage(Common.HANDLER_BILLING_GET_CONTENTS) //상품리스트 가저오기
//                } else {
//                    billingHandler.sendEmptyMessage(Common.HANDLER_BILLING_CONNECTION_FAIL)
//                }
//            }
//
//            //연결 끊김
//            override fun onBillingServiceDisconnected() {}
//        })
//    }
//
//    //구매확정
//    fun confirmPerchase(purchase: Purchase) { //PURCHASED
//        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//            if (!purchase.isAcknowledged) {
//                val acknowledgePurchaseParams =
//                    AcknowledgePurchaseParams.newBuilder()
//                        .setPurchaseToken(purchase.purchaseToken)
//                        .build()
//                billingClient!!.acknowledgePurchase(
//                    acknowledgePurchaseParams
//                ) { billingResult: BillingResult? -> }
//            }
//        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) { //구매 유예
//        } else { //구매확정 취소됨(기타 다양한 사유...)
//        }
//    }
//
//    private fun handlePurchase(purchase: Purchase) {
//        val purchase_json = purchase.originalJson
//        // 원하는 데이터를 가공해 서버에 저장시킨다
//    }
//
//    fun getContentsList() {
//        val sku_contents_list: MutableList<String> =
//            ArrayList()
//        sku_contents_list.add(itemName)
//        val params = SkuDetailsParams.newBuilder()
//        params.setSkusList(sku_contents_list).setType(BillingClient.SkuType.SUBS) //결제타입 지정
//        val listener =
//            SkuDetailsResponseListener { billingResult, skuDetailsList ->
//                //연결을 못함.
//                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
//                    return@SkuDetailsResponseListener
//                }
//                //상품정보를 가저오지 못함 -
//                if (skuDetailsList == null) {
//                    return@SkuDetailsResponseListener
//                }
//                //상품사이즈 체크
//                L.i("## response data size : " + skuDetailsList.size)
//                //상품가저오기 : 정기결제상품 하나라서 한개만 처리함.
//                try {
//                    for (skuDetails in skuDetailsList) {
//                        val title = skuDetails.title
//                        val sku = skuDetails.sku
//                        val price = skuDetails.price
//                        userSkuDetails = skuDetails
//                        if (itemName.equals(sku)) {
//                            showBilling(skuDetails)
//                        }
//                    }
//                } catch (e: Exception) {
//                    L.e("## 리스트 가저오기 오류$e")
//                }
//            }
//        billingClient!!.querySkuDetailsAsync(params.build(), listener)
//    }
//
//    fun checkPurchase() {
//        billingClient = BillingClient.newBuilder(mContext)
//            .enablePendingPurchases()
//            .setListener(purchasesUpdatedListener)
//            .build()
//        billingClient!!.startConnection(object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) { //connection success
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) { //check query
//                    val purchasesResult =
//                        billingClient!!.queryPurchases(BillingClient.SkuType.SUBS)
//                    val list = purchasesResult.purchasesList.size
//                    //앱스토어에서 정기결제 구독한 사람
//                    if (list >= 0) {
//                        for (purchase in purchasesResult.purchasesList) {
//                            val gson = Gson()
//                            val bsVO: BillingStateVO = gson.fromJson(
//                                purchase.originalJson,
//                                BillingStateVO::class.java
//                            )
//                            var isPerchase = false
//                            isPerchase =
//                                pakageName.equals(bsVO.getPackageName()) && itemName.equals(bsVO.getProductId())
//                            //해당앱 정기 구독한 사람
//                            if (isPerchase) { //정기구독 유지중인 사람
//                                if (bsVO.isAutoRenewing()) {
//                                } else {
//                                }
//                            } else {
//                            }
//                        }
//                    } else {
//                    }
//                } else {
//                }
//            }
//
//            override fun onBillingServiceDisconnected() { //
//            }
//        })
//    }
//
////    //purchase callback
////    private val purchasesUpdatedListener =
////        PurchasesUpdatedListener {
////            billingResult:BillingResult?, @Nullable purchases:kotlin.collections.MutableList<Purchase?>? ->  //성공
////            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
////                for (purchaseItem in purchases) {
////                    handlePurchase(purchaseItem!!) //구매 정보
////                    confirmPerchase(purchaseItem!!) //구매 확정
////                }
////            } else if (BillingClient.BillingResponseCode.USER_CANCELED == billingResult.getResponseCode()) {
////            } else {
////            }
////        }
////
////    var billingHandler: Handler = object : Handler() {
////        fun handleMessage(msg: Notification.MessagingStyle.Message) {
////            super.handleMessage(msg)
////            when (msg.what) {
////                Common.HANDLER_BILLING_INIT -> initBilling()
////                Common.HANDLER_BILLING_GET_CONTENTS -> getContentsList()
////                Common.HANDLER_BILLING_SHOW_PURCHASE -> showBilling(userSkuDetails)
////                Common.HANDLER_BILLING_CHECK_PERCHASE -> checkPurchaseAppCache()
////                Common.HANDLER_BILLING_HISTORY_PURCHASE -> checkPurchaseHttpConection()
////                Common.HANDLER_BILLING_CONNECTION_FAIL -> unstableServer()
////            }
////        }
////    }
//}
//
//class BillingStateVO {
//    var orderId: String? = null
//    var packageName: String? = null
//    var productId: String? = null
//    var purchaseTime //long
//            : Long = 0
//    var purchaseState //int
//            : String? = null
//    var purchaseToken: String? = null
//    var isAutoRenewing //boolean
//            = false
//    var isAcknowledged //boolean
//            = false
//
//    fun printVO() {
//        Log.i(
//            "",
//            "\\n## orderId : \" + orderId +\n" +
//                    "                    \"\\n pakageName : \" + packageName +\n" +
//                    "                    \"\\n productId : \" + productId +\n" +
//                    "                    \"\\n purchaseTime : \" + purchaseTime +\n" +
//                    "                    \"\\n purchaseState : \" + purchaseState +\n" +
//                    "                    \"\\n purchaseToken : \" + purchaseToken +\n" +
//                    "                    \"\\n autoRenewing : \" + isAutoRenewing +\n" +
//                    "                    \"\\n acknowledged : \" + isAcknowledged + "
//        )
//    }
//}
//

