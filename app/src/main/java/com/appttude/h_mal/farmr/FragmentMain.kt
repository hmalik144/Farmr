package com.appttude.h_mal.farmr

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.ajts.androidmads.library.SQLiteToExcel
import com.ajts.androidmads.library.SQLiteToExcel.ExportListener
import com.appttude.h_mal.farmr.data.ShiftsContract.ShiftsEntry
import com.appttude.h_mal.farmr.data.ShiftsDbHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class FragmentMain : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    var mCursorAdapter: ShiftsCursorAdapter? = null
    var shiftsDbhelper: ShiftsDbHelper? = null
    lateinit var defaultLoaderCallback: LoaderManager.LoaderCallbacks<Cursor>
    lateinit var activity: MainActivity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        setHasOptionsMenu(true)
        activity = (requireActivity() as MainActivity)

        activity.setActionBarTitle(getString(R.string.app_name))
        activity.filter = activity.getSharedPreferences("PREFS", 0)
        activity.sortOrder = activity.filter?.getString("Filter", null)
        defaultLoaderCallback = this
        val productListView = rootView.findViewById<View>(R.id.list_item_view) as ListView
        val emptyView = rootView.findViewById<View>(R.id.empty_view)
        productListView.emptyView = emptyView
        mCursorAdapter = ShiftsCursorAdapter(activity, null)
        productListView.adapter = mCursorAdapter
        loaderManager.initLoader(DEFAULT_LOADER, null, defaultLoaderCallback)
        val fab = rootView.findViewById<FloatingActionButton>(R.id.fab1)
        fab.setOnClickListener {
            val fragmentTransaction: FragmentTransaction = activity.fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.container, FragmentAddItem()).addToBackStack("additem").commit()
        }
        return rootView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> {
                deleteAllProducts()
                return true
            }

            R.id.help -> {
                AlertDialog.Builder(context)
                        .setTitle("Help & Support:")
                        .setView(R.layout.dialog_layout)
                        .setPositiveButton(android.R.string.yes) { arg0, arg1 -> }.create().show()
                return true
            }

            R.id.filter_data -> {
                val fragmentTransaction: FragmentTransaction = activity.fragmentManager!!.beginTransaction()
                fragmentTransaction.replace(R.id.container, FilterDataFragment()).addToBackStack("filterdata").commit()
                return true
            }

            R.id.sort_data -> {
                sortData()
                return true
            }

            R.id.clear_filter -> {
                activity.args = null
                activity.selection = null
                NEW_LOADER = 0
                loaderManager.restartLoader(DEFAULT_LOADER, null, this)
                return true
            }

            R.id.export_data -> {
                if (checkStoragePermissions(activity)) {
                    AlertDialog.Builder(context)
                            .setTitle("Export?")
                            .setMessage("Exporting current filtered data. Continue?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes) { arg0, arg1 -> ExportData() }.create().show()
                } else {
                    Toast.makeText(context, "Storage permissions required", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            R.id.action_favorite -> {
                AlertDialog.Builder(context)
                        .setTitle("Info:")
                        .setMessage(retrieveInfo())
                        .setPositiveButton(android.R.string.yes) { arg0, arg1 -> }.create().show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sortData() {
        val grpname = arrayOf("Added", "Date", "Name")
        val sortQuery = arrayOf<String?>("")
        var checkedItem = -1
        if (activity.sortOrder != null && activity.sortOrder!!.contains(ShiftsEntry._ID)) {
            checkedItem = 0
            sortQuery[0] = ShiftsEntry._ID
        } else if (activity.sortOrder != null && activity.sortOrder!!.contains(ShiftsEntry.COLUMN_SHIFT_DATE)) {
            checkedItem = 1
            sortQuery[0] = ShiftsEntry.COLUMN_SHIFT_DATE
        } else if (activity.sortOrder != null && activity.sortOrder!!.contains(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION)) {
            checkedItem = 2
            sortQuery[0] = ShiftsEntry.COLUMN_SHIFT_DESCRIPTION
        }
        val alt_bld = AlertDialog.Builder(context)
        //alt_bld.setIcon(R.drawable.icon);
        alt_bld.setTitle("Sort by:")
        alt_bld.setSingleChoiceItems(grpname, checkedItem, DialogInterface.OnClickListener { dialog, item ->
            when (item) {
                0 -> {
                    sortQuery[0] = ShiftsEntry._ID
                    return@OnClickListener
                }

                1 -> {
                    sortQuery[0] = ShiftsEntry.COLUMN_SHIFT_DATE
                    return@OnClickListener
                }

                2 -> sortQuery[0] = ShiftsEntry.COLUMN_SHIFT_DESCRIPTION
            }
        }).setPositiveButton("Ascending") { dialog, id ->
            activity.sortOrder = sortQuery[0] + " ASC"
            activity.filter!!.edit().putString("Filter", activity.sortOrder).apply()
            loaderManager.restartLoader(DEFAULT_LOADER, null, defaultLoaderCallback)
            dialog.dismiss()
        }.setNegativeButton("Descending") { dialog, id ->
            activity.sortOrder = sortQuery[0] + " DESC"
            activity.filter!!.edit().putString("Filter", activity.sortOrder).apply()
            loaderManager.restartLoader(DEFAULT_LOADER, null, defaultLoaderCallback)
            dialog.dismiss()
        }
        val alert = alt_bld.create()
        alert.show()
    }

    private fun deleteAllProducts() {
        AlertDialog.Builder(context)
                .setTitle("Delete?")
                .setMessage("Are you sure you want to delete all date?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes) { arg0, arg1 ->
                    val rowsDeleted = activity.contentResolver.delete(ShiftsEntry.CONTENT_URI, null, null)
                    Toast.makeText(context, "$rowsDeleted Items Deleted", Toast.LENGTH_SHORT).show()
                }.create().show()
    }

    override fun onResume() {
        super.onResume()
        if (NEW_LOADER > DEFAULT_LOADER) {
            loaderManager.restartLoader(DEFAULT_LOADER, null, defaultLoaderCallback)
            println("reloading loader")
        }
    }

    private fun ExportData() {
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Storage permissions not granted", Toast.LENGTH_SHORT).show()
            return
        }
        shiftsDbhelper = ShiftsDbHelper(context)
        val database = shiftsDbhelper!!.writableDatabase
        val projection_export = arrayOf<String?>(
                ShiftsEntry.COLUMN_SHIFT_DESCRIPTION,
                ShiftsEntry.COLUMN_SHIFT_DATE,
                ShiftsEntry.COLUMN_SHIFT_TIME_IN,
                ShiftsEntry.COLUMN_SHIFT_TIME_OUT,
                ShiftsEntry.COLUMN_SHIFT_BREAK,
                ShiftsEntry.COLUMN_SHIFT_DURATION,
                ShiftsEntry.COLUMN_SHIFT_TYPE,
                ShiftsEntry.COLUMN_SHIFT_UNIT,
                ShiftsEntry.COLUMN_SHIFT_PAYRATE,
                ShiftsEntry.COLUMN_SHIFT_TOTALPAY)
        val cursor = activity.contentResolver.query(
                ShiftsEntry.CONTENT_URI,
                projection_export,
                activity.selection,
                activity.args,
                activity.sortOrder)
        database.delete(ShiftsEntry.TABLE_NAME_EXPORT, null, null)
        var totalDuration = 0.00f
        var totalUnits = 0.00f
        var totalPay = 0.00f
        try {
            while (cursor!!.moveToNext()) {
                val descriptionColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION))
                val dateColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DATE))
                val timeInColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TIME_IN))
                val timeOutColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TIME_OUT))
                val durationColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DURATION))
                val breakOutColumnIndex = cursor.getInt(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_BREAK))
                val typeColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TYPE))
                val unitColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_UNIT))
                val payrateColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_PAYRATE))
                val totalpayColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TOTALPAY))
                totalUnits = totalUnits + unitColumnIndex
                totalDuration = totalDuration + durationColumnIndex
                totalPay = totalPay + totalpayColumnIndex
                val values = ContentValues()
                values.put(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION, descriptionColumnIndex)
                values.put(ShiftsEntry.COLUMN_SHIFT_DATE, dateColumnIndex)
                values.put(ShiftsEntry.COLUMN_SHIFT_TIME_IN, timeInColumnIndex)
                values.put(ShiftsEntry.COLUMN_SHIFT_TIME_OUT, timeOutColumnIndex)
                values.put(ShiftsEntry.COLUMN_SHIFT_BREAK, breakOutColumnIndex)
                values.put(ShiftsEntry.COLUMN_SHIFT_DURATION, durationColumnIndex)
                values.put(ShiftsEntry.COLUMN_SHIFT_TYPE, typeColumnIndex)
                values.put(ShiftsEntry.COLUMN_SHIFT_UNIT, unitColumnIndex)
                values.put(ShiftsEntry.COLUMN_SHIFT_PAYRATE, payrateColumnIndex)
                values.put(ShiftsEntry.COLUMN_SHIFT_TOTALPAY, totalpayColumnIndex)
                database.insert(ShiftsEntry.TABLE_NAME_EXPORT, null, values)
            }
        } catch (e: Exception) {
            Log.e("FragmentMain", "ExportData: ", e)
        } finally {
            val values = ContentValues()
            values.put(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION, "")
            values.put(ShiftsEntry.COLUMN_SHIFT_DATE, "")
            values.put(ShiftsEntry.COLUMN_SHIFT_TIME_IN, "")
            values.put(ShiftsEntry.COLUMN_SHIFT_TIME_OUT, "")
            values.put(ShiftsEntry.COLUMN_SHIFT_BREAK, "Total duration:")
            values.put(ShiftsEntry.COLUMN_SHIFT_DURATION, totalDuration)
            values.put(ShiftsEntry.COLUMN_SHIFT_TYPE, "Total units:")
            values.put(ShiftsEntry.COLUMN_SHIFT_UNIT, totalUnits)
            values.put(ShiftsEntry.COLUMN_SHIFT_PAYRATE, "Total pay:")
            values.put(ShiftsEntry.COLUMN_SHIFT_TOTALPAY, totalPay)
            database.insert(ShiftsEntry.TABLE_NAME_EXPORT, null, values)
            cursor!!.close()
        }
        val savePath = Environment.getExternalStorageDirectory().toString() + "/ShifttrackerTemp"
        val file = File(savePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val sqLiteToExcel = SQLiteToExcel(context, "shifts.db", savePath)
        sqLiteToExcel.exportSingleTable("shiftsexport", "shifthistory.xls", object : ExportListener {
            override fun onStart() {}
            override fun onCompleted(filePath: String) {
                Toast.makeText(context, filePath, Toast.LENGTH_SHORT).show()
                val newPath = Uri.parse("file://$savePath/shifthistory.xls")
                val builder = VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                val emailintent = Intent(Intent.ACTION_SEND)
                emailintent.type = "application/vnd.ms-excel"
                emailintent.putExtra(Intent.EXTRA_SUBJECT, "historic shifts")
                emailintent.putExtra(Intent.EXTRA_TEXT, "I'm email body.")
                emailintent.putExtra(Intent.EXTRA_STREAM, newPath)
                startActivity(Intent.createChooser(emailintent, "Send Email"))
            }

            override fun onError(e: Exception) {
                println("Error msg: $e")
                Toast.makeText(context, "Failed to Export data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun retrieveInfo(): String {
        val projection = arrayOf<String?>(
                ShiftsEntry._ID,
                ShiftsEntry.COLUMN_SHIFT_DESCRIPTION,
                ShiftsEntry.COLUMN_SHIFT_DATE,
                ShiftsEntry.COLUMN_SHIFT_TIME_IN,
                ShiftsEntry.COLUMN_SHIFT_TIME_OUT,
                ShiftsEntry.COLUMN_SHIFT_BREAK,
                ShiftsEntry.COLUMN_SHIFT_DURATION,
                ShiftsEntry.COLUMN_SHIFT_TYPE,
                ShiftsEntry.COLUMN_SHIFT_UNIT,
                ShiftsEntry.COLUMN_SHIFT_PAYRATE,
                ShiftsEntry.COLUMN_SHIFT_TOTALPAY)
        val cursor = activity.contentResolver.query(
                ShiftsEntry.CONTENT_URI,
                projection,
                activity.selection,
                activity.args,
                activity.sortOrder)
        var totalDuration = 0.0f
        var countOfTypeH = 0
        var countOfTypeP = 0
        var totalUnits = 0f
        var totalPay = 0f
        var lines = 0
        try {
            while (cursor!!.moveToNext()) {
                val durationColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DURATION))
                val typeColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TYPE))
                val unitColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_UNIT))
                val totalpayColumnIndex = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TOTALPAY))
                totalDuration = totalDuration + durationColumnIndex
                if (typeColumnIndex.contains("Hourly")) {
                    countOfTypeH = countOfTypeH + 1
                } else if (typeColumnIndex.contains("Piece")) {
                    countOfTypeP = countOfTypeP + 1
                }
                totalUnits = totalUnits + unitColumnIndex
                totalPay = totalPay + totalpayColumnIndex
            }
        } finally {
            if (cursor != null && cursor.count > 0) {
                lines = cursor.count
                cursor.close()
            }
        }
        return buildInfoString(totalDuration, countOfTypeH, countOfTypeP, totalUnits, totalPay, lines)
    }

    @SuppressLint("DefaultLocale")
    fun buildInfoString(totalDuration: Float, countOfTypeH: Int, countOfTypeP: Int, totalUnits: Float, totalPay: Float, lines: Int): String {
        var textString: String
        textString = "$lines Shifts"
        if (countOfTypeH != 0 && countOfTypeP != 0) {
            textString = "$textString ($countOfTypeH Hourly/$countOfTypeP Piece Rate)"
        }
        if (countOfTypeH != 0) {
            textString = """
                $textString
                Total Hours: ${String.format("%.2f", totalDuration)}
                """.trimIndent()
        }
        if (countOfTypeP != 0) {
            textString = """
                $textString
                Total Units: ${String.format("%.2f", totalUnits)}
                """.trimIndent()
        }
        if (totalPay != 0f) {
            textString = """
                $textString
                Total Pay: ${"$"}${String.format("%.2f", totalPay)}
                """.trimIndent()
        }
        return textString
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        val projection = arrayOf<String?>(
                ShiftsEntry._ID,
                ShiftsEntry.COLUMN_SHIFT_DESCRIPTION,
                ShiftsEntry.COLUMN_SHIFT_DATE,
                ShiftsEntry.COLUMN_SHIFT_TIME_IN,
                ShiftsEntry.COLUMN_SHIFT_TIME_OUT,
                ShiftsEntry.COLUMN_SHIFT_BREAK,
                ShiftsEntry.COLUMN_SHIFT_DURATION,
                ShiftsEntry.COLUMN_SHIFT_TYPE,
                ShiftsEntry.COLUMN_SHIFT_PAYRATE,
                ShiftsEntry.COLUMN_SHIFT_UNIT,
                ShiftsEntry.COLUMN_SHIFT_TOTALPAY)
        return CursorLoader(context!!,
                ShiftsEntry.CONTENT_URI,
                projection,
                activity.selection,
                activity.args,
                activity.sortOrder)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        mCursorAdapter!!.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mCursorAdapter!!.swapCursor(null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("request code$requestCode")
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportDialog()
            } else {
                Toast.makeText(context, "Storage Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun exportDialog() {
        AlertDialog.Builder(context)
                .setTitle("Export?")
                .setMessage("Exporting current filtered data. Continue?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes) { arg0, arg1 -> ExportData() }.create().show()
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
        private const val DEFAULT_LOADER = 0
        var NEW_LOADER = 0

        //    // Storage Permissions
        //    private static final int REQUEST_EXTERNAL_STORAGE = 1;
        //    private static String[] PERMISSIONS_STORAGE = {
        //            Manifest.permission.READ_EXTERNAL_STORAGE,
        //            Manifest.permission.WRITE_EXTERNAL_STORAGE
        //    };
        //    /**
        //     * Checks if the app has permission to write to device storage
        //     *
        //     * If the app does not has permission then the user will be prompted to grant permissions
        //     *
        //     * @param activity
        //     */
        //    public static void verifyStoragePermissions(Activity activity) {
        //        // Check if we have write permission
        //        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //
        //        if (permission != PackageManager.PERMISSION_GRANTED) {
        //            // We don't have permission so prompt the user
        //            ActivityCompat.requestPermissions(
        //                    activity,
        //                    PERMISSIONS_STORAGE,
        //                    REQUEST_EXTERNAL_STORAGE
        //            );
        //        }
        //    }
        fun checkStoragePermissions(activity: Activity?): Boolean {
            var status = false
            val permission = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                status = true
            }
            return status
        }
    }
}