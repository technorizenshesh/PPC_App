package com.codeinger.ppc_app.ui.activity.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.codeinger.ppc_app.R
import com.codeinger.ppc_app.databinding.ActivitySignUpBinding
import com.codeinger.ppc_app.retrofit.Constant
import com.codeinger.ppc_app.ui.activity.MainActivity
import com.codeinger.ppc_app.ui.activity.membership.ChooseMembershipActivity
import com.codeinger.ppc_app.utils.CustomDialogProgress
import com.codeinger.ppc_app.utils.NetworkAvailablity
import com.codeinger.ppc_app.utils.SessionManager
import com.google.gson.Gson
import com.iratepro.retrofit.BackEndApi
import com.iratepro.retrofit.WebServiceClient
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    fun init(){
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
        when{

            binding.etFname!!.text!!.isEmpty()->{
                binding.etFname!!.error = getString(R.string.field_required)
            }

            binding.etLname!!.text!!.isEmpty()->{
                binding.etLname!!.error = getString(R.string.field_required)
            }

            binding.etEmail!!.text!!.isEmpty()->{
                binding.etEmail!!.error = getString(R.string.field_required)
            }
            !isValidEmail(binding.etEmail!!.text.toString())->{
                binding.etEmail!!.error = getString(R.string.wrong_email)
            }
            binding.etPassword!!.text!!.isEmpty()->{
                binding.etPassword!!.error = getString(R.string.field_required)
            }

            else ->{
                if  (NetworkAvailablity.checkNetworkStatus(this)) userSignuppp()
                else Toast.makeText(this,getString(R.string.network_failure),Toast.LENGTH_LONG).show()
            }

        }

    }


    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun userSignuppp() {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()
        val rootObject= JSONObject()
        rootObject.put("firstName",binding.etFname!!.text.toString())
        rootObject.put("lastName",binding.etLname!!.text.toString())
        rootObject.put("email",binding.etEmail!!.text.toString())
        rootObject.put("password",binding.etPassword!!.text.toString())
        rootObject.put("deviceType",1)
        rootObject.put("devicePushNotificationToken","3134fhhh")

        val requestFile = RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG=====Json===", "kkkkkkkk: $requestFile.toString()")
        WebServiceClient.client.create(BackEndApi::class.java).signupUser( requestFile)
            .enqueue(object : Callback<Object> {

                override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                    customDialogProgress!!.dismiss()
                    if (response!!.code() == 200) {
                        val `object` = JSONObject(Gson().toJson(response!!.body()))
                        Log.e("TAG", "Singup onResponse: $`object`")
                        if (`object`.get("success") == true) {
                            Log.e("chala ga bhai====", "onResponse: $`object`")
                            Toast.makeText(this@SignUpActivity, `object`!!.get("message").toString(), Toast.LENGTH_SHORT).show()
                            SessionManager.writeString(this@SignUpActivity, Constant.token,`object`.get("token").toString())
                            SessionManager.writeString(this@SignUpActivity, Constant.Fname,binding.etFname!!.text.toString())
                            SessionManager.writeString(this@SignUpActivity, Constant.Lname,binding.etLname!!.text.toString())
                            SessionManager.writeString(this@SignUpActivity, Constant.Email,binding.etEmail!!.text.toString())
                            startActivity(Intent(this@SignUpActivity, ChooseMembershipActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@SignUpActivity, `object`!!.get("message").toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SignUpActivity, response!!.message(), Toast.LENGTH_SHORT)
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