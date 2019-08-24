package com.spyrdonapps.currencyconverter.ui

import android.os.Handler
import android.os.Looper
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class MainViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)

    private val _currenciesLiveData: MutableLiveData<Result<List<Currency>>> = MutableLiveData()
    val currenciesLiveData: LiveData<Result<List<Currency>>> = _currenciesLiveData

    val timeNow: String
        get() = SimpleDateFormat("hh:mm:ss:SSS").format(Date())

    val tag = "MVM"

    val callerMethod: String
        get() {
            val methodNames = Thread.currentThread().stackTrace.map { it.methodName }
            return methodNames.first {
                !it.toLowerCase().contains("stacktrace")
                        && !it.toLowerCase().contains("callermethod")
                        && !it.toLowerCase().contains("invokesuspend")}
        }

    init {
        loadData()

        launchOnMainTest()

        callbackTest()
    }

    private fun launchOnMainTest() {
        val thisMethod = callerMethod
        scope.launch {
            delay(1000)
            Timber.tag(thisMethod).e("$timeNow finished executing corou")
        }
        Timber.tag(thisMethod).e("$timeNow main thread continues instantly")
    }

    private fun callbackTest() {
        // CALLBACKS
/*      2019-08-24 11:55:24.181 9068-9068/com.spyrdonapps.currencyconverter E/MainViewModel: WAIT AND NOTIFY: WILL START, Thread[main,5,main]
        2019-08-24 11:55:24.181 9068-9068/com.spyrdonapps.currencyconverter E/MainViewModel: THREAD CONTINUES, thread: Thread[main,5,main]
        2019-08-24 11:55:24.181 9068-9105/com.spyrdonapps.currencyconverter E/MainViewModel$doOnBackgroundAndNotifyListeners: WAIT AND NOTIFY: EXECUTING, Thread[Thread-6,5,main]
        2019-08-24 11:55:26.182 9068-9068/com.spyrdonapps.currencyconverter E/MainViewModel: WAIT AND NOTIFY: FINISHED: Thread[main,5,main]*/

//        doOnBackgroundAndNotifyListeners(onFinish = {
//            Timber.e("WAIT AND NOTIFY: FINISHED: ${Thread.currentThread()}")
//        })
//        Timber.e("THREAD CONTINUES, thread: ${Thread.currentThread()}")
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

    private suspend fun launchLoadDataFromCache(e: Exception) {
        try {
            currencyRepository.getCurrenciesFromCache().let { cachedCurrencies ->
                _currenciesLiveData.postValue(Result.Error(e, cachedCurrencies.takeUnless { it.isEmpty() }))
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

    fun doOnBackgroundAndNotifyListeners(onFinish: () -> Unit) {
        Timber.e("WAIT AND NOTIFY: WILL START, ${Thread.currentThread()}")
        Thread {
            Timber.e("WAIT AND NOTIFY: EXECUTING, ${Thread.currentThread()}")
            Thread.sleep(2000)
            Handler(Looper.getMainLooper()).post {
                onFinish()
            }
        }.start()
    }
}