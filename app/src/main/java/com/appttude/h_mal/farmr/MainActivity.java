package com.appttude.h_mal.farmr;

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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences filter;
    public static Context context;
    static String sortOrder;
    public static String selection;
    public static String[] args;
    private static Toolbar toolbar;
    public static FragmentManager fragmentManager;
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

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                List<Fragment> f = fragmentManager.getFragments();
                Fragment frag = f.get(0);
                currentFragment = frag.getClass().getSimpleName();
            }
        });

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
