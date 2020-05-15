package com.gsgana.gsledger

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.gsgana.gsledger.databinding.StatFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.chart_layout.view.*


@Suppress("UNCHECKED_CAST", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class StatFragment : Fragment() {
    private lateinit var binding: StatFragmentBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var rgl: MutableList<Char>
    private var switchChart = false

    private var FLAG_GOLDCHART = false

    private var FLAG_SILVERCHART = false

    private lateinit var fm: FragmentManager

    private var ratio: List<Double>? = null

    private var chartCompareProduct = 0

    private lateinit var sf: SharedPreferences

    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(
            activity!!,
            activity!!.intent.getCharArrayExtra(KEY)
        )
    }
    private var currencyOption: Int = 0
    private var weightOption: Int = 0

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sf = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        currencyOption = sf.getInt(CURR_NAME, 0)
        weightOption = sf.getInt(WEIGHT_NAME, 0)

        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()
        fm = childFragmentManager
        fm.popBackStack()

        binding = StatFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner


        val MAX_HEIGHT = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            240f,
            getResources().getDisplayMetrics()
        ).toInt()
        val MIN_HEIGHT = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            130f,
            getResources().getDisplayMetrics()
        ).toInt()

        binding.chartLayout.goldZoom?.setOnClickListener {

            val firstConstraintSet = ConstraintSet()
            val secondConstraintSet = ConstraintSet()
            val constraintLayout = binding.chartLayout as ConstraintLayout
            firstConstraintSet.load(context!!, R.layout.chart_layout)
            secondConstraintSet.load(context!!, R.layout.chart_gold)

            if (!FLAG_GOLDCHART) {
                binding.chartLayout.goldZoom.background =
                    resources.getDrawable(R.drawable.ic_zoom_out_black_24dp, null)

                binding.chartLayout.goldShortChart.layoutParams.height = MAX_HEIGHT

                getShortLineGoldChartZoom(context!!, viewModel, binding, viewModel.getstackChartData())

                TransitionManager.beginDelayedTransition(constraintLayout)
                secondConstraintSet.applyTo(constraintLayout)
                FLAG_GOLDCHART = true

            } else {
                binding.chartLayout.goldZoom.background =
                    resources.getDrawable(R.drawable.ic_zoom_in_black_24dp, null)

                binding.chartLayout.goldShortChart.layoutParams.height = MIN_HEIGHT

                getShortLineGoldChart(context!!, viewModel, binding, viewModel.getstackChartData())

                TransitionManager.beginDelayedTransition(constraintLayout)
                firstConstraintSet.applyTo(constraintLayout)
                FLAG_GOLDCHART = false
            }
        }

        binding.chartLayout.silverZoom?.setOnClickListener {
            val firstConstraintSet = ConstraintSet()
            val secondConstraintSet = ConstraintSet()
            val constraintLayout = binding.chartLayout as ConstraintLayout
            firstConstraintSet.load(context!!, R.layout.chart_layout)
            secondConstraintSet.load(context!!, R.layout.chart_silver)

            if (!FLAG_SILVERCHART) {
                binding.chartLayout.silverZoom.background =
                    resources.getDrawable(R.drawable.ic_zoom_out_black_24dp, null)

                binding.chartLayout.silverShortChart.layoutParams.height = MAX_HEIGHT
                getShortLineSilverChartZoom(context!!, viewModel, binding, viewModel.getstackChartData())

                TransitionManager.beginDelayedTransition(constraintLayout)
                secondConstraintSet.applyTo(constraintLayout)
                FLAG_SILVERCHART = true
            } else {
                binding.chartLayout.silverZoom.background =
                    resources.getDrawable(R.drawable.ic_zoom_in_black_24dp, null)

                binding.chartLayout.silverShortChart.layoutParams.height = MIN_HEIGHT
                getShortLineSilverChart(context!!, viewModel, binding, viewModel.getstackChartData())

                TransitionManager.beginDelayedTransition(constraintLayout)
                firstConstraintSet.applyTo(constraintLayout)
                FLAG_SILVERCHART = false
            }
        }

        viewModel.getRealData().observe(viewLifecycleOwner, Observer { _ ->
            if (!viewModel.getProducts().value.isNullOrEmpty()) {
                val products = viewModel.getProducts().value!!
                ratio = getcalculateProduct(
                    viewModel,
                    binding,
                    products
                )
                if (ratio!![0] <= 0.0 && ratio!![1] <= 0.0) {
                    binding.totalGoldLayout.visibility = View.GONE
                } else if (ratio!![2] <= 0.0 && ratio!![3] <= 0.0) {
                    binding.totalSilverLayout.visibility = View.GONE
                }
            }
            getsetLabel(binding, viewModel)

            if (!(!FLAG_GOLDCHART || !FLAG_SILVERCHART)) {
                getShortLineGoldChart(context!!, viewModel, binding, viewModel.getstackChartData())
                getShortLineSilverChart(context!!, viewModel, binding, viewModel.getstackChartData())
            }

            Handler().postDelayed({
                getcalChart1(
                    binding,
                    viewModel,
                    chartCompareProduct,
                    context
                )
            }, 600)
        }
        )

        viewModel.getProducts().observe(viewLifecycleOwner, Observer { products ->
            Handler().postDelayed(
                {
                    if (!viewModel.getRealData().value.isNullOrEmpty()) {
                        ratio =
                            getcalculateProduct(
                                viewModel,
                                binding,
                                products
                            )
                        if (ratio!![0] <= 0.0 && ratio!![1] <= 0.0) {
                            binding.totalGoldLayout.visibility = View.GONE
                        } else if (ratio!![2] <= 0.0 && ratio!![3] <= 0.0) {
                            binding.totalSilverLayout.visibility = View.GONE
                        }

                        if (context != null) {
                            getsetChart(
                                context,
                                binding,
                                ratio!!
                            )
                        }

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
                }, 600
            )
        }
        )

        Handler().postDelayed(
            {
                if (!ratio.isNullOrEmpty()) {
                    if (!switchChart) getsetChart(
                        requireContext(),
                        binding,
                        ratio!!
                    ) //if (context!=null)
                }
            }, 600
        )

        viewModel.getstackChart().observe(viewLifecycleOwner, Observer {
            getShortLineGoldChart(context!!, viewModel, binding, it)
            getShortLineSilverChart(context!!, viewModel, binding, it)
            Handler().postDelayed({
                getcalChart1(
                    binding,
                    viewModel,
                    chartCompareProduct,
                    context
                )
                getsetLabel(binding, viewModel)
            }, 600)
        })


        binding.addBtn.setOnClickListener {
            if (it.findNavController().currentDestination?.id == R.id.homeViewPagerFragment) {
                findNavController()
                    .navigate(R.id.action_homeViewPagerFragment_to_write1Fragment)
            }
        }

        return binding.root
    }

}

