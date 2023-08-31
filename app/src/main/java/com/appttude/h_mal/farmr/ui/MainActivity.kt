package com.appttude.h_mal.farmr.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BackPressedListener
import com.appttude.h_mal.farmr.base.BaseActivity
import com.appttude.h_mal.farmr.utils.popBackStack

class MainActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, FragmentMain()).addToBackStack("main").commit()
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
                // Todo: go back
//                goBack()
            } else {
                super.onBackPressed()
            }
        }
    }
}