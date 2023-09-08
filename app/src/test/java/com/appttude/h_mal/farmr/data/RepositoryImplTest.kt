package com.appttude.h_mal.farmr.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appttude.h_mal.farmr.data.legacydb.LegacyDatabase
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.data.prefs.PreferenceProvider
import com.appttude.h_mal.farmr.data.room.AppDatabase
import com.appttude.h_mal.farmr.data.room.ShiftDao
import com.appttude.h_mal.farmr.data.room.entity.ShiftEntity
import com.appttude.h_mal.farmr.model.Shift
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class RepositoryImplTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var repository: RepositoryImpl

    @RelaxedMockK
    lateinit var db: AppDatabase

    @MockK
    lateinit var prefs: PreferenceProvider

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = RepositoryImpl(db, prefs)
    }

    @Test
    fun readDatabase_validResponse() {
        // Arrange
        val liveData = mockk<LiveData<List<ShiftEntity>>>()

        //Act
        every { db.getShiftDao().getAllFullShift() } returns liveData

        // Assert
        val result = repository.readShiftsFromDatabase()
        assertIs<LiveData<List<ShiftEntity>>>(result)
        assertEquals(result, liveData)
    }

    @Test
    fun updateShift_invalidDescription_validThrow() {
        // Arrange
        val id = anyLong()
        val shift = mockk<Shift>()

        //Act
        val emptyDescExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.description } returns ""
            repository.updateShiftIntoDatabase(id, shift)
        }
        val untrimmedDescExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.description } returns "DQ "
            repository.updateShiftIntoDatabase(id, shift)
        }

        // Assert
        assertEquals(emptyDescExceptionReturned.message, "description required")
        assertEquals(untrimmedDescExceptionReturned.message, "description required")
    }

    @Test
    fun updateShift_invalidDate_validThrow() {
        // Arrange
        val id = anyLong()
        val shift = mockk<Shift>()

        //Act
        every { shift.description } returns "Valid desc"
        val emptyDateExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.date } returns ""
            repository.updateShiftIntoDatabase(id, shift)
        }
        val untrimmedDateExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.description } returns "2022-03-02 "
            repository.updateShiftIntoDatabase(id, shift)
        }
        val wrongFormatDateExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.description } returns "02-03-2020"
            repository.updateShiftIntoDatabase(id, shift)
        }
        val wrongFormatDateExceptionReturned2 = assertFailsWith<IllegalArgumentException> {
            every { shift.description } returns "2022/03/02"
            repository.updateShiftIntoDatabase(id, shift)
        }

        // Assert
        assertEquals(emptyDateExceptionReturned.message, "date required")
        assertEquals(untrimmedDateExceptionReturned.message, "date required")
        assertEquals(wrongFormatDateExceptionReturned.message, "date required")
        assertEquals(wrongFormatDateExceptionReturned2.message, "date required")
    }

    @Test
    fun updateShift_invalidTimeInAndTimeOut_validThrow() {
        // Arrange
        val id = anyLong()
        val shift = mockk<Shift>()

        //Act
        every { shift.description } returns "Valid desc"
        every { shift.date } returns "2020-06-05"
        val emptyTimeInExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.timeIn } returns ""
            repository.updateShiftIntoDatabase(id, shift)
        }
        val untrimmedTimeInExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.timeIn } returns "14:04 "
            repository.updateShiftIntoDatabase(id, shift)
        }
        val wrongFormatTimeInExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.timeIn } returns "14 04"
            repository.updateShiftIntoDatabase(id, shift)
        }
        val wrongFormatTimeInExceptionReturned2 = assertFailsWith<IllegalArgumentException> {
            every { shift.timeIn } returns "1404"
            repository.updateShiftIntoDatabase(id, shift)
        }
        every { shift.timeIn } returns "14:00"
        val emptyTimeOutExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.timeOut } returns ""
            repository.updateShiftIntoDatabase(id, shift)
        }
        val untrimmedTimeOutExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.timeOut } returns "14:04 "
            repository.updateShiftIntoDatabase(id, shift)
        }
        val wrongFormatTimeOutExceptionReturned = assertFailsWith<IllegalArgumentException> {
            every { shift.timeOut } returns "14 04"
            repository.updateShiftIntoDatabase(id, shift)
        }
        val wrongFormatTimeOutExceptionReturned2 = assertFailsWith<IllegalArgumentException> {
            every { shift.timeOut } returns "1404"
            repository.updateShiftIntoDatabase(id, shift)
        }

        // Assert
        assertEquals(emptyTimeInExceptionReturned.message, "time in required")
        assertEquals(untrimmedTimeInExceptionReturned.message, "time in required")
        assertEquals(wrongFormatTimeInExceptionReturned.message, "time in required")
        assertEquals(wrongFormatTimeInExceptionReturned2.message, "time in required")

        assertEquals(emptyTimeOutExceptionReturned.message, "time out required")
        assertEquals(untrimmedTimeOutExceptionReturned.message, "time out required")
        assertEquals(wrongFormatTimeOutExceptionReturned.message, "time out required")
        assertEquals(wrongFormatTimeOutExceptionReturned2.message, "time out required")
    }

}