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
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class IntroActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 9001
    private val LOG_IN = 18
    private val SIGN_UP = 6
    private val PREF_NAME = "01504f779d6c77df04"
    private val ENCRYPT_NAME = "a345f2f713ie8bd261"
    private val UID_NAME = "7e19f667a8a1c7075f"
    private val DATABASE_PATH = "qnI4vK2zSUq6GdeT6b"
    private lateinit var sf: SharedPreferences

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSigninClient: GoogleSignInClient
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        mAuth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        sf = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (sf.getString(ENCRYPT_NAME, null).isNullOrEmpty()) {
            //First Signup
            binding.introProgressBar.visibility = View.GONE
            googleSignInOption(SIGN_UP, binding)

        } else {
            //login

        }
        // select signup or login by getSharedPreferences

        val stat = 6

        if (stat == LOG_IN) {
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
                val docRef = db.collection(DATABASE_PATH).document(user)
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
        val sf = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) //option
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
//                    // Write a message to the firestore
                    if (sf.getString(ENCRYPT_NAME, null).isNullOrBlank()) {
                        // signup and generate key
                        val test1 = generateRgl()
                        sf.edit().putString(ENCRYPT_NAME, test1)
                            .commit()
                        val test2 = sf.getString(ENCRYPT_NAME, null)

                        sf.edit().putString(UID_NAME, encrypt(this, mAuth.currentUser?.uid!!))
                            .commit()

                        val test3 = encrypt(this, mAuth.currentUser?.uid!!)

                        val test = decrypt(sf.getString(UID_NAME, null))

                        val docRef = FirebaseFirestore.getInstance()
                            .collection(DATABASE_PATH)
                            .document(mAuth.currentUser?.uid!!)
                            .set(
                                hashMapOf(
                                    "Rgl" to generateRgl(),
                                    "Col0" to 0,
                                    "Col1" to 0,
                                    "Col2" to 0,
                                    "Col3" to 0,
                                    "Col4" to 0,
                                    "Col5" to getBytes(this).toString()
                                )
                            )

                        startActivity(intent)
                        finish()


                        Toast.makeText(this, sf.getString(ENCRYPT_NAME, null), Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "already" + sf.getString(ENCRYPT_NAME, null),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    mAuth = FirebaseAuth.getInstance()
                    val db = FirebaseFirestore.getInstance()
                    val docRef = db.collection(DATABASE_PATH).document(mAuth.currentUser?.uid!!)
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
                                    .document(mAuth.currentUser?.uid!!)
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

    private fun getBytes(context: Context): ByteArray? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val str = prefs.getString(ENCRYPT_NAME, null)
        return if (!str.isNullOrEmpty()) str.toByteArray(Charsets.ISO_8859_1)
        else
            null
    }

    private fun setBytes(context: Context, byteArray: ByteArray) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val e = prefs.edit()
        e.putString(ENCRYPT_NAME, String(byteArray, Charsets.ISO_8859_1))
            .commit()
    }

    private fun encrypt(context: Context, plainText: String): String? = try {
        // 각각 알고리즘/Block Cipher Mode/Padding 메카니즘이다.
        sf = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(sf.getString(ENCRYPT_NAME, null)?.toByteArray(), "AES")
        )
        cipher.doFinal(plainText.toByteArray()).toString()


    } catch (e: Exception) {
        println("Error while encrypting: $e")
        null
    }

    private fun decrypt(cipherText: String?): String? = try {
        // 각각 알고리즘/Block Cipher Mode/Padding 메카니즘이다.
        val cipher = Cipher.getInstance("AES/ECB/PKC55PADDING")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(sf.getString(ENCRYPT_NAME, null)?.toByteArray(), "AES")
        )
        String(cipher.doFinal(cipherText?.toByteArray()))
    } catch (e: Exception) {
        println("Error while decrypting: $e")
        null
    }
}