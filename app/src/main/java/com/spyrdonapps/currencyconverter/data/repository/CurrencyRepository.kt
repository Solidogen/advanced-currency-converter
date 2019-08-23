package com.spyrdonapps.currencyconverter.data.repository

import com.spyrdonapps.currencyconverter.data.model.Currency

interface CurrencyRepository {

    suspend fun getCurrencies(): List<Currency>
}