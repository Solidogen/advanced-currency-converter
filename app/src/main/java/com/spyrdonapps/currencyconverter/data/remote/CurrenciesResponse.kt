package com.spyrdonapps.currencyconverter.data.remote

data class CurrenciesResponse(
    val base: String,
    val date: String,
    val rates: ApiCurrencies
)