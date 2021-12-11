package com.codeinger.ppc_app.ui.activity.membership

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeinger.ppc_app.R
import com.codeinger.ppc_app.databinding.ActivityChooseMembershipBinding
import com.codeinger.ppc_app.model.MemberShipPlanModel
import com.codeinger.ppc_app.retrofit.Constant
import com.codeinger.ppc_app.ui.activity.authentication.PurchaseMemberShipAct
import com.codeinger.ppc_app.ui.adapter.ChooseMembershipAdapter
import com.codeinger.ppc_app.utils.CustomDialogProgress
import com.codeinger.ppc_app.utils.NetworkAvailablity
import com.codeinger.ppc_app.utils.SessionManager
import com.google.gson.Gson
import com.iratepro.retrofit.BackEndApi
import com.iratepro.retrofit.WebServiceClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseMembershipActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseMembershipBinding
    var membershipList = ArrayList<MemberShipPlanModel>()
    var dataaaa = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChooseMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        if  (NetworkAvailablity.checkNetworkStatus(this)) getPlan()
        else Toast.makeText(this,getString(R.string.network_failure),Toast.LENGTH_LONG).show()
    }

    fun init(){
        binding.tvContinue.setOnClickListener {
            if(!SessionManager.readString(this,Constant.PlanId,"")!!.equals("")){
                val intent=Intent(this, PurchaseMemberShipAct::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(
                        this@ChooseMembershipActivity,
                      getString(R.string.please_select_plan),
                        Toast.LENGTH_SHORT
                )
                        .show()
            }

        }

    }


    private fun getPlan() {
         var customDialogProgress = CustomDialogProgress(this)
          customDialogProgress!!.show()
        WebServiceClient.client.create(BackEndApi::class.java).getMembershipPlan()
            .enqueue(object : Callback<List<MemberShipPlanModel>> {

                override fun onResponse(
                    call: Call<List<MemberShipPlanModel>>?,
                    response: Response<List<MemberShipPlanModel>>?
                ) {
                       customDialogProgress!!.dismiss()
                    if (response!!.code() == 200) {
                        dataaaa = JSONArray(Gson().toJson(response!!.body()))
                        // val fff =   Gson().toJson(response!!.body())
                        //   membershipList.addAll(dataaaa)
                        for (i in 0 until dataaaa.length()) {
                            val rootObject = JSONObject(dataaaa.get(i).toString())
                            membershipList.add(
                                MemberShipPlanModel(
                                    rootObject.get("membershipPlanId").toString(), Integer.parseInt(
                                        rootObject.get(
                                            "price"
                                        ).toString()
                                    ), rootObject.get("durationDisplay").toString()
                                )
                            )
                        }
                        val obj_adapter = ChooseMembershipAdapter(
                            this@ChooseMembershipActivity,
                            membershipList
                        )
                        binding.rvChooseMembership.layoutManager = LinearLayoutManager(
                            this@ChooseMembershipActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.rvChooseMembership.adapter = obj_adapter

                        Log.e("TAG", "onResponse: ${dataaaa.toString()}")
                        Log.e("TAG", "Size====: ${membershipList.size.toString()}")


                    } else {
                        Toast.makeText(
                            this@ChooseMembershipActivity,
                            response!!.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }


                override fun onFailure(call: Call<List<MemberShipPlanModel>>?, t: Throwable?) {
                         customDialogProgress!!.dismiss()
                    Toast.makeText(this@ChooseMembershipActivity, t!!.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })







    }

}