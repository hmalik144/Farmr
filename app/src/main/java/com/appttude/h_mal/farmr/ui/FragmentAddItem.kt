package com.appttude.h_mal.farmr.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.FormFragment
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.model.Success
import com.appttude.h_mal.farmr.utils.ID
import com.appttude.h_mal.farmr.utils.createDialog
import com.appttude.h_mal.farmr.utils.displayToast
import com.appttude.h_mal.farmr.utils.formatAsCurrencyString
import com.appttude.h_mal.farmr.utils.formatToTwoDpString
import com.appttude.h_mal.farmr.utils.goBack
import com.appttude.h_mal.farmr.utils.hide
import com.appttude.h_mal.farmr.utils.setDatePicker
import com.appttude.h_mal.farmr.utils.setTimePicker
import com.appttude.h_mal.farmr.utils.show
import com.appttude.h_mal.farmr.utils.validateField
import com.appttude.h_mal.farmr.viewmodel.SubmissionViewModel

class FragmentAddItem : FormFragment<SubmissionViewModel>(R.layout.fragment_add_item),
    RadioGroup.OnCheckedChangeListener {

    private lateinit var onBackPressed: OnBackPressedCallback

    private lateinit var mHourlyRadioButton: RadioButton
    private lateinit var mPieceRadioButton: RadioButton
    private lateinit var mLocationEditText: EditText
    private lateinit var mDateEditText: EditText
    private lateinit var mDurationTextView: TextView
    private lateinit var mTimeInEditText: EditText
    private lateinit var mTimeOutEditText: EditText
    private lateinit var mBreakEditText: EditText
    private lateinit var mUnitEditText: EditText
    private lateinit var mPayRateEditText: EditText
    private lateinit var mTotalPayTextView: TextView
    private lateinit var hourlyDataView: LinearLayout
    private lateinit var unitsHolder: LinearLayout
    private lateinit var durationHolder: LinearLayout
    private lateinit var wholeView: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var submitProduct: Button
    private lateinit var mRadioGroup: RadioGroup

    private var mDate: String? = null
    private var mDescription: String? = null
    private var mTimeIn: String? = null
    private var mTimeOut: String? = null
    private var mBreaks: Int? = null
    private var mUnits: Float? = null
    private var mPayRate = 0f
    private var mType: ShiftType? = null
    private var mDuration: Float? = null

    private var id: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollView = view.findViewById(R.id.total_view)
        mRadioGroup = view.findViewById(R.id.rg)
        mHourlyRadioButton = view.findViewById(R.id.hourly)
        mPieceRadioButton = view.findViewById(R.id.piecerate)
        mLocationEditText = view.findViewById(R.id.locationEditText)
        mDateEditText = view.findViewById(R.id.dateEditText)
        mTimeInEditText = view.findViewById(R.id.timeInEditText)
        mBreakEditText = view.findViewById(R.id.breakEditText)
        mTimeOutEditText = view.findViewById(R.id.timeOutEditText)
        mDurationTextView = view.findViewById(R.id.ShiftDuration)
        mUnitEditText = view.findViewById(R.id.unitET)
        mPayRateEditText = view.findViewById(R.id.payrateET)
        mTotalPayTextView = view.findViewById(R.id.totalpayval)
        hourlyDataView = view.findViewById(R.id.hourly_data_holder)
        unitsHolder = view.findViewById(R.id.units_holder)
        durationHolder = view.findViewById(R.id.duration_holder)
        wholeView = view.findViewById(R.id.whole_view)
        submitProduct = view.findViewById(R.id.submit)

        mRadioGroup.setOnCheckedChangeListener(this)
        mLocationEditText.doAfterTextChanged {
            mDescription = it.toString()
        }
        mDateEditText.setDatePicker { mDate = it }
        mTimeInEditText.setTimePicker {
            mTimeIn = it
            calculateTotalPay()
        }
        mTimeOutEditText.setTimePicker {
            mTimeOut = it
            calculateTotalPay()
        }
        mBreakEditText.doAfterTextChanged {
            mBreaks = it.toString().toIntOrNull() ?: 0
            calculateTotalPay()
        }
        mUnitEditText.doAfterTextChanged {
            it.toString().toFloatOrNull()?.let { u -> mUnits = u }
            calculateTotalPay()
        }
        mPayRateEditText.doAfterTextChanged {
            it.toString().toFloatOrNull()?.let { p ->
                mPayRate = p
                calculateTotalPay()
            }
        }

        submitProduct.setOnClickListener { submitShift() }

        setupViewAfterViewCreated()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        // This callback is only called when MyFragment is at least started
        onBackPressed = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressed)

        id = try {
            FragmentAddItemArgs.fromBundle(requireArguments()).shiftId
        } catch (e: Exception) {
            Log.i("Nav Args", "Failed to retrieve args from navigation")
            null
        }
    }

    override fun onResume() {
        super.onResume()
        val title = when (arguments?.containsKey(ID)) {
            true -> getString(R.string.edit_item_title)
            else -> getString(R.string.add_item_title)
        }
        setTitle(title)

        onBackPressed.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        onBackPressed.isEnabled = false
    }

    private fun setupViewAfterViewCreated() {
        wholeView.hide()

        // Since we are editing a shift lets load the shift data into the views
        id?.let { viewModel.getCurrentShift(it) }?.run {
            mLocationEditText.setText(description)
            mDateEditText.setText(date)

            // Set types
            mType = ShiftType.getEnumByType(type)
            mDescription = description
            mDate = date
            mPayRate = rateOfPay

            when (ShiftType.getEnumByType(type)) {
                ShiftType.HOURLY -> {
                    mHourlyRadioButton.isChecked = true
                    mPieceRadioButton.isChecked = false
                    mTimeInEditText.setText(timeIn)
                    mTimeOutEditText.setText(timeOut)
                    mBreakEditText.setText(breakMins.toString())
                    val durationText = "${duration.formatToTwoDpString()} Hours"
                    mDurationTextView.text = durationText

                    // Set fields
                    mTimeIn = timeIn
                    mTimeOut = timeOut
                    mBreaks = breakMins
                }

                ShiftType.PIECE -> {
                    mHourlyRadioButton.isChecked = false
                    mPieceRadioButton.isChecked = true
                    mUnitEditText.setText(units.formatToTwoDpString())

                    // Set piece rate units
                    mUnits = units
                }
            }
            mPayRateEditText.setText(rateOfPay.formatAsCurrencyString())
            mTotalPayTextView.text = totalPay.formatAsCurrencyString()

            calculateTotalPay()
        }

        applyFormListener(view = view as ViewGroup)
    }

    override fun onCheckedChanged(radioGroup: RadioGroup, id: Int) {
        when (radioGroup.checkedRadioButtonId) {
            R.id.hourly -> {
                mType = ShiftType.HOURLY
                wholeView.show()
                unitsHolder.hide()
                hourlyDataView.show()
                durationHolder.show()
            }

            R.id.piecerate -> {
                mType = ShiftType.PIECE
                wholeView.show()
                unitsHolder.show()
                hourlyDataView.hide()
                durationHolder.hide()
            }
        }
    }

    private fun submitShift() {
        mDate.validateField({ !it.isNullOrBlank() }) {
            onFailure("Date field cannot be empty")
            return
        }
        mDescription.validateField({ !it.isNullOrBlank() }) {
            onFailure("Description field cannot be empty")
            return
        }
        mPayRate.validateField({ !it.isNaN() }) {
            onFailure("Rate of pay field cannot be empty")
            return
        }

        if (mPieceRadioButton.isChecked) {

            mUnits.validateField({ it != null && it >= 0 }) {
                onFailure("Units field cannot be empty")
                return
            }
            if (id != null) {
                // update
                viewModel.updateShift(
                    id!!,
                    description = mDescription,
                    date = mDate,
                    units = mUnits,
                    rateOfPay = mPayRate
                )
            } else {
                // insert
                viewModel.insertPieceRateShift(mDescription!!, mDate!!, mUnits!!, mPayRate)
            }
        } else if (mHourlyRadioButton.isChecked) {
            if (id != null) {
                // update
                viewModel.updateShift(
                    id!!,
                    description = mDescription,
                    date = mDate,
                    rateOfPay = mPayRate,
                    timeIn = mTimeIn,
                    timeOut = mTimeOut,
                    breakMins = mBreaks
                )
            } else {
                // insert
                viewModel.insertHourlyShift(
                    mDescription!!,
                    mDate!!,
                    mPayRate,
                    mTimeIn,
                    mTimeOut,
                    mBreaks
                )
            }

        }
    }

    private fun calculateTotalPay() {
        mType?.let {
            val total = when (it) {
                ShiftType.HOURLY -> {
                    // Calculate duration before total pay calculation
                    mDuration = viewModel.retrieveDurationText(mTimeIn, mTimeOut, mBreaks) ?: return
                    mDurationTextView.text =
                        StringBuilder().append(mDuration).append(" hours").toString()
                    mDuration!! * mPayRate
                }

                ShiftType.PIECE -> {
                    (mUnits ?: 0f) * mPayRate
                }
            }
            mTotalPayTextView.text = total.formatAsCurrencyString()
        }
    }

    fun onBackPressed() {
        if (didFormChange()) {
            requireContext().createDialog(
                title = "Discard Changes?",
                message = "Are you sure you want to discard changes?",
                displayCancel = true,
                okCallback = { _, _ ->
                    goBack()
                }
            )
        } else {
            goBack()
        }
    }

    override fun onSuccess(data: Any?) {
        super.onSuccess(data)
        if (data is Success) {
            displayToast(data.successMessage)
            goBack()
        }
    }
}