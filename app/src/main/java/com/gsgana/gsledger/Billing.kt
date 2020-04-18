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
////
//public class BillingManager implements BillingProcessor.IBillingHandler {
//    private BaseActivity activity;
//    private BillingCallback billingCallback; // # 제가 정의한 콜백함수입니다. 각 화면마다 구독 했을때 원하는 결과가 다를 수도 있겠죠!
//    private BillingProcessor bp;
//    private AppStorage storage;
//
//    /*
//    - 변수 및 커스텀 클래스 참조
//    Config.GP_LICENSE_KEY: 구글 플레이 라이센스 키 (비밀!)
//    Config.SKU: 광고제거용 PRO 버전 (관리되는 제품)
//    Config.SUBSCRIBE_SKU: 1개월 광고제거 (구독 상품)
//    AdLoader(activity.mAdLoader): 구글 애드몹 광고를 보여주기 위해 제가 만든 클래스
//    AppStorage: SharedPreference를 쓰기 쉽게 제가 만든 클래스 -> 간단하게 현재 구독 및 결제 상태를 저장하기 위해 사용
//     */
//
//    public interface BillingCallback {
//        // # 원하는 함수가있다면 추가, 필요없다면 제거하기
//        void onPurchased(String productId); // # 구매가 정상적으로 완료되었을때 해당 제품 아이디를 넘겨줍니다.
//        void onUpdatePrice(Pair<Double, Double> prices); // # 화면에 가격을 표시하고 싶으므로 가격 정보를 넘겨줍니다.
//    }
//
//    public BillingManager(BaseActivity activity) {
//        this.activity = activity;
//        this.storage = new AppStorage(activity);
//    }
//
//    public BillingManager init(BillingCallback billingCallback) {
//        this.billingCallback = billingCallback;
//        bp = new BillingProcessor(activity, Config.GP_LICENSE_KEY, this);
//        bp.initialize();
//        return this;
//    }
//
//    /**
//     * 구독 또는 구매 완료시
//     * @param productId 제품 아이디
//     * @param details 거래 정보
//     */
//    @Override
//    public void onProductPurchased(String productId, TransactionDetails details) {
//        bp.loadOwnedPurchasesFromGoogle(); // 구매정보 업데이트
//        if (billingCallback != null) {
//            billingCallback.onPurchased(productId);
//        }
//        onResume();
//    }
//
//    @Override
//    public void onPurchaseHistoryRestored() {
//        // # 구매 복원 호출시 이 함수가 실행됩니다.
//        onResume();
//    }
//
//    @Override
//    public void onBillingError(int errorCode, Throwable error) {
//        // # 결제 오류시 따로 토스트 메세지를 표시하고 싶으시면 여기에 하시면됩니다.
//    }
//
//    /**
//     * BillingProcessor 초기화 완료시
//     */
//    @Override
//    public void onBillingInitialized() {
//        SkuDetails details = bp.getPurchaseListingDetails(Config.SKU); // PRO 버전 정보
//        SkuDetails subDetails = bp.getSubscriptionListingDetails(Config.SUBSCRIBE_SKU); // 1개월 구독 정보
//        if (billingCallback != null && details != null) {
//            // # SkuDetails.priceValue: ex) 1,000원일경우 => 1000.00
//            Pair<Double, Double> pair = new Pair<>(details.priceValue, subDetails.priceValue);
//            billingCallback.onUpdatePrice(pair);
//        }
//        bp.loadOwnedPurchasesFromGoogle(); // 구매정보 업데이트
//        onResume();
//    }
//
//    /**
//     * 인앱 상품 구매하기
//     */
//    public void purchase() {
//        if (bp != null && bp.isInitialized()) {
//            if (bp.isSubscribed(Config.SUBSCRIBE_SKU)) {
//                Toast.makeText(activity, "이미 광고 제거 상품을 구독중입니다. 이중 결제 방지를 위해 구독이 끝나면 PRO 버전을 구매 해 주십시오.", Toast.LENGTH_LONG).show();
//            } else {
//                bp.purchase(activity, Config.SKU);
//            }
//        }
//    }
//
//    /**
//     * 구독하기
//     */
//    public void subscribe() {
//        if (bp != null && bp.isInitialized()) {
//            // # 저는 PRO 버전도 같이 팔고있기 때문에 중복 구입 방지를 위해 구매여부 체크를 해두었습니다.
//            if (!bp.isPurchased(Config.SKU) && !bp.isSubscribed(Config.SUBSCRIBE_SKU)) {
//                bp.subscribe(activity, Config.SUBSCRIBE_SKU);
//            }
//        }
//    }
//
//    public void onResume() {
//        // # SharedPreference에 구매 여부를 저장 해 두고, 그에 따라 광고를 바로 숨기거나 보여주는 코드입니다.
//        bp.loadOwnedPurchasesFromGoogle();
//        // # PRO 버전 구매를 했거나 구독을 했다면!
//        storage.setPurchasedProVersion(bp.isPurchased(Config.SKU) || bp.isSubscribed(Config.SUBSCRIBE_SKU));
//        // # 안에는 대충 이런 코드입니다. purchased ? adView.setVisibility(View.GONE) : View.VISIBLE
//        if (activity.mAdLoader != null) activity.mAdLoader.update();
//    }
//
//    public void onDestroy() {
//        // # 꼭! 릴리즈 해주세요.
//        if (bp != null) {
//            bp.release();
//        }
//    }
//
//    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
//        return bp.handleActivityResult(requestCode, resultCode, data);
//    }
//}
