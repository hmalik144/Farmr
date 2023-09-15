package com.appttude.h_mal.farmr.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseListAdapter
import com.appttude.h_mal.farmr.base.ChildFragment
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.utils.formatToTwoDpString
import com.appttude.h_mal.farmr.viewmodel.MainViewModel


const val PIECE_ITEM = 500
const val HOURLY_ITEM = 501

class ShiftListAdapter(
    private val fragment: ChildFragment<*>,
    emptyView: View?,
    private val viewModel: MainViewModel
) : BaseListAdapter<ShiftObject>(diffCallBack, R.layout.list_item_1, emptyView) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CurrentViewHolder, position: Int) {
        val view = holder.itemView
        val data = getItem(position)

        val descriptionTextView: TextView = view.findViewById(R.id.location)
        val dateTextView: TextView = view.findViewById(R.id.date)
        val totalPay: TextView = view.findViewById(R.id.total_pay)
        val editView: ImageView = view.findViewById(R.id.imageView)

        when (getItemViewType(position)) {
            HOURLY_ITEM -> {
                val hoursView: TextView = view.findViewById(R.id.hours)
                val minutesView: TextView = view.findViewById(R.id.minutes)

                val time = data.getHoursMinutesPairFromDuration()
                hoursView.text = time.first
                minutesView.text = if (time.second.length == 1) "0${time.second}" else time.second
            }

            PIECE_ITEM -> {
                val unitsView: TextView = view.findViewById(R.id.pieces)
                val unitsText: String = data.units.toString()

                unitsView.text = unitsText
            }
        }
        descriptionTextView.text = data.description
        dateTextView.text = data.date
        totalPay.text = data.totalPay.formatToTwoDpString()

        view.setOnClickListener {
            // Navigate to further info
            val nav = FragmentMainDirections.mainToFurtherInfo(data.id)
            fragment.navigateParent(nav)
        }
        editView.setOnClickListener {
            //creating a popup menu
            val popup = PopupMenu(it.context, it)
            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu)

            //adding click listener
            popup.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.update -> {
                        // Navigate to edit
                        val nav = FragmentMainDirections.mainToAddItem(data.id)
                        fragment.navigateParent(nav)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.delete -> {
                        AlertDialog.Builder(it.context)
                            .setMessage("Are you sure you want to delete")
                            .setPositiveButton("delete") { _, _ -> viewModel.deleteShift(data.id) }
                            .setNegativeButton("cancel") { dialog, _ ->
                                dialog?.dismiss()
                            }
                            .create().show()
                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val typeString = getItem(position).type
        return when (ShiftType.getEnumByType(typeString)) {
            ShiftType.HOURLY -> HOURLY_ITEM
            ShiftType.PIECE -> PIECE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentViewHolder {
        val layoutId = when (viewType) {
            HOURLY_ITEM -> R.layout.list_cell_hourly
            PIECE_ITEM -> R.layout.list_cell_piece
            else -> {
                return super.onCreateViewHolder(parent, viewType)
            }
        }
        val currentView = LayoutInflater
            .from(parent.context)
            .inflate(layoutId, parent, false)
        return CurrentViewHolder(currentView)
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