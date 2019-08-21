package com.spyrdonapps.currencyconverter.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay

@ExperimentalCoroutinesApi
fun CoroutineScope.interval(periodMs: Long) = produce {
    while (true) {
        send(Unit)
        delay(periodMs)
    }
}