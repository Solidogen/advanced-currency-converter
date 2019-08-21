package com.spyrdonapps.currencyconverter.di.components

import com.spyrdonapps.currencyconverter.MyApp
import com.spyrdonapps.currencyconverter.di.modules.ActivityBuildersModule
import com.spyrdonapps.currencyconverter.di.modules.AppModule
import com.spyrdonapps.currencyconverter.di.modules.DatabaseModule
import com.spyrdonapps.currencyconverter.di.modules.NetworkModule
import com.spyrdonapps.currencyconverter.di.modules.RepositoryModule
import com.spyrdonapps.currencyconverter.di.scopes.ApplicationScope
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@ApplicationScope
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        RepositoryModule::class,
        ActivityBuildersModule::class
    ]
)
interface MyAppComponent : AndroidInjector<MyApp> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MyApp>()
}