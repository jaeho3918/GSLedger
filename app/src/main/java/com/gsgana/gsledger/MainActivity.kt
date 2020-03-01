package com.gsgana.gsledger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.data.AppDatabase

class MainActivity : AppCompatActivity() {

    private val DATABASE_PATH = "qnI4vK2zSUq6GdeT6b"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val key = intent.getCharArrayExtra("key")
//
        mAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(DATABASE_PATH).document(mAuth.currentUser?.uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                val tes1 = mutableListOf<Char>()
                val test = document.data?.get("Rgl") as ArrayList<String>
                for (s in test) {
                    tes1.add(s.toCharArray()[0])
                }
                test.clear()
                val reg =
                    AppDatabase.getInstance(this, tes1.toCharArray())
                tes1.clear()
            }
    }
}
