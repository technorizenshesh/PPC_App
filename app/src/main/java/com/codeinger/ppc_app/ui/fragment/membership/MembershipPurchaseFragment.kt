package com.codeinger.ppc_app.ui.fragment.membership

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codeinger.ppc_app.databinding.FragmentMembershipPurchaseBinding
import com.codeinger.ppc_app.ui.activity.MainActivity
import com.codeinger.ppc_app.ui.fragment.payments.PaymentsFragment

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