package com.spyrdonapps.currencyconverter.data.model

import java.util.Locale

data class CurrencyUiModel(
    val isoCode: String,
    val rateBasedOnEuro: Double,
    val fullName: String
) {
    val flagImageUrl = "https://fxtop.com/ico/${isoCode.toLowerCase(Locale.ROOT)}.gif"
}