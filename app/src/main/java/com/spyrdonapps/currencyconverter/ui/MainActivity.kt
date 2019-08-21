package com.spyrdonapps.currencyconverter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.spyrdonapps.currencyconverter.R
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

    val viewModel: MainViewModel by lazy {
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

        // todo remove
        viewModel.loadData()
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = currenciesAdapter
        }
    }

    private fun handleSavedInstanceStateIfNeeded(savedInstanceState: Bundle?) {
        savedInstanceState?.getParcelable<Parcelable>(LAYOUT_MANAGER_INSTANCE_STATE_TAG)?.let {
            layoutManagerInstanceState = it
        }
    }

    // todo implement normal mvvm flow
//    fun showList(list: List<YoutubeVideo>) {
//        progressBar.isVisible = false
//        youtubeVideoAdapter.setItems(list)
//        restoreRecyclerPositionIfNeeded()
//    }

    // todo call on list get
    private fun restoreRecyclerPositionIfNeeded() {
        layoutManagerInstanceState?.let {
            recyclerView.layoutManager?.onRestoreInstanceState(it)
        }
    }

    // todo implement normal mvvm flow
//    fun showError() {
//        progressBar.isVisible = false
//        AlertDialog.Builder(this)
//            .setTitle(R.string.error)
//            .setMessage("")
//            .setPositiveButton("OK", null)
//            .show()
//    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(LAYOUT_MANAGER_INSTANCE_STATE_TAG, recyclerView.layoutManager?.onSaveInstanceState())
    }

    companion object {
        const val LAYOUT_MANAGER_INSTANCE_STATE_TAG = "LLM"
    }
}
