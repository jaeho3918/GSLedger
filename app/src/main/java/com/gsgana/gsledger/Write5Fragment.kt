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

        binding.moveTo5.setOnClickListener { findNavController().navigate(R.id.action_write5Fragment_to_write6Fragment) }



        binding.callback = object : Callback {
            override fun click() {
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
                    addProperty("type", viewModel.typeField1.value.toString())
                    addProperty("brand", viewModel.brandField1.value)
                    addProperty("weight", viewModel.weightCalculator.value)
                    addProperty("quantity", viewModel.quantityField.value)
                    addProperty("weightr", viewModel.weightUnit.value)
                    addProperty("packageType", viewModel.packageTypeField.value.toString())
                    addProperty("grade", viewModel.gradeField.value.toString())
                    addProperty("gradeNum", viewModel.gradeNumField.value.toString())
                    addProperty("currency", viewModel.currencyField.value.toString())
                    addProperty("priceMerger", viewModel.price.value)
                    addProperty("date", date_buf)
                }
                val retrofit = Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                val service = retrofit.create(ServerNetwork::class.java)

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
                                        binding.resultPrice.text = response.body().toString()
                                        viewModel.regField.value = response.body()?.reg?.toFloat()
                                        viewModel.curField.value = response.body()?.cur?.toFloat()

                                        binding.priceEditText1.visibility = View.VISIBLE
                                        binding.priceEditText2.visibility = View.VISIBLE
                                        binding.priceEditText2.visibility = View.VISIBLE
                                        binding.resultSubmit.visibility = View.GONE
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
        setEditTextUi(binding, viewModel)


        viewModel.price.observe(viewLifecycleOwner) {
            binding.totalCurrency.text = CURRENCYSYMBOL[viewModel.currencyField.value ?: 0]
            binding.totalPrice.text =
                (it.toFloat() * (viewModel.quantityField.value
                    ?: "0").toFloat() * viewModel.packageType.value!!
                        ).toString()
            viewModel.totalPrice.value = (it.toFloat() * (viewModel.quantityField.value
                ?: "0").toFloat() * viewModel.packageType.value!!
                    ).toString()
        }

        return binding.root
    }

    private fun setEditTextUi(
        binding: FragmentWrite5Binding,
        viewModel: WriteViewModel,
        array: Array<String>? = null,
        table: Array<String>? = null
    ) {
        binding.priceEditText1.setText(viewModel.priceField1.value ?: "")
        binding.priceEditText2.setText(viewModel.priceField2.value ?: "")

        binding.priceEditText1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var buf = viewModel.priceField.value

                if (s.toString() != "") {
                    viewModel.priceField1.value = s.toString()
                    buf?.set(0, s.toString())
                } else {
                    viewModel.priceField1.value = ""
                    buf?.set(0, null)
                }
                viewModel.priceField.value = buf
                buf = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )

        binding.priceEditText2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var buf = viewModel.priceField.value

                if (s.toString() != "") {
                    viewModel.priceField2.value = s.toString()
                    buf?.set(1, s.toString())
                } else {
                    viewModel.priceField2.value = ""
                    buf?.set(1, null)
                }
                viewModel.priceField.value = buf
                buf = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )
    }

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


    interface Callback {
        fun click()

    }
}