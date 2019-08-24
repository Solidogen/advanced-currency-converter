package com.spyrdonapps.currencyconverter.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjection
import javax.inject.Inject

abstract class BaseActivity<T : BaseViewModel> : AppCompatActivity() {

    protected abstract val layoutId: Int
    protected abstract val viewModelType: Class<T>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModel: T by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(viewModelType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        initializeViewModelIfNeeded()
        observeViewModel()
    }

    private fun initializeViewModelIfNeeded() {
        if (!viewModel.isInitialized) {
            viewModel.initialize()
        }
    }

    protected open fun observeViewModel() {}
}