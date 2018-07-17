package com.example.h_mal.shift_tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.RelativeLayout;

/**
 * Created by h_mal on 27/06/2017.
 */

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.hyperspace_jump, android.R.anim.fade_out).toBundle();

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.splash_layout);

        final Intent i = new Intent(SplashScreen.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
//                startActivity(i,bundle);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,R.anim.hyperspace_jump);
//                finish();
            }
        }, SPLASH_TIME_OUT);
    }


}