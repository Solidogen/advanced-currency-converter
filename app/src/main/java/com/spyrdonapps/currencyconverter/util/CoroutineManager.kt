package com.spyrdonapps.currencyconverter.util

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

class CoroutineManager(private val scope: CoroutineScope) {

    // TODO try async await as well

    init {
//        launchOnMainTest()
//
//        launchOnMainBlockingTest()
//
//        withContextIoTest()
//
//        scope.launch {
//            withContextIoSuspendTest()
//        }
//
//        withContextIoBlockingTest()

//        callbackTest()

        scope.launch {
            withContext(Dispatchers.IO) {
                suspendCallbackTest()
                log(callerMethod, "I WAS BLOCKED HERE IN NEXT LINE WHILE WAITING FOR SUSPEND callbackTest FUN TO FINISH")
            }
        }

        log(CallerMethod("init", 0), "$timeNow init method ends")
    }

    private fun callbackTest(caller: CallerMethod = callerMethod) {
        doOnBackgroundAndNotifyListeners(caller, onFinish = {
            log(callerMethod, "WAIT AND NOTIFY - FINISHED CALLBACK")
        })
        log(caller, "THREAD CONTINUES INSTANTLY")
    }

    /*
    * suspend cancellable coroutine uses cancellable continuation to resume coroutine in any moment
    *
    * this lets us convert callback api to coroutines
    * */
    private suspend fun suspendCallbackTest(caller: CallerMethod = callerMethod) {
        suspendCancellableCoroutine<Unit> { continuation ->
            doOnBackgroundAndNotifyListeners {
                log(caller, "WAIT AND NOTIFY - FINISHED CALLBACK")
                continuation.resume(Unit)
            }
            log(caller, "THREAD CONTINUES INSTANTLY")
        }
    }

    /*
    * typical callback api, we can convert it to a coroutine
    * */
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

    /*
    * launch doesn't block calling function
    * */
    private fun launchOnMainTest(caller: CallerMethod = callerMethod) {
        log(caller, "$timeNow starting executing corou")
        scope.launch {
            delay(1000) // when another coroutine with runBlocking (?) also makes delay, that delay is added to this delay (what)
            log(caller, "$timeNow Finished executing corou")
        }
        log(caller, "$timeNow main thread continues instantly")
    }

    /*
    * runBlocking blocks calling function
    *
    * it defines it's own BlockingCoroutineScope, no need to provide any scope
    *
    * runBlocking is designed to be called from places where there are no coroutines yet, where we are able to block the thread
    * */
    private fun launchOnMainBlockingTest(caller: CallerMethod = callerMethod) {
        runBlocking {
            log(caller, "$timeNow starting executing corou")
            launch {
                delay(1000)
                log(caller, "$timeNow Finished executing corou")
            }
            log(caller, "$timeNow main thread continues instantly after launch")
        }
        log(caller, "$timeNow main thread continues after runBlocking coroutine finishes")
    }

    /*
    * withContext blocks scope where is was launched
    * */
    private fun withContextIoTest(caller: CallerMethod = callerMethod) {
        scope.launch {
            log(caller, "$timeNow starting executing corou")
            withContext(Dispatchers.IO) {
                delay(1000)
                log(caller, "$timeNow Finished executing corou")
            }
            log(caller, "$timeNow withContext coroutine finishes, back to scope where withContext was called")
        }
        log(caller, "$timeNow main thread continues instantly after launch")
    }

    /*
    * withContext blocks scope where is was launched
    *
    * No difference from withContextIoTest, withContext just forces caller to be a CoroutineScope
    * */
    private suspend fun withContextIoSuspendTest(caller: CallerMethod = callerMethod) {
        log(caller, "$timeNow starting executing corou")
        withContext(Dispatchers.IO) {
            delay(1000)
            log(caller, "$timeNow Finished executing corou")
        }
        log(caller, "$timeNow withContext coroutine finishes, back to scope where withContext was called")
    }

    /*
    * BLOCKS calling function
    * */
    private fun withContextIoBlockingTest(caller: CallerMethod = callerMethod) {
        runBlocking {
            log(caller, "$timeNow starting executing corou")
            withContext(Dispatchers.IO) {
                delay(1000)
                log(caller, "$timeNow Finished executing corou")
            }
            log(caller, "$timeNow withContext coroutine finishes, back to scope where withContext was called")
        }
        log(caller, "$timeNow main thread continues after runBlocking coroutine finishes")
    }
}

private val timeNow: String
    get() = SimpleDateFormat("hh:mm:ss:SSS", Locale.ROOT).format(Date())

data class CallerMethod(val name: String, val codeLine: Int)

private val callerMethod: CallerMethod
    get() {
        val stackTraceElement = Thread.currentThread().stackTrace
            .first { stackTraceElement ->
                !arrayOf("stacktrace", "callermethod", "invoke", "print")
                    .toList()
                    .any {
                        stackTraceElement.methodName.toLowerCase(Locale.ROOT).contains(it)
                    }
            }
        return CallerMethod(stackTraceElement.methodName.removeSuffix("\$default"), stackTraceElement.lineNumber)
    }

private fun log(callerMethod: CallerMethod, str: String) {
    Timber.tag("${callerMethod.name} ${callerMethod.codeLine}").e(str)
}

val coroutineDebugTree = object : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        System.err.println("$tag; $message; thread: ${Thread.currentThread().name}")
    }
}