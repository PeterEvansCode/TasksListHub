package com.example.taskslisthub

import android.content.pm.PackageManager
import android.credentials.CredentialManager
import android.credentials.GetCredentialResponse
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.PasswordCredential
import androidx.drawerlayout.widget.DrawerLayout
import com.example.taskslisthub.databinding.FragmentSettingsBinding
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import java.security.SecureRandom
import java.util.Base64

class SettingsFragment : Fragment() {

    //binding
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind toolbar with activity's drawer layout
        (activity as? AppCompatActivity)?.let { appCompatActivity ->
            val drawerLayout = appCompatActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            binding.toolbar.bindWithDrawerLayout(appCompatActivity, drawerLayout)
        }

        // Set up the Sign-In button
        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val appInfo = context?.packageManager?.getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
        val clientId = appInfo!!.metaData!!.getString("com.google.android.gms.client_id")
        val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder()
            .setServerClientId(clientId!!)
            .setNonce(generateNonce())
        .build()
    }

    private fun generateNonce(): String {
        val random = SecureRandom()
        val nonceBytes = ByteArray(16) // 16 bytes = 128 bits
        random.nextBytes(nonceBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.toolbar.cleanUp()
    }
}