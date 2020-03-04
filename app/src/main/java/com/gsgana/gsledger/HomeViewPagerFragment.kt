package com.gsgana.gsledger

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
    private lateinit var viewModelFactory: HomeViewPagerViewModelFactory
    private lateinit var viewModel: HomeViewPagerViewModel
    private lateinit var rgl: MutableList<Char>

    private var white: Int? = null
    private var gray: Int? = null
    private var red: Int? = null
    private var green: Int? = null
    private var blue: Int? = null
    private var preAu: Int? = null
    private var preAg: Int? = null
    private var duration: Long = 530


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()

        white = ContextCompat.getColor(context!!, R.color.white)
        gray = ContextCompat.getColor(context!!, R.color.colorAccent)
        red = ContextCompat.getColor(context!!, R.color.mu1_data_down)
        green = ContextCompat.getColor(context!!, R.color.mu1_data_up)
        blue = ContextCompat.getColor(context!!, R.color.mu2_data_down)

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(USERS_DB_PATH).document(mAuth.currentUser?.uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                val test = document.data?.get("Rgl") as ArrayList<String>
                for (s in test) {
                    this.rgl.add(s.toCharArray()[0])
                }
                test.clear()

                viewModelFactory = InjectorUtils.provideHomeViewPagerViewModelFactory(context!!,rgl.toCharArray())
                viewModel = viewModelFactory.getViewModel

//                val viewModel = activity.run {
//                    ViewModelProviders.of(
//                        activity!!,
//                        InjectorUtils.provideHomeViewPagerViewModelFactory(
//                            activity!!,
//                            rgl.toCharArray()
//                        )
//                    ).get(HomeViewPagerViewModel::class.java)
//                }
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
                        data["DATE"] = 0.0

                        if (viewModel.realData != null) viewModel.realData.postValue(data.toMutableMap())
                        binding.realUpdatedDate.text = (p0?.value as HashMap<String, String>)["DATE"]
                    }
                }
                )

                viewModel.realData.observe(viewLifecycleOwner) {

                    var currency = if (it[CURR_NAME] == 0.0) {
                        1.0
                    } else {
                        it[CURRENCY[(it[CURR_NAME]?.toInt() ?: 0)]]

                    }
                    binding.realGoldPrice.text =
                        String.format("%,.2f", (currency?.times((it["AU"])!!.toDouble())))
                    binding.realSilverPrice.text =
                        String.format(
                            "%,.2f", (currency?.times((it["AG"])!!.toDouble()))
                        )

                    binding.realGoldCurrency.text = CURRENCYSYMBOL[(it[CURR_NAME]?.toInt() ?: 0)]
                    binding.realSilverCurrency.text = CURRENCYSYMBOL[(it[CURR_NAME]?.toInt() ?: 0)]
                    /////////////////////////////////////////////////////////////////////////////////////////////
//                    val divAuValue = ((it["AU"] ?: 0.0) - (it["YESAU"] ?: 0.0)) / (it["AU"] ?: 0.0)
//                    val divAgValue = ((it["AG"] ?: 0.0) - (it["YESAG"] ?: 0.0)) / (it["AG"] ?: 0.0)
//                    when {
//                        divAuValue > 0 -> {
//                            binding.realGoldPrice.setText(
//                                "(" + "+" + String.format(
//                                    " %.2f",
//                                    divAuValue
//                                ) + "%)"
//                            )
//                        }
//                        divAuValue < 0 -> {
//                            binding.realGoldPrice.setText(
//                                "(" + "-" + String.format(
//                                    " %.2f",
//                                    -1 * divAuValue
//                                ) + "%)"
//                            )
//                        }
//                        else -> {
//                            binding.realGoldPrice.setText("( 0.00%)");
//                        }
//                    }
//                    when {
//                        divAgValue > 0 -> {
//                            binding.realSilverPrice.setText(
//                                "(" + "+" + String.format(
//                                    " %.2f",
//                                    divAgValue
//                                ) + "%)"
//                            )
//                        }
//                        divAgValue < 0 -> {
//                            binding.realSilverPrice.setText(
//                                "(" + "-" + String.format(
//                                    " %.2f",
//                                    -1 * divAgValue
//                                ) + "%)"
//                            )
//                        }
//                        else -> {
//                            binding.realSilverPrice.setText("( 0.00%)")
//                        }
//                    }
//                    if (preAu!! > (it["AU"]?.toInt()!!)) {
//                        ObjectAnimator.ofObject(
//                            binding.realGoldPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            white,
//                            red
//                        )
//                            .setDuration(duration)
//                            .start();
//                        ObjectAnimator.ofObject(
//                            binding.realGoldPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            red,
//                            white
//                        )
//                            .setDuration(duration)
//                            .start();
//                    } else if (preAu == null) {
//                        preAu = (it["AU"]?.toInt()!!)
//                    } else {
//                        ObjectAnimator.ofObject(
//                            binding.realGoldPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            white,
//                            green
//                        )
//                            .setDuration(duration)
//                            .start();
//                        ObjectAnimator.ofObject(
//                            binding.realGoldPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            green,
//                            white
//                        )
//                            .setDuration(duration)
//                            .start();
//                    }
//                    if (preAg!! > (it["AG"]?.toInt()!!)) {
//                        ObjectAnimator.ofObject(
//                            binding.realSilverPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            white,
//                            red
//                        )
//                            .setDuration(duration)
//                            .start();
//                        ObjectAnimator.ofObject(
//                            binding.realSilverPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            red,
//                            white
//                        )
//                            .setDuration(duration)
//                            .start();
//                    } else if (preAg == null) {
//                        preAg = (it["AG"]?.toInt()!!);
//                    } else {
//                        ObjectAnimator.ofObject(
//                            binding.realSilverPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            white,
//                            green
//                        )
//                            .setDuration(duration)
//                            .start();
//                        ObjectAnimator.ofObject(
//                            binding.realSilverPrice,
//                            "backgroundColor",
//                            ArgbEvaluator(),
//                            green,
//                            white
//                        )
//                            .setDuration(duration)
//                            .start();
//                    }

                    /////////////////////////////////////////////////////////////////////////////////////////////
                }


            }

        rgl.clear()

        binding = HomeViewPagerFragmentBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = PagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()



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
