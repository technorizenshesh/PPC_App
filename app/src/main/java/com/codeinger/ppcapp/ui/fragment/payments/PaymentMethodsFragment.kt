package com.codeinger.ppcapp.ui.fragment.payments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codeinger.ppcapp.CheckoutActivity
import com.codeinger.ppcapp.databinding.FragmentPaymentMethodsBinding
import com.codeinger.ppcapp.ui.activity.MainActivity
import com.codeinger.ppcapp.ui.activity.payment.MakeDonationActivity


class PaymentMethodsFragment : Fragment() {

    private lateinit var Gatway_Name: String
    private lateinit var binding: FragmentPaymentMethodsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentMethodsBinding.inflate(layoutInflater)

        (activity as MainActivity).settext("Payment Methods")

        binding.checkPaypal.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Gatway_Name = "Paypal"
                binding.checkGoogle.setChecked(false);
            }
        }

        binding.checkGoogle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Gatway_Name = "Google Pay"
                binding.checkPaypal.setChecked(false);
            }
        }

         binding.tvContinue.setOnClickListener {
            val intent = Intent(requireContext(), MakeDonationActivity::class.java)
                .putExtra("name",Gatway_Name)
            startActivity(intent)
        }

    /*    binding.googlePay.setOnClickListener {
            val intent = Intent(requireContext(), CheckoutActivity::class.java)
            startActivity(intent)
        }*/

        return (binding.root)
    }

}