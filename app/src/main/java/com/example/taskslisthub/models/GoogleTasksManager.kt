package com.example.taskslisthub.models

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.tasks.Tasks

class GoogleTasksManager(private val context: Context) {

    private lateinit var tasksService: Tasks

    // Link to Google account
    private var googleAccount: GoogleSignInAccount? = null

    // Set Google account
    fun setGoogleAccount(account: GoogleSignInAccount?) {
        googleAccount = account
        account?.let { setupTasksService(it) }
    }

    // Get Google account
    fun getGoogleAccount(): GoogleSignInAccount? {
        return googleAccount
    }

    private fun setupTasksService(account: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf("https://www.googleapis.com/auth/tasks")
        )
        credential.selectedAccount = account.account

        // Create the Google Tasks API client
        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        tasksService = Tasks.Builder(transport, jsonFactory, credential)
            .setApplicationName("TasksListHub")
            .build()
    }
}
