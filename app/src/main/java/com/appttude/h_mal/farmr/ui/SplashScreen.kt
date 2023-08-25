package com.appttude.h_mal.farmr.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RelativeLayout
import androidx.core.app.ActivityOptionsCompat
import com.appttude.h_mal.farmr.R

/**
 * Created by h_mal on 27/06/2017.
 */
class SplashScreen : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val bundle = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.hyperspace_jump, android.R.anim.fade_out).toBundle()
        val relativeLayout = findViewById<View>(R.id.splash_layout) as RelativeLayout
        val i = Intent(this@SplashScreen, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
//                startActivity(i,bundle);
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            //                finish();
        }, SPLASH_TIME_OUT.toLong())
    }

    companion object {
        // Splash screen timer
        private const val SPLASH_TIME_OUT = 2000
    }
}