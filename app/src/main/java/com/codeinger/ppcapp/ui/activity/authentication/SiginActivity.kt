package com.codeinger.ppcapp.ui.activity.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codeinger.ppcapp.R
import com.codeinger.ppcapp.databinding.ActivitySiginBinding
import com.codeinger.ppcapp.retrofit.Constant
import com.codeinger.ppcapp.ui.activity.MainActivity
import com.codeinger.ppcapp.ui.activity.membership.ChooseMembershipActivity
import com.codeinger.ppcapp.utils.CustomDialogProgress
import com.codeinger.ppcapp.utils.NetworkAvailablity
import com.codeinger.ppcapp.utils.SessionManager
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
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


class SiginActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySiginBinding
    private val RC_SIGN_IN = 100

    /*    private lateinit var callbackManager: CallbackManager
        private lateinit var mGoogleSignInClient: GoogleSignInClient*/
    private val EMAIL = "email"

    private var mAuth: FirebaseAuth? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    val GOOGLE_SIGN_IN_REQUEST_CODE = 1234
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiginBinding.inflate(layoutInflater)

        sosiallogin()

        // Initialize Firebase Auth

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()


        setContentView(binding.root)

        initGoogle()
        initFb()
        init()
        // SetuUI()

    }

    private fun sosiallogin() {

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
                })
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
                        Log.e("kjsgdfkjdgsf", "Userid = " + user.uid)

                        socialLoginCall(
                            user.displayName, user.email, profilePhoto,
                            user.uid
                        )

                    } else {
                        Toast.makeText(
                            this@SiginActivity,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
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

        displayName?.let { Log.e("displayName>>>>", it) }
        profilePhoto?.let { Log.e("profilePhoto>>>>", it) }
        email?.let { Log.e("email>>>>", it) }
    }

    private fun initFb() {

    }

    private fun initGoogle() {


    }

    fun init() {

        binding.tvForgetPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassAct::class.java)
            startActivity(intent)
        }

        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, ChooseMembershipActivity::class.java)
            startActivity(intent)
        }

        binding.tvSignIn.setOnClickListener { validation() }

    }

    private fun validation() {
        when {
            binding.etEmail!!.text!!.isEmpty() -> {
                binding.etEmail!!.error = getString(R.string.field_required)
            }
            !isValidEmail(binding.etEmail!!.text.toString()) -> {
                binding.etEmail!!.error = getString(R.string.wrong_email)
            }
            binding.etPassword!!.text!!.isEmpty() -> {
                binding.etPassword!!.error = getString(R.string.field_required)
            }


            else -> {
                if (NetworkAvailablity.checkNetworkStatus(this)) userLogin()
                else Toast.makeText(this, getString(R.string.network_failure), Toast.LENGTH_LONG)
                    .show()

            }

        }


    }

    private fun userLogin() {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()
        val rootObject = JSONObject()
        rootObject.put("email", binding.etEmail!!.text.toString())
        rootObject.put("password", binding.etPassword!!.text.toString())
        rootObject.put("deviceType", 1)
        rootObject.put("devicePushNotificationToken", "1234dbfth")

        val requestFile =
            RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG=====Json===", "kkkkkkkk: $requestFile.toString()")
        WebServiceClient.client.create(BackEndApi::class.java).login(/*header,*/ requestFile)
            .enqueue(object : Callback<Object> {

                override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                    customDialogProgress!!.dismiss()
                    if (response!!.code() == 200) {
                        val `object` = JSONObject(Gson().toJson(response!!.body()))
                        Log.e("TAG", "onResponse: $`object`")
                        if (`object`.get("success") == true) {
                            Log.e("chala ga bhai====", "onResponse: $`object`")

                            SessionManager.writeString(
                                this@SiginActivity,
                                Constant.token,
                                `object`.get("token").toString()
                            )

                            if (`object`.get("isPaidMember") == true) {
                                Toast.makeText(
                                    this@SiginActivity,
                                    getString(R.string.login_successfully),
                                    Toast.LENGTH_SHORT
                                ).show()

                                startActivity(
                                    Intent(
                                        this@SiginActivity,
                                        MainActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                finishAffinity()

                            } else {
                                SessionManager.writeString(
                                    this@SiginActivity,
                                    Constant.token,
                                    `object`.get("token").toString()
                                )
                                SessionManager.writeString(
                                    this@SiginActivity,
                                    Constant.Fname,
                                    `object`.get("firstName").toString()
                                )
                                SessionManager.writeString(
                                    this@SiginActivity,
                                    Constant.Lname,
                                    `object`.get("lastName").toString()
                                )
                                SessionManager.writeString(
                                    this@SiginActivity,
                                    Constant.Email,
                                    binding.etEmail!!.text.toString()
                                )

                                startActivity(
                                    Intent(
                                        this@SiginActivity,
                                        ChooseMembershipActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )

                                finishAffinity()
                            }

                        } else {
                            Toast.makeText(
                                this@SiginActivity,
                                `object`!!.get("message").toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this@SiginActivity, response!!.message(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }


                override fun onFailure(call: Call<Object>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(this@SiginActivity, t!!.message, Toast.LENGTH_SHORT).show()
                }

            })


    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            /* Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);*/
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {

            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                    if (user != null) {
                        Log.e("kjsgdfkjdgsf", "profilePhoto = " + user.photoUrl)
                        Log.e("kjsgdfkjdgsf", "name = " + user.displayName)
                        Log.e("kjsgdfkjdgsf", "email = " + user.email)
                        Log.e("kjsgdfkjdgsf", "Userid = " + user.uid)
                        socialLoginCall(
                            user.displayName,
                            user.email, user.photoUrl.toString(),
                            user.uid
                        )
                    }
                } else {
                }
            }
    }
}