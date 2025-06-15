package com.example.taskslisthub.SettingsFragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.taskslisthub.R
import com.example.taskslisthub.databinding.FragmentPersonaliseBinding
import com.github.dhaval2404.colorpicker.ColorPickerDialog

class PersonaliseFragment : Fragment() {

    private lateinit var binding: FragmentPersonaliseBinding

    private lateinit var themeSpinner: Spinner
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPersonaliseBinding.inflate(inflater, container, false)

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        initialiseSpinner()
        /*binding.changeColorButton.setOnClickListener{
            showColorPicker()
        }*/

        return binding.root
    }

    private fun initialiseSpinner(){


        // Initialize the spinner
        themeSpinner = binding.themeSpinner

        // Set the current selected item based on saved preference
        val currentTheme = sharedPreferences.getString("theme_preference", "system")
        val position = when (currentTheme) {
            "light" -> 1
            "dark" -> 2
            else -> 0
        }
        themeSpinner.setSelection(position)

        // Set listener for spinner selection
        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedTheme = when (position) {
                    1 -> "light"
                    2 -> "dark"
                    else -> "system"
                }
                saveThemePreference(selectedTheme)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    // Function to save the selected theme to SharedPreferences
    private fun saveThemePreference(themeValue: String) {


        // Get the theme preference value
        val themePreference = sharedPreferences.getString("theme_preference", "system")

        if (themePreference != themeValue) {
            with(sharedPreferences.edit()) {
                putString("theme_preference", themeValue)
                apply()
            }

            // Apply the selected theme
            applyChanges()
        }
    }

    // Function to apply the selected theme
    private fun applyChanges() {
        requireActivity().recreate()
    }

    private fun showColorPicker() {
        ColorPickerDialog
            .Builder(requireContext()) // or `this` if using an Activity
            .setTitle("Pick an Accent Color")
            .setDefaultColor(R.color.colorAccent)
            .setColorListener { color, colorHex ->
                saveColorPreference(color)
            }
            .show()
    }

    private fun saveColorPreference(color: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("colorPrimary", color)
            apply()
        }

        applyChanges()
    }
}
