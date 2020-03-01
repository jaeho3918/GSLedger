package com.gsgana.gsledger

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.gsgana.gsledger.data.AppDatabase
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.HomeViewPagerFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlin.collections.HashMap


class HomeViewPagerFragment : Fragment() {
    private lateinit var binding: HomeViewPagerFragmentBinding
    private lateinit var mAuth: FirebaseAuth
    private val REAL_NAME = "sYTVBn2FO8VNT9Ykw90L"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val PREF_NAME = "01504f779d6c77df04"
    private val DATABASE_PATH = "qnI4vK2zSUq6GdeT6b"
    private lateinit var rgl : MutableList<Char>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(DATABASE_PATH).document(mAuth.currentUser?.uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                val test = document.data?.get("Rgl") as ArrayList<String>
                for (s in test) {
                    this.rgl.add(s.toCharArray()[0])
                }
                test.clear()
            }

        val viewModel = activity.run {
            ViewModelProviders.of(
                activity!!,
                InjectorUtils.provideHomeViewPagerViewModelFactory(
                    activity!!,
                    rgl.toCharArray()
                )
            ).get(HomeViewPagerViewModel::class.java)
        }
        rgl.clear()
        val currencyOption =
            activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)?.getInt(CURR_NAME, 0)
        viewModel?.currencyOption?.value = currencyOption

        binding = HomeViewPagerFragmentBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = PagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        viewModel?.products?.value


        val databaseRef = FirebaseDatabase.getInstance().getReference(REAL_NAME)

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val data = p0?.value as HashMap<String, Double>

                if (currencyOption != null) {
                    if (!viewModel?.realData?.value.isNullOrEmpty()) {
                        data["currency"] = viewModel?.realData?.value!!.getValue("currency")
                    } else {
                        data["currency"] = currencyOption.toDouble()
                    }
                }
                viewModel?.realData?.value = data
            }
        }
        )

        viewModel?.realData?.observe(viewLifecycleOwner) {

            var currency = if (it["currency"] == 0.0) {
                1.0
            } else {
                it[CURRENCY[(it["currency"]?.toInt() ?: 0)]]

            }
            binding.realTimeGold.text =
                String.format("%,.2f", (currency?.times((it["AU"])!!.toDouble())))
            binding.realTimeSilver.text =
                String.format(
                    "%,.2f", (currency?.times((it["AG"])!!.toDouble()))
                )

            binding.realCurrency1.text = CURRENCYSYMBOL[(it["currency"]?.toInt() ?: 0)]
            binding.realCurrency2.text = CURRENCYSYMBOL[(it["currency"]?.toInt() ?: 0)]

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
