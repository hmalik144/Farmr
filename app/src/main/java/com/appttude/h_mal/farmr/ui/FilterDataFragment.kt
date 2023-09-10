package com.appttude.h_mal.farmr.ui

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.doAfterTextChanged
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseFragment
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.model.Success
import com.appttude.h_mal.farmr.utils.goBack
import com.appttude.h_mal.farmr.utils.setDatePicker
import com.appttude.h_mal.farmr.viewmodel.FilterViewModel

class FilterDataFragment : BaseFragment<FilterViewModel>(R.layout.fragment_filter_data),
    AdapterView.OnItemSelectedListener, OnClickListener {
    private val spinnerList: Array<String> =
        arrayOf("", ShiftType.HOURLY.type, ShiftType.PIECE.type)

    private lateinit var LocationET: EditText
    private lateinit var dateFromET: EditText
    private lateinit var dateToET: EditText
    private lateinit var typeSpinner: Spinner

    private var descriptionString: String? = null
    private var dateFromString: String? = null
    private var dateToString: String? = null
    private var typeString: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LocationET = view.findViewById(R.id.filterLocationEditText)
        dateFromET = view.findViewById(R.id.fromdateInEditText)
        dateToET = view.findViewById(R.id.filterDateOutEditText)
        typeSpinner = view.findViewById(R.id.TypeFilterEditText)
        val submit: Button = view.findViewById(R.id.submitFiltered)

        val adapter: ArrayAdapter<String> =
            ArrayAdapter((context)!!, android.R.layout.simple_spinner_dropdown_item, spinnerList)
        typeSpinner.adapter = adapter

        val filterDetails = viewModel.getFiltrationDetails()

        filterDetails.run {
            description?.let {
                LocationET.setText(it)
                descriptionString = it
            }
            dateFrom?.let {
                dateFromET.setText(it)
                dateFromString = it
            }
            dateTo?.let {
                dateToET.setText(it)
                dateToString = it
            }
            type?.let {
                typeString = it
                val spinnerPosition: Int = adapter.getPosition(it)
                typeSpinner.setSelection(spinnerPosition)
            }
        }

        LocationET.doAfterTextChanged { descriptionString = it.toString() }
        dateFromET.setDatePicker { dateFromString = it }
        dateToET.setDatePicker { dateToString = it }
        typeSpinner.onItemSelectedListener = this

        submit.setOnClickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.title_activity_filter_data))
    }

    override fun onItemSelected(
        parentView: AdapterView<*>?,
        selectedItemView: View?,
        position: Int,
        id: Long
    ) {
        typeString = when (position) {
            1 -> ShiftType.HOURLY.type
            2 -> ShiftType.PIECE.type
            else -> return
        }
    }

    override fun onNothingSelected(parentView: AdapterView<*>?) {}

    private fun submitFiltrationDetails() {
        viewModel.applyFilters(descriptionString, dateFromString, dateToString, typeString)
    }

    override fun onClick(p0: View?) {
        submitFiltrationDetails()
    }

    override fun onSuccess(data: Any?) {
        super.onSuccess(data)
        if (data is Success) goBack()
    }
}