package com.appttude.h_mal.farmr.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.ChildFragment
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.model.Success
import com.appttude.h_mal.farmr.utils.displayToast
import com.appttude.h_mal.farmr.utils.navigateTo
import com.appttude.h_mal.farmr.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FragmentList : ChildFragment<MainViewModel>(R.layout.fragment_list) {
    private lateinit var productListView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var mAdapter: ShiftListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyView = view.findViewById(R.id.empty_view)
        productListView = view.findViewById(R.id.list_item_view)

        mAdapter = ShiftListAdapter(this, emptyView, viewModel)
        productListView.adapter = mAdapter
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
        } else if (data is Success) {
            displayToast(data.successMessage)
        }
    }

}