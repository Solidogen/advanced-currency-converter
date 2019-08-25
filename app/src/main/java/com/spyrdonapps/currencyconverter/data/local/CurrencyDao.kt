package com.spyrdonapps.currencyconverter.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.spyrdonapps.currencyconverter.data.model.Currency

// TODO check how to test room db and try it

@Dao
abstract class CurrencyDao {

    @Transaction
    @Query("SELECT * FROM currencies")
    abstract fun getCurrencies(): List<Currency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveCurrencies(list: List<Currency>)
}