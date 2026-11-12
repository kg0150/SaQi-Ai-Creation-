package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.OmniRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class OmniNavigationItem(val label: String) {
    HOME("Home"),
    DOWNLOADS("Download"),
    MUSIC("Music"),
    VIDEO("Video"),
    STUDIO("Studio"),
    AI_ASSISTANT("AI Hub"),
    FILES("Files")
}

class OmniViewModel(
    private val repository: OmniRepository = OmniRepository()
) : ViewModel() {

    // Active Navigation
    private val _currentNav = MutableStateFlow(OmniNavigationItem.HOME)
    val currentNav: StateFlow<OmniNavigationItem> = _currentNav.asStateFlow()

    // Sub-screens or active modals
    private val _showNewDownloadDialog = MutableStateFlow(false)
    val showNewDownloadDialog: StateFlow<Boolean> = _showNewDownloadDialog.asStateFlow()

    private val _showEqualizerDialog = MutableStateFlow(false)
    val showEqualizerDialog: StateFlow<Boolean> = _showEqualizerDialog.asStateFlow()

    private val _showSleepTimerDialog = MutableStateFlow(false)
    val showSleepTimerDialog: StateFlow<Boolean> = _showSleepTimerDialog.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Repository Flows
    val mediaList = repository.mediaList
    val downloads = repository.downloads
    val campaigns = repository.campaigns

    val currentTrack = repository.currentTrack
    val isMusicPlaying = repository.isMusicPlaying
    val musicPositionMs = repository.musicPositionMs
    val isShuffle = repository.isShuffle
    val repeatMode = repository.repeatMode
    val equalizerState = repository.equalizerState
    val sleepTimerMinutesLeft = repository.sleepTimerMinutesLeft

    val currentVideo = repository.currentVideo
    val isVideoPlaying = repository.isVideoPlaying
    val videoPositionMs = repository.videoPositionMs
    val videoSpeed = repository.videoSpeed
    val subtitlesEnabled = repository.subtitlesEnabled
    val aspectRatioMode = repository.aspectRatioMode
    val isPipActive = repository.isPipActive
    val brightness = repository.brightness
    val volume = repository.volume

    val storageStats = repository.storageStats
    val aiChatMessages = repository.aiChatMessages
    val isAiProcessing = repository.isAiProcessing

    // Navigation
    fun navigateTo(item: OmniNavigationItem) {
        _currentNav.value = item
    }

    fun openNewDownloadDialog() {
        _showNewDownloadDialog.value = true
    }

    fun closeNewDownloadDialog() {
        _showNewDownloadDialog.value = false
    }

    fun openEqualizerDialog() {
        _showEqualizerDialog.value = true
    }

    fun closeEqualizerDialog() {
        _showEqualizerDialog.value = false
    }

    fun openSleepTimerDialog() {
        _showSleepTimerDialog.value = true
    }

    fun closeSleepTimerDialog() {
        _showSleepTimerDialog.value = false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Downloads
    fun submitDownload(url: String, title: String, format: String, quality: String) {
        repository.startDownload(url, title, format, quality)
        closeNewDownloadDialog()
    }

    fun pauseDownload(id: String) = repository.pauseDownload(id)
    fun resumeDownload(id: String) = repository.resumeDownload(id)
    fun cancelDownload(id: String) = repository.cancelDownload(id)

    // Music
    fun playTrack(track: MediaItem) {
        repository.playTrack(track)
        _currentNav.value = OmniNavigationItem.MUSIC
    }

    fun toggleMusicPlayPause() = repository.toggleMusicPlayPause()
    fun nextTrack() = repository.nextTrack()
    fun previousTrack() = repository.previousTrack()
    fun seekMusic(posMs: Long) = repository.seekMusic(posMs)
    fun toggleShuffle() = repository.toggleShuffle()
    fun toggleRepeat() = repository.toggleRepeat()
    fun setEqualizerPreset(preset: EqualizerPreset) = repository.setEqualizerPreset(preset)
    fun updateEqualizerBand(index: Int, gain: Float) = repository.updateEqualizerBand(index, gain)
    fun updateBassBoost(value: Float) = repository.updateBassBoost(value)
    fun updateVirtualizer(value: Float) = repository.updateVirtualizer(value)
    fun setSleepTimer(minutes: Int) = repository.setSleepTimer(minutes)
    fun toggleFavorite(id: String) = repository.toggleFavorite(id)

    // Video
    fun playVideo(video: MediaItem) {
        repository.playVideo(video)
        _currentNav.value = OmniNavigationItem.VIDEO
    }

    fun toggleVideoPlayPause() = repository.toggleVideoPlayPause()
    fun seekVideo(posMs: Long) = repository.seekVideo(posMs)
    fun setVideoSpeed(speed: Float) = repository.setVideoSpeed(speed)
    fun toggleSubtitles() = repository.toggleSubtitles()
    fun setAspectRatio(mode: AspectRatioMode) = repository.setAspectRatio(mode)
    fun togglePip() = repository.togglePip()
    fun updateBrightness(delta: Float) = repository.updateBrightness(delta)
    fun updateVolume(delta: Float) = repository.updateVolume(delta)

    // Advertising
    fun onAdImpression(campaignId: String) = repository.recordAdImpression(campaignId)
    fun onAdClick(campaignId: String) = repository.recordAdClick(campaignId)

    // Cleaner & File Management
    fun cleanCache() = repository.cleanJunkCache()
    fun deleteMedia(id: String) = repository.deleteMedia(id)

    // AI
    fun sendAiPrompt(prompt: String) = repository.sendAiPrompt(prompt)
}
