package com.example.googletasksassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.googletasksassistant.AboutFragment
import com.example.googletasksassistant.MainTaskListFragment
import com.example.googletasksassistant.R
import com.example.googletasksassistant.SettingsFragment
import com.example.googletasksassistant.databinding.ActivityMainBinding
import com.example.googletasksassistant.databinding.FragmentMainTaskListBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    // Assign viewModel
    private val menuViewModel: MenuViewModel by viewModels {
        MenuModelFactory((application as TodoApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set nav view
        binding.navView.setNavigationItemSelectedListener(this)

        // Handle back press to close drawer if open
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        setMenuBindings()

        //load main fragment
        loadInitialFragment(savedInstanceState)
    }

    private fun setMenuBindings(){
        menuViewModel.taskTags.observe(this, Observer {
            val menu = binding.navView.menu

            // First, clear any previous dynamic items in the middle group
            menu.removeGroup(R.id.group_dynamic)

            // Add the new dynamic items in the middle of the menu
            val dynamicGroupId = R.id.group_dynamic  // This group ID must be defined in `ids.xml`
            it.forEachIndexed { index, tag ->
                menu.add(dynamicGroupId, index, index, tag.name).apply {
                    icon = getDrawable(R.drawable.ic_tag)  // Optional: Set an icon for dynamic items
                }
            }

            // Invalidate the menu to refresh
            binding.navView.invalidate()
        })
    }

    private fun loadInitialFragment(savedInstanceState: Bundle?){
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainTaskListFragment()).commit()
            binding.navView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainTaskListFragment()).commit()

            R.id.nav_about -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutFragment()).commit()

            R.id.nav_settings -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()

            R.id.nav_deleted -> Toast.makeText(this, "previously deleted", Toast.LENGTH_SHORT)
                .show()
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
