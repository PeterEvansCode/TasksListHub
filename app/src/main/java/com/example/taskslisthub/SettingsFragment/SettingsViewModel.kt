package com.example.taskslisthub.SettingsFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskslisthub.TaskItemRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class SettingsViewModel(private val repository: TaskItemRepository) : ViewModel() {

    fun setGoogleAccount(context: Context) {
        repository.setGoogleAccount(context)
    }

    fun getGoogleAccount(): GoogleSignInAccount? {
        return repository.getGoogleAccount()
    }

    class SettingsViewModelFactory(private val repository: TaskItemRepository) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}