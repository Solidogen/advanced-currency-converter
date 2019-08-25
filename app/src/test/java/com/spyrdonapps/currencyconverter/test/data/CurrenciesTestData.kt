package com.spyrdonapps.currencyconverter.test.data

import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.remote.ApiCurrencies
import com.spyrdonapps.currencyconverter.data.remote.CurrenciesResponse

object CurrenciesTestData {

    val currenciesResponse = CurrenciesResponse(
        "", "", ApiCurrencies(
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0
        )
    )

    val currencies = listOf(
        Currency("EUR", 1.0),
        Currency("AUD", 1.0),
        Currency("BGN", 1.0),
        Currency("BRL", 1.0),
        Currency("CAD", 1.0),
        Currency("CHF", 1.0),
        Currency("CNY", 1.0),
        Currency("CZK", 1.0),
        Currency("DKK", 1.0),
        Currency("GBP", 1.0),
        Currency("HKD", 1.0),
        Currency("HRK", 1.0),
        Currency("HUF", 1.0),
        Currency("IDR", 1.0),
        Currency("ILS", 1.0),
        Currency("INR", 1.0),
        Currency("ISK", 1.0),
        Currency("JPY", 1.0),
        Currency("KRW", 1.0),
        Currency("MXN", 1.0),
        Currency("MYR", 1.0),
        Currency("NOK", 1.0),
        Currency("NZD", 1.0),
        Currency("PHP", 1.0),
        Currency("PLN", 1.0),
        Currency("RON", 1.0),
        Currency("RUB", 1.0),
        Currency("SEK", 1.0),
        Currency("SGD", 1.0),
        Currency("THB", 1.0),
        Currency("TRY", 1.0),
        Currency("USD", 1.0),
        Currency("ZAR", 1.0)
    )
}