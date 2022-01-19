package com.codeinger.ppcapp.ui.fragment.membership

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codeinger.ppcapp.R
import com.codeinger.ppcapp.databinding.FragmentMyMembershipBinding
import com.codeinger.ppcapp.model.MemberShipDetailModel
import com.codeinger.ppcapp.retrofit.Constant
import com.codeinger.ppcapp.ui.activity.MainActivity
import com.codeinger.ppcapp.ui.activity.authentication.WelcomeActivity
import com.codeinger.ppcapp.utils.CustomDialogProgress
import com.codeinger.ppcapp.utils.NetworkAvailablity
import com.codeinger.ppcapp.utils.SessionManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.iratepro.retrofit.BackEndApi
import com.iratepro.retrofit.WebServiceClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyMembershipFragment : Fragment() {
    private lateinit var binding: FragmentMyMembershipBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyMembershipBinding.inflate(layoutInflater)

        (activity as MainActivity).settext("My Membership")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.ivEdit.setOnClickListener {
            (activity as MainActivity).replace(MembershipEditFragment(), "add")
        }

        binding.tvLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()

            Toast.makeText(requireContext(), "Successfully sign out", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            startActivity(intent)
            context?.let { it1 -> SessionManager.clear(it1) }
        }

        return (binding.root)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun getMemberShip() {
        var customDialogProgress = CustomDialogProgress(requireContext())
        customDialogProgress!!.show()
        WebServiceClient.client1.create(BackEndApi::class.java).getMembershipDetail()
            .enqueue(object : Callback<MemberShipDetailModel> {
                override fun onResponse(
                    call: Call<MemberShipDetailModel>?,
                    response: Response<MemberShipDetailModel>?
                ) {
                    customDialogProgress!!.dismiss()
                    if (response!!.code() == 200) {
                        val data = Gson().toJson(response!!.body())
                        Log.e("TAG", "get memberShip Details onResponse: $data")
                        if (response!!.body()!!.success == true) {
                            SessionManager.writeString(
                                requireContext(),
                                Constant.membership_get,
                                data
                            )
                            setDataOnView(response!!.body())

                        } else {
//                                Toast.makeText(requireContext(), response!!.body().message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<MemberShipDetailModel>?, t: Throwable?) {
                    customDialogProgress!!.dismiss()
                    Toast.makeText(requireContext(), t!!.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }


    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
            })
      }


    @SuppressLint("SetTextI18n")
    private fun setDataOnView(model: MemberShipDetailModel) {
        Glide.with(requireContext()).load(R.drawable.user_default).apply(
            RequestOptions().placeholder(R.drawable.user_default).error(R.drawable.user_default)
        ).into(binding.ivUser)
        binding.tvName.setText(model.firstName + " " + model.lastName)
        binding.tvJoinDate.setText(model.joinedDate)
        binding.tvExpiry.setText(model.expiryDate)
        binding.tvEmail.setText(model.membershipEmails.get(0))
        binding.tvAddressLine1.setText(model.addressLine1)
        binding.tvAddressLine2.setText(model.city)
        binding.tvAddressLine3.setText(model.province + " " + model.postalCode)
        (activity as MainActivity).setNavigationText(model.firstName + " " + model.lastName)
    }

    override fun onResume() {
        super.onResume()
        if (NetworkAvailablity.checkNetworkStatus(activity)) getMemberShip()
        else Toast.makeText(activity, getString(R.string.network_failure), Toast.LENGTH_LONG).show()
    }
}

private fun Any.addOnCompleteListener(
    myMembershipFragment:
    MyMembershipFragment,
    onCompleteListener: OnCompleteListener<Void?>
) {
    TODO("Not yet implemented")
}
