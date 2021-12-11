package com.codeinger.ppc_app.ui.activity.membership

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codeinger.ppc_app.databinding.ActivityPurchseSuccessMembershipBinding
import com.codeinger.ppc_app.retrofit.Constant
import com.codeinger.ppc_app.ui.activity.MainActivity
import com.codeinger.ppc_app.utils.SessionManager

class PurchseSuccessMembershipActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPurchseSuccessMembershipBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPurchseSuccessMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    fun init() {
        binding.tvPrice.setText("$"+ SessionManager.readString(this, Constant.PlanAmount,""))
        binding.tvYear.setText(SessionManager.readString(this, Constant.PlanYears,"") + " Membership")
        binding.tvTotal.setText("$"+ SessionManager.readString(this, Constant.PlanAmount,""))

        binding.tvContinue.setOnClickListener {
            val intent=Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}