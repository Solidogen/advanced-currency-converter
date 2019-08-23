package com.spyrdonapps.currencyconverter.data.mappers

import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.remote.CurrenciesResponse
import com.spyrdonapps.currencyconverter.ui.CurrenciesAdapter
import kotlin.reflect.full.memberProperties

/*
* Custom deserialization in interceptor would be much better,
* but decided to have fun with reflection anyway
* */
fun CurrenciesResponse.toCurrencyList(): List<Currency> =
    rates::class.memberProperties
        .map {
            Currency(
                isoCode = it.name,
                rateBasedOnEuro = it.getter.call(rates) as Double
            )
        }
        .toMutableList()
        .apply {
            sortBy { it.isoCode }
            add(0, euroCurrencyModel)
        }

val euroCurrencyModel = Currency(
    isoCode = CurrenciesAdapter.EURO_ISO_CODE,
    rateBasedOnEuro = 1.0
).apply {
    canChangeDisplayedRate = false
}