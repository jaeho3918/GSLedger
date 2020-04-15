package com.gsgana.gsledger

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.databinding.AdsAndOptionFragmentBinding
import com.gsgana.gsledger.utilities.CURRENCY
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.utilities.WEIGHTUNIT
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel
import kotlinx.android.synthetic.main.ads_and_option_fragment.*


class AdsAndOptionFragment : Fragment() {
    private val PREF_NAME = "01504f779d6c77df04"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val WEIGHT_NAME = "f79604050dfc500715"
    private lateinit var binding: AdsAndOptionFragmentBinding
    private lateinit var googleSigninClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mAuth: FirebaseAuth
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"

    private lateinit var option: SharedPreferences
    private lateinit var adapter: ArrayAdapter<String>

    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(
            activity!!,
            charArrayOf(
                "q"[0],
                "0"[0],
                "J"[0],
                "2"[0],
                "3"[0],
                "o"[0],
                "1"[0],
                "m"[0],
                "D"[0],
                "E"[0],
                "Y"[0],
                "y"[0],
                "X"[0],
                "j"[0],
                "3"[0],
                "Q"[0],
                "s"[0],
                "h"[0],
                "E"[0],
                "l"[0],
                "8"[0],
                "n"[0],
                "j"[0],
                "Y"[0],
                "B"[0],
                "P"[0],
                "J"[0],
                "C"[0],
                "E"[0]
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = AdsAndOptionFragmentBinding.inflate(inflater, container, false)

        option = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        setSpinner(binding, viewModel)

        binding.delBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getString(R.string.caution))
            builder.setMessage(resources.getString(R.string.delAgreeCheck))
            builder.setPositiveButton(resources.getString(R.string.agree)
            ) { _, _ ->
                del_btn.text = ""
                del_btn.isEnabled = false
                del_progressBar.visibility = View.VISIBLE
                viewModel.deleteProducts()
                mAuth = FirebaseAuth.getInstance()
                mAuth.currentUser!!.delete()
                gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                googleSigninClient = GoogleSignIn.getClient(activity!!, gso)
                googleSigninClient.signOut()
                option = activity!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                option.edit()
                    .clear()
                    .apply()

                Handler().postDelayed({
                    activity!!.finish()
                }, 1800)


            }

            builder.show()
        }

        return binding.root
    }

    private fun setSpinner(
        binding: AdsAndOptionFragmentBinding,
        viewModel: HomeViewPagerViewModel?
    ) {

        adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, CURRENCY
            )

        binding.currencyOption.adapter = adapter
        binding.currencyOption.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.currencyOption.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    option.edit()?.putInt(CURR_NAME, position)?.apply()
                    val getData = viewModel?.realData?.value?.toMutableMap()
                    getData?.set("currency", position.toDouble())
                    viewModel?.realData?.value = getData?.toMap()
                    getData?.clear()
                }
            }


        adapter =
            ArrayAdapter(
                context!!, R.layout.support_simple_spinner_dropdown_item, WEIGHTUNIT
            )

        binding.weightUnitOption.adapter = adapter
        binding.weightUnitOption.dropDownVerticalOffset = dipToPixels(53f).toInt()
        binding.weightUnitOption.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    option.edit()?.putInt(WEIGHT_NAME, position)?.apply()
                    val getData = viewModel?.realData?.value?.toMutableMap()
                    getData?.set("weightUnit", position.toDouble())
                    viewModel?.realData?.value = getData?.toMap()
                    getData?.clear()
                }
            }


        binding.currencyOption.setSelection(
            (viewModel?.realData?.value?.get("currency") ?: 0.0).toInt()
        )
        binding.weightUnitOption.setSelection(
            (viewModel?.realData?.value?.get("weightUnit") ?: 0.0).toInt()
        )

    }

    interface Callback {
        fun add()
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }
}
