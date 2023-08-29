package com.appttude.h_mal.farmr.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BackPressedListener
import com.appttude.h_mal.farmr.base.BaseFragment
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.Sortable
import com.appttude.h_mal.farmr.model.Success
import com.appttude.h_mal.farmr.utils.createDialog
import com.appttude.h_mal.farmr.utils.displayToast
import com.appttude.h_mal.farmr.utils.hide
import com.appttude.h_mal.farmr.utils.navigateToFragment
import com.appttude.h_mal.farmr.utils.show
import com.appttude.h_mal.farmr.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import kotlin.system.exitProcess


class FragmentMain : BaseFragment<MainViewModel>(R.layout.fragment_main), BackPressedListener {
    private lateinit var productListView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var mAdapter: ShiftListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Shift List")
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = ShiftListAdapter(this) {
            viewModel.deleteShift(it)
        }
        productListView = view.findViewById(R.id.list_item_view)
        productListView.adapter = mAdapter
        emptyView  = view.findViewById(R.id.empty_view)

        mAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (mAdapter.itemCount == 0) emptyView.show()
                else emptyView.hide()
            }
        })

        view.findViewById<FloatingActionButton>(R.id.fab1).setOnClickListener {
            navigateToFragment(FragmentAddItem(), name = "additem")
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshLiveData()
    }

    override fun onSuccess(data: Any?) {
        super.onSuccess(data)
        if (data is List<*>) {
            @Suppress("UNCHECKED_CAST")
            mAdapter.submitList(data as List<ShiftObject>)
        }
        if (data is Success) {
            displayToast(data.successMessage)
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
                    .setPositiveButton(android.R.string.ok) { arg0, _ -> arg0.dismiss() }
                    .create().show()
                return true
            }

            R.id.filter_data -> {
                navigateToFragment(FilterDataFragment(), name = "filterdata")
                return true
            }

            R.id.sort_data -> {
                sortData()
                return true
            }

            R.id.clear_filter -> {
                viewModel.clearFilters()
                return true
            }

            R.id.export_data -> {
                if (checkStoragePermissions(activity)) {
                    AlertDialog.Builder(context)
                        .setTitle("Export?")
                        .setMessage("Exporting current filtered data. Continue?")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok) { _, _ -> exportData() }
                        .create().show()
                } else {
                    displayToast("Storage permissions required")
                }
                return true
            }

            R.id.action_favorite -> {
                AlertDialog.Builder(context)
                    .setTitle("Info:")
                    .setMessage(viewModel.getInformation())
                    .setPositiveButton(android.R.string.ok) { arg0, _ ->
                        arg0.dismiss()
                    }.create().show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sortData() {
        val groupName = Sortable.values().map { it.label }.toTypedArray()
        var sort = Sortable.ID

        val sortAndOrder = viewModel.getSortAndOrder()
        val checkedItem = Sortable.values().indexOf(sortAndOrder.first)

        AlertDialog.Builder(context)
            .setTitle("Sort by:")
            .setSingleChoiceItems(
                groupName,
                checkedItem
            ) { _, p1 -> sort = Sortable.getEnumByType(groupName[p1]) }
            .setPositiveButton("Ascending") { dialog, _ ->
                viewModel.setSortAndOrder(sort)
                dialog.dismiss()
            }.setNegativeButton("Descending") { dialog, _ ->
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

    private fun exportData() {
        val permission =
            ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Storage permissions not granted", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = "shifthistory.xls"
        val file = File(requireContext().externalCacheDir, fileName)

        viewModel.createExcelSheet(file)?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            val excelUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                file
            )
            intent.setDataAndType(excelUri, "application/vnd.ms-excel")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("request code$requestCode")
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                exportDialog()
            } else {
                displayToast("Storage Permissions denied")
            }
        }
    }

    private fun exportDialog() {
        AlertDialog.Builder(context)
            .setTitle("Export?")
            .setMessage("Exporting current filtered data. Continue?")
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ -> exportData() }.create().show()
    }

    private fun checkStoragePermissions(activity: Activity?): Boolean {
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