package com.gsgana.gsledger

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
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
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.util.encoders.Base64
import java.util.regex.Matcher
import java.util.regex.Pattern


class IntroActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 9001
    private val LOG_IN = 18
    private val SIGN_UP = 6
    private val PREF_NAME = "01504f779d6c77df04"
    private val ENCRYPT_NAME = "a345f2f713ie8bd261"
    private val UID_NAME = "7e19f667a8a1c7075f"
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b"
    private val REAL_DB_PATH = "sYTVBn6F18VT6Ykw6L"
    private val LAST_DB_PATH = "OGn6sgTK6umHojW6QV"
    private val KEY = "Kd6c26TK65YSmkw6oU"
    private lateinit var sf: SharedPreferences
    private lateinit var rgl: CharArray
    private lateinit var rgl_b: MutableList<Char>

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSigninClient: GoogleSignInClient
    private lateinit var binding: ActivityIntroBinding
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        mAuth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sf = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        if (sf.getString(ENCRYPT_NAME, null).isNullOrEmpty()) {
            //First Signup
            binding.introProgressBar.visibility = View.GONE
            val mTransform = Linkify.TransformFilter() { matcher: Matcher, s: String ->
                ""
            }
            val pattern1 = Pattern.compile("Privacy Policy")
            val pattern2 = Pattern.compile("개인정보보호정책")

            Linkify.addLinks(
                binding.agreeText,
                pattern1,
                "https://gsledger-29cad.firebaseapp.com/privacypolicy.html",
                null,
                mTransform
            );
            Linkify.addLinks(
                binding.agreeText,
                pattern2,
                "https://gsledger-29cad.firebaseapp.com/privacypolicy.html",
                null,
                mTransform
            );

            googleSignInOption(SIGN_UP, binding)

        } else {
            if (mAuth.currentUser == null) {
                //LOGIN
                binding.introProgressBar.visibility = View.GONE
                googleSignInOption(LOG_IN, binding)
                val mTransform = Linkify.TransformFilter { matcher: Matcher, s: String ->
                    "".toString()
                }
                val pattern1 = Pattern.compile("Privacy Policy")
                val pattern2 = Pattern.compile("개인정보보호정책")

                Linkify.addLinks(
                    binding.agreeText,
                    pattern1,
                    "https://gsledger-29cad.firebaseapp.com/privacypolicy.html",
                    null,
                    mTransform
                );
                Linkify.addLinks(
                    binding.agreeText,
                    pattern2,
                    "https://gsledger-29cad.firebaseapp.com/privacypolicy.html",
                    null,
                    mTransform
                );

            } else {
                val doc = FirebaseFirestore.getInstance()
                    .collection(USERS_DB_PATH)
                    .document(mAuth.currentUser?.uid!!)
                    .get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            //if
                            if (decrypt(sf.getString(UID_NAME, null)) ==
                                document.data?.get("Col3")
                            ) {
                                rgl_b = arrayListOf()
                                val test = document.data?.get("Rgl") as ArrayList<String>
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

                            } else {
                                googleSignInOption(SIGN_UP, binding)
                            }
                        } else {
                            binding.introProgressBar.visibility = View.GONE
                            val mTransform =
                                Linkify.TransformFilter { matcher: Matcher, s: String ->
                                    "".toString()
                                }
                            val pattern1 = Pattern.compile("Privacy Policy")
                            val pattern2 = Pattern.compile("개인정보보호정책")

                            Linkify.addLinks(
                                binding.agreeText,
                                pattern1,
                                "https://gsledger-29cad.firebaseapp.com/privacypolicy.html",
                                null,
                                mTransform
                            );
                            Linkify.addLinks(
                                binding.agreeText,
                                pattern2,
                                "https://gsledger-29cad.firebaseapp.com/privacypolicy.html",
                                null,
                                mTransform
                            );
                            googleSignInOption(SIGN_UP, binding)
                        }
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
                        sf.edit().putString(ENCRYPT_NAME, generateRgl(32))
                            .commit()
                        sf.edit().putString(UID_NAME, encrypt(mAuth.currentUser?.uid!!))
                            .commit()
                        val docRef = FirebaseFirestore.getInstance()
                            .collection(USERS_DB_PATH)
                            .document(mAuth.currentUser?.uid!!)
                        docRef.get().addOnSuccessListener { document ->
                            if (document.data?.get("Col3") == null) {
                                docRef
                                    .set(
                                        hashMapOf(
                                            "Rgl" to generateRgl6(),
                                            "Col3" to mAuth.currentUser?.uid!!
                                        )
                                    )
                            }
                        }
                        rgl_b = arrayListOf()
                        var test1 = generateRgl6()
                        for (s in test1) {
                            this.rgl_b.add(s.toCharArray()[0])
                        }
                        test1 = mutableListOf()
                        rgl = rgl_b.toCharArray()
                        rgl_b.clear()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        intent.putExtra(KEY, rgl)
                        rgl = charArrayOf()
                        startActivity(intent)
                        finish()

                    } else {

                        mAuth = FirebaseAuth.getInstance()
                        val db = FirebaseFirestore.getInstance()
                        val docRef = db.collection(USERS_DB_PATH).document(mAuth.currentUser?.uid!!)
                        docRef.get()
                            .addOnSuccessListener { document ->
                                if (decrypt(sf.getString(UID_NAME, null)) ==
                                    document.data?.get("Col3")
                                ) {
                                    rgl_b = arrayListOf()
                                    var test1 = generateRgl6()
                                    for (s in test1) {
                                        this.rgl_b.add(s.toCharArray()[0])
                                    }
                                    test1 = mutableListOf()
                                    rgl = rgl_b.toCharArray()
                                    rgl_b.clear()
                                    val intent =
                                        Intent(applicationContext, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    intent.putExtra(KEY, rgl)
                                    rgl = charArrayOf()
                                    startActivity(intent)
                                    finish()

                                } else {
                                    rgl_b = arrayListOf()
                                    var test1 = generateRgl6()
                                    for (s in test1) {
                                        this.rgl_b.add(s.toCharArray()[0])
                                    }
                                    test1 = mutableListOf()
                                    rgl = rgl_b.toCharArray()
                                    rgl_b.clear()
                                    val intent =
                                        Intent(applicationContext, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    intent.putExtra(KEY, rgl)
                                    rgl = charArrayOf()
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("GSNOTE", "get failed with ", exception)
                            }

                    }
                }
            }
    }

    private fun googleSignInOption(code: Int, binding: ActivityIntroBinding) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSigninClient = GoogleSignIn.getClient(this, gso)

        binding.signInCheck.visibility = View.VISIBLE
        binding.signupBtn.visibility = View.VISIBLE
        binding.signupBtn.setOnClickListener {
            if (!binding.agreeBox.isChecked) {
//                    Toast.makeText(this, resources.getString(),Toast.LENGTH_LONG).show()"Please read the Privacy Policy and check the box"
                Toast.makeText(
                    this,
                    resources.getString(R.string.checkBox),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val signInIntent = googleSigninClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }


    }


    private fun generateRgl(length: Int = 18): String {
        val ALLOWED_CHARACTERS = "013567890123456789ABCDEFGHIJKLMNOPQRSTUWXYZ"
        val random = Random()
        val sb = StringBuilder(length)
        for (i in 0 until length)
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


    private fun encrypt(strToEncrypt: String): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray
        try {
            keyBytes = sf.getString(ENCRYPT_NAME, null)!!.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = strToEncrypt.toByteArray(charset("UTF8"))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, skey)

                val cipherText = ByteArray(cipher.getOutputSize(input.size))
                var ctLength = cipher.update(
                    input, 0, input.size,
                    cipherText, 0
                )
                ctLength += cipher.doFinal(cipherText, ctLength)
                return String(
                    Base64.encode(cipherText)
                )
            }

        } catch (e: Exception) {
            println("Error while encrypting: $e")
            return null
        }
    }

    private fun decrypt(strToDecrypt: String?): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = sf.getString(ENCRYPT_NAME, null)!!.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = org.bouncycastle.util.encoders.Base64
                .decode(strToDecrypt?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.DECRYPT_MODE, skey)

                val plainText = ByteArray(cipher.getOutputSize(input.size))
                var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }
            }
        } catch (e: Exception) {
            println("Error while decrypting: $e")
            return null
        }
    }
}