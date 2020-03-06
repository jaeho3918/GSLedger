package com.gsgana.gsledger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
    private lateinit var viewModel: HomeViewPagerViewModel
    private lateinit var rgl: CharArray
    private lateinit var rgl_b: MutableList<Char>

    private lateinit var database: FirebaseDatabase

    private var currencyOption: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        viewModel = ViewModelProviders.of( this,
            InjectorUtils.provideHomeViewPagerViewModelFactory(this, intent.getCharArrayExtra(KEY))
        ).get(HomeViewPagerViewModel::class.java)
    }

    override fun onBackPressed() {

    }
}
