package com.spyrdonapps.currencyconverter.ui

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.util.state.Result
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import javax.inject.Inject
import androidx.recyclerview.widget.SimpleItemAnimator

class MainActivity : AppCompatActivity() {

/*
TODO
    create conversion app kotlin:
    calculate all rates based on first rate multiplier
    ui perfect
    lots of tests
*/
    private val currenciesAdapter = CurrenciesAdapter()
    private var layoutManagerInstanceState: Parcelable? = null
    private var wasNetworkErrorShown = false

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupRecyclerView()
        handleSavedInstanceStateIfNeeded(savedInstanceState)
        observeViewModel()
    }

    private fun setupToolbar() {
        supportActionBar?.setTitle(R.string.toolbar_text)
    }

    private fun observeViewModel() {
        viewModel.currenciesLiveData.observe(this, Observer {
            handleCurrenciesState(it)
        })
    }

    private fun handleCurrenciesState(result: Result<List<Currency>>) {
        when (result) {
            is Result.Success -> showList(result.data)
            Result.Loading -> showLoading()
            is Result.Error -> showError(result.exception)
        }
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = currenciesAdapter
            // todo commented for testing, uncomment when done debugging
//            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
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
        currenciesAdapter.setData(list)
        restoreRecyclerPositionIfNeeded()
    }

    private fun showLoading() {
        progressBar.isVisible = true
    }

    private fun showError(exception: Exception) {
        // todo ditch this flag and alert, simply show "sync off" icon in the corner
        if (wasNetworkErrorShown) {
            return
        }
        progressBar.isVisible = false
        wasNetworkErrorShown = true

        AlertDialog.Builder(this)
            .setTitle(if (exception is IOException) R.string.network_error else R.string.error)
            .setMessage("")
            .setPositiveButton("OK", null)
            .show()
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
