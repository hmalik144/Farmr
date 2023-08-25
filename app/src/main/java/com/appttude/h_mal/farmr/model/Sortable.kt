package com.appttude.h_mal.farmr.model

enum class Sortable(val label: String) {
    ID("Default"),
    TYPE("Shift Type"),
    DATE("Date"),
    DESCRIPTION("Description"),
    DURATION("Added"), UNITS("Duration"),
    RATEOFPAY("Rate of pay"),
    TOTALPAY("Total Pay")
}