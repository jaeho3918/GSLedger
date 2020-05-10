package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import com.gsgana.gsledger.utilities.CURRENCYSYMBOL
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.marker_view.view.*
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

    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()

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

        if (sf.getInt(ADFREE_NAME, 6) != 18) {
            setAds()
        } else {
            binding.chartProgress.visibility = View.GONE
        }

        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()
        fm = childFragmentManager
        fm.popBackStack()

        val today = sf.getString(TODAY_NAME, "")
        binding.todayLabel.text = today
        binding.todayLabel1.text = today

        getChart(
            sf.getString(NEW_LABEL, "")!!,
            sf.getString(NEW_ENCRYPT, "")!!,
            sf.getInt(NUMBER, 0)
        ).addOnSuccessListener { data ->
            val list1 = data.get("value_AU") as ArrayList<Float>
            val list2 = data.get("value_AG") as ArrayList<Float>
            val dates = data.get("date") as ArrayList<String>

            if (context != null) setLineChart(context!!, binding, dates, list1)
            if (context != null) setLineChart1(context!!, binding, dates, list2)
            viewModel.setChartDate(dates)
            list1.clear()
            list2.clear()
        }

        binding.chartBackButton.setOnClickListener {
            findNavController()
                .navigate(R.id.action_chartFragment_to_homeViewPagerFragment)
        }



        return binding.root
    }

    override fun onDestroy() {
        viewModel.getChartDate().clear()
        super.onDestroy()
    }

    private fun setLineChart(
        context: Context,
        binding: ChartFragmentBinding,
        date_input: ArrayList<String>,
        value: List<Float>
    ) {

        val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
        val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
        val backGround = context.resources.getColor(R.color.border_background, null)

        val entries = arrayListOf<Entry>()

        value.forEachIndexed { index, fl -> entries.add(Entry(index.toFloat(), fl)) }

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
        val date = date_input

        val data = LineData(dataSet)

        val chart = binding.goldLongChart.apply {
            isEnabled = true
            setData(data)
            setViewPortOffsets(50f, 30f, 50f, 50f)
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
                setLabelCount(5, false)
                setDrawGridLines(false)
                textSize = 5F

            }

        chart.axisLeft
            .apply {
                textColor = resources.getColor(R.color.chart_font, null)
                gridColor = resources.getColor(R.color.chart_goldB, null)
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
                setDrawGridLines(true)
                setLabelCount(5, true)
                axisMaximum = dataSet.yMax * 15 / 13
                axisMinimum = 0f
                axisLineColor = backGround
            }

        val mv =
            CustomMarkerView(context, "$", R.layout.marker_view, viewModel).apply {
                chartView = chart
            }

        chart.marker = mv
        chart.invalidate()
        binding.goldLongChartLayout.visibility = View.VISIBLE
        binding.goldLongChartProgress.visibility = View.GONE
    }

    private fun setLineChart1(
        context: Context,
        binding: ChartFragmentBinding,
        date: ArrayList<String>,
        value: List<Float>
    ) {
        val chart_silverC = context.resources.getColor(R.color.chart_silverC, null)
        val chart_silverB = context.resources.getColor(R.color.chart_silverB, null)
        val backGround = context.resources.getColor(R.color.border_background, null)
        val entries = arrayListOf<Entry>()

        value.forEachIndexed { index, fl -> entries.add(Entry(index.toFloat(), fl)) }

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

        val data = LineData(dataSet)

        val chart = binding.silverLongChart.apply {
            isEnabled = true
            setData(data)
            setViewPortOffsets(50f, 30f, 50f, 50f)
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
                setLabelCount(5, false)
                setDrawGridLines(true)
                textSize = 5F

            }

        chart.axisLeft
            .apply {
                textColor = resources.getColor(R.color.chart_font, null)
                gridColor = resources.getColor(R.color.chart_silverB, null)
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
                setDrawGridLines(true)
                setLabelCount(5, true)
                axisMaximum = dataSet.yMax * 15 / 13
                axisMinimum = 0f
                axisLineColor = backGround
            }

        val mv =
            CustomMarkerView(context, "$", R.layout.marker_view, viewModel).apply {
                chartView = chart
            }

        chart.marker = mv
        chart.invalidate()
        binding.silverLongChartLayout.visibility = View.VISIBLE
        binding.silverLongChartProgress.visibility = View.GONE
    }

    private class CustomMarkerView(
        context: Context,
        private val currencySymbol: String,
        layoutResource: Int,
        private val viewModel: HomeViewPagerViewModel
    ) : MarkerView(
        context,
        layoutResource
    ) {

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            if ((e?.x ?: 0f).toInt() <= viewModel.getChartDate().size - 1) {
                tvDate.text = viewModel.getChartDate()[(e?.x ?: 0f).toInt()]
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

    private fun getChart(label: String, reg: String, num: Int): Task<Map<*, ArrayList<*>>> {

        // Create the arguments to the callable function.

        val data = hashMapOf(
            "label" to label,
            "reg" to reg,
            "number" to num
        )

        return functions
            .getHttpsCallable("L3Vi6HftOI0HK6VH6rHsB6At")
            .call(data)
            .continueWith {task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.

                val result = task.result?.data as Map<*, ArrayList<*>>
                result
            }
    }

    //    private class ChartAxisValueFormatter : ValueFormatter() {
//
//        private lateinit var mValues: ArrayList<String>
//
//        fun setValue(mValues: ArrayList<String>) {
//            this.mValues = mValues
//        }
//
//        override fun getFormattedValue(value: Float): String {
//            if (mValues.size != 0) {
//                return if (value <= (mValues.size - 1)) {
//                    mValues[value.toInt()]
//                } else {
//                    mValues[mValues.size - 1]
//                }
//            }
//            return ""
//        }
//    }
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
                Toast.makeText(context!!, p0.toString(),Toast.LENGTH_SHORT).show()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                binding.chartProgress.visibility = View.GONE
            }
        }
    }
}