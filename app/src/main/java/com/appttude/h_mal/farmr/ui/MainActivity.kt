package com.appttude.h_mal.farmr.ui

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
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

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        if (currentFragment is BackPressedListener) {
            currentFragment.onBackPressed()
        } else {
            if (supportFragmentManager.backStackEntryCount > 1) {
                popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }
}