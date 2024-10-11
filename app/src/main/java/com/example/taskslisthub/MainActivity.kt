package com.example.taskslisthub

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.example.taskslisthub.SettingsFragment.SettingsFragment
import com.example.taskslisthub.TaskListFragment.TaskListFragment
import com.example.taskslisthub.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding

    // Assign viewModel
    private val menuViewModel: MenuViewModel by viewModels {
        MenuModelFactory((application as TasksListHub).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        //set correct theme
        applyTheme()

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
        menuViewModel.allTaskTags.observe(this, Observer { taskTags ->
            val menu = binding.navView.menu

            // First, clear any previous dynamic items in the middle group
            menu.removeGroup(R.id.group_tag_menu_items)

            taskTags.sortedBy{it.name}.forEachIndexed { index, tag ->
                menu.add(
                    R.id.group_tag_menu_items, //add to the tag menu group
                    tag.id, //itemId = tagId for easy lookup
                    index, //sort alphabetically
                    tag.name //name of menu item
                ).apply {
                    //apply icon
                   @DrawableRes
                   icon = this@MainActivity.getDrawable(R.drawable.ic_tag)
                }
            }

            // Invalidate the menu to refresh
            binding.navView.invalidate()
        })
    }

    private fun loadInitialFragment(savedInstanceState: Bundle?){
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TaskListFragment()).commit()
            binding.navView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.groupId == R.id.group_tag_menu_items) {
            val selectedTag = menuViewModel.getTaskTag(item.itemId)
            openFragment(TaskListFragment(tagFilter = selectedTag))
        }
        else {
            when (item.itemId) {
                R.id.nav_home -> openFragment(TaskListFragment())

                R.id.nav_about -> openFragment(AboutFragment())

                R.id.nav_settings -> openFragment(SettingsFragment())

                R.id.nav_deleted_tasks ->
                    Toast.makeText(this, "previously deleted tasks", Toast.LENGTH_SHORT)
                        .show()

                R.id.nav_deleted_tags ->
                    Toast.makeText(this, "previously deleted tasks", Toast.LENGTH_SHORT)
                        .show()

                R.id.nav_priority_high -> openFragment(TaskListFragment(priorityFilter = 3))

                R.id.nav_priority_medium -> openFragment(TaskListFragment(priorityFilter = 2))

                R.id.nav_priority_low -> openFragment(TaskListFragment(priorityFilter = 1))
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment).commit()
    }

    private fun applyTheme() {
        val themePreference = sharedPreferences.getString("theme_preference", "system")
        when (themePreference) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}
