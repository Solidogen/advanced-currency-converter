package com.spyrdonapps.currencyconverter.di.modules

import androidx.lifecycle.ViewModel
import com.spyrdonapps.currencyconverter.di.ViewModelKey
import com.spyrdonapps.currencyconverter.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ActivityViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun mainViewModel(viewModel: MainViewModel): ViewModel
}