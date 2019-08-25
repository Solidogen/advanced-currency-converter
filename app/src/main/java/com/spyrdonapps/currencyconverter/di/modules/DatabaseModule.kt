package com.spyrdonapps.currencyconverter.di.modules

import android.content.Context
import androidx.room.Room
import com.spyrdonapps.currencyconverter.data.local.CurrencyDao
import com.spyrdonapps.currencyconverter.data.local.CurrencyDatabase
import com.spyrdonapps.currencyconverter.di.qualifiers.ApplicationContext
import com.spyrdonapps.currencyconverter.di.scopes.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
abstract class DatabaseModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @ApplicationScope
        fun database(@ApplicationContext context: Context): CurrencyDatabase =
            Room.databaseBuilder(context, CurrencyDatabase::class.java, CurrencyDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()

        @JvmStatic
        @Provides
        @ApplicationScope
        fun currencyDao(database: CurrencyDatabase): CurrencyDao = database.currencyDao()
    }
}