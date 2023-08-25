package com.appttude.h_mal.farmr.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseRecyclerAdapter
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.utils.ID
import com.appttude.h_mal.farmr.utils.navigateToFragment

class ShiftRecyclerAdapter(
    private val fragment: Fragment,
    private val longPressCallback: (Long) -> Unit
) : BaseRecyclerAdapter<ShiftObject>(
    emptyViewId = R.layout.empty_list_view,
    currentViewId = R.layout.list_item_1
) {
    override fun bindCurrentView(view: View, position: Int, data: ShiftObject) {
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
        val totalPayText: String = data.totalPay.toString()

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
                .setPositiveButton("delete") { dialog, id -> longPressCallback.invoke(data.id) }
                .setNegativeButton("cancel") { dialog, id ->
                    dialog?.dismiss()
                }
                .create().show()
            true
        }
    }

//    override fun getItemId(position: Int): Long {
//        return if (list.isNullOrEmpty()) {
//            RecyclerView.NO_ID
//        } else {
//            list!![position].id
//        }
//
//    }
//
//    override fun setHasStableIds(hasStableIds: Boolean) {
//        super.setHasStableIds(true)
//    }
}