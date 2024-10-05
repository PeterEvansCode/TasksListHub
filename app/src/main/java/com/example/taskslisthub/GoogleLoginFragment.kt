package com.example.taskslisthub

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.taskslisthub.databinding.FragmentGoogleLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.security.SecureRandom
import java.util.Base64

class GoogleLoginFragment : Fragment() {

    //binding
    private lateinit var binding: FragmentGoogleLoginBinding

    //google tasks API
    private lateinit var client: GoogleSignInClient
    val REQUESTCODE = 10001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGoogleLoginBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null){
            // Reload the SettingsFragment after successful sign-in
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, SettingsFragment())
                commit()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = GoogleSignInOptions.Builder()
            .requestIdToken(BuildConfig.CLIENT_ID)
            .requestEmail()
            .build()
        client = GoogleSignIn.getClient(requireActivity(), options)

        if (FirebaseAuth.getInstance().currentUser != null){
            initialiseSignedIn()
        }

        else initialiseSignedOut()
    }

    /*private fun initialiseSignedIn(){
        val signedInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())

        // Set up the Sign-In button
        binding.signInButton.text = R.string.signed_in_button.toString()
        binding.signInButton.setOnClickListener {
            signOut()
        }
        //get account name
        val accountName = signedInAccount?.displayName
        binding.googleAccountName.text = accountName

        //load profile picture
        val photoUrl = signedInAccount?.photoUrl
        Glide.with(this)
            .load(photoUrl)
            .into(binding.profileImage)
    }

    private fun initialiseSignedOut(){
        // Set up the Sign-In button
        binding.signInButton.text = R.string.signed_out_button.toString()
        binding.signInButton.setOnClickListener {
            signIn()
        }

        //set logged out text
        binding.googleAccountName.text = R.string.signed_out_account.toString()

        //set blank profile picture
        binding.profileImage.setImageResource(R.drawable.logout_24)
    }

    private fun signIn() {
        val intent = client.signInIntent
        startActivityForResult(intent, REQUESTCODE)
    }

    private fun generateNonce(): String {
        val random = SecureRandom()
        val nonceBytes = ByteArray(16)
        random.nextBytes(nonceBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java) //error here
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            initialiseSignedIn()
                        } else {
                            // Show a toast with the error message
                            Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                // Handle the exception
                Toast.makeText(requireContext(), "Sign-in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        initialiseSignedOut()
    }*/
}