package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.gms.tasks.Task
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

    private lateinit var functions: FirebaseFunctions// ...

    private val KEY = "Kd6c26TK65YSmkw6oU"

    private val PREF_NAME = "01504f779d6c77df04"
    private lateinit var sf : SharedPreferences
    private val TODAY_NAME = "0d07f05fd0c595f615"


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

        sf = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()
        fm = childFragmentManager
        fm.popBackStack()

        functions = FirebaseFunctions.getInstance()

        binding = ChartFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val today = sf.getString(TODAY_NAME,"")
        binding.todayLabel.text = today
        binding.todayLabel1.text = today

        getChart().addOnCompleteListener { data ->
            val list1 = data.result?.get("value_AU") as ArrayList<Float>
            val list2 = data.result?.get("value_AG") as ArrayList<Float>
            val dates = data.result?.get("date") as ArrayList<String>

            if (context != null) setLineChart(context!!, binding, dates, list1)
            if (context != null) setLineChart1(context!!, binding, dates, list2)
            list1.clear()
            list2.clear()
            dates.clear()
        }

        return binding.root
    }


    private fun setLineChart(
        context: Context,
        binding: ChartFragmentBinding,
        date: ArrayList<String>,
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

        val data = LineData(dataSet)

        val chart = binding.goldChart.apply {
            isEnabled = true
            setData(data)
            setViewPortOffsets(50f, 30f, 50f, 50f)
            setBackgroundColor(backGround)
            isDoubleTapToZoomEnabled = false
            setDrawMarkers(true)
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = false
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
//
//        chart.setVisibleYRange(
//            0f,
//            dataSet.yMax + dataSet.yMax / 13,
//            y.axisDependency
//        )

        val mv =
            CustomMarkerView(context, viewModel, R.layout.marker_view1).apply { chartView = chart }

        chart.marker = mv
        chart.invalidate()
        binding.goldChartLayout.visibility = View.VISIBLE
        binding.goldChartProgress.visibility = View.GONE
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

        val chart = binding.silverChart.apply {
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

//        chart.setVisibleYRange(
//            dataSet.yMax + dataSet.yMax / 130,
//            0f,
//            y.axisDependency
//        )

        val mv =
            CustomMarkerView(context, viewModel, R.layout.marker_view1).apply { chartView = chart }

        chart.marker = mv
        chart.invalidate()
        binding.silverChartLayout.visibility = View.VISIBLE
        binding.silverChartProgress.visibility = View.GONE
    }

    private class CustomMarkerView(
        context: Context,
        private val viewModel: HomeViewPagerViewModel,
        layoutResource: Int
    ) : MarkerView(
        context,
        layoutResource
    ) {
        private val tvContent: TextView
            get() = findViewById<View>(R.id.tvContent) as TextView

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            tvContent.text =
                String.format("%,.2f", e?.y) // set the entry-value as the display text
            tvCurrency.text = CURRENCYSYMBOL[0]
//                CURRENCYSYMBOL[viewModel.realData.value?.get("currency")?.toInt() ?: 0]

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

    private fun getChart(): Task<Map<*, ArrayList<*>>> {

        // Create the arguments to the callable function.

        val data = hashMapOf(
            "date" to listOf<String>(),
            "value_AU" to listOf<Float>(),
            "value_AG" to listOf<Float>()
        )
        return functions
            .getHttpsCallable("getLongChart")
            .call(data)
            .continueWith { task ->
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

}