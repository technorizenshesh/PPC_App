package com.codeinger.ppcapp.ui.activity.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codeinger.ppcapp.R
import com.codeinger.ppcapp.databinding.ActivityWelcomeBinding
import com.codeinger.ppcapp.retrofit.Constant
import com.codeinger.ppcapp.ui.activity.MainActivity
import com.codeinger.ppcapp.ui.activity.membership.ChooseMembershipActivity
import com.codeinger.ppcapp.ui.activity.membership.PurchseSuccessMembershipActivity
import com.codeinger.ppcapp.utils.CustomDialogProgress
import com.codeinger.ppcapp.utils.DataManager
import com.codeinger.ppcapp.utils.SessionManager
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.iratepro.retrofit.BackEndApi
import com.iratepro.retrofit.WebServiceClient
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class WelcomeActivity : AppCompatActivity() {

    private var tokennnn: String? = null
    private var tokenm: String? = null
    private var SCREEN_TIME_OUT = 3000
    private lateinit var binding: ActivityWelcomeBinding
    private var mAuth: FirebaseAuth? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    val GOOGLE_SIGN_IN_REQUEST_CODE = 1234
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)

        init()

        FirebaseApp.initializeApp(this)
        callbackManager = CallbackManager.Factory.create()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        setContentView(binding.root)

        initFb()
        DataManager.getKeyHash(this@WelcomeActivity)

        mAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    fun init() {

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, SiginActivity::class.java)
            startActivity(intent)
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        processNext()

    }

    private fun processNext() {

        tokenm = SessionManager.readString(this@WelcomeActivity, Constant.isPaidMember, "")
        tokennnn = SessionManager.readString(this@WelcomeActivity, Constant.token, "")

        Log.e("tokenm>>", "" + tokenm)
        Log.e("tokennnn>>", "" + tokennnn)

        if (SessionManager.readString(this@WelcomeActivity, Constant.isPaidMember, "")
                ?.equals("true")!! == true
        ) {

            if (!tokennnn.equals("")) {
                startActivity(
                    Intent(
                        this@WelcomeActivity,
                        PurchseSuccessMembershipActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )

            } else {
                Handler().postDelayed(Runnable {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()

                }, SCREEN_TIME_OUT.toLong())
            }

        } else {

            if (!tokennnn.equals("")) {
                startActivity(
                    Intent(
                        this@WelcomeActivity,
                        ChooseMembershipActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
            } else {


            }

        }
    }

    private fun initFb() {
        binding.ivGoogle.setOnClickListener { v ->
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
        }

        binding.ivFb.setOnClickListener { v ->
            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        handleFacebookAccessToken(loginResult.accessToken)
                    }

                    override fun onCancel() {
                        Log.e("kjsgdfkjdgsf", "onCancel")
                    }

                    override fun onError(error: FacebookException) {
                        Log.e("kjsgdfkjdgsf", "error = " + error.message)
                    }
                }
                )
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful) {
                        val user = mAuth!!.currentUser
                        val profilePhoto =
                            "https://graph.facebook.com/" + token.userId + "/picture?height=500"
                        Log.e("kjsgdfkjdgsf", "profilePhoto = $profilePhoto")
                        Log.e("kjsgdfkjdgsf", "name = " + user!!.displayName)
                        Log.e("kjsgdfkjdgsf", "email = " + user.email)
                        Log.e("kjsgdfkjdgsf", "UserID = " + user.uid)
                        Log.e("token_facebook>>>", token.toString())

                        socialLoginCall(user.displayName, user.email, profilePhoto, user.uid)

                    } else {
                        Toast.makeText(
                            this@WelcomeActivity, "Authentication failed.", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    private fun socialLoginCall(
        displayName: String?,
        email: String?,
        profilePhoto: String,
        uid: String
    ) {

        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()
        val rootObject = JSONObject()
        rootObject.put("firstName", displayName)
        rootObject.put("lastName", displayName)
        rootObject.put("email", email)
        rootObject.put("facebookToken", uid)
        rootObject.put("devicePushNotificationToken", "1234dbfth")
        val requestFile =
            RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG=====Json===", "kkkkkkkk: $requestFile.toString()")

        WebServiceClient.client.create(BackEndApi::class.java).userFblogin(/*header,*/ requestFile)
            .enqueue(object : Callback<Object> {

                override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                    customDialogProgress!!.dismiss()

                    if (response!!.code() == 200) {
                        val `object` = JSONObject(Gson().toJson(response!!.body()))
                        if (`object`.get("success") == true) {
                            Log.e("chala ga bhai====", "onResponse: $`object`")

                            SessionManager.writeString(
                                this@WelcomeActivity, Constant.token,
                                `object`.get("token").toString()
                            )

                            SessionManager.writeString(
                                this@WelcomeActivity,
                                Constant.isPaidMember,
                                `object`.get("isPaidMember").toString()
                            )

                            if (`object`.get("isPaidMember") == true) {
                                Toast.makeText(
                                    this@WelcomeActivity,
                                    getString(R.string.login_successfully),
                                    Toast.LENGTH_SHORT
                                ).show()

                                startActivity(
                                    Intent(
                                        this@WelcomeActivity,
                                        MainActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                finishAffinity()
                            } else {
                                SessionManager.writeString(
                                    this@WelcomeActivity,
                                    Constant.token,
                                    `object`.get("token").toString()
                                )

                                SessionManager.writeString(
                                    this@WelcomeActivity,
                                    Constant.Fname,
                                    `object`.get("firstName").toString()
                                )


                                SessionManager.writeString(
                                    this@WelcomeActivity,
                                    Constant.Lname,
                                    `object`.get("lastName").toString()
                                )

                                startActivity(
                                    Intent(
                                        this@WelcomeActivity,
                                        ChooseMembershipActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                finishAffinity()
                            }

                        } else {
                            Toast.makeText(
                                this@WelcomeActivity,
                                `object`!!.get("message").toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@WelcomeActivity,
                            response!!.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<Object>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(this@WelcomeActivity, t!!.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = mAuth!!.currentUser
                if (user != null) {
                    Log.e("kjsgdfkjdgsf", "profilePhoto = " + user.photoUrl)
                    Log.e("kjsgdfkjdgsf", "name = " + user.displayName)
                    Log.e("kjsgdfkjdgsf", "email = " + user.email)
                    Log.e("kjsgdfkjdgsf", "UserId = " + user.uid)

                    Log.e("token_google>>>", idToken.toString())

                    socialLoginCall1(
                        user.displayName,
                        user.email,
                        user.photoUrl.toString(),
                        user.uid
                    )
                }
            } else {

            }
        }
    }

    private fun socialLoginCall1(
        displayName: String?,
        email: String?,
        toString: String,
        uid: String
    ) {

        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()
        val rootObject = JSONObject()
        rootObject.put("firstName", displayName)
        rootObject.put("lastName", displayName)
        rootObject.put("email", email)
        rootObject.put("googleToken", uid)
        rootObject.put("devicePushNotificationToken", "1234dbfth")
        val requestFile =
            RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG=====Json===", "kkkkkkkk: $requestFile.toString()")
        WebServiceClient.client.create(BackEndApi::class.java)
            .userGpluslogin(requestFile)
            .enqueue(object : Callback<Object> {
                override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                    customDialogProgress!!.dismiss()

                    if (response!!.code() == 200) {
                        val `object` = JSONObject(Gson().toJson(response!!.body()))
                        Log.e("TAG Fb Login", "onResponse: $`object`")
                        if (`object`.get("success") == true) {
                            Log.e("chala ga bhai====", "onResponse: $`object`")

                            SessionManager.writeString(
                                this@WelcomeActivity,
                                Constant.token,
                                `object`.get("token").toString()
                            )

                            SessionManager.writeString(
                                this@WelcomeActivity, Constant.isPaidMember,
                                `object`.get("isPaidMember").toString()
                            )

                            if (`object`.get("isPaidMember") == true) {
                                Toast.makeText(
                                    this@WelcomeActivity,
                                    getString(R.string.login_successfully),
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@WelcomeActivity,
                                        MainActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                finishAffinity()

                            } else {
                                SessionManager.writeString(
                                    this@WelcomeActivity,
                                    Constant.token,
                                    `object`.get("token").toString()
                                )
                                SessionManager.writeString(
                                    this@WelcomeActivity,
                                    Constant.Fname,
                                    `object`.get("firstName").toString()
                                )

                                SessionManager.writeString(
                                    this@WelcomeActivity,
                                    Constant.Lname,
                                    `object`.get("lastName").toString()
                                )

                                startActivity(
                                    Intent(
                                        this@WelcomeActivity,
                                        ChooseMembershipActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                finishAffinity()
                            }

                        } else {
                            Toast.makeText(
                                this@WelcomeActivity,
                                `object`!!.get("message").toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@WelcomeActivity,
                            response!!.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }


                override fun onFailure(call: Call<Object>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(this@WelcomeActivity, t!!.message, Toast.LENGTH_SHORT).show()
                }

            }
            )

    }

}