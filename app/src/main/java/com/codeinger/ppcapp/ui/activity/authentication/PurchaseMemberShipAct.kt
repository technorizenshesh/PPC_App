package com.codeinger.ppcapp.ui.activity.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeinger.ppcapp.R
import com.codeinger.ppcapp.adapter.Add_Email_Adapter
import com.codeinger.ppcapp.adapter.Add_Phone_Number
import com.codeinger.ppcapp.databinding.ActivityPuchaseMembershipBinding
import com.codeinger.ppcapp.retrofit.Constant
import com.codeinger.ppcapp.ui.activity.membership.PurchseSuccessMembershipActivity
import com.codeinger.ppcapp.utils.CustomDialogProgress
import com.codeinger.ppcapp.utils.NetworkAvailablity
import com.codeinger.ppcapp.utils.SessionManager
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
import java.util.*

class PurchaseMemberShipAct : AppCompatActivity() {

    private lateinit var email_string: String
    private lateinit var phone_string: String
    private lateinit var adapter: Add_Email_Adapter
    private lateinit var adapter1: Add_Phone_Number
    var listEmail = ArrayList<String>()
    var listPhone = ArrayList<String>()

    private lateinit var lininaer: LinearLayoutManager
    private lateinit var lininaer1: LinearLayoutManager
    lateinit var binding: ActivityPuchaseMembershipBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuchaseMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {

        binding.tvAmount.setText("$" + SessionManager.readString(this, Constant.PlanAmount, ""))
        binding.tvYears.setText(
            SessionManager.readString(
                this,
                Constant.PlanYears,
                ""
            ) + " Membership"
        )

        binding.etFname.setText(SessionManager.readString(this, Constant.Fname, ""))
        binding.etLname.setText(SessionManager.readString(this, Constant.Lname, ""))
        binding.etEmail.setText(SessionManager.readString(this, Constant.Email, ""))

        binding.tvPurchase.setOnClickListener { validation() }

        adapter = this@PurchaseMemberShipAct?.let { Add_Email_Adapter(it, listEmail) }
        lininaer = LinearLayoutManager(this@PurchaseMemberShipAct)
        binding.recycleViewEmail.setLayoutManager(lininaer)
        binding.recycleViewEmail.setAdapter(adapter)


        adapter1 = this@PurchaseMemberShipAct?.let { Add_Phone_Number(it, listPhone) }
        lininaer1 = LinearLayoutManager(this@PurchaseMemberShipAct)
        binding.recycleViewPhone.setLayoutManager(lininaer1)
        binding.recycleViewPhone.setAdapter(adapter1)

        // add_btn_phone

        binding.addBtnPhone.setOnClickListener { v ->
            phone_string = binding.etphoneNumber.text.toString()

            if (!phone_string.equals("")) {
                listPhone.add(phone_string)
                adapter1.notifyDataSetChanged()
                binding.etphoneNumber.setText("")
            }
        }


        binding.addBtn.setOnClickListener { v ->
            email_string = binding.etEmail.text.toString()

            if (!email_string.equals("")) {
                listEmail.add(email_string)
                adapter.notifyDataSetChanged()
                binding.etEmail.setText("")
            }
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

            binding.etAddress1!!.text!!.isEmpty() -> {
                binding.etAddress1!!.error = getString(R.string.field_required)
            }

            binding.etAddress2!!.text!!.isEmpty() -> {
                binding.etAddress2!!.error = getString(R.string.field_required)
            }

            binding.etCity!!.text!!.isEmpty() -> {
                binding.etCity!!.error = getString(R.string.field_required)
            }

            binding.etProvince!!.text!!.isEmpty() -> {
                binding.etProvince!!.error = getString(R.string.field_required)
            }

            binding.etPostCode!!.text!!.isEmpty() -> {
                binding.etPostCode!!.error = getString(R.string.field_required)
            }

            else -> {
                if (NetworkAvailablity.checkNetworkStatus(this)) purcheseMemberShip()
                else Toast.makeText(this, getString(R.string.network_failure), Toast.LENGTH_LONG)
                    .show()
            }

        }
    }


    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun purcheseMemberShip() {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()
        val rootObject = JSONObject()

        val emailArray = JSONArray(listEmail)
        val phoneArray = JSONArray(listPhone)

        rootObject.put("firstName", binding.etFname!!.text.toString())
        rootObject.put("lastName", binding.etLname!!.text.toString())

        rootObject.put("isOptIn", true)
        rootObject.put("addressLine1", binding.etAddress1!!.text.toString())
        rootObject.put("addressLine2", binding.etAddress2!!.text.toString())
        rootObject.put("city", binding.etCity!!.text.toString())
        rootObject.put("province", binding.etProvince!!.text.toString())
        rootObject.put("postalCode", binding.etPostCode!!.text.toString())
        rootObject.put(
            "membershipPlanId",
            SessionManager.readString(this@PurchaseMemberShipAct, Constant.PlanId, "")
        )
        rootObject.put("paymentgatewaytranscationid", "125366hhfh")
        rootObject.put("paymentgatewayid", 1)
        rootObject.put(
            "amount",
            Integer.parseInt(
                SessionManager.readString(
                    this@PurchaseMemberShipAct,
                    Constant.PlanAmount,
                    ""
                )
            )
        )

        phoneArray.put(binding.etphoneNumber!!.text.toString())
        emailArray.put(binding.etEmail!!.text.toString())

        rootObject.put("email", emailArray)
        rootObject.put("phoneNumber", phoneArray)
        val requestFile =
            RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
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
                            startActivity(
                                Intent(
                                    this@PurchaseMemberShipAct,
                                    PurchseSuccessMembershipActivity::class.java
                                )
                            )
                            finish()
                        } else {
                            Toast.makeText(
                                this@PurchaseMemberShipAct,
                                `object`!!.get("message").toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@PurchaseMemberShipAct,
                            response!!.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }


                override fun onFailure(call: Call<Object>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(this@PurchaseMemberShipAct, t!!.message, Toast.LENGTH_SHORT)
                        .show()
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