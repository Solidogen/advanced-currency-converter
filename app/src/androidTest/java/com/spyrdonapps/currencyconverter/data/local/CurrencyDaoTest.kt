package com.spyrdonapps.currencyconverter.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.spyrdonapps.currencyconverter.data.model.Currency
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert.*
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class CurrencyDaoTest {

    // region constants

    // endregion constants

    // region helper fields

    private lateinit var inMemoryDatabase: CurrencyDatabase
    private val testData = listOf(
        Currency("EUR", 1.0),
        Currency("AUD", 4.20)
    )

    // endregion helper fields

    private lateinit var classUnderTest: CurrencyDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        inMemoryDatabase = Room.inMemoryDatabaseBuilder(context, CurrencyDatabase::class.java).build()
        classUnderTest = inMemoryDatabase.currencyDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        inMemoryDatabase.close()
    }

    @Test
    fun currencyDao_savesData_fetchedDataIsEqualToSavedData() {
        classUnderTest.saveCurrencies(testData)
        val fetchedData = classUnderTest.getCurrencies()
        assertThat(testData, `is`(fetchedData))
    }


    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}