package com.example.taskslisthub.models

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.tasks.Tasks

class GoogleTasksManager() {

    private lateinit var tasksService: Tasks

    // Link to Google account
    private var googleAccount: GoogleSignInAccount? = null

    // Set Google account
    fun setGoogleAccount(context: Context) {
        googleAccount = GoogleSignIn.getLastSignedInAccount(context)
        googleAccount?.let { setupTasksService(context) }
    }

    // Get Google account
    fun getGoogleAccount(): GoogleSignInAccount? {
        return googleAccount
    }

    private fun setupTasksService(context: Context) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf("https://www.googleapis.com/auth/tasks")
        )
        credential.selectedAccount = googleAccount?.account

        // Create the Google Tasks API client
        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        tasksService = Tasks.Builder(transport, jsonFactory, credential)
            .setApplicationName("TasksListHub")
            .build()
    }
}
