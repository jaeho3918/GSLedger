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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.databinding.StatFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.CURRENCYSYMBOL
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.PACKAGENUM
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModelFactory
import java.util.*

class StatFragment : Fragment() {
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private lateinit var binding: StatFragmentBinding

    private lateinit var viewModel: HomeViewPagerViewModel

    private lateinit var mAuth: FirebaseAuth
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"
    private lateinit var viewModelFactory: HomeViewPagerViewModelFactory
    private lateinit var rgl: MutableList<Char>

//    private val viewModel: HomeViewPagerViewModel by viewModels {
//        InjectorUtils.provideHomeViewPagerViewModelFactory(context!!,null)
//    }

    private val key = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()

        viewModel =
            activity?.run {
                ViewModelProviders.of(
                    activity!!
                )
                    .get(HomeViewPagerViewModel::class.java)
            }

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(USERS_DB_PATH).document(mAuth.currentUser?.uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                val test = document.data?.get("Rgl") as ArrayList<String>
                for (s in test) {
                    this.rgl.add(s.toCharArray()[0])
                }
                test.clear()


                viewModel?.realData?.observe(viewLifecycleOwner, Observer { realData ->
                    var reg1 = 0f
                    var reg2 = 0f
                    var weight = 1f
                    var quantity = 1
                    var _package = 1
                    var price1 = 0.0
                    var price2 = 0.0
                    var goldPlper = 0.0
                    var silverPlper = 0.0
                    var totalPlper = 0.0
                    var goldPl = 0.0
                    var silverPl = 0.0
                    var totalPl = 0.0
                    var goldPladd = 0.0
                    var silverPladd = 0.0
                    var totalPladd = 0.0
                    var metalPre1 = 0.0
                    var metalPre2 = 0.0

                    var goldPladd1 = 0.0
                    var silverPladd1 = 0.0
                    var totalPladd1 = 0.0

                    var uc = 0f
                    var ub = 0f
                    var gc = 0f
                    var gb = 0f

                    val metalPrice1 = realData["AU"] ?: 1.0
                    val metalPrice2 = realData["AG"] ?: 1.0

                    var currency = if (realData[CURR_NAME] == 0.0) {
                        1.0
                    } else {
                        realData[CURRENCY[(realData[CURR_NAME]?.toInt() ?: 0)]]
                    }

                    viewModel.products.value.also { products ->
                        products?.forEach { product ->
                            if (product.metal == 0) {
                                reg1 += ((1 + product.reg) * product.weightr * product.weight * product.quantity * PACKAGENUM[product.packageType])
                                price1 += product.price / (realData[CURRENCY[product.currency]]
                                    ?: 1.0)
//                        goldPladd += (1 + product.reg) * product.weightr * product.weight * metalPrice1
                                goldPladd += (reg1 * metalPrice1 * (realData[CURRENCY[product.currency]]
                                    ?: 1.0))
                                metalPre1 += product.prePrice

                                if (product.type == 0) {
                                    uc += product.prePrice
                                } else if (product.type == 1) {
                                    ub += product.prePrice
                                }

                            } else if (product.metal == 1) {
                                reg2 += ((1 + product.reg) * product.weightr * product.weight * product.quantity * PACKAGENUM[product.packageType])
                                price2 += product.price / (realData[CURRENCY[product.currency]]
                                    ?: 1.0)
//                        silverPladd += (1 + product.reg) * product.weightr * product.weight * metalPrice2
                                silverPladd += reg2 * metalPrice2 * (realData[CURRENCY[product.currency]]
                                    ?: 1.0)
                                metalPre2 += product.prePrice

                                if (product.type == 0) {
                                    gc += product.prePrice
                                } else if (product.type == 1) {
                                    gb += product.prePrice
                                }
                            }
                            goldPladd1 = (reg1 * metalPrice1 * (currency ?: 1.0)) - metalPre1
                            silverPladd1 = (reg2 * metalPrice2 * (currency ?: 1.0)) - metalPre2
                            totalPladd1 = goldPladd1 + silverPladd1


                            viewModel?.ratioMetal?.value = mutableListOf(
                                uc,
                                ub,
                                gc,
                                gb
                            )

                            _package = product.packageType

                        }
                        if (!products.isNullOrEmpty()) {
                            goldPlper = (goldPladd - price1) / (goldPladd)
                            silverPlper = (silverPladd - price2) / (silverPladd)
                            totalPlper =
                                (goldPladd + silverPladd - (price1 + price2)) / (goldPladd + silverPladd)

                            binding.goldPl.text = if (goldPladd1 < 0) {
                                String.format("(%,.2f)", goldPladd1)
                            } else {
                                String.format("(+%,.2f)", goldPladd1)
                            }

                            binding.silverPl.text = if (silverPladd1 < 0) {
                                String.format("(%,.2f)", silverPladd1)
                            } else {
                                String.format("(+%,.2f)", silverPladd1)
                            }
                            binding.totalPl.text = if (totalPladd1 < 0) {
                                String.format("(%,.2f)", totalPladd1)
                            } else {
                                String.format("(+%,.2f)", totalPladd1)
                            }

                            binding.goldCurrency.text =
                                CURRENCYSYMBOL[(realData[CURR_NAME]?.toInt() ?: 0)]
                            binding.silverCurrency.text =
                                CURRENCYSYMBOL[(realData[CURR_NAME]?.toInt() ?: 0)]
                            binding.totalCurrency.text =
                                CURRENCYSYMBOL[(realData[CURR_NAME]?.toInt() ?: 0)]

                            binding.goldlabel.text =
                                String.format("%,.0f", (reg1 * metalPrice1 * (currency ?: 1.0)))
                            binding.silverlabel.text =
                                String.format("%,.0f", (reg2 * metalPrice2 * (currency ?: 1.0)))
                            binding.totallabel.text = String.format(
                                "%,.0f",
                                (reg1 * metalPrice1 * (currency
                                    ?: 1.0) + reg2 * metalPrice2 * (currency
                                    ?: 1.0))
                            )

                        } else {
                            binding.goldlabel.text = ""
                            binding.silverlabel.text = ""
                            binding.totallabel.text = ""
                            binding.goldPl.text = ""
                            binding.silverPl.text = ""
                            binding.totalPl.text = ""
                            binding.goldCurrency.text = ""
                            binding.silverCurrency.text = ""
                            binding.totalCurrency.text = ""
                        }
                    }
                })

                viewModel?.products?.observe(viewLifecycleOwner, Observer {
                    var reg1 = 0f
                    var reg2 = 0f
                    var weight = 1f
                    var quantity = 1
                    var _package = 1
                    val realData = viewModel.realData.value ?: mapOf()
                    val metalPrice1 = realData.get("AU") ?: 1.0
                    val metalPrice2 = realData.get("AG") ?: 1.0
                    var price1 = 0.0
                    var price2 = 0.0
                    var goldPlper = 0.0
                    var silverPlper = 0.0
                    var totalPlper = 0.0
                    var goldPl = 0.0
                    var silverPl = 0.0
                    var totalPl = 0.0
                    var goldPladd = 0.0
                    var silverPladd = 0.0
                    var totalPladd = 0.0
                    var metalPre1 = 0.0
                    var metalPre2 = 0.0
                    var goldPladd1 = 0.0
                    var silverPladd1 = 0.0
                    var totalPladd1 = 0.0

                    var uc = 0f
                    var ub = 0f
                    var gc = 0f
                    var gb = 0f

                    var currency = if (realData[CURR_NAME] == 0.0) {
                        1.0
                    } else {
                        realData[CURRENCY[(realData[CURR_NAME]?.toInt() ?: 0)]]
                    }

                    viewModel.products.value.also { products ->
                        products?.forEach { product ->
                            if (product.metal == 0) {
                                reg1 += ((1 + product.reg) * product.weightr * product.weight * product.quantity * PACKAGENUM[product.packageType])
                                price1 += product.price / (realData[CURRENCY[product.currency]]
                                    ?: 1.0)
//                        goldPladd += (1 + product.reg) * product.weightr * product.weight * metalPrice1
                                goldPladd += (reg1 * metalPrice1 * (realData[CURRENCY[product.currency]]
                                    ?: 1.0))
                                metalPre1 += product.prePrice

                                if (product.type == 0) {
                                    uc += product.prePrice
                                } else if (product.type == 1) {
                                    ub += product.prePrice
                                }

                            } else if (product.metal == 1) {
                                reg2 += ((1 + product.reg) * product.weightr * product.weight * product.quantity * PACKAGENUM[product.packageType])
                                price2 += product.price / (realData[CURRENCY[product.currency]]
                                    ?: 1.0)
//                        silverPladd += (1 + product.reg) * product.weightr * product.weight * metalPrice2
                                silverPladd += reg2 * metalPrice2 * (realData[CURRENCY[product.currency]]
                                    ?: 1.0)
                                metalPre2 += product.prePrice

                                if (product.type == 0) {
                                    gc += product.prePrice
                                } else if (product.type == 1) {
                                    gb += product.prePrice
                                }
                            }
                            goldPladd1 = (reg1 * metalPrice1 * (currency ?: 1.0)) - metalPre1
                            silverPladd1 = (reg2 * metalPrice2 * (currency ?: 1.0)) - metalPre2
                            totalPladd1 = goldPladd1 + silverPladd1


                            viewModel?.ratioMetal?.value = mutableListOf(
                                uc,
                                ub,
                                gc,
                                gb
                            )

                            _package = product.packageType

                        }
                        if (!products.isNullOrEmpty()) {
                            goldPlper = (goldPladd - price1) / (goldPladd)
                            silverPlper = (silverPladd - price2) / (silverPladd)
                            totalPlper =
                                (goldPladd + silverPladd - (price1 + price2)) / (goldPladd + silverPladd)

                            goldPl = (goldPladd * goldPlper * (currency ?: 1.0))
                            silverPl = (silverPladd * silverPlper * (currency ?: 1.0))
                            totalPl = ((goldPladd + silverPladd) * totalPlper * (currency ?: 1.0))
                            totalPladd = goldPladd + silverPladd
                            binding.goldPl.text = String.format("(%,.2f)", goldPladd)
                            binding.silverPl.text = String.format("(%,.2f)", silverPladd)
                            binding.totalPl.text = String.format("(%,.2f)", totalPladd)

                            binding.goldCurrency.text =
                                CURRENCYSYMBOL[(realData[CURR_NAME]?.toInt() ?: 0)]
                            binding.silverCurrency.text =
                                CURRENCYSYMBOL[(realData[CURR_NAME]?.toInt() ?: 0)]
                            binding.totalCurrency.text =
                                CURRENCYSYMBOL[(realData[CURR_NAME]?.toInt() ?: 0)]

                            binding.goldlabel.text =
                                String.format("%,.0f", (reg1 * metalPrice1 * (currency ?: 1.0)))
                            binding.silverlabel.text =
                                String.format("%,.0f", (reg2 * metalPrice2 * (currency ?: 1.0)))
                            binding.totallabel.text = String.format(
                                "%,.0f",
                                (reg1 * metalPrice1 * (currency
                                    ?: 1.0) + reg2 * metalPrice2 * (currency
                                    ?: 1.0))
                            )

                        } else {
                            binding.goldlabel.text = ""
                            binding.silverlabel.text = ""
                            binding.totallabel.text = ""
                            binding.goldPl.text = ""
                            binding.silverPl.text = ""
                            binding.totalPl.text = ""
                            binding.goldCurrency.text = ""
                            binding.silverCurrency.text = ""
                            binding.totalCurrency.text = ""

                        }
                    }


                })
                if (!viewModel?.products?.value.isNullOrEmpty()) {
                    Handler().postDelayed({
                        setData(viewModel, binding)
                        setChart(context, viewModel, binding)
                    }, 1800)
                } else {
                    setData(viewModel, binding)
                    Handler().postDelayed({
                        setChart(context, viewModel, binding)
                    }, 1800)
                }

            }
        binding = StatFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

//        binding.moveBtn.setOnClickListener {
//            findNavController()
//                .navigate(R.id.action_homeViewPagerFragment_to_write1Fragment)
//        }


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

        val chart_goldC = ContextCompat.getColor(context!!, R.color.chart_goldC)
        val chart_goldB = ContextCompat.getColor(context!!, R.color.chart_goldB)
        val chart_silverC = ContextCompat.getColor(context!!, R.color.chart_silverC)
        val chart_silverB = ContextCompat.getColor(context!!, R.color.chart_silverB)
        val duration = 530

        val chartData = viewModel?.ratioMetal?.value

        val pieChart = binding.statChart
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false

        pieChart.dragDecelerationFrictionCoef = 0.95f

        pieChart.isDrawHoleEnabled = true
//        pieChart.setHoleColor(com.gsgana.gsledger.fragment.FirstFragment.gray)
//
//        pieChart.setTransparentCircleColor(com.gsgana.gsledger.fragment.FirstFragment.gray)
        pieChart.setTransparentCircleAlpha(110)

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
            binding.graphprogress.visibility = View.GONE
            pieChart.invalidate()
        } else {
            pieChart.visibility = View.GONE
        }
        pieChart.animateY(1400, Easing.EaseInOutQuad)
    }

    private fun setData(viewModel: HomeViewPagerViewModel?, binding: StatFragmentBinding) {
        var reg1 = 0f
        var reg2 = 0f
        var weight = 1f
        var quantity = 1
        var _package = 1
        val realData = viewModel?.realData?.value ?: mapOf()
        val metalPrice1 = realData.get("AU") ?: 1.0
        val metalPrice2 = realData.get("AG") ?: 1.0
        var price1 = 0.0
        var price2 = 0.0
        var goldPlper = 0.0
        var silverPlper = 0.0
        var totalPlper = 0.0
        var goldPl = 0.0
        var silverPl = 0.0
        var totalPl = 0.0
        var goldPladd = 0.0
        var silverPladd = 0.0
        var totalPladd = 0.0
        var metalPre1 = 0.0
        var metalPre2 = 0.0
        var goldPladd1 = 0.0
        var silverPladd1 = 0.0
        var totalPladd1 = 0.0

        var uc = 0f
        var ub = 0f
        var gc = 0f
        var gb = 0f

        var currency = if (realData[CURR_NAME] == 0.0) {
            1.0
        } else {
            realData[CURRENCY[(realData[CURR_NAME]?.toInt() ?: 0)]]
        }

        viewModel?.products?.value.also { products ->
            products?.forEach { product ->
                if (product.metal == 0) {
                    reg1 += ((1 + product.reg) * product.weightr * product.weight * product.quantity * PACKAGENUM[product.packageType])
                    price1 += product.price / (realData[CURRENCY[product.currency]] ?: 1.0)
//                        goldPladd += (1 + product.reg) * product.weightr * product.weight * metalPrice1
                    goldPladd += (reg1 * metalPrice1 * (realData[CURRENCY[product.currency]]
                        ?: 1.0))
                    metalPre1 += product.prePrice

                    if (product.type == 0) {
                        uc += product.prePrice
                    } else if (product.type == 1) {
                        ub += product.prePrice
                    }

                } else if (product.metal == 1) {
                    reg2 += ((1 + product.reg) * product.weightr * product.weight * product.quantity * PACKAGENUM[product.packageType])
                    price2 += product.price / (realData[CURRENCY[product.currency]] ?: 1.0)
//                        silverPladd += (1 + product.reg) * product.weightr * product.weight * metalPrice2
                    silverPladd += reg2 * metalPrice2 * (realData[CURRENCY[product.currency]]
                        ?: 1.0)
                    metalPre2 += product.prePrice

                    if (product.type == 0) {
                        gc += product.prePrice
                    } else if (product.type == 1) {
                        gb += product.prePrice
                    }
                }
                goldPladd1 = (reg1 * metalPrice1 * (currency ?: 1.0)) - metalPre1
                silverPladd1 = (reg2 * metalPrice2 * (currency ?: 1.0)) - metalPre2
                totalPladd1 = goldPladd1 + silverPladd1


                viewModel?.ratioMetal?.value = mutableListOf(
                    uc,
                    ub,
                    gc,
                    gb
                )

                _package = product.packageType

            }
            if (!products.isNullOrEmpty()) {
                goldPlper = (goldPladd - price1) / (goldPladd)
                silverPlper = (silverPladd - price2) / (silverPladd)
                totalPlper =
                    (goldPladd + silverPladd - (price1 + price2)) / (goldPladd + silverPladd)

                goldPl = (goldPladd * goldPlper * (currency ?: 1.0))
                silverPl = (silverPladd * silverPlper * (currency ?: 1.0))
                totalPl = ((goldPladd + silverPladd) * totalPlper * (currency ?: 1.0))
                totalPladd = goldPladd + silverPladd
                binding.goldPl.text = String.format("(%,.2f)", goldPladd)
                binding.silverPl.text = String.format("(%,.2f)", silverPladd)
                binding.totalPl.text = String.format("(%,.2f)", totalPladd)

                binding.goldCurrency.text = CURRENCYSYMBOL[(realData[CURR_NAME]?.toInt() ?: 0)]
                binding.silverCurrency.text =
                    CURRENCYSYMBOL[(realData[CURR_NAME]?.toInt() ?: 0)]
                binding.totalCurrency.text =
                    CURRENCYSYMBOL[(realData[CURR_NAME]?.toInt() ?: 0)]

                binding.goldlabel.text =
                    String.format("%,.0f", (reg1 * metalPrice1 * (currency ?: 1.0)))
                binding.silverlabel.text =
                    String.format("%,.0f", (reg2 * metalPrice2 * (currency ?: 1.0)))
                binding.totallabel.text = String.format(
                    "%,.0f",
                    (reg1 * metalPrice1 * (currency ?: 1.0) + reg2 * metalPrice2 * (currency
                        ?: 1.0))
                )
            }

            binding.goldlabel.visibility = View.VISIBLE
            binding.silverlabel.visibility = View.VISIBLE
            binding.totallabel.visibility = View.VISIBLE

            binding.goldCurrency.visibility = View.VISIBLE
            binding.silverCurrency.visibility = View.VISIBLE
            binding.totalCurrency.visibility = View.VISIBLE

            binding.goldPl.visibility = View.VISIBLE
            binding.silverPl.visibility = View.VISIBLE
            binding.totalPl.visibility = View.VISIBLE

            binding.goldprogress.visibility = View.GONE
            binding.silverprogress.visibility = View.GONE
            binding.totalprogress.visibility = View.GONE
        }
    }
}

