package com.codeinger.ppcapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.codeinger.ppcapp.R
import com.codeinger.ppcapp.databinding.ActivityMainBinding
import com.codeinger.ppcapp.ui.activity.authentication.ForgetPasswordActivity
import com.codeinger.ppcapp.ui.fragment.membership.MyMembershipFragment
import com.codeinger.ppcapp.ui.fragment.payments.ContactUs
import com.codeinger.ppcapp.ui.fragment.payments.PaymentsFragment
import com.codeinger.ppcapp.utils.SessionManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth


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
            replace(PaymentsFragment(), "add")
            binding.drawer.closeDrawer(GravityCompat.START)

        }

        binding.llContactUs.setOnClickListener {
            replace(ContactUs(), "add")
            binding.drawer.closeDrawer(GravityCompat.START)
        }


        binding.llResetPass.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ForgetPasswordActivity::class.java
                )
            )
        }

        binding.llLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()

            SessionManager.clear(this)
        }

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