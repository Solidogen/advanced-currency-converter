package com.spyrdonapps.currencyconverter.util.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.spyrdonapps.currencyconverter.util.state.Event

fun <T> LiveData<Event<T>>.observeEvent(owner: LifecycleOwner, onEventUnhandledContent: (T) -> Unit) {
    observe(owner, Observer { it?.getContentIfNotHandled()?.let(onEventUnhandledContent) })
}