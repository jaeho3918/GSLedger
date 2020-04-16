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
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.CHART_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.LEDGER_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.STAT_PAGE_INDEX
import com.gsgana.gsledger.databinding.HomeViewPagerFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.CURRENCYSYMBOL
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.WEIGHTUNIT
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@Suppress("UNCHECKED_CAST")
class HomeViewPagerFragment : Fragment() {
    private lateinit var binding: HomeViewPagerFragmentBinding
    private val REAL_DB_PATH = "sYTVBn6F18VT6Ykw6L"

//    private val LAST_DB_PATH = "OGn6sgTK6umHojW6QV"
//    private val REAL_NAME = "sYTVBn2FO8VNT9Ykw90L"

    private val PREF_NAME = "01504f779d6c77df04"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val WEIGHT_NAME = "f79604050dfc500715"
//
//    private lateinit var mAuth: FirebaseAuth
//    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"
//    private lateinit var rgl: CharArray
//    private lateinit var rgl_b: MutableList<Char>
//    private lateinit var database: FirebaseDatabase

    private lateinit var option: SharedPreferences

    private var currencyOption: Int? = null
    private var weightOption: Int? = null

    private var weight: Double? = null

    private lateinit var databaseRef: DatabaseReference
    private lateinit var mAdView: AdView

    private lateinit var calendar: TimeZone

//    private lateinit var viewModel: HomeViewPagerViewModel

    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(activity!!, activity!!.intent.getCharArrayExtra(KEY))
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        calendar = Calendar.getInstance().timeZone
        option = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        databaseRef = FirebaseDatabase.getInstance().getReference(REAL_DB_PATH)

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                val data = p0.value as HashMap<String, Double>
                currencyOption = option.getInt(CURR_NAME, 0)
                weightOption = option.getInt(WEIGHT_NAME, 0)

                if (currencyOption != null) {
                    if (!viewModel.realData.value.isNullOrEmpty()) {
                        data["currency"] = viewModel.realData.value?.getValue("currency") ?: 0.0
                        data["weightUnit"] = viewModel.realData.value?.getValue("weightUnit") ?: 0.0
                    } else {
                        data["currency"] = currencyOption!!.toDouble()
                        data["weightUnit"] = weightOption!!.toDouble()
                    }
                }
                data["USD"] = 1.0
                viewModel.realData.value = data
            }
        }
        )

        binding = HomeViewPagerFragmentBinding.inflate(inflater, container, false)

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

        viewModel.realData.observe(viewLifecycleOwner) { realData ->
            val currencyOption = activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                ?.getInt(CURR_NAME, 0)

            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
            simpleDateFormat.timeZone = calendar
            val date = simpleDateFormat.format(Date(realData["DATE"]!!.toLong() * 1000))

            binding.realUpdatedDate.text = date


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
            ADSANDOPTION_PAGE_INDEX -> context?.resources?.getString(R.string.option)
            CHART_PAGE_INDEX -> context?.resources?.getString(R.string.chart)
            else -> null
        }
    }
}
