package com.spyrdonapps.currencyconverter.data.repository

import com.nhaarman.mockitokotlin2.mock
import com.spyrdonapps.currencyconverter.data.local.CurrencyDao
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.remote.ApiCurrencies
import com.spyrdonapps.currencyconverter.data.remote.CurrenciesResponse
import com.spyrdonapps.currencyconverter.data.remote.CurrencyService
import com.spyrdonapps.currencyconverter.test.data.CurrenciesTestData
import com.spyrdonapps.currencyconverter.test.data.TestExceptions
import com.spyrdonapps.currencyconverter.test.data.TestExceptions.ioException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.junit.MockitoJUnitRunner

import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert.*
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import java.io.IOException


@RunWith(MockitoJUnitRunner::class)
class AppCurrencyRepositoryTest {

    // region constants

    // endregion constants

    // region helper fields

    private val testDispatcher = TestCoroutineDispatcher()

    private val mockCurrencyService: CurrencyService = mock()
    private val mockCurrencyDao: CurrencyDao = mock()

    // endregion helper fields

    private lateinit var classUnderTest: AppCurrencyRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        classUnderTest = AppCurrencyRepository(mockCurrencyService, mockCurrencyDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `currencyRepository getCurrenciesFromRemote, remote data available, repo returns correct data`() {
        runBlocking {
            `when`(mockCurrencyService.getCurrenciesResponse()).thenAnswer { CurrenciesTestData.currenciesResponse }
            val currencies = classUnderTest.getCurrenciesFromRemote()
            assertThat(currencies, `is`(CurrenciesTestData.currencies))
        }
    }

    @Test(expected = IOException::class)
    fun `currencyRepository getCurrenciesFromRemote, remote data error, repo throws error`() {
        runBlocking {
            `when`(mockCurrencyService.getCurrenciesResponse()).thenAnswer { throw ioException }
            classUnderTest.getCurrenciesFromRemote()
        }
    }

    @Test
    fun `currencyRepository getCurrenciesFromCache, cached data available, repo returns correct data`() {
        runBlocking {
            `when`(mockCurrencyDao.getCurrencies()).thenAnswer { CurrenciesTestData.currencies }
            val currencies = classUnderTest.getCurrenciesFromCache()
            assertThat(currencies, `is`(CurrenciesTestData.currencies))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `currencyRepository getCurrenciesFromCache, cached data is empty list, repo throws error`() {
        runBlocking {
            `when`(mockCurrencyDao.getCurrencies()).thenAnswer { emptyList<Currency>() }
            classUnderTest.getCurrenciesFromCache()
        }
    }

    @Test(expected = Exception::class)
    fun `currencyRepository getCurrenciesFromCache, cached data error, repo throws error`() {
        runBlocking {
            `when`(mockCurrencyDao.getCurrencies()).thenAnswer { throw Exception() }
            classUnderTest.getCurrenciesFromCache()
        }
    }

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}