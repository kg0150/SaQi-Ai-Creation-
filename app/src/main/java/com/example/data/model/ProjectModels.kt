package com.example.data.model

enum class ProjectStatus(val label: String) {
    DRAFT("Draft"),
    PROCESSING("Processing"),
    QA_REVIEW("QA Review"),
    NEEDS_FIX("Needs Fix"),
    AUTO_FIXING("Auto-Fixing"),
    RENDERING("Rendering"),
    APPROVED("Approved"),
    COMPLETED("Completed"),
    FAILED("Failed")
}

enum class VideoFormat(val label: String, val aspectRatio: Float, val badge: String) {
    PORTRAIT_9_16("9:16 (Shorts/TikTok/Reels)", 9f / 16f, "9:16"),
    LANDSCAPE_16_9("16:9 (YouTube/Desktop)", 16f / 9f, "16:9"),
    SQUARE_1_1("1:1 (Instagram/Feed)", 1f, "1:1")
}

enum class Resolution(val label: String) {
    RES_720P("720p HD"),
    RES_1080P("1080p Full HD"),
    RES_4K("4K Ultra HD")
}

enum class MapStyle(val label: String) {
    CINEMATIC("Cinematic Golden"),
    SATELLITE("Satellite Earth"),
    TERRAIN("3D Topo Terrain"),
    DARK("Cyber Dark Minimal"),
    LIGHT("Cartographic Light"),
    NEWS("Broadcasting News"),
    DOCUMENTARY("National Geographic Doc"),
    MAP_3D("Interactive 3D Horizon")
}

enum class VoiceStyle(val label: String) {
    CINEMATIC("Cinematic Narrator"),
    NEWS("News Anchor"),
    DOCUMENTARY("Deep Documentary"),
    ENERGETIC("Energetic Social"),
    CALM("Calm & Poetic"),
    EMOTIONAL("Dramatic & Emotional")
}

enum class AgentStatus(val label: String) {
    PENDING("Pending"),
    RUNNING("Running..."),
    PASSED("Passed"),
    FLAGGED("Flagged"),
    AUTO_FIXING("Auto-Fixing"),
    APPROVED("Approved")
}

data class SceneItem(
    val id: Int,
    var title: String,
    var durationSeconds: Float = 4.5f,
    var narration: String,
    var visualDescription: String,
    var cameraMovement: String = "Slow Zoom In",
    var transition: String = "Cross Dissolve",
    var soundEffect: String = "Cinematic Whoosh",
    var imagePrompt: String,
    var subtitleText: String,
    val isMapScene: Boolean = false,
    val mapOrigin: String? = null,
    val mapDestination: String? = null,
    var status: String = "Ready"
)

data class MapConfig(
    val enabled: Boolean = true,
    var originCountry: String = "Pakistan",
    var originCity: String = "Islamabad",
    var destinationCountry: String = "Saudi Arabia",
    var destinationCity: String = "Riyadh",
    var style: MapStyle = MapStyle.CINEMATIC,
    var showFlightPath: Boolean = true,
    var showRadarPulse: Boolean = true,
    var showCountryLabels: Boolean = true,
    var showDirectionArrow: Boolean = true
)

data class VoiceConfig(
    var language: String = "English",
    var languageCode: String = "en-US",
    var voiceGender: String = "Male",
    var voiceStyle: VoiceStyle = VoiceStyle.CINEMATIC,
    var speed: Float = 1.0f,
    var pitch: Float = 1.0f,
    val pronunciationDict: MutableMap<String, String> = mutableMapOf("Riyadh" to "/rɪˈjɑːd/")
)

data class AgentEvaluationItem(
    val agentId: Int,
    val agentName: String,
    val description: String,
    var status: AgentStatus,
    var score: Int,
    var feedback: String,
    val detectedErrors: MutableList<String> = mutableListOf(),
    var correctionInstructions: String? = null,
    val affectedSceneId: Int? = null
)

data class QualityScores(
    var scriptQuality: Int = 95,
    var voiceQuality: Int = 94,
    var visualQuality: Int = 96,
    var mapAccuracy: Int = 98,
    var subtitleQuality: Int = 97,
    var audioQuality: Int = 93,
    var editingQuality: Int = 95,
    var thumbnailQuality: Int = 96,
    var overallQuality: Int = 96
) {
    fun isApproved(): Boolean = overallQuality >= 90
}

data class ThumbnailConceptItem(
    val id: Int,
    val title: String,
    val visualConcept: String,
    val headlineText: String,
    val prompt: String,
    val composition: String,
    val aspectRatio: String = "16:9"
)

data class DeliveryPackage(
    val finalVideoUrl: String,
    val voiceoverMp3Url: String,
    val subtitlesSrtUrl: String,
    val suggestedTitles: List<String>,
    val seoDescription: String,
    val hashtags: List<String>,
    val youtubeTags: List<String>,
    val keywords: List<String>,
    val shortCaption: String
)

data class VideoProject(
    val id: String,
    var title: String,
    val prompt: String,
    var status: ProjectStatus,
    val createdAt: String,
    var durationSeconds: Float,
    var format: VideoFormat = VideoFormat.PORTRAIT_9_16,
    var resolution: Resolution = Resolution.RES_1080P,
    val scenes: MutableList<SceneItem>,
    val agentEvaluations: MutableList<AgentEvaluationItem>,
    var qualityScores: QualityScores,
    val mapConfig: MapConfig,
    val voiceConfig: VoiceConfig,
    val thumbnailConcepts: List<ThumbnailConceptItem>,
    var deliveryPackage: DeliveryPackage? = null
)
