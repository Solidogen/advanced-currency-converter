package com.spyrdonapps.currencyconverter.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class BaseViewModel : ViewModel() {

    private val job = SupervisorJob()
    val scope = CoroutineScope(job + Dispatchers.Main)

    var isInitialized = false
        private set

    @CallSuper
    open fun initialize() {
        isInitialized = true
    }

    @CallSuper
    override fun onCleared() {
        job.cancel()
    }
}