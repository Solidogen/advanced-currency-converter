package com.spyrdonapps.currencyconverter.data.repository

import com.spyrdonapps.currencyconverter.data.model.Currency

interface CurrencyRepository {

    suspend fun getCurrenciesFromRemote(): List<Currency>

    suspend fun getCurrenciesFromCache(): List<Currency>
}