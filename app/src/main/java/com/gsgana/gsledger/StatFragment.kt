package com.gsgana.gsledger


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

        binding = StatFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel?.realData?.observe(viewLifecycleOwner, Observer { realData ->
            if (!viewModel?.products.value.isNullOrEmpty()) {
                val products = viewModel?.products.value!!
                val currencyOption = realData["currency"]!!.toInt()
                calculateProduct(binding, realData, products, currencyOption)
            }
        })

        viewModel?.products?.observe(viewLifecycleOwner, Observer { products ->
            if (!viewModel?.realData.value.isNullOrEmpty()) {
                val realData = viewModel?.realData?.value!!
                val currencyOption = realData["currency"]!!.toInt()
                calculateProduct(binding, realData, products, currencyOption)
            }
        })

        if (!viewModel?.products?.value.isNullOrEmpty()) {
            Handler().postDelayed({
                setData(viewModel, binding)
                setChart(context, viewModel, binding)
            }, 1800)
        } else {
            Handler().postDelayed({
                setData(viewModel, binding)
                setChart(context, viewModel, binding)
            }, 1800)
        }

        return binding.root
    }

    interface Callback {
        fun click()

    }

    private fun setChart(
        context: Context?,
        viewModel: HomeViewPagerViewModel?,
        binding: StatFragmentBinding
    ) {
        val white = ContextCompat.getColor(context!!, R.color.white)
        val gray = ContextCompat.getColor(context!!, R.color.colorAccent)
        val red = ContextCompat.getColor(context!!, R.color.red)
        val green = ContextCompat.getColor(context!!, R.color.green)
        val chart_goldC = ContextCompat.getColor(context!!, R.color.chart_goldC)
        val chart_goldB = ContextCompat.getColor(context!!, R.color.chart_goldB)
        val chart_silverC = ContextCompat.getColor(context!!, R.color.chart_silverC)
        val chart_silverB = ContextCompat.getColor(context!!, R.color.chart_silverB)
        val backGround = ContextCompat.getColor(context!!, R.color.border_background)
        val duration = 530

        val chartData = viewModel?.ratioMetal?.value

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

        if (chartData?.sum()!! > 0f) {
            pieChart.visibility = View.VISIBLE
            binding.chartprogress.visibility = View.GONE
            pieChart.invalidate()
        } else {
            binding.chartprogress.visibility = View.VISIBLE
            pieChart.visibility = View.GONE
        }
        pieChart.animateY(1400, Easing.EaseInOutQuad)
    }

    private fun setData(viewModel: HomeViewPagerViewModel?, binding: StatFragmentBinding) {
        val realData = viewModel?.realData?.value!!
        val products = viewModel?.products.value!!
        val currencyOption = realData["currency"]!!.toInt()
        calculateProduct(binding, realData, products, currencyOption)
    }


    private fun calculateProduct(
        binding: StatFragmentBinding,
        realData: Map<String, Double>,
        products: List<Product>,
        currencyOption: Int
    ) {

        val currency = realData[CURRENCY[currencyOption]]!!

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

        var goldCoin_Ratio = 0.0
        var goldBar_Ratio = 0.0
        var silverCoin_Ratio = 0.0
        var silverBar_Ratio = 0.0

        products?.forEach { product ->
            /* sum each metal and type  */
            if (!realData.isNullOrEmpty()) {
                if (product.metal == 0) {
                    if (product.type == 0) {
                        goldCoin_Total += realData["AU"]!! * product.reg * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                        goldCoin_BuyPrice += product.price / realData[CURRENCY[product.currency]]!!
                    } else if (product.type == 1) {
                        goldBar_Total += product.reg * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                        goldBar_BuyPrice += product.price / realData[CURRENCY[product.currency]]!!
                    }
                } else if (product.metal == 1) {
                    if (product.type == 0) {
                        silverCoin_Total += product.reg * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                        silverCoin_BuyPrice += product.price / realData[CURRENCY[product.currency]]!!
                    } else if (product.type == 1) {
                        silverBar_Total += product.reg * PACKAGENUM[product.packageType] * product.quantity * product.weightr * product.weight
                        silverBar_BuyPrice += product.price / realData[CURRENCY[product.currency]]!!
                    }
                }

                /*calculate Total*/
                val total = goldCoin_Total + goldBar_Total + silverCoin_Total + silverBar_Total
                val total_Pl =
                    total - goldCoin_BuyPrice + goldBar_BuyPrice + silverCoin_BuyPrice + silverBar_BuyPrice
                val total_plper = total_Pl / total

                /*calculate Pl*/
                goldCoin_Pl = goldCoin_Total - goldCoin_BuyPrice
                goldBar_Pl = goldBar_Total - goldBar_BuyPrice
                silverCoin_Pl = silverCoin_Total - silverCoin_BuyPrice
                silverBar_Pl = silverBar_Total - silverBar_BuyPrice

                /*calculate Ratio*/
                goldCoin_Ratio = goldCoin_Total / total
                goldBar_Ratio = goldBar_Total / total
                silverCoin_Ratio = silverCoin_Total / total
                silverBar_Ratio = silverBar_Total / total

                /*calculate Price USD -> CURRENCY*/
                val result_total = total * currency
                val result_goldCoin = goldCoin_Total * currency
                val result_goldBar = goldBar_Total * currency
                val result_silverCoin = silverCoin_Total * currency
                val result_silverBar = silverBar_Total * currency


                /* set Visible Layout */
                if (result_total > 0) {
                    binding.totalCurrency.text = CURRENCYSYMBOL[currencyOption]
                    binding.totalPrice.text = priceToString(result_total, "priceInt")
                    binding.totalPlper.text = priceToString(total_Pl, "Pl")

                    binding.totalLayout.visibility = View.VISIBLE
                    binding.statChart.visibility = View.GONE
                }
                if (result_goldCoin > 0) {
                    binding.goldCoinCurrency.text = CURRENCYSYMBOL[currencyOption]
                    binding.goldCoinPrice.text = priceToString(result_goldCoin, "priceInt")
                    binding.goldCoinPl.text = priceToString(goldCoin_Pl, "priceInt")

                    binding.goldCoinLayout.visibility = View.VISIBLE
                }
                if (result_goldBar > 0) {
                    binding.goldBarCurrency.text = CURRENCYSYMBOL[currencyOption]
                    binding.goldBarPrice.text = priceToString(result_goldBar, "priceInt")
                    binding.goldBarPl.text = priceToString(goldCoin_Pl, "priceInt")

                    binding.goldBarLayout.visibility = View.VISIBLE
                }
                if (result_silverCoin > 0) {
                    binding.silverCoinCurrency.text = CURRENCYSYMBOL[currencyOption]
                    binding.silverCoinPrice.text = priceToString(result_silverCoin, "priceInt")
                    binding.silverCoinPl.text = priceToString(silverCoin_Pl, "priceInt")

                    binding.silverCoinLayout.visibility = View.VISIBLE
                }
                if (result_silverBar > 0) {
                    binding.silverBarCurrency.text = CURRENCYSYMBOL[currencyOption]
                    binding.silverBarPrice.text = priceToString(result_silverBar, "priceInt")
                    binding.silverBarPl.text = priceToString(goldCoin_Pl, "priceInt")

                    binding.silverBarLayout.visibility = View.VISIBLE
                }

            }

        }
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
                    price < 0 -> {
                        "(+" + String.format("%,.2f", price) + "%)"
                    }
                    price > 0 -> {
                        "(" + String.format("%,.2f", price) + "%)"
                    }
                    else -> "( 0.00%)"
                }
            }
            else -> {

                ""
            }
        }
    }
}


