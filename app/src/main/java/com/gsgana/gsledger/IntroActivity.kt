package com.gsgana.gsledger

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.databinding.ActivityIntroBinding
import java.util.*

class IntroActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 9001
    private val LOG_IN = 18
    private val SIGN_UP = 6
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSigninClient: GoogleSignInClient
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        mAuth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        val sf = this.getSharedPreferences("4vK6O2zSUqGd6", Context.MODE_PRIVATE)
        val stat = sf?.getInt("ptQY6xnS6Nv6PM", 6)

        // select signup or login by getSharedPreferences

        // sign up
        if (stat == SIGN_UP) {
            binding.introProgressBar.visibility = View.GONE
            googleSignInOption(stat, binding)

            // login
        } else if (stat == LOG_IN) {
            val intent = Intent(applicationContext, MainActivity::class.java)
//            intent.putExtra(
//                "key",
//                document.data?.get("Rgl").toString().toCharArray()
//            )

            mAuth = FirebaseAuth.getInstance()
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                binding.introProgressBar.visibility = View.GONE

            } else {
                binding.signupBtn.visibility = View.GONE
                val user = mAuth.currentUser?.uid.toString()
                val db = FirebaseFirestore.getInstance()
                val docRef = db.collection("qnI4vKO2zSUq6GdeyT6b").document(user)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.putExtra(
                                "key",
                                document.data?.get("Rgl").toString().toCharArray()
                            )
                            startActivity(intent)
                            finish()
                        } else {
                            Log.d("GSNOTE", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("GSNOTE", "get failed with ", exception)
                    }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(this, "11111111111111111111111111", Toast.LENGTH_LONG)
        val sf = this.getSharedPreferences("4vK6O2zSUqGd6", Context.MODE_PRIVATE) //option
        // google login
        if (requestCode === RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!, sf)
            } catch (e: ApiException) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, sf: SharedPreferences) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, sf.getString("II4vKO2zSUqGd", null), Toast.LENGTH_LONG)

//                    // Write a message to the firestore
                    if (sf.getString("II4vKO2zSUqGd", null).isNullOrBlank()) {
                        sf.edit().putInt("ptQY6xnS6Nv6PM", 18).commit() //save at sharedpreference
                        sf.edit().putString("II4vKO2zSUqGd", mAuth.currentUser?.uid).commit() //save at sharedpreference
                        Toast.makeText(this, sf.getString("II4vKO2zSUqGd", null), Toast.LENGTH_LONG)

                        val docRef = FirebaseFirestore.getInstance()
                            .collection("qnI4vKO2zSUq6GdeyT6b")
                            .document(sf.getString("II4vKO2zSUqGd", null) ?: "")
                            .collection(mAuth.currentUser?.uid ?: "")

                        startActivity(intent)
                        finish()


                        Toast.makeText(this, sf.getString("II4vKO2zSUqGd", null), Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "already" + sf.getString("II4vKO2zSUqGd", null),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    mAuth = FirebaseAuth.getInstance()
                    val user = mAuth.currentUser?.uid.toString()
                    val db = FirebaseFirestore.getInstance()
                    val docRef = db.collection("qnI4vKO2zSUq6GdeyT6b").document(user)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                intent.putExtra(
                                    "key",
                                    document.data?.get("Rgl").toString().toCharArray()
                                )
                                startActivity(intent)
                                finish()
                            } else {
                                db.collection("users")
                                    .document(user)
                                    .set(
                                        hashMapOf(
                                            "Rgl" to generateRgl(),
                                            "Col1" to 0,
                                            "Col2" to 0,
                                            "Col3" to 0,
                                            "Col4" to 0,
                                            "Col5" to 0
                                        )
                                    )
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                intent.putExtra(
                                    "key",
                                    document.data?.get("Rgl").toString().toCharArray()
                                )
                                startActivity(intent)
                                finish()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("GSNOTE", "get failed with ", exception)
                        }

                } else {
                    Toast.makeText(this, "로그인실패", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun googleSignInOption(code: Int, binding: ActivityIntroBinding) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSigninClient = GoogleSignIn.getClient(this, gso)

        if (code == 6) {
            binding.signupBtn.visibility = View.VISIBLE
            binding.signupBtn.setOnClickListener {
                val signInIntent = googleSigninClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

        } else if (code == 18) {
            binding.loginBtn.visibility = View.VISIBLE
            binding.loginBtn.setOnClickListener {
                val signInIntent = googleSigninClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
    }


    private fun generateRgl(): String {
        val ALLOWED_CHARACTERS = "013567890123456789ABCDEFGHIJKLMNOPQRSTUWXYZ"
        val random = Random()
        val sb = StringBuilder(18)
        for (i in 0 until 18)
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }
}