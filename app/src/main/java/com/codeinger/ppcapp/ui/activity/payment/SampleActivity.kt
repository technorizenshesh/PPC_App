package com.codeinger.ppcapp.ui.activity.payment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codeinger.ppcapp.R
import com.paypal.android.sdk.payments.*
import org.json.JSONException
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashSet

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_activity)

        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        startService(intent)
    }

    fun onBuyPressed(pressed: View) {

        val thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE)
        val intent = Intent(this@SampleActivity, PaymentActivity::class.java)

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy)
        startActivityForResult(intent, REQUEST_CODE_PAYMENT)
    }

    private fun getThingToBuy(paymentIntent: String): PayPalPayment {
        return PayPalPayment(BigDecimal("1.75"), "USD", "sample item", paymentIntent)
    }

    private fun getStuffToBuy(paymentIntent: String): PayPalPayment {
        val items = arrayOf(
            PayPalItem("sample item #1", 2, BigDecimal("87.50"), "USD", "sku-12345678"),
            PayPalItem("free sample item #2", 1, BigDecimal("0.00"), "USD", "sku-zero-price"),
            PayPalItem(
                "sample item #3 with a longer name",
                6,
                BigDecimal("37.99"),
                "USD",
                "sku-33333"
            )
        )
        val subtotal = PayPalItem.getItemTotal(items)
        val shipping = BigDecimal("7.21")
        val tax = BigDecimal("4.67")
        val paymentDetails = PayPalPaymentDetails(shipping, subtotal, tax)
        val amount = subtotal.add(shipping).add(tax)
        val payment = PayPalPayment(amount, "USD", "sample item", paymentIntent)
        payment.items(items).paymentDetails(paymentDetails)

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.")

        return payment
    }

    private fun addAppProvidedShippingAddress(paypalPayment: PayPalPayment) {
        val shippingAddress = ShippingAddress()
            .recipientName("Mom Parker")
            .line1("52 North Main St.")
            .city("Austin")
            .state("TX")
            .postalCode("78729")
            .countryCode("US")
        paypalPayment.providedShippingAddress(shippingAddress)
    }

    private fun enableShippingAddressRetrieval(paypalPayment: PayPalPayment, enable: Boolean) {
        paypalPayment.enablePayPalShippingAddressesRetrieval(enable)
    }

    fun onFuturePaymentPressed(pressed: View) {
        val intent = Intent(this@SampleActivity, PayPalFuturePaymentActivity::class.java)

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT)
    }

    fun onProfileSharingPressed(pressed: View) {
        val intent = Intent(this@SampleActivity, PayPalProfileSharingActivity::class.java)

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, oauthScopes)

        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING)
    }

    private val oauthScopes: PayPalOAuthScopes
        get() {
            val scopes = HashSet(
                Arrays.asList(
                    PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL,
                    PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS
                )
            )
            return PayPalOAuthScopes(scopes)
        }

    protected fun displayResultText(result: String) {
        var resultView: TextView = findViewById(R.id.txtResult)
        resultView.text = "Result : " + result
        Toast.makeText(applicationContext, result, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PAYMENT) {

            if (resultCode == Activity.RESULT_OK) {

                Log.e("data>>>>", "" + data.toString())
                val confirm =
                    data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4))
                        Log.i(TAG, confirm.payment.toJSONObject().toString(4))

                        Log.e("confirm>>>", confirm.toString())

                        displayResultText("PaymentConfirmation info received from PayPal")


                    } catch (e: JSONException) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e)
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                    TAG,
                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
                )
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                val auth =
                    data?.getParcelableExtra<PayPalAuthorization>(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION)
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4))

                        val authorization_code = auth.authorizationCode
                        Log.i("FuturePaymentExample", authorization_code)

                        sendAuthorizationToServer(auth)
                        displayResultText("Future Payment code received from PayPal")

                    } catch (e: JSONException) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e)
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.")
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                    "FuturePaymentExample",
                    "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs."
                )
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                val auth =
                    data?.getParcelableExtra<PayPalAuthorization>(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION)
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4))

                        val authorization_code = auth.authorizationCode
                        Log.i("ProfileSharingExample", authorization_code)

                        sendAuthorizationToServer(auth)
                        displayResultText("Profile Sharing code received from PayPal")

                    } catch (e: JSONException) {
                        Log.e(
                            "ProfileSharingExample",
                            "an extremely unlikely failure occurred: ",
                            e
                        )
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.")
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                    "ProfileSharingExample",
                    "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs."
                )
            }
        }
    }

    private fun sendAuthorizationToServer(authorization: PayPalAuthorization) {


    }

    fun onFuturePaymentPurchasePressed(pressed: View) {
        val metadataId = PayPalConfiguration.getClientMetadataId(this)

        Log.i("FuturePaymentExample", "Client Metadata ID: " + metadataId)
        displayResultText("Client Metadata Id received from SDK")
    }

    public override fun onDestroy() {
        stopService(Intent(this, PayPalService::class.java))
        super.onDestroy()
    }

    companion object {

        private val TAG = "paymentExample"
        private val CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK
        private val CONFIG_CLIENT_ID =
            "Af0pt4iTc67jBeFb47Fzd59ktEEf1J0qdM_NR-OaOFk7--gRpkVV5VNqaECVrTchnpPyZEAudjwfQpAn"

        private val REQUEST_CODE_PAYMENT = 1
        private val REQUEST_CODE_FUTURE_PAYMENT = 2
        private val REQUEST_CODE_PROFILE_SHARING = 3

        private val config = PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)

            .merchantName("Harshit")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"))
    }
}