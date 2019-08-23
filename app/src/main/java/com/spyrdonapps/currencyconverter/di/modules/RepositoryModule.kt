package com.spyrdonapps.currencyconverter.di.modules

import com.spyrdonapps.currencyconverter.data.repository.AppCurrencyRepository
import com.spyrdonapps.currencyconverter.data.local.CurrencyDao
import com.spyrdonapps.currencyconverter.data.remote.CurrencyService
import com.spyrdonapps.currencyconverter.data.repository.CurrencyRepository
import com.spyrdonapps.currencyconverter.di.scopes.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
abstract class RepositoryModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @ApplicationScope
        fun currencyRepository(service: CurrencyService, dao: CurrencyDao): CurrencyRepository =
            AppCurrencyRepository(service, dao)
    }
}