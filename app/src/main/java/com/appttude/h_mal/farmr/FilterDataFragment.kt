package com.appttude.h_mal.farmr

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.appttude.h_mal.farmr.data.ShiftsContract.ShiftsEntry
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class FilterDataFragment : Fragment() {
    private val spinnerList: Array<String> = arrayOf("", "Hourly", "Piece Rate")
    private val listArgs: MutableList<String> = ArrayList()
    private var LocationET: EditText? = null
    private var dateFromET: EditText? = null
    private var dateToET: EditText? = null
    private var typeSpinner: Spinner? = null
    lateinit var mcurrentDate: Calendar
    private lateinit var activity: MainActivity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val activity = (activity)

        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_filter_data, container, false)
        activity.setActionBarTitle(getString(R.string.title_activity_filter_data))
        mcurrentDate = Calendar.getInstance()
        LocationET = rootView.findViewById<View>(R.id.filterLocationEditText) as EditText?
        dateFromET = rootView.findViewById<View>(R.id.fromdateInEditText) as EditText?
        dateToET = rootView.findViewById<View>(R.id.filterDateOutEditText) as EditText?
        typeSpinner = rootView.findViewById<View>(R.id.TypeFilterEditText) as Spinner?


        if (activity.selection != null && activity.selection!!.contains(" AND " + ShiftsEntry.COLUMN_SHIFT_DESCRIPTION + " LIKE ?")) {
            var str: String = activity.args!!.get(2)
            str = str.replace("%", "")
            LocationET!!.setText(str)
        }
        if (activity.selection != null && !(activity.args!!.get(0) == "2000-01-01")) {
            dateFromET!!.setText(activity.args!!.get(0))
        }
        if ((activity.selection != null) && (activity.args != null) && !(activity.args!![1] == (mcurrentDate.get(Calendar.YEAR).toString() + "-"
                        + String.format("%02d", (mcurrentDate.get(Calendar.MONTH) + 1)) + "-"
                        + String.format("%02d", mcurrentDate.get(Calendar.DAY_OF_MONTH))).toString())) {
            dateToET!!.setText(activity.args!![1])
        }
        dateFromET!!.setOnClickListener { //To show current date in the datepicker
            var mYear: Int = mcurrentDate.get(Calendar.YEAR)
            var mMonth: Int = mcurrentDate.get(Calendar.MONTH)
            var mDay: Int = mcurrentDate.get(Calendar.DAY_OF_MONTH)
            if (!(dateFromET!!.text.toString() == "")) {
                val dateString: String = dateFromET!!.text.toString().trim()
                mYear = dateString.substring(0, 4).toInt()
                mMonth = dateString.substring(5, 7).toInt()
                if (mMonth == 1) {
                    mMonth = 0
                } else {
                    mMonth = mMonth - 1
                }
                mDay = dateString.substring(8).toInt()
            }

            val mDatePicker = DatePickerDialog((context)!!, { datepicker, selectedyear, selectedmonth, selectedday ->
                val input = MessageFormat.format("{0}-{1}-{2}", selectedyear, String.format("%02d", (selectedmonth + 1)), String.format("%02d", selectedday))
                dateFromET!!.setText(input)
            }, mYear, mMonth, mDay)
            mDatePicker.setTitle("Select date")
            mDatePicker.show()
        }
        dateToET!!.setOnClickListener { //To show current date in the datepicker
            val mcurrentDate: Calendar = Calendar.getInstance()
            var mYear: Int = mcurrentDate.get(Calendar.YEAR)
            var mMonth: Int = mcurrentDate.get(Calendar.MONTH)
            var mDay: Int = mcurrentDate.get(Calendar.DAY_OF_MONTH)
            if (!(dateToET!!.text.toString() == "")) {
                val dateString: String = dateToET!!.text.toString().trim({ it <= ' ' })
                mYear = dateString.substring(0, 4).toInt()
                mMonth = dateString.substring(5, 7).toInt()
                if (mMonth == 1) {
                    mMonth = 0
                } else {
                    mMonth -= 1
                }
                mDay = dateString.substring(8).toInt()
            }
            val mDatePicker = DatePickerDialog((context)!!, { datepicker, selectedyear, selectedmonth, selectedday ->
                val input = MessageFormat.format("{0}-{1}-{2}", selectedyear, String.format("%02d", (selectedmonth + 1)), String.format("%02d", selectedday))
                dateToET!!.setText(input)
            }, mYear, mMonth, mDay)
            mDatePicker.setTitle("Select date")
            mDatePicker.show()
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter((context)!!, android.R.layout.simple_spinner_dropdown_item, spinnerList)
        typeSpinner!!.adapter = adapter
        if (activity.selection != null && activity.selection!!.contains(" AND " + ShiftsEntry.COLUMN_SHIFT_TYPE + " IS ?")) {
            val spinnerPosition: Int = adapter.getPosition(activity.args!!.get(activity.args!!.size - 1))
            typeSpinner!!.setSelection(spinnerPosition)
        }
        val submit: Button = rootView.findViewById<View>(R.id.submitFiltered) as Button
        submit.setOnClickListener {
            BuildQuery()
            activity.args = listArgs.toTypedArray<String>()
            FragmentMain.NEW_LOADER = 1
            activity.fragmentManager!!.popBackStack()
        }
        return rootView
    }

    private fun BuildQuery() {
        val dateQuery: String = ShiftsEntry.COLUMN_SHIFT_DATE + " BETWEEN ? AND ?"
        val descQuery: String = " AND " + ShiftsEntry.COLUMN_SHIFT_DESCRIPTION + " LIKE ?"
        val typeQuery: String = " AND " + ShiftsEntry.COLUMN_SHIFT_TYPE + " IS ?"
        var dateFrom = "2000-01-01"
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        var dateTo: String = df.format(c)
        if (!TextUtils.isEmpty(dateFromET!!.text.toString().trim())) {
            dateFrom = dateFromET!!.text.toString().trim()
        }
        if (!TextUtils.isEmpty(dateToET!!.text.toString().trim())) {
            dateTo = dateToET!!.text.toString().trim()
        }
        activity.selection = dateQuery
        listArgs.add(dateFrom)
        listArgs.add(dateTo)
        if (!TextUtils.isEmpty(LocationET!!.text.toString().trim())) {
            activity.selection = activity.selection + descQuery
            listArgs.add("%" + LocationET!!.text.toString().trim() + "%")
        }
        if (!(typeSpinner!!.selectedItem.toString() == "")) {
            activity.selection = activity.selection + typeQuery
            listArgs.add(typeSpinner!!.selectedItem.toString())
        }
    }
}