package com.codeinger.ppc_app.ui.fragment.membership

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codeinger.ppc_app.databinding.FragmentMyMembershipBinding
import com.codeinger.ppc_app.ui.activity.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener


class MyMembershipFragment : Fragment() {
    private lateinit var binding: FragmentMyMembershipBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

//        binding.tvLogout.setOnClickListener {
//            signOut()
//            Toast.makeText(requireContext(), "Successfully sign out", Toast.LENGTH_SHORT).show()
//            val intent = Intent(requireContext(), WelcomeActivity::class.java)
//            startActivity(intent)
//        }

        return (binding.root)

    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
                // ...
            })
    }

}

private fun Any.addOnCompleteListener(
    myMembershipFragment: MyMembershipFragment,
    onCompleteListener: OnCompleteListener<Void?>
) {
    TODO("Not yet implemented")
}
