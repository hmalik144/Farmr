package com.appttude.h_mal.farmr.viewmodel

import com.appttude.h_mal.farmr.data.Repository
import com.appttude.h_mal.farmr.model.Success


class FilterViewModel(
    repository: Repository
) : ShiftViewModel(repository) {

    fun applyFilters(
        description: String?,
        dateFrom: String?,
        dateTo: String?,
        type: String?
    ) {
        super.setFiltrationDetails(description, dateFrom, dateTo, type)
        onSuccess(Success("Filter(s) have been applied"))
    }

}