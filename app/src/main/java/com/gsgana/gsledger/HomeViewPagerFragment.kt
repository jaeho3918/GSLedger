package com.gsgana.gsledger

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import com.google.firebase.functions.FirebaseFunctions
import com.gsgana.gsledger.adapters.PagerAdapter
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.ADSANDOPTION_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.CHART_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.LEDGER_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.STAT_PAGE_INDEX
import com.gsgana.gsledger.databinding.HomeViewPagerFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.CURRENCYSYMBOL
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.WEIGHTUNIT
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import com.gsgana.gsledger.viewmodels.WriteViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Suppress("UNCHECKED_CAST")
class HomeViewPagerFragment : Fragment() {
    private lateinit var binding: HomeViewPagerFragmentBinding
    private val REAL_DB_PATH = "sYTVBn6F18VT6Ykw6L" //v6WqgKE6RLT6JkFuBv   실제 sYTVBn6F18VT6Ykw6L

    private val REAL_STACK_DB_PATH = "AeBuYTRW4x0B2QQQIt"

    private val PREF_NAME = "01504f779d6c77df04"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val WEIGHT_NAME = "f79604050dfc500715"
    private val TODAY_NAME = "0d07f05fd0c595f615"

    private val START_NAME = "zT6VjmOul6oDKF6FUI"

    private var last_ag = 0.0
    private var last_au = 0.0

    private val functions = FirebaseFunctions.getInstance()

    private val NEW_LABEL = "RECSHenWYqdadfXOog"
    private val NEW_ENCRYPT = "X67LWGmYAc3rlCbmPe"
    private val NUMBER = "HYf75f2q2a36enW18b"

    private val DURATION: Long = 180

    private var currencyOption: Int? = null
    private var weightOption: Int? = null

    private var weight: Double? = null

    private lateinit var databaseRef: FirebaseDatabase
    private lateinit var mAdView: AdView

    private lateinit var calendar: Calendar

    private val ADFREE_NAME = "CQi7aLBQH7dR7qyrCG"
    private lateinit var sf: SharedPreferences

    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(
            activity!!,
            activity!!.intent.getCharArrayExtra(KEY)
        )
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sf = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        calendar = Calendar.getInstance()

        databaseRef = FirebaseDatabase.getInstance()

        databaseRef
            .getReference(REAL_DB_PATH)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    val data = p0.value as HashMap<String, Double>
                    viewModel.setDateTime(data.get("DATE") as Long)
                    data.remove("DATE")

                    currencyOption = sf.getInt(CURR_NAME, 0)
                    weightOption = sf.getInt(WEIGHT_NAME, 0)

                    data["currency"] = currencyOption!!.toDouble()
                    data["weightUnit"] = weightOption!!.toDouble()

                    data["USD"] = 1.0
                    viewModel.setRealData(data) //1587652649.61714
                }
            })

        databaseRef
            .getReference(REAL_STACK_DB_PATH)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    val data = p0.value as Map<String, Map<String, Double>>
                    val data_sorted = data.toSortedMap()
                    // 받은 데이터를 value_au, value_ag, date 로 정렬하기
                    val array_AU = arrayListOf<Float>()
                    val array_AG = arrayListOf<Float>()
                    val array_DATE = arrayListOf<String>()

                    val cal = Calendar.getInstance()
                    val tz = cal.timeZone
                    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
                    sdf.timeZone = tz

                    data_sorted.forEach { mapData ->
                        array_AU.add(mapData.value.getValue("AU").toFloat())
                        array_AG.add(mapData.value.getValue("AG").toFloat())
                        array_DATE.add(sdf.format(mapData.value.getValue("DATE") * 1000).substring(6..15))
                    }

                    viewModel.setstackChartData(
                        mapOf(
                            "value_AU" to array_AU,
                            "value_AG" to array_AG,
                            "date" to array_DATE
                        )
                    )
                }
            })

        getChart(
            sf.getString(NEW_LABEL, "")!!,
            sf.getString(NEW_ENCRYPT, "")!!,
            sf.getInt(NUMBER, 0)
        ).addOnSuccessListener { data ->
            if (!data.isNullOrEmpty()) {
                viewModel.setchartData(data)
            }
        }

        Handler().postDelayed({
            getSSShortChart(
                sf.getString(NEW_LABEL, "")!!,
                sf.getString(NEW_ENCRYPT, "")!!,
                sf.getInt(NUMBER, 0)
            ).addOnSuccessListener { data ->
                if (!data.isNullOrEmpty()) {
                    viewModel.setSSShortchartData(data)
                }
            }
        }, 333)

        Handler().postDelayed({
            getLongChart(
                sf.getString(NEW_LABEL, "")!!,
                sf.getString(NEW_ENCRYPT, "")!!,
                sf.getInt(NUMBER, 0)
            ).addOnSuccessListener { data ->
                if (!data.isNullOrEmpty()) {
                    viewModel.setLongchartData(data)
                }
            }
        }, 666)





        binding = HomeViewPagerFragmentBinding.inflate(inflater, container, false)

        if (!(activity!!.intent.getIntExtra(ADFREE_NAME, 6) == 18 ||
                    sf.getInt(ADFREE_NAME, 6) == 18)
        ) {
            binding.adView.visibility = View.VISIBLE
        }

        val mTransform = Linkify.TransformFilter { _: Matcher, _: String ->
            ""
        }
        val pattern1 = Pattern.compile("Disclaimer")
        val pattern2 = Pattern.compile("면책조항")

        Linkify.addLinks(
            binding.disclaimer,
            pattern1,
            "https://gsledger-29cad.firebaseapp.com/disclaimer.html",
            null,
            mTransform
        )
        Linkify.addLinks(
            binding.disclaimer,
            pattern2,
            "https://gsledger-29cad.firebaseapp.com/disclaimer_kr.html",
            null,
            mTransform
        )

        MobileAds.initialize(context)
        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = PagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(context, position)
        }.attach()

        viewModel.getRealData().observe(viewLifecycleOwner) { realData ->

            val currencyOption = activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                ?.getInt(CURR_NAME, 0)

//            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
//            val date1 = simpleDateFormat.parse(viewModel.getDateTime())
//
//            val nowDf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//
//            val dateString = nowDf.format(date1)

            val cal = Calendar.getInstance()
            val tz = cal.timeZone
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
            sdf.timeZone = tz

            val timestamp = viewModel.getDateTime()
            val localTime = sdf.format(Date(timestamp.toLong() * 1000))


//sf.edit().putString(TODAY_NAME, dateString.substring(0..15)).apply()
            sf.edit().putString(TODAY_NAME, localTime).apply()

            binding.realUpdatedDate.text = localTime
            binding.realGoldCurrency.text = CURRENCYSYMBOL[currencyOption ?: 0]
            binding.realSilverCurrency.text = CURRENCYSYMBOL[currencyOption ?: 0]
            binding.realGoldCurrency.text = CURRENCYSYMBOL[currencyOption ?: 0]
            binding.realSilverCurrency.text = CURRENCYSYMBOL[currencyOption ?: 0]

            weight = when (realData["weightUnit"]!!) {
                0.0 -> 1.0 //toz
                1.0 -> 0.03215 //g
                2.0 -> 32.150747  //kg
                3.0 -> 0.120565//don
                else -> 1.0
            }

            binding.weightUnitLabel.text =
                "(Unit: 1" + WEIGHTUNIT[realData.getValue("weightUnit").toInt()] + ")"

            binding.realGoldPrice.text = String.format(
                "%,.2f",
                (realData["AU"]!! * realData.getValue(CURRENCY[currencyOption!!]) * weight!!

                        )
            )
            binding.realSilverPrice.text = String.format(
                "%,.2f",
                (realData["AG"]!! * realData.getValue(CURRENCY[currencyOption]) * weight!!)
            )

            /////////////////////////////////////////////////////////////////////////////////////////////


            val divAuValue =
                ((realData["AU"] ?: 0.0) - (realData["YESAU"] ?: 0.0)) / (realData["AU"]
                    ?: 0.0) * 100
            val divAgValue =
                ((realData["AG"] ?: 0.0) - (realData["YESAG"] ?: 0.0)) / (realData["AG"]
                    ?: 0.0) * 100
            setPriceColor(context!!, divAuValue, "pl", binding.realGoldPL)
            setPriceColor(context!!, divAgValue, "pl", binding.realSilverPL)

            if (divAuValue != last_au) {
                if (divAuValue > last_au) {
                    val objectAnimator = ObjectAnimator.ofObject(
                        binding.realGoldPrice,
                        "backgroundColor",
                        ArgbEvaluator(),
                        ContextCompat.getColor(context!!, R.color.white),
                        ContextCompat.getColor(context!!, R.color.mu1_data_up)
                    )
                    objectAnimator.repeatCount = 1
                    objectAnimator.repeatMode = ValueAnimator.REVERSE
                    objectAnimator.duration = DURATION
                    objectAnimator.start()
                    last_au = divAuValue
                } else {
                    val objectAnimator = ObjectAnimator.ofObject(
                        binding.realGoldPrice,
                        "backgroundColor",
                        ArgbEvaluator(),
                        ContextCompat.getColor(context!!, R.color.white),
                        ContextCompat.getColor(context!!, R.color.mu1_data_down)
                    )
                    objectAnimator.repeatCount = 1
                    objectAnimator.repeatMode = ValueAnimator.REVERSE
                    objectAnimator.duration = DURATION
                    objectAnimator.start()
                    last_au = divAuValue
                }
            }

            if (divAgValue != last_ag) {
                if (divAgValue > last_ag) {
                    val objectAnimator = ObjectAnimator.ofObject(
                        binding.realSilverPrice,
                        "backgroundColor",
                        ArgbEvaluator(),
                        ContextCompat.getColor(context!!, R.color.white),
                        ContextCompat.getColor(context!!, R.color.mu1_data_up)
                    )
                    objectAnimator.repeatCount = 1
                    objectAnimator.repeatMode = ValueAnimator.REVERSE
                    objectAnimator.duration = DURATION
                    objectAnimator.start()
                    last_ag = divAgValue
                } else {
                    val objectAnimator = ObjectAnimator.ofObject(
                        binding.realSilverPrice,
                        "backgroundColor",
                        ArgbEvaluator(),
                        ContextCompat.getColor(context!!, R.color.white),
                        ContextCompat.getColor(context!!, R.color.mu1_data_down)
                    )
                    objectAnimator.repeatCount = 1
                    objectAnimator.repeatMode = ValueAnimator.REVERSE
                    objectAnimator.duration = DURATION
                    objectAnimator.start()
                    last_ag = divAgValue
                }
            }

            last_au = divAuValue
            last_ag = divAgValue

        }

        if (sf.getInt(START_NAME, 6) == 6) {
            viewPager.doOnLayout {
                sf.edit().putInt(START_NAME, 18).apply()
                viewPager.currentItem = ADSANDOPTION_PAGE_INDEX
                viewPager.setCurrentItem(ADSANDOPTION_PAGE_INDEX, true)
            }
        }

        return binding.root
    }

    private fun setPriceColor(
        context: Context,
        price: Double,
        type: String,
        textView: TextView,
        style: Int = 0,
        plSwitch: Int = 1
    ): Int {

        if (plSwitch == 0) {
            textView.visibility = View.INVISIBLE
            return 0
        }
//        val white = ContextCompat.getColor(context, R.color.white)
//        val gray = ContextCompat.getColor(context, R.color.colorAccent)
        val red = ContextCompat.getColor(context, R.color.mu1_data_down)
        val green = ContextCompat.getColor(context, R.color.mu1_data_up)
        val blue = ContextCompat.getColor(context, R.color.mu2_data_down)

        val string = when (type) {
            "priceint" -> {
                String.format("%,.0f", price)
            }

            "pricefloat" -> {
                String.format("%,.2f", price)
            }

            "pl" -> {
                when {
                    price > 0.01 -> {
                        if (style == 0) textView.setTextColor(green) else textView.setTextColor(
                            red
                        )
                        "(+" + String.format("%,.2f", price) + "%)"
                    }
                    price < -0.01 -> {
                        if (style == 0) textView.setTextColor(red) else textView.setTextColor(
                            blue
                        )
                        "(" + String.format("%,.2f", price) + "%)"
                    }
                    else -> "( 0.00%)"
                }
            }
            "pricepl" -> {
                when {
                    price > 1 -> {
                        if (style == 0) textView.setTextColor(green) else textView.setTextColor(
                            red
                        )
                        "+" + String.format("%,.0f", price)
                    }
                    price < -1 -> {
                        if (style == 0) textView.setTextColor(red) else textView.setTextColor(
                            blue
                        )
                        "" + String.format("%,.0f", price)
                    }
                    else -> "0"
                }
            }
            else -> {
                ""
            }
        }
        textView.text = string
        return 1
    }

    private fun getTabTitle(context: Context?, position: Int): String? {
        return when (position) {
            STAT_PAGE_INDEX -> context?.resources?.getString(R.string.overview)
            LEDGER_PAGE_INDEX -> context?.resources?.getString(R.string.list)
            CHART_PAGE_INDEX -> context?.resources?.getString(R.string.chart)
            ADSANDOPTION_PAGE_INDEX -> context?.resources?.getString(R.string.option)
            else -> null
        }
    }

    private fun getChart(label: String, reg: String, num: Int): Task<Map<String, ArrayList<*>>> {
        // Create the arguments to the callable function.

        val data = hashMapOf(
            "label" to label,
            "reg" to reg,
            "number" to num
        )

        return functions
            .getHttpsCallable("SG0ZRjr99Z1OAdeROjF1e6nS")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as Map<String, ArrayList<*>>
                result
            }
    }

    private fun getLongChart(
        label: String,
        reg: String,
        num: Int
    ): Task<Map<String, ArrayList<*>>> {

        // Create the arguments to the callable function.

        val data = hashMapOf(
            "label" to label,
            "reg" to reg,
            "number" to num
        )

        return functions
            .getHttpsCallable("L3Vi6HftOI0HK6VH6rHsB6At")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.

                val result = task.result?.data as Map<String, ArrayList<*>>
                result
            }
    }

    private fun getSSShortChart(
        label: String,
        reg: String,
        num: Int
    ): Task<Map<String, ArrayList<*>>> {

        // Create the arguments to the callable function.

        val data = hashMapOf(
            "label" to label,
            "reg" to reg,
            "number" to num
        )

        return functions
            .getHttpsCallable("SSSZRy57RJG1eb9JUuff45Q6")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.

                val result = task.result?.data as Map<String, ArrayList<*>>
                result
            }
    }


}

private fun getPrice(
    viewModel: WriteViewModel,
    price: String, label: String, reg: String, num: Int
): Task<Map<String, String>> {

    val functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    val test = viewModel.getdateField()!!.split("/")

    val date_buf = if (test != null) {
        val cal = Calendar.getInstance()
        val _year = cal.get(Calendar.YEAR)
        val _month = cal.get(Calendar.MONTH) + 1
        val _date = cal.get(Calendar.DATE)
        viewModel.getdateField() ?: String.format(
            "%04d%02d%02d", _year, _month, _date
        )
    } else {
        String.format(
            "%04d%02d%02d", test[0].toInt(), test[1].toInt(), test[2].toInt()
        )
    }
    // Create the arguments to the callable function.
    val data = hashMapOf(
        "metal" to viewModel.getmetalField1().toString(),
        "type1" to viewModel.gettypeField1().toString(),
        "brand" to viewModel.brand.value.toString(),
        "weight" to viewModel.weightCalculator.value,
        "quantity" to viewModel.getquantityField(),
        "weightr" to viewModel.weightUnit.value,
        "packageType1" to viewModel.getpackageTypeField().toString(),
        "grade" to viewModel.getgradeField().toString(),
        "gradeNum" to viewModel.getgradeNumField().toString(),
        "currency" to viewModel.getcurrencyField().toString(),
        "year" to viewModel.getyearSeriesField().toString(),
        "date" to date_buf,
        "priceMerger" to price,
        "price" to viewModel.price.value,
        "label" to label,
        "reg" to reg,
        "number" to num
    )

    return functions
        .getHttpsCallable("summitData18")
        .call(data)
        .continueWith { task ->
            // This continuation runs on either success or failure, but if the task
            // has failed then result will throw an Exception which will be
            // propagated down.
            val result = task.result?.data as Map<String, String>
            result
        }
}

fun getPriceData(
    viewModel: WriteViewModel,
    price: String, label: String, reg: String, num: Int
): Task<Map<String, String>> {
    return getPrice(viewModel, price, label, reg, num)
}