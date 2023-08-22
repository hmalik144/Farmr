package com.appttude.h_mal.farmr

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.appttude.h_mal.farmr.data.ShiftsContract.ShiftsEntry
import java.util.Calendar

class FragmentAddItem : Fragment(), LoaderManager.LoaderCallbacks<Cursor?> {
    lateinit var activity: MainActivity

    private var mCurrentProductUri: Uri? = null
    private var mRadioButtonOne: RadioButton? = null
    private var mRadioButtonTwo: RadioButton? = null
    private var mLocationEditText: EditText? = null
    private var mDateEditText: EditText? = null
    private var mDurationTextView: TextView? = null
    private var mTimeInEditText: EditText? = null
    private var mTimeOutEditText: EditText? = null
    private var mBreakEditText: EditText? = null
    private var mUnitEditText: EditText? = null
    private var mPayRateEditText: EditText? = null
    private var mTotalPayTextView: TextView? = null
    private var hourlyDataView: LinearLayout? = null
    private var unitsHolder: LinearLayout? = null
    private var durationHolder: LinearLayout? = null
    private var wholeView: LinearLayout? = null
    private var scrollView: ScrollView? = null
    private var progressBarAI: ProgressBar? = null
    private var mBreaks = 0
    private var mDay = 0
    private var mMonth = 0
    private var mYear = 0
    private var mHoursIn = 0
    private var mMinutesIn = 0
    private var mHoursOut = 0
    private var mMinutesOut = 0
    private var mUnits = 0f
    private var mPayRate = 0f
    private var mType: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_add_item, container, false)
        setHasOptionsMenu(true)
        activity = (requireActivity() as MainActivity)

        progressBarAI = rootView.findViewById<View>(R.id.pd_ai) as ProgressBar
        scrollView = rootView.findViewById<View>(R.id.total_view) as ScrollView
        mRadioGroup = rootView.findViewById<View>(R.id.rg) as RadioGroup
        mRadioButtonOne = rootView.findViewById<View>(R.id.hourly) as RadioButton
        mRadioButtonTwo = rootView.findViewById<View>(R.id.piecerate) as RadioButton
        mLocationEditText = rootView.findViewById<View>(R.id.locationEditText) as EditText
        mDateEditText = rootView.findViewById<View>(R.id.dateEditText) as EditText
        mTimeInEditText = rootView.findViewById<View>(R.id.timeInEditText) as EditText
        mBreakEditText = rootView.findViewById<View>(R.id.breakEditText) as EditText
        mTimeOutEditText = rootView.findViewById<View>(R.id.timeOutEditText) as EditText
        mDurationTextView = rootView.findViewById<View>(R.id.ShiftDuration) as TextView
        mUnitEditText = rootView.findViewById<View>(R.id.unitET) as EditText
        mPayRateEditText = rootView.findViewById<View>(R.id.payrateET) as EditText
        mTotalPayTextView = rootView.findViewById<View>(R.id.totalpayval) as TextView
        hourlyDataView = rootView.findViewById<View>(R.id.hourly_data_holder) as LinearLayout
        unitsHolder = rootView.findViewById<View>(R.id.units_holder) as LinearLayout
        durationHolder = rootView.findViewById<View>(R.id.duration_holder) as LinearLayout
        wholeView = rootView.findViewById<View>(R.id.whole_view) as LinearLayout
        mPayRate = 0.0f
        mUnits = 0.0f
        val b = arguments
        if (b != null) {
            mCurrentProductUri = Uri.parse(b.getString("uri"))
        }
        if (mCurrentProductUri == null) {
            activity.setActionBarTitle(getString(R.string.add_item_title))
            wholeView!!.visibility = View.GONE
        } else {
            activity.setActionBarTitle(getString(R.string.edit_item_title))
            loaderManager.initLoader(EXISTING_PRODUCT_LOADER, null, this)
        }
        mBreakEditText!!.hint = "insert break in minutes"
        mRadioGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            if (mRadioButtonOne!!.isChecked) {
                mType = mRadioButtonOne!!.text.toString()
                wholeView!!.visibility = View.VISIBLE
                unitsHolder!!.visibility = View.GONE
                hourlyDataView!!.visibility = View.VISIBLE
                durationHolder!!.visibility = View.VISIBLE
            } else if (mRadioButtonTwo!!.isChecked) {
                mType = mRadioButtonTwo!!.text.toString()
                wholeView!!.visibility = View.VISIBLE
                unitsHolder!!.visibility = View.VISIBLE
                hourlyDataView!!.visibility = View.GONE
                durationHolder!!.visibility = View.GONE
            }
        })
        mDateEditText!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                //To show current date in the datepicker
                if (TextUtils.isEmpty(mDateEditText!!.text.toString().trim { it <= ' ' })) {
                    val mcurrentDate = Calendar.getInstance()
                    mYear = mcurrentDate[Calendar.YEAR]
                    mMonth = mcurrentDate[Calendar.MONTH]
                    mDay = mcurrentDate[Calendar.DAY_OF_MONTH]
                } else {
                    val dateString = mDateEditText!!.text.toString().trim { it <= ' ' }
                    mYear = dateString.substring(0, 4).toInt()
                    mMonth = dateString.substring(5, 7).toInt()
                    if (mMonth == 1) {
                        mMonth = 0
                    } else {
                        mMonth = mMonth - 1
                    }
                    mDay = dateString.substring(8).toInt()
                }
                val mDatePicker = DatePickerDialog((context)!!, object : OnDateSetListener {
                    override fun onDateSet(datepicker: DatePicker, selectedyear: Int, selectedmonth: Int, selectedday: Int) {
                        var selectedmonth = selectedmonth
                        mDateEditText!!.setText(
                                (selectedyear.toString() + "-"
                                        + String.format("%02d", (selectedmonth + 1.also { selectedmonth = it })) + "-"
                                        + String.format("%02d", selectedday))
                        )
                        setDate(selectedyear, selectedmonth, selectedday)
                    }
                }, mYear, mMonth, mDay)
                mDatePicker.setTitle("Select date")
                mDatePicker.show()
            }
        })
        mTimeInEditText!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if ((mTimeInEditText!!.text.toString() == "")) {
                    val mcurrentTime = Calendar.getInstance()
                    mHoursIn = mcurrentTime[Calendar.HOUR_OF_DAY]
                    mMinutesIn = mcurrentTime[Calendar.MINUTE]
                } else {
                    mHoursIn = (mTimeInEditText!!.text.toString().subSequence(0, 2)).toString().toInt()
                    mMinutesIn = (mTimeInEditText!!.text.toString().subSequence(3, 5)).toString().toInt()
                }
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(context, object : OnTimeSetListener {
                    override fun onTimeSet(timePicker: TimePicker, selectedHour: Int, selectedMinute: Int) {
                        val ddTime = String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute)
                        setTime(selectedMinute, selectedHour)
                        mTimeInEditText!!.setText(ddTime)
                    }
                }, mHoursIn, mMinutesIn, true) //Yes 24 hour time
                mTimePicker.setTitle("Select Time")
                mTimePicker.show()
            }
        })
        mTimeOutEditText!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if ((mTimeOutEditText!!.text.toString() == "")) {
                    val mcurrentTime = Calendar.getInstance()
                    mHoursOut = mcurrentTime[Calendar.HOUR_OF_DAY]
                    mMinutesOut = mcurrentTime[Calendar.MINUTE]
                } else {
                    mHoursOut = (mTimeOutEditText!!.text.toString().subSequence(0, 2)).toString().toInt()
                    mMinutesOut = (mTimeOutEditText!!.text.toString().subSequence(3, 5)).toString().toInt()
                }
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(context, object : OnTimeSetListener {
                    override fun onTimeSet(timePicker: TimePicker, selectedHour: Int, selectedMinute: Int) {
                        val ddTime = String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute)
                        setTime2(selectedMinute, selectedHour)
                        mTimeOutEditText!!.setText(ddTime)
                    }
                }, mHoursOut, mMinutesOut, true) //Yes 24 hour time
                mTimePicker.setTitle("Select Time")
                mTimePicker.show()
            }
        })
        mTimeInEditText!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {
                setDuration()
                calculateTotalPay()
            }
        })
        mTimeOutEditText!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {
                setDuration()
                calculateTotalPay()
            }
        })
        mBreakEditText!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}
            override fun afterTextChanged(s: Editable) {
                setDuration()
                calculateTotalPay()
            }
        })
        mUnitEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
//                if(mRadioButtonTwo.isChecked()) {
//                    mUnits = 0.0f;
//                    if (!mUnitEditText.getText().toString().equals("")){
//                        mUnits = Float.parseFloat(mUnitEditText.getText().toString());
//                    }
//                    mPayRate = 0.0f;
//                    if (!mPayRateEditText.getText().toString().equals("")){
//                        mPayRate = Float.parseFloat(mPayRateEditText.getText().toString());
//                    }
//                    Float total = mPayRate * mUnits;
//                    mTotalPayTextView.setText(String.valueOf(total));
//                }
                calculateTotalPay()
            }
        })
        mPayRateEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                calculateTotalPay()
            }
        })
        val SubmitProduct = rootView.findViewById<View>(R.id.submit) as Button
        SubmitProduct.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                saveProduct()
            }
        })
        return rootView
    }

    private fun calculateTotalPay() {
        var total = 0.0f
        mPayRate = 0.0f
        if (mRadioButtonTwo!!.isChecked) {
            mUnits = 0.0f
            if (mUnitEditText!!.text.toString() != "") {
                mUnits = mUnitEditText!!.text.toString().toFloat()
            }
            if (mPayRateEditText!!.text.toString() != "") {
                mPayRate = mPayRateEditText!!.text.toString().toFloat()
            }
            total = mPayRate * mUnits
            mTotalPayTextView!!.text = total.toString()
        } else if (mRadioButtonOne!!.isChecked) {
            if (mPayRateEditText!!.text.toString() != "") {
                mPayRate = mPayRateEditText!!.text.toString().toFloat()
                total = mPayRate * calculateDuration(mHoursIn, mMinutesIn, mHoursOut, mMinutesOut, mBreaks)
            }
        }
        mTotalPayTextView!!.text = String.format("%.2f", total)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun saveProduct() {
        val typeString = mType
        val descriptionString = mLocationEditText!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(descriptionString)) {
            Toast.makeText(context, "please insert Location", Toast.LENGTH_SHORT).show()
            return
        }
        val dateString = mDateEditText!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(dateString)) {
            Toast.makeText(context, "please insert Date", Toast.LENGTH_SHORT).show()
            return
        }
        var timeInString: String = ""
        var timeOutString: String = ""
        var breaks = 0
        var units = 0f
        var duration = 0f
        var payRate = 0f
        val payRateString = mPayRateEditText!!.text.toString().trim { it <= ' ' }
        if (!TextUtils.isEmpty(payRateString)) {
            payRate = payRateString.toFloat()
        }
        var totalPay = 0f
        if ((typeString == "Hourly")) {
            timeInString = mTimeInEditText!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(timeInString)) {
                Toast.makeText(context, "please insert Time in", Toast.LENGTH_SHORT).show()
                return
            }
            timeOutString = mTimeOutEditText!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(timeOutString)) {
                Toast.makeText(context, "please insert Time out", Toast.LENGTH_SHORT).show()
                return
            }
            val breakMins = mBreakEditText!!.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(breakMins)) {
                breaks = breakMins.toInt()
                if ((breaks.toFloat() / 60) > calculateDurationWithoutBreak(mHoursIn, mMinutesIn, mHoursOut, mMinutesOut)) {
                    Toast.makeText(context, "Break larger than duration", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            duration = calculateDuration(mHoursIn, mMinutesIn, mHoursOut, mMinutesOut, breaks)
            totalPay = duration * payRate
        } else if ((typeString == "Piece Rate")) {
            val unitsString = mUnitEditText!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(unitsString) || unitsString.toFloat() <= 0) {
                Toast.makeText(context, "Insert Units", Toast.LENGTH_SHORT).show()
                return
            } else {
                units = unitsString.toFloat()
            }
            duration = 0f
            totalPay = units * payRate
        }
        val values = ContentValues()
        values.put(ShiftsEntry.COLUMN_SHIFT_TYPE, typeString)
        values.put(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION, descriptionString)
        values.put(ShiftsEntry.COLUMN_SHIFT_DATE, dateString)
        values.put(ShiftsEntry.COLUMN_SHIFT_TIME_IN, timeInString)
        values.put(ShiftsEntry.COLUMN_SHIFT_TIME_OUT, timeOutString)
        values.put(ShiftsEntry.COLUMN_SHIFT_DURATION, duration)
        values.put(ShiftsEntry.COLUMN_SHIFT_BREAK, breaks)
        values.put(ShiftsEntry.COLUMN_SHIFT_UNIT, units)
        values.put(ShiftsEntry.COLUMN_SHIFT_PAYRATE, payRate)
        values.put(ShiftsEntry.COLUMN_SHIFT_TOTALPAY, totalPay)
        if (mCurrentProductUri == null) {
            val newUri = activity.contentResolver.insert(ShiftsEntry.CONTENT_URI, values)
            if (newUri == null) {
                Toast.makeText(context, getString(R.string.insert_item_failed),
                        Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.insert_item_successful),
                        Toast.LENGTH_SHORT).show()
            }
        } else {
            val rowsAffected = activity.contentResolver.update(mCurrentProductUri!!, values, null, null)
            if (rowsAffected == 0) {
                Toast.makeText(context, getString(R.string.update_item_failed),
                        Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.update_item_successful),
                        Toast.LENGTH_SHORT).show()
            }
        }
        (activity.fragmentManager)!!.popBackStack("main", 0)
    }

    private fun setDuration() {
        val mcurrentTime = Calendar.getInstance()
        mBreaks = 0
        if (mBreakEditText!!.text.toString() != "") {
            mBreaks = mBreakEditText!!.text.toString().toInt()
        }
        if ((mTimeOutEditText!!.text.toString() == "")) {
            mHoursOut = mcurrentTime[Calendar.HOUR_OF_DAY]
            mMinutesOut = mcurrentTime[Calendar.MINUTE]
        } else {
            mHoursOut = (mTimeOutEditText!!.text.toString().subSequence(0, 2)).toString().toInt()
            mMinutesOut = (mTimeOutEditText!!.text.toString().subSequence(3, 5)).toString().toInt()
        }
        if ((mTimeInEditText!!.text.toString() == "")) {
            mHoursIn = mcurrentTime[Calendar.HOUR_OF_DAY]
            mMinutesIn = mcurrentTime[Calendar.MINUTE]
        } else {
            mHoursIn = (mTimeInEditText!!.text.toString().subSequence(0, 2)).toString().toInt()
            mMinutesIn = (mTimeInEditText!!.text.toString().subSequence(3, 5)).toString().toInt()
        }
        mDurationTextView!!.text = calculateDuration(mHoursIn, mMinutesIn, mHoursOut, mMinutesOut, mBreaks).toString() + " hours"
    }

    private fun setDate(year: Int, month: Int, day: Int) {
        mYear = year
        mMonth = month
        mDay = day
    }

    private fun setTime(minutes: Int, hours: Int) {
        mMinutesIn = minutes
        mHoursIn = hours
    }

    private fun setTime2(minutes: Int, hours: Int) {
        mMinutesOut = minutes
        mHoursOut = hours
    }

    private fun calculateDuration(hoursIn: Int, minutesIn: Int, hoursOut: Int, minutesOut: Int, breaks: Int): Float {
        val duration: Float
        if (hoursOut > hoursIn) {
            duration = ((hoursOut.toFloat() + (minutesOut.toFloat() / 60)) - (hoursIn.toFloat() + (minutesIn.toFloat() / 60))) - (breaks.toFloat() / 60)
        } else {
            duration = (((hoursOut.toFloat() + (minutesOut.toFloat() / 60)) - (hoursIn.toFloat() + (minutesIn.toFloat() / 60))) - (breaks.toFloat() / 60) + 24)
        }
        val s = String.format("%.2f", duration)
        return s.toFloat()
    }

    private fun calculateDurationWithoutBreak(hoursIn: Int, minutesIn: Int, hoursOut: Int, minutesOut: Int): Float {
        val duration: Float
        if (hoursOut > hoursIn) {
            duration = ((hoursOut.toFloat() + (minutesOut.toFloat() / 60)) - (hoursIn.toFloat() + (minutesIn.toFloat() / 60)))
        } else {
            duration = (((hoursOut.toFloat() + (minutesOut.toFloat() / 60)) - (hoursIn.toFloat() + (minutesIn.toFloat() / 60))) + 24)
        }
        val s = String.format("%.2f", duration)
        return s.toFloat()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        progressBarAI!!.visibility = View.VISIBLE
        scrollView!!.visibility = View.GONE
        val projection = arrayOf<String?>(
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
        return CursorLoader((context)!!, (mCurrentProductUri)!!,
                projection, null, null, null)
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {}

    override fun onLoadFinished(loader: Loader<Cursor?>, cursor: Cursor?) {
        progressBarAI!!.visibility = View.GONE
        scrollView!!.visibility = View.VISIBLE
        if (cursor == null || cursor.count < 1) {
            return
        }
        if (cursor.moveToFirst()) {
            val descriptionColumnIndex = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION)
            val dateColumnIndex = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_DATE)
            val timeInColumnIndex = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_TIME_IN)
            val timeOutColumnIndex = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_TIME_OUT)
            val breakColumnIndex = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_BREAK)
            val durationColumnIndex = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_DURATION)
            val typeColumnIndex = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_TYPE)
            val unitColumnIndex = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_UNIT)
            val payrateColumnIndex = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_PAYRATE)
            val totalPayColumnIndex = cursor.getColumnIndex(ShiftsEntry.COLUMN_SHIFT_TOTALPAY)
            val type = cursor.getString(typeColumnIndex)
            val description = cursor.getString(descriptionColumnIndex)
            val date = cursor.getString(dateColumnIndex)
            val timeIn = cursor.getString(timeInColumnIndex)
            val timeOut = cursor.getString(timeOutColumnIndex)
            val breaks = cursor.getInt(breakColumnIndex)
            val duration = cursor.getFloat(durationColumnIndex)
            val unit = cursor.getFloat(unitColumnIndex)
            val payrate = cursor.getFloat(payrateColumnIndex)
            val totalPay = cursor.getFloat(totalPayColumnIndex)
            mLocationEditText!!.setText(description)
            mDateEditText!!.setText(date)
            if ((type == "Hourly") || (type == "hourly")) {
                mRadioButtonOne!!.isChecked = true
                mRadioButtonTwo!!.isChecked = false
                mTimeInEditText!!.setText(timeIn)
                mTimeOutEditText!!.setText(timeOut)
                mBreakEditText!!.setText(Integer.toString(breaks))
                mDurationTextView!!.text = String.format("%.2f", duration) + " Hours"
            } else if ((type == "Piece Rate")) {
                mRadioButtonOne!!.isChecked = false
                mRadioButtonTwo!!.isChecked = true
                mUnitEditText!!.setText(java.lang.Float.toString(unit))
            }
            mPayRateEditText!!.setText(String.format("%.2f", payrate))
            mTotalPayTextView!!.text = String.format("%.2f", totalPay)
        }
    }

    companion object {
        private val EXISTING_PRODUCT_LOADER = 0
        var mRadioGroup: RadioGroup? = null
    }
}