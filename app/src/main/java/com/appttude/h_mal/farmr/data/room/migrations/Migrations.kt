package com.appttude.h_mal.farmr.data.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.appttude.h_mal.farmr.data.legacydb.ShiftsDbHelper

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS '" + ShiftsDbHelper.SQL_CREATE_PRODUCTS_TABLE_2 + "'");
        database.execSQL("ALTER TABLE `shifts` ADD COLUMN `date` TEXT NOT NULL")

        // CREATE TABLE IF NOT EXISTS  (`description` TEXT NOT NULL, , `timein` TEXT NOT NULL, `timeout` TEXT NOT NULL, `break` INTEGER, `duration` REAL NOT NULL DEFAULT 0, `shifttype` TEXT NOT NULL, `unit` REAL, `payrate` REAL, `totalpay` REAL, `_id` INTEGER, PRIMARY KEY(`_id`))
    }
}