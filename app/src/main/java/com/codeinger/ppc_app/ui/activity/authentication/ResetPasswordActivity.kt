package com.codeinger.ppc_app.ui.activity.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codeinger.ppc_app.databinding.ActivityResetPasswordBinding
import com.codeinger.ppc_app.ui.activity.membership.ChooseMembershipActivity

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    fun init(){
        binding.tvReset.setOnClickListener {
            val intent= Intent(this, ChooseMembershipActivity::class.java)
            startActivity(intent)
        }
    }
}