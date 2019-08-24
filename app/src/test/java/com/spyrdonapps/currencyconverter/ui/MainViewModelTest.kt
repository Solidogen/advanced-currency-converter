package com.spyrdonapps.currencyconverter.ui

import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.isNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.repository.CurrencyRepository
import com.spyrdonapps.currencyconverter.test.data.CurrenciesTestData
import com.spyrdonapps.currencyconverter.test.util.InstantTaskExecutorRule
import com.spyrdonapps.currencyconverter.util.state.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
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

    private var observer: Observer<Result<List<Currency>>> = mock()

    // endregion helper fields

    private lateinit var classUnderTest: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        classUnderTest = MainViewModel(mockCurrencyRepository)
        classUnderTest.currenciesLiveData.observeForever(observer)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
//        reset(observer, mockCurrencyRepository)
//        classUnderTest.currenciesLiveData.removeObserver(observer)
    }

    // TODO always works when ran alone, always fails when testing whole class, some race conditions occur
    @Test
    fun mainViewModel_currenciesLiveDataHadDataLoadingStatePosted() {
        argumentCaptor<Result<List<Currency>>>().run {
            verify(observer, times(2)).onChanged(capture())

            assertThat(firstValue, `is`(instanceOf(Result.Loading::class.java)))
        }
    }

    // TODO sometimes returns correct data, some race conditions occur
    // Also works when viewmodel is created right in arrange of this test
    @Test
    fun mainViewModel_currenciesLiveDataInteractedTwice() {
        verify(observer, times(2)).onChanged(any())
    }

    // TODO sometimes returns correct data, some race conditions occur
    @Test
    fun mainViewModel_remoteDataAvailable_currenciesLiveDataHadSuccessStateWithCorrectData() {
        runBlockingTest {
            `when`(mockCurrencyRepository.getCurrenciesFromRemote()).thenAnswer { CurrenciesTestData.currencies }
        }
        argumentCaptor<Result<List<Currency>>>().run {
            verify(observer, times(2)).onChanged(capture())

            assertThat(lastValue, `is`(Result.Success(CurrenciesTestData.currencies) as Result<List<Currency>>))
        }
    }

    // TODO doesn't work now, TooLittleActualInvocations, some race conditions occur
    @Test
    fun `mainViewModel, remote data not available and cached data not available, currenciesLiveData had error state`() {
        runBlockingTest {
            `when`(mockCurrencyRepository.getCurrenciesFromRemote()).thenAnswer { throw IOException() }
            `when`(mockCurrencyRepository.getCurrenciesFromCache()).thenAnswer { emptyList<List<Currency>>() }
        }
        argumentCaptor<Result<List<Currency>>>().run {
            verify(observer, times(2)).onChanged(capture())

            assertThat(lastValue, `is`(instanceOf(Result.Error::class.java)))
            assertThat((lastValue as Result.Error<List<Currency>>).data, isNull())
        }
    }

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}