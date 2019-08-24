package com.spyrdonapps.currencyconverter.ui

import android.os.Bundle
import android.os.Parcelable
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.ui.base.BaseActivity
import com.spyrdonapps.currencyconverter.util.extensions.hideKeyboard
import com.spyrdonapps.currencyconverter.util.state.Result
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>() {

    override val layoutId = R.layout.activity_main
    override val viewModelType = MainViewModel::class.java

    private val currenciesAdapter = CurrenciesAdapter()
    private var layoutManagerInstanceState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        handleSavedInstanceStateIfNeeded(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    private fun setupToolbar() {
        supportActionBar?.setTitle(R.string.toolbar_text)
    }

    override fun observeViewModel() {
        viewModel.currenciesLiveData.observe(this, Observer {
            handleCurrenciesState(it)
        })
    }

    private fun handleCurrenciesState(result: Result<List<Currency>>) {
        when (result) {
            is Result.Success -> showList(result.data)
            Result.Loading -> showLoading()
            is Result.Error -> showError(result.data)
        }
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = currenciesAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun handleSavedInstanceStateIfNeeded(savedInstanceState: Bundle?) {
        savedInstanceState?.getParcelable<Parcelable>(LAYOUT_MANAGER_INSTANCE_STATE_TAG)
            ?.let { state: Parcelable ->
                layoutManagerInstanceState = state
            }
    }

    private fun showList(list: List<Currency>) {
        progressBar.isVisible = false
        syncOffImageView.isVisible = false
        currenciesAdapter.setData(list)
        restoreRecyclerPositionIfNeeded()
    }

    private fun showLoading() {
        progressBar.isVisible = true
        syncOffImageView.isVisible = false
    }

    private fun showError(data: List<Currency>?) {
        progressBar.isVisible = false
        syncOffImageView.isVisible = true
        data?.let {
            currenciesAdapter.setData(it)
        }
        restoreRecyclerPositionIfNeeded()
    }

    private fun restoreRecyclerPositionIfNeeded() {
        layoutManagerInstanceState?.let {
            recyclerView.layoutManager?.onRestoreInstanceState(it)
            layoutManagerInstanceState = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(
            LAYOUT_MANAGER_INSTANCE_STATE_TAG,
            recyclerView.layoutManager?.onSaveInstanceState()
        )
    }

    companion object {
        const val LAYOUT_MANAGER_INSTANCE_STATE_TAG = "LLM"
    }
}
