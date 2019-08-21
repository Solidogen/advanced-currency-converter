package com.spyrdonapps.currencyconverter.data.local

import com.spyrdonapps.currencyconverter.data.mappers.toCurrencyList
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.data.remote.CurrencyService
import com.spyrdonapps.currencyconverter.data.repository.CurrencyRepository

class AppCurrencyRepository(private val currencyService: CurrencyService, private val currencyDao: CurrencyDao) : CurrencyRepository {

    override suspend fun getCurrencies(): List<Currency> {
        return try {
            currencyService.getCurrenciesResponse().toCurrencyList()
        } catch (e: Exception) {
            currencyDao.getCurrencies()
        }
    }

    override suspend fun saveCurrencies(list: List<Currency>) {
        currencyDao.saveCurrencies(list)
    }
}