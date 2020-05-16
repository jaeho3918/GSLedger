@file:Suppress("UNCHECKED_CAST")

package com.gsgana.gsledger.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.gsgana.gsledger.R
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.ChartFragmentBinding
import com.gsgana.gsledger.databinding.DetailFragmentBinding
import com.gsgana.gsledger.databinding.StatFragmentBinding
import com.gsgana.gsledger.viewmodels.DetailViewModel
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.chart_layout.view.*
import kotlinx.android.synthetic.main.gold_18m_chart_layout.view.*
import kotlinx.android.synthetic.main.marker_view.view.*
import kotlinx.android.synthetic.main.ratio_18m_chart_layout.view.*
import kotlinx.android.synthetic.main.silver_18m_chart_layout.view.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@SuppressLint("SimpleDateFormat")
private fun calChart1(
    binding: StatFragmentBinding,
    viewModel: HomeViewPagerViewModel,
    chartCompareProduct: Int,
    context: Context?
) {
//    var chartCompareProduct = chartCompareProduct
//    val context_input = context

    val PREF_NAME = "01504f779d6c77df04"
    val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    var currency: Float
    val data = viewModel.getchartData()

    if (viewModel.getRealDataValue().get("currency")?.toInt() ?: 0 == 0) {
        val currencyOption =
            context!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)
        currency =
            (viewModel.getRealData().value?.get(CURRENCY[currencyOption]) ?: 1.0).toFloat()


    } else if (viewModel.getRealDataValue().get("currency")?.toInt() == null) {
        val currencyOption =
            context!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)
        currency =
            (viewModel.getRealData().value?.get(CURRENCY[currencyOption]) ?: 1.0).toFloat()

    } else {
        currency = (viewModel.getRealData().value?.get(
            CURRENCY[(viewModel.getRealDataValue().get("currency")?.toInt() ?: 0)]
        ) ?: 1.0).toFloat()
    }

    val products = viewModel.getProducts().value
    if (!products.isNullOrEmpty()) {
        binding.totalChartProgress.visibility = View.VISIBLE
        binding.totalchartLayout.visibility = View.GONE
        val holder_AG = mutableListOf<Float>()
        val holder_AU = mutableListOf<Float>()
        for (i in 0..80) {
            holder_AG.add(0f)
            holder_AU.add(0f)
        }
        val list1 = data.get("value_AU") as ArrayList<Float>
        val list2 = data.get("value_AG") as ArrayList<Float>
        val dates = data.get("date") as ArrayList<String>
        val result = mutableListOf<Float>()
        var dateFLAG: Int
//                val mformat = SimpleDateFormat("yyyy/MM/dd")

        products.forEach { product ->
            dateFLAG = 0

            if (product.metal == 0) { //Gold
                for (idx in dateFLAG until dates.size) {
                    holder_AU[idx] += list1[idx] * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                }
            } else { //Silver
                for (idx in dateFLAG until dates.size) {
                    holder_AG[idx] += list2[idx] * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                }
            }
        }

        for (idx in 0 until list1.size) {
            result.add(
                (holder_AU[idx] + holder_AG[idx]) * (currency)
            )
        }

        if (context != null) setLineChart(
            context,
            binding,
            dates,
            result,
            viewModel
        )

        binding.totalchartLayout.visibility = View.VISIBLE
        binding.totalChartProgress.visibility = View.GONE
        binding.totalchartCardView.visibility = View.VISIBLE
    } else {
        binding.totalchartCardView.visibility = View.GONE
    }

}

fun getcalChart1(
    binding: StatFragmentBinding,
    viewModel: HomeViewPagerViewModel,
    chartCompareProduct: Int,
    context: Context?
) {
    if (context != null) {
        calChart1(
            binding,
            viewModel,
            chartCompareProduct,
            context
        )
    } else {
        binding.totalchartCardView.visibility = View.GONE
    }
}


private fun calculateProduct(
    viewModel: HomeViewPagerViewModel,
    binding: StatFragmentBinding,
    products: List<Product>
): List<Double> {

    val realData = viewModel.getRealDataValue()

    val currency = viewModel.getRealDataValue().get(
        CURRENCY[((viewModel.getRealDataValue().get("currency") ?: 0.0).toInt())]
    ) ?: 1.0

    val currencySymbol =
        CURRENCYSYMBOL[((viewModel.getRealDataValue().get("currency") ?: 0.0).toInt())]


    var goldCoin_RealData = 0.0
    var goldBar_RealData = 0.0
    var silverCoin_RealData = 0.0
    var silverBar_RealData = 0.0

    var goldCoin_BuyPrice = 0.0
    var goldBar_BuyPrice = 0.0
    var silverCoin_BuyPrice = 0.0
    var silverBar_BuyPrice = 0.0

    val goldCoin_PlPer: Double
    val goldBar_PlPer: Double
    val silverCoin_PlPer: Double
    val silverBar_PlPer: Double

    val goldCoin_Ratio: Double
    val goldBar_Ratio: Double
    val silverCoin_Ratio: Double
    val silverBar_Ratio: Double

    products.forEach { product ->
        /* sum each metal and type  */
        val product_currency = realData[CURRENCY[product.currency]]!!
        //
        if (!realData.isNullOrEmpty()) {
            if (product.metal == 0) {
                if (product.type == 0) {
                    goldCoin_RealData += realData["AU"]!! * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                    goldCoin_BuyPrice += product.prePrice / product_currency

                } else if (product.type == 1) {
                    goldBar_RealData += realData["AU"]!! * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                    goldBar_BuyPrice += product.prePrice / product_currency
                }

            } else if (product.metal == 1) {
                if (product.type == 0) {
                    silverCoin_RealData += realData["AG"]!! * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                    silverCoin_BuyPrice += product.prePrice / product_currency

                } else if (product.type == 1) {
                    silverBar_RealData += realData["AG"]!! * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                    silverBar_BuyPrice += product.prePrice / product_currency
                }
            }
        }
    }

    /*calculate Total*/
    val total = goldCoin_RealData + goldBar_RealData + silverCoin_RealData + silverBar_RealData
    val total_Pl =
        total - (goldCoin_BuyPrice + goldBar_BuyPrice + silverCoin_BuyPrice + silverBar_BuyPrice)
    val total_Plper =
        total_Pl / (goldCoin_BuyPrice + goldBar_BuyPrice + silverCoin_BuyPrice + silverBar_BuyPrice) * 100

    /*calculate Ratio*/
    goldCoin_Ratio = (goldCoin_RealData / total)
    goldBar_Ratio = (goldBar_RealData / total)
    silverCoin_Ratio = (silverCoin_RealData / total)
    silverBar_Ratio = (silverBar_RealData / total)

    /*calculate Price USD -> CURRENCY*/
    val result_total = total * currency
    val result_totalPl = total_Pl * currency

    val result_goldCoin = goldCoin_RealData * currency
    val result_goldBar = goldBar_RealData * currency
    val result_silverCoin = silverCoin_RealData * currency
    val result_silverBar = silverBar_RealData * currency

    val result_goldCoin_BuyPrice = goldCoin_BuyPrice * currency
    val result_goldBar_BuyPrice = goldBar_BuyPrice * currency
    val result_silverCoin_BuyPrice = silverCoin_BuyPrice * currency
    val result_silverBar_BuyPrice = silverBar_BuyPrice * currency

    /*calculate Pl*/
    goldCoin_PlPer =
        (result_goldCoin - result_goldCoin_BuyPrice) / result_goldCoin_BuyPrice * 100
    goldBar_PlPer = (result_goldBar - result_goldBar_BuyPrice) / result_goldBar_BuyPrice * 100
    silverCoin_PlPer =
        (result_silverCoin - result_silverCoin_BuyPrice) / result_silverCoin_BuyPrice * 100
    silverBar_PlPer =
        (result_silverBar - result_silverBar_BuyPrice) / result_silverBar_BuyPrice * 100

    /* set Visible Layout */
    viewModel.setRatioMetal(
        listOf(
            goldCoin_Ratio,
            goldBar_Ratio,
            silverCoin_Ratio,
            silverBar_Ratio
        )
    )

    if (total == 0.0) {
        binding.totalCurrency.text = ""
        binding.totalPrice.text = "-"
        binding.totalPlper.text = ""

        binding.plPrice.text = "-"
        binding.plCurrency.text = ""
        binding.chartprogress.visibility = View.GONE
    }

    if (result_goldCoin + result_goldBar == 0.0) {
        binding.goldprogress.visibility = View.GONE
        binding.goldNone.visibility = View.VISIBLE
    } else {
        binding.goldNone.visibility = View.GONE
    }
    if (result_silverCoin + result_silverBar == 0.0) {
        binding.silverNone.visibility = View.VISIBLE
        binding.silverprogress.visibility = View.GONE
    } else {
        binding.silverNone.visibility = View.GONE
    }


    /* set Visible Layout */
    if (result_total > 0) {
        binding.totalCurrency.text = currencySymbol
        binding.totalPrice.text =
            priceToString(result_total, "PriceInt")
        binding.totalPlper.text =
            priceToString(total_Plper, "Pl")

        binding.plPrice.text =
            priceToString(
                result_totalPl,
                "PriceInt"
            )
        binding.plCurrency.text = currencySymbol

        binding.totalLayout.visibility = View.VISIBLE
    }

    if (result_goldCoin > 0) {
        binding.goldCoinCurrency.text = currencySymbol
        binding.goldCoinPrice.text =
            priceToString(
                result_goldCoin,
                "PriceInt"
            )
        binding.goldCoinPl.text = priceToString(
            goldCoin_PlPer,
            "Pl"
        ) //${CURRENCYSYMBOL[currencyOption]} ${priceToString(goldCoin_Pl, "PricePl")}
//            binding.goldCoinPlCurrency.text = CURRENCYSYMBOL[currencyOption] //"(" + CURRENCYSYMBOL[currencyOption]

        binding.totalGoldLayout.visibility = View.VISIBLE
        binding.goldCoinLayout.visibility = View.VISIBLE
        binding.goldprogress.visibility = View.GONE
    } else {
        binding.goldCoinLayout.visibility = View.GONE
    }
    if (result_goldBar > 0) {
        binding.goldBarCurrency.text = currencySymbol
        binding.goldBarPrice.text =
            priceToString(
                result_goldBar,
                "PriceInt"
            )
        binding.goldBarPl.text = priceToString(
            goldBar_PlPer,
            "Pl"
        )// ${CURRENCYSYMBOL[currencyOption]} ${priceToString(goldBar_Pl, "PricePl")}
//            binding.goldBarPlCurrency.text = CURRENCYSYMBOL[currencyOption] //"(" + CURRENCYSYMBOL[currencyOption]

        binding.totalGoldLayout.visibility = View.VISIBLE
        binding.goldBarLayout.visibility = View.VISIBLE
        binding.goldprogress.visibility = View.GONE
    } else {
        binding.goldBarLayout.visibility = View.GONE
    }
    if (result_silverCoin > 0) {
        binding.silverCoinCurrency.text = currencySymbol
        binding.silverCoinPrice.text =
            priceToString(
                result_silverCoin,
                "PriceInt"
            )
        binding.silverCoinPl.text = priceToString(
            silverCoin_PlPer,
            "Pl"
        )// ${CURRENCYSYMBOL[currencyOption]} ${priceToString(silverCoin_Pl, "PricePl")}
//            binding.silverCoinPlCurrency.text = CURRENCYSYMBOL[currencyOption] //"(" + CURRENCYSYMBOL[currencyOption]

        binding.totalSilverLayout.visibility = View.VISIBLE
        binding.silverCoinLayout.visibility = View.VISIBLE
        binding.silverprogress.visibility = View.GONE
    } else {
        binding.silverCoinLayout.visibility = View.GONE
    }
    if (result_silverBar > 0) {
        binding.silverBarCurrency.text = currencySymbol
        binding.silverBarPrice.text =
            priceToString(
                result_silverBar,
                "PriceInt"
            )
        binding.silverBarPl.text = priceToString(
            silverBar_PlPer,
            "Pl"
        )// ${CURRENCYSYMBOL[currencyOption]} ${priceToString(silverBar_Pl, "PricePl")}
//            binding.silverBarPlCurrency.text = CURRENCYSYMBOL[currencyOption] //"(" + CURRENCYSYMBOL[currencyOption]

        binding.totalSilverLayout.visibility = View.VISIBLE
        binding.silverBarLayout.visibility = View.VISIBLE
        binding.silverprogress.visibility = View.GONE
    } else {
        binding.silverBarLayout.visibility = View.GONE
    }

    return listOf(
        goldCoin_Ratio,
        goldBar_Ratio,
        silverCoin_Ratio,
        silverBar_Ratio
    )
}

fun getcalculateProduct(
    viewModel: HomeViewPagerViewModel,
    binding: StatFragmentBinding,
    products: List<Product>
): List<Double> {
    return calculateProduct(
        viewModel,
        binding,
        products
    )
}

private fun priceToString(price: Double, type: String): String {
    return when (type) {
        "PriceInt" -> {
            String.format("%,.0f", price)
        }

        "PriceFloat" -> {
            String.format("%,.2f", price)
        }

        "Pl" -> {
            when {
                price > 0.01 -> {
                    "(+" + String.format("%,.2f", price) + "%)"
                }
                price < -0.01 -> {
                    "(" + String.format("%,.2f", price) + "%)"
                }
                else -> "( 0.00%)"
            }
        }
        "PricePl" -> {
            when {
                price > 1 -> {
                    "+" + String.format("%,.0f", price)
                }
                price < -1 -> {
                    "" + String.format("%,.0f", price)
                }
                else -> "0"
            }
        }

        else -> {

            ""
        }
    }
}

private fun setChart(
    context: Context?,
    binding: StatFragmentBinding,
    ratio: List<Double>
) {

    val chart_goldC = context?.resources?.getColor(R.color.chart_goldC, null)
    val chart_goldB = context?.resources?.getColor(R.color.chart_goldB, null)
    val chart_silverC = context?.resources?.getColor(R.color.chart_silverC, null)
    val chart_silverB = context?.resources?.getColor(R.color.chart_silverB, null)
    val backGround = context?.resources?.getColor(R.color.white, null)

    val chartData = mutableListOf(0f, 0f, 0f, 0f)
    ratio.forEachIndexed { idx, value ->
        chartData[idx] = value.toFloat()
    }
    if (ratio.isNullOrEmpty()) chartData.clear()


    val pieChart = binding.statChart
    pieChart.setUsePercentValues(true)
    pieChart.description.isEnabled = false

    pieChart.dragDecelerationFrictionCoef = 0.95f

    pieChart.isDrawHoleEnabled = true
    pieChart.setHoleColor(backGround ?: Color.parseColor("#000000"))
//
    pieChart.setTransparentCircleAlpha(110)
    pieChart.setCenterTextSize(18f)

    pieChart.holeRadius = 35f
    pieChart.transparentCircleRadius = 41f

    pieChart.setDrawCenterText(true)

    pieChart.rotationAngle = 0f

    pieChart.isRotationEnabled = false
    pieChart.isHighlightPerTapEnabled = false

    // add a selection listener
    pieChart.animateXY(1100, 1100)

    pieChart.setEntryLabelTextSize(15f)

    val yvalues = ArrayList<PieEntry>()

    if (chartData[0] != 0f) yvalues.add(PieEntry(chartData[0], "Gold Coin"))
    if (chartData[1] != 0f) yvalues.add(PieEntry(chartData[1], "Gold Bar"))
    if (chartData[2] != 0f) yvalues.add(PieEntry(chartData[2], "Silver Coin"))
    if (chartData[3] != 0f) yvalues.add(PieEntry(chartData[3], "Silver Bar"))

    val dataSet = PieDataSet(yvalues, "")

    // add a lot of colors
    val colors = mutableListOf<Int>()
    if (chartData[0] != 0f) colors.add(chart_goldC ?: Color.parseColor("#ffd752"))
    if (chartData[1] != 0f) colors.add(chart_goldB ?: Color.parseColor("#ffd752"))
    if (chartData[2] != 0f) colors.add(chart_silverC ?: Color.parseColor("#a8a8a8"))
    if (chartData[3] != 0f) colors.add(chart_silverB ?: Color.parseColor("#a8a8a8"))

    dataSet.colors = colors

    val data = PieData(dataSet)
    data.setValueFormatter(PercentFormatter(pieChart))
    data.setValueTextSize(11f)

//        data.setValueTextColor(com.gsgana.gsledger.fragment.FirstFragment.gray)
    pieChart.data = data

    // undo all highlights
    pieChart.highlightValues(null)

    val sumData = chartData.sum()

    if (sumData != 0f) {
        pieChart.visibility = View.VISIBLE
        binding.chartprogress.visibility = View.GONE
        pieChart.invalidate()
    } else {
        pieChart.visibility = View.GONE
        binding.chartprogress.visibility = View.VISIBLE
    }

    pieChart.invalidate()

    pieChart.animateY(1100, Easing.EaseInOutQuad)
}

fun getsetChart(
    context: Context?,
    binding: StatFragmentBinding,
    ratio: List<Double>
) {
    setChart(context, binding, ratio)
}

private fun setLineChart(  //TOTAL CHART
    context: Context,
    binding: StatFragmentBinding,
    date: ArrayList<String>,
    value: List<Float>,
    viewModel: HomeViewPagerViewModel
) {

    val chart_font = context.resources.getColor(R.color.chart_font, null)
    val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
    val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
    val backGround = context.resources.getColor(R.color.white, null)

    val currencySymbol = CURRENCYSYMBOL[(viewModel.getRealData().value?.get("currency")
        ?: 0.0).toInt()]

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

    val data_set = LineData(dataSet)

    val chart = binding.totalChart.apply {
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
//            isEnabled = false
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
            isEnabled = false
            setDrawGridLines(false)
            setLabelCount(3, true)              //none
            axisMaximum = dataSet.yMax * 25 / 24
            axisMinimum = dataSet.yMin * 23 / 24
            axisLineColor = backGround
        }


    val mv =
        CustomMarkerView(
            context,
            currencySymbol,
            R.layout.marker_view,
            date
        ).apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()
}

private class CustomMarkerView(
    context: Context,
    private val currencySymbol: String,
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


private class RatioMarkerView(
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


private fun setShortLineGoldChart(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: StatFragmentBinding,
    data: Map<String, List<*>>
) {

//    val chart_font = context.resources.getColor(R.color.chart_font, null)
    val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
    val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
    val backGround = context.resources.getColor(R.color.white, null)

    val PREF_NAME = "01504f779d6c77df04"
    val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    var currency: Float
    var currencySymbol: String
    val weight: Float = when (viewModel.getRealDataValue()["weightUnit"]!!) {
        0.0 -> 1.0f //toz
        1.0 -> 0.03215f //g
        2.0 -> 32.150747f  //kg
        3.0 -> 0.120565f//don
        else -> 1.0f
    }

    if (viewModel.getRealDataValue().get("currency")?.toInt() ?: 0 == 0) {
        val currencyOption =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)

        currency =
            (viewModel.getRealData().value?.get(CURRENCY[currencyOption]) ?: 1.0).toFloat()

        currencySymbol = CURRENCYSYMBOL[currencyOption]

    } else if (viewModel.getRealDataValue().get("currency")?.toInt() == null) {
        val currencyOption =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)

        currency =
            (viewModel.getRealData().value?.get(CURRENCY[currencyOption]) ?: 1.0).toFloat()

        currencySymbol = CURRENCYSYMBOL[currencyOption]

    } else {
        currency = (viewModel.getRealData().value?.get(
            CURRENCY[(viewModel.getRealDataValue().get("currency")?.toInt() ?: 0)]
        ) ?: 1.0).toFloat()
        currencySymbol =
            CURRENCYSYMBOL[(viewModel.getRealDataValue().get("currency")?.toInt() ?: 0)]
    }

    val result = arrayListOf<Entry>()
    data.getValue("value_AU").forEachIndexed { index, fl ->
        result.add(
            Entry(
                index.toFloat(),
                (fl.toString().toFloat()) * currency * weight
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
            isHighlightEnabled = false
            setDrawCircles(false)
        }

    val data_set = LineData(dataSet)

    val chart = binding.chartLayout.goldShortChart.apply {
        isEnabled = true
        setData(data_set)
//        setViewPortOffsets(50f, 30f, 50f, 80f)
        setBackgroundColor(backGround)
        isDoubleTapToZoomEnabled = false
        setDrawMarkers(false)
        description.isEnabled = false
        setTouchEnabled(false)
        isDragEnabled = false
        setScaleEnabled(false)
        setDrawGridBackground(false)
        maxHighlightDistance = 300f
        setPinchZoom(false)
        isHighlightPerTapEnabled = true
        axisRight.isEnabled = false
        legend.isEnabled = false
        fitScreen()

    }
//    val valueFormatter = IndexAxisValueFormatter(date)

    chart.xAxis
        .apply {
//            setValueFormatter(valueFormatter)
            isEnabled = false
//            gridColor = chart_goldB
//            textColor = chart_font
            position = XAxis.XAxisPosition.BOTTOM
//            setLabelCount(5, false)
            setDrawGridLines(false)
//            textSize = 5F
        }

    chart.axisLeft
        .apply {
//            textColor = chart_font
//            gridColor = chart_goldB
//            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            isEnabled = false
            setDrawGridLines(false)
//            setLabelCount(3, true)              //none
            axisMaximum = dataSet.yMax + dataSet.yMin * 1 / 180
            axisMinimum = dataSet.yMin - dataSet.yMin * 1 / 180
//            axisLineColor = backGround
        }

    val mv =
        CustomMarkerView(context, currencySymbol, R.layout.marker_view, date)
            .apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()
    binding.chartLayout.goldShortChart.visibility = View.VISIBLE
    binding.chartLayout.goldShortChartProgress.visibility = View.GONE
}

fun getShortLineGoldChart(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: StatFragmentBinding,
    data: Map<String, List<*>>
) {
    setShortLineGoldChart(context, viewModel, binding, data)
}

private fun setShortLineSilverChart(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: StatFragmentBinding,
    data: Map<String, List<*>>
) {
//    val chart_font = context.resources.getColor(R.color.chart_font, null)
    val chart_silverC = context.resources.getColor(R.color.chart_silverC, null)
    val chart_silverB = context.resources.getColor(R.color.chart_silverB, null)
    val backGround = context.resources.getColor(R.color.white, null)

    val PREF_NAME = "01504f779d6c77df04"
    val CURR_NAME = "1w3d4f7w9d2qG2eT36"

    var currency: Float
    var currencySymbol: String

    val weight: Float = when (viewModel.getRealDataValue()["weightUnit"]!!) {
        0.0 -> 1.0f //toz
        1.0 -> 0.03215f //g
        2.0 -> 32.150747f  //kg
        3.0 -> 0.120565f//don
        else -> 1.0f
    }

    if (viewModel.getRealDataValue().get("currency")?.toInt() ?: 0 == 0) {
        val currencyOption =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)

        currency =
            (viewModel.getRealData().value?.get(CURRENCY[currencyOption]) ?: 1.0).toFloat()

        currencySymbol = CURRENCYSYMBOL[currencyOption]

    } else if (viewModel.getRealDataValue().get("currency")?.toInt() == null) {
        val currencyOption =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)

        currency =
            (viewModel.getRealData().value?.get(CURRENCY[currencyOption]) ?: 1.0).toFloat()

        currencySymbol = CURRENCYSYMBOL[currencyOption]

    } else {
        currency = (viewModel.getRealData().value?.get(
            CURRENCY[(viewModel.getRealDataValue().get("currency")?.toInt() ?: 0)]
        ) ?: 1.0).toFloat()
        currencySymbol =
            CURRENCYSYMBOL[(viewModel.getRealDataValue().get("currency")?.toInt() ?: 0)]
    }


    val result = arrayListOf<Entry>()
    data.getValue("value_AG").forEachIndexed { index, fl ->
        result.add(
            Entry(
                index.toFloat(),
                (fl.toString().toFloat()) * currency * weight
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
            isHighlightEnabled = false
            setDrawCircles(false)
        }

    val data_set = LineData(dataSet)

    val chart = binding.chartLayout.silverShortChart.apply {
        isEnabled = true
        setData(data_set)
//        setViewPortOffsets(50f, 30f, 50f, 80f)
        setBackgroundColor(backGround)
        isDoubleTapToZoomEnabled = false
        setDrawMarkers(false)
        description.isEnabled = false
        setTouchEnabled(false)
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
//    val valueFormatter = IndexAxisValueFormatter(date)

    chart.xAxis
        .apply {
//            setValueFormatter(valueFormatter)
            isEnabled = false
//            gridColor = chart_silverB
//            textColor = chart_font
//            position = XAxis.XAxisPosition.BOTTOM
//            setLabelCount(5, false)
            setDrawGridLines(false)
//            textSize = 5F
        }

    chart.axisLeft
        .apply {
//            textColor = chart_font
//            gridColor = chart_silverB
//            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            isEnabled = false
            setDrawGridLines(false)
//            setLabelCount(3, true)              //none
            axisMaximum = dataSet.yMax + dataSet.yMin * 1 / 180
            axisMinimum = dataSet.yMin - dataSet.yMin * 1 / 180
            axisLineColor = backGround
        }

    val mv =
        CustomMarkerView(context, currencySymbol, R.layout.marker_view, date)
            .apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()
    binding.chartLayout.silverShortChart.visibility = View.VISIBLE
    binding.chartLayout.silverShortChartProgress.visibility = View.GONE
}

fun getShortLineSilverChart(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: StatFragmentBinding,
    data: Map<String, List<*>>
) {
    setShortLineSilverChart(context, viewModel, binding, data)
}


private fun setShortLineGoldChartZoom(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: StatFragmentBinding,
    data: Map<String, List<*>>
) {

    val chart_font = context.resources.getColor(R.color.chart_font, null)
    val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
    val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
    val backGround = context.resources.getColor(R.color.white, null)

    val PREF_NAME = "01504f779d6c77df04"
    val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    val currency: Float
    val currencySymbol: String
    val weight: Float = when (viewModel.getRealDataValue()["weightUnit"]!!) {
        0.0 -> 1.0f //toz
        1.0 -> 0.03215f //g
        2.0 -> 32.150747f  //kg
        3.0 -> 0.120565f//don
        else -> 1.0f
    }

    if (viewModel.getRealDataValue().get("currency")?.toInt() ?: 0 == 0) {
        val currencyOption =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)

        currency =
            (viewModel.getRealData().value?.get(CURRENCY[currencyOption]) ?: 1.0).toFloat()

        currencySymbol = CURRENCYSYMBOL[currencyOption]

    } else if (viewModel.getRealDataValue().get("currency")?.toInt() == null) {
        val currencyOption =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)

        currency =
            (viewModel.getRealData().value?.get(CURRENCY[currencyOption]) ?: 1.0).toFloat()

        currencySymbol = CURRENCYSYMBOL[currencyOption]

    } else {
        currency = (viewModel.getRealData().value?.get(
            CURRENCY[(viewModel.getRealDataValue().get("currency")?.toInt() ?: 0)]
        ) ?: 1.0).toFloat()
        currencySymbol =
            CURRENCYSYMBOL[(viewModel.getRealDataValue().get("currency")?.toInt() ?: 0)]
    }

    val result = arrayListOf<Entry>()
    data.getValue("value_AU").forEachIndexed { index, fl ->
        result.add(
            Entry(
                index.toFloat(),
                (fl.toString().toFloat()) * currency * weight
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

    val chart = binding.chartLayout.goldShortChart.apply {
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
            isEnabled = false
            setDrawGridLines(false)
            setLabelCount(3, true)              //none
            axisMaximum = dataSet.yMax + dataSet.yMin * 1 / 180
            axisMinimum = dataSet.yMin - dataSet.yMin * 1 / 180
            axisLineColor = backGround
        }

    val mv =
        CustomMarkerView(context, currencySymbol, R.layout.marker_view, date)
            .apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()
    binding.chartLayout.goldShortChart.visibility = View.VISIBLE
    binding.chartLayout.goldShortChartProgress.visibility = View.GONE
}

fun getShortLineGoldChartZoom(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: StatFragmentBinding,
    data: Map<String, List<*>>
) {
    setShortLineGoldChartZoom(context, viewModel, binding, data)
}

private fun setShortLineSilverChartZoom(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: StatFragmentBinding,
    data: Map<String, List<*>>
) {
    val chart_font = context.resources.getColor(R.color.chart_font, null)
    val chart_silverC = context.resources.getColor(R.color.chart_silverC, null)
    val chart_silverB = context.resources.getColor(R.color.chart_silverB, null)
    val backGround = context.resources.getColor(R.color.white, null)

    val PREF_NAME = "01504f779d6c77df04"
    val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    var currency: Float
    var currencySymbol: String
    val weight: Float = when (viewModel.getRealDataValue()["weightUnit"]!!) {
        0.0 -> 1.0f //toz
        1.0 -> 0.03215f //g
        2.0 -> 32.150747f  //kg
        3.0 -> 0.120565f//don
        else -> 1.0f
    }

    if (viewModel.getRealDataValue().get("currency")?.toInt() ?: 0 == 0) {
        val currencyOption =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)

        currency =
            (viewModel.getRealData().value?.get(CURRENCY[currencyOption]) ?: 1.0).toFloat()

        currencySymbol = CURRENCYSYMBOL[currencyOption]

    } else if (viewModel.getRealDataValue().get("currency")?.toInt() == null) {
        val currencyOption =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)

        currency =
            (viewModel.getRealData().value?.get(CURRENCY[currencyOption]) ?: 1.0).toFloat()

        currencySymbol = CURRENCYSYMBOL[currencyOption]

    } else {
        currency = (viewModel.getRealData().value?.get(
            CURRENCY[(viewModel.getRealDataValue().get("currency")?.toInt() ?: 0)]
        ) ?: 1.0).toFloat()
        currencySymbol =
            CURRENCYSYMBOL[(viewModel.getRealDataValue().get("currency")?.toInt() ?: 0)]
    }


    val result = arrayListOf<Entry>()
    data.getValue("value_AG").forEachIndexed { index, fl ->
        result.add(
            Entry(
                index.toFloat(),
                (fl.toString().toFloat()) * currency * weight
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

    val chart = binding.chartLayout.silverShortChart.apply {
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
            isEnabled = false
            setDrawGridLines(false)
            setLabelCount(3, true)              //none
            axisMaximum = dataSet.yMax + dataSet.yMin * 1 / 180
            axisMinimum = dataSet.yMin - dataSet.yMin * 1 / 180
            axisLineColor = backGround
        }
    val mv =
        CustomMarkerView(context, currencySymbol, R.layout.marker_view, date)
            .apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()
    binding.chartLayout.silverShortChart.visibility = View.VISIBLE
    binding.chartLayout.silverShortChartProgress.visibility = View.GONE
}

fun getShortLineSilverChartZoom(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: StatFragmentBinding,
    data: Map<String, List<*>>
) {
    setShortLineSilverChartZoom(context, viewModel, binding, data)
}


private fun setRatioChartSelect_18m(
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

    val chart = binding.ratioLayout.ratio_18m_Chart.apply {
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
        RatioMarkerView(context, R.layout.marker_view, date)
            .apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()

    binding.ratioLayout.ratio_18m_Chart.visibility = View.VISIBLE
    binding.ratioLongChartVisibleLayout.visibility = View.VISIBLE
    binding.ratioLongChartProgress.visibility = View.GONE
}

fun getRatioChartSelect_18m(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: ChartFragmentBinding,
    data: Map<String, List<*>>
) {
    setRatioChartSelect_18m(context, viewModel, binding, data)
}

private fun setGoldChartSelect_18m(
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

    val chart = binding.goldLayout.gold_18m_Chart.apply {
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
        LongMarkerView(context, "$", R.layout.marker_view, date)
            .apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()
    binding.goldLayout.gold_18m_Chart.visibility = View.VISIBLE
    binding.goldLongChartVisibleLayout.visibility = View.VISIBLE
    binding.goldLongChartProgress.visibility = View.GONE
}

fun getGoldChartSelect_18m(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: ChartFragmentBinding,
    data: Map<String, List<*>>
) {
    setGoldChartSelect_18m(context, viewModel, binding, data)
}

private fun setSilverChartSelect_18m(
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

    val chart = binding.silverLayout.silver_18m_Chart.apply {
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
        LongMarkerView(context, "$", R.layout.marker_view, date)
            .apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()
    binding.silverLayout.silver_18m_Chart.visibility = View.VISIBLE
    binding.silverLongChartVisibleLayout.visibility = View.VISIBLE
    binding.silverLongChartProgress.visibility = View.GONE
}

fun getSilverChartSelect_18m(
    context: Context,
    viewModel: HomeViewPagerViewModel,
    binding: ChartFragmentBinding,
    data: Map<String, List<*>>
) {
    setSilverChartSelect_18m(context, viewModel, binding, data)
}

private fun setLabel(binding: StatFragmentBinding, viewModel: HomeViewPagerViewModel) {

    val weight: Double = when (viewModel.getRealDataValue()["weightUnit"]!!) {
        0.0 -> 1.0 //toz
        1.0 -> 0.03215 //g
        2.0 -> 32.150747  //kg
        3.0 -> 0.120565//don
        else -> 1.0
    }
    var currency = viewModel.getRealDataValue().get("currency")!!.toInt()

    binding.chartLayout.goldRealCurrency.text = CURRENCYSYMBOL[currency]
    binding.chartLayout.goldRealPrice.text =
        String.format(
            "%,.2f",
            viewModel.getRealDataValue()["AU"]!! * viewModel.getRealDataValue()[CURRENCY[currency]]!! * weight
        )
    binding.chartLayout.goldRealWeight.text =
        "1 " + WEIGHTUNIT[viewModel.getRealDataValue().getValue("weightUnit").toInt()] + ": "

    binding.chartLayout.silverRealCurrency.text = CURRENCYSYMBOL[currency]
    binding.chartLayout.silverRealPrice.text =
        String.format(
            "%,.2f",
            viewModel.getRealDataValue()["AG"]!! * viewModel.getRealDataValue()[CURRENCY[currency]]!! * weight
        )
    binding.chartLayout.silverRealWeight.text =
        "1 " + WEIGHTUNIT[viewModel.getRealDataValue().getValue("weightUnit").toInt()] + ": "
}

fun getsetLabel(binding: StatFragmentBinding, viewModel: HomeViewPagerViewModel) {
    setLabel(binding, viewModel)
}

private fun detailChart(
    binding: DetailFragmentBinding,
    viewModel: DetailViewModel,
    context: Context?,
    input_data: Map<String, ArrayList<*>>,
    realData: HashMap<String, Double>
) {
//    var chartCompareProduct = chartCompareProduct
//    val context_input = context


    var currency: Float
    val data = input_data

    currency =
        (realData.get(CURRENCY[viewModel.getProduct().value?.currency!!]) ?: 1.0).toFloat()

    val product = viewModel.getProduct().value
    if (product != null) {

        val list1 = data.get("value") as ArrayList<Float>
        val dates = data.get("date") as ArrayList<String>
        val result = mutableListOf<Float>()

        for (idx in 0 until list1.size) {
            if (list1[idx] != null)
                result.add(
                    list1[idx] * currency * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                )
        }

        if (context != null) setDetailChart(
            context,
            viewModel,
            binding,
            dates,
            result,
            CURRENCYSYMBOL[viewModel.getProduct().value?.currency!!]
        )

        binding.totalDetailChartLayout.visibility = View.VISIBLE
        binding.totalDetailChart.visibility = View.VISIBLE
        binding.totalDetailChartProgress.visibility = View.GONE

    }

}

fun getdetailChart(
    binding: DetailFragmentBinding,
    viewModel: DetailViewModel,
    context: Context?,
    date: Map<String, ArrayList<*>>,
    realData: HashMap<String, Double>
) {
    if (context != null) {
        detailChart(
            binding,
            viewModel,
            context,
            date, realData
        )
    }
}

private fun setDetailChart(
    context: Context,
    viewModel: DetailViewModel,
    binding: DetailFragmentBinding,
    date: ArrayList<String>,
    value: MutableList<Float>,
    symbol: String
) {

    val chart_font = context.resources.getColor(R.color.chart_font, null)
    val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
    val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
    val backGround = context.resources.getColor(R.color.white, null)

    val currencySymbol = symbol

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

    val data_set = LineData(dataSet)

    val chart = binding.totalDetailChart.apply {
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
//            isEnabled = false
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
//            isEnabled = false
            setDrawGridLines(false)
            setLabelCount(3, true)              //none
            axisMaximum = dataSet.yMax * 25 / 24
            axisMinimum = dataSet.yMin * 23 / 24
            axisLineColor = backGround
        }


    val mv =
        CustomMarkerDetailView(
            context,
            currencySymbol,
            R.layout.marker_view,
            viewModel
        ).apply { chartView = chart }

    chart.marker = mv
    chart.invalidate()
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


private class CustomMarkerDetailView(
    context: Context,
    private val currencySymbol: String,
    layoutResource: Int,
    private val viewModel: DetailViewModel
) : MarkerView(
    context,
    layoutResource
) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if ((e?.x ?: 0f).toInt() <= viewModel.getChartDate().size - 1) {
            val date = viewModel.getChartDate()[(e?.x ?: 0f).toInt()]
            tvDate.text =
                date.substring(0..3) + "/" + date.substring(4..5) + "/" + date.substring(6..7)
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


