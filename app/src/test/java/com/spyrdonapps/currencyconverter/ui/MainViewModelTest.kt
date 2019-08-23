package com.spyrdonapps.currencyconverter.ui

import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.repository.CurrencyRepository
import com.spyrdonapps.currencyconverter.test.data.CurrenciesTestData
import com.spyrdonapps.currencyconverter.test.util.InstantTaskExecutorRule
import com.spyrdonapps.currencyconverter.util.state.Result
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations


@RunWith(JUnit4::class)
class MainViewModelTest {

    // region constants

    // endregion constants

    // region helper fields

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockCurrencyRepository: CurrencyRepository = mock()
    private val observer: Observer<Result<List<Currency>>> = mock()

    // endregion helper fields

    private lateinit var classUnderTest: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        classUnderTest = MainViewModel(mockCurrencyRepository)
        classUnderTest.currenciesLiveData.observeForever(observer)
    }

    @After
    fun tearDown() {
        reset(observer, mockCurrencyRepository)
        classUnderTest.currenciesLiveData.removeObserver(observer)
    }

    @Test
    fun mainViewModel_currenciesLiveDataHadDataLoadingStatePosted() {
        verify(observer).onChanged(Result.Loading)
    }

    @Test
    fun mainViewModel_currenciesLiveDataInteractedTwice() {
        verify(observer, times(2)).onChanged(any())
    }

    @Test
    fun mainViewModel_remoteDataAvailable_currenciesLiveDataHadSuccessStateWithCorrectData() {
        runBlockingTest {
            `when`(mockCurrencyRepository.getCurrenciesFromRemote()).thenReturn(CurrenciesTestData.currencies)
        }
        argumentCaptor<Result<List<Currency>>>().run {
            verify(observer, times(2)).onChanged(capture())

            assertThat(lastValue, `is`(Result.Success(CurrenciesTestData.currencies) as Result<List<Currency>>))
        }
    }

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}