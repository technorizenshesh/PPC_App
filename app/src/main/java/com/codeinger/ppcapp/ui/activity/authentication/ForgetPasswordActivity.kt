package com.codeinger.ppcapp.ui.activity.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.codeinger.ppcapp.R
import com.codeinger.ppcapp.databinding.ActivityForgetPasswordBinding
import com.codeinger.ppcapp.utils.CustomDialogProgress
import com.codeinger.ppcapp.utils.NetworkAvailablity
import com.google.gson.Gson
import com.iratepro.retrofit.BackEndApi
import com.iratepro.retrofit.WebServiceClient
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    fun init(){
        binding.ivBack.setOnClickListener{finish()}

        binding.tvReset.setOnClickListener {
            validation()
        }
    }


    private fun validation() {
        when {

            binding.etOldPass!!.text!!.isEmpty() -> {
                binding.etOldPass!!.error = getString(R.string.field_required)
            }

            binding.etNewPass!!.text!!.isEmpty() -> {
                binding.etNewPass!!.error = getString(R.string.field_required)
            }

             binding.etConfirmPass!!.text!!.isEmpty() -> {
               binding.etConfirmPass!!.error = getString(R.string.field_required)
            }

            binding.etConfirmPass!!.text!!.equals(binding.etNewPass!!.text!!) -> {
                Toast.makeText(this, getString(R.string.password_do_matched), Toast.LENGTH_LONG).show()
            }


            else -> {
                if (NetworkAvailablity.checkNetworkStatus(this)) ChangePassword()
                else Toast.makeText(this, getString(R.string.network_failure), Toast.LENGTH_LONG)
                    .show()

            }

        }


    }




    private fun ChangePassword() {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()

        val rootObject= JSONObject()
        rootObject.put("oldPassword", binding.etOldPass!!.text.toString())
        rootObject.put("lastName", binding.etNewPass!!.text.toString())

        val requestFile = RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG=ChangePasswordJson=", "kkkkkkkk: $requestFile")
        Log.e("TAG=ChangePasswordJson=", "kkkkkkkk: $rootObject")
        WebServiceClient.client1.create(BackEndApi::class.java).userResetPass(requestFile)
            .enqueue(object : Callback<Object> {
                override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                    customDialogProgress!!.dismiss()
                    if (response!!.code() == 200) {
                        val `object` = JSONObject(Gson().toJson(response!!.body()))
                        Log.e("TAG", "ChangePassword onResponse: $`object`")
                        if (`object`.get("success") == true) {
                            Toast.makeText(this@ForgetPasswordActivity, getString(R.string.changed_succesfully), Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@ForgetPasswordActivity, `object`!!.get("message").toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ForgetPasswordActivity, response!!.message(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }


                override fun onFailure(call: Call<Object>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(this@ForgetPasswordActivity, t!!.message, Toast.LENGTH_SHORT).show()
                }

            })







    }
}