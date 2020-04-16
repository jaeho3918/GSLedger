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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.StatFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.CURRENCYSYMBOL
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.PACKAGENUM
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.marker_view.view.*


@Suppress("UNCHECKED_CAST")
class StatFragment : Fragment() {
    private lateinit var binding: StatFragmentBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var rgl: MutableList<Char>
    private var switchChart = false

    private lateinit var fm: FragmentManager

    private var ratio: List<Double>? = null

    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(
            activity!!,
            activity!!.intent.getCharArrayExtra(KEY)
        )
    }
    private lateinit var functions: FirebaseFunctions// ...

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()
        fm = childFragmentManager
        fm.popBackStack()

        functions = FirebaseFunctions.getInstance()

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


        getChart().addOnCompleteListener { data ->
            val list1 = data.result?.get("value_AU") as ArrayList<Float>
            val list2 = data.result?.get("value_AG") as ArrayList<Float>
            val dates = data.result?.get("date") as ArrayList<String>

            if (context != null) setLineChart(context!!, binding, dates, list1)

//            if (context != null) setLineChart1(context!!, binding, dates, list2)
            list1.clear()
            list2.clear()
            dates.clear()
        }

        Handler().postDelayed({
            if (!ratio.isNullOrEmpty()) {
                if (!switchChart) setChart(requireContext(), binding, ratio!!) //if (context!=null)
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
            setViewPortOffsets(85f, 30f, 80f, 50f)
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
            legend.isEnabled = true
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
                setLabelCount(5, true)
                setDrawGridLines(true)
                spaceMax = 18f
            }

        chart.axisLeft
            .apply {
                textColor = resources.getColor(R.color.chart_font, null)
                gridColor = resources.getColor(R.color.chart_goldB, null)
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
                setDrawGridLines(true)
                setLabelCount(5, true)
                axisMaximum = dataSet.yMax + dataSet.yMax / 13
                axisMinimum = dataSet.yMin - dataSet.yMin / 13
                axisLineColor = backGround
            }

        val mv =
            CustomMarkerView(context, viewModel, R.layout.marker_view).apply { chartView = chart }

        chart.marker = mv
        chart.invalidate()
    }

//    private fun setLineChart1(
//        context: Context,
//        binding: StatFragmentBinding,
//        date: ArrayList<String>,
//        value: List<Float>
//    ) {
//        val chart_silverC = context.resources.getColor(R.color.chart_silverC, null)
//        val chart_silverB = context.resources.getColor(R.color.chart_silverB, null)
//        val backGround = context.resources.getColor(R.color.border_background, null)
//        val entries = arrayListOf<Entry>()
//
//        value.forEachIndexed { index, fl -> entries.add(Entry(index.toFloat(), fl)) }
//
//        val dataSet = LineDataSet(entries, "")
//            .apply {
//                setDrawFilled(true)
//                setDrawValues(false)
//                fillColor = chart_silverC
//                color = chart_silverC
//                setCircleColor(chart_silverB)
//                mode = LineDataSet.Mode.CUBIC_BEZIER
//                isHighlightEnabled = true
//                setDrawCircles(false)
//            }
//
//        val data = LineData(dataSet)
//
//        val chart = binding.silverChart.apply {
//            isEnabled = true
//            setData(data)
//            setViewPortOffsets(80f, 30f, 80f, 50f)
//            setBackgroundColor(backGround)
//            isDoubleTapToZoomEnabled = false
//            setDrawMarkers(true)
//            description.isEnabled = false
//            setTouchEnabled(true)
//            isDragEnabled = true
//            setScaleEnabled(false)
//            setDrawGridBackground(false)
//            maxHighlightDistance = 300f
//            setPinchZoom(true)
//            setDrawMarkers(true)
//            isHighlightPerTapEnabled = true
//            axisRight.isEnabled = false
//            legend.isEnabled = true
//            fitScreen()
//        }
//
//        val valueFormatter = IndexAxisValueFormatter(date)
//
//        chart.xAxis
//            .apply {
//                setValueFormatter(valueFormatter)
//                isEnabled = true
//                gridColor = resources.getColor(R.color.chart_silverB, null)
//                textColor = context.resources.getColor(R.color.chart_font, null)
//                position = XAxis.XAxisPosition.BOTTOM
//                setDrawGridLines(true)
//                setLabelCount(5, true)
//                spaceMax = 18f
//            }
//
//        chart.axisLeft
//            .apply {
//                textColor = resources.getColor(R.color.chart_font, null)
//                gridColor = resources.getColor(R.color.chart_silverB, null)
//                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
//                setDrawGridLines(true)
//                setLabelCount(5, true)
//                axisMaximum = dataSet.yMax + dataSet.yMax / 13
//                axisMinimum = dataSet.yMin - dataSet.yMin / 13
//                axisLineColor = backGround
//            }
//
//        val mv =
//            CustomMarkerView(context, R.layout.marker_view1).apply { chartView = chart }
//
//        chart.marker = mv
//        chart.invalidate()
//    }

    private fun calculateProduct(
        binding: StatFragmentBinding,
        realData: Map<String, Double>,
        products: List<Product>
    ): List<Double> {

        val currency = realData.getValue(CURRENCY[realData.getValue("currency").toInt()])

        val currencyOption = realData.getValue("currency").toInt()

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

    private class CustomMarkerView(
        context: Context,
        viewModel: HomeViewPagerViewModel,
        layoutResource: Int
    ) : MarkerView(
        context,
        layoutResource
    ) {
        private val tvContent: TextView = findViewById<View>(R.id.tvContent) as TextView
        private val viewModel: HomeViewPagerViewModel = viewModel

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            tvContent.text =
                String.format("%,.2f", e?.y) // set the entry-value as the display text
            tvCurrency.text = CURRENCYSYMBOL[viewModel.realData.value?.get("currency")?.toInt()?: 0]

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
            .getHttpsCallable("getChart")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.

                val result = task.result?.data as Map<*, ArrayList<*>>
                result
            }
    }
}