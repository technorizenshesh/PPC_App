package com.codeinger.ppc_app.ui.activity.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codeinger.ppc_app.R
import com.codeinger.ppc_app.databinding.ActivitySiginBinding
import com.codeinger.ppc_app.model.MemberShipPlanModel
import com.codeinger.ppc_app.retrofit.Constant
import com.codeinger.ppc_app.ui.activity.MainActivity
import com.codeinger.ppc_app.ui.activity.membership.ChooseMembershipActivity
import com.codeinger.ppc_app.utils.CustomDialogProgress
import com.codeinger.ppc_app.utils.NetworkAvailablity
import com.codeinger.ppc_app.utils.SessionManager
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.iratepro.retrofit.BackEndApi
import com.iratepro.retrofit.WebServiceClient
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap


class SiginActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySiginBinding
    private val RC_SIGN_IN = 100
    private lateinit var callbackManager: CallbackManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGoogle()
        initFb()
        init()

    }

    private fun initFb() {
        callbackManager = CallbackManager.Factory.create()

        binding.ivFb.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(
                    this,
                    Arrays.asList("email", "public_profile", "user_friends")
                )
        }

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    // App code
                    Toast.makeText(this@SiginActivity, "Successfully sign in", Toast.LENGTH_SHORT)
                        .show()

                    // App code
                    val request = GraphRequest.newMeRequest(
                        loginResult!!.accessToken
                    ) { `object`: JSONObject, response: GraphResponse ->
                        Log.v("LoginActivity", response.toString())

                        // Application code
                        var email = ""
                        var id = ""
                        var name = ""
                        try {
                            email = `object`.getString("email")
                            name = `object`.getString("name")
                            id = `object`.getString("id")
                            Log.i("kdsndfv", "fb:id $id")
                            Log.i("kdsndfv", "fb:email $email")
                            Log.i("kdsndfv", "fb:name $name")

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,gender,birthday")
                    request.parameters = parameters
                    request.executeAsync()

                }

                override fun onCancel() {
                    // App code
                    Log.i("xsvdvd", "onCancel: " + 14)

                }


                override fun onError(exception: FacebookException) {
                    // App code
                    Log.i("xsvdvd", "onError: " + exception.toString())

                }

            })


    }

    private fun initGoogle() {

        val account = GoogleSignIn.getLastSignedInAccount(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.ivGoogle.setOnClickListener {
            signIn()
        }


    }

    fun init() {
        binding.tvForgetPassword.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
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


    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val personName = acct.displayName
                val personGivenName = acct.givenName
                val personFamilyName = acct.familyName
                val personEmail = acct.email
                val personId = acct.id
                val personPhoto: Uri = acct.photoUrl

                Log.i("kdsndfv", "handleSignInResult:id $personName")
                Log.i("kdsndfv", "handleSignInResult:email $personEmail")
                Log.i("kdsndfv", "handleSignInResult:name $personId")

                Toast.makeText(this, "Successfully sign in", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ChooseMembershipActivity::class.java)
                startActivity(intent)
            }


            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

        }
    }


//    private fun fb() {
//        //        binding.ivFb.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
////            override fun onSuccess(loginResult: LoginResult?) {
////                // App code
////            }
////
////            override fun onCancel() {
////                // App code
////            }
////
////            override fun onError(exception: FacebookException) {
////                // App code
////            }
////        })
//    }


}