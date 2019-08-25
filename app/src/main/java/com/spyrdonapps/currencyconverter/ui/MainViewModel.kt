package com.spyrdonapps.currencyconverter.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.repository.CurrencyRepository
import com.spyrdonapps.currencyconverter.ui.base.BaseViewModel
import com.spyrdonapps.currencyconverter.util.CoroutineManager
import com.spyrdonapps.currencyconverter.util.extensions.interval
import com.spyrdonapps.currencyconverter.util.state.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) : BaseViewModel() {

    private val _currenciesLiveData: MutableLiveData<Result<List<Currency>>> = MutableLiveData()
    val currenciesLiveData: LiveData<Result<List<Currency>>> = _currenciesLiveData

    override fun initialize() {
        super.initialize()
        loadData()

        /*
         * TESTING COROUTINES
         */
        CoroutineManager(scope)
    }

    private fun loadData() {
        _currenciesLiveData.postValue(Result.Loading)

        scope.launch(Dispatchers.IO) {
            interval(periodMs = INTERVAL_CHECK_PERIOD_MS) {
                launch {
                    launchLoadDataFromRemote()
                }
            }
        }
    }

    private suspend fun launchLoadDataFromRemote() = withContext(Dispatchers.IO) {
        try {
            currencyRepository.getCurrenciesFromRemote().let { currencies ->
                _currenciesLiveData.postValue(Result.Success(currencies))
            }
        } catch (e: Exception) {
            Timber.e(e)
            launchLoadDataFromCache(e)
        }
    }

    private suspend fun launchLoadDataFromCache(loadFromRemoteException: Exception) {
        try {
            currencyRepository.getCurrenciesFromCache().let { cachedCurrencies ->
                _currenciesLiveData.postValue(Result.Error(loadFromRemoteException, cachedCurrencies))
            }
        } catch (e: Exception) {
            Timber.e(e)
            _currenciesLiveData.postValue(Result.Error(e))
        }
    }

    companion object {
        const val INTERVAL_CHECK_PERIOD_MS = 1000L
    }
}