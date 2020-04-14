package com.gsgana.gsledger

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.FirebaseAuth
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.StatFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.CURRENCYSYMBOL
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.PACKAGENUM
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel


class StatFragment : Fragment() {
    private lateinit var binding: StatFragmentBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var rgl: MutableList<Char>
    private var switchChart = false

    private lateinit var fm: FragmentManager

    private var ratio: List<Double>? = null

    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(requireActivity(), null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()
        fm = childFragmentManager
        fm.popBackStack()


        binding = StatFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.realData.observe(viewLifecycleOwner, Observer { realData ->
            if (!viewModel.getProducts().value.isNullOrEmpty()) {
                val products = viewModel.getProducts().value!!
                ratio = calculateProduct(binding, realData, products)
            }
        }
        )

        viewModel.getProducts().observe(viewLifecycleOwner, Observer { products ->
            if (!viewModel.realData.value.isNullOrEmpty()) {
                val realData = viewModel.realData.value!!
                ratio = calculateProduct(binding, realData, products)
                setChart(context!!, binding, ratio!!)
                switchChart = true
            } else {
                binding.statChart.visibility = View.GONE
                binding.totalCurrency.text = ""
                binding.totalPrice.text = "-"
                binding.totalPlper.text = ""
                binding.plPrice.text = "-"
                binding.plCurrency.text = ""
                binding.goldprogress.visibility = View.GONE
                binding.silverprogress.visibility = View.GONE
                binding.goldNone.visibility = View.VISIBLE
                binding.silverNone.visibility = View.VISIBLE
            }
            if (!products.isNullOrEmpty()) { // List exist
                binding.statChart.visibility = View.VISIBLE
                binding.isEmptyLayout.visibility = View.GONE
            } else { //Empty
                binding.statChart.visibility = View.GONE
                binding.isEmptyLayout.visibility = View.VISIBLE

            }
        }
        )

        val list1 = arrayListOf(
            321386.0f,
            316279.0f,
            300740.0f,
            344709.0f,
            343791.0f,
            368166.0f,
            386592.0f,
            393136.0f,
            415565.0f,
            407112.0f,
            472422.0f,
            462777.0f,
            459997.0f,
            440277.0f,
            433119.0f,
            493098.0f,
            539812.0f,
            621672.0f,
            577607.0f,
            623822.0f,
            670383.0f,
            880014.0f,
            922649.0f,
            1214453.0f,
            1185725.0f,
            1192379.0f,
            1303124.0f,
            1545649.0f,
            1505712.0f,
            1577093.0f,
            1773164.0f,
            1856614.0f,
            1826469.0f,
            1951372.0f,
            1772516.0f,
            1435832.0f,
            1317464.0f,
            1371267.0f,
            1306834.0f,
            1381955.0f,
            1311315.0f,
            1315183.0f,
            1484457.0f,
            1497402.0f,
            1347457.0f,
            1391865.0f,
            1472266.0f,
            1425448.0f,
            1384784.0f,
            1383482.0f,
            1488812.0f,
            1866962.0f,
            1810542.0f
        )
        val dates = arrayListOf(
            "20000120",
            "20000614",
            "20001101",
            "20010322",
            "20010809",
            "20011227",
            "20020517",
            "20021004",
            "20030224",
            "20030715",
            "20031202",
            "20040421",
            "20040908",
            "20050126",
            "20050615",
            "20051102",
            "20060322",
            "20060809",
            "20061227",
            "20070517",
            "20071004",
            "20080221",
            "20080710",
            "20081127",
            "20090416",
            "20090903",
            "20100121",
            "20100610",
            "20101028",
            "20110318",
            "20110805",
            "20111223",
            "20120511",
            "20120928",
            "20130215",
            "20130708",
            "20131125",
            "20140414",
            "20140901",
            "20150119",
            "20150608",
            "20151026",
            "20160314",
            "20160801",
            "20161219",
            "20170508",
            "20170925",
            "20180212",
            "20180703",
            "20181120",
            "20190409",
            "20190828",
            "20200110"
        )

        Handler().postDelayed({
            if (!ratio.isNullOrEmpty()) {
                if (!switchChart) setChart(context!!, binding, ratio!!) //if (context!=null)
                setLineChart(context!!, binding, dates, list1)
            }
        }, 2400)

        binding.addBtn.setOnClickListener {
            findNavController()
                .navigate(R.id.action_homeViewPagerFragment_to_write1Fragment)
        }


        return binding.root
    }

    private fun setChart(
        context: Context,
        binding: StatFragmentBinding,
        ratio: List<Double>
    ) {

        val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
        val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
        val chart_silverC = context.resources.getColor(R.color.chart_silverC, null)
        val chart_silverB = context.resources.getColor(R.color.chart_silverB, null)
        val backGround = context.resources.getColor(R.color.border_background, null)

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
        pieChart.setHoleColor(backGround)
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

//        pieChart.setEntryLabelColor(com.gsgana.gsledger.fragment.FirstFragment.gray)
        pieChart.setEntryLabelTextSize(15f)

        val yvalues = ArrayList<PieEntry>()

        if (chartData[0] != 0f) yvalues.add(PieEntry(chartData[0], "Gold Coin"))
        if (chartData[1] != 0f) yvalues.add(PieEntry(chartData[1], "Gold Bar"))
        if (chartData[2] != 0f) yvalues.add(PieEntry(chartData[2], "Silver Coin"))
        if (chartData[3] != 0f) yvalues.add(PieEntry(chartData[3], "Silver Bar"))

        val dataSet = PieDataSet(yvalues, "")

        // add a lot of colors
        val colors = mutableListOf<Int>()
        if (chartData[0] != 0f) colors.add(chart_goldC)
        if (chartData[1] != 0f) colors.add(chart_goldB)
        if (chartData[2] != 0f) colors.add(chart_silverC)
        if (chartData[3] != 0f) colors.add(chart_silverB)

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

//        pieChart.visibility = View.VISIBLE
//        binding.chartprogress.visibility = View.GONE

        pieChart.invalidate()

        pieChart.animateY(1100, Easing.EaseInOutQuad)
    }

    private fun setLineChart(
        context: Context,
        binding: StatFragmentBinding,
        date: ArrayList<String>,
        value: List<Float>
    ) {

        val chart_goldC = context.resources.getColor(R.color.chart_goldC, null)
        val chart_goldB = context.resources.getColor(R.color.chart_goldB, null)
        val chart_silverC = context.resources.getColor(R.color.chart_silverC, null)
        val chart_silverB = context.resources.getColor(R.color.chart_silverB, null)
        val backGround = context.resources.getColor(R.color.border_background, null)

        val chart = binding.goldChart.apply {
            setViewPortOffsets(0f, 0f, 0f, 0f)
            setBackgroundColor(backGround)
        }
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
            }

        val data = LineData(dataSet)

        chart.data = data

        chart.setDrawMarkers(true)


//        chart.setOnApplyWindowInsetsListener(this)

        chart.isEnabled = true

        // enable touch gestures

        // enable touch gestures
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately

        chart.setDrawGridBackground(false)
        chart.maxHighlightDistance = 300f

        val xAxis = chart.xAxis

        val valueFormatter = ChartAxisValueFormatter().apply {
            setValue(date)
        }

        xAxis.valueFormatter = valueFormatter
        xAxis.isEnabled = true
        xAxis.textColor = context.resources.getColor(R.color.chart_font, null)
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.setDrawGridLines(false)
        xAxis.setLabelCount(6, true) //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
        xAxis.spaceMax = 18f


        val y: YAxis = chart.axisLeft
        y.setLabelCount(6, false)
        y.textColor = resources.getColor(R.color.chart_font, null)
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        y.setDrawGridLines(false)
        y.axisLineColor = backGround

        chart.setVisibleYRange(dataSet.yMax, dataSet.yMax, y.axisDependency)

        chart.setPinchZoom(true)

        val mv =
            CustomMarkerView(
                context,
                R.layout.marker_view
            )
                .apply{ chartView = chart }

        chart.marker = mv

        chart.setDrawMarkers(true)

        chart.isHighlightPerTapEnabled = true

        chart.axisRight.isEnabled = false

        chart.legend.isEnabled = false

//        chart.animateXY(2000, 2000)

        chart.invalidate()

//        chart.animateY(1100, Easing.EaseInOutQuad)
    }


    private fun calculateProduct(
        binding: StatFragmentBinding,
        realData: Map<String, Double>,
        products: List<Product>
    ): List<Double> {

        val currency = realData[CURRENCY[realData["currency"]!!.toInt()]]!!

        val currencyOption = realData["currency"]!!.toInt()

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
        viewModel.ratioMetal.value =
            listOf(goldCoin_Ratio, goldBar_Ratio, silverCoin_Ratio, silverBar_Ratio)

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
            binding.totalCurrency.text = CURRENCYSYMBOL[currencyOption]
            binding.totalPrice.text = priceToString(result_total, "PriceInt")
            binding.totalPlper.text = priceToString(total_Plper, "Pl")

            binding.plPrice.text = priceToString(result_totalPl, "PriceInt")
            binding.plCurrency.text = CURRENCYSYMBOL[currencyOption]

            binding.totalLayout.visibility = View.VISIBLE
        }

        if (result_goldCoin > 0) {
            binding.goldCoinCurrency.text = CURRENCYSYMBOL[currencyOption]
            binding.goldCoinPrice.text = priceToString(result_goldCoin, "PriceInt")
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
            binding.goldBarCurrency.text = CURRENCYSYMBOL[currencyOption]
            binding.goldBarPrice.text = priceToString(result_goldBar, "PriceInt")
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
            binding.silverCoinCurrency.text = CURRENCYSYMBOL[currencyOption]
            binding.silverCoinPrice.text = priceToString(result_silverCoin, "PriceInt")
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
            binding.silverBarCurrency.text = CURRENCYSYMBOL[currencyOption]
            binding.silverBarPrice.text = priceToString(result_silverBar, "PriceInt")
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

    private open class ChartAxisValueFormatter : ValueFormatter() {

        private lateinit var mValues: ArrayList<String>

        fun setValue(mValues: ArrayList<String>) {
            this.mValues = mValues
        }

        override fun getFormattedValue(value: Float): String {
            return mValues[value.toInt()]
        }
    }

    private open class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(
        context,
        layoutResource
    ) {
        private val tvContent: TextView = findViewById<View>(R.id.tvContent) as TextView

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            tvContent.text = String.format("%,.0f", e?.y) // set the entry-value as the display text
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
            super.getOffset().y = -(height.toFloat() - 6f)
            return super.getOffset()
        }
//        override fun getX(): Float {
//            return -(super.getWidth() / 2).toFloat()
//        }
//
//        override fun getY(): Float {
//            return super.getHeight().toFloat()
//        }

//        fun getYOffset(xpos: Float): Int { // this will center the marker-view horizontally
//            return -(width / 2)
//        }
//
//        fun getYOffset(ypos: Float): Int { // this will cause the marker-view to be above the selected value
//            return -height
//        }
    }
}


