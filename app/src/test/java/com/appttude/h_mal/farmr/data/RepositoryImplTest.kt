package com.appttude.h_mal.farmr.data

import com.appttude.h_mal.farmr.data.legacydb.LegacyDatabase
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.data.prefs.PreferenceProvider
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RepositoryImplTest {

    private lateinit var repository: RepositoryImpl

    @MockK
    lateinit var db: LegacyDatabase

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
        val elements = listOf<ShiftObject>(
            mockk { every { id } returns anyLong() },
            mockk { every { id } returns anyLong() },
            mockk { every { id } returns anyLong() },
            mockk { every { id } returns anyLong() }
        )

        //Act
        every { db.readShiftsFromDatabase() } returns elements

        // Assert
        val result = repository.readShiftsFromDatabase()
        assertIs<List<ShiftObject>>(result)
        assertEquals(result.first().id, anyLong())
    }

}