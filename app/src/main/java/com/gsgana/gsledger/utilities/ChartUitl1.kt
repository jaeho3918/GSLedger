package com.gsgana.gsledger.utilities

import android.content.Context
import android.graphics.Canvas
import android.view.View
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.gsgana.gsledger.R
import com.gsgana.gsledger.databinding.ChartFragmentBinding
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.gold_3m_chart_layout.view.*
import kotlinx.android.synthetic.main.marker_view.view.*
import kotlinx.android.synthetic.main.ratio_3m_chart_layout.view.*
import kotlinx.android.synthetic.main.silver_3m_chart_layout.view.*


private fun setRatioChartSelect_3m(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: ChartFragmentBinding,
    data: Map<String, List<*>>
) {

    val chart_font = context.resources.getColor(R.color.chart_font, null)
    val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
    val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
    val backGround = context.resources.getColor(R.color.white, null)

    val currencySymbol: String

    val result = arrayListOf<Entry>()
    data.getValue("value_RATIO").forEachIndexed { index, fl ->
        result.add(
            Entry(
                index.toFloat(),
                fl.toString().toFloat()
            )
        )
    }

    val date = data.getValue("date") as ArrayList<String>
    val dataSet = LineDataSet(result, "")
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

    val data_set = LineData(dataSet)

    val chart = binding.ratioLayout.ratio_3m_Chart.apply {
        isEnabled = true
        setData(data_set)
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
            gridColor = chart_goldB
            textColor = chart_font
            position = XAxis.XAxisPosition.BOTTOM

            setLabelCount(5, false)
            setDrawGridLines(false)
            textSize = 5F
        }

    chart.axisLeft
        .apply {
            textColor = chart_font
            gridColor = chart_goldB
            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            isEnabled = true
            setDrawGridLines(false)
            setLabelCount(5, false)
            axisMaximum = dataSet.yMax * 25 / 24
            axisMinimum = dataSet.yMin * 23 / 24
            axisLineColor = backGround
        }

    val mv =
        RatioMarkerView1(context, R.layout.marker_view, date)
            .apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()

    binding.ratioLayout.ratio_3m_Chart.visibility = View.VISIBLE
    binding.ratioLongChartVisibleLayout.visibility = View.VISIBLE
    binding.ratioLongChartProgress.visibility = View.GONE
}

fun getRatioChartSelect_3m(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: ChartFragmentBinding,
    data: Map<String, List<*>>
) {
    setRatioChartSelect_3m(context, viewModel, binding, data)
}

private fun setGoldChartSelect_3m(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: ChartFragmentBinding,
    data: Map<String, List<*>>
) {

    val chart_font = context.resources.getColor(R.color.chart_font, null)
    val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
    val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
    val backGround = context.resources.getColor(R.color.white, null)

    val result = arrayListOf<Entry>()
    data.getValue("value_AU").forEachIndexed { index, fl ->
        result.add(
            Entry(
                index.toFloat(),
                fl.toString().toFloat()
            )
        )
    }

    val date = data.getValue("date") as ArrayList<String>
    val dataSet = LineDataSet(result, "")
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

    val data_set = LineData(dataSet)

    val chart = binding.goldLayout.gold_3m_Chart.apply {
        isEnabled = true
        setData(data_set)
//        setViewPortOffsets(50f, 30f, 50f, 80f)
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
            gridColor = chart_goldB
            textColor = chart_font
            position = XAxis.XAxisPosition.BOTTOM

            setLabelCount(5, false)
            setDrawGridLines(false)
            textSize = 5F
        }

    chart.axisLeft
        .apply {
            textColor = chart_font
            gridColor = chart_goldB
            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            isEnabled = true
            setDrawGridLines(false)
            setLabelCount(5, false)        //none
            axisMaximum = dataSet.yMax * 25 / 24
            axisMinimum = dataSet.yMin * 23 / 24
            axisLineColor = backGround
        }

    val mv =
        LongMarkerView1(context, "$", R.layout.marker_view, date)
            .apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()
    binding.goldLayout.gold_3m_Chart.visibility = View.VISIBLE
    binding.goldLongChartVisibleLayout.visibility = View.VISIBLE
    binding.goldLongChartProgress.visibility = View.GONE
}

fun getGoldChartSelect_3m(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: ChartFragmentBinding,
    data: Map<String, List<*>>
) {
    setGoldChartSelect_3m(context, viewModel, binding, data)
}

private fun setSilverChartSelect_3m(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: ChartFragmentBinding,
    data: Map<String, List<*>>
) {
    val chart_font = context.resources.getColor(R.color.chart_font, null)
    val chart_silverC = context.resources.getColor(R.color.chart_silverC, null)
    val chart_silverB = context.resources.getColor(R.color.chart_silverB, null)
    val backGround = context.resources.getColor(R.color.white, null)

    val result = arrayListOf<Entry>()
    data.getValue("value_AG").forEachIndexed { index, fl ->
        result.add(
            Entry(
                index.toFloat(),
                fl.toString().toFloat()
            )
        )
    }

    val date = data.getValue("date") as ArrayList<String>

    val dataSet = LineDataSet(result, "")
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

    val data_set = LineData(dataSet)

    val chart = binding.silverLayout.silver_3m_Chart.apply {
        isEnabled = true
        setData(data_set)
//        setViewPortOffsets(50f, 30f, 50f, 80f)
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
            gridColor = chart_silverB
            textColor = chart_font
            position = XAxis.XAxisPosition.BOTTOM

            setLabelCount(5, false)
            setDrawGridLines(false)
            textSize = 5F
        }

    chart.axisLeft
        .apply {
            textColor = chart_font
            gridColor = chart_silverB
            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            isEnabled = true
            setDrawGridLines(false)
            setLabelCount(5, true)           //none
            axisMaximum = dataSet.yMax * 25 / 24
            axisMinimum = dataSet.yMin * 23 / 24
            axisLineColor = backGround
        }
    val mv =
        LongMarkerView1(context, "$", R.layout.marker_view, date)
            .apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()
    binding.silverLayout.silver_3m_Chart.visibility = View.VISIBLE
    binding.silverLongChartVisibleLayout.visibility = View.VISIBLE
    binding.silverLongChartProgress.visibility = View.GONE
}

fun getSilverChartSelect_3m(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: ChartFragmentBinding,
    data: Map<String, List<*>>
) {
    setSilverChartSelect_3m(context, viewModel, binding, data)
}


private class LongMarkerView1(
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
            dateLabel.text = "Date: "
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

private class RatioMarkerView1(
    context: Context,
    layoutResource: Int,
    date_input: ArrayList<String>
) : MarkerView(
    context,
    layoutResource
) {
    private val date = date_input

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if ((e?.x ?: 0f).toInt() <= date.size - 1) {
            val date = date[(e?.x ?: 0f).toInt()]
            tvDate.text = date
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