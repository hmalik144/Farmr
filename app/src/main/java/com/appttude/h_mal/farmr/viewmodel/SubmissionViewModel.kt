package com.appttude.h_mal.farmr.viewmodel

import com.appttude.h_mal.farmr.data.Repository
import com.appttude.h_mal.farmr.data.room.converters.DateConverter
import com.appttude.h_mal.farmr.data.room.converters.TimeConverter
import com.appttude.h_mal.farmr.model.Shift
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.model.Success
import com.appttude.h_mal.farmr.utils.calculateDuration
import com.appttude.h_mal.farmr.utils.dateStringIsValid
import com.appttude.h_mal.farmr.utils.formatToTwoDp
import com.appttude.h_mal.farmr.utils.getTimeString
import com.appttude.h_mal.farmr.utils.timeStringIsValid
import java.io.IOException
import java.util.Calendar


class SubmissionViewModel(
    private val repository: Repository
) : ShiftViewModel(repository) {

    private val dateConverter = DateConverter()
    private val timeConverter = TimeConverter()

    fun insertHourlyShift(
        description: String,
        date: String,
        rateOfPay: Float,
        timeIn: String?,
        timeOut: String?,
        breakMins: Int?,
    ) {
        // Validate inputs from the edit texts
        (description.trim().length > 3).validateField {
            onError("Description length should be longer")
            return
        }
        date.dateStringIsValid().validateField {
            onError("Date format is invalid")
            return
        }
        (rateOfPay >= 0.00).validateField {
            onError("Rate of pay is invalid")
            return
        }
        timeIn?.timeStringIsValid()?.validateField {
            onError("Time in format is in correct")
            return
        }
        timeOut?.timeStringIsValid()?.validateField {
            onError("Time out format is in correct")
            return
        }
        breakMins?.let { it >= 0 }?.validateField {
            onError("Break in minutes is invalid")
            return
        }

        val result = insertShiftIntoDatabase(
            ShiftType.HOURLY,
            description.trim(),
            date,
            rateOfPay.formatToTwoDp(),
            timeIn,
            timeOut,
            breakMins,
            null
        )

        if (result) onSuccess(Success("New shift successfully added"))
        else onError("Cannot insert shift")
    }

    fun insertPieceRateShift(
        description: String,
        date: String,
        units: Float,
        rateOfPay: Float
    ) {
        // Validate inputs from the edit texts
        (description.trim().length > 3).validateField {
            onError("Description length should be longer")
            return
        }
        date.dateStringIsValid().validateField {
            onError("Date format is invalid")
            return
        }
        (rateOfPay >= 0.00).validateField {
            onError("Rate of pay is invalid")
            return
        }
        (units.toInt() >= 0).validateField {
            onError("Units cannot be below zero")
            return
        }

        val result = insertShiftIntoDatabase(
            type = ShiftType.PIECE,
            description = description.trim(),
            date = date,
            rateOfPay = rateOfPay.formatToTwoDp(),
            null,
            null,
            null,
            units = units
        )
        if (result) onSuccess(Success("New shift successfully added"))
        else onError("Cannot insert shift")
    }

    fun updateShift(
        id: Long,
        type: String? = null,
        description: String? = null,
        date: String? = null,
        rateOfPay: Float? = null,
        timeIn: String? = null,
        timeOut: String? = null,
        breakMins: Int? = null,
        units: Float? = null,
    ) {
        description?.let {
            (it.length > 3).validateField {
                onError("Description length should be longer")
                return
            }
        }
        date?.dateStringIsValid()?.validateField {
            onError("Date format is invalid")
            return
        }
        rateOfPay?.let {
            (it >= 0.00).validateField {
                onError("Rate of pay is invalid")
                return
            }
        }
        units?.let {
            (it.toInt() >= 0).validateField {
                onError("Units cannot be below zero")
                return
            }
        }
        timeIn?.timeStringIsValid()?.validateField {
            onError("Time in format is in correct")
            return
        }
        timeOut?.timeStringIsValid()?.validateField {
            onError("Time out format is in correct")
            return
        }
        breakMins?.let { it >= 0 }?.validateField {
            onError("Break in minutes is invalid")
            return
        }

        val result = updateShiftInDatabase(
            id,
            type = type?.let { ShiftType.getEnumByType(it) },
            description = description,
            date = date,
            rateOfPay = rateOfPay,
            timeIn = timeIn,
            timeOut = timeOut,
            breakMins = breakMins,
            units = units
        )

        if (result) onSuccess(Success("Shift successfully updated"))
        else onError("Cannot update shift")
    }

    private fun updateShiftInDatabase(
        id: Long,
        type: ShiftType? = null,
        description: String? = null,
        date: String? = null,
        rateOfPay: Float? = null,
        timeIn: String? = null,
        timeOut: String? = null,
        breakMins: Int? = null,
        units: Float? = null,
    ): Boolean {
        val currentShift = repository.readSingleShiftFromDatabase(id)
            ?: throw IOException("Cannot update shift as it does not exist")

        val mDate = date ?: dateConverter.fromDate(currentShift.date)
        val mTimeIn = timeIn ?: timeConverter.fromTime(currentShift.timeIn)
        val mTimeOut = timeOut ?: timeConverter.fromTime(currentShift.timeIn)

        val shift = when (type) {
            ShiftType.HOURLY -> {
                // Shift type has changed so mandatory fields for hourly shift are now required as well
                Shift(
                    description = description ?: currentShift.description,
                    date = mDate,
                    timeIn = mTimeIn,
                    timeOut = mTimeOut,
                    breakMins = breakMins ?: currentShift.breakMins,
                    rateOfPay = (rateOfPay ?: currentShift.payRate) ?: 0f
                )
            }

            ShiftType.PIECE -> {
                // Shift type has changed so mandatory fields for piece rate shift are now required as well
                Shift(
                    description = description ?: currentShift.description,
                    date = mDate,
                    units = (units ?: currentShift.units) ?: 0f,
                    rateOfPay = (rateOfPay ?: currentShift.payRate) ?: 0f
                )
            }

            else -> {
                if (timeIn == null && timeOut == null && units == null && breakMins == null && rateOfPay == null) {
                    // Updates to description or date field
                    Shift(
                        ShiftType.getEnumByType(currentShift.type),
                        description ?: currentShift.description,
                        mDate,
                        mTimeIn,
                        mTimeOut,
                        currentShift.duration,
                        currentShift.breakMins,
                        currentShift.units,
                        currentShift.payRate ?: 0f,
                        currentShift.totalPay ?: 0f
                    )
                } else {
                    // Updating shifts where shift type has remained the same
                    when (ShiftType.getEnumByType(currentShift.type)) {
                        ShiftType.HOURLY -> {
                            Shift(
                                description = description ?: currentShift.description,
                                date = mDate,
                                timeIn = mTimeIn,
                                timeOut = mTimeOut,
                                breakMins = breakMins ?: currentShift.breakMins,
                                rateOfPay = (rateOfPay ?: currentShift.payRate) ?: 0f
                            )
                        }

                        ShiftType.PIECE -> {
                            val insertUnits = (units ?: currentShift.units)
                            Shift(
                                description = description ?: currentShift.description,
                                date = mDate,
                                units = (insertUnits) ?: 0f,
                                rateOfPay = (rateOfPay ?: currentShift.payRate) ?: 0f
                            )
                        }
                    }
                }
            }
        }

        return repository.updateShiftIntoDatabase(id, shift)
    }

    private fun insertShiftIntoDatabase(
        type: ShiftType,
        description: String,
        date: String,
        rateOfPay: Float,
        timeIn: String?,
        timeOut: String?,
        breakMins: Int?,
        units: Float?,
    ): Boolean {
        val shift = when (type) {
            ShiftType.HOURLY -> {
                if (timeIn.isNullOrBlank() && timeOut.isNullOrBlank()) throw IOException("Time in and time out are null")
                val calendar by lazy { Calendar.getInstance() }
                val insertTimeIn = timeIn ?: calendar.getTimeString()
                val insertTimeOut = timeOut ?: calendar.getTimeString()
                Shift(
                    description = description,
                    date = date,
                    timeIn = insertTimeIn,
                    timeOut = insertTimeOut,
                    breakMins = breakMins,
                    rateOfPay = rateOfPay
                )
            }

            ShiftType.PIECE -> {
                Shift(
                    description = description,
                    date = date,
                    units = units!!,

                    rateOfPay = rateOfPay,
                )
            }
        }

        return repository.insertShiftIntoDatabase(shift)
    }

    private inline fun Boolean.validateField(failureCallback: () -> Unit) {
        if (!this) failureCallback.invoke()
    }

    fun retrieveDurationText(mTimeIn: String?, mTimeOut: String?, mBreaks: Int?): Float? {
        try {
            return calculateDuration(mTimeIn, mTimeOut, mBreaks)
        } catch (e: IOException) {
            onError(e)
        }
        return null
    }

}