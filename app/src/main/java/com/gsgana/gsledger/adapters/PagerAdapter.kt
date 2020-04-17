package com.gsgana.gsledger.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gsgana.gsledger.AdsAndOptionFragment
import com.gsgana.gsledger.ChartFragment
import com.gsgana.gsledger.ListFragment
import com.gsgana.gsledger.StatFragment
import java.lang.IndexOutOfBoundsException

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    companion object {
        const val STAT_PAGE_INDEX = 0
        const val LEDGER_PAGE_INDEX = 1
        const val CHART_PAGE_INDEX = 2 //
        const val ADSANDOPTION_PAGE_INDEX = 3 //

    }

    private val tabFragmentCreator: Map<Int, () -> Fragment> = mapOf(
        STAT_PAGE_INDEX to { StatFragment() },
        LEDGER_PAGE_INDEX to { ListFragment() },
        CHART_PAGE_INDEX to { ChartFragment() },
        ADSANDOPTION_PAGE_INDEX to { AdsAndOptionFragment() }
    )

    override fun getItemCount() = tabFragmentCreator.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentCreator[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

}


