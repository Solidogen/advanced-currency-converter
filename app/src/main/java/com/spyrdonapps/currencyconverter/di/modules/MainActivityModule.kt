package com.spyrdonapps.currencyconverter.di.modules

import android.app.Activity
import com.spyrdonapps.currencyconverter.ui.MainActivity
import dagger.Binds
import dagger.Module

@Module
abstract class MainActivityModule {

    @Binds
    abstract fun mainActivity(activity: MainActivity): Activity
}