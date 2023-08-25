package com.appttude.h_mal.farmr.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseFragment
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.utils.CURRENCY
import com.appttude.h_mal.farmr.utils.ID
import com.appttude.h_mal.farmr.utils.hide
import com.appttude.h_mal.farmr.utils.navigateToFragment
import com.appttude.h_mal.farmr.utils.show
import com.appttude.h_mal.farmr.viewmodel.MainViewModel

class FurtherInfoFragment : BaseFragment<MainViewModel>(R.layout.fragment_futher_info) {
    private lateinit var typeTV: TextView
    private lateinit var descriptionTV: TextView
    private lateinit var dateTV: TextView
    private lateinit var times: TextView
    private lateinit var breakTV: TextView
    private lateinit var durationTV: TextView
    private lateinit var unitsTV: TextView
    private lateinit var payRateTV: TextView
    private lateinit var totalPayTV: TextView
    private lateinit var hourlyDetailHolder: LinearLayout
    private lateinit var unitsHolder: LinearLayout
    private lateinit var wholeView: LinearLayout
    private lateinit var progressBarFI: ProgressBar
    private lateinit var editButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getString(R.string.further_info_title))

        progressBarFI = view.findViewById(R.id.progressBar_info)
        wholeView = view.findViewById(R.id.further_info_view)
        typeTV = view.findViewById(R.id.details_shift)
        descriptionTV = view.findViewById(R.id.details_desc)
        dateTV = view.findViewById(R.id.details_date)
        times = view.findViewById(R.id.details_time)
        breakTV = view.findViewById(R.id.details_breaks)
        durationTV = view.findViewById(R.id.details_duration)
        unitsTV = view.findViewById(R.id.details_units)
        payRateTV = view.findViewById(R.id.details_pay_rate)
        totalPayTV = view.findViewById(R.id.details_totalpay)
        editButton = view.findViewById(R.id.details_edit)
        hourlyDetailHolder = view.findViewById(R.id.details_hourly_details)
        unitsHolder = view.findViewById(R.id.details_units_holder)

        val id = arguments!!.getLong(ID)

        editButton.setOnClickListener {
            navigateToFragment(FragmentAddItem(), name = "additem", bundle = arguments!!)
        }

        setupView(id)
    }

    private fun setupView(id: Long) {
        viewModel.getCurrentShift(id)?.run {
            typeTV.text = type
            descriptionTV.text = description
            dateTV.text = date
            payRateTV.text = rateOfPay.toString()
            totalPayTV.text = StringBuilder(CURRENCY).append(totalPay).toString()

            when (ShiftType.getEnumByType(type)) {
                ShiftType.HOURLY -> {
                    hourlyDetailHolder.show()
                    unitsHolder.hide()
                    times.text = StringBuilder(timeIn).append("-").append(timeOut).toString()
                    breakTV.text = StringBuilder(breakMins).append("mins").toString()
                    durationTV.text = buildDurationSummary(this)
                    val paymentSummary =
                        StringBuilder().append(duration).append(" Hours @ ").append(CURRENCY)
                            .append(rateOfPay).append(" per Hour").append("\n")
                            .append("Equals: ").append(CURRENCY).append(totalPay)
                    totalPayTV.text = paymentSummary
                }

                ShiftType.PIECE -> {
                    hourlyDetailHolder.hide()
                    unitsHolder.show()
                    unitsTV.text = units.toString()

                    val paymentSummary =
                        StringBuilder().append(units).append(" Units @ ").append(CURRENCY)
                            .append(rateOfPay).append(" per Unit").append("\n")
                            .append("Equals: ").append(CURRENCY).append(totalPay)
                    totalPayTV.text = paymentSummary
                }
            }
        }
    }

    private fun buildDurationSummary(shiftObject: ShiftObject): String {
        val time = shiftObject.getHoursMinutesPairFromDuration()

        val stringBuilder = StringBuilder().append(time.first).append(" Hours ").append(time.second)
            .append(" Minutes ")
        if (shiftObject.breakMins > 0) {
            stringBuilder.append(" (+ ").append(shiftObject.breakMins).append(" minutes break)")
        }
        return stringBuilder.toString()
    }
}