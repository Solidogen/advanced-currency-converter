package com.spyrdonapps.currencyconverter.ui

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.spyrdonapps.currencyconverter.data.remote.CurrencyService
import com.spyrdonapps.currencyconverter.util.interval
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(private val currencyService: CurrencyService) : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)
    private val onClearedRelay: PublishRelay<Unit> = PublishRelay.create()

    // TODO change this back
//    init {
//        loadData()
//    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    fun loadData() {
        // TODO inject schedulers for testing
        Timber.e("loadData")
        scope.launch(Dispatchers.IO) {
            interval(periodMs = INTERVAL_CHECK_PERIOD_MS).consumeEach {
                launchLoadData()
            }
        }
    }

    // TODO get event, result classes and use them with livedata
    private suspend fun launchLoadData() {
        Timber.e("launchLoadData")
        try {
            val items = withContext(Dispatchers.IO) {
                currencyService.getCurrencies()
            }.also {
                Timber.d(it.toString())
                // todo this already works, serialize it
            }

//            view?.showList(items.toUiModel().also { list ->
//                Timber.d(list.toString())
//            })
        } catch (e: Exception) {
            Timber.e(e)
//            view?.showError()
        }
    }

//    private fun CurrenciesResponse.toUiModel(): List<YoutubeVideo> {
//        return items
//            .map { item ->
//                YoutubeVideo(
//                    item.snippet.title,
//                    item.snippet.description,
//                    item.snippet.thumbnails.default.url
//                )
//            }
//            .sortedBy { it.description.count() }
//    }

    override fun onCleared() {
        onClearedRelay.accept(Unit)
        job.cancel()
    }

    companion object {
        const val INTERVAL_CHECK_PERIOD_MS = 1000L
    }
}