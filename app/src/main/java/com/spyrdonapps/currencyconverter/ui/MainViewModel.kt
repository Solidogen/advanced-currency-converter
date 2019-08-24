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
import java.util.Locale
import javax.inject.Inject

class MainViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)

    private val _currenciesLiveData: MutableLiveData<Result<List<Currency>>> = MutableLiveData()
    val currenciesLiveData: LiveData<Result<List<Currency>>> = _currenciesLiveData

    // region testing
    private val timeNow: String
        get() = SimpleDateFormat("hh:mm:ss:SSS", Locale.ROOT).format(Date())

    data class CallerMethod(val name: String)

    private val callerMethod: CallerMethod
        get() {
            return CallerMethod(Thread.currentThread().stackTrace
                .map { it.methodName.toLowerCase(Locale.ROOT).removeSuffix("\$default") }.first { methodName ->
                    !arrayOf(
                        "stacktrace",
                        "callermethod",
                        "invoke",
                        "print"
                    ).toList().any { methodName.contains(it) }
                })
        }

    private fun log(callerMethod: CallerMethod, str: String) {
        Timber.tag(callerMethod.name).e(str)
    }

    init {
        loadData()

        launchOnMainTest()

        callbackTest()
    }

    private fun launchOnMainTest(caller: CallerMethod = callerMethod) {
        scope.launch {
            delay(1000)
            log(caller, "$timeNow finished executing corou")
        }
        log(caller, "$timeNow main thread continues instantly")
    }

    private fun callbackTest(caller: CallerMethod = callerMethod) {
        Thread.sleep(2000)
        // CALLBACKS
/*      2019-08-24 11:55:24.181 9068-9068/com.spyrdonapps.currencyconverter E/MainViewModel: WAIT AND NOTIFY: WILL START, Thread[main,5,main]
        2019-08-24 11:55:24.181 9068-9068/com.spyrdonapps.currencyconverter E/MainViewModel: THREAD CONTINUES, thread: Thread[main,5,main]
        2019-08-24 11:55:24.181 9068-9105/com.spyrdonapps.currencyconverter E/MainViewModel$doOnBackgroundAndNotifyListeners: WAIT AND NOTIFY: EXECUTING, Thread[Thread-6,5,main]
        2019-08-24 11:55:26.182 9068-9068/com.spyrdonapps.currencyconverter E/MainViewModel: WAIT AND NOTIFY: FINISHED: Thread[main,5,main]*/

        doOnBackgroundAndNotifyListeners(caller, onFinish = {
            log(caller, "WAIT AND NOTIFY: FINISHED")
        })
        log(caller, "THREAD CONTINUES")
    }

    private fun doOnBackgroundAndNotifyListeners(caller: CallerMethod, onFinish: () -> Unit) {
        log(caller, "WAIT AND NOTIFY: WILL START, ${Thread.currentThread()}")
        Thread {
            log(caller, "WAIT AND NOTIFY: EXECUTING, ${Thread.currentThread()}")
            Thread.sleep(2000)
            Handler(Looper.getMainLooper()).post {
                onFinish()
            }
        }.start()
    }

    // endregion

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
}