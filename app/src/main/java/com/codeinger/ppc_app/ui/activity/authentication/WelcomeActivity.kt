package com.codeinger.ppc_app.ui.activity.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codeinger.ppc_app.databinding.ActivityWelcomeBinding
import com.codeinger.ppc_app.retrofit.Constant
import com.codeinger.ppc_app.ui.activity.MainActivity
import com.codeinger.ppc_app.ui.activity.membership.ChooseMembershipActivity
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
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class WelcomeActivity : AppCompatActivity() {
    private var SCREEN_TIME_OUT = 3000
    private lateinit var binding: ActivityWelcomeBinding
    private val RC_SIGN_IN = 100
    private lateinit var callbackManager: CallbackManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initGoogle()
        initFb()

    }
    fun init(){
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
        if(!SessionManager.readString(this@WelcomeActivity,Constant.token,"").equals("")){
            Handler().postDelayed(Runnable {
                startActivity(Intent(this, MainActivity::class.java))
                finish()


            }, SCREEN_TIME_OUT.toLong())
        }
        else{


        }
    }

    private fun initFb() {
        callbackManager = CallbackManager.Factory.create()

        binding.ivFb.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(
                    this,
                    Arrays.asList("email", "public_profile","user_friends")
                )
        }

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    // App code
                    Toast.makeText(this@WelcomeActivity, "Successfully sign in", Toast.LENGTH_SHORT)
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

            Log.i("kdsndfv", "fail:name ${e.message}")

        }
    }


}