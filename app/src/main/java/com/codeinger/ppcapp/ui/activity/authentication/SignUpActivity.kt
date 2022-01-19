package com.codeinger.ppcapp.ui.activity.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.codeinger.ppcapp.R
import com.codeinger.ppcapp.databinding.ActivitySignUpBinding
import com.codeinger.ppcapp.retrofit.Constant
import com.codeinger.ppcapp.ui.activity.MainActivity
import com.codeinger.ppcapp.ui.activity.membership.ChooseMembershipActivity
import com.codeinger.ppcapp.utils.CustomDialogProgress
import com.codeinger.ppcapp.utils.NetworkAvailablity
import com.codeinger.ppcapp.utils.SessionManager
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
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

class SignUpActivity : AppCompatActivity() {

    private val EMAIL = "email"
    private var mAuth: FirebaseAuth? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    val GOOGLE_SIGN_IN_REQUEST_CODE = 1234
    private var callbackManager: CallbackManager? = null

    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        callbackManager = CallbackManager.Factory.create()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        initFb()
        init()
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
                        Log.e("kjsgdfkjdgsf", "UserID = " + user.uid)
                        Log.e("token_facebook>>>", token.toString())

                        socialLoginCall(user.displayName, user.email, profilePhoto, user.uid)

                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
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
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()
        val rootObject = JSONObject()
        rootObject.put("firstName", displayName)
        rootObject.put("lastName", displayName)
        rootObject.put("email", email)
        rootObject.put("facebookToken", uid)
        rootObject.put("devicePushNotificationToken", "1234dbfth")

        val requestFile = RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG=====Json===", "kkkkkkkk: $requestFile.toString()")

        WebServiceClient.client.create(BackEndApi::class.java).userFblogin(/*header,*/ requestFile)
            .enqueue(object : Callback<Object> {

                override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                    customDialogProgress!!.dismiss()
                    if (response!!.code() == 200) {
                        val `object` = JSONObject(Gson().toJson(response!!.body()))

                        Log.e("TAG Fb Login", "onResponse: $`object`")
                        if (`object`.get("success") == true) {
                            Log.e("chala ga bhai====", "onResponse: $`object`")

                            SessionManager.writeString(
                                this@SignUpActivity, Constant.token,
                                `object`.get("token").toString()
                            )

                            SessionManager.writeString(
                                this@SignUpActivity, Constant.isPaidMember,
                                `object`.get("isPaidMember").toString()
                            )

                            if (`object`.get("isPaidMember") == true) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    getString(R.string.login_successfully),
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@SignUpActivity,
                                        MainActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                finishAffinity()
                            } else {
                                SessionManager.writeString(
                                    this@SignUpActivity,
                                    Constant.token,
                                    `object`.get("token").toString()
                                )
                                SessionManager.writeString(
                                    this@SignUpActivity,
                                    Constant.Fname,
                                    `object`.get("firstName").toString()
                                )


                                SessionManager.writeString(
                                    this@SignUpActivity,
                                    Constant.Lname,
                                    `object`.get("lastName").toString()
                                )

                                /*           SessionManager.writeString(
                                               this@WelcomeActivity,
                                               Constant.Email,
                                               `object`.get("email").toString())*/

                                startActivity(
                                    Intent(
                                        this@SignUpActivity,
                                        ChooseMembershipActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                finishAffinity()
                            }

                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                `object`!!.get("message").toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            response!!.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<Object>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(this@SignUpActivity, t!!.message, Toast.LENGTH_SHORT).show()
                }

            })

    }

    fun init() {
        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, SiginActivity::class.java)
            startActivity(intent)
        }

        binding.tvSignUp.setOnClickListener {
            /* val intent = Intent(this, ChooseMembershipActivity::class.java)
             startActivity(intent)*/
            validation();
        }

    }


    private fun validation() {
        when {

            binding.etFname!!.text!!.isEmpty() -> {
                binding.etFname!!.error = getString(R.string.field_required)
            }

            binding.etLname!!.text!!.isEmpty() -> {
                binding.etLname!!.error = getString(R.string.field_required)
            }

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
                if (NetworkAvailablity.checkNetworkStatus(this)) userSignuppp()
                else Toast.makeText(this, getString(R.string.network_failure), Toast.LENGTH_LONG)
                    .show()
            }

        }

    }


    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun userSignuppp() {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()
        val rootObject = JSONObject()
        rootObject.put("firstName", binding.etFname!!.text.toString())
        rootObject.put("lastName", binding.etLname!!.text.toString())
        rootObject.put("email", binding.etEmail!!.text.toString())
        rootObject.put("password", binding.etPassword!!.text.toString())
        rootObject.put("deviceType", 1)
        rootObject.put("devicePushNotificationToken", "3134fhhh")

        val requestFile =
            RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG=====Json===", "kkkkkkkk: $requestFile.toString()")

        WebServiceClient.client1.create(BackEndApi::class.java).signupUser(requestFile)
            .enqueue(object : Callback<Object> {

                override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                    customDialogProgress!!.dismiss()
                    if (response!!.code() == 200) {
                        val `object` = JSONObject(Gson().toJson(response!!.body()))
                        Log.e("TAG", "Singup onResponse: $`object`")
                        if (`object`.get("success") == true) {
                            Log.e("chala ga bhai====", "onResponse: $`object`")
                            // Toast.makeText(this@SignUpActivity, `object`!!.get("message").toString(), Toast.LENGTH_SHORT).show()
                            SessionManager.writeString(
                                this@SignUpActivity,
                                Constant.token,
                                `object`.get("token").toString()
                            )

                            SessionManager.writeString(
                                this@SignUpActivity,
                                Constant.Fname,
                                binding.etFname!!.text.toString()
                            )
                            SessionManager.writeString(
                                this@SignUpActivity,
                                Constant.Lname,
                                binding.etLname!!.text.toString()
                            )
                            SessionManager.writeString(
                                this@SignUpActivity,
                                Constant.Email,
                                binding.etEmail!!.text.toString()
                            )
                            startActivity(
                                Intent(
                                    this@SignUpActivity,
                                    ChooseMembershipActivity::class.java
                                )
                            )
                            finish()
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                `object`!!.get("message").toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            response!!.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }


                override fun onFailure(call: Call<Object>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(this@SignUpActivity, t!!.message, Toast.LENGTH_SHORT).show()
                }

            })

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
                                this@SignUpActivity,
                                Constant.token,
                                `object`.get("token").toString()
                            )

                            SessionManager.writeString(
                                this@SignUpActivity, Constant.isPaidMember,
                                `object`.get("isPaidMember").toString()
                            )

                            if (`object`.get("isPaidMember") == true) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    getString(R.string.login_successfully),
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@SignUpActivity,
                                        MainActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                finishAffinity()

                            } else {
                                SessionManager.writeString(
                                    this@SignUpActivity,
                                    Constant.token,
                                    `object`.get("token").toString()
                                )
                                SessionManager.writeString(
                                    this@SignUpActivity,
                                    Constant.Fname,
                                    `object`.get("firstName").toString()
                                )

                                SessionManager.writeString(
                                    this@SignUpActivity,
                                    Constant.Lname,
                                    `object`.get("lastName").toString()
                                )

                                startActivity(
                                    Intent(
                                        this@SignUpActivity, ChooseMembershipActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                finishAffinity()
                            }

                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                `object`!!.get("message").toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            response!!.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }


                override fun onFailure(call: Call<Object>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(this@SignUpActivity, t!!.message, Toast.LENGTH_SHORT).show()
                }

            })
    }
}