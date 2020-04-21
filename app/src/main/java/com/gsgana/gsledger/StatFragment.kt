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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.gsgana.gsledger.databinding.StatFragmentBinding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
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


    private val PREF_NAME = "01504f779d6c77df04"
    private lateinit var sf : SharedPreferences
    private val TODAY_NAME = "0d07f05fd0c595f615"

    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(
            activity!!,
            activity!!.intent.getCharArrayExtra(KEY)
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sf = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        rgl = mutableListOf()
        mAuth = FirebaseAuth.getInstance()
        fm = childFragmentManager
        fm.popBackStack()



        binding = StatFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner


        val today = sf.getString(TODAY_NAME,"")
        binding.todayLabel.text = today

        viewModel.getRealData().observe(viewLifecycleOwner, Observer { realData ->
            if (!viewModel.getProducts().value.isNullOrEmpty()) {
                val products = viewModel.getProducts().value!!
                ratio = getcalculateProduct(
                    viewModel,
                    binding,
                    realData,
                    products
                )
            }
        }
        )

        viewModel.getProducts().observe(viewLifecycleOwner, Observer { products ->
            Handler().postDelayed(
                {
                    if (!viewModel.getRealData().value.isNullOrEmpty()) {
                        val realData = viewModel.getRealData().value!!
                        ratio =
                            getcalculateProduct(
                                viewModel,
                                binding,
                                realData,
                                products
                            )
                        if (context !=null)
                        {
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

                    if (chartCompareProduct != products.size) {
                        getcalChart2(
                            products,
                            context,
                            binding,
                            viewModel,
                            chartCompareProduct
                        )
                    }
                }, 600
            )
        }
        )

        viewModel.getCurrencyOption().observe(viewLifecycleOwner, Observer
        {
            getcalChart1(
                binding,
                viewModel,
                chartCompareProduct,
                context
            )
        })


        Handler().postDelayed(
            {
                if (!ratio.isNullOrEmpty()) {
                    if (!switchChart) getsetChart(
                        requireContext(),
                        binding,
                        ratio!!
                    ) //if (context!=null)
                }
            }, 2400
        )

        binding.addBtn.setOnClickListener {
            findNavController()
                .navigate(R.id.action_homeViewPagerFragment_to_write1Fragment)
        }


        return binding.root
    }

}
 fun getChart(): Task<Map<*, ArrayList<*>>> {

    // Create the arguments to the callable function.
    lateinit var functions: FirebaseFunctions// ...

    functions = FirebaseFunctions.getInstance()

    val data = hashMapOf(
        "date" to listOf<String>(),
        "value_AU" to listOf<Float>(),
        "value_AG" to listOf<Float>()
    )
    return functions
        .getHttpsCallable("getShortChart")
        .call(data)
        .continueWith { task ->
            // This continuation runs on either success or failure, but if the task
            // has failed then result will throw an Exception which will be
            // propagated down.

            val result = task.result?.data as Map<*, ArrayList<*>>
            result
        }
}