package com.appttude.h_mal.farmr.model

enum class ShiftType(val type: String){
    HOURLY("Hourly"),
    PIECE("Piece Rate");

    companion object {
        fun getEnumByType(type: String): ShiftType {
            return values().first { it.type == type }
        }
    }
}