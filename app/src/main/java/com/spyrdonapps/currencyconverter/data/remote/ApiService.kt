package com.spyrdonapps.currencyconverter.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET

interface ApiService {

    // todo swap with good things
    @GET("latest?base=EUR")
    suspend fun getResponse(): ResponseBody
}