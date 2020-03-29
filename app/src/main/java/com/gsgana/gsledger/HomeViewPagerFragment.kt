package com.gsgana.gsledger

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.gsgana.gsledger.adapters.PagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.ADSANDOPTION_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.LIST_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.STAT_PAGE_INDEX
import com.gsgana.gsledger.databinding.HomeViewPagerFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class HomeViewPagerFragment : Fragment() {
    private lateinit var binding: HomeViewPagerFragmentBinding
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val PREF_NAME = "01504f779d6c77df04"
    private val REAL_DB_PATH = "sYTVBn6F18VT6Ykw6L"
//    private val LAST_DB_PATH = "OGn6sgTK6umHojW6QV"
//    private val REAL_NAME = "sYTVBn2FO8VNT9Ykw90L"

//    private lateinit var mAuth: FirebaseAuth
//    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"
//
//    private lateinit var rgl: CharArray
//    private lateinit var rgl_b: MutableList<Char>
//
//    private lateinit var database: FirebaseDatabase
//
//    private var currencyOption: Int? = null
//    private var white: Int? = null
//    private var gray: Int? = null
//    private var red: Int? = null
//    private var green: Int? = null
//    private var blue: Int? = null
//    private var preAu: Int? = null
//    private var preAg: Int? = null
//    private var duration: Long = 530
//    private var color_up: Int? = null
//    private var color_down: Int? = null

    private lateinit var databaseRef: DatabaseReference

    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(requireActivity(), null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val currencyOption =
            activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)?.getInt(CURR_NAME, 0)

        databaseRef = FirebaseDatabase.getInstance().getReference(REAL_DB_PATH)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val data = p0?.value as HashMap<String, Double>

                if (currencyOption != null) {
                    if (!viewModel.realData.value.isNullOrEmpty()) {
                        data["currency"] = viewModel.realData.value?.getValue("currency") ?: 0.0
                    } else {
                        data["currency"] = currencyOption.toDouble()
                    }
                }
                data["USD"] = 1.0

                viewModel.realData.value = data

                /////////////////////////////////////////////////////////////////////////////////////////////

            }
        }
        )

        binding = HomeViewPagerFragmentBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = PagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        viewModel.realData.observe(viewLifecycleOwner) { realData ->
            val currencyOption = activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                ?.getInt(CURR_NAME, 0)

            binding.realUpdatedDate.text = SimpleDateFormat("yyyy/MM/dd HH:mm")
                .format(Date(realData["DATE"]!!.toLong() * 1000))


            binding.realGoldCurrency.text = CURRENCYSYMBOL[currencyOption ?: 0]
            binding.realSilverCurrency.text = CURRENCYSYMBOL[currencyOption ?: 0]
            binding.realGoldCurrency.text = CURRENCYSYMBOL[currencyOption ?: 0]
            binding.realSilverCurrency.text = CURRENCYSYMBOL[currencyOption ?: 0]

            binding.realGoldPrice.text = String.format(
                "%,.2f",
                (realData["AU"]!! * (realData[CURRENCY[currencyOption ?: 0]] ?: error("")))
            )
            binding.realSilverPrice.text = String.format(
                "%,.2f",
                (realData["AG"]!! * realData[CURRENCY[currencyOption!!]]!!)
            )


            /////////////////////////////////////////////////////////////////////////////////////////////

            val divAuValue =
                ((realData["AU"] ?: 0.0) - (realData["YESAU"] ?: 0.0)) / (realData["AU"]
                    ?: 0.0) * 100
            val divAgValue =
                ((realData["AG"] ?: 0.0) - (realData["YESAG"] ?: 0.0)) / (realData["AG"]
                    ?: 0.0) * 100
            setPriceColor(context!!,divAuValue,"pl",binding.realGoldPL)
            setPriceColor(context!!,divAgValue,"pl",binding.realSilverPL)
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


    private fun getTabTitle(position: Int): String? {
        return when (position) {
            STAT_PAGE_INDEX -> "Stat"
            LIST_PAGE_INDEX -> "List"
            ADSANDOPTION_PAGE_INDEX -> "AdsAndOption"
            else -> null
        }
    }
}
