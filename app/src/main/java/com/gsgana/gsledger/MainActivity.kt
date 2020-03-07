package com.gsgana.gsledger

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.data.AppDatabase
import com.gsgana.gsledger.databinding.ActivityMainBinding
import com.gsgana.gsledger.utilities.InjectorUtils
import com.gsgana.gsledger.viewmodels.HomeViewPagerViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private val REAL_NAME = "sYTVBn2FO8VNT9Ykw90L"
    private val CURR_NAME = "1w3d4f7w9d2qG2eT36"
    private val PREF_NAME = "01504f779d6c77df04"
    private val REAL_DB_PATH = "sYTVBn6F18VT6Ykw6L"
    private val LAST_DB_PATH = "OGn6sgTK6umHojW6QV"

    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"
    private lateinit var rgl: CharArray
    private lateinit var rgl_b: MutableList<Char>

    private lateinit var database: FirebaseDatabase
    //    private lateinit var viewModel: HomeViewPagerViewModel

    private val viewModel: HomeViewPagerViewModel by viewModels {
        InjectorUtils.provideHomeViewPagerViewModelFactory(this, intent.getCharArrayExtra(KEY))
    }

    private var currencyOption: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val currencyOption =
            getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)?.getInt(CURR_NAME, 0)

        val databaseRef = FirebaseDatabase.getInstance().getReference(REAL_DB_PATH)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val data = p0?.value as HashMap<String, Double>

                if (currencyOption != null) {
                    if (!viewModel.realData.value.isNullOrEmpty()) {
                        data["currency"] = viewModel.realData.value?.getValue("currency") ?: 0.0
                    } else {
                        data["currency"] = currencyOption.toDouble()
                    }
                }
                data["USD"] = 1.0
                data["DATE"] = 0.0

                viewModel.realData.value = data
                viewModel.realTime.value = (p0.value as HashMap<String, String>)["DATE"]
            }


        }
        )

    }

    override fun onBackPressed() {

    }
}
