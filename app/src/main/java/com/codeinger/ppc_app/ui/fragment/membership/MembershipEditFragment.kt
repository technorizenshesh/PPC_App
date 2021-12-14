package com.codeinger.ppc_app.ui.fragment.membership

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codeinger.ppc_app.R
import com.codeinger.ppc_app.databinding.FragmentMembershipEditBinding
import com.codeinger.ppc_app.model.MemberShipDetailModel
import com.codeinger.ppc_app.ui.activity.MainActivity
import com.codeinger.ppc_app.utils.CustomDialogProgress
import com.codeinger.ppc_app.utils.DataManager
import com.codeinger.ppc_app.utils.NetworkAvailablity
import com.google.gson.Gson
import com.iratepro.retrofit.BackEndApi
import com.iratepro.retrofit.WebServiceClient
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MembershipEditFragment : Fragment() {

    private lateinit var binding : FragmentMembershipEditBinding
    var shipDetailModel: MemberShipDetailModel? = null
    var emailArray = JSONArray()
    var  phoneArray = JSONArray()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentMembershipEditBinding.inflate(layoutInflater)
        (activity as MainActivity).settext("Edit Membership Details")
        return (binding.root)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(DataManager.getOurInstance().getMemberShipDetails(requireContext())!=null){
            shipDetailModel = DataManager.getOurInstance().getMemberShipDetails(requireContext())
            setDataOnView(shipDetailModel!!)
        }



        binding.tvCancelChanges.setOnClickListener {
            (activity as MainActivity).replace(MyMembershipFragment())
        }

        binding.tvSaveDetails.setOnClickListener { validation() }


        binding.btnAddEmail.setOnClickListener {
            if (binding.etEmail2!!.text.toString().equals("")) {
                binding.etEmail2!!.error = getString(R.string.field_required)
            } else if (!isValidEmail(binding.etEmail2!!.text.toString())) {
                binding.etEmail2!!.error = getString(R.string.wrong_email)
            }
            else{
                addEmails()
            }

        }

        binding.btnAddPhone.setOnClickListener {
            if (binding.etPhone2!!.text.toString().equals("")) {
                binding.etPhone2!!.error = getString(R.string.field_required)
            }
            else{
                addPhones()
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun setDataOnView(model: MemberShipDetailModel) {
        binding.etFname.setText(model.firstName)
        binding.etLname.setText(model.lastName)
        binding.etEmailPri.setText(model.membershipEmails.get(0))
        binding.etPhonePri.setText(model.membershipPhoneNumbers.get(0))
        binding.etAddress1.setText(model.addressLine1)
        binding.etAddress2.setText(model.addressLine2)
        binding.etCity.setText(model.city)
        binding.etProvince.setText(model.province)
        binding.etPostCode.setText(model.postalCode)


    }



    private fun validation() {
        when{

            binding.etFname!!.text!!.isEmpty()->{
                binding.etFname!!.error = getString(R.string.field_required)
            }

            binding.etLname!!.text!!.isEmpty()->{
                binding.etLname!!.error = getString(R.string.field_required)
            }

            binding.etEmailPri!!.text!!.isEmpty()->{
                binding.etEmailPri!!.error = getString(R.string.field_required)
            }
            !isValidEmail(binding.etEmailPri!!.text.toString())->{
                binding.etEmailPri!!.error = getString(R.string.wrong_email)
            }

            binding.etPhonePri!!.text!!.isEmpty()->{
                binding.etPhonePri!!.error = getString(R.string.field_required)
            }

            binding.etAddress1!!.text!!.isEmpty()->{
                binding.etAddress1!!.error = getString(R.string.field_required)
            }

            binding.etAddress2!!.text!!.isEmpty()->{
                binding.etAddress2!!.error = getString(R.string.field_required)
            }

            binding.etCity!!.text!!.isEmpty()->{
                binding.etCity!!.error = getString(R.string.field_required)
            }

            binding.etProvince!!.text!!.isEmpty()->{
                binding.etProvince!!.error = getString(R.string.field_required)
            }


            binding.etPostCode!!.text!!.isEmpty()->{
                binding.etPostCode!!.error = getString(R.string.field_required)
            }

            else ->{
                if  (NetworkAvailablity.checkNetworkStatus(requireContext())) EditMemberShip()
                else Toast.makeText(requireContext(), getString(R.string.network_failure), Toast.LENGTH_LONG).show()
            }

        }

    }


    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun EditMemberShip() {
        var customDialogProgress = CustomDialogProgress(requireContext())
        customDialogProgress!!.show()

        val rootObject= JSONObject()
        rootObject.put("membershipGUID", shipDetailModel!!.membershipGUID)
        rootObject.put("firstName", binding.etFname!!.text.toString())
        rootObject.put("lastName", binding.etLname!!.text.toString())
        rootObject.put("isOptIn", true)
        rootObject.put("addressLine1", binding.etAddress1!!.text.toString())
        rootObject.put("addressLine2", binding.etAddress2!!.text.toString())
        rootObject.put("city", binding.etCity!!.text.toString())
        rootObject.put("province", binding.etProvince!!.text.toString())
        rootObject.put("postalCode", binding.etPostCode!!.text.toString())
        addEmails()
        addPhones()
        rootObject.put("email", emailArray)
        rootObject.put("phoneNumber", phoneArray)
        val requestFile = RequestBody.create(MediaType.parse("application/json"), rootObject.toString())
        Log.e("TAG==EditMShip Json==", "kkkkkkkk: $requestFile")
        Log.e("TAG==EditMShip Json==", "kkkkkkkk: $rootObject")
        WebServiceClient.client1.create(BackEndApi::class.java).editMemberShipss(requestFile)
                .enqueue(object : Callback<Object> {
                    override fun onResponse(call: Call<Object>?, response: Response<Object>?) {
                        customDialogProgress!!.dismiss()
                        if (response!!.code() == 200) {
                            val `object` = JSONObject(Gson().toJson(response!!.body()))
                            Log.e("TAG", "Edit MemberShip onResponse: $`object`")
                            if (`object`.get("success") == true) {
                                Toast.makeText(requireContext(), getString(R.string.changed_succesfully), Toast.LENGTH_SHORT).show()
                                (activity as MainActivity).replace(MyMembershipFragment())
                            } else {
                                Toast.makeText(requireContext(), `object`!!.get("message").toString(), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), response!!.message(), Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }


                    override fun onFailure(call: Call<Object>?, t: Throwable?) {
                        customDialogProgress!!.dismiss()
                        Toast.makeText(requireContext(), t!!.message, Toast.LENGTH_SHORT).show()
                    }

                })







    }

    private fun addEmails() {
        emailArray = JSONArray()
        if(!binding.etEmailPri!!.text.toString().equals("")) emailArray.put(binding.etEmailPri!!.text.toString())
        if(!binding.etEmail2!!.text.toString().equals("")){
            emailArray.put(binding.etEmail2!!.text.toString())
        }
    }


    private fun addPhones() {
        phoneArray = JSONArray()
        if(!binding.etPhonePri!!.text.toString().equals("")) phoneArray.put(binding.etPhonePri!!.text.toString())
        if(!binding.etPhone2!!.text.toString().equals("")){
            phoneArray.put(binding.etPhone2!!.text.toString())
        }
    }


}