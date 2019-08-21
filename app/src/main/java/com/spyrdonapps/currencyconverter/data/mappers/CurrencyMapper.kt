package com.spyrdonapps.currencyconverter.data.mappers

import com.spyrdonapps.currencyconverter.data.model.CurrencyUiModel
import com.spyrdonapps.currencyconverter.data.remote.CurrenciesResponse

fun CurrenciesResponse.toUiModel(): List<CurrencyUiModel> {
//        return items
//            .map { item ->
//                YoutubeVideo(
//                    item.snippet.title,
//                    item.snippet.description,
//                    item.snippet.thumbnails.default.url
//                )
//            }
//            .sortedBy { it.description.count() }
    return listOf(
        CurrencyUiModel("ASD", "ASDASD ASD", 2.02),
        CurrencyUiModel("ASD", "ASDASD ASD", 2.02),
        CurrencyUiModel("ASD", "ASDASD ASD", 2.02),
        CurrencyUiModel("ASD", "ASDASD ASD", 2.02)
    )
}