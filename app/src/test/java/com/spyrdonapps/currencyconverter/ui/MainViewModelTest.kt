package com.spyrdonapps.currencyconverter.ui

import androidx.lifecycle.Observer
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.repository.CurrencyRepository
import com.spyrdonapps.currencyconverter.test.util.InstantTaskExecutorRule
import com.spyrdonapps.currencyconverter.test.util.mock
import com.spyrdonapps.currencyconverter.util.state.Result
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
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
        classUnderTest = MainViewModel(mockCurrencyRepository)
        classUnderTest.currenciesLiveData.observeForever(observer)
    }

    @Test
    fun mainViewModel_wasDataLoadingStatePosted() {
        verify(observer).onChanged(Result.Loading)
    }

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}