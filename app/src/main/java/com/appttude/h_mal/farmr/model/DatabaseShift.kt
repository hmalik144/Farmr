package com.appttude.h_mal.farmr.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityItem(
        @PrimaryKey(autoGenerate = false)
        val id: String,
        val shift: Shift
)