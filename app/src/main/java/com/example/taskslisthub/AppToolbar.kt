package com.example.taskslisthub

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.taskslisthub.databinding.AppToolbarBinding
import com.google.android.material.navigation.NavigationView

class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: AppToolbarBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    init {
        orientation = HORIZONTAL
        // Inflate custom toolbar layout
        val inflater = LayoutInflater.from(context)
        binding = AppToolbarBinding.inflate(inflater, this)

        // Handle drawer button behavior using ViewBinding
        binding.drawerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun bindWithDrawerLayout(activity: AppCompatActivity, drawerLayout: DrawerLayout) {
        if (activity !is NavigationView.OnNavigationItemSelectedListener)
            throw IllegalArgumentException("Activity must implement NavigationView.OnNavigationItemSelectedListener")

        this.drawerLayout = drawerLayout

        toggle = ActionBarDrawerToggle(activity, drawerLayout, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun cleanUp(){
        drawerLayout.removeDrawerListener(toggle)
    }

    fun addCustomView(view: View) {
        binding.customContainer.addView(view)
    }
}
