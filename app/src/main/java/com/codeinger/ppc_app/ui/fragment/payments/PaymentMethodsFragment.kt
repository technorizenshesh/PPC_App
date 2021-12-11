package com.codeinger.ppc_app.ui.fragment.payments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codeinger.ppc_app.databinding.FragmentPaymentMethodsBinding
import com.codeinger.ppc_app.ui.activity.MainActivity
import com.codeinger.ppc_app.ui.activity.payment.MakeDonationActivity


class PaymentMethodsFragment : Fragment() {

    private lateinit var binding : FragmentPaymentMethodsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentPaymentMethodsBinding.inflate(layoutInflater)

        (activity as MainActivity).settext("Payment Methods")

        binding.tvContinue.setOnClickListener {
            val intent = Intent(requireContext(),MakeDonationActivity::class.java)
            startActivity(intent)
        }

        return (binding.root)
    }

}