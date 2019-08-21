package com.spyrdonapps.currencyconverter.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.spyrdonapps.currencyconverter.MyApp
import com.spyrdonapps.currencyconverter.di.qualifiers.ApplicationContext
import com.spyrdonapps.currencyconverter.di.scopes.ApplicationScope
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class AppModule {

    @Binds
    abstract fun application(myApp: MyApp): Application

    @Module
    companion object {

        @Provides
        @ApplicationScope
        @JvmStatic
        fun sharedPreferences(application: Application): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

        @Provides
        @ApplicationScope
        @JvmStatic
        @ApplicationContext
        fun provideAppContext(app: Application): Context = app.applicationContext
    }
}