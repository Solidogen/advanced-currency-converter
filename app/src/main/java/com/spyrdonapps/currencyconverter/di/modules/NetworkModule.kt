package com.spyrdonapps.currencyconverter.di.modules

import com.spyrdonapps.currencyconverter.data.remote.CurrencyService
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
        fun provideApiService(): CurrencyService = Network.currencyService
    }

    private object Network {

        private const val API_URL = "https://revolut.duckdns.org/"

        private val retrofit = Retrofit.Builder().baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val currencyService: CurrencyService = retrofit.create(CurrencyService::class.java)
    }
}