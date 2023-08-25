package com.appttude.h_mal.farmr.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BackPressedListener
import com.appttude.h_mal.farmr.base.BaseFragment
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.Sortable
import com.appttude.h_mal.farmr.utils.createDialog
import com.appttude.h_mal.farmr.utils.navigateToFragment
import com.appttude.h_mal.farmr.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.system.exitProcess

class FragmentMain : BaseFragment<MainViewModel>(R.layout.fragment_main), BackPressedListener {
    lateinit var activity: MainActivity
    private lateinit var productListView: RecyclerView
    private lateinit var mAdapter: ShiftRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = ShiftRecyclerAdapter(this) {
            viewModel.deleteShift(it)
        }
        productListView = view.findViewById(R.id.list_item_view)
        productListView.adapter = mAdapter

        view.findViewById<FloatingActionButton>(R.id.fab1).setOnClickListener {
            navigateToFragment(FragmentAddItem(), name = "additem")
        }
    }

    override fun onSuccess(data: Any?) {
        super.onSuccess(data)
        if (data is List<*>) {
            mAdapter.updateData(data as List<ShiftObject>)
        }
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
                    .setPositiveButton(android.R.string.yes) { arg0, arg1 -> arg0.dismiss() }
                    .create().show()
                return true
            }

            R.id.filter_data -> {
//                val fragmentTransaction: FragmentTransaction =
//                    activity.fragmentManager!!.beginTransaction()
//                fragmentTransaction.replace(R.id.container, FilterDataFragment())
//                    .addToBackStack("filterdata").commit()
                // Todo: filter shift

                return true
            }

            R.id.sort_data -> {
                sortData()
                return true
            }
            R.id.clear_filter -> {
                // Todo: Apply filter to list
                return true
            }

            R.id.export_data -> {
                if (checkStoragePermissions(activity)) {
                    AlertDialog.Builder(context)
                        .setTitle("Export?")
                        .setMessage("Exporting current filtered data. Continue?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes) { arg0, arg1 -> ExportData() }
                        .create().show()
                } else {
                    Toast.makeText(context, "Storage permissions required", Toast.LENGTH_SHORT)
                        .show()
                }
                return true
            }

            R.id.action_favorite -> {
                AlertDialog.Builder(context)
                    .setTitle("Info:")
                    .setMessage(viewModel.getInformation())
                    .setPositiveButton(android.R.string.yes) { arg0, arg1 ->
                        arg0.dismiss()
                    }.create().show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sortData() {
        val groupName = Sortable.entries.map { it.label }.toTypedArray()
        var sort = Sortable.ID

        val sortAndOrder = viewModel.getSortAndOrder()
        val checkedItem = Sortable.values().indexOf(sortAndOrder.first)

        AlertDialog.Builder(context)
            .setTitle("Sort by:")
            .setSingleChoiceItems(
                groupName,
                checkedItem
            ) { p0, p1 -> sort = Sortable.valueOf(groupName[p1]) }
            .setPositiveButton("Ascending") { dialog, id ->
                viewModel.setSortAndOrder(sort)
                dialog.dismiss()
            }.setNegativeButton("Descending") { dialog, id ->
                viewModel.setSortAndOrder(sort, Order.DESCENDING)
                dialog.dismiss()
            }
            .create().show()
    }

    private fun deleteAllProducts() {
        requireContext().createDialog(
            "Warning",
            message = "Are you sure you want to delete all date?",
            displayCancel = true,
            okCallback = { _, _ ->
                viewModel.deleteAllShifts()
            }
        )
    }

    private fun ExportData() {
//        val permission =
//            ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(context, "Storage permissions not granted", Toast.LENGTH_SHORT).show()
//            return
//        }
//        shiftsDbhelper = ShiftsDbHelper(context)
//        val database = shiftsDbhelper!!.writableDatabase
//        val projection_export = arrayOf<String?>(
//            ShiftsEntry.COLUMN_SHIFT_DESCRIPTION,
//            ShiftsEntry.COLUMN_SHIFT_DATE,
//            ShiftsEntry.COLUMN_SHIFT_TIME_IN,
//            ShiftsEntry.COLUMN_SHIFT_TIME_OUT,
//            ShiftsEntry.COLUMN_SHIFT_BREAK,
//            ShiftsEntry.COLUMN_SHIFT_DURATION,
//            ShiftsEntry.COLUMN_SHIFT_TYPE,
//            ShiftsEntry.COLUMN_SHIFT_UNIT,
//            ShiftsEntry.COLUMN_SHIFT_PAYRATE,
//            ShiftsEntry.COLUMN_SHIFT_TOTALPAY
//        )
//        val cursor = activity.contentResolver.query(
//            ShiftsEntry.CONTENT_URI,
//            projection_export,
//            activity.selection,
//            activity.args,
//            activity.sortOrder
//        )
//        database.delete(ShiftsEntry.TABLE_NAME_EXPORT, null, null)
//        var totalDuration = 0.00f
//        var totalUnits = 0.00f
//        var totalPay = 0.00f
//        try {
//            while (cursor!!.moveToNext()) {
//                val descriptionColumnIndex =
//                    cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION))
//                val dateColumnIndex =
//                    cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DATE))
//                val timeInColumnIndex =
//                    cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TIME_IN))
//                val timeOutColumnIndex =
//                    cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TIME_OUT))
//                val durationColumnIndex =
//                    cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DURATION))
//                val breakOutColumnIndex =
//                    cursor.getInt(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_BREAK))
//                val typeColumnIndex =
//                    cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TYPE))
//                val unitColumnIndex =
//                    cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_UNIT))
//                val payrateColumnIndex =
//                    cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_PAYRATE))
//                val totalpayColumnIndex =
//                    cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TOTALPAY))
//                totalUnits = totalUnits + unitColumnIndex
//                totalDuration = totalDuration + durationColumnIndex
//                totalPay = totalPay + totalpayColumnIndex
//                val values = ContentValues()
//                values.put(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION, descriptionColumnIndex)
//                values.put(ShiftsEntry.COLUMN_SHIFT_DATE, dateColumnIndex)
//                values.put(ShiftsEntry.COLUMN_SHIFT_TIME_IN, timeInColumnIndex)
//                values.put(ShiftsEntry.COLUMN_SHIFT_TIME_OUT, timeOutColumnIndex)
//                values.put(ShiftsEntry.COLUMN_SHIFT_BREAK, breakOutColumnIndex)
//                values.put(ShiftsEntry.COLUMN_SHIFT_DURATION, durationColumnIndex)
//                values.put(ShiftsEntry.COLUMN_SHIFT_TYPE, typeColumnIndex)
//                values.put(ShiftsEntry.COLUMN_SHIFT_UNIT, unitColumnIndex)
//                values.put(ShiftsEntry.COLUMN_SHIFT_PAYRATE, payrateColumnIndex)
//                values.put(ShiftsEntry.COLUMN_SHIFT_TOTALPAY, totalpayColumnIndex)
//                database.insert(ShiftsEntry.TABLE_NAME_EXPORT, null, values)
//            }
//        } catch (e: Exception) {
//            Log.e("FragmentMain", "ExportData: ", e)
//        } finally {
//            val values = ContentValues()
//            values.put(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION, "")
//            values.put(ShiftsEntry.COLUMN_SHIFT_DATE, "")
//            values.put(ShiftsEntry.COLUMN_SHIFT_TIME_IN, "")
//            values.put(ShiftsEntry.COLUMN_SHIFT_TIME_OUT, "")
//            values.put(ShiftsEntry.COLUMN_SHIFT_BREAK, "Total duration:")
//            values.put(ShiftsEntry.COLUMN_SHIFT_DURATION, totalDuration)
//            values.put(ShiftsEntry.COLUMN_SHIFT_TYPE, "Total units:")
//            values.put(ShiftsEntry.COLUMN_SHIFT_UNIT, totalUnits)
//            values.put(ShiftsEntry.COLUMN_SHIFT_PAYRATE, "Total pay:")
//            values.put(ShiftsEntry.COLUMN_SHIFT_TOTALPAY, totalPay)
//            database.insert(ShiftsEntry.TABLE_NAME_EXPORT, null, values)
//            cursor!!.close()
//        }
//        val savePath = Environment.getExternalStorageDirectory().toString() + "/ShifttrackerTemp"
//        val file = File(savePath)
//        if (!file.exists()) {
//            file.mkdirs()
//        }
//        val sqLiteToExcel = SQLiteToExcel(context, "shifts.db", savePath)
//        sqLiteToExcel.exportSingleTable(
//            "shiftsexport",
//            "shifthistory.xls",
//            object : ExportListener {
//                override fun onStart() {}
//                override fun onCompleted(filePath: String) {
//                    Toast.makeText(context, filePath, Toast.LENGTH_SHORT).show()
//                    val newPath = Uri.parse("file://$savePath/shifthistory.xls")
//                    val builder = VmPolicy.Builder()
//                    StrictMode.setVmPolicy(builder.build())
//                    val emailintent = Intent(Intent.ACTION_SEND)
//                    emailintent.type = "application/vnd.ms-excel"
//                    emailintent.putExtra(Intent.EXTRA_SUBJECT, "historic shifts")
//                    emailintent.putExtra(Intent.EXTRA_TEXT, "I'm email body.")
//                    emailintent.putExtra(Intent.EXTRA_STREAM, newPath)
//                    startActivity(Intent.createChooser(emailintent, "Send Email"))
//                }
//
//                override fun onError(e: Exception) {
//                    println("Error msg: $e")
//                    Toast.makeText(context, "Failed to Export data", Toast.LENGTH_SHORT).show()
//                }
//            })
    }

    @SuppressLint("DefaultLocale")
    fun buildInfoString(
        totalDuration: Float,
        countOfTypeH: Int,
        countOfTypeP: Int,
        totalUnits: Float,
        totalPay: Float,
        lines: Int
    ): String {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("request code$requestCode")
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
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

    fun checkStoragePermissions(activity: Activity?): Boolean {
        var status = false
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            status = true
        }
        return status
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    }

    override fun onBackPressed(): Boolean {
        requireContext().createDialog(
            title = "Leave?",
            message = "Are you sure you want to exit Farmr?",
            displayCancel = true,
            okCallback = { _, _ ->
                val intent = Intent(Intent.ACTION_MAIN)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
                requireActivity().finish()
                exitProcess(0)
            }
        )
        return true
    }
}