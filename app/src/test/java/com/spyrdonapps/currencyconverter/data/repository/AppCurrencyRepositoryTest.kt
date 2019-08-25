package com.spyrdonapps.currencyconverter.data.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.spyrdonapps.currencyconverter.data.local.CurrencyDao
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.remote.CurrencyService
import com.spyrdonapps.currencyconverter.test.data.CurrenciesTestData
import com.spyrdonapps.currencyconverter.test.data.TestExceptions.ioException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
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

    @Test
    fun `currencyRepository getCurrenciesFromRemote, remote data available, data is saved to cache`() {
        runBlocking {
            `when`(mockCurrencyService.getCurrenciesResponse()).thenAnswer { CurrenciesTestData.currenciesResponse }
            classUnderTest.getCurrenciesFromRemote()
            verify(mockCurrencyDao).saveCurrencies(CurrenciesTestData.currencies)
        }
    }

    @Test(expected = IOException::class)
    fun `currencyRepository getCurrenciesFromRemote, remote data error, repo throws error`() {
        runBlocking {
            `when`(mockCurrencyService.getCurrenciesResponse()).thenAnswer { throw ioException }
            classUnderTest.getCurrenciesFromRemote()
        }
        // expected exception defined in @Test parameter
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
        // expected exception defined in @Test parameter
    }

    @Test(expected = Exception::class)
    fun `currencyRepository getCurrenciesFromCache, cached data error, repo throws error`() {
        runBlocking {
            `when`(mockCurrencyDao.getCurrencies()).thenAnswer { throw Exception() }
            classUnderTest.getCurrenciesFromCache()
        }
        // expected exception defined in @Test parameter
    }

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}