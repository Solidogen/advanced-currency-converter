package com.spyrdonapps.currencyconverter.data.mappers

import com.spyrdonapps.currencyconverter.data.model.CurrencyUiModel
import com.spyrdonapps.currencyconverter.data.remote.CurrenciesResponse
import kotlin.reflect.full.memberProperties

/*
* Custom deserialization in interceptor would be much better,
* but decided to have fun with reflection anyway
* */
fun CurrenciesResponse.toUiModel(): List<CurrencyUiModel> =
    rates::class.memberProperties.map {
        CurrencyUiModel(
            isoCode = it.name,
            rateBasedOnEuro = it.getter.call(rates) as Double
        )
    }