package com.codeinger.ppcapp.ui.activity.payment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codeinger.ppcapp.CheckoutActivity
import com.codeinger.ppcapp.databinding.ActivityMakeDonationBinding
import kotlinx.android.synthetic.main.activity_checkout.*


class MakeDonationActivity : AppCompatActivity() {
    private var name: String? = null
    private lateinit var binding: ActivityMakeDonationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeDonationBinding.inflate(layoutInflater)

        if (savedInstanceState == null) {
            val extras = intent.extras
            name = extras?.getString("name")
        } else {
            savedInstanceState.getSerializable("name") as String?
        }

        if (name.equals("Paypal")) {

            Toast.makeText(
                this,
                name,
                Toast.LENGTH_SHORT
            )
                .show()

            binding.imagePayment.visibility = View.VISIBLE
            binding.imagePayment1.visibility == View.GONE

            /*   binding.imagePayment.visibility == View.VISIBLE
               binding.imagePayment1.visibility == View.GONE*/

        }
        if (name.equals("Google Pay")) {

            Toast.makeText(
                this,
                name,
                Toast.LENGTH_SHORT
            )
                .show()

            binding.imagePayment.visibility = View.GONE
            binding.imagePayment1.visibility == View.VISIBLE
            /*    binding.imagePayment1.visibility == View.VISIBLE
                binding.imagePayment.visibility == View.GONE*/

        }

        binding.gatwayName.setText("" + name)

        setContentView(binding.root)

        binding.tvContinue.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
                .putExtra("amount",binding.amount.text.toString())
            startActivity(intent)
        }
    }
}