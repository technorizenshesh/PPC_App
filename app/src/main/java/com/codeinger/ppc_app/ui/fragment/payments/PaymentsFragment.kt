package com.codeinger.ppc_app.ui.fragment.payments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeinger.ppc_app.databinding.FragmentPaymentsBinding
import com.codeinger.ppc_app.ui.activity.MainActivity
import com.codeinger.ppc_app.adapter.PaymentAdapter


class PaymentsFragment : Fragment() {

    private lateinit var binding: FragmentPaymentsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentPaymentsBinding.inflate(layoutInflater)
        (activity as MainActivity).settext("Payments")

        val obj_adapter = PaymentAdapter{
            (activity as MainActivity).replace(PaymentMethodsFragment(),"add")
        }

        binding.rvPurchase.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPurchase.adapter = obj_adapter

        init()

        return binding.root
    }

    fun init() {
        binding.tvSaveDetails.setOnClickListener {

        }
    }
}