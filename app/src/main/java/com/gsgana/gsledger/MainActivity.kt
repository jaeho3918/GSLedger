package com.gsgana.gsledger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.data.AppDatabase
import com.gsgana.gsledger.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val DATABASE_PATH = "qnI4vK2zSUq6GdeT6b"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }
}
