package com.gsgana.gsledger

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.gsgana.gsledger.databinding.StatFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.stat_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


@Suppress("UNCHECKED_CAST", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class StatFragment : Fragment() {
    private lateinit var binding: StatFragmentBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var rgl: MutableList<Char>
    private var switchChart = false

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

//        val today = sf.getString(TODAY_NAME, "")
//        binding.todayLabel.text = today

        binding.moveToChart.setOnClickListener {
            findNavController()
                .navigate(R.id.action_homeViewPagerFragment_to_chartFragment)
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
            getsetLabel(binding,viewModel)

            getShortLineGoldChart(context!!, viewModel, binding, viewModel.getchartData())
            getShortLineSilverChart(context!!, viewModel, binding, viewModel.getchartData())
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

        viewModel.getchart().observe(viewLifecycleOwner, Observer {
            getShortLineGoldChart(context!!, viewModel, binding, it)
            getShortLineSilverChart(context!!, viewModel, binding, it)
            Handler().postDelayed({
                getcalChart1(
                    binding,
                    viewModel,
                    chartCompareProduct,
                    context
                )
                getsetLabel(binding,viewModel)
            }, 600)
        })


        binding.addBtn.setOnClickListener {
            findNavController()
                .navigate(R.id.action_homeViewPagerFragment_to_write1Fragment)
        }

        return binding.root
    }

}

