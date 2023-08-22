package com.appttude.h_mal.farmr

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {
    var filter: SharedPreferences? = null
    var context: Context? = null
    var sortOrder: String? = null
    var selection: String? = null
    var args: Array<String>? = null
    private var toolbar: Toolbar? = null
    var fragmentManager: FragmentManager? = null
    private var currentFragment: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view)
        verifyStoragePermissions(this)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.container, FragmentMain())?.addToBackStack("main")?.commit()
        fragmentManager?.addOnBackStackChangedListener {
            val f = fragmentManager?.fragments
            val frag = f?.get(0)
            currentFragment = frag?.javaClass?.simpleName
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onBackPressed() {
        when (currentFragment) {
            "FragmentMain" -> {
                AlertDialog.Builder(this)
                        .setTitle("Leave?")
                        .setMessage("Are you sure you want to exit Farmr?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes) { arg0, arg1 ->
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            intent.addCategory(Intent.CATEGORY_HOME)
                            startActivity(intent)
                            finish()
                            System.exit(0)
                        }.create().show()
                return
            }

            "FragmentAddItem" -> {
                if (FragmentAddItem.Companion.mRadioGroup!!.checkedRadioButtonId == -1) {
                    fragmentManager!!.popBackStack()
                } else {
                    AlertDialog.Builder(this)
                            .setTitle("Discard Changes?")
                            .setMessage("Are you sure you want to discard changes?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes) { arg0, arg1 -> fragmentManager!!.popBackStack() }.create().show()
                }
                return
            }

            else -> if (fragmentManager!!.backStackEntryCount > 1) {
                fragmentManager!!.popBackStack()
            }
        }
    }

    fun setActionBarTitle(title: String?) {
        toolbar!!.title = title
    }

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}