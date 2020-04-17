@file:Suppress("DEPRECATION")

package com.gsgana.gsledger

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import com.gsgana.gsledger.databinding.FragmentWrite5Binding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.WriteViewModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


@Suppress("DEPRECATION")
class Write5Fragment : Fragment() {

    private val PREF_NAME = "01504f779d6c77df04"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"

    //    private val TIME_NAME = "6ck9uUlDuh7o6QKQFZ"
    private var option: Int? = null
    private var price1: String? = null
    private var price2: String? = null

    private var min: Float = 0f
    private var max: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel =
            ViewModelProviders.of(
                activity!!,
                InjectorUtils.provideWriteViewModelFactory(activity!!, null)
            )
                .get(WriteViewModel::class.java)

        option =
            activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(CURR_NAME, 0)

        val binding = DataBindingUtil.inflate<FragmentWrite5Binding>(
            inflater, R.layout.fragment_write5, container, false
        )
        binding.layout5.setOnClickListener {
            findNavController().navigate(R.id.action_write5Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()
        }
        binding.layout5r.setOnClickListener {
            findNavController().navigate(R.id.action_write5Fragment_to_homeViewPagerFragment)
            viewModel.initProduct()
        }

        binding.callbackSummit = object : CallbackSummit {
            override fun click() {
                price1 = if ("${binding.priceEditText1.text}" == "") {
                    "0"
                } else {
                    binding.priceEditText1.text.toString()
                }

                price2 = if ("${binding.priceEditText2.text}" == "") {
                    "0"
                } else {
                    binding.priceEditText2.text.toString()
                }

                binding.priceEditText1.isEnabled = false
                binding.priceEditText2.isEnabled = false
                binding.priceEditText3.isEnabled = false

                val price_buf = "${price1}.${price2}".toFloat()
                if (price_buf in min..max) {

                    binding.summitProgress.visibility = View.VISIBLE
                    binding.summitButton.isEnabled = false
                    binding.summitButton.text = ""

                    binding.priceEditText1.isEnabled = false
                    binding.priceEditText2.isEnabled = false
                    binding.priceEditText3.isEnabled = false

                    val test = viewModel.getdateField()?.split("/")
                    val date_buf = if (test.isNullOrEmpty()) {
                        val cal = Calendar.getInstance()
                        val _year = cal.get(Calendar.YEAR)
                        val _month = cal.get(Calendar.MONTH) + 1
                        val _date = cal.get(Calendar.DATE)
                        viewModel.getdateField()?: String.format(
                            "%04d%02d%02d", _year, _month, _date
                        )
                    } else {
                        String.format(
                            "%04d%02d%02d", test[0].toInt(), test[1].toInt(), test[2].toInt()
                        )
                    }

                    val jsonParam = JsonObject().apply {
                        addProperty("metal", viewModel.getmetalField1().toString())
                        addProperty("type1", viewModel.gettypeField1().toString())
                        addProperty("brand", viewModel.brand.value.toString())
                        addProperty("weight", viewModel.weightCalculator.value)
                        addProperty("quantity", viewModel.getquantityField())
                        addProperty("weightr", viewModel.weightUnit.value)
                        addProperty("packageType1", viewModel.getpackageTypeField().toString())
                        addProperty("grade", viewModel.getgradeField().toString())
                        addProperty("gradeNum", viewModel.getgradeNumField().toString())
                        addProperty("currency", viewModel.getcurrencyField().toString())
                        addProperty("year", viewModel.getyearSeriesField().toString())

                        price1 = if ("${binding.priceEditText1.text}" == "") {
                            "0"
                        } else {
                            binding.priceEditText1.text.toString()
                        }

                        price2 = if ("${binding.priceEditText2.text}" == "") {
                            "0"
                        } else {
                            binding.priceEditText2.text.toString()
                        }
                        addProperty(
                            "priceMerger",
                            "${price1}.${(price2)}"
                        )

                        addProperty("price", viewModel.price.value)
                        addProperty("date", date_buf)
                    }
                    val retrofit = Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create()).build()
                    val service = retrofit.create(ServerSummit::class.java)

                    val mUser = FirebaseAuth.getInstance().currentUser
                    mUser?.getIdToken(true)
                        ?.addOnCompleteListener { p0 ->
                            if (p0.isSuccessful) {
                                val idToken = p0.result?.token
                                service.postRequest(idToken!!, jsonParam)
                                    .enqueue(object : retrofit2.Callback<Data> {
                                        override fun onFailure(call: Call<Data>, t: Throwable) {
                                            Toast.makeText(
                                                activity,
                                                t.toString(),
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        }

                                        @SuppressLint("SetTextI18n")
                                        override fun onResponse(
                                            call: Call<Data>,
                                            response: Response<Data>
                                        ) {
                                            viewModel.setPriceTest(priceCalculate(binding).toString())
                                            viewModel.setregField(response.body()?.reg?.toFloat())
                                            viewModel.setcurField(response.body()?.cur?.toFloat())
                                            viewModel.setpre(response.body()?.pre?.toFloat())

                                            min = response.body()!!.min.toFloat()
                                            max = response.body()!!.max.toFloat()

                                            if ((min <= priceCalculate(binding)) &&
                                                (max >= priceCalculate(binding))
                                            ) {
                                                val imm =
                                                    context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                                imm.hideSoftInputFromWindow(view!!.windowToken, 0)
                                                findNavController().navigate(R.id.action_write5Fragment_to_write6Fragment)
                                            } else {
                                                binding.priceCur1.text =
                                                    CURRENCYSYMBOL[viewModel.getcurrencyField()
                                                        ?: 0]
                                                binding.priceCur2.text =
                                                    CURRENCYSYMBOL[viewModel.getcurrencyField()
                                                        ?: 0]
                                                binding.priceRange.visibility = View.VISIBLE
                                                binding.summitButton.text = "NEXT"
                                                binding.summitButton.isEnabled = true
                                                binding.summitProgress.visibility = View.GONE
                                                binding.priceMin.text =
                                                    String.format("%,.1f", min)
                                                binding.priceMax.text =
                                                    String.format("%,.1f", max)
                                                binding.priceEditText1.isEnabled = true
                                                binding.priceEditText2.isEnabled = true
                                                binding.priceEditText3.isEnabled = true
                                            }
                                        }
                                    })
                            }
                        }
                } else if (price_buf !in min..max) {

                    if (min == 0f && max == 0f) {
                        binding.summitProgress.visibility = View.VISIBLE
                        binding.summitButton.isEnabled = false
                        binding.summitButton.text = ""

                        val test = viewModel.getdateField()?.split("/")
                        val date_buf = if (test.isNullOrEmpty()) {
                            val cal = Calendar.getInstance()
                            val _year = cal.get(Calendar.YEAR)
                            val _month = cal.get(Calendar.MONTH) + 1
                            val _date = cal.get(Calendar.DATE)
                            viewModel.getdateField() ?: String.format(
                                "%04d%02d%02d", _year, _month, _date
                            )
                        } else {
                            String.format(
                                "%04d%02d%02d", test[0].toInt(), test[1].toInt(), test[2].toInt()
                            )
                        }

                        val jsonParam = JsonObject().apply {
                            addProperty("metal", viewModel.getmetalField1().toString())
                            addProperty("type1", viewModel.gettypeField1().toString())
                            addProperty("brand", viewModel.brand.value.toString())
                            addProperty("weight", viewModel.weightCalculator.value)
                            addProperty("quantity", viewModel.getquantityField())
                            addProperty("weightr", viewModel.weightUnit.value)
                            addProperty("packageType1", viewModel.getpackageTypeField().toString())
                            addProperty("grade", viewModel.getgradeField().toString())
                            addProperty("gradeNum", viewModel.getgradeNumField().toString())
                            addProperty("currency", viewModel.getcurrencyField().toString())
                            addProperty("year", viewModel.getyearSeriesField().toString())


                            price1 = if ("${binding.priceEditText1.text}" == "") {
                                "0"
                            } else {
                                binding.priceEditText1.text.toString()
                            }

                            price2 = if ("${binding.priceEditText2.text}" == "") {
                                "0"
                            } else {
                                binding.priceEditText2.text.toString()
                            }
                            addProperty(
                                "priceMerger",
                                "${price1}.${(price2)}"
                            )

                            addProperty("price", viewModel.price.value)
                            addProperty("date", date_buf)
                        }
                        val retrofit = Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create()).build()
                        val service = retrofit.create(ServerSummit::class.java)

                        val mUser = FirebaseAuth.getInstance().currentUser
                        mUser?.getIdToken(true)
                            ?.addOnCompleteListener { p0 ->
                                if (p0.isSuccessful) {
                                    val idToken = p0.result?.token
                                    service.postRequest(idToken!!, jsonParam)
                                        .enqueue(object : retrofit2.Callback<Data> {
                                            override fun onFailure(call: Call<Data>, t: Throwable) {
                                                Toast.makeText(
                                                    activity,
                                                    t.toString(),
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }

                                            @SuppressLint("SetTextI18n")
                                            override fun onResponse(
                                                call: Call<Data>,
                                                response: Response<Data>
                                            ) {

                                                viewModel.setPriceTest(priceCalculate(binding).toString())
                                                viewModel.setregField(response.body()?.reg?.toFloat())
                                                viewModel.setcurField(response.body()?.cur?.toFloat())
                                                viewModel.setpre(response.body()?.pre?.toFloat())
                                                min = response.body()!!.min.toFloat()
                                                max = response.body()!!.max.toFloat()

                                                if ((min <= priceCalculate(binding)) &&
                                                    (max >= priceCalculate(binding))
                                                ) {

                                                    val imm =
                                                        context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                                    imm.hideSoftInputFromWindow(
                                                        view!!.windowToken,
                                                        0
                                                    )
                                                    findNavController().navigate(R.id.action_write5Fragment_to_write6Fragment)
                                                } else {
                                                    binding.priceCur1.text =
                                                        CURRENCYSYMBOL[viewModel.getcurrencyField()
                                                            ?: 0]
                                                    binding.priceCur2.text =
                                                        CURRENCYSYMBOL[viewModel.getcurrencyField()
                                                            ?: 0]
                                                    binding.priceRange.visibility = View.VISIBLE
                                                    binding.summitButton.text = "NEXT"
                                                    binding.summitButton.isEnabled = true
                                                    binding.summitProgress.visibility = View.GONE
                                                    binding.priceMin.text =
                                                        String.format("%,.1f", min)
                                                    binding.priceMax.text =
                                                        String.format("%,.1f", max)
                                                }

                                            }
                                        })
                                } else {
                                    p0.exception
                                }
                            }

                        binding.priceEditText1.isEnabled = true
                        binding.priceEditText2.isEnabled = true
                        binding.priceEditText3.isEnabled = true

                    } else {

                        binding.priceEditText1.isEnabled = true
                        binding.priceEditText2.isEnabled = true
                        binding.priceEditText3.isEnabled = true

                        Toast.makeText(
                            context,
                            resources.getString(R.string.rangePrice),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        setSpinnerUi(binding, viewModel)


        return binding.root
    }


    private fun setSpinnerUi(
        binding: FragmentWrite5Binding,
        viewModel: WriteViewModel
    ) {
        val adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, CURRENCY
            )
        binding.currencySpinner1.adapter = adapter
        binding.currencySpinner1.dropDownVerticalOffset = 53f.dipToPixels().toInt()
        binding.currencySpinner1.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setcurrencyField( position)
                }
            }
        binding.currencySpinner1.setSelection(option ?: 0)
    }


    private fun Float.dipToPixels(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            resources.displayMetrics
        )
    }

    private fun priceCalculate(binding: FragmentWrite5Binding): Float {
        var text1 = binding.priceEditText1.text.toString()
        var text2 = binding.priceEditText2.text.toString()

        if (text1 == "") text1 = "0"
        if (text2 == "") text2 = "0"

        return "${text1}.${text2}".toFloat()
    }


    interface CallbackSummit {
        fun click()

    }
}