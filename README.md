# CineCraft AI — Cloud-Based AI Video Production Platform

[![Android](https://img.shields.io/badge/Platform-Android%20%7C%20Web-brightgreen.svg)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-blue.svg)](https://developer.android.com/jetpack/compose)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-purple.svg)](https://kotlinlang.org)
[![FastAPI](https://img.shields.io/badge/Backend-FastAPI%20%7C%20Python%203.11-teal.svg)](https://fastapi.tiangolo.com)
[![Docker](https://img.shields.io/badge/Cloud-Docker%20%7C%20Cloud%20Run-blue.svg)](https://cloud.google.com/run)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

CineCraft AI is a cloud-based AI video production platform built for mobile creators and desktop power users. It features an automated **13-agent quality control pipeline**, a **self-healing error-correction loop** with bounded retries, a **cinematic 3D/2D flight-path map studio**, **25+ global languages with pronunciation dictionary**, and a complete **social media delivery package** (video stems, audio narration, SRT/VTT subtitles, viral titles, tags, hashtags, and CTR-optimized thumbnails).

---

## 1. System Architecture

```
                      ┌──────────────────────────────────────────────┐
                      │  CineCraft Android / Mobile Studio (M3 UI)  │
                      │  - Jetpack Compose Edge-to-Edge Responsive   │
                      │  - Bezier Flight Path Animated Map Canvas    │
                      │  - Multi-aspect Video Preview (9:16, 16:9, 1:1)│
                      └──────────────────────┬───────────────────────┘
                                             │ REST / WebSocket
                      ┌──────────────────────▼───────────────────────┐
                      │  Cloud Gateway (FastAPI / Node.js Proxy)     │
                      └───────┬───────────────────────────────┬──────┘
                              │                               │
           ┌──────────────────▼───────────────┐  ┌────────────▼────────────────┐
           │ 13-Agent Quality Control Pipeline│  │ Asynchronous Rendering Worker│
           │  1. Director Agent               │  │  - FFmpeg Engine / MoviePy  │
           │  2. Script Agent                 │  │  - 3D/2D Trajectory Math    │
           │  3. Fact Check Agent             │  │  - Subtitle Burning & Timing │
           │  4. Language Agent (25+ langs)   │  │  - Dynamic Audio Ducking    │
           │  5. Voice-over QA Agent          │  └────────────┬─────────────────┘
           │  6. Visual QA Agent              │               │
           │  7. Map QA Agent                 │  ┌────────────▼─────────────────┐
           │  8. Subtitle QA Agent            │  │ Cloud Storage (GCS / S3)     │
           │  9. Audio QA Agent               │  │  - MP4 Video (1080p / 4K)    │
           │ 10. Video Editor Agent           │  │  - Narration Master (.mp3)   │
           │ 11. Social Media QA Agent        │  │  - Captions (.srt / .vtt)    │
           │ 12. Thumbnail QA Agent           │  │  - Cover Art (16:9 & 9:16)   │
           │ 13. Final Master QA Gatekeeper   │  └──────────────────────────────┘
           └──────────────────┬───────────────┘
                              │
           ┌──────────────────▼────────────────────────────────┐
           │ Self-Healing Automated Error-Correction Loop      │
           │  Flag Issue ➔ Identify Agent ➔ Surgical Re-gen    │
           │  - Bounded Retries: Max 3 Attempts                │
           │  - 300s Operation Timeout Guard                  │
           │  - Unlocks "APPROVED" (>=90/100 threshold)        │
           └───────────────────────────────────────────────────┘
```

---

## 2. 13 Specialized AI Production & QA Agents

| # | Agent Name | Domain & Scope | Quality Metric Enforced |
|---|---|---|---|
| **1** | **Director Agent** | Understands core idea, sets pacing, genre, structure, scene breakdown | Concept clarity & scene pacing |
| **2** | **Script Agent** | Authors narrative voiceover with vivid hooks, natural pauses | Narrative flow & hook strength |
| **3** | **Fact Check Agent** | Verifies geographic coordinates, historical dates, national boundaries | Factual accuracy & verification |
| **4** | **Language Agent** | Phonetic mapping, regional dialects, grammar across 25+ languages | Pronunciation & translation fidelity |
| **5** | **Voice-over QA Agent** | Audits audio waveforms against script, cadence, syllable timing | Speech intelligibility & sync |
| **6** | **Visual QA Agent** | Inspects visual renders for anatomical flaws, lighting consistency | Photorealism & visual coherence |
| **7** | **Map QA Agent** | Validates trajectory curves, waypoints, country borders, radar pulses | Cartographic & flight-path accuracy |
| **8** | **Subtitle QA Agent** | Enforces 32-character maximum safe-area margins and word highlighting | Safe-area compliance & readability |
| **9** | **Audio QA Agent** | Balances speech ducking (-18dB under dialogue), frequency spectrum | LUFS loudness normalization (-14 LUFS) |
| **10** | **Video Editor Agent** | Balances hook impact (< 2.5s), cut points, and cinematic transitions | Pacing, transitions, visual rhythm |
| **11** | **Social Media QA Agent** | Audits safe-zones for YouTube Shorts, TikTok, Instagram Reels, Feed | Aspect ratio & platform UI safe zones |
| **12** | **Thumbnail QA Agent** | Evaluates focal hierarchy, typography contrast, mobile CTR legibility | High-CTR composition & contrast |
| **13** | **Final Master QA Gatekeeper** | Comprehensive 8-category audit; enforces strict **90/100** threshold | Final approval & export package sign-off |

---

## 3. Self-Healing Quality Control & Retry Limits

The multi-agent pipeline is protected against infinite loops and silent failures:
1. **Bounded Auto-Repair Retries**: Configured with `MAX_AUTO_REPAIR_RETRIES = 3`. If an issue cannot be resolved within 3 attempts, the pipeline halts gracefully and transitions to `NEEDS_FIX` with actionable error diagnostics.
2. **Surgical Regeneration**: When an error is flagged in a specific scene (e.g. Scene 4 pronunciation of "Riyadh"), the system:
   - Updates the pronunciation dictionary (`"Riyadh"` to `"/rɪˈjɑːd/"`).
   - Targets Agent 5 (Voice-over QA) and regenerates **only Scene 4's audio stem**.
   - Preserves all other rendered scenes and assets.
3. **Approval Threshold**: The project status only transitions to `APPROVED` / `READY` once Agent 13 gives an overall score of **90/100 or higher**.

---

## 4. Mobile Responsiveness & Android Design

The Android client is built with **Jetpack Compose Material 3** adhering to modern Android standards:
- **Edge-to-Edge Experience**: Uses `enableEdgeToEdge()` with proper window insets handling.
- **Adaptive Screen Layouts**: Dynamic layout adaptation for compact smartphones (portrait), foldables, and tablets.
- **Touch Targets**: All buttons, chips, sliders, and navigation tabs meet or exceed the **48dp x 48dp** Android Accessibility guideline.
- **Predictive Back Navigation**: `BackHandler` integrated throughout sub-screens (Storyboard, Map Studio, Error Center, Delivery Package).
- **Dark/Light Mode M3 Palette**: Premium dark theme with `#0B0F19` deep sapphire surface and `#6366F1` indigo accents.

---

## 5. Technology Stack

- **Android Application**:
  - Language: Kotlin 2.2.0
  - Framework: Jetpack Compose with Material Design 3 (M3)
  - Target SDK: Android 16 (API 36) | Minimum SDK: Android 7.0 (API 24)
  - Image Loading: Coil Compose
  - Navigation: Compose Navigation with type-safe state
  - Async: Kotlin Coroutines & StateFlow
- **Web / Preview Gateway**:
  - Node.js 18+ / Express
  - Interactive Single Page Application (SPA) with responsive dashboard
  - Full REST API mirroring backend endpoints
- **Cloud Backend & Rendering**:
  - Python 3.11 with FastAPI & Pydantic
  - FFmpeg 6.0+ & MoviePy for video stitching, subtitle burn-in, audio ducking
  - Multi-stage Docker container optimized for Cloud Run
  - Google Cloud Storage / AWS S3 asset delivery

---

## 6. Environment Variables Setup

Configuration is managed via environment variables. Copy `.env.example` to `.env`:

```bash
cp .env.example .env
```

| Variable | Description | Default / Example |
|---|---|---|
| `APP_ENV` | Application environment | `production` |
| `PORT` | Web server / API port | `8080` (or `3000` for Node) |
| `RENDER_WORKER_TIMEOUT_SECONDS` | Maximum rendering job duration | `300` |
| `MAX_AUTO_REPAIR_RETRIES` | Max automated retry attempts | `3` |
| `MAX_CONCURRENT_RENDERS` | Concurrency limit per worker instance | `5` |
| `STORAGE_BUCKET_NAME` | Cloud storage bucket URI | `gs://cinecraft-cloud-assets` |
| `STORAGE_PUBLIC_BASE_URL` | CDN / public storage base URL | `https://storage.googleapis.com/...` |
| `CLOUD_RUN_API_URL` | Cloud Run backend service URL | `https://cinecraft-run-...run.app` |
| `GEMINI_API_KEY` | Optional AI key (via Secrets panel in AI Studio) | Injected via BuildConfig |

> **Security Note**: Never commit `.env` or keystore files to source control. They are ignored in `.gitignore`.

---

## 7. Android Build & APK Generation

### Debug APK Build
To build the debug APK locally:
```bash
gradle :app:assembleDebug
```
The output APK is generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK Build
To build the release APK for distribution:
```bash
export KEYSTORE_PATH="/path/to/your/upload-keystore.jks"
export KEYSTORE_PASSWORD="your-keystore-password"
export KEY_ALIAS="your-key-alias"
export KEY_PASSWORD="your-key-password"

gradle :app:assembleRelease
```
The output APK is generated at:
```
app/build/outputs/apk/release/app-release.apk
```

### Running Unit Tests
Execute the local JVM test suite:
```bash
gradle :app:testDebugUnitTest
```

---

## 8. Cloud Backend Deployment

### Local Docker Build & Run
```bash
# Build multi-stage Docker image with FFmpeg
docker build -t cinecraft-backend .

# Run container on port 8080
docker run -p 8080:8080 -e PORT=8080 --env-file .env cinecraft-backend
```

### Deploy to Google Cloud Run
```bash
# Submit build to Google Cloud Build
gcloud builds submit --tag gcr.io/PROJECT_ID/cinecraft-backend:latest

# Deploy to Cloud Run with 2 vCPUs and 4GB memory for rendering
gcloud run deploy cinecraft-backend \
  --image gcr.io/PROJECT_ID/cinecraft-backend:latest \
  --platform managed \
  --region europe-west3 \
  --allow-unauthenticated \
  --memory 4Gi \
  --cpu 2 \
  --timeout 300
```

---

## 9. Troubleshooting & FAQ

1. **Preview is stuck / blank in browser**:
   - CineCraft includes `server.js` running on port 3000 which feeds the Nginx proxy on port 8080.
   - Run `curl -I http://localhost:8080/` to verify HTTP 200 status.
   - If port 3000 is occupied, restart with `node server.js &`.

2. **Android Build: "package not found"**:
   - Dependencies are centrally managed via `gradle/libs.versions.toml`. Run `compile_applet` or `gradle :app:assembleDebug` to resolve Gradle dependencies.

3. **Auto-Repair Loop Limit**:
   - If an edge-case video prompt fails repeatedly, check the **Error Center** (`NEEDS_FIX` status). The automated repair halts at 3 attempts to prevent unbounded compute usage. You can manually tweak the script in Storyboard and retry.

---

## 10. License & Support

CineCraft AI is proprietary software. All rights reserved.
For inquiries and cloud enterprise hosting, contact support@cinecraft.ai.
