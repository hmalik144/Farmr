package com.example.h_mal.shift_tracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences filter;
    public static Context context;
    static String sortOrder;
    public static String selection;
    public static String[] args;
    private static Toolbar toolbar;
    public static FragmentManager fragmentManager;
    private AdView adView;
    private ProgressBar progressBar;
    private String currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        verifyStoragePermissions(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,new FragmentMain()).addToBackStack("main").commit();

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                List<Fragment> f = fragmentManager.getFragments();
                Fragment frag = f.get(0);
                currentFragment = frag.getClass().getSimpleName();
            }
        });

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        adView = (AdView) findViewById(R.id.adView);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                progressBar.setVisibility(View.GONE);
            }
        });
        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

    }
    public static void setActionBarTitle(String title){
        toolbar.setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        switch (currentFragment) {
            case "FragmentMain":
                new AlertDialog.Builder(this)
                        .setTitle("Leave?")
                        .setMessage("Are you sure you want to exit Farmr?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                startActivity(intent);
                                finish();
                                System.exit(0);
                            }
                        }).create().show();
                return;
            case "FragmentAddItem":
                if(FragmentAddItem.mRadioGroup.getCheckedRadioButtonId() == -1) {
                    fragmentManager.popBackStack();
                }else{
                    new AlertDialog.Builder(this)
                            .setTitle("Discard Changes?")
                            .setMessage("Are you sure you want to discard changes?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    fragmentManager.popBackStack();
                                }
                            }).create().show();

                }
                return;
            default:
                if (fragmentManager.getBackStackEntryCount() > 1) {
                    fragmentManager.popBackStack();
                }
        }

    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//
//        Intent intent = getIntent();
//        String activity = intent.getStringExtra("activity");
//
//        if (activity == null || !activity.equals("first") || !activity.equals("firsttime")) {
//            NavUtils.navigateUpFromSameTask(MainActivity.this);
//        } else {
//            getIntent().removeExtra("activity");
//        }
//
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        // If the screen is off then the device has been locked
//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//        boolean isScreenOn;
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//            isScreenOn = powerManager.isInteractive();
//        } else {
//            isScreenOn = powerManager.isScreenOn();
//        }
//
//        if (!isScreenOn) {
//            Intent intent = getIntent();
//            String activity = intent.getStringExtra("activity");
//            if (activity == null || !activity.equals("first") || !activity.equals("firsttime")){
//                NavUtils.navigateUpFromSameTask(MainActivity.this);
//            }else {
//                getIntent().removeExtra("activity");
//            }
//            System.out.println("mainactivity");
//        }
//    }
}
