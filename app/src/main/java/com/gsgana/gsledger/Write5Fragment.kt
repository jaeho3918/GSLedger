package com.gsgana.gsledger

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import com.gsgana.gsledger.databinding.FragmentWrite3Binding
import com.gsgana.gsledger.databinding.FragmentWrite4Binding
import com.gsgana.gsledger.databinding.FragmentWrite5Binding
import com.gsgana.gsledger.utilities.*
import com.gsgana.gsledger.viewmodels.WriteViewModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class Write5Fragment : Fragment() {

    private val PREF_NAME = "01504f779d6c77df04"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private var option: Int? = null
    private var price1: Float? = null
    private var price2: Float? = null
    private var priceAvrString: String? = null
    private var priceAvrFloat: Float? = null


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
//
//        binding.moveTo5.setOnClickListener { findNavController().navigate(R.id.action_write5Fragment_to_write6Fragment) }


//        binding.callbackRequest = object : CallbackRequest {
//            override fun click() {
//                binding.requestButton.isEnabled = false
//                binding.requestButton.text = ""
//                binding.requestProgress.visibility = View.VISIBLE
//
//                val test = viewModel.dateField.value?.split("/")
//                var date_buf = if (test.isNullOrEmpty()) {
//                    val cal = Calendar.getInstance()
//                    val _year = cal.get(Calendar.YEAR)
//                    val _month = cal.get(Calendar.MONTH) + 1
//                    val _date = cal.get(Calendar.DATE)
//                    viewModel.dateField.value ?: String.format(
//                        "%04d%02d%02d", _year, _month, _date
//                    )
//                } else {
//                    String.format(
//                        "%04d%02d%02d", test[0].toInt(), test[1].toInt(), test[2].toInt()
//                    )
//                }
//
//                val jsonParam = JsonObject().apply {
//                    addProperty("metal", viewModel.metalField1.value.toString())
//                    addProperty("type1", viewModel.typeField1.value.toString())
//                    addProperty("brand", viewModel.brandField1.value)
//                    addProperty("weight", viewModel.weightCalculator.value)
//                    addProperty("quantity", viewModel.quantityField.value)
//                    addProperty("weightr", viewModel.weightUnit.value)
//                    addProperty("packageType1", viewModel.packageTypeField.value.toString())
//                    addProperty("grade", viewModel.gradeField.value.toString())
//                    addProperty("gradeNum", viewModel.gradeNumField.value.toString())
//                    addProperty("currency", viewModel.currencyField.value.toString())
//                    addProperty("priceMerger", viewModel.price.value)
//                    addProperty("date", date_buf)
//                }
//                val retrofit = Retrofit.Builder()
//                    .baseUrl(URL)
//                    .addConverterFactory(GsonConverterFactory.create()).build()
//                val service = retrofit.create(ServerRequest::class.java)
//
//                val mUser = FirebaseAuth.getInstance().currentUser
//                mUser?.getIdToken(true)
//                    ?.addOnCompleteListener { p0 ->
//                        if (p0.isSuccessful) {
//                            val idToken = p0.result?.token
//
//                            service.postRequest(idToken!!, jsonParam)
//                                ?.enqueue(object : retrofit2.Callback<DataRequest> {
//                                    override fun onFailure(call: Call<DataRequest>, t: Throwable) {
//                                        Toast.makeText(activity, t.toString(), Toast.LENGTH_LONG)
//                                            .show()
//                                    }
//
//                                    override fun onResponse(
//                                        call: Call<DataRequest>,
//                                        response: Response<DataRequest>
//                                    ) {
//                                        Toast.makeText(
//                                            activity,
//                                            response.body().toString(),
//                                            Toast.LENGTH_LONG
//                                        ).show()
//
//                                        price1 = response.body()?.price1!!.toFloat()
//                                        price2 = response.body()?.price2!!.toFloat()
//                                        priceAvrString =
//                                            String.format("%,.2f", (price1!! + price2!!) / 2)
//                                        priceAvrFloat = priceAvrString!!.replace(",", "").toFloat()
//
//                                        val splitPrice =
//                                            priceAvrString!!.replace(",", "").split(".")
//                                        if (splitPrice!!.size != 1) {
//                                            viewModel.priceField.value =
//                                                splitPrice!!.toMutableList()
//                                            binding.priceEditText1.setText(splitPrice!![0])
//                                            binding.priceEditText2.setText(splitPrice!![1])
//                                        } else if (splitPrice!!.size == 1) {
//                                            viewModel.priceField.value =
//                                                splitPrice!!.toMutableList()
//                                            binding.priceEditText1.setText(splitPrice!![0])
//                                            binding.priceEditText2.setText("00")
//                                        }
//
//                                        binding.priceEditText1.visibility = View.VISIBLE
//                                        binding.priceEditText2.visibility = View.VISIBLE
//                                        binding.priceEditText3.visibility = View.VISIBLE
//                                        binding.requestButton.visibility = View.GONE
//                                        binding.requestProgress.visibility = View.GONE
//                                        binding.summitButton.isEnabled = true
//
//                                        binding.currencySpinner1.visibility = View.GONE
//                                        binding.currencyTextView1.visibility = View.VISIBLE
//                                        binding.currencyTextView1.text =
//                                            CURRENCY[viewModel.currencyField.value!!]
//                                    }
//                                })
//                        } else {
//                            p0.exception
//                            Toast.makeText(activity, "Response?", Toast.LENGTH_LONG).show()
//                        }
//                    }
//            }
//        }
        binding.callbackSummit = object : CallbackSummit {
            override fun click() {
                binding.summitProgress.visibility = View.VISIBLE
                binding.summitButton.isEnabled = false
                binding.summitButton.text = ""

                val test = viewModel.dateField.value?.split("/")
                var date_buf = if (test.isNullOrEmpty()) {
                    val cal = Calendar.getInstance()
                    val _year = cal.get(Calendar.YEAR)
                    val _month = cal.get(Calendar.MONTH) + 1
                    val _date = cal.get(Calendar.DATE)
                    viewModel.dateField.value ?: String.format(
                        "%04d%02d%02d", _year, _month, _date
                    )
                } else {

                    String.format(
                        "%04d%02d%02d", test[0].toInt(), test[1].toInt(), test[2].toInt()
                    )
                }

                val jsonParam = JsonObject().apply {
                    addProperty("metal", viewModel.metalField1.value.toString())
                    addProperty("type1", viewModel.typeField1.value.toString())
                    addProperty("brand", viewModel.brandField1.value)
                    addProperty("weight", viewModel.weightCalculator.value)
                    addProperty("quantity", viewModel.quantityField.value)
                    addProperty("weightr", viewModel.weightUnit.value)
                    addProperty("packageType1", viewModel.packageTypeField.value.toString())
                    addProperty("grade", viewModel.gradeField.value.toString())
                    addProperty("gradeNum", viewModel.gradeNumField.value.toString())
                    addProperty("currency", viewModel.currencyField.value.toString())
                    addProperty(
                        "priceMerger",
                        "${binding.priceEditText1.text}.${(binding.priceEditText2.text ?: 0)}"
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
                                ?.enqueue(object : retrofit2.Callback<Data> {
                                    override fun onFailure(call: Call<Data>, t: Throwable) {
                                        Toast.makeText(activity, t.toString(), Toast.LENGTH_LONG)
                                            .show()
                                    }

                                    override fun onResponse(
                                        call: Call<Data>,
                                        response: Response<Data>
                                    ) {
                                        viewModel.priceTest = priceCalculate(binding).toString()
                                        viewModel.regField.value = response.body()?.reg?.toFloat()
                                        viewModel.curField.value = response.body()?.cur?.toFloat()
                                        viewModel.pre.value = response.body()?.pre?.toFloat()
                                        val min = response.body()?.min?.toFloat()
                                        val max = response.body()?.max?.toFloat()

                                        if ((min!! <= priceCalculate(binding)) &&
                                            (max!! >= priceCalculate(binding))
                                        ) {
                                            findNavController().navigate(R.id.action_write5Fragment_to_write6Fragment)
                                        } else {
                                            binding.priceCur1.text = CURRENCYSYMBOL[viewModel.currencyField.value?:0]
                                            binding.priceCur2.text = CURRENCYSYMBOL[viewModel.currencyField.value?:0]
                                            binding.priceRange.visibility = View.VISIBLE
                                            binding.summitButton.text = "NEXT"
                                            binding.summitButton.isEnabled = true
                                            binding.summitProgress.visibility = View.GONE
                                            binding.priceMin.text = String.format("%,.0f", min)
                                            binding.priceMax.text = String.format("%,.0f", max)
                                        }

                                    }
                                })
                        } else {
                            p0.exception
                            Toast.makeText(activity, "Response?", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        setSpinnerUi(binding, viewModel)

//        setEditTextUi(binding, viewModel)


//        viewModel.price.observe(viewLifecycleOwner) {
//            binding.totalCurrency.text = CURRENCYSYMBOL[viewModel.currencyField.value ?: 0]
//            binding.totalPrice.text =
//                (it.toFloat() * (viewModel.quantityField.value
//                    ?: "0").toFloat() * viewModel.packageType.value!!
//                        ).toString()
//            viewModel.totalPrice.value = (it.toFloat() * (viewModel.quantityField.value
//                ?: "0").toFloat() * viewModel.packageType.value!!
//                    ).toString()
//        }

        return binding.root
    }

//    private fun setEditTextUi(
//        binding: FragmentWrite5Binding,
//        viewModel: WriteViewModel,
//        array: Array<String>? = null,
//        table: Array<String>? = null
//    ) {
//
//        binding.priceEditText1.setText(viewModel.priceField1.value ?: "")
//        binding.priceEditText2.setText(viewModel.priceField2.value ?: "")
//
//        binding.priceEditText1.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                var buf = viewModel.priceField.value
//
//                if (s.toString() != "") {
//                    viewModel.priceField1.value = s.toString()
//                    buf?.set(0, s.toString())
//                } else {
//                    viewModel.priceField1.value = ""
//                    buf?.set(0, null)
//                }
//                viewModel.priceField.value = buf
//                buf = null
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        }
//        )
//
//        binding.priceEditText2.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                var buf = viewModel.priceField.value
//
//                if (s.toString() != "") {
//                    viewModel.priceField2.value = s.toString()
//                    buf?.set(1, s.toString())
//                } else {
//                    viewModel.priceField2.value = ""
//                    buf?.set(1, null)
//                }
//                viewModel.priceField.value = buf
//                buf = null
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        }
//        )
//    }

    private fun setSpinnerUi(
        binding: FragmentWrite5Binding,
        viewModel: WriteViewModel
    ) {
        var adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, CURRENCY
            )
        binding.currencySpinner1.adapter = adapter
        binding.currencySpinner1.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.currencySpinner1.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.currencyField.value = position
                }
            }
        binding.currencySpinner1.setSelection(option ?: 0)
    }


    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
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

    interface CallbackRequest {
        fun click()

    }

    interface CallbackSummit {
        fun click()

    }
}