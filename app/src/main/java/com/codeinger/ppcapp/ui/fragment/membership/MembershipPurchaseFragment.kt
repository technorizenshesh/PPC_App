package com.codeinger.ppcapp.ui.fragment.membership

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codeinger.ppcapp.databinding.FragmentMembershipPurchaseBinding
import com.codeinger.ppcapp.ui.activity.MainActivity
import com.codeinger.ppcapp.ui.fragment.payments.PaymentsFragment

class MembershipPurchaseFragment : Fragment() {

    private lateinit var binding : FragmentMembershipPurchaseBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentMembershipPurchaseBinding.inflate(layoutInflater)

        (activity as MainActivity).settext("Membership Purchase")

        binding.tvPurchase.setOnClickListener {

            (activity as MainActivity).replace(PaymentsFragment(),"add")
        }

        return (binding.root)
    }


}