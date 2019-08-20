package com.spyrdonapps.currencyconverter.ui

import androidx.lifecycle.ViewModel
import com.spyrdonapps.currencyconverter.data.remote.ApiService
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)

    init {
        loadData()
    }

    private fun loadData() {
        scope.launch {
            launchLoadData()
        }
    }

    // TODO get event, result classes and use them with livedata
    private suspend fun launchLoadData() {
        try {
            val items = withContext(Dispatchers.IO) {
                apiService.getResponse()
            }.also {
                Timber.d(it.string())
            }

//            view?.showList(items.toUiModel().also { list ->
//                Timber.d(list.toString())
//            })
        } catch (e: Exception) {
            Timber.e(e)
//            view?.showError()
        }
    }

//    private fun ApiResponse.toUiModel(): List<YoutubeVideo> {
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
        job.cancel()
    }
}