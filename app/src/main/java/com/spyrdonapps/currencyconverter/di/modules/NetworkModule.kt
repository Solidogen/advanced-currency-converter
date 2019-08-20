package com.spyrdonapps.currencyconverter.di.modules

import com.spyrdonapps.currencyconverter.data.remote.ApiService
import com.spyrdonapps.currencyconverter.di.scopes.ApplicationScope
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
abstract class NetworkModule {

    @Module
    companion object {

        @ApplicationScope
        @Provides
        @JvmStatic
        fun provideApiService(): ApiService = Network.apiService
    }
}

private object Network {

    private const val API_URL = "https://revolut.duckdns.org/"

    private val retrofit = Retrofit.Builder().baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService : ApiService = retrofit.create(ApiService::class.java)
}