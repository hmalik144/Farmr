package com.appttude.h_mal.farmr.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.DeleteTable
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry
import com.appttude.h_mal.farmr.data.room.entity.ShiftEntity

@Dao
interface ShiftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertFullShift(item: ShiftEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertListOfFullShift(items: List<ShiftEntity>)

    @Query("SELECT * FROM shifts WHERE ${ShiftsEntry._ID} = :shiftId LIMIT 1")
    fun getCurrentFullShift(shiftId: Long): LiveData<ShiftEntity>

    @Query("SELECT * FROM shifts WHERE ${ShiftsEntry._ID} = :shiftId LIMIT 1")
    fun getCurrentFullShiftSingle(shiftId: Long): ShiftEntity?

    @Query("SELECT * FROM shifts")
    fun getAllFullShift(): LiveData<List<ShiftEntity>>

    @Query("DELETE FROM shifts WHERE ${ShiftsEntry._ID} = :shiftId")
    fun deleteShift(shiftId: Long): Int

    @Query("DELETE FROM shifts")
    fun deleteAllShifts(): Int
}