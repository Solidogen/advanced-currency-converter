package com.spyrdonapps.currencyconverter.data.repository

import com.spyrdonapps.currencyconverter.data.local.CurrencyDao
import com.spyrdonapps.currencyconverter.data.mappers.toCurrencyList
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.remote.CurrencyService
import java.io.IOException

class AppCurrencyRepository(private val currencyService: CurrencyService, private val currencyDao: CurrencyDao) : CurrencyRepository {

    override suspend fun getCurrenciesFromRemote(): List<Currency> {
        currencyService.getCurrenciesResponse().toCurrencyList().let { list ->
            currencyDao.saveCurrencies(list)
            return list
        }
    }

    override suspend fun getCurrenciesFromCache(): List<Currency> {
        val cachedCurrencies = currencyDao.getCurrencies()
        if (cachedCurrencies.isEmpty()) {
            throw IllegalArgumentException("No available currencies")
        }
        return cachedCurrencies
    }
}