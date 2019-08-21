package com.spyrdonapps.currencyconverter.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spyrdonapps.currencyconverter.data.mappers.toUiModel
import com.spyrdonapps.currencyconverter.data.model.CurrencyUiModel
import com.spyrdonapps.currencyconverter.data.remote.CurrencyService
import com.spyrdonapps.currencyconverter.util.extensions.interval
import com.spyrdonapps.currencyconverter.util.state.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import timber.log.Timber
import javax.inject.Inject

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(private val currencyService: CurrencyService) : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)

    private val _currenciesLiveData: MutableLiveData<Result<List<CurrencyUiModel>>> = MutableLiveData()
    val currenciesLiveData: LiveData<Result<List<CurrencyUiModel>>> = _currenciesLiveData

    // todo relay kotlin equivalent if needed? would be fun to implement

    init {
        loadData()
    }

    // TODO inject schedulers for testing
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
                currencyService.getCurrencies()
            }.let { response ->
                // todo cache in room db
                Timber.d(response.toString())
                _currenciesLiveData.postValue(Result.Success(response.toUiModel()))
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