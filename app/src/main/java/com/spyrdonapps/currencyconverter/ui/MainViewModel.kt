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
import kotlinx.coroutines.runBlocking
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
                    !arrayOf("stacktrace", "callermethod", "invoke", "print").toList().any { methodName.contains(it) }
                })
        }

    private fun log(callerMethod: CallerMethod, str: String) {
        Timber.tag(callerMethod.name).e(str)
    }

    init {
        loadData()

//        launchOnMainTest()
//        launchOnMainBlockingTest()

//        withContextIoTest()
//        withContextIoBlockingTest()

        callSuspendingWithContextIoTest()

//        callbackTest()
    }

    private fun launchOnMainTest(caller: CallerMethod = callerMethod) {
        log(caller, "$timeNow starting executing corou")
        scope.launch {
            delay(1000) // when another coroutine with runBlocking (?) also makes delay, that delay is added to this delay (what)
            log(caller, "$timeNow Finished executing corou")
        }
        log(caller, "$timeNow main thread continues instantly")
    }

    /*
    * runBlocking is blocking coroutine scope, different than scope defined in viewmodel
    *
    * scope.launch doesn't block thread, runBlocking does (?)
    *
    * this lets coroutine finish before
    * */
    private fun launchOnMainBlockingTest(caller: CallerMethod = callerMethod) {
        runBlocking {
            log(caller, "$timeNow starting executing corou")
            launch {
                delay(1000)
                log(caller, "$timeNow Finished executing corou")
            }
        }
        log(caller, "$timeNow main thread continues after coroutine finishes")
    }

    /*
    * thread continues instantly (I would think that is "blocks" but it's not a suspending function, see 'callSuspendingWithContextIoTest')
    * */
    private fun withContextIoTest(caller: CallerMethod = callerMethod) {
        scope.launch {
            log(caller, "$timeNow starting executing corou")
            withContext(Dispatchers.IO) {
                delay(1000)
                log(caller, "$timeNow Finished executing corou")
            }
        }
        log(caller, "$timeNow main thread continues instantly")
    }

    private fun withContextIoBlockingTest(caller: CallerMethod = callerMethod) {
        runBlocking {
            log(caller, "$timeNow starting executing corou")
            withContext(Dispatchers.IO) {
                delay(1000)
                log(caller, "$timeNow Finished executing corou")
            }
        }
        log(caller, "$timeNow main thread continues after coroutine finishes")
    }

    /*
    * thread is actually blocked because of 'suspend' keywork, otherwise it is the same as 'withContextIoTest'
    * */
    private fun callSuspendingWithContextIoTest(caller: CallerMethod = callerMethod) {

        suspend fun method(caller: CallerMethod) {
            log(caller, "$timeNow starting executing corou")
            withContext(Dispatchers.IO) {
                delay(1000)
                log(caller, "$timeNow Finished executing corou")
            }
        }

        scope.launch {
            method(caller)
            log(caller, "$timeNow main thread continues after coroutine finishes")
        }
    }

    private fun callbackTest(caller: CallerMethod = callerMethod) {
        // CALLBACKS
/*      2019-08-24 13:47:48.171 16219-16219 W/System.err: callbacktest;, WAIT AND NOTIFY: WILL START; thread: main
        2019-08-24 13:47:48.172 16219-16219 W/System.err: callbacktest;, THREAD CONTINUES; thread: main
        2019-08-24 13:47:48.172 16219-16258 W/System.err: callbacktest;, WAIT AND NOTIFY: EXECUTING; thread: Thread-7
        2019-08-24 13:47:50.174 16219-16219 W/System.err: callbacktest;, WAIT AND NOTIFY: FINISHED; thread: main*/

        doOnBackgroundAndNotifyListeners(caller, onFinish = {
            log(caller, "WAIT AND NOTIFY - FINISHED CALLBACK")
        })
        log(caller, "THREAD CONTINUES INSTANTLY")
    }

    private fun doOnBackgroundAndNotifyListeners(caller: CallerMethod = callerMethod, onFinish: () -> Unit) {
        log(caller, "WAIT AND NOTIFY - WILL START")
        Thread {
            log(caller, "WAIT AND NOTIFY - EXECUTING")
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