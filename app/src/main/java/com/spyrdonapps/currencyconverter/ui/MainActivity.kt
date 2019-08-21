package com.spyrdonapps.currencyconverter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.data.model.CurrencyUiModel
import com.spyrdonapps.currencyconverter.util.state.Result
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

/*
TODO
    create conversion app kotlin + offline mode works + screen rotation
    + ui perfect + small things + save recycler position on rotate
    + lots of tests + country flags/emojis
TODO
*/

    private val currenciesAdapter = CurrenciesAdapter()
    private var layoutManagerInstanceState: Parcelable? = null

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        handleSavedInstanceStateIfNeeded(savedInstanceState)
        observeViewModel()

        // todo remove
        viewModel.loadData()
    }

    private fun observeViewModel() {
        viewModel.currenciesLiveData.observe(this, Observer {
            handleCurrenciesState(it)
        })
    }

    private fun handleCurrenciesState(result: Result<List<CurrencyUiModel>>) {
        when (result) {
            is Result.Success -> showList(result.data)
            Result.Loading -> showLoading()
            is Result.Error -> showError()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = currenciesAdapter
        }
    }

    private fun handleSavedInstanceStateIfNeeded(savedInstanceState: Bundle?) {
        savedInstanceState?.getParcelable<Parcelable>(LAYOUT_MANAGER_INSTANCE_STATE_TAG)?.let { state: Parcelable ->
            layoutManagerInstanceState = state
        }
    }

    private fun showList(list: List<CurrencyUiModel>) {
        progressBar.isVisible = false
        currenciesAdapter.submitList(list)

        // todo probably will need submit list listener because diff is on bg thread, check for list adapter callbacks or recycler listeners
        restoreRecyclerPositionIfNeeded()
    }

    private fun showLoading() {
        progressBar.isVisible = true
    }

    private fun showError() {
        progressBar.isVisible = false
        AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage("")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun restoreRecyclerPositionIfNeeded() {
        layoutManagerInstanceState?.let {
            recyclerView.layoutManager?.onRestoreInstanceState(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(LAYOUT_MANAGER_INSTANCE_STATE_TAG, recyclerView.layoutManager?.onSaveInstanceState())
    }

    companion object {
        const val LAYOUT_MANAGER_INSTANCE_STATE_TAG = "LLM"
    }
}
