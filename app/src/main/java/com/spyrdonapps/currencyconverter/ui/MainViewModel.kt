package com.spyrdonapps.currencyconverter.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.repository.CurrencyRepository
import com.spyrdonapps.currencyconverter.util.extensions.interval
import com.spyrdonapps.currencyconverter.util.state.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)

    private val _currenciesLiveData: MutableLiveData<Result<List<Currency>>> = MutableLiveData()
    val currenciesLiveData: LiveData<Result<List<Currency>>> = _currenciesLiveData

    init {
        loadData()
    }

    // TODO tests + inject schedulers for testing
    private fun loadData() {
        _currenciesLiveData.postValue(Result.Loading)

        scope.launch(Dispatchers.IO) {
            interval(periodMs = INTERVAL_CHECK_PERIOD_MS).consumeEach {
                launchLoadData()
            }
        }
    }

    private suspend fun launchLoadData() {
        try {
            withContext(Dispatchers.IO) {
                currencyRepository.getCurrencies()
            }.let { currencies ->
                currencyRepository.saveCurrencies(currencies)
                _currenciesLiveData.postValue(Result.Success(currencies))
            }
        } catch (e: Exception) {
            Timber.e(e)
            _currenciesLiveData.postValue(Result.Error(e))
        }
    }

    override fun onCleared() {
        job.cancel()
    }

    companion object {
        const val INTERVAL_CHECK_PERIOD_MS = 1000L
    }
}