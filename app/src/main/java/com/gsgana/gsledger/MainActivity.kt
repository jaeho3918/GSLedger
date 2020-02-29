package com.gsgana.gsledger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gsna.gsnote.data.AppDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val key = intent.getCharArrayExtra("key")
        val reg = AppDatabase.getInstance(this, key)


    }
}
