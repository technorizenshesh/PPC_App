package com.codeinger.ppc_app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.codeinger.ppc_app.R
import com.codeinger.ppc_app.databinding.ActivityMainBinding
import com.codeinger.ppc_app.ui.fragment.membership.MembershipPurchaseFragment
import com.codeinger.ppc_app.ui.fragment.membership.MyMembershipFragment
import com.codeinger.ppc_app.utils.SessionManager


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        replace(MyMembershipFragment())

        binding.llMyMembership.setOnClickListener {
            replace(MyMembershipFragment())
            binding.drawer.closeDrawer(GravityCompat.START)
        }

        binding.llPurchase.setOnClickListener {
            replace(MembershipPurchaseFragment(), "add")
            binding.drawer.closeDrawer(GravityCompat.START)

        }


        binding.llLogout.setOnClickListener{ SessionManager.clear(this)}



        binding.ivMenu.setOnClickListener {

            if (binding.drawer.isDrawerVisible(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            } else {
                binding.drawer.openDrawer(GravityCompat.START)
            }
        }


    }

    fun replace(fragment: Fragment, add: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame, fragment)
        ft.addToBackStack(add)
        ft.commit()
    }

    fun replace(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame, fragment)
        ft.commit()
    }

    fun settext(string: String) {
        binding.tvTitle.text = string
    }


    fun setNavigationText(name: String) {
        binding.tvProfile.text = name
    }


}