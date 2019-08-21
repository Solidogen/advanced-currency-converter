package com.spyrdonapps.currencyconverter.data.model

data class CurrencyUiModel(val isoCode: String, val rateBasedOnEuro: Double) {

    // todo maybe get those with mapper as well?

    // would need to map currency->country->flag

    var flagImageUrl: String? = null
    val fullName: String? = null
}