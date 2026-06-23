package com.frank.anim.ui.kmp

import com.frank.anim.network.ApiClient
import com.frank.anim.network.model.Photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

data class KmpTestUiState(
    val isLoading: Boolean = false,
    val photos: List<Photo> = emptyList(),
    val errorMessage: String? = null,
    val token: String? = null,
    val isUsingCache: Boolean = false
)

class KmpTestViewModel(
    private val apiClient: ApiClient,
    private val scope: CoroutineScope = MainScope()
) {

    private val job = Job()
    private val viewModelScope = scope + job
    private val _state = MutableStateFlow(KmpTestUiState(isLoading = true))
    val state: StateFlow<KmpTestUiState> = _state

    private var cachedPhotos: List<Photo> = emptyList()
    private var refreshSequence: Int = 0

    init {
        refresh()
    }

    fun refresh(limit: Int = 12) {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val photos = apiClient.fetchDemoPhotos(limit)
            if (photos.isNotEmpty()) {
                cachedPhotos = photos
                refreshSequence += 1
                _state.value = KmpTestUiState(
                    isLoading = false,
                    photos = photos,
                    token = "demo-token-$refreshSequence",
                    isUsingCache = false
                )
            } else if (cachedPhotos.isNotEmpty()) {
                _state.value = KmpTestUiState(
                    isLoading = false,
                    photos = cachedPhotos,
                    token = "cached-demo-session",
                    isUsingCache = true
                )
            } else {
                _state.value = KmpTestUiState(
                    isLoading = false,
                    errorMessage = "加载图片失败，请检查网络连接后重试"
                )
            }
        }
    }

    fun clear() {
        job.cancel()
    }
}
