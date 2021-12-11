package com.codeinger.ppc_app.ui.activity.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codeinger.ppc_app.databinding.ActivityForgetPasswordBinding

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgetPasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)

        init()

    }

    fun init(){
        binding.tvReset.setOnClickListener {
            val intent=Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}