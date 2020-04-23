package com.gsgana.gsledger

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import com.gsgana.gsledger.adapters.PagerAdapter
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.ADSANDOPTION_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.LEDGER_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.STAT_PAGE_INDEX
import com.gsgana.gsledger.databinding.HomeViewPagerFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.CURRENCYSYMBOL
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.WEIGHTUNIT
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.stat_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@Suppress("UNCHECKED_CAST")
class HomeViewPagerFragment : Fragment() {
    private lateinit var binding: HomeViewPagerFragmentBinding
    private val REAL_DB_PATH = "sYTVBn6F18VT6Ykw6L"

    private val PREF_NAME = "01504f779d6c77df04"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val WEIGHT_NAME = "f79604050dfc500715"
    private val TODAY_NAME = "0d07f05fd0c595f615"

    private lateinit var option: SharedPreferences

    private var currencyOption: Int? = null
    private var weightOption: Int? = null

    private var weight: Double? = null

    private lateinit var databaseRef: DatabaseReference
    private lateinit var mAdView: AdView

    private lateinit var calendar: Calendar

    private lateinit var dateTime : String

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


        databaseRef = FirebaseDatabase.getInstance().getReference(REAL_DB_PATH)

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                val data = p0.value as HashMap<String, Double>
                dateTime = data.get("DATE") as String
                data.remove("DATE")

                currencyOption = sf.getInt(CURR_NAME, 0)
                weightOption = sf.getInt(WEIGHT_NAME, 0)

                if (currencyOption != null) {
                    if (!viewModel.getRealData().value.isNullOrEmpty()) {
                        data["currency"] =
                            viewModel.getRealData().value?.getValue("currency") ?: 0.0
                        data["weightUnit"] =
                            viewModel.getRealData().value?.getValue("weightUnit") ?: 0.0
                    } else {
                        data["currency"] = currencyOption!!.toDouble()
                        data["weightUnit"] = weightOption!!.toDouble()
                    }
                }
                data["USD"] = 1.0
                viewModel.setRealData(data) //1587652649.61714
            }
        }

        )

        binding = HomeViewPagerFragmentBinding.inflate(inflater, container, false)

        if (!(activity!!.intent.getIntExtra(ADFREE_NAME, 6) == 18 || sf.getInt(
                ADFREE_NAME,
                6
            ) == 18)
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

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val utcTime = simpleDateFormat.parse(dateTime)

            val offset = TimeZone.getDefault().rawOffset;
            val nowTime = utcTime.time + offset
            val date =  Date(nowTime)
            val dateString = simpleDateFormat.format(date)

            sf.edit().putString(TODAY_NAME, dateString.substring(0..10)).apply()

            binding.realUpdatedDate.text = dateString.substring(0..15) + " Utc"
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

            goldRealCurrency.text = CURRENCYSYMBOL[realData["currency"]!!.toInt()]
            goldRealPrice.text =
                String.format(
                    "%,.2f",
                    realData["AU"]!! * realData[CURRENCY[realData["currency"]!!.toInt()]]!! * weight!!
                )
            goldRealLayout.visibility = View.VISIBLE
            goldRealWeight.text = "1 " + WEIGHTUNIT[realData.getValue("weightUnit").toInt()] + ": "

            silverRealCurrency.text = CURRENCYSYMBOL[realData["currency"]!!.toInt()]
            silverRealPrice.text =
                String.format(
                    "%,.2f",
                    realData["AG"]!! * realData[CURRENCY[realData["currency"]!!.toInt()]]!! * weight!!
                )
            silverRealLayout.visibility = View.VISIBLE

            silverRealWeight.text =
                "1 " + WEIGHTUNIT[realData.getValue("weightUnit").toInt()] + ": "


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
//            CHART_PAGE_INDEX -> context?.resources?.getString(R.string.chart)
            ADSANDOPTION_PAGE_INDEX -> context?.resources?.getString(R.string.option)
            else -> null
        }
    }
}
