package com.samyak.smailtm

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var bottomNavigation: BottomNavigationView
    private var homeFragment: HomeFragment? = null
    private var inboxFragment: InboxFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        setupStatusBar()
        
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        bottomNavigation = findViewById(R.id.bottomNavigation)
        
        setupBottomNavigation()
        
        // Show home fragment by default
        if (savedInstanceState == null) {
            showHomeFragment()
        }
    }
    
    private fun setupStatusBar() {
        // Set status bar color to match dark theme
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTheme)
        
        // Set navigation bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorTheme)
        
        // Set status bar icons to light color (for dark background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and 
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showHomeFragment()
                    true
                }
                R.id.nav_inbox -> {
                    showInboxFragment()
                    true
                }
                else -> false
            }
        }
    }

    private fun showHomeFragment() {
        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance()
            setupMessageListener()
        }
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, homeFragment!!)
            .commit()
    }

    private fun showInboxFragment() {
        if (inboxFragment == null) {
            inboxFragment = InboxFragment.newInstance()
        }
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, inboxFragment!!)
            .commit()
    }
    
    private fun setupMessageListener() {
        homeFragment?.setOnMessageReceivedListener { message ->
            inboxFragment?.addNewMessage(message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.mailTM.value?.closeMessageListener()
    }
}
