package com.gsgana.gsledger

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.gsgana.gsledger.databinding.ActivityIntroBinding
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.util.encoders.Base64
import java.util.regex.Matcher
import java.util.regex.Pattern


@Suppress("NAME_SHADOWING", "DEPRECATED_IDENTITY_EQUALS", "UNCHECKED_CAST")
class IntroActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 9001
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b" //FuWKuAiLI5Q4suD4ciBv
    private val ENCRYPT_NAME = "a345f2f713ie8bd261"  //waiECtOFcBCylMcgjf7I
    private val ENCRYPT_NAME1 = "cBywaiEtOFlMg6jf7I"
    private val ENCRYPT_NAME6 = "JHv6DQ6loOBd6lLRrk"
    private val UID_NAME = "7e19f667a8a1c7075f"
    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val PREF_NAME = "01504f779d6c77df04"

    private lateinit var sf: SharedPreferences
    private lateinit var rgl: CharArray
    private lateinit var rgl_b: MutableList<Char>
    private lateinit var gso: GoogleSignInOptions

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSigninClient: GoogleSignInClient
    private lateinit var binding: ActivityIntroBinding
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private lateinit var pattern1: Pattern
    private lateinit var pattern2: Pattern
    private lateinit var pattern3: Pattern
    private lateinit var pattern4: Pattern


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sf = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        setContentView(R.layout.activity_intro)
        mAuth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        if (mAuth.currentUser == null) {
            //First Signup
            binding.introProgressBar.visibility = View.GONE
            val mTransform = Linkify.TransformFilter { _: Matcher, _: String -> "" }
            pattern1 = Pattern.compile("Privacy Policy")
            pattern2 = Pattern.compile("개인정보보호정책")
            pattern3 = Pattern.compile("Terms and Conditions")
            pattern4 = Pattern.compile("이용약관")

            Linkify.addLinks(
                binding.agreeText,
                pattern1,
                "https://gsledger-29cad.firebaseapp.com/privacypolicy.html",
                null,
                mTransform
            )
            Linkify.addLinks(
                binding.agreeText,
                pattern2,
                "https://gsledger-29cad.firebaseapp.com/privacypolicy_kr.html",
                null,
                mTransform
            )
            Linkify.addLinks(
                binding.agreeText,
                pattern3,
                "https://gsledger-29cad.firebaseapp.com/termsandconditions.html",
                null,
                mTransform
            )
            Linkify.addLinks(
                binding.agreeText,
                pattern4,
                "https://gsledger-29cad.firebaseapp.com/termsandconditions_kr.html",
                null,
                mTransform
            )

            googleSignInOption(binding)

        } else {
            //currentUser exist id
            FirebaseFirestore
                .getInstance()
                .collection(USERS_DB_PATH)
                .document(mAuth.currentUser?.uid!!)
                .get()
                .addOnSuccessListener { data ->
                    //if
                    rgl_b = arrayListOf()
                    val test = data.data?.get(
                        sf.getString(ENCRYPT_NAME6, "null")
                    ) as ArrayList<String>

                    for (s in test) {
                        this.rgl_b.add(s.toCharArray()[0])
                    }
                    test.clear()
                    rgl = rgl_b.toCharArray()
                    rgl_b.clear()
                    val intent =
                        Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra(KEY, rgl)
                    startActivity(intent)
                    rgl = charArrayOf()
                    finish()

                }


        }
    }

    @SuppressLint("ShowToast")
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

    private fun firebaseAuthWithGoogle(
        account: GoogleSignInAccount,
        sf: SharedPreferences
    ) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    if (sf.getString(ENCRYPT_NAME, null).isNullOrEmpty()) {
                        sf.edit()
                            .apply {
                                putString(ENCRYPT_NAME, "F6uWK6uAiLIBC5Q4suDci6Bv")
                                putString(ENCRYPT_NAME1, "hmqDA6f1tlYS18RV6qRHI6EV")
                                putString(ENCRYPT_NAME6, 32.generateRgl())
                                commit()
                            }
                    }

                    val docRef = FirebaseFirestore
                        .getInstance()
                        .collection(USERS_DB_PATH)
                        .document(mAuth.currentUser?.uid!!)
                        .apply {
                            get()
                                .addOnSuccessListener { data ->
                                    if (data.exists()) {
                                        update(
                                            mapOf(
                                                sf.getString(
                                                    ENCRYPT_NAME6,
                                                    "null"
                                                )!! to generateRgl6()
                                            )
                                        ).addOnFailureListener {
                                            Toast.makeText(
                                                applicationContext,
                                                it.toString(),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else {
                                        set(
                                            hashMapOf(
                                                sf.getString(
                                                    ENCRYPT_NAME6,
                                                    "null"
                                                )!! to generateRgl6()
                                            )
                                        ).addOnFailureListener {
                                            Toast.makeText(
                                                applicationContext,
                                                it.toString(),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                }
                        }
                }

                Handler().postDelayed({
                    docRef
                        .get()
                        .addOnSuccessListener { data ->
                            //if
                            rgl_b = arrayListOf()
                            val test = data.data?.get(
                                sf.getString(ENCRYPT_NAME6, "null")
                            ) as ArrayList<String>

                            for (s in test) {
                                this.rgl_b.add(s.toCharArray()[0])
                            }
                            test.clear()
                            rgl = rgl_b.toCharArray()
                            rgl_b.clear()
                            val intent =
                                Intent(applicationContext, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            intent.putExtra(KEY, rgl)
                            startActivity(intent)
                            rgl = charArrayOf()
                            finish()
                        }
                }, 5000)


            }
    }
}

private fun googleSignInOption(binding: ActivityIntroBinding) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    googleSigninClient = GoogleSignIn.getClient(this, gso)

    binding.signInCheck.visibility = View.VISIBLE
    binding.signupBtn.visibility = View.VISIBLE
    binding.signupBtn.setOnClickListener {
        if (!binding.agreeBox.isChecked) {
            Toast.makeText(
                this,
                resources.getString(R.string.checkBox),
                Toast.LENGTH_LONG
            ).show()
        } else {
            val signInIntent = googleSigninClient.signInIntent
            binding.signupBtn.visibility = View.GONE
            binding.agreeBox.visibility = View.GONE
            binding.agreeText.visibility = View.GONE
            binding.signupProgress.visibility = View.VISIBLE
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
}

private fun Int.generateRgl(): String {
    val ALLOWED_CHARACTERS = "013567890123456789ABCDEFGHIJKLMNOPQRSTUWXYZ"
    val random = Random()
    val sb = StringBuilder(this)
    for (i in 0 until this)
        sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
    return sb.toString()
}

private fun generateRgl6(length: Int = 36): List<String> {
    val ALLOWED_CHARACTERS = "013567890123456789ABCDEFGHIJKLMNOPQRSTUWXYZ"
    val random = Random()
    val sb = mutableListOf<String>()
    for (i in 0 until length)
        sb.add(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)].toString())
    return sb
}


}