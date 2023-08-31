package com.appttude.h_mal.farmr.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BackPressedListener
import com.appttude.h_mal.farmr.base.BaseActivity
import com.appttude.h_mal.farmr.utils.goBack
import com.appttude.h_mal.farmr.utils.popBackStack

class MainActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var navHost: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navHost = supportFragmentManager
            .findFragmentById(R.id.container) as NavHostFragment
        val navController = navHost.navController
        navController.setGraph(R.navigation.shift_navigation)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        if (currentFragment is BackPressedListener) {
            currentFragment.onBackPressed()
        } else {
            if (supportFragmentManager.backStackEntryCount > 1) {
                navHost.goBack()
            } else {
                super.onBackPressed()
            }
        }
    }
}