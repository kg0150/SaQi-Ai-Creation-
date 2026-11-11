package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class VideoProductionRepository {

    private val _projects = MutableStateFlow<List<VideoProject>>(emptyList())
    val projects: StateFlow<List<VideoProject>> = _projects.asStateFlow()

    private val _currentProject = MutableStateFlow<VideoProject?>(null)
    val currentProject: StateFlow<VideoProject?> = _currentProject.asStateFlow()

    private val _pipelineLogs = MutableStateFlow<List<String>>(emptyList())
    val pipelineLogs: StateFlow<List<String>> = _pipelineLogs.asStateFlow()

    init {
        // Initialize with default flagship project: "Pakistan to Saudi Arabia"
        val initialProject = buildDefaultProject()
        _projects.value = listOf(initialProject)
        _currentProject.value = initialProject
    }

    fun selectProject(projectId: String) {
        val found = _projects.value.find { it.id == projectId }
        if (found != null) {
            _currentProject.value = found
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
    ): VideoProject {
        val mapConfig = MapConfig(
            originCountry = originCountry,
            originCity = originCity,
            destinationCountry = destCountry,
            destinationCity = destCity,
            style = mapStyle
        )
        val voiceConfig = VoiceConfig(
            language = language,
            voiceStyle = voiceStyle
        )
        val scenes = generateScenesForPrompt(originCountry, originCity, destCountry, destCity, format)
        val agents = create13AgentsList(originCity, destCity)
        val thumbnails = createThumbnailsList(originCountry, destCountry)

        val totalDuration = scenes.sumOf { it.durationSeconds.toDouble() }.toFloat()
        val newProj = VideoProject(
            id = UUID.randomUUID().toString().take(8),
            title = if (title.isNotBlank()) title else "Journey: $originCountry to $destCountry",
            prompt = prompt,
            status = ProjectStatus.PROCESSING,
            createdAt = "Just now",
            durationSeconds = totalDuration,
            format = format,
            resolution = resolution,
            scenes = scenes.toMutableList(),
            agentEvaluations = agents.toMutableList(),
            qualityScores = QualityScores(overallQuality = 89, voiceQuality = 87),
            mapConfig = mapConfig,
            voiceConfig = voiceConfig,
            thumbnailConcepts = thumbnails,
            deliveryPackage = createDeliveryPackage(originCountry, destCountry, format, resolution)
        )

        _projects.value = listOf(newProj) + _projects.value
        _currentProject.value = newProj
        autoRepairAttempts = 0
        return newProj
    }

    private var autoRepairAttempts = 0
    private val maxAutoRepairAttempts = 3

    suspend fun runFullAgentPipeline(onStepUpdate: (String) -> Unit) {
        val current = _currentProject.value ?: return
        current.status = ProjectStatus.PROCESSING
        val logs = mutableListOf<String>()

        try {
            for (agent in current.agentEvaluations) {
                agent.status = AgentStatus.RUNNING
                onStepUpdate("Running Agent ${agent.agentId}: ${agent.agentName}...")
                logs.add("[AGENT ${agent.agentId}] ${agent.agentName} analyzing project constraints...")
                _pipelineLogs.value = logs.toList()
                delay(280)

                if (agent.agentId == 5) {
                    // Agent 5: Voice QA detects phonetic issue on Scene 4
                    agent.status = AgentStatus.FLAGGED
                    agent.score = 87
                    agent.detectedErrors.clear()
                    agent.detectedErrors.add("Scene 4: Pronunciation stress on '${current.mapConfig.destinationCity}' accented")
                    agent.correctionInstructions = "Apply phonetic phoneme /rɪˈjɑːd/ to Scene 4 synthesizer."
                    logs.add("⚠️ [ALERT] Voice QA Agent flagged Scene 4 pronunciation inconsistency.")
                } else if (agent.agentId == 13) {
                    // Master QA Gatekeeper checks all previous agents
                    agent.status = AgentStatus.FLAGGED
                    agent.score = 89
                    agent.feedback = "Overall score 89/100 below 90 threshold due to Voice QA flag on Scene 4. Dispatching auto-repair."
                    logs.add("❌ [MASTER QA] Project flagged (Score 89 < 90). Auto-repair required.")
                } else {
                    agent.status = AgentStatus.PASSED
                    logs.add("✓ [PASSED] ${agent.agentName} verified (Score: ${agent.score}/100)")
                }
                _pipelineLogs.value = logs.toList()
            }

            current.qualityScores = QualityScores(overallQuality = 89, voiceQuality = 87)
            current.status = ProjectStatus.NEEDS_FIX
            _currentProject.value = current
        } catch (e: Exception) {
            logs.add("❌ [PIPELINE ERROR] Execution encountered unexpected state: ${e.localizedMessage ?: "Unknown error"}")
            _pipelineLogs.value = logs.toList()
            current.status = ProjectStatus.FAILED
            _currentProject.value = current
            onStepUpdate("Pipeline execution failed. Please retry.")
        }
    }

    suspend fun executeAutoRepairLoop(onProgress: (String) -> Unit) {
        val current = _currentProject.value ?: return
        val logs = _pipelineLogs.value.toMutableList()

        if (autoRepairAttempts >= maxAutoRepairAttempts) {
            onProgress("Maximum auto-repair retries ($maxAutoRepairAttempts) reached. Manual review required.")
            logs.add("⚠️ [LIMIT REACHED] Auto-repair halted after $maxAutoRepairAttempts attempts to prevent infinite retry loop.")
            _pipelineLogs.value = logs.toList()
            current.status = ProjectStatus.NEEDS_FIX
            _currentProject.value = current
            return
        }

        autoRepairAttempts++
        current.status = ProjectStatus.AUTO_FIXING

        try {
            onProgress("Isolating affected asset: Scene 4 Voice-Over (Attempt $autoRepairAttempts/$maxAutoRepairAttempts)...")
            logs.add("🔄 [AUTO-REPAIR #$autoRepairAttempts] Isolating Scene 4 voiceover stem. Keeping Scenes 1, 2, 3, 5 cached.")
            _pipelineLogs.value = logs.toList()
            delay(400)

            onProgress("Applying phonetic correction /rɪˈjɑːd/...")
            logs.add("🔧 [PHONETICS] Applied pronunciation dictionary override: '${current.mapConfig.destinationCity}' -> /rɪˈjɑːd/")
            _pipelineLogs.value = logs.toList()
            delay(400)

            // Fix Scene 4
            val scene4 = current.scenes.find { it.id == 4 }
            scene4?.status = "Voice Re-synthesized & In-Phase"

            onProgress("Agent 5 (Voice QA) re-evaluating Scene 4...")
            val voiceAgent = current.agentEvaluations.find { it.agentId == 5 }
            voiceAgent?.status = AgentStatus.APPROVED
            voiceAgent?.score = 98
            voiceAgent?.feedback = "Pronunciation verified natural, phonetically accurate, and synced with visual timeline."
            voiceAgent?.detectedErrors?.clear()
            voiceAgent?.correctionInstructions = null
            logs.add("✓ [VOICE QA] Scene 4 re-audit PASSED. Score elevated to 98/100.")
            _pipelineLogs.value = logs.toList()
            delay(350)

            onProgress("Agent 13 (Master QA Gatekeeper) performing final audit...")
            val masterAgent = current.agentEvaluations.find { it.agentId == 13 }
            masterAgent?.status = AgentStatus.APPROVED
            masterAgent?.score = 97
            masterAgent?.feedback = "All 12 production modules approved. Overall quality score 97/100 exceeds 90 threshold."
            masterAgent?.detectedErrors?.clear()
            logs.add("🎉 [MASTER QA] FINAL APPROVAL GRANTED! Overall Quality: 97/100 (Threshold: 90).")
            _pipelineLogs.value = logs.toList()
            delay(300)

            current.qualityScores = QualityScores(
                scriptQuality = 96,
                voiceQuality = 98,
                visualQuality = 97,
                mapAccuracy = 99,
                subtitleQuality = 98,
                audioQuality = 96,
                editingQuality = 96,
                thumbnailQuality = 97,
                overallQuality = 97
            )
            current.status = ProjectStatus.APPROVED
            _currentProject.value = current
        } catch (e: Exception) {
            logs.add("❌ [AUTO-REPAIR ERROR] Failed to complete auto-repair: ${e.localizedMessage ?: "Unknown error"}")
            _pipelineLogs.value = logs.toList()
            current.status = ProjectStatus.NEEDS_FIX
            _currentProject.value = current
            onProgress("Auto-repair failed. Please try again.")
        }
    }

    fun regenerateSingleScene(sceneId: Int) {
        val current = _currentProject.value ?: return
        val scene = current.scenes.find { it.id == sceneId }
        if (scene != null) {
            scene.status = "Regenerated & Verified"
            _currentProject.value = current
        }
    }

    fun updateSceneNarration(sceneId: Int, newNarration: String) {
        val current = _currentProject.value ?: return
        val scene = current.scenes.find { it.id == sceneId }
        if (scene != null) {
            scene.narration = newNarration
            scene.subtitleText = newNarration
            _currentProject.value = current
        }
    }

    fun duplicateScene(sceneId: Int) {
        val current = _currentProject.value ?: return
        val scene = current.scenes.find { it.id == sceneId }
        if (scene != null) {
            val newId = (current.scenes.maxOfOrNull { it.id } ?: 0) + 1
            val duplicate = scene.copy(
                id = newId,
                title = "${scene.title} (Copy)"
            )
            current.scenes.add(duplicate)
            current.durationSeconds += duplicate.durationSeconds
            _currentProject.value = current
        }
    }

    fun deleteScene(sceneId: Int) {
        val current = _currentProject.value ?: return
        if (current.scenes.size > 1) {
            current.scenes.removeAll { it.id == sceneId }
            current.durationSeconds = current.scenes.sumOf { it.durationSeconds.toDouble() }.toFloat()
            _currentProject.value = current
        }
    }

    fun moveScene(fromIndex: Int, toIndex: Int) {
        val current = _currentProject.value ?: return
        if (fromIndex in current.scenes.indices && toIndex in current.scenes.indices) {
            val item = current.scenes.removeAt(fromIndex)
            current.scenes.add(toIndex, item)
            _currentProject.value = current
        }
    }

    private fun buildDefaultProject(): VideoProject {
        val originCountry = "Pakistan"
        val originCity = "Islamabad"
        val destCountry = "Saudi Arabia"
        val destCity = "Riyadh"
        val format = VideoFormat.PORTRAIT_9_16
        val resolution = Resolution.RES_1080P

        val scenes = generateScenesForPrompt(originCountry, originCity, destCountry, destCity, format)
        val agents = create13AgentsList(originCity, destCity)
        val thumbnails = createThumbnailsList(originCountry, destCountry)

        val totalDuration = scenes.sumOf { it.durationSeconds.toDouble() }.toFloat()

        return VideoProject(
            id = "cinemap-pk-sa",
            title = "Pakistan to Saudi Arabia: Cinematic Flight",
            prompt = "Create a cinematic map video showing Pakistan to Saudi Arabia with 3D flight trajectory, historical commentary, and orchestral soundtrack.",
            status = ProjectStatus.NEEDS_FIX,
            createdAt = "Today at 09:42",
            durationSeconds = totalDuration,
            format = format,
            resolution = resolution,
            scenes = scenes.toMutableList(),
            agentEvaluations = agents.toMutableList(),
            qualityScores = QualityScores(overallQuality = 89, voiceQuality = 87),
            mapConfig = MapConfig(
                originCountry = originCountry,
                originCity = originCity,
                destinationCountry = destCountry,
                destinationCity = destCity,
                style = MapStyle.CINEMATIC
            ),
            voiceConfig = VoiceConfig(
                language = "English",
                languageCode = "en-US",
                voiceStyle = VoiceStyle.CINEMATIC
            ),
            thumbnailConcepts = thumbnails,
            deliveryPackage = createDeliveryPackage(originCountry, destCountry, format, resolution)
        )
    }

    private fun generateScenesForPrompt(
        originCountry: String,
        originCity: String,
        destCountry: String,
        destCity: String,
        format: VideoFormat
    ): List<SceneItem> {
        val aspectBadge = if (format == VideoFormat.PORTRAIT_9_16) "9:16 vertical mobile" else "16:9 cinematic"
        return listOf(
            SceneItem(
                id = 1,
                title = "Departure Establishing Shot",
                durationSeconds = 4.5f,
                narration = "From the rugged mountains and storied heritage of $originCountry, an unforgettable voyage begins.",
                visualDescription = "Sweeping golden hour aerial view over $originCity, dawn mist rising between mountain ridges.",
                cameraMovement = "Slow Orbit In",
                transition = "Cross Dissolve",
                soundEffect = "Jet Engine & Rising Ambient Synth",
                imagePrompt = "Ultra-cinematic 8k establishing aerial of $originCity skyline at golden hour, volumetric lighting, photorealistic, $aspectBadge",
                subtitleText = "From the storied heritage of $originCountry, an unforgettable voyage begins.",
                isMapScene = false
            ),
            SceneItem(
                id = 2,
                title = "3D Animated Map Trajectory",
                durationSeconds = 6.0f,
                narration = "Ascending above the clouds, our vector traces across the Arabian Sea, charting directly toward $destCountry.",
                visualDescription = "Glowing golden trajectory line traveling from $originCity to $destCity over 3D terrain globe with radar beacons.",
                cameraMovement = "Dynamic Cartographic Flyover",
                transition = "Whip Pan",
                soundEffect = "Futuristic Radar Pulse & Flight Glide",
                imagePrompt = "3D digital cartography satellite map with glowing neon trajectory connecting $originCountry to $destCountry, $aspectBadge",
                subtitleText = "Ascending above the clouds, charting directly toward $destCountry.",
                isMapScene = true,
                mapOrigin = originCity,
                mapDestination = destCity
            ),
            SceneItem(
                id = 3,
                title = "Arabian Desert Vista",
                durationSeconds = 5.0f,
                narration = "Crossing into the Arabian Peninsula, endless golden sand dunes stretch as far as the eye can see.",
                visualDescription = "High-altitude cinematic panorama over undulating desert dunes glowing in deep amber sunlight.",
                cameraMovement = "Wide Forward Tracking",
                transition = "Light Leak Glint",
                soundEffect = "Warm Wind Rush & Cello Swell",
                imagePrompt = "National Geographic style aerial landscape over vast golden desert dunes, IMAX color grade, $aspectBadge",
                subtitleText = "Endless golden sand dunes stretch across the horizon.",
                isMapScene = false
            ),
            SceneItem(
                id = 4,
                title = "Descent into Destination",
                durationSeconds = 5.5f,
                narration = "Descending into $destCity, where ancient cultural legacy converges with hyper-modern architectural wonder.",
                visualDescription = "Sunset view of $destCity skyline, Kingdom Centre and illuminated glass skyscrapers glowing in dusk light.",
                cameraMovement = "Slow Tilt & Reveal",
                transition = "Smooth Zoom Cut",
                soundEffect = "Cinematic Orchestral Swell",
                imagePrompt = "Stunning twilight skyline of $destCity, illuminated skyscrapers and historic citadel, photorealistic, $aspectBadge",
                subtitleText = "Descending into $destCity, where legacy meets the future.",
                isMapScene = false,
                status = "Flagged: Pronunciation Check"
            ),
            SceneItem(
                id = 5,
                title = "Final Arrival & Outro",
                durationSeconds = 4.0f,
                narration = "A bridge between nations, faith, and shared destiny.",
                visualDescription = "Grand wide establishing shot with animated telemetry graphic showing total distance traveled.",
                cameraMovement = "Slow Pull Out",
                transition = "Fade to Black",
                soundEffect = "Deep Cinematic Sub Bass Hit",
                imagePrompt = "Warm illuminated airport runway lights fading into distant city lights, bokeh blur, $aspectBadge",
                subtitleText = "A bridge between nations, faith, and shared destiny.",
                isMapScene = false
            )
        )
    }

    private fun create13AgentsList(originCity: String, destCity: String): List<AgentEvaluationItem> {
        return listOf(
            AgentEvaluationItem(
                agentId = 1,
                agentName = "Director Agent",
                description = "Concept, video style, audience targeting, pacing structure",
                status = AgentStatus.PASSED,
                score = 97,
                feedback = "Cinematic travel documentary pacing established. Strong 2.5-second retention hook."
            ),
            AgentEvaluationItem(
                agentId = 2,
                agentName = "Script Agent",
                description = "Narration writing, logical flow, avoiding redundancy",
                status = AgentStatus.PASSED,
                score = 95,
                feedback = "Narration phrasing is vivid, evocative, and calibrated for rhythmic voice delivery."
            ),
            AgentEvaluationItem(
                agentId = 3,
                agentName = "Fact Check Agent",
                description = "Validating names, places, dates, coordinates, geography",
                status = AgentStatus.PASSED,
                score = 99,
                feedback = "Geographic route from $originCity to $destCity verified across verified satellite coordinates."
            ),
            AgentEvaluationItem(
                agentId = 4,
                agentName = "Language Agent",
                description = "Grammar, natural phonetics, translation fidelity",
                status = AgentStatus.PASSED,
                score = 96,
                feedback = "Syntax and vocabulary adhere to professional documentary broadcast standard."
            ),
            AgentEvaluationItem(
                agentId = 5,
                agentName = "Voice-over QA Agent",
                description = "Word match, pronunciation check, pitch/speed synchronization",
                status = AgentStatus.FLAGGED,
                score = 87,
                feedback = "Phonetic stress on '$destCity' in Scene 4 requires custom pronunciation override.",
                detectedErrors = mutableListOf("Scene 4: Pronunciation stress on '$destCity' slightly accented"),
                correctionInstructions = "Apply phonetic phoneme /rɪˈjɑːd/ to Scene 4 synthesizer.",
                affectedSceneId = 4
            ),
            AgentEvaluationItem(
                agentId = 6,
                agentName = "Visual QA Agent",
                description = "Scene artifacts, lighting, distortion, correct objects & environment",
                status = AgentStatus.PASSED,
                score = 96,
                feedback = "Verified: Zero AI anatomical defects or lighting inconsistencies detected across all scenes."
            ),
            AgentEvaluationItem(
                agentId = 7,
                agentName = "Map QA Agent",
                description = "Borders, flight routes, city labels, navigation vectors",
                status = AgentStatus.PASSED,
                score = 98,
                feedback = "Accurate great-circle bezier arc between origin and destination waypoints."
            ),
            AgentEvaluationItem(
                agentId = 8,
                agentName = "Subtitle QA Agent",
                description = "Timestamp sync, safe area margins, readability",
                status = AgentStatus.PASSED,
                score = 96,
                feedback = "Text margins within mobile safe-zone (under 32 chars/line, safe from TikTok/Reels UI)."
            ),
            AgentEvaluationItem(
                agentId = 9,
                agentName = "Audio QA Agent",
                description = "Voice vs BGM ducking, audio clipping, silence detection",
                status = AgentStatus.PASSED,
                score = 94,
                feedback = "Background orchestral stem ducked at -18dB under speech. Zero digital clipping."
            ),
            AgentEvaluationItem(
                agentId = 10,
                agentName = "Video Editor Agent",
                description = "Hook strength, scene pacing, visual variety, cinematic transitions",
                status = AgentStatus.PASSED,
                score = 95,
                feedback = "Scene transitions align seamlessly with orchestral downbeats."
            ),
            AgentEvaluationItem(
                agentId = 11,
                agentName = "Social Media QA Agent",
                description = "Platform aspect ratio, retention curve, safe zones",
                status = AgentStatus.PASSED,
                score = 97,
                feedback = "Optimized for high CTR and viewer retention on Shorts, Reels, and TikTok."
            ),
            AgentEvaluationItem(
                agentId = 12,
                agentName = "Thumbnail QA Agent",
                description = "Focal point contrast, typography clarity, mobile legibility",
                status = AgentStatus.PASSED,
                score = 95,
                feedback = "High contrast focal title. Readable at small smartphone icon scale."
            ),
            AgentEvaluationItem(
                agentId = 13,
                agentName = "Final Master QA Agent",
                description = "Comprehensive gatekeeper audit across all 12 domains. Threshold >= 90",
                status = AgentStatus.FLAGGED,
                score = 89,
                feedback = "Project overall score 89/100 is below the 90 minimum threshold due to Voice QA flag on Scene 4.",
                detectedErrors = mutableListOf("Scene 4 voice synthesis requires pronunciation re-generation."),
                affectedSceneId = 4
            )
        )
    }

    private fun createThumbnailsList(originCountry: String, destCountry: String): List<ThumbnailConceptItem> {
        return listOf(
            ThumbnailConceptItem(
                id = 1,
                title = "Cinematic Golden Arc",
                visualConcept = "Glowing gold trajectory arcing over illuminated 3D globe connecting $originCountry and $destCountry",
                headlineText = "THE EPIC FLIGHT",
                prompt = "Ultra-detailed 3D globe with glowing gold laser flight path from $originCountry to $destCountry, deep space backdrop, 8k",
                composition = "Diagonal leading line, bold amber headline at top",
                aspectRatio = "16:9"
            ),
            ThumbnailConceptItem(
                id = 2,
                title = "Split Heritage Vista",
                visualConcept = "Split frame showcasing $originCountry historic monuments on left and $destCountry futuristic skyline on right",
                headlineText = "$originCountry ➔ $destCountry",
                prompt = "Split screen dynamic composite, golden hour contrast, cinematic lighting",
                composition = "Symmetrical split with glowing divider line",
                aspectRatio = "16:9"
            ),
            ThumbnailConceptItem(
                id = 3,
                title = "Mobile Vertical Radar",
                visualConcept = "Top-down cyber map with pulsating radar circles over $originCountry and $destCountry",
                headlineText = "ROUTE REVEALED",
                prompt = "Minimal dark mode digital cartography map, glowing telemetry markers, vertical 9:16",
                composition = "Center focal point with large bold yellow text",
                aspectRatio = "9:16"
            ),
            ThumbnailConceptItem(
                id = 4,
                title = "Aerial Desert Wings",
                visualConcept = "Cinematic airliner wing view soaring above rolling golden sand dunes during sunrise",
                headlineText = "UNSEEN PASSAGE",
                prompt = "Photorealistic aerial view of passenger jet wing over golden dunes, morning rim light",
                composition = "Leading line from jet wing toward horizon",
                aspectRatio = "16:9"
            ),
            ThumbnailConceptItem(
                id = 5,
                title = "Minimal Cyber Vector",
                visualConcept = "High-tech flight simulator telemetry HUD with flight duration and altitude badges",
                headlineText = "DISTANCE & TIME",
                prompt = "Futuristic flight telemetry HUD interface, cyan and amber data points on dark obsidian",
                composition = "Technical grid layout with central navigation vector",
                aspectRatio = "16:9"
            )
        )
    }

    private fun createDeliveryPackage(
        originCountry: String,
        destCountry: String,
        format: VideoFormat,
        resolution: Resolution
    ): DeliveryPackage {
        return DeliveryPackage(
            finalVideoUrl = "https://cloud.cinecraft.ai/renders/cinecraft_master_${resolution.name}.mp4",
            voiceoverMp3Url = "https://cloud.cinecraft.ai/stems/narration_master.mp3",
            subtitlesSrtUrl = "https://cloud.cinecraft.ai/captions/subtitles_en.srt",
            suggestedTitles = listOf(
                "1. Journey Across Continents: $originCountry to $destCountry in 4K Cinematic Map",
                "2. How Close Are They? Flying from $originCountry to $destCountry",
                "3. The Epic Flight Route from $originCountry to $destCountry Explained",
                "4. From Desert to Sky: The Spectacular Voyage from $originCountry to $destCountry",
                "5. Behind the Border: Flight Path from $originCountry to $destCountry Revealed"
            ),
            seoDescription = "Experience a cinematic 3D map animation exploring the journey from $originCountry to $destCountry. Rendered in ${resolution.label} with multi-agent AI verification, orchestral audio design, and precise cartographic telemetry. Subscribe for more high-fidelity geographic journeys.",
            hashtags = listOf(
                "#MapAnimation", "#CinematicTravel", "#${originCountry.replace(" ", "")}",
                "#${destCountry.replace(" ", "")}", "#AIVideo", "#TravelDocumentary", "#Shorts"
            ),
            youtubeTags = listOf(
                originCountry, destCountry, "$originCountry to $destCountry", "map animation",
                "flight path", "cinematic travel", "geography documentary", "AI video generator"
            ),
            keywords = listOf(
                "flight route animation", "3d map video", "cinematic cartography",
                "geographic visualizer", "travel documentary"
            ),
            shortCaption = "Epic aerial journey from $originCountry to $destCountry! ✈️🌍 Where should we fly next?"
        )
    }
}
