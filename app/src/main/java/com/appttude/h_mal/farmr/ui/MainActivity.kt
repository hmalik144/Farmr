package com.appttude.h_mal.farmr.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseActivity

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}