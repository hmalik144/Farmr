package com.appttude.h_mal.farmr.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseListAdapter
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.utils.ID
import com.appttude.h_mal.farmr.utils.formatToTwoDpString
import com.appttude.h_mal.farmr.utils.navigateToFragment

class ShiftListAdapter(
    private val fragment: Fragment,
    emptyView: View,
    private val longPressCallback: (Long) -> Unit
) : BaseListAdapter<ShiftObject>(diffCallBack, R.layout.list_item_1, emptyView) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CurrentViewHolder, position: Int) {
        val view = holder.itemView
        val data = getItem(position)

        val descriptionTextView: TextView = view.findViewById(R.id.location)
        val dateTextView: TextView = view.findViewById(R.id.date)
        val totalPay: TextView = view.findViewById(R.id.total_pay)
        val hoursView: TextView = view.findViewById(R.id.hours)
        val h: TextView = view.findViewById(R.id.h)
        val minutesView: TextView = view.findViewById(R.id.minutes)
        val m: TextView = view.findViewById(R.id.m)
        val editView: ImageView = view.findViewById(R.id.imageView)
        h.text = "h"
        m.text = "m"
        val typeText: String = data.type
        val descriptionText: String = data.description
        val dateText: String = data.date
        val totalPayText: String = data.totalPay.formatToTwoDpString()

        descriptionTextView.text = descriptionText
        dateTextView.text = dateText
        totalPay.text = totalPayText

        when (ShiftType.getEnumByType(typeText)) {
            ShiftType.HOURLY -> {
                val time = data.getHoursMinutesPairFromDuration()

                hoursView.text = time.first
                minutesView.text = time.second
            }

            ShiftType.PIECE -> {
                val unitsText: String = data.units.toString()

                hoursView.text = unitsText
                h.text = ""
                minutesView.text = ""
                m.text = "pcs"
            }
        }

        val b: Bundle = Bundle()
        b.putLong(ID, data.id)
        view.setOnClickListener {
            // Navigate to further info
            fragment.navigateToFragment(
                FurtherInfoFragment(),
                bundle = b,
                name = "furtherinfo"
            )
        }
        editView.setOnClickListener {
            // Navigate to edit
            fragment.navigateToFragment(
                FragmentAddItem(),
                bundle = b,
                name = "additem"
            )
        }
        view.setOnLongClickListener {
            AlertDialog.Builder(it.context)
                .setMessage("Are you sure you want to delete")
                .setPositiveButton("delete") { _, _ -> longPressCallback.invoke(data.id) }
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog?.dismiss()
                }
                .create().show()
            true
        }
    }

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<ShiftObject>() {
            override fun areItemsTheSame(oldItem: ShiftObject, newItem: ShiftObject): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ShiftObject, newItem: ShiftObject): Boolean {
                return oldItem == newItem
            }
        }
    }
}