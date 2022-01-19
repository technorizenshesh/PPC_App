package com.codeinger.ppcapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import com.codeinger.ppcapp.retrofit.Constant
import com.codeinger.ppcapp.ui.activity.MainActivity
import com.codeinger.ppcapp.ui.activity.membership.ChooseMembershipActivity
import com.codeinger.ppcapp.util.Json
import com.codeinger.ppcapp.utils.CustomDialogProgress
import com.codeinger.ppcapp.utils.SessionManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.google.gson.Gson
import com.iratepro.retrofit.BackEndApi
import com.iratepro.retrofit.WebServiceClient
import kotlinx.android.synthetic.main.activity_checkout.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToLong


/**
 * Checkout implementation for the app
 */
class CheckoutActivity : Activity() {

    private var amount_int: Int = 0
    private var amount1: String? = null
    private lateinit var token1: String

    /**
     * A client for interacting with the Google Pay API.
     *
     * @see [PaymentsClient](https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient)
     */
    private lateinit var paymentsClient: PaymentsClient
    private val shippingCost = (90 * 1000000).toLong()

    private lateinit var garmentList: JSONArray
    private lateinit var selectedGarment: JSONObject

    /**
     * Arbitrarily-picked constant integer you define to track a request for payment data activity.
     *
     * @value #LOAD_PAYMENT_DATA_REQUEST_CODE
     */
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991

    /**
     * Initialize the Google Pay API on creation of the activity
     *
     * @see Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        if (savedInstanceState == null) {
            val extras = intent.extras
            amount1 = extras?.getString("amount")
        } else {
            savedInstanceState.getSerializable("amount") as String?
        }
        // Set up the mock information for our item in the UI.
        selectedGarment = fetchRandomGarment()
        displayGarment(selectedGarment)

        // Initialize a Google Pay API client for an environment suitable for testing.
        // It's recommended to create the PaymentsClient object inside of the onCreate method.
        paymentsClient = PaymentsUtil.createPaymentsClient(this)
        possiblyShowGooglePayButton()

        googlePayButton.setOnClickListener { requestPayment() }
    }

    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button.
     *
     * @see [](https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient.html.isReadyToPay
    ) */
    private fun possiblyShowGooglePayButton() {

        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                // Process error
                Log.w("isReadyToPay failed", exception)
            }
        }
    }

    /**
     * If isReadyToPay returned `true`, show the button and hide the "checking" text. Otherwise,
     * notify the user that Google Pay is not available. Please adjust to fit in with your current
     * user flow. You are not required to explicitly let the user know if isReadyToPay returns `false`.
     *
     * @param available isReadyToPay API response.
     */
    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            googlePayButton.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                this,
                "Unfortunately, Google Pay is not available on this device",
                Toast.LENGTH_LONG
            ).show();
        }
    }

    private fun requestPayment() {

        // Disables the button to prevent multiple clicks.
        googlePayButton.isClickable = false


        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        // val garmentPriceMicros = (selectedGarment.getDouble("price") * 1000000).roundToLong()
        //val price = (garmentPriceMicros + shippingCost).microsToString()

        val paymentDataRequestJson = amount1?.let { PaymentsUtil.getPaymentDataRequest(it) }
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE
            )
        }
    }

    /**
     * Handle a resolved activity from the Google Pay payment sheet.
     *
     * @param requestCode Request code originally supplied to AutoResolveHelper in requestPayment().
     * @param resultCode Result code returned by the Google Pay API.
     * @param data Intent from the Google Pay API containing payment or error data.
     * @see [Getting a result
     * from an Activity](https://developer.android.com/training/basics/intents/result)
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // value passed in AutoResolveHelper
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }
                    Activity.RESULT_CANCELED -> {
                        // Nothing to do here normally - the user simply cancelled without selecting a
                        // payment method.
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }
                // Re-enables the Google Pay payment button.
                googlePayButton.isClickable = true
            }
        }
    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson() ?: return

        try {
            val paymentMethodData =
                JSONObject(paymentInformation).getJSONObject("paymentMethodData")


            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type") == "PAYMENT_GATEWAY" && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token") == "examplePaymentMethodToken"
            ) {

                AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage(
                        "Gateway name set to \"example\" - please modify " +
                                "Constants.java and replace it with your own gateway."
                    )
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
            }

            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)


            Toast.makeText(
                this,
                getString(R.string.payments_show_name, billingName),
                Toast.LENGTH_LONG
            ).show()

            Log.e("paymentInformation>>>", paymentInformation)

            token1 = paymentMethodData
                .getJSONObject("tokenizationData")
                .getString("token")


            AddDonationAPI(token1)

            // Logging token string.
            Log.d(
                "GooglePaymentToken", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
            )

        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString())
        }
    }

    private fun AddDonationAPI(token1: String) {

        amount_int = Integer.parseInt(amount1)

        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress!!.show()
        val rootObject = JSONObject()
        rootObject.put("transactionId", token1)
        rootObject.put("paymentType", 1)
        rootObject.put("amount", amount_int)
        val requestFile =
            RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG=====Json===", "kkkkkkkk: $requestFile.toString()")

        Log.e("rootObject>>>", rootObject.toString())

        WebServiceClient.client1.create(BackEndApi::class.java)
            .add_donation(/*header,*/ requestFile)
            .enqueue(object : Callback<Object> {

                override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                    customDialogProgress!!.dismiss()

                    if (response!!.code() == 200) {
                        val `object` = JSONObject(Gson().toJson(response!!.body()))

                        Log.e("TAG add  donation", "onResponse: $`object`")
                        if (`object`.get("success") == true) {
                            Log.e("chala====", "onResponse: $`object`")

                            Toast.makeText(
                                this@CheckoutActivity, "SucessFully Donation With Your Account ",
                                Toast.LENGTH_SHORT
                            ).show()
                            //finishAffinity()
                        }

                    } else {
                        Toast.makeText(
                            this@CheckoutActivity, "dfdaf",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<Object>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(this@CheckoutActivity, t!!.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun handleError(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }

    private fun fetchRandomGarment(): JSONObject {
        if (!::garmentList.isInitialized) {
            garmentList = Json.readFromResources(this, R.raw.tshirts)
        }

        val randomIndex: Int = Math.round(Math.random() * (garmentList.length() - 1)).toInt()
        return garmentList.getJSONObject(randomIndex)
    }

    private fun displayGarment(garment: JSONObject) {
        detailTitle.setText(garment.getString("title"))
        detailPrice.setText("\$${garment.getString("price")}")

        val escapedHtmlText: String = Html.fromHtml(garment.getString("description")).toString()
        detailDescription.setText(Html.fromHtml(escapedHtmlText))

        val imageUri = "@drawable/${garment.getString("image")}"
        val imageResource = resources.getIdentifier(imageUri, null, packageName)
        detailImage.setImageResource(imageResource)
    }
}
