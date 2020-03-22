package com.gsgana.gsledger


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.firebase.auth.FirebaseAuth
import com.gsgana.gsledger.data.Product
import com.gsgana.gsledger.databinding.StatFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.CURRENCYSYMBOL
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.PACKAGENUM
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import java.util.*

class StatFragment : Fragment() {
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private lateinit var binding: StatFragmentBinding

    private lateinit var mAuth: FirebaseAuth
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"
    private lateinit var rgl: MutableList<Char>
    private val PREF_NAME = "01504f779d6c77df04"
    private var switchChart = false

    private lateinit var fm: FragmentManager

    private var ratio: List<Double>? = null

    private var currencyOption =
        activity?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)?.getInt(CURR_NAME, 0)

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

        viewModel?.realData?.observe(viewLifecycleOwner, Observer { realData ->
            if (!viewModel?.getProducts().value.isNullOrEmpty()) {
                val products = viewModel?.getProducts().value!!
                ratio = calculateProduct(binding, realData, products)
            }
        }
        )

        viewModel?.getProducts()?.observe(viewLifecycleOwner, Observer { products ->
            if (!viewModel?.realData.value.isNullOrEmpty()) {
                val realData = viewModel?.realData?.value!!
                val currencyOption = realData["currency"]!!.toInt()
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
        }
        )

        Handler().postDelayed({
            if (!ratio.isNullOrEmpty()) {
                if (!switchChart) setChart(context!!, binding, ratio!!) //if (context!=null)
            }
        }, 1800)

        return binding.root
    }

    interface Callback {
        fun click()

    }

    private fun setChart(
        context: Context,
        binding: StatFragmentBinding,
        ratio: List<Double>
    ) {
//        val white = ContextCompat.getColor(context, R.color.white)
//        val gray = ContextCompat.getColor(context, R.color.colorAccent)
//        val red = ContextCompat.getColor(context, R.color.red)
//        val green = ContextCompat.getColor(context, R.color.green)
        val chart_goldC = ContextCompat.getColor(context, R.color.chart_goldC)
        val chart_goldB = ContextCompat.getColor(context, R.color.chart_goldB)
        val chart_silverC = ContextCompat.getColor(context, R.color.chart_silverC)
        val chart_silverB = ContextCompat.getColor(context, R.color.chart_silverB)
        val backGround = ContextCompat.getColor(context, R.color.border_background)
        val duration = 530

        var chartData = mutableListOf(0f, 0f, 0f, 0f)
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
//        pieChart.setTransparentCircleColor(com.gsgana.gsledger.fragment.FirstFragment.gray)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.setCenterTextSize(18f)

        pieChart.holeRadius = 35f
        pieChart.transparentCircleRadius = 41f

        pieChart.setDrawCenterText(true)

        pieChart.rotationAngle = 0f

        // enable rotation of the pieChart by touch
        // enable rotation of the pieChart by touch
        pieChart.isRotationEnabled = false
        pieChart.isHighlightPerTapEnabled = false

        // add a selection listener

        // add a selection listener
        pieChart.animateXY(1100, 1100)

        // entry label styling
        // entry label styling
//        pieChart.setEntryLabelColor(com.gsgana.gsledger.fragment.FirstFragment.gray)
        pieChart.setEntryLabelTextSize(15f)

        val yvalues = ArrayList<PieEntry>()

        if (chartData?.get(0) != 0f) yvalues.add(PieEntry(chartData?.get(0) ?: 0f, "Gold Coin"))
        if (chartData?.get(1) != 0f) yvalues.add(PieEntry(chartData?.get(1) ?: 0f, "Gold Bar"))
        if (chartData?.get(2) != 0f) yvalues.add(PieEntry(chartData?.get(2) ?: 0f, "Silver Coin"))
        if (chartData?.get(3) != 0f) yvalues.add(PieEntry(chartData?.get(3) ?: 0f, "Silver Bar"))

        val dataSet = PieDataSet(yvalues, "")

        // add a lot of colors
        val colors = mutableListOf<Int>()
        if (chartData?.get(0) != 0f) colors.add(chart_goldC)
        if (chartData?.get(1) != 0f) colors.add(chart_goldB)
        if (chartData?.get(2) != 0f) colors.add(chart_silverC)
        if (chartData?.get(3) != 0f) colors.add(chart_silverB)

        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(11f)

//        data.setValueTextColor(com.gsgana.gsledger.fragment.FirstFragment.gray)
        pieChart.data = data

        // undo all highlights
        pieChart.highlightValues(null)

        val sumData = chartData?.sum()

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

    private fun setData(viewModel: HomeViewPagerViewModel?, binding: StatFragmentBinding) {
//        val realData = viewModel?.realData?.value!!
//        val products = viewModel?.products.value!!
//        val currencyOption = realData["currency"]!!.toInt()
//        calculateProduct(binding, realData, products, currencyOption)
    }


    private fun calculateProduct(
        binding: StatFragmentBinding,
        realData: Map<String, Double>,
        products: List<Product>
    ): List<Double> {

        val currency = realData[CURRENCY[realData["currency"]!!.toInt()]]!!
        val currencyOption = realData["currency"]!!.toInt()

        var goldCoin_Total = 0.0
        var goldBar_Total = 0.0
        var silverCoin_Total = 0.0
        var silverBar_Total = 0.0

        var goldCoin_BuyPrice = 0.0
        var goldBar_BuyPrice = 0.0
        var silverCoin_BuyPrice = 0.0
        var silverBar_BuyPrice = 0.0

        var goldCoin_Pl = 0.0
        var goldBar_Pl = 0.0
        var silverCoin_Pl = 0.0
        var silverBar_Pl = 0.0

        var goldCoin_PlPer = 0.0
        var goldBar_PlPer = 0.0
        var silverCoin_PlPer = 0.0
        var silverBar_PlPer = 0.0

        var goldCoin_Ratio = 0.0
        var goldBar_Ratio = 0.0
        var silverCoin_Ratio = 0.0
        var silverBar_Ratio = 0.0

        products?.forEach { product ->
            /* sum each metal and type  */
            if (!realData.isNullOrEmpty()) {
                if (product.metal == 0) {
                    if (product.type == 0) {
                        goldCoin_Total += realData["AU"]!! * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                        goldCoin_BuyPrice += product.price / realData[CURRENCY[product.currency]]!! * PACKAGENUM[product.packageType] * product.quantity
                        val test = goldCoin_Total
                        val test111 = goldCoin_BuyPrice
                    } else if (product.type == 1) {
                        goldBar_Total += realData["AU"]!! * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                        goldBar_BuyPrice += product.price / realData[CURRENCY[product.currency]]!! * PACKAGENUM[product.packageType] * product.quantity
                        val test1 = goldBar_Total
                        val test11 = goldBar_BuyPrice

                    }
                } else if (product.metal == 1) {
                    if (product.type == 0) {
                        silverCoin_Total += realData["AG"]!! * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                        silverCoin_BuyPrice += product.price / realData[CURRENCY[product.currency]]!! * PACKAGENUM[product.packageType] * product.quantity
                        val test2 = silverCoin_Total
                        val test21 = silverCoin_BuyPrice

                    } else if (product.type == 1) {
                        silverBar_Total += realData["AG"]!! * (1 + product.reg) * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                        silverBar_BuyPrice += product.price / realData[CURRENCY[product.currency]]!! * PACKAGENUM[product.packageType] * product.quantity
                        val test3 = silverBar_Total
                        val test31 = silverBar_BuyPrice
                    }
                }
            }
        }

        /*calculate Total*/
        val total = goldCoin_Total + goldBar_Total + silverCoin_Total + silverBar_Total
        val total_Pl =
            total - (goldCoin_BuyPrice + goldBar_BuyPrice + silverCoin_BuyPrice + silverBar_BuyPrice)
        val total_Plper = total_Pl / total * 100

        /*calculate Ratio*/
        goldCoin_Ratio = (goldCoin_Total / total)
        goldBar_Ratio = (goldBar_Total / total)
        silverCoin_Ratio = (silverCoin_Total / total)
        silverBar_Ratio = (silverBar_Total / total)

        /*calculate Price USD -> CURRENCY*/
        val result_total = total * currency
        val result_totalPl = total_Pl * currency
        val result_goldCoin = goldCoin_Total * currency
        val result_goldBar = goldBar_Total * currency
        val result_silverCoin = silverCoin_Total * currency
        val result_silverBar = silverBar_Total * currency

        val result_goldCoin_BuyPrice = goldCoin_BuyPrice * currency
        val result_goldBar_BuyPrice = goldBar_BuyPrice * currency
        val result_silverCoin_BuyPrice = silverCoin_BuyPrice * currency
        val result_silverBar_BuyPrice = silverBar_BuyPrice * currency

        /*calculate Pl*/
        goldCoin_Pl = result_goldCoin - result_goldCoin_BuyPrice
        goldBar_Pl = result_goldBar - result_goldBar_BuyPrice
        silverCoin_Pl = result_silverCoin - result_silverCoin_BuyPrice
        silverBar_Pl = result_silverBar - result_silverBar_BuyPrice

        /*calculate Pl*/
        goldCoin_PlPer = (result_goldCoin - result_goldCoin_BuyPrice) / result_goldCoin
        goldBar_PlPer = (result_goldBar - result_goldBar_BuyPrice) / result_goldBar
        silverCoin_PlPer = (result_silverCoin - result_silverCoin_BuyPrice) / result_silverCoin
        silverBar_PlPer = (result_silverBar - result_silverBar_BuyPrice) / result_silverBar

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
        } else {

        }

        if (result_goldCoin > 0) {
            binding.goldCoinCurrency.text = CURRENCYSYMBOL[currencyOption]
            binding.goldCoinPrice.text = priceToString(result_goldCoin, "PriceInt")
            binding.goldCoinPl.text = "${priceToString(goldCoin_PlPer, "Pl")} ${CURRENCYSYMBOL[currencyOption]} ${priceToString(goldCoin_Pl, "PricePl")}"
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
            binding.goldBarPl.text = "${priceToString(goldBar_PlPer, "Pl")} ${CURRENCYSYMBOL[currencyOption]} ${priceToString(goldBar_Pl, "PricePl")}"
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
            binding.silverCoinPl.text = "${priceToString(silverCoin_PlPer, "Pl")} ${CURRENCYSYMBOL[currencyOption]} ${priceToString(silverCoin_Pl, "PricePl")}"
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
            binding.silverBarPl.text = "${priceToString(silverBar_PlPer, "Pl")} ${CURRENCYSYMBOL[currencyOption]} ${priceToString(silverBar_Pl, "PricePl")}"
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
}


