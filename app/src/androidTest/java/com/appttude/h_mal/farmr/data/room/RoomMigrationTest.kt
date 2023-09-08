package com.appttude.h_mal.farmr.data.room

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract
import com.appttude.h_mal.farmr.data.legacydb.ShiftsDbHelper
import com.appttude.h_mal.farmr.data.legacydb.ShiftsDbHelper.Companion.DATABASE_NAME
import com.appttude.h_mal.farmr.data.room.migrations.MIGRATION_4_5
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.ui.utils.getShifts
import org.junit.Rule
import org.junit.Test
import java.io.IOException


class RoomMigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val testHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName
    )

    @Test
    @Throws(IOException::class)
    fun migrationFrom2To3_containsCorrectData() {
        // Create the database in version 4
        val db = testHelper.createDatabase(TEST_DB, 4)
        // Insert some data
        getShifts().forEach {
            db.insert(
                ShiftsContract.ShiftsEntry.TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
                    put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TYPE, it.type.type)
                    put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DESCRIPTION, it.description)
                    put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DATE, it.date)
                    put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TIME_IN, it.timeIn ?: "00:00")
                    put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TIME_OUT, it.timeOut ?: "00:00")
                    put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DURATION, it.duration ?: 0.00f)
                    put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_BREAK, it.breakMins ?: 0)
                    put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_UNIT, it.units ?: 0.00f)
                    put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_PAYRATE, it.rateOfPay)
                    put(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TOTALPAY, it.totalPay)
                })
        }
        //Prepare for the next version
        db.close()

        // Re-open the database with version 5 and provide MIGRATION_4_5
        // and MIGRATION_4_5 as the migration process.
        testHelper.runMigrationsAndValidate(
            TEST_DB, 5,
            true, MIGRATION_4_5
        )

        // MigrationTestHelper automatically verifies the schema
        //changes, but not the data validity
        // Validate that the data was migrated properly.
        val dbUser: User = getMigratedRoomDatabase().userDao().getUser()
        assertEquals(dbUser.getId(), USER.getId())
        assertEquals(dbUser.getUserName(), USER.getUserName())
        // The date was missing in version 2, so it should be null in
        //version 3
        assertEquals(dbUser.getDate(), null)
    }
}