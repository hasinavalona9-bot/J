package com.example.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookmarkItem(
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class WebViewState(
    val currentUrl: String = "https://hayinfo.infinityfreeapp.com",
    val pageTitle: String = "Hay Info",
    val progress: Int = 0,
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isSecure: Boolean = true,
    val isDesktopMode: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null,
    val bookmarks: List<BookmarkItem> = listOf(
        BookmarkItem("Accueil Hay Info", "https://hayinfo.infinityfreeapp.com")
    )
)

class WebViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WebViewState())
    val uiState: StateFlow<WebViewState> = _uiState.asStateFlow()

    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun registerNetworkCallback(context: Context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager = cm

        val builder = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(isOffline = false, errorMessage = null)
                }
            }

            override fun onLost(network: Network) {
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(isOffline = true)
                }
            }
        }

        cm?.registerNetworkCallback(builder.build(), networkCallback!!)

        // Initial check
        val activeNetwork = cm?.activeNetwork
        val caps = cm?.getNetworkCapabilities(activeNetwork)
        val hasInternet = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        _uiState.value = _uiState.value.copy(isOffline = !hasInternet)
    }

    fun unregisterNetworkCallback() {
        networkCallback?.let {
            connectivityManager?.unregisterNetworkCallback(it)
        }
    }

    fun updateUrl(url: String) {
        val isHttps = url.startsWith("https://", ignoreCase = true)
        _uiState.value = _uiState.value.copy(
            currentUrl = url,
            isSecure = isHttps
        )
    }

    fun updatePageTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            pageTitle = if (title.isBlank() || title.startsWith("http")) "Hay Info" else title
        )
    }

    fun updateProgress(progress: Int) {
        _uiState.value = _uiState.value.copy(
            progress = progress,
            isLoading = progress in 1..99
        )
    }

    fun updateNavigationState(canGoBack: Boolean, canGoForward: Boolean) {
        _uiState.value = _uiState.value.copy(
            canGoBack = canGoBack,
            canGoForward = canGoForward
        )
    }

    fun setPageError(error: String?) {
        _uiState.value = _uiState.value.copy(
            errorMessage = error,
            isLoading = false
        )
    }

    fun toggleDesktopMode() {
        val nextMode = !_uiState.value.isDesktopMode
        _uiState.value = _uiState.value.copy(isDesktopMode = nextMode)
    }

    fun toggleBookmark() {
        val currentUrl = _uiState.value.currentUrl
        val currentTitle = _uiState.value.pageTitle
        val existing = _uiState.value.bookmarks.find { it.url == currentUrl }

        val newBookmarks = if (existing != null) {
            _uiState.value.bookmarks.filterNot { it.url == currentUrl }
        } else {
            _uiState.value.bookmarks + BookmarkItem(currentTitle, currentUrl)
        }

        _uiState.value = _uiState.value.copy(bookmarks = newBookmarks)
    }

    fun removeBookmark(bookmark: BookmarkItem) {
        _uiState.value = _uiState.value.copy(
            bookmarks = _uiState.value.bookmarks.filterNot { it.url == bookmark.url }
        )
    }

    fun isCurrentUrlBookmarked(): Boolean {
        return _uiState.value.bookmarks.any { it.url == _uiState.value.currentUrl }
    }
}
