package com.spyrdonapps.currencyconverter.ui

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.spyrdonapps.currencyconverter.data.remote.CurrencyService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel @Inject constructor(private val currencyService: CurrencyService) : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)
    private val compositeDisposable = CompositeDisposable()
    private val onClearedRelay: PublishRelay<Unit> = PublishRelay.create()

    // TODO change this back
//    init {
//        loadData()
//    }

    fun loadData() {
        // TODO inject schedulers for testing
        Timber.e("loadData")
        Observable.interval(INTERVAL_CHECK_PERIOD_SECONDS, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe {
                scope.launch {
                    launchLoadData()
                }
            }
            .addTo(compositeDisposable)
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
        const val INTERVAL_CHECK_PERIOD_SECONDS = 1L
    }
}