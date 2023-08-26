package com.appttude.h_mal.farmr.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.ui.FragmentAddItem
import java.util.Calendar

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun Context.displayToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.displayToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun ViewGroup.generateView(layoutId: Int): View = LayoutInflater
    .from(context)
    .inflate(layoutId, this, false)

fun Fragment.hideKeyboard() {
    val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun View.triggerAnimation(@AnimRes id: Int, complete: (View) -> Unit) {
    val animation = AnimationUtils.loadAnimation(context, id)
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationEnd(animation: Animation?) = complete(this@triggerAnimation)
        override fun onAnimationStart(a: Animation?) {}
        override fun onAnimationRepeat(a: Animation?) {}
    })
    startAnimation(animation)
}

fun Fragment.navigateToFragment(fragment: Fragment, @IdRes container: Int = R.id.container, name: String = "") {
    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
    fragmentTransaction.replace(container, fragment).addToBackStack(name).commit()
}

fun Fragment.navigateToFragment(fragment: Fragment, @IdRes container: Int = R.id.container, name: String = "", bundle: Bundle) {
    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
    fragmentTransaction.replace(container, fragment.apply { arguments = bundle }).addToBackStack(name).commit()
}

fun Context.createDialog(
    title: String?,
    message: String?,
    displayCancel: Boolean = false,
    displayOk: Boolean = true,
    cancelCallback: DialogInterface.OnClickListener? = null,
    okCallback: DialogInterface.OnClickListener? = null,
) {
    val builder = AlertDialog.Builder(this)
    title?.let { builder.setTitle(it) }
    message?.let { builder.setMessage(it) }
    if (displayCancel) {
        builder.setNegativeButton(android.R.string.cancel, cancelCallback)
    }
    if (displayOk) {
        builder.setPositiveButton(android.R.string.ok, okCallback)
    }

    builder.create().show()
}

fun AppCompatActivity.popBackStack() {
    supportFragmentManager.popBackStack()
}

fun EditText.setTimePicker(onSelected: (String) -> Unit) {
    var mHoursOut: Int
    var mMinutesOut: Int

    setOnClickListener {
        val mCurrentTime by lazy { Calendar.getInstance() }
        if (!text.isNullOrEmpty()) {
            // EditText contains text - lets try set the parse the text
            try {
                val convertedString = convertTimeStringToHourMinutesPair(text.toString())
                mHoursOut = convertedString.first
                mMinutesOut = convertedString.second
            } catch (e: Exception) {
                mHoursOut = mCurrentTime[Calendar.HOUR_OF_DAY]
                mMinutesOut = mCurrentTime[Calendar.MINUTE]
            }
        } else {
            mHoursOut = mCurrentTime[Calendar.HOUR_OF_DAY]
            mMinutesOut = mCurrentTime[Calendar.MINUTE]
        }
        val mTimePicker = TimePickerDialog(this.context,
            { _, selectedHour, selectedMinute ->
                val ddTime = String.format("%02d", selectedHour) + ":" + String.format(
                    "%02d",
                    selectedMinute
                )
                setText(ddTime)
                onSelected.invoke(ddTime)
            }, mHoursOut, mMinutesOut, true
        ) //Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }
}

fun EditText.setDatePicker(onSelected: (String) -> Unit) {
    //To show current date in the datepicker
    var mYear: Int
    var mMonth: Int
    var mDay: Int

    val mCurrentDate by lazy { Calendar.getInstance() }

    if (!text.isNullOrEmpty()) {
        try {
            val dateSplit = text.split("-")

            mYear = dateSplit[0].toInt()
            mMonth = dateSplit[1].toInt()
            mMonth = if (mMonth == 1) {
                0
            } else {
                mMonth - 1
            }
            mDay = dateSplit[2].toInt()
        } catch (e: Exception) {
            mYear = mCurrentDate[Calendar.YEAR]
            mMonth = mCurrentDate[Calendar.MONTH]
            mDay = mCurrentDate[Calendar.DAY_OF_MONTH]
        }
    } else {
        mYear = mCurrentDate[Calendar.YEAR]
        mMonth = mCurrentDate[Calendar.MONTH]
        mDay = mCurrentDate[Calendar.DAY_OF_MONTH]
    }
    val mDatePicker = DatePickerDialog(
        (this.context),
        { datepicker, selectedyear, selectedmonth, selectedday ->
            var currentMonth = selectedmonth
            val dateString = StringBuilder().append(selectedyear).append("-")
                .append(String.format("%02d", (currentMonth + 1.also { currentMonth = it })))
                .append("-")
                .append(String.format("%02d", selectedday))
                .toString()
            setText(dateString)
            onSelected.invoke(dateString)
        }, mYear, mMonth, mDay
    )
    mDatePicker.setTitle("Select date")
    setOnClickListener {
        mDatePicker.show()
    }
}