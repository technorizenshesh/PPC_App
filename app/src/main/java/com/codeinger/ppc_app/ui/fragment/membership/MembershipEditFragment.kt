package com.codeinger.ppc_app.ui.fragment.membership

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codeinger.ppc_app.databinding.FragmentMembershipEditBinding
import com.codeinger.ppc_app.ui.activity.MainActivity


class MembershipEditFragment : Fragment() {

    private lateinit var binding : FragmentMembershipEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentMembershipEditBinding.inflate(layoutInflater)

        (activity as MainActivity).settext("Edit Membership Details")
        return (binding.root)
    }

}