package com.spyrdonapps.currencyconverter.ui

import com.nhaarman.mockitokotlin2.mock
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.repository.CurrencyRepository
import com.spyrdonapps.currencyconverter.test.data.CurrenciesTestData
import com.spyrdonapps.currencyconverter.test.util.InstantTaskExecutorRule
import com.spyrdonapps.currencyconverter.test.util.captureValues
import com.spyrdonapps.currencyconverter.util.state.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException


@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    // region constants

    // endregion constants

    // region helper fields

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val mockCurrencyRepository: CurrencyRepository = mock()

    // endregion helper fields

    private lateinit var classUnderTest: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        classUnderTest = MainViewModel(mockCurrencyRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // TODO sometimes returns correct data, some race conditions occur
    @Test
    fun `mainViewModel, remote data not available and cached data not available, currenciesLiveData had error state`() {
        runBlockingTest {
            val exception = IOException()
            `when`(mockCurrencyRepository.getCurrenciesFromRemote()).thenAnswer { throw exception }
            `when`(mockCurrencyRepository.getCurrenciesFromCache()).thenAnswer { emptyList<List<Currency>>() }
            classUnderTest.currenciesLiveData.captureValues {
                assertSendsValues(2000, Result.Loading, Result.Error(exception))
            }
        }
    }

    // TODO sometimes returns correct data, some race conditions occur
    // Also works when viewmodel is created right in arrange of this test
    @Test
    fun `mainViewModel, remote data available, currenciesLiveData had success state with correct data`() {
        runBlockingTest {
            `when`(mockCurrencyRepository.getCurrenciesFromRemote()).thenAnswer { CurrenciesTestData.currencies }
            classUnderTest.currenciesLiveData.captureValues {
                assertSendsValues(2000, Result.Loading, Result.Success(CurrenciesTestData.currencies))
            }
        }
    }

    // TODO sometimes returns correct data, some race conditions occur
//    @Test
//    fun mainViewModel_remoteDataAvailable_currenciesLiveDataHadSuccessStateWithCorrectData() {
//        runBlockingTest {
//            `when`(mockCurrencyRepository.getCurrenciesFromRemote()).thenAnswer { CurrenciesTestData.currencies }
//            delay(1000)
//        }
//        argumentCaptor<Result<List<Currency>>>().run {
//            verify(observer, times(2)).onChanged(capture())
//
//            assertThat(lastValue, `is`(Result.Success(CurrenciesTestData.currencies) as Result<List<Currency>>))
//        }
//    }

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}