package com.gsgana.gsledger.utilities

//import android.app.Activity
//import android.content.SharedPreferences
//import com.anjlab.android.iab.v3.BillingProcessor
//import com.anjlab.android.iab.v3.SkuDetails
//import com.anjlab.android.iab.v3.TransactionDetails
//
//class BillingManager : BillingProcessor.IBillingHandler {
//
//    private lateinit var activity: Activity
//    private lateinit var bp: BillingProcessor
//    private lateinit var sf: SharedPreferences
//    private lateinit var billingCallback: BillingCallback
//
//    fun BillingManager(activity: Activity){
//        this.activity = activity
//        this.sf = activity.getSharedPreferences()
//    }
//
//    fun init(billingCallback: BillingCallback): BillingManager {
//        this.billingCallback = billingCallback;
//        bp = BillingProcessor(activity, Config.GP_LICENSE_KEY, this)
//        bp.initialize();
//        return this
//    }
//
//    override fun onBillingInitialized() {
//        val details : SkuDetails = bp.getPurchaseListingDetails(Config.SKU); // PRO 버전 정보
//        val subDetails :SkuDetails = bp.getSubscriptionListingDetails(Config.SUBSCRIBE_SKU); // 1개월 구독 정보
//        if (billingCallback != null && details != null) {
//            // # SkuDetails.priceValue: ex) 1,000원일경우 => 1000.00
//            val pair : Map<Double, Double> = mapOf<Double,Double>(details.priceValue, subDetails.priceValue)
//            billingCallback.onUpdatePrice(pair);
//        }
//        bp.loadOwnedPurchasesFromGoogle(); // 구매정보 업데이트
//        onResume();
//    }
//
//    override fun onPurchaseHistoryRestored() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
//        bp.loadOwnedPurchasesFromGoogle(); // 구매정보 업데이트
//        if (billingCallback != null) {
//            billingCallback.onPurchased(productId);
//        }
//
//    }
//
//    override fun onBillingError(errorCode: Int, error: Throwable?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//
//     interface BillingCallback {
//        // # 원하는 함수가있다면 추가, 필요없다면 제거하기
//        fun  onPurchased(productId :String ); // # 구매가 정상적으로 완료되었을때 해당 제품 아이디를 넘겨줍니다.
//         fun onUpdatePrice(prices : Map<Double,Double>); // # 화면에 가격을 표시하고 싶으므로 가격 정보를 넘겨줍니다.
//    }
//
//
//    fun subscribe() {
//        if (bp != null && bp.isInitialized) {
//            // # 저는 PRO 버전도 같이 팔고있기 때문에 중복 구입 방지를 위해 구매여부 체크를 해두었습니다.
//            if (!bp.isPurchased(Config.SKU) && !bp.isSubscribed(Config.SUBSCRIBE_SKU)) {
//                bp.subscribe(activity, Config.SUBSCRIBE_SKU);
//            }
//        }
//    }
//    fun onResume() {
//        // # SharedPreference에 구매 여부를 저장 해 두고, 그에 따라 광고를 바로 숨기거나 보여주는 코드입니다.
//        bp.loadOwnedPurchasesFromGoogle();
//        // # PRO 버전 구매를 했거나 구독을 했다면!
//
//        sf.edit().putBoolean("",bp.isPurchased(Config.SKU) || bp.isSubscribed(Config.SUBSCRIBE_SKU));
//        // # 안에는 대충 이런 코드입니다. purchased ? adView.setVisibility(View.GONE) : View.VISIBLE
//
//        if (activity.mAdLoader != null) activity.mAdLoader.update();
//    }
//
//    fun onDestroy() {
//        // # 꼭! 릴리즈 해주세요.
//        if (bp != null) {
//            bp.release();
//        }
//    }
//}