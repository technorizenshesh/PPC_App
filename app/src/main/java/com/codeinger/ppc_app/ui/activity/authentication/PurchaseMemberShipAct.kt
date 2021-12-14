package com.codeinger.ppc_app.ui.activity.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codeinger.ppc_app.R
import com.codeinger.ppc_app.databinding.ActivityPuchaseMembershipBinding
import com.codeinger.ppc_app.retrofit.Constant
import com.codeinger.ppc_app.ui.activity.membership.ChooseMembershipActivity
import com.codeinger.ppc_app.ui.activity.membership.PurchseSuccessMembershipActivity
import com.codeinger.ppc_app.utils.CustomDialogProgress
import com.codeinger.ppc_app.utils.NetworkAvailablity
import com.codeinger.ppc_app.utils.SessionManager
import com.google.gson.Gson
import com.iratepro.retrofit.BackEndApi
import com.iratepro.retrofit.WebServiceClient
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchaseMemberShipAct : AppCompatActivity() {
    lateinit var binding: ActivityPuchaseMembershipBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuchaseMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        binding.tvAmount.setText("$"+SessionManager.readString(this,Constant.PlanAmount,""))
        binding.tvYears.setText(SessionManager.readString(this,Constant.PlanYears,"") + " Membership")
        binding.etFname.setText(SessionManager.readString(this,Constant.Fname,""))
        binding.etLname.setText(SessionManager.readString(this,Constant.Lname,""))
        binding.etEmail.setText(SessionManager.readString(this,Constant.Email,""))

        binding.tvPurchase.setOnClickListener {validation()}

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

            binding.etphoneNumber!!.text!!.isEmpty()->{
                binding.etphoneNumber!!.error = getString(R.string.field_required)
            }

            binding.etAddress1!!.text!!.isEmpty()->{
                binding.etAddress1!!.error = getString(R.string.field_required)
            }

            binding.etAddress2!!.text!!.isEmpty()->{
                binding.etAddress2!!.error = getString(R.string.field_required)
            }

            binding.etCity!!.text!!.isEmpty()->{
                binding.etCity!!.error = getString(R.string.field_required)
            }

            binding.etProvince!!.text!!.isEmpty()->{
                binding.etProvince!!.error = getString(R.string.field_required)
            }


            binding.etPostCode!!.text!!.isEmpty()->{
                binding.etPostCode!!.error = getString(R.string.field_required)
            }

            else ->{
                if  (NetworkAvailablity.checkNetworkStatus(this)) purcheseMemberShip()
                else Toast.makeText(this,getString(R.string.network_failure), Toast.LENGTH_LONG).show()
            }

        }

    }


    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun purcheseMemberShip() {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()
        val rootObject= JSONObject()
        val  emailArray = JSONArray()
        val  phoneArray = JSONArray()
        rootObject.put("firstName",binding.etFname!!.text.toString())
        rootObject.put("lastName",binding.etLname!!.text.toString())
        emailArray.put(binding.etEmail!!.text.toString())
        rootObject.put("isOptIn",true)
        rootObject.put("addressLine1",binding.etAddress1!!.text.toString())
        rootObject.put("addressLine2",binding.etAddress2!!.text.toString())
        rootObject.put("city",binding.etCity!!.text.toString())
        rootObject.put("province",binding.etProvince!!.text.toString())
        rootObject.put("postalCode",binding.etPostCode!!.text.toString())
        phoneArray.put(binding.etphoneNumber!!.text.toString())
        rootObject.put("membershipPlanId",SessionManager.readString(this@PurchaseMemberShipAct,Constant.PlanId,""))
        rootObject.put("paymentGatewayTranscationId","125366hhfh")
        rootObject.put("paymentGatewayId",1)
        rootObject.put("amount",Integer.parseInt(SessionManager.readString(this@PurchaseMemberShipAct,Constant.PlanAmount,"")))
        rootObject.put("email",emailArray)
        rootObject.put("phoneNumber",phoneArray)
        val requestFile = RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG=====Json===", "kkkkkkkk: $requestFile")
        Log.e("TAG=====Json===", "kkkkkkkk: $rootObject")
        WebServiceClient.client1.create(BackEndApi::class.java).createMemberShip(requestFile)
                .enqueue(object : Callback<Object> {

                    override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                        customDialogProgress!!.dismiss()
                        if (response!!.code() == 200) {
                            val `object` = JSONObject(Gson().toJson(response!!.body()))
                            Log.e("TAG", "Create MemberShip onResponse: $`object`")
                            if (`object`.get("success") == true) {
                                startActivity(Intent(this@PurchaseMemberShipAct, PurchseSuccessMembershipActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@PurchaseMemberShipAct, `object`!!.get("message").toString(), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@PurchaseMemberShipAct, response!!.message(), Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }


                    override fun onFailure(call: Call<Object>?, t: Throwable?) {
                        customDialogProgress!!.dismiss()
                        Toast.makeText(this@PurchaseMemberShipAct, t!!.message, Toast.LENGTH_SHORT).show()
                    }

                })







    }



   /* private fun purcheseMemberShip1() {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()


        val rootObject= JSONObject()
        val  emailArray = JSONArray()
        val  phoneArray = JSONArray()

        rootObject.put("firstName","rrr")
        rootObject.put("lastName","tttt")
        emailArray.put("tt@gmail.com")
        rootObject.put("isOptIn",true)
        rootObject.put("addressLine1","Indore")
        rootObject.put("addressLine2","Indore")
        rootObject.put("city","Indore")
        rootObject.put("province","Mp")
        rootObject.put("postalCode","472001")
        phoneArray.put("9876543210")
        rootObject.put("membershipPlanId","e633b911-6973-4d3a-9524-188efdf8d0ce")
        rootObject.put("paymentGatewayTranscationId","125366hhfh")
        rootObject.put("paymentGatewayId",1)
        rootObject.put("amount",20)
        rootObject.put("email",emailArray)
        rootObject.put("phoneNumber",phoneArray)
        val requestFile = RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG=====Json===", "kkkkkkkk: $requestFile")
        Log.e("TAG=====Json===", "kkkkkkkk: $rootObject")
        WebServiceClient.client1.create(BackEndApi::class.java).createMemberShip(requestFile)
                .enqueue(object : Callback<Object> {

                    override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                        customDialogProgress!!.dismiss()
                        if (response!!.code() == 200) {
                            val `object` = JSONObject(Gson().toJson(response!!.body()))
                            Log.e("TAG", "Create MemberShip onResponse: $`object`")
                            if (`object`.get("success") == true) {
                                startActivity(Intent(this@PurchaseMemberShipAct, PurchseSuccessMembershipActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@PurchaseMemberShipAct, `object`!!.get("message").toString(), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@PurchaseMemberShipAct, response!!.message(), Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }


                    override fun onFailure(call: Call<Object>?, t: Throwable?) {
                        customDialogProgress!!.dismiss()
                        Toast.makeText(this@PurchaseMemberShipAct, t!!.message, Toast.LENGTH_SHORT).show()
                    }

                })







    }*/

}