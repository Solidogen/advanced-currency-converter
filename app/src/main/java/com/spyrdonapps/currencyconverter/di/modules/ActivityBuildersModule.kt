package com.spyrdonapps.currencyconverter.di.modules

import com.spyrdonapps.currencyconverter.di.scopes.ActivityScope
import com.spyrdonapps.currencyconverter.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuildersModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun mainActivity(): MainActivity
}