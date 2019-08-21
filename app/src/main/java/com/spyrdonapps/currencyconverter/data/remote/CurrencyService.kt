package com.spyrdonapps.currencyconverter.data.remote

import retrofit2.http.GET

interface CurrencyService {

    @GET("latest?base=EUR")
    suspend fun getCurrencies(): CurrenciesResponse
}