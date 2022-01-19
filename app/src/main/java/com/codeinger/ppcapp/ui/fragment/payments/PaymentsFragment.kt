package com.codeinger.ppcapp.ui.fragment.payments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeinger.ppcapp.databinding.FragmentPaymentsBinding
import com.codeinger.ppcapp.ui.activity.MainActivity
import com.codeinger.ppcapp.adapter.PaymentAdapter
import com.codeinger.ppcapp.model.GetTranscationsModal
import com.codeinger.ppcapp.model.MemberShipPlanModel
import com.codeinger.ppcapp.utils.CustomDialogProgress
import com.google.gson.Gson
import com.iratepro.retrofit.BackEndApi
import com.iratepro.retrofit.WebServiceClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PaymentsFragment : Fragment() {

    private lateinit var dataaaa: JSONArray
    private lateinit var binding: FragmentPaymentsBinding
    var membershipList = ArrayList<GetTranscationsModal>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        binding = FragmentPaymentsBinding.inflate(layoutInflater)
        (activity as MainActivity).settext("Payments")

        /*      val obj_adapter = PaymentAdapter({
                  // (activity as MainActivity).replace(PaymentMethodsFragment(), "add")
              }, membershipList)*/

        GetTranscationsList()

        /*  binding.rvPurchase.layoutManager = LinearLayoutManager(requireContext())
          binding.rvPurchase.adapter = obj_adapter*/

        init()


        return binding.root
    }

    private fun GetTranscationsList() {

        var customDialogProgress = context?.let { CustomDialogProgress(it) }
        customDialogProgress!!.show()
        WebServiceClient.client1.create(BackEndApi::class.java).GetTranscationsList()
            .enqueue(object : Callback<List<GetTranscationsModal>> {

                override fun onResponse(
                    call: Call<List<GetTranscationsModal>>?,
                    response: Response<List<GetTranscationsModal>>?
                ) {
                    customDialogProgress!!.dismiss()

                    if (response!!.code() == 200) {
                        dataaaa = JSONArray(Gson().toJson(response!!.body()))
                        // val fff =   Gson().toJson(response!!.body())
                        //   membershipList.addAll(dataaaa)

                        for (i in 0 until dataaaa.length()) {
                            val rootObject = JSONObject(dataaaa.get(i).toString())
                            membershipList.add(
                                GetTranscationsModal(
                                    rootObject.get("title").toString(),
                                    Integer.parseInt(
                                        rootObject.get(
                                            "amount"
                                        ).toString()
                                    ),
                                    rootObject.get("transactionType").toString(),
                                    rootObject.get("transactionDate").toString(),
                                    rootObject.get("transactionTime").toString()
                                )
                            )
                        }

                        val obj_adapter = context?.let {
                            PaymentAdapter(it, membershipList)

                        }


                        binding.rvPurchase.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvPurchase.adapter = obj_adapter

                        /*        binding.rvChooseMembership.layoutManager = LinearLayoutManager(
                                    this@ChooseMembershipActivity,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )

                                binding.rvChooseMembership.adapter = obj_adapter*/

                        Log.e("TAG", "onResponse: ${dataaaa.toString()}")
                        Log.e("TAG", "Size====: ${membershipList.size.toString()}")


                    } else {
                        Toast.makeText(
                            context,
                            response!!.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<List<GetTranscationsModal>>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(context, t!!.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })

    }

    fun init() {
        binding.tvSaveDetails.setOnClickListener {
            (activity as MainActivity).replace(PaymentMethodsFragment(), "add")
        }
    }
}