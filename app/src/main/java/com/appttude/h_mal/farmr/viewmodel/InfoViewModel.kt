package com.appttude.h_mal.farmr.viewmodel

import android.os.Bundle
import com.appttude.h_mal.farmr.data.Repository
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.utils.ID


class InfoViewModel(
    repository: Repository
) : ShiftViewModel(repository) {

    fun retrieveData(bundle: Bundle?) {
        val id = bundle?.getLong(ID)
        if (id == null) {
            onError("Failed to retrieve shift")
            return
        }

        val shift = getCurrentShift(id)
        if (shift == null) {
            onError("Failed to retrieve shift")
            return
        }

        onSuccess(shift)
    }

    fun buildDurationSummary(shiftObject: ShiftObject): String {
        val time = shiftObject.getHoursMinutesPairFromDuration()

        val stringBuilder = StringBuilder().append(time.first).append(" Hours ").append(time.second)
            .append(" Minutes ")
        if (shiftObject.breakMins > 0) {
            stringBuilder.append(" (+ ").append(shiftObject.breakMins).append(" minutes break)")
        }
        return stringBuilder.toString()
    }
}