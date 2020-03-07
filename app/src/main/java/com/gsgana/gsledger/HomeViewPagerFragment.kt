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
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.gsgana.gsledger.adapters.PagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.ADSANDOPTION_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.LIST_PAGE_INDEX
import com.gsgana.gsledger.adapters.PagerAdapter.Companion.STAT_PAGE_INDEX
import com.gsgana.gsledger.databinding.HomeViewPagerFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModelFactory
import kotlin.collections.HashMap


class HomeViewPagerFragment : Fragment() {
    private lateinit var binding: HomeViewPagerFragmentBinding
    private val REAL_NAME = "sYTVBn2FO8VNT9Ykw90L"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val PREF_NAME = "01504f779d6c77df04"
    private val REAL_DB_PATH = "sYTVBn6F18VT6Ykw6L"
    private val LAST_DB_PATH = "OGn6sgTK6umHojW6QV"

    private lateinit var mAuth: FirebaseAuth
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"

    private lateinit var rgl: CharArray
    private lateinit var rgl_b: MutableList<Char>

    private lateinit var database: FirebaseDatabase

    private var currencyOption: Int? = null

    private var white: Int? = null
    private var gray: Int? = null
    private var red: Int? = null
    private var green: Int? = null
    private var blue: Int? = null
    private var preAu: Int? = null
    private var preAg: Int? = null
    private var duration: Long = 530

    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(requireActivity(), null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val currencyOption =
            activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)?.getInt(CURR_NAME, 0)

        val databaseRef = FirebaseDatabase.getInstance().getReference(REAL_DB_PATH)
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
                data["DATE"] = 0.0

                viewModel.realData.value = data

                /////////////////////////////////////////////////////////////////////////////////////////////


            }
        }
        )


//        currencyOption = activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)?.getInt(CURR_NAME, 0)


        white = ContextCompat.getColor(context!!, R.color.white)
        gray = ContextCompat.getColor(context!!, R.color.colorAccent)
        red = ContextCompat.getColor(context!!, R.color.mu1_data_down)
        green = ContextCompat.getColor(context!!, R.color.mu1_data_up)
        blue = ContextCompat.getColor(context!!, R.color.mu2_data_down)


        binding = HomeViewPagerFragmentBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = PagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        viewModel.realData.observe(viewLifecycleOwner) {
            val currencyOption = activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)?.getInt(CURR_NAME, 0)

            binding.realGoldCurrency.text = CURRENCYSYMBOL[currencyOption?:0]
            binding.realSilverCurrency.text = CURRENCYSYMBOL[currencyOption?:0]
            binding.realGoldCurrency.text = CURRENCYSYMBOL[currencyOption!!]
            binding.realSilverCurrency.text = CURRENCYSYMBOL[currencyOption!!]

            binding.realGoldPrice.text = String.format(
                " %.2f",
                (it["AU"]!! * (it[CURRENCY[currencyOption!!]] ?: error("")))
            )
            binding.realSilverPrice.text = String.format(
                " %.2f",
                (it["AG"]!! * it[CURRENCY[currencyOption!!]]!!)
            )


            /////////////////////////////////////////////////////////////////////////////////////////////
            val divAuValue =
                ((it["AU"] ?: 0.0) - (it["YESAU"] ?: 0.0)) / (it["AU"] ?: 0.0) * 100
            val divAgValue =
                ((it["AG"] ?: 0.0) - (it["YESAG"] ?: 0.0)) / (it["AG"] ?: 0.0) * 100
            when {
                divAuValue > 0 -> {
                    binding.realGoldPL.setText(
                        "(" + "+" + String.format(
                            " %.2f",
                            divAuValue
                        ) + "%)"
                    )
                }
                divAuValue < 0 -> {
                    binding.realGoldPL.setText(
                        "(" + "-" + String.format(
                            " %.2f",
                            -1 * divAuValue
                        ) + "%)"
                    )
                }
                else -> {
                    binding.realGoldPL.setText("( 0.00%)");
                }
            }
            when {
                divAgValue > 0 -> {
                    binding.realSilverPL.setText(
                        "(" + "+" + String.format(
                            " %.2f",
                            divAgValue
                        ) + "%)"
                    )
                }
                divAgValue < 0 -> {
                    binding.realSilverPL.setText(
                        "(" + "-" + String.format(
                            " %.2f",
                            -1 * divAgValue
                        ) + "%)"
                    )
                }
                else -> {
                    binding.realSilverPL.setText("( 0.00%)")
                }
            }
            if (preAu ?: 999999 > (it["AU"]?.toInt()!!)) {
                ObjectAnimator.ofObject(
                    binding.realGoldPrice,
                    "backgroundColor",
                    ArgbEvaluator(),
                    white,
                    red
                )
                    .setDuration(duration)
                    .start();
                ObjectAnimator.ofObject(
                    binding.realGoldPrice,
                    "backgroundColor",
                    ArgbEvaluator(),
                    red,
                    white
                )
                    .setDuration(duration)
                    .start();
            } else if (preAu == (it["AU"]?.toInt()!!)) {
                preAu = (it["AU"]?.toInt()!!)
            } else {
                ObjectAnimator.ofObject(
                    binding.realGoldPrice,
                    "backgroundColor",
                    ArgbEvaluator(),
                    white,
                    green
                )
                    .setDuration(duration)
                    .start();
                ObjectAnimator.ofObject(
                    binding.realGoldPrice,
                    "backgroundColor",
                    ArgbEvaluator(),
                    green,
                    white
                )
                    .setDuration(duration)
                    .start();
            }
            if (preAg ?: 999999 > (it["AG"]?.toInt()!!)) {
                ObjectAnimator.ofObject(
                    binding.realSilverPrice,
                    "backgroundColor",
                    ArgbEvaluator(),
                    white,
                    red
                )
                    .setDuration(duration)
                    .start();
                ObjectAnimator.ofObject(
                    binding.realSilverPrice,
                    "backgroundColor",
                    ArgbEvaluator(),
                    red,
                    white
                )
                    .setDuration(duration)
                    .start();
            } else if (preAg == it["AG"]?.toInt()!!) {
                preAg = (it["AG"]?.toInt()!!);
            } else {
                ObjectAnimator.ofObject(
                    binding.realSilverPrice,
                    "backgroundColor",
                    ArgbEvaluator(),
                    white,
                    green
                )
                    .setDuration(duration)
                    .start();
                ObjectAnimator.ofObject(
                    binding.realSilverPrice,
                    "backgroundColor",
                    ArgbEvaluator(),
                    green,
                    white
                )
                    .setDuration(duration)
                    .start();
            }



        }

        return binding.root
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
