package com.spyrdonapps.currencyconverter.di.modules

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import com.spyrdonapps.currencyconverter.di.MyViewModelFactory
import com.spyrdonapps.currencyconverter.ui.MainActivity
import dagger.Binds
import dagger.Module

@Module
abstract class MainActivityModule {

    @Binds
    abstract fun mainActivity(activity: MainActivity): Activity

    @Binds
    abstract fun bindViewModelFactory(factory: MyViewModelFactory): ViewModelProvider.Factory
}