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
import com.appttude.h_mal.farmr.utils.formatAsCurrencyString
import com.appttude.h_mal.farmr.utils.hide
import com.appttude.h_mal.farmr.utils.navigateTo
import com.appttude.h_mal.farmr.utils.navigateToFragment
import com.appttude.h_mal.farmr.utils.show
import com.appttude.h_mal.farmr.viewmodel.InfoViewModel

class FurtherInfoFragment : BaseFragment<InfoViewModel>(R.layout.fragment_futher_info) {
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

        val id = FurtherInfoFragmentArgs.fromBundle(requireArguments()).shiftId

        editButton.setOnClickListener {
            val nav = FurtherInfoFragmentDirections.furtherInfoToAddItem(id)
            navigateTo(nav)
        }

        viewModel.retrieveData(id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.further_info_title))
    }

    override fun onSuccess(data: Any?) {
        super.onSuccess(data)
        if (data is ShiftObject) data.setupView()
    }

    private fun ShiftObject.setupView() {
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
                breakTV.text = StringBuilder().append(breakMins).append(" mins").toString()
                durationTV.text = viewModel.buildDurationSummary(this)
                val paymentSummary =
                    StringBuilder().append(duration).append(" Hours @ ")
                        .append(rateOfPay.formatAsCurrencyString()).append(" per Hour").append("\n")
                        .append("Equals: ").append(totalPay.formatAsCurrencyString())
                totalPayTV.text = paymentSummary
            }

            ShiftType.PIECE -> {
                hourlyDetailHolder.hide()
                unitsHolder.show()
                unitsTV.text = units.toString()

                val paymentSummary =
                    StringBuilder().append(units.formatAsCurrencyString()).append(" Units @ ")
                        .append(rateOfPay.formatAsCurrencyString()).append(" per Unit").append("\n")
                        .append("Equals: ").append(totalPay.formatAsCurrencyString())
                totalPayTV.text = paymentSummary
            }
        }
    }
}