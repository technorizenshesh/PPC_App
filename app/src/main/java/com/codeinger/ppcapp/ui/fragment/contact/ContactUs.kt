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
import com.codeinger.ppcapp.databinding.ContactUsBinding
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


class ContactUs : Fragment() {

    private lateinit var dataaaa: JSONArray
    private lateinit var binding: ContactUsBinding
    var membershipList = ArrayList<GetTranscationsModal>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        binding = ContactUsBinding.inflate(layoutInflater)
        (activity as MainActivity).settext("Contact Us ")

        return binding.root
    }


}