package com.spyrdonapps.currencyconverter.data.repository

import com.spyrdonapps.currencyconverter.data.local.CurrencyDao
import com.spyrdonapps.currencyconverter.data.mappers.toCurrencyList
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.remote.CurrencyService

class AppCurrencyRepository(private val currencyService: CurrencyService, private val currencyDao: CurrencyDao) : CurrencyRepository {

    override suspend fun getCurrencies(): List<Currency> {
        return try {
            val currencyList = currencyService.getCurrenciesResponse().toCurrencyList()
            currencyDao.saveCurrencies(currencyList)
            return currencyList
        } catch (e: Exception) {
            currencyDao.getCurrencies()
        }
    }
}