package com.codeinger.ppc_app.ui.activity.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codeinger.ppc_app.databinding.ActivityMakeDonationBinding

class MakeDonationActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMakeDonationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMakeDonationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvContinue.setOnClickListener {
//            val intent = Intent(this,Succ)
        }
    }
}