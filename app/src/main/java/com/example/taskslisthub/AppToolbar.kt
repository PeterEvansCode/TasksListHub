package com.example.taskslisthub

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: CustomToolbarBinding
    private lateinit var activity: AppCompatActivity
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    init {
        // Initialize any padding or other properties here
        setPadding(paddingStart, paddingTop, paddingEnd + 16, paddingBottom)  // Adjust right padding
    }

    fun bindWithDrawerLayout(activity: AppCompatActivity, drawerLayout: DrawerLayout) {
        if (activity !is NavigationView.OnNavigationItemSelectedListener)
            throw IllegalArgumentException("Activity must implement NavigationView.OnNavigationItemSelectedListener")

        this.activity = activity
        this.drawerLayout = drawerLayout

        // Set the custom toolbar as the action bar
        activity.setSupportActionBar(this)

        setupToolbarWithDrawer()
    }

    private fun setupToolbarWithDrawer() {
        //add drawer menu button
        toggle = ActionBarDrawerToggle(activity, drawerLayout, this, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun cleanUp(){
        drawerLayout.removeDrawerListener(toggle)
        activity.setSupportActionBar(null)
    }
}
