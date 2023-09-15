package com.appttude.h_mal.farmr.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.ChildFragment
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.utils.tryGet
import com.appttude.h_mal.farmr.viewmodel.MainViewModel
import java.util.Calendar

class CalendarFragment : ChildFragment<MainViewModel>(R.layout.fragment_calendar) {
    private lateinit var shiftListView: RecyclerView
    private lateinit var calendarView: CalendarView

    private lateinit var mAdapter: ShiftListAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shiftListView = view.findViewById(R.id.shifts_available_recycler)
        calendarView = view.findViewById(R.id.calendarView)

        mAdapter = ShiftListAdapter(this, null, viewModel)
        shiftListView.adapter = mAdapter

        calendarView.setOnDayClickListener { populateShiftListsForDay(it.calendar) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshLiveData()
    }

    override fun onSuccess(data: Any?) {
        super.onSuccess(data)
        if (data is List<*>) {
            val events: List<EventDay>? = viewModel.retrieveEvents()
            calendarView.setEvents(events)

            tryGet { calendarView.firstSelectedDate }?.let {
                populateShiftListsForDay(it)
            }
        }
    }

    private fun populateShiftListsForDay(calendar: Calendar) {
        val data: List<ShiftObject>? = viewModel.getShiftsOnTheDay(calendar)
        mAdapter.submitList(data)
    }

}