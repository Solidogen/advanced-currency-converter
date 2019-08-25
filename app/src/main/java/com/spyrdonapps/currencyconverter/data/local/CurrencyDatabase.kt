package com.spyrdonapps.currencyconverter.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spyrdonapps.currencyconverter.data.model.Currency

@Database(entities = [
    Currency::class
], version = 1, exportSchema = false)
abstract class CurrencyDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao

    companion object {
        const val DATABASE_NAME = "app-database"
    }
}