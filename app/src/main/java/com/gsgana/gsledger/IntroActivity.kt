package com.gsgana.gsledger

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.*
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.gsgana.gsledger.databinding.ActivityIntroBinding
import io.fabric.sdk.android.Fabric
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


@Suppress("NAME_SHADOWING", "DEPRECATED_IDENTITY_EQUALS", "UNCHECKED_CAST")
class IntroActivity : AppCompatActivity(), PurchasesUpdatedListener {

    private val RC_SIGN_IN = 9001
    private val UPDATE_REQUEST_CODE = 6
    private val USERS_DB_PATH = "qnI4vK2zSUq6GdeT6b" //FuWKuAiLI5Q4suD4ciBv
    private val ENCRYPT_NAME = "a345f2f713ie8bd261"  //waiECtOFcBCylMcgjf7I
    private val ENCRYPT_NAME1 = "cBywaiEtOFlMg6jf7I"
    private val ENCRYPT_NAME6 = "JHv6DQ6loOBd6lLRrk"

    private val NEW_LABEL = "RECSHenWYqdadfXOog"
    private val NEW_ENCRYPT = "X67LWGmYAc3rlCbmPe"
    private val NUMBER = "HYf75f2q2a36enW18b"

    //    private val UID_NAME = "7e19f667a8a1c7075f"
    private val KEY = "Kd6c26TK65YSmkw6oU"
    private val PREF_NAME = "01504f779d6c77df04"

    private lateinit var sf: SharedPreferences
    private lateinit var rgl: CharArray
    private lateinit var rgl_b: MutableList<Char>
//    private lateinit var gso: GoogleSignInOptions

    private val functions = FirebaseFunctions.getInstance()

    private lateinit var mfirebase: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSigninClient: GoogleSignInClient
    private lateinit var binding: ActivityIntroBinding
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private lateinit var appUpdateManager: AppUpdateManager

    private val ADFREE_NAME = "CQi7aLBQH7dR7qyrCG"

//    private lateinit var billingClient: BillingClient
//    private val sku1800 = "gsledger_subscribe"
//    private val sku3600 = "adfree_unlimited_entry"
//    private lateinit var flowParams : BillingFlowParams

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        sf = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//        mAuth.signInAnonymously()
//        Log.d("signInAnonymously check", mAuth.signInAnonymously().result.toString())
//        Log.d("signInAnonymously check", mAuth.signInAnonymously().isCanceled.toString())
//        Log.d("signInAnonymously check", mAuth.signInAnonymously().isComplete.toString())


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Fabric.with(this, Crashlytics())

        appUpdateManager = AppUpdateManagerFactory.create(this)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // For a flexible update, use AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE, // or AppUpdateType.IMMEDIATE
                    this,
                    UPDATE_REQUEST_CODE
                )
            } else {
//                Toast.makeText(this, "gooooooooooooooooooood", Toast.LENGTH_LONG).show()
            }
        }


//        sf.edit().putString(NEW_LABEL, "").apply()
//        sf.edit().putString(NEW_ENCRYPT, "").apply()

        setContentView(R.layout.activity_intro)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        if (mAuth.currentUser != null) {
            //currentUser exist id
            if (sf.getString(NEW_LABEL, "") == "") {
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

                        var label = generateLabel()
                        var encrypt = generateRgl6()
                        setNewKey(label, encrypt, test).addOnSuccessListener { result ->
                            if (result[0] == 1) {
                                this.finish()
                            } else if (result[0] == 18) {
                                sf.edit().putString(NEW_LABEL, label).apply()
                                sf.edit().putString(NEW_ENCRYPT, encrypt).apply()
                                sf.edit().putInt(NUMBER, result[1]).apply()
                                label = ""
                                test.clear()

                                mAuth.signOut()
                                mAuth.signInAnonymously()

                                rgl = rgl_b.toCharArray()
                                rgl_b.clear()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                intent.putExtra(KEY, rgl)
                                startActivity(intent)
                                rgl = charArrayOf()
                                this.finish()
                            }
                        }
                    }
            } else {
                getNewKey(
                    sf.getString(NEW_LABEL, "")!!,
                    sf.getString(NEW_ENCRYPT, "")!!,
                    sf.getInt(NUMBER, 0)
                ).addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra(KEY, it)
                    startActivity(intent)
                    this.finish()
                }
            }
        } else {
            //firebase Auth currenct User not exist
            if (sf.getString(NEW_LABEL, "") == "") {
                mAuth.signInAnonymously()
                    .addOnSuccessListener {
                        var label = generateLabel()
                        val tset = generateRgl18()
                        val encrypt = generateRgl6()
                        setNewKey(label, encrypt, tset).addOnSuccessListener { result ->
                            if (result[0] == 1) {
                                this.finish()
                            } else if (result[0] == 18) {
                                sf.edit().putString(NEW_LABEL, label).apply()
                                sf.edit().putString(NEW_ENCRYPT, encrypt).apply()
                                sf.edit().putInt(NUMBER, result[1]).apply()
                                label = ""
                                rgl_b = arrayListOf()
                                for (s in tset) {
                                    this.rgl_b.add(s.toCharArray()[0])
                                }
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                intent.putExtra(KEY, rgl_b.toCharArray())
                                startActivity(intent)
                                this.finish()
                            }
                        }
                    }
//                    .addOnCompleteListener {
//                        Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
//                    }
//                    .addOnFailureListener {
//                        Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
//                    }

            } else {
                getNewKey(
                    sf.getString(NEW_LABEL, "")!!,
                    sf.getString(NEW_ENCRYPT, "")!!,
                    sf.getInt(NUMBER, 0)
                )
                    .addOnSuccessListener {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        intent.putExtra(KEY, it)
                        startActivity(intent)
                        this.finish()
                    }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // google login
        if (requestCode === RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
            } catch (e: ApiException) {
            }
            //Update Result
        } else if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(
                    this,
                    "Update failed!\nPlease install the latest version.",
                    Toast.LENGTH_LONG
                ).show()
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

    override fun onPurchasesUpdated(
        billingresult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        if (billingresult?.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.let {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        sf.edit().putInt(ADFREE_NAME, 18).apply()
//                    Toast.makeText(context!!,"Baaaaaaaaaaaaad", Toast.LENGTH_LONG) .show()
                    } else {
                        sf.edit().putInt(ADFREE_NAME, 6).apply()
                    }
                }
            }
        }
    }

    private fun generateRgl6(length: Int = 24): String {
        val ALLOWED_CHARACTERS = "01356789012356789ABCDEFGHIJKLMNOPQRSTUWXYZ"
        val random = Random()
        var sb = ""
        for (i in 0 until length)
            sb += ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)]
        return sb
    }

    private fun generateRgl18(length: Int = 24): List<String> {
        val ALLOWED_CHARACTERS = "01356789012356789ABCDEFGHIJKLMNOPQRSTUWXYZ"
        val random = Random()
        var sb = mutableListOf<String>()
        for (i: Int in 0 until length)
            sb.add(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)].toString())
        return sb
    }

    private fun generateLabel(length: Int = 36): String {
        val ALLOWED_CHARACTERS = "01356789012356789ABCDEFGHIJKLMNOPQRSTUWXYZ"
        val random = Random()
        var sb = ""
        for (i in 0 until length)
            sb += ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)]
        return sb
    }

    private fun setNewKey(label: String, reg: String, list: List<String>): Task<ArrayList<Int>> {
        // Create the arguments to the callable function.

        val data = hashMapOf(
            "label" to label,
            "reg" to reg,
            "list" to list
        )

        return functions
            .getHttpsCallable("NEWXcgis5LkXO3g6ibNDE")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.

                val result = task.result?.data as ArrayList<Int>
                result
            }
    }

    private fun getNewKey(label: String, reg: String, num: Int): Task<CharArray> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "label" to label,
            "reg" to reg,
            "number" to num
        )
        return functions
            .getHttpsCallable("NEGETKEYis5kXO3DEg6iN")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.

                val rgl = mutableListOf<Char>()

                val result = task.result?.data as ArrayList<String>

                for (s in result) {
                    rgl.add(s.toCharArray()[0])
                }
                rgl.toCharArray()
            }
    }


}
