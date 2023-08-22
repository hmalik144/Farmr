package com.appttude.h_mal.farmr

import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.appttude.h_mal.farmr.data.ShiftProvider
import com.appttude.h_mal.farmr.data.ShiftsContract.ShiftsEntry
import kotlin.math.floor

/**
 * Created by h_mal on 26/12/2017.
 */
class ShiftsCursorAdapter constructor(private val activity: MainActivity, c: Cursor?) : CursorAdapter(activity, c, 0) {
    private var mContext: Context? = null
    var shiftProvider: ShiftProvider? = null
    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.list_item_1, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        mContext = context
        val descriptionTextView: TextView = view.findViewById<View>(R.id.location) as TextView
        val dateTextView: TextView = view.findViewById<View>(R.id.date) as TextView
        val totalPay: TextView = view.findViewById<View>(R.id.total_pay) as TextView
        val hoursView: TextView = view.findViewById<View>(R.id.hours) as TextView
        val h: TextView = view.findViewById<View>(R.id.h) as TextView
        val minutesView: TextView = view.findViewById<View>(R.id.minutes) as TextView
        val m: TextView = view.findViewById<View>(R.id.m) as TextView
        val editView: ImageView = view.findViewById<View>(R.id.imageView) as ImageView
        h.text = "h"
        m.text = "m"
        val typeColumnIndex: String = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TYPE))
        val descriptionColumnIndex: String = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION))
        val dateColumnIndex: String = cursor.getString(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DATE))
        val durationColumnIndex: Float = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_DURATION))
        val unitsColumnIndex: Float = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_UNIT))
        val totalpayColumnIndex: Float = cursor.getFloat(cursor.getColumnIndexOrThrow(ShiftsEntry.COLUMN_SHIFT_TOTALPAY))
        descriptionTextView.text = descriptionColumnIndex
        dateTextView.text = newDate(dateColumnIndex)
        totalPay.text = String.format("%.2f", totalpayColumnIndex)
        if ((typeColumnIndex == "Piece Rate") && durationColumnIndex == 0f) {
            hoursView.text = unitsColumnIndex.toString()
            h.text = ""
            minutesView.text = ""
            m.text = "pcs"
        } else  //            if(typeColumnIndex.equals("Hourly") || typeColumnIndex.equals("hourly"))
        {
            hoursView.text = timeValues(durationColumnIndex).get(0)
            minutesView.text = timeValues(durationColumnIndex).get(1)
        }
        val ID: Long = cursor.getLong(cursor.getColumnIndexOrThrow(ShiftsEntry._ID))
        val currentProductUri: Uri = ContentUris.withAppendedId(ShiftsEntry.CONTENT_URI, ID)
        val b: Bundle = Bundle()
        b.putString("uri", currentProductUri.toString())
        view.setOnClickListener { //                activity.clickOnViewItem(ID);
            val fragmentTransaction: FragmentTransaction = (activity.fragmentManager)!!.beginTransaction()
            val fragment2: FurtherInfoFragment = FurtherInfoFragment()
            fragment2.arguments = b
            fragmentTransaction.replace(R.id.container, fragment2).addToBackStack("furtherinfo").commit()
        }
        editView.setOnClickListener {
            val fragmentTransaction: FragmentTransaction = (activity.fragmentManager)!!.beginTransaction()
            val fragment3: FragmentAddItem = FragmentAddItem()
            fragment3.arguments = b
            fragmentTransaction.replace(R.id.container, fragment3).addToBackStack("additem").commit()
        }
        view.setOnLongClickListener {
            println("long click: $ID")
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setMessage("Are you sure you want to delete")
            builder.setPositiveButton("delete") { dialog, id -> deleteProduct(ID) }
            builder.setNegativeButton("cancel") { dialog, id ->
                dialog?.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
            true
        }
    }

    private fun deleteProduct(id: Long) {
        val args: Array<String> = arrayOf(id.toString())
        //        String whereClause = String.format(ShiftsEntry._ID + " in (%s)", new Object[] { TextUtils.join(",", Collections.nCopies(args.length, "?")) }); //for deleting multiple lines
        mContext!!.contentResolver.delete(ShiftsEntry.CONTENT_URI, ShiftsEntry._ID + "=?", args)
    }

    private fun newDate(dateString: String): String {
        var returnString: String? = "01/01/2010"
        val year: String = dateString.substring(0, 4)
        val month: String = dateString.substring(5, 7)
        val day: String = dateString.substring(8)
        returnString = "$day-$month-$year"
        return returnString
    }

    companion object {
        fun timeValues(duration: Float): Array<String> {
            val hours: Int = floor(duration.toDouble()).toInt()
            val minutes: Int = ((duration - hours) * 60).toInt()
            val hoursString: String = hours.toString() + ""
            val minutesString: String = String.format("%02d", minutes)
            return arrayOf(hoursString, minutesString)
        }
    }
}