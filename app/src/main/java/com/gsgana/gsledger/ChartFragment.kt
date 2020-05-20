package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.gsgana.gsledger.adapters.PagerAdapter
import com.gsgana.gsledger.databinding.ChartFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.gold_3m_chart_layout.view.*
import kotlinx.android.synthetic.main.home_view_pager_fragment.*
import kotlinx.android.synthetic.main.marker_view.view.*
import kotlinx.android.synthetic.main.ratio_3m_chart_layout.view.*
import kotlinx.android.synthetic.main.silver_3m_chart_layout.view.*
import kotlin.collections.ArrayList


@Suppress("UNCHECKED_CAST")
class ChartFragment : Fragment(), PurchasesUpdatedListener {
    private lateinit var binding: ChartFragmentBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var rgl: MutableList<Char>

    private lateinit var fm: FragmentManager

    private val NEW_LABEL = "RECSHenWYqdadfXOog"
    private val NEW_ENCRYPT = "X67LWGmYAc3rlCbmPe"
    private val NUMBER = "HYf75f2q2a36enW18b"

//    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    private val KEY = "Kd6c26TK65YSmkw6oU"

    private val PREF_NAME = "01504f779d6c77df04"
    private lateinit var sf: SharedPreferences
    private val TODAY_NAME = "0d07f05fd0c595f615"
    private val ADFREE_NAME = "CQi7aLBQH7dR7qyrCG"

    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var mBuilder: AdRequest.Builder

    private var doneOnce = true
    private val AD_ID = "ca-app-pub-3940256099942544/1033173712"
    // 실제   "ca-app-pub-8453032642509497/3082833180"
//  // 테스트 "ca-app-pub-3940256099942544/1033173712"

    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(
            activity!!,
            activity!!.intent.getCharArrayExtra(KEY)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ChartFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        sf = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

//        if (18 != 18) {
//            setAds()
//        } else {
//            binding.chartProgress.visibility = View.GONE
//        }

        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()
        fm = childFragmentManager
        fm.popBackStack()

        viewModel.getSSShortchart().observe(viewLifecycleOwner, Observer {
            if (context != null) getRatioChartSelect_3m(context!!, viewModel, binding, it)
            if (context != null) getGoldChartSelect_3m(context!!, viewModel, binding, it)
            if (context != null) getSilverChartSelect_3m(context!!, viewModel, binding, it)
        })

        viewModel.getchart().observe(viewLifecycleOwner, Observer {
            if (context != null) getRatioChartSelect_18m(context!!, viewModel, binding, it)
            if (context != null) getGoldChartSelect_18m(context!!, viewModel, binding, it)
            if (context != null) getSilverChartSelect_18m(context!!, viewModel, binding, it)
        })

        viewModel.getLongchart().observe(viewLifecycleOwner, Observer {
            if (context != null) setRatioChartSelect_29y(context!!, binding, it)
            if (context != null) setGoldChartSelect_29y(context!!, binding, it)
            if (context != null) setSilverChartSelect_29y(context!!, binding, it)
        })

        val viewPager = parentFragment?.viewPager

        val test = 1

        binding.goldSubscribe.setOnClickListener {
            viewPager?.doOnLayout {
                viewPager.currentItem = PagerAdapter.ADSANDOPTION_PAGE_INDEX
                viewPager.setCurrentItem(PagerAdapter.ADSANDOPTION_PAGE_INDEX, true)
            }
        }

        binding.silverSubscribe.setOnClickListener {
            viewPager?.doOnLayout {
                viewPager.currentItem = PagerAdapter.ADSANDOPTION_PAGE_INDEX
                viewPager.setCurrentItem(PagerAdapter.ADSANDOPTION_PAGE_INDEX, true)
            }
        }

        binding.ratioSubscribe.setOnClickListener {
            viewPager?.doOnLayout {
                viewPager.currentItem = PagerAdapter.ADSANDOPTION_PAGE_INDEX
                viewPager.setCurrentItem(PagerAdapter.ADSANDOPTION_PAGE_INDEX, true)
            }
        }


        var goldChart_select = 3
        var silverChart_select = 3
        var ratioChart_select = 3

        binding.ratio3mBtn.setOnClickListener {
            binding.ratioLayout.ratio_3m_Chart.visibility = View.VISIBLE
            binding.ratioSubscribe.visibility = View.GONE
            if (ratioChart_select != 3) {
                binding.ratioLayout.ratio_18m_Chart.visibility = View.INVISIBLE
                binding.ratioLayout.ratio_29y_Chart.visibility = View.INVISIBLE
                val firstConstraintSet = ConstraintSet()
                val secondConstraintSet = ConstraintSet()
                val constraintLayout = binding.ratioLayout as ConstraintLayout
                firstConstraintSet.load(context!!, R.layout.ratio_18m_chart_layout)
                secondConstraintSet.load(context!!, R.layout.ratio_3m_chart_layout)
                TransitionManager.beginDelayedTransition(constraintLayout)
                secondConstraintSet.applyTo(constraintLayout)
                ratioChart_select = 3
            }
        }

        binding.gold3mBtn.setOnClickListener {
            binding.goldSubscribe.visibility = View.GONE
            binding.goldLayout.gold_3m_Chart.visibility = View.VISIBLE
            if (goldChart_select != 3) {
                binding.goldLayout.gold_18m_Chart.visibility = View.INVISIBLE
                binding.goldLayout.gold_29y_Chart.visibility = View.INVISIBLE
                val firstConstraintSet = ConstraintSet()
                val secondConstraintSet = ConstraintSet()
                val constraintLayout = binding.goldLayout as ConstraintLayout
                firstConstraintSet.load(context!!, R.layout.gold_18m_chart_layout)
                secondConstraintSet.load(context!!, R.layout.gold_3m_chart_layout)
                TransitionManager.beginDelayedTransition(constraintLayout)
                secondConstraintSet.applyTo(constraintLayout)
                goldChart_select = 3
            }
        }

        binding.silver3mBtn.setOnClickListener {
            binding.silverSubscribe.visibility = View.GONE
            binding.silverLayout.silver_3m_Chart.visibility = View.VISIBLE
            if (silverChart_select != 3) {
                binding.silverLayout.silver_18m_Chart.visibility = View.INVISIBLE
                binding.silverLayout.silver_29y_Chart.visibility = View.INVISIBLE
                val firstConstraintSet = ConstraintSet()
                val secondConstraintSet = ConstraintSet()
                val constraintLayout = binding.silverLayout as ConstraintLayout
                firstConstraintSet.load(context!!, R.layout.silver_18m_chart_layout)
                secondConstraintSet.load(context!!, R.layout.silver_3m_chart_layout)
                TransitionManager.beginDelayedTransition(constraintLayout)
                secondConstraintSet.applyTo(constraintLayout)
                silverChart_select = 3
            }
        }



        binding.ratio18mBtn.setOnClickListener {
            if (ratioChart_select != 6) {
                if (18 == 18) {
                    binding.ratioSubscribe.visibility = View.GONE
                    binding.ratioLayout.ratio_3m_Chart.visibility = View.INVISIBLE
                    binding.ratioLayout.ratio_18m_Chart.visibility = View.VISIBLE
                    binding.ratioLayout.ratio_29y_Chart.visibility = View.INVISIBLE
                    val firstConstraintSet = ConstraintSet()
                    val secondConstraintSet = ConstraintSet()
                    val constraintLayout = binding.ratioLayout as ConstraintLayout
                    firstConstraintSet.load(context!!, R.layout.ratio_3m_chart_layout)
                    secondConstraintSet.load(context!!, R.layout.ratio_18m_chart_layout)
                    TransitionManager.beginDelayedTransition(constraintLayout)
                    secondConstraintSet.applyTo(constraintLayout)
                    ratioChart_select = 6
                } else {
                    binding.ratioLayout.ratio_3m_Chart.visibility = View.INVISIBLE
                    binding.ratioSubscribe.visibility = View.VISIBLE
                }
            }
        }

        binding.gold18mBtn.setOnClickListener {
            if (goldChart_select != 6) {
                if (18 == 18) {
                    binding.goldSubscribe.visibility = View.GONE
                    binding.goldLayout.gold_3m_Chart.visibility = View.INVISIBLE
                    binding.goldLayout.gold_18m_Chart.visibility = View.VISIBLE
                    binding.goldLayout.gold_29y_Chart.visibility = View.INVISIBLE
                    val firstConstraintSet = ConstraintSet()
                    val secondConstraintSet = ConstraintSet()
                    val constraintLayout = binding.goldLayout as ConstraintLayout
                    firstConstraintSet.load(context!!, R.layout.gold_3m_chart_layout)
                    secondConstraintSet.load(context!!, R.layout.gold_18m_chart_layout)
                    TransitionManager.beginDelayedTransition(constraintLayout)
                    secondConstraintSet.applyTo(constraintLayout)
                    goldChart_select = 6
                } else {
                    binding.goldLayout.gold_3m_Chart.visibility = View.INVISIBLE
                    binding.goldSubscribe.visibility = View.VISIBLE
                }
            }
        }

        binding.silver18mBtn.setOnClickListener {
            if (silverChart_select != 6) {
                if (18 == 18) {
                    binding.silverSubscribe.visibility = View.GONE
                    binding.silverLayout.silver_3m_Chart.visibility = View.INVISIBLE
                    binding.silverLayout.silver_18m_Chart.visibility = View.VISIBLE
                    binding.silverLayout.silver_29y_Chart.visibility = View.INVISIBLE
                    val firstConstraintSet = ConstraintSet()
                    val secondConstraintSet = ConstraintSet()
                    val constraintLayout = binding.silverLayout as ConstraintLayout
                    firstConstraintSet.load(context!!, R.layout.silver_3m_chart_layout)
                    secondConstraintSet.load(context!!, R.layout.silver_18m_chart_layout)
                    TransitionManager.beginDelayedTransition(constraintLayout)
                    secondConstraintSet.applyTo(constraintLayout)
                    silverChart_select = 6
                } else {
                    binding.silverLayout.silver_3m_Chart.visibility = View.INVISIBLE
                    binding.silverSubscribe.visibility = View.VISIBLE
                }
            }
        }

        binding.ratio29yBtn.setOnClickListener {
            if (ratioChart_select != 29) {
                if (18 == 18) {
                    binding.ratioSubscribe.visibility = View.GONE
                    binding.ratioLayout.ratio_3m_Chart.visibility = View.INVISIBLE
                    binding.ratioLayout.ratio_18m_Chart.visibility = View.INVISIBLE
                    binding.ratioLayout.ratio_29y_Chart.visibility = View.VISIBLE
                    val firstConstraintSet = ConstraintSet()
                    val secondConstraintSet = ConstraintSet()
                    val constraintLayout = binding.ratioLayout as ConstraintLayout
                    firstConstraintSet.load(context!!, R.layout.ratio_18m_chart_layout)
                    secondConstraintSet.load(context!!, R.layout.ratio_29y_chart_layout)
                    TransitionManager.beginDelayedTransition(constraintLayout)
                    secondConstraintSet.applyTo(constraintLayout)
                    ratioChart_select = 29
                } else {
                    binding.ratioLayout.ratio_3m_Chart.visibility = View.INVISIBLE
                    binding.ratioSubscribe.visibility = View.VISIBLE
                }
            }
        }

        binding.gold29yBtn.setOnClickListener {
            if (goldChart_select != 29) {
                if (18 == 18) {
                    binding.goldSubscribe.visibility = View.GONE
                    binding.goldLayout.gold_3m_Chart.visibility = View.INVISIBLE
                    binding.goldLayout.gold_18m_Chart.visibility = View.INVISIBLE
                    binding.goldLayout.gold_29y_Chart.visibility = View.VISIBLE
                    val firstConstraintSet = ConstraintSet()
                    val secondConstraintSet = ConstraintSet()
                    val constraintLayout = binding.goldLayout as ConstraintLayout
                    firstConstraintSet.load(context!!, R.layout.gold_18m_chart_layout)
                    secondConstraintSet.load(context!!, R.layout.gold_29y_chart_layout)
                    TransitionManager.beginDelayedTransition(constraintLayout)
                    secondConstraintSet.applyTo(constraintLayout)
                    goldChart_select = 29
                } else {
                    binding.goldLayout.gold_3m_Chart.visibility = View.INVISIBLE
                    binding.goldSubscribe.visibility = View.VISIBLE
                }
            }
        }

        binding.silver29yBtn.setOnClickListener {
            if (silverChart_select != 29) {
                if (18 == 18) {
                    binding.silverSubscribe.visibility = View.GONE
                    binding.silverLayout.silver_3m_Chart.visibility = View.INVISIBLE
                    binding.silverLayout.silver_18m_Chart.visibility = View.INVISIBLE
                    binding.silverLayout.silver_29y_Chart.visibility = View.VISIBLE
                    binding.chartScroll.isEnabled = false
                    val firstConstraintSet = ConstraintSet()
                    val secondConstraintSet = ConstraintSet()
                    val constraintLayout = binding.silverLayout as ConstraintLayout
                    firstConstraintSet.load(context!!, R.layout.silver_18m_chart_layout)
                    secondConstraintSet.load(context!!, R.layout.silver_29y_chart_layout)
                    TransitionManager.beginDelayedTransition(constraintLayout)
                    secondConstraintSet.applyTo(constraintLayout)
                    silverChart_select = 29
                } else {
                    binding.silverLayout.silver_3m_Chart.visibility = View.INVISIBLE
                    binding.silverSubscribe.visibility = View.VISIBLE
                }
            }
        }



        return binding.root
    }

    override fun onDestroy() {
        viewModel.getChartDate().clear()
        super.onDestroy()
    }

    private fun setRatioChartSelect_29y(
        context: Context,
        binding: ChartFragmentBinding,
        data: Map<String, ArrayList<*>>
    ) {

        val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
        val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
        val backGround = context.resources.getColor(R.color.white, null)

        val entries = arrayListOf<Entry>()

        data.getValue("value_RATIO").forEachIndexed { index, fl ->
            entries.add(
                Entry(
                    index.toFloat(),
                    fl.toString().toFloat()
                )
            )
        }

        val dataSet = LineDataSet(entries, "")
            .apply {
                setDrawFilled(false)
                setDrawValues(false)
                fillColor = chart_goldC
                lineWidth = 2f
                color = chart_goldC
                setCircleColor(chart_goldB)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                isHighlightEnabled = true
                setDrawCircles(false)
            }

        val date = data.getValue("date").toList() as List<String>

        val data = LineData(dataSet)

        val chart = binding.ratioLayout.ratio_29y_Chart.apply {
            isEnabled = true
            setData(data)
//            setViewPortOffsets(50f, 30f, 50f, 50f)
            setBackgroundColor(backGround)
            isDoubleTapToZoomEnabled = false
            setDrawMarkers(true)
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setDrawGridBackground(false)
            maxHighlightDistance = 300f
            setPinchZoom(false)
            setDrawMarkers(true)
            isHighlightPerTapEnabled = true
            axisRight.isEnabled = false
            legend.isEnabled = false
            fitScreen()
        }

        val valueFormatter = IndexAxisValueFormatter(date)
//        val valueFormatter = ChartAxisValueFormatter().apply {
//            setValue(date)
//        }
//        val valueFormatter = object : IndexAxisValueFormatter(){
//            val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
//            val cal = Calendar.getInstance()
//
//            override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
//                cal.timeInMillis = date[value.toInt()] * 1000L
//                return simpleDateFormat.format(cal).toString()
//            }
//        }

        chart.xAxis
            .apply {
                setValueFormatter(valueFormatter)
                isEnabled = true
                gridColor = resources.getColor(R.color.chart_goldB, null)
                textColor = context.resources.getColor(R.color.chart_font, null)
                position = XAxis.XAxisPosition.BOTTOM

                setLabelCount(6, false)
                setDrawGridLines(false)
                textSize = 5F

            }
//////////////////////////////////////////
        chart.axisLeft
            .apply {
                textColor = resources.getColor(R.color.chart_font, null)
                gridColor = resources.getColor(R.color.chart_goldB, null)
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
                setDrawGridLines(false)
                setLabelCount(5, true)
                axisMaximum = dataSet.yMax * 25 / 24
                axisMinimum = dataSet.yMin * 23 / 24
                axisLineColor = backGround
            }

        val mv =
            CustomMarkerRatioView(context, R.layout.marker_view, date).apply {
                chartView = chart
            }

        chart.marker = mv
        chart.invalidate()

        binding.ratioLayout.ratio_29y_Chart.visibility = View.VISIBLE
        binding.ratioLongChartVisibleLayout.visibility = View.VISIBLE
        binding.ratioLongChartProgress.visibility = View.GONE
    }

    private fun setGoldChartSelect_29y(
        context: Context,
        binding: ChartFragmentBinding,
        data: Map<String, ArrayList<*>>
    ) {

        val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
        val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
        val backGround = context.resources.getColor(R.color.white, null)

        val entries = arrayListOf<Entry>()

        data.getValue("value_AU").forEachIndexed { index, fl ->
            entries.add(
                Entry(
                    index.toFloat(),
                    fl.toString().toFloat()
                )
            )
        }

        val dataSet = LineDataSet(entries, "")
            .apply {
                setDrawFilled(true)
                setDrawValues(false)
                fillColor = chart_goldC
                color = chart_goldC
                setCircleColor(chart_goldB)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                isHighlightEnabled = true
                setDrawCircles(false)
            }

        val date = data.getValue("date").toList() as List<String>

        val data = LineData(dataSet)

        val chart = binding.goldLayout.gold_29y_Chart.apply {
            isEnabled = true
            setData(data)
//            setViewPortOffsets(50f, 30f, 50f, 50f)
            setBackgroundColor(backGround)
            isDoubleTapToZoomEnabled = false
            setDrawMarkers(true)
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setDrawGridBackground(false)
            maxHighlightDistance = 300f
            setPinchZoom(false)
            setDrawMarkers(true)
            isHighlightPerTapEnabled = true
            axisRight.isEnabled = false
            legend.isEnabled = false
            fitScreen()
        }
        val valueFormatter = IndexAxisValueFormatter(date)

        chart.xAxis
            .apply {
                setValueFormatter(valueFormatter)
                isEnabled = true
                gridColor = resources.getColor(R.color.chart_goldB, null)
                textColor = context.resources.getColor(R.color.chart_font, null)
                position = XAxis.XAxisPosition.BOTTOM

                setLabelCount(6, false)
                setDrawGridLines(false)
                textSize = 5F

            }

        chart.axisLeft
            .apply {
                textColor = resources.getColor(R.color.chart_font, null)
                gridColor = resources.getColor(R.color.chart_goldB, null)
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
                setDrawGridLines(false)

                setLabelCount(5, true)
                axisMaximum = dataSet.yMax * 25 / 24
                axisMinimum = dataSet.yMin * 23 / 24
                axisLineColor = backGround
            }

        val mv =
            LongMarkerView(context, "$", R.layout.marker_view, date).apply {
                chartView = chart
            }

        chart.marker = mv
        chart.invalidate()
        binding.goldLayout.gold_29y_Chart.visibility = View.VISIBLE
        binding.goldLongChartVisibleLayout.visibility = View.VISIBLE
        binding.goldLongChartProgress.visibility = View.GONE
    }

    private fun setSilverChartSelect_29y(
        context: Context,
        binding: ChartFragmentBinding,
        data: Map<String, ArrayList<*>>
    ) {
        val chart_silverC = context.resources.getColor(R.color.chart_silverC, null)
        val chart_silverB = context.resources.getColor(R.color.chart_silverB, null)
        val backGround = context.resources.getColor(R.color.white, null)
        val entries = arrayListOf<Entry>()

        data.getValue("value_AG").forEachIndexed { index, fl ->
            entries.add(
                Entry(
                    index.toFloat(),
                    fl.toString().toFloat()
                )
            )
        }

        val dataSet = LineDataSet(entries, "")
            .apply {
                setDrawFilled(true)
                setDrawValues(false)
                fillColor = chart_silverC
                color = chart_silverC
                setCircleColor(chart_silverB)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                isHighlightEnabled = true
                setDrawCircles(false)
            }


        val date = data.getValue("date").toList() as List<String>

        val data = LineData(dataSet)

        val chart = binding.silverLayout.silver_29y_Chart.apply {
            isEnabled = true
            setData(data)
//            setViewPortOffsets(50f, 30f, 50f, 50f)
            setBackgroundColor(backGround)
            isDoubleTapToZoomEnabled = false
            setDrawMarkers(true)
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setDrawGridBackground(false)
            maxHighlightDistance = 300f
            setPinchZoom(false)
            setDrawMarkers(true)
            isHighlightPerTapEnabled = true
            axisRight.isEnabled = false
            legend.isEnabled = false
            fitScreen()
        }


        val valueFormatter = IndexAxisValueFormatter(date)


        chart.xAxis
            .apply {
                setValueFormatter(valueFormatter)
                isEnabled = true
                gridColor = resources.getColor(R.color.chart_silverB, null)
                textColor = context.resources.getColor(R.color.chart_font, null)
                position = XAxis.XAxisPosition.BOTTOM

                setLabelCount(6, false)
                setDrawGridLines(false)
                textSize = 5F

            }

        chart.axisLeft
            .apply {
                textColor = resources.getColor(R.color.chart_font, null)
                gridColor = resources.getColor(R.color.chart_silverB, null)
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
                setDrawGridLines(false)
                setLabelCount(5, true)
                axisMaximum = dataSet.yMax * 25 / 24
                axisMinimum = dataSet.yMin * 23 / 24
                axisLineColor = backGround
            }

        val mv =
            LongMarkerView(context, "$", R.layout.marker_view, date).apply {
                chartView = chart
            }

        chart.marker = mv
        chart.invalidate()
        binding.silverLayout.silver_29y_Chart.visibility = View.VISIBLE
        binding.silverLongChartVisibleLayout.visibility = View.VISIBLE
        binding.silverLongChartProgress.visibility = View.GONE
    }

    private class CustomMarkerRatioView(
        context: Context,
        layoutResource: Int,
        private val date: List<String>
    ) : MarkerView(
        context,
        layoutResource
    ) {

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            if ((e?.x ?: 0f).toInt() <= date.size - 1) {
                tvDate.text = date[(e?.x ?: 0f).toInt()]
                dateLayout.visibility = View.VISIBLE
            } else
                dateLayout.visibility = View.GONE

            tvContent.text =
                String.format("%,.2f", e?.y) // set the entry-value as the display text

            super.refreshContent(e, highlight)
        }

        override fun draw(
            canvas: Canvas?,
            posX: Float,
            posY: Float
        ) {
            super.draw(canvas, posX, posY)
            getOffsetForDrawingAtPoint(posX, posY)
        }

        override fun getOffset(): MPPointF {
            super.getOffset().x = -(width / 2).toFloat()
            super.getOffset().y = -(height.toFloat() + 18f)
            return super.getOffset()
        }
    }

    private class LongMarkerView(
        context: Context,
        private val currencySymbol: String,
        layoutResource: Int,
        private val date: List<String>
    ) : MarkerView(
        context,
        layoutResource
    ) {

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            if ((e?.x ?: 0f).toInt() <= date.size - 1) {
                tvDate.text = date[(e?.x ?: 0f).toInt()]
                dateLayout.visibility = View.VISIBLE
            } else
                dateLayout.visibility = View.GONE

            tvContent.text =
                String.format("%,.2f", e?.y) // set the entry-value as the display text
            tvCurrency.text = currencySymbol

            super.refreshContent(e, highlight)
        }

        override fun draw(
            canvas: Canvas?,
            posX: Float,
            posY: Float
        ) {
            super.draw(canvas, posX, posY)
            getOffsetForDrawingAtPoint(posX, posY)
        }

        override fun getOffset(): MPPointF {
            super.getOffset().x = -(width / 2).toFloat()
            super.getOffset().y = -(height.toFloat() + 18f)
            return super.getOffset()
        }
    }

    override fun onPurchasesUpdated(
        billingresult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        if (billingresult?.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.let {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        sf.edit().putInt(ADFREE_NAME, 18).apply()
                        Toast.makeText(
                            context,
                            resources.getString(R.string.adfreerestart),
                            Toast.LENGTH_LONG
                        ).show()
//                    Toast.makeText(context!!,"Baaaaaaaaaaaaad", Toast.LENGTH_LONG) .show()
                    } else {
                        sf.edit().putInt(ADFREE_NAME, 6).apply()
                    }
                }
            }
        }
    }

}
