package com.example.data.model

enum class MediaType {
    VIDEO, AUDIO
}

enum class DownloadStatus {
    QUEUED, DOWNLOADING, PAUSED, COMPLETED, FAILED
}

enum class AdPlacement {
    HOME_BANNER, VIDEO_PLAYER_SIDE, MUSIC_PLAYER_BOTTOM, VIDEO_LIBRARY_CARD, NATIVE_FEED
}

enum class EqualizerPreset(val displayName: String, val bandGains: List<Float>) {
    FLAT("Flat", listOf(0f, 0f, 0f, 0f, 0f)),
    BASS_BOOST("Bass Boost", listOf(8f, 5f, 1f, 0f, 0f)),
    ROCK("Rock", listOf(5f, 3f, -1f, 3f, 6f)),
    POP("Pop", listOf(-1f, 2f, 5f, 2f, -1f)),
    JAZZ("Jazz", listOf(4f, 2f, -2f, 2f, 5f)),
    CLASSICAL("Classical", listOf(5f, 3f, -1f, 2f, 4f)),
    HIP_HOP("Hip Hop", listOf(7f, 4f, 0f, 3f, 4f)),
    VOCAL("Vocal Booster", listOf(-2f, 1f, 6f, 3f, -1f))
}

enum class AspectRatioMode(val label: String) {
    FIT("Fit Screen"),
    FILL("Crop to Fill"),
    SIXTEEN_NINE("16:9 Cinema"),
    FOUR_THREE("4:3 Classic")
}

data class SubtitleCue(
    val index: Int,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val text: String
)

data class MediaItem(
    val id: String,
    val title: String,
    val artist: String = "OmniPlay Studio",
    val durationMs: Long = 180000L,
    val mediaType: MediaType = MediaType.VIDEO,
    val uri: String = "",
    val thumbnailUrl: String = "",
    val resolution: String = "1080p 60fps",
    val format: String = "MP4",
    val sizeBytes: Long = 48500000L,
    val genre: String = "Cinematic",
    val album: String = "Original Productions",
    val lastPositionMs: Long = 0L,
    val isFavorite: Boolean = false,
    val lyrics: String = "",
    val srtSubtitles: List<SubtitleCue> = emptyList()
)

data class DownloadTask(
    val id: String,
    val title: String,
    val sourceUrl: String,
    val targetFormat: String = "MP4",
    val qualityLabel: String = "1080p Full HD",
    val totalBytes: Long = 65000000L,
    val downloadedBytes: Long = 0L,
    val speedBytesPerSec: Long = 2500000L,
    val status: DownloadStatus = DownloadStatus.DOWNLOADING,
    val etaSeconds: Int = 18,
    val timestamp: Long = System.currentTimeMillis(),
    val filePath: String = "/storage/emulated/0/Download/OmniPlay/"
) {
    val progress: Float
        get() = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f) else 0f
}

data class AdCampaign(
    val id: String,
    val title: String,
    val brandName: String,
    val targetUrl: String,
    val imageUrl: String,
    val videoUrl: String = "",
    val headline: String,
    val bodyText: String,
    val callToAction: String = "Learn More",
    val placement: AdPlacement = AdPlacement.HOME_BANNER,
    val rotationIntervalSec: Int = 12,
    val active: Boolean = true,
    val impressions: Long = 0L,
    val clicks: Long = 0L,
    val cpm: Float = 2.50f
)

data class EqualizerState(
    val preset: EqualizerPreset = EqualizerPreset.BASS_BOOST,
    val bands: List<Float> = listOf(8f, 5f, 1f, 0f, 0f), // 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz (-12 to +12)
    val bassBoost: Float = 65f, // 0 to 100
    val virtualizer3D: Float = 40f, // 0 to 100
    val enabled: Boolean = true
)

data class TranscriptSegment(
    val speaker: String,
    val timestamp: String,
    val text: String
)

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "ai"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class StorageStats(
    val totalGb: Float = 128f,
    val usedGb: Float = 48.2f,
    val videosGb: Float = 18.4f,
    val audioGb: Float = 7.6f,
    val downloadsGb: Float = 8.9f,
    val cacheJunkMb: Float = 480f,
    val freeGb: Float = 79.8f
)
