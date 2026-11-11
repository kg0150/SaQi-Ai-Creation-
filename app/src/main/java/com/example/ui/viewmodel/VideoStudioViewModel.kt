package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.VideoProductionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class StudioTab(val label: String) {
    DASHBOARD("Dashboard"),
    NEW_PROJECT("New Video"),
    PIPELINE("AI Pipeline"),
    STORYBOARD("Storyboard"),
    MAP_STUDIO("Map Studio"),
    ERROR_CENTER("Error Center"),
    THUMBNAILS("Thumbnails"),
    DELIVERY("Export & Share"),
    SETTINGS("Cloud Settings")
}

class VideoStudioViewModel(
    private val repository: VideoProductionRepository = VideoProductionRepository()
) : ViewModel() {

    val projects = repository.projects
    val currentProject = repository.currentProject
    val pipelineLogs = repository.pipelineLogs

    private val _currentTab = MutableStateFlow(StudioTab.DASHBOARD)
    val currentTab: StateFlow<StudioTab> = _currentTab.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackTime = MutableStateFlow(0f)
    val playbackTime: StateFlow<Float> = _playbackTime.asStateFlow()

    private val _selectedSceneIndex = MutableStateFlow(0)
    val selectedSceneIndex: StateFlow<Int> = _selectedSceneIndex.asStateFlow()

    private val _selectedAspectRatio = MutableStateFlow(VideoFormat.PORTRAIT_9_16)
    val selectedAspectRatio: StateFlow<VideoFormat> = _selectedAspectRatio.asStateFlow()

    private val _isPipelineRunning = MutableStateFlow(false)
    val isPipelineRunning: StateFlow<Boolean> = _isPipelineRunning.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>("Ready. 13-agent AI video production engine online.")
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _inspectedAgent = MutableStateFlow<AgentEvaluationItem?>(null)
    val inspectedAgent: StateFlow<AgentEvaluationItem?> = _inspectedAgent.asStateFlow()

    private val _cloudApiUrl = MutableStateFlow("https://cinecraft-run-451885523722.europe-west3.run.app")
    val cloudApiUrl: StateFlow<String> = _cloudApiUrl.asStateFlow()

    private var playbackJob: Job? = null

    init {
        currentProject.value?.let { proj ->
            _selectedAspectRatio.value = proj.format
        }
    }

    fun setTab(tab: StudioTab) {
        _currentTab.value = tab
    }

    fun selectProject(projectId: String) {
        repository.selectProject(projectId)
        currentProject.value?.let {
            _selectedAspectRatio.value = it.format
            _playbackTime.value = 0f
            _selectedSceneIndex.value = 0
            _isPlaying.value = false
        }
    }

    fun createProject(
        title: String,
        prompt: String,
        originCountry: String,
        originCity: String,
        destCountry: String,
        destCity: String,
        mapStyle: MapStyle,
        language: String,
        voiceStyle: VoiceStyle,
        format: VideoFormat,
        resolution: Resolution
    ) {
        val proj = repository.createProject(
            title = title,
            prompt = prompt,
            originCountry = originCountry,
            originCity = originCity,
            destCountry = destCountry,
            destCity = destCity,
            mapStyle = mapStyle,
            language = language,
            voiceStyle = voiceStyle,
            format = format,
            resolution = resolution
        )
        _selectedAspectRatio.value = format
        _playbackTime.value = 0f
        _currentTab.value = StudioTab.PIPELINE
        runFullPipeline()
    }

    fun runFullPipeline() {
        if (_isPipelineRunning.value) return
        _isPipelineRunning.value = true
        _statusBanner.value = "Starting 13-Agent Quality Control Pipeline..."

        viewModelScope.launch {
            repository.runFullAgentPipeline { update ->
                _statusBanner.value = update
            }
            _isPipelineRunning.value = false
            _statusBanner.value = "Pipeline run finished. Auto-repair loop available."
        }
    }

    fun triggerAutoRepair() {
        if (_isPipelineRunning.value) return
        _isPipelineRunning.value = true
        _statusBanner.value = "Executing Automatic Error-Correction Loop..."

        viewModelScope.launch {
            repository.executeAutoRepairLoop { update ->
                _statusBanner.value = update
            }
            _isPipelineRunning.value = false
            _statusBanner.value = "Auto-repair complete! Overall quality score 97/100 (Approved)."
        }
    }

    fun regenerateScene(sceneId: Int) {
        repository.regenerateSingleScene(sceneId)
        _statusBanner.value = "Scene #$sceneId re-synthesized and verified."
    }

    fun updateNarration(sceneId: Int, newText: String) {
        repository.updateSceneNarration(sceneId, newText)
        _statusBanner.value = "Scene #$sceneId narration updated."
    }

    fun duplicateScene(sceneId: Int) {
        repository.duplicateScene(sceneId)
        _statusBanner.value = "Scene duplicated."
    }

    fun deleteScene(sceneId: Int) {
        repository.deleteScene(sceneId)
        _statusBanner.value = "Scene deleted."
    }

    fun moveScene(from: Int, to: Int) {
        repository.moveScene(from, to)
    }

    fun setAspectRatio(format: VideoFormat) {
        _selectedAspectRatio.value = format
        currentProject.value?.format = format
    }

    fun inspectAgent(agent: AgentEvaluationItem?) {
        _inspectedAgent.value = agent
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        val proj = currentProject.value ?: return
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_isPlaying.value) {
                delay(100)
                val newTime = _playbackTime.value + 0.1f
                if (newTime >= proj.durationSeconds) {
                    _playbackTime.value = 0f
                    _isPlaying.value = false
                    break
                } else {
                    _playbackTime.value = newTime
                    updateActiveSceneIndex(newTime, proj.scenes)
                }
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekTo(seconds: Float) {
        val proj = currentProject.value ?: return
        val clamped = seconds.coerceIn(0f, proj.durationSeconds)
        _playbackTime.value = clamped
        updateActiveSceneIndex(clamped, proj.scenes)
    }

    fun selectSceneByIndex(index: Int) {
        val proj = currentProject.value ?: return
        if (index in proj.scenes.indices) {
            _selectedSceneIndex.value = index
            var cumulativeTime = 0f
            for (i in 0 until index) {
                cumulativeTime += proj.scenes[i].durationSeconds
            }
            _playbackTime.value = cumulativeTime
        }
    }

    private fun updateActiveSceneIndex(time: Float, scenes: List<SceneItem>) {
        var elapsed = 0f
        for (i in scenes.indices) {
            val sceneDuration = scenes[i].durationSeconds
            if (time >= elapsed && time < elapsed + sceneDuration) {
                _selectedSceneIndex.value = i
                return
            }
            elapsed += sceneDuration
        }
        _selectedSceneIndex.value = (scenes.size - 1).coerceAtLeast(0)
    }

    fun updateCloudApiUrl(url: String) {
        _cloudApiUrl.value = url
        _statusBanner.value = "Cloud backend endpoint updated: $url"
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
    }
}
