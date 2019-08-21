package com.spyrdonapps.currencyconverter.util.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay

fun CoroutineScope.interval(periodMs: Long) = produce {
    while (true) {
        send(Unit)
        delay(periodMs)
    }
}