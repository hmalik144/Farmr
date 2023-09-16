package com.appttude.h_mal.farmr.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract
import com.appttude.h_mal.farmr.data.legacydb.ShiftsDbHelper
import com.appttude.h_mal.farmr.data.legacydb.ShiftsDbHelper.Companion.DATABASE_NAME
import com.appttude.h_mal.farmr.data.room.converters.DateConverter
import com.appttude.h_mal.farmr.data.room.converters.TimeConverter
import com.appttude.h_mal.farmr.data.room.entity.ShiftEntity
import com.appttude.h_mal.farmr.data.room.migrations.MIGRATION_4_5

const val ROOM_DATABASE = "room_${ShiftsContract.ShiftsEntry.TABLE_NAME}"
@Database(
    entities = [ShiftEntity::class],
    version = 5,
    exportSchema = true
)
@TypeConverters(DateConverter::class, TimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getShiftDao(): ShiftDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        // create an instance of room database or use previously created instance
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).addMigrations(MIGRATION_4_5)
                .addTypeConverter(DateConverter())
                .addTypeConverter(TimeConverter())
                .build()
    }
}

