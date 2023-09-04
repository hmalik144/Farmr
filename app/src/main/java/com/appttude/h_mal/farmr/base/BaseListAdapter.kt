package com.appttude.h_mal.farmr.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appttude.h_mal.farmr.utils.hide
import com.appttude.h_mal.farmr.utils.show

abstract class BaseListAdapter<T : Any>(
    diff: DiffUtil.ItemCallback<T>,
    private val layoutId: Int,
    private val emptyView: View
) : ListAdapter<T, BaseListAdapter.CurrentViewHolder>(diff) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrentViewHolder {
        val currentViewHolder = LayoutInflater
            .from(parent.context)
            .inflate(layoutId, parent, false)
        return CurrentViewHolder(currentViewHolder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                checkEmpty()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                checkEmpty()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                checkEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                checkEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                checkEmpty()
            }

            fun checkEmpty() {
                if (itemCount == 0) emptyView.show()
                else emptyView.hide()
            }
        })
    }

    class CurrentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}