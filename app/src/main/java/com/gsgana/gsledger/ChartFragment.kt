package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.gsgana.gsledger.databinding.ChartFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.chart_fragment.view.*
import kotlinx.android.synthetic.main.gold_29y_chart_layout.*
import kotlinx.android.synthetic.main.gold_29y_chart_layout.view.*
import kotlinx.android.synthetic.main.gold_29y_chart_layout.view.gold_29y_ChartLayout
import kotlinx.android.synthetic.main.marker_view.view.*
import kotlinx.android.synthetic.main.ratio_29y_chart_layout.*
import kotlinx.android.synthetic.main.ratio_29y_chart_layout.view.*
import kotlinx.android.synthetic.main.ratio_29y_chart_layout.view.ratio_29y_ChartLayout
import kotlinx.android.synthetic.main.silver_29y_chart_layout.*
import kotlinx.android.synthetic.main.silver_29y_chart_layout.view.*
import kotlinx.android.synthetic.main.silver_29y_chart_layout.view.silver_29y_ChartLayout
import kotlin.collections.ArrayList


@Suppress("UNCHECKED_CAST")
class ChartFragment : Fragment() {
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
    private val AD_ID = "ca-app-pub-3940256099942544/8691691433"
    // 실제   "ca-app-pub-8453032642509497/3082833180"
//  // 테스트 "ca-app-pub-3940256099942544/8691691433"

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

//        if (sf.getInt(ADFREE_NAME, 6) != 18) {
//            setAds()
//        } else {
//            binding.chartProgress.visibility = View.GONE
//        }

        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()
        fm = childFragmentManager
        fm.popBackStack()

        val today = sf.getString(TODAY_NAME, "")
        binding.todayLabel.text = today?.substring(0..10)
        binding.todayLabel1.text = today?.substring(0..10)
        binding.todayLabel2.text = today?.substring(0..10)


        viewModel.getLongchart().observe(viewLifecycleOwner, Observer {
            if (context != null) getRatioChartSelect6m(context!!, viewModel, binding, it)
            if (context != null) getGoldChartSelect6m(context!!, viewModel, binding, it)
            if (context != null) getSilverChartSelect6m(context!!, viewModel, binding, it)
        })

        var goldChart_select = 0
        var silverChart_select = 0
        var ratioChart_select = 0

        binding.gold6mBtn.setOnClickListener {
            if (goldChart_select != 6) {
                getGoldChartSelect6m(context!!, viewModel, binding, viewModel.getchartData())
                goldChart_select = 6
            }
        }

        binding.silver6mBtn.setOnClickListener {
            if (silverChart_select != 6) {
                getSilverChartSelect6m(context!!, viewModel, binding, viewModel.getchartData())
                silverChart_select = 6
            }
        }

        binding.ratio6mBtn.setOnClickListener {
            if (ratioChart_select != 6) {
                getRatioChartSelect6m(context!!, viewModel, binding, viewModel.getchartData())
                ratioChart_select = 6
            }
        }

        binding.gold29yBtn.setOnClickListener {
            if (goldChart_select != 29) {
                if (context != null)setGold_29y_Chart(context!!, binding, viewModel.getchartData())
                goldChart_select = 29
            }
        }

        binding.silver29yBtn.setOnClickListener {
            if (silverChart_select != 29) {
                if (context != null) setSilver_29y_Chart(
                    context!!,
                    binding,
                    viewModel.getchartData()
                )
                silverChart_select = 29
            }
        }

        binding.ratio29yBtn.setOnClickListener {
            if (ratioChart_select != 29) {
                if (context != null) setRatio_29y_Chart(context!!, binding, viewModel.getchartData())
                ratioChart_select = 29
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        viewModel.getChartDate().clear()
        super.onDestroy()
    }

    private fun setRatio_29y_Chart(
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
                notifyDataSetChanged()
            }

        val date = data.getValue("date").toList() as List<String>

        val data = LineData(dataSet)

        data.notifyDataChanged()

        val chart = ratio_29y_ChartLayout.ratio_29y_Chart.apply {
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
            notifyDataSetChanged()
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
                setLabelCount(5, false)
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

                setLabelCount(5, false)
                axisMaximum = dataSet.yMax * 15 / 13
                axisMinimum = 0f
                axisLineColor = backGround
            }

        val mv =
            CustomMarkerRatioView(context, R.layout.marker_view, date).apply {
                chartView = chart
            }

        chart.marker = mv
        chart.invalidate()
        ratio_29y_ChartLayout.ratio_29y_Chart.visibility = View.VISIBLE
        binding.ratioLongChartVisibleLayout.visibility = View.VISIBLE
        binding.ratioLongChartProgress.visibility = View.GONE
    }

    private fun setGold_29y_Chart(
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
                notifyDataSetChanged()
            }

        val date = data.getValue("date").toList() as List<String>

        val data = LineData(dataSet)
        data.notifyDataChanged()


        val chart = gold_29y_ChartLayout.gold_29y_Chart.apply {
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
            notifyDataSetChanged()
        }
        val valueFormatter = IndexAxisValueFormatter(date)

        chart.xAxis
            .apply {
                setValueFormatter(valueFormatter)
                isEnabled = true
                gridColor = resources.getColor(R.color.chart_goldB, null)
                textColor = context.resources.getColor(R.color.chart_font, null)
                position = XAxis.XAxisPosition.BOTTOM
                setLabelCount(5, false)
                setDrawGridLines(false)
                textSize = 5F

            }

        chart.axisLeft
            .apply {
                textColor = resources.getColor(R.color.chart_font, null)
                gridColor = resources.getColor(R.color.chart_goldB, null)
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
                setDrawGridLines(false)

                setLabelCount(5, false)
                axisMaximum = dataSet.yMax * 15 / 13
                axisMinimum = 0f
                axisLineColor = backGround
            }

        val mv =
            LongMarkerView(context, "$", R.layout.marker_view, date).apply {
                chartView = chart
            }

        chart.marker = mv
        chart.invalidate()
        gold_29y_ChartLayout.gold_29y_Chart.visibility = View.VISIBLE
        binding.goldLongChartVisibleLayout.visibility = View.VISIBLE
        binding.goldLongChartProgress.visibility = View.GONE
    }

    private fun setSilver_29y_Chart(
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
                notifyDataSetChanged()
            }


        val date = data.getValue("date").toList() as List<String>

        val data = LineData(dataSet)

        data.notifyDataChanged()

        val chart = silver_29y_ChartLayout.silver_29y_Chart.apply {
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
            notifyDataSetChanged()
        }


        val valueFormatter = IndexAxisValueFormatter(date)


        chart.xAxis
            .apply {
                setValueFormatter(valueFormatter)
                isEnabled = true
                gridColor = resources.getColor(R.color.chart_silverB, null)
                textColor = context.resources.getColor(R.color.chart_font, null)
                position = XAxis.XAxisPosition.BOTTOM
                setLabelCount(5, false)
                setDrawGridLines(false)
                textSize = 5F

            }

        chart.axisLeft
            .apply {
                textColor = resources.getColor(R.color.chart_font, null)
                gridColor = resources.getColor(R.color.chart_silverB, null)
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
                setDrawGridLines(false)
                setLabelCount(5, false)
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
        silver_29y_ChartLayout.silver_29y_Chart.visibility = View.VISIBLE
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


    private fun setAds() {
        MobileAds.initialize(context)
        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = AD_ID
        mBuilder = AdRequest.Builder()
        mInterstitialAd.loadAd(mBuilder.build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                if (mInterstitialAd.isLoaded) {
                    if (doneOnce) {
                        mInterstitialAd.show()
                        doneOnce = false
                    }
                }
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                Toast.makeText(context!!, p0.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                binding.chartProgress.visibility = View.GONE
            }
        }
    }
}
