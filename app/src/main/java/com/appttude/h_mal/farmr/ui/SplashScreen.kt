package com.appttude.h_mal.farmr.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.appttude.h_mal.farmr.R

/**
 * Created by h_mal on 27/06/2017.
 */
@SuppressLint("CustomSplashScreen")
class SplashScreen : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val i = Intent(this@SplashScreen, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            this.finish()
        }, SPLASH_TIME_OUT)
    }

    companion object {
        // Splash screen timer
        const val SPLASH_TIME_OUT: Long = 2000
    }
}