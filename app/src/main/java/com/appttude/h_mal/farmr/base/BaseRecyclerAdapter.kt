package com.appttude.h_mal.farmr.base

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.appttude.h_mal.farmr.utils.generateView

open class BaseRecyclerAdapter<T: Any>(
    @LayoutRes private val emptyViewId: Int,
    @LayoutRes private val currentViewId: Int
): RecyclerView.Adapter<ViewHolder>()  {
    var list: List<T>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (list.isNullOrEmpty()) {
            val emptyViewHolder = parent.generateView(emptyViewId)
            EmptyViewHolder(emptyViewHolder)
        } else {
            val currentViewHolder = parent.generateView(currentViewId)
            CurrentViewHolder(currentViewHolder)
        }
    }

    override fun getItemCount(): Int {
        return if (list.isNullOrEmpty()) 1 else list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is EmptyViewHolder -> bindEmptyView(holder.itemView)
            is CurrentViewHolder -> bindCurrentView(holder.itemView, position, list!![position])
        }
    }

    open fun bindEmptyView(view: View) {}
    open fun bindCurrentView(view: View, position: Int, data: T) {}

    class EmptyViewHolder(itemView: View): ViewHolder(itemView)
    class CurrentViewHolder(itemView: View): ViewHolder(itemView)
}