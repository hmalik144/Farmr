package com.appttude.h_mal.farmr.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseFragment
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.Sortable
import com.appttude.h_mal.farmr.utils.createDialog
import com.appttude.h_mal.farmr.utils.navigateTo
import com.appttude.h_mal.farmr.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import kotlin.system.exitProcess


class FragmentMain : BaseFragment<MainViewModel>(R.layout.fragment_main) {
    private lateinit var onBackPressed: OnBackPressedCallback

    lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        // This callback is only called when MyFragment is at least started
        onBackPressed = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressed)
    }

    override fun onResume() {
        super.onResume()

        onBackPressed.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        onBackPressed.isEnabled = false

        viewModel.saveBottomBarState(navView.selectedItemId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navView = view.findViewById(R.id.bottom_bar)
        val navHost = childFragmentManager.findFragmentById(R.id.sub_container) as NavHostFragment

        val navController = navHost.navController
        navController.setGraph(R.navigation.home_navigation)

        navView.setupWithNavController(navController)

        viewModel.getBottomBarState()?.let {
            navView.selectedItemId = it
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            setTitle(destination.label.toString())
        }

        view.findViewById<FloatingActionButton>(R.id.fab1).setOnClickListener {
            navigateTo(R.id.main_to_addItem)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu)
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
                navigateTo(R.id.main_to_filterData)
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
                AlertDialog.Builder(context)
                    .setTitle("Export?")
                    .setMessage("Exporting current filtered data. Continue?")
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok) { _, _ -> exportData() }
                    .create().show()
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
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }

    }

    fun onBackPressed() {
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
    }
}