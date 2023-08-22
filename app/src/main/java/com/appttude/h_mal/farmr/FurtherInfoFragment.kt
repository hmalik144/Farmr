package com.appttude.h_mal.farmr

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.appttude.h_mal.farmr.data.ShiftsContract.ShiftsEntry

class FurtherInfoFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var typeTV: TextView? = null
    private var descriptionTV: TextView? = null
    private var dateTV: TextView? = null
    private var times: TextView? = null
    private var breakTV: TextView? = null
    private var durationTV: TextView? = null
    private var unitsTV: TextView? = null
    private var payRateTV: TextView? = null
    private var totalPayTV: TextView? = null
    private var hourlyDetailHolder: LinearLayout? = null
    private var unitsHolder: LinearLayout? = null
    private var wholeView: LinearLayout? = null
    private var progressBarFI: ProgressBar? = null
    private var editButton: Button? = null
    private var CurrentUri: Uri? = null

    lateinit var activity: MainActivity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                     savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_futher_info, container, false)
        setHasOptionsMenu(true)
        activity = (requireActivity() as MainActivity)
        activity.setActionBarTitle(getString(R.string.further_info_title))

        progressBarFI = rootView.findViewById<View>(R.id.progressBar_info) as ProgressBar?
        wholeView = rootView.findViewById<View>(R.id.further_info_view) as LinearLayout?
        typeTV = rootView.findViewById<View>(R.id.details_shift) as TextView?
        descriptionTV = rootView.findViewById<View>(R.id.details_desc) as TextView?
        dateTV = rootView.findViewById<View>(R.id.details_date) as TextView?
        times = rootView.findViewById<View>(R.id.details_time) as TextView?
        breakTV = rootView.findViewById<View>(R.id.details_breaks) as TextView?
        durationTV = rootView.findViewById<View>(R.id.details_duration) as TextView?
        unitsTV = rootView.findViewById<View>(R.id.details_units) as TextView?
        payRateTV = rootView.findViewById<View>(R.id.details_pay_rate) as TextView?
        totalPayTV = rootView.findViewById<View>(R.id.details_totalpay) as TextView?
        editButton = rootView.findViewById<View>(R.id.details_edit) as Button?
        hourlyDetailHolder = rootView.findViewById<View>(R.id.details_hourly_details) as LinearLayout?
        unitsHolder = rootView.findViewById<View>(R.id.details_units_holder) as LinearLayout?
        val b: Bundle? = arguments
        CurrentUri = Uri.parse(b!!.getString("uri"))
        loaderManager.initLoader(DEFAULT_LOADER, null, this)
        editButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val fragmentTransaction: FragmentTransaction = (activity.fragmentManager)!!.beginTransaction()
                val fragment: Fragment = FragmentAddItem()
                fragment.arguments = b
                fragmentTransaction.replace(R.id.container, fragment).addToBackStack("additem").commit()
            }
        })
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        progressBarFI!!.visibility = View.VISIBLE
        wholeView!!.visibility = View.GONE
        val projection: Array<String?> = arrayOf(
                ShiftsEntry._ID,
                ShiftsEntry.COLUMN_SHIFT_DESCRIPTION,
                ShiftsEntry.COLUMN_SHIFT_DATE,
                ShiftsEntry.COLUMN_SHIFT_TIME_IN,
                ShiftsEntry.COLUMN_SHIFT_TIME_OUT,
                ShiftsEntry.COLUMN_SHIFT_BREAK,
                ShiftsEntry.COLUMN_SHIFT_DURATION,
                ShiftsEntry.COLUMN_SHIFT_TYPE,
                ShiftsEntry.COLUMN_SHIFT_PAYRATE,
                ShiftsEntry.COLUMN_SHIFT_UNIT,
                ShiftsEntry.COLUMN_SHIFT_TOTALPAY)
        return CursorLoader((context)!!, (CurrentUri)!!,
                projection, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        progressBarFI!!.visibility = View.GONE
        wholeView!!.visibility = View.VISIBLE
        if (cursor == null || cursor.count < 1) {
            return
        }
        if (cursor.moveToFirst()) {
            val descriptionColumnIndex: Int = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION)
            val dateColumnIndex: Int = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_DATE)
            val timeInColumnIndex: Int = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_TIME_IN)
            val timeOutColumnIndex: Int = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_TIME_OUT)
            val breakColumnIndex: Int = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_BREAK)
            val durationColumnIndex: Int = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_DURATION)
            val typeColumnIndex: Int = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_TYPE)
            val unitColumnIndex: Int = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_UNIT)
            val payrateColumnIndex: Int = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_PAYRATE)
            val totalPayColumnIndex: Int = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_TOTALPAY)
            val type: String = cursor.getString(typeColumnIndex)
            val description: String = cursor.getString(descriptionColumnIndex)
            val date: String = cursor.getString(dateColumnIndex)
            val timeIn: String = cursor.getString(timeInColumnIndex)
            val timeOut: String = cursor.getString(timeOutColumnIndex)
            val breaks: Int = cursor.getInt(breakColumnIndex)
            val duration: Float = cursor.getFloat(durationColumnIndex)
            val unit: Float = cursor.getFloat(unitColumnIndex)
            val payrate: Float = cursor.getFloat(payrateColumnIndex)
            val totalPay: Float = cursor.getFloat(totalPayColumnIndex)
            var durationString: String = ShiftsCursorAdapter.Companion.timeValues(duration).get(0) + " Hours " + ShiftsCursorAdapter.Companion.timeValues(duration).get(1) + " Minutes "
            if (breaks != 0) {
                durationString = durationString + " (+ " + Integer.toString(breaks) + " minutes break)"
            }
            typeTV!!.text = type
            descriptionTV!!.text = description
            dateTV!!.text = date
            var totalPaid: String? = ""
            val currency: String = "$"
            if ((type == "Hourly")) {
                hourlyDetailHolder!!.visibility = View.VISIBLE
                unitsHolder!!.visibility = View.GONE
                times!!.text = timeIn + " - " + timeOut
                breakTV!!.text = Integer.toString(breaks) + "mins"
                durationTV!!.text = durationString
                totalPaid = (String.format("%.2f", duration) + " Hours @ " + currency + String.format("%.2f", payrate) + " per Hour" + "\n"
                        + "Equals: " + currency + String.format("%.2f", totalPay))
            } else if ((type == "Piece Rate")) {
                hourlyDetailHolder!!.visibility = View.GONE
                unitsHolder!!.visibility = View.VISIBLE
                unitsTV!!.text = String.format("%.2f", unit)
                totalPaid = (String.format("%.2f", unit) + " Units @ " + currency + String.format("%.2f", payrate) + " per Unit" + "\n"
                        + "Equals: " + currency + String.format("%.2f", totalPay))
            }
            payRateTV!!.text = String.format("%.2f", payrate)
            totalPayTV!!.text = totalPaid
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}

    companion object {
        private val DEFAULT_LOADER: Int = 0
    }
}