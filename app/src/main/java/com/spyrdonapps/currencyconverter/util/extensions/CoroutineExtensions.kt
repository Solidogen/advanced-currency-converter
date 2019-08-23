package com.spyrdonapps.currencyconverter.util.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

suspend fun interval(periodMs: Long, block: () -> Unit) {
    runBlocking {
        flow {
            while (true) {
                emit(Unit)
                delay(periodMs)
            }
        }.collect {
            block()
        }
    }
}