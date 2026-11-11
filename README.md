# CineCraft AI — Cloud-Based AI Video Production Platform

CineCraft AI is an enterprise-grade cloud AI video production platform designed for mobile and desktop creators. It features a **13-agent quality control pipeline**, an **automated error-correction loop**, a **cinematic 3D/2D map journey animation studio**, **25+ global languages with pronunciation dictionary**, and complete **social media export packaging** (video, stems, SRT/VTT captions, 5 titles, SEO, tags, hashtags, and thumbnails).

---

## Architecture Overview

```
                      ┌──────────────────────────────────────────────┐
                      │  CineCraft Android / Mobile Studio (M3 UI)  │
                      └──────────────────────┬───────────────────────┘
                                             │ REST / WebSocket
                      ┌──────────────────────▼───────────────────────┐
                      │  FastAPI Cloud Backend Gateway (GCP / Cloud) │
                      └───────┬───────────────────────────────┬──────┘
                              │                               │
           ┌──────────────────▼───────────────┐  ┌────────────▼────────────────┐
           │ 13-Agent Quality Control Pipeline│  │ Asynchronous Rendering Worker│
           │  1. Director Agent               │  │  - FFmpeg Engine            │
           │  2. Script Agent                 │  │  - MoviePy Composite         │
           │  3. Fact Check Agent             │  │  - 3D/2D Map Flight Trajectory│
           │  4. Language Agent (25+ langs)   │  │  - Subtitle Burning          │
           │  5. Voice-over QA Agent          │  │  - Dynamic Audio Ducking     │
           │  6. Visual QA Agent              │  └────────────┬─────────────────┘
           │  7. Map QA Agent                 │               │
           │  8. Subtitle QA Agent            │  ┌────────────▼─────────────────┐
           │  9. Audio QA Agent               │  │ Cloud Storage (GCS / S3)     │
           │ 10. Video Editor Agent           │  │  - MP4 Video (1080p / 4K)    │
           │ 11. Social Media QA Agent        │  │  - Audio Narration (.mp3)    │
           │ 12. Thumbnail QA Agent           │  │  - Captions (.srt / .vtt)    │
           │ 13. Final Master QA Gatekeeper   │  │  - Thumbnails (16:9 / 9:16)  │
           └──────────────────┬───────────────┘  └──────────────────────────────┘
                              │
           ┌──────────────────▼────────────────────────────────┐
           │ Automated Error-Correction Feedback Loop          │
           │  Find Issue ➔ Identify Agent ➔ Surgical Re-gen   │
           │  (e.g., Scene 4 Voice Pronunciation Re-synthesized│
           │   without re-rendering untouched scenes)          │
           └───────────────────────────────────────────────────┘
```

---

## 13 Specialized AI Production & QA Agents

1. **Director Agent**: Analyzes the creator prompt, defines visual pacing, target demographic, and scene breakdown.
2. **Script Agent**: Authors narrative voiceover with logical flow, vivid imagery, and concise phrasing.
3. **Fact Check Agent**: Verifies coordinates, historical dates, national boundaries, and factual claims.
4. **Language Agent**: Evaluates grammar, phonetics, translation fidelity, and pronunciation pitfalls across 25+ languages.
5. **Voice-over QA Agent**: Verifies word accuracy against the script, pronunciation, inflection, and timecode sync.
6. **Visual QA Agent**: Inspects renders for anatomical errors, AI artifacts, distortions, and lighting consistency.
7. **Map QA Agent**: Validates cartographic accuracy, country borders, trajectory curves, and waypoint labels.
8. **Subtitle QA Agent**: Enforces 32-character maximum safe-area margins, line breaks, and readability.
9. **Audio QA Agent**: Balances dialogue ducking (-18dB under speech), eliminates clipping, and times SFX transients.
10. **Video Editor Agent**: Balances hook strength (first 2.5s), scene durations, cuts, and cinematic transitions.
11. **Social Media QA Agent**: Formats for YouTube, Shorts, TikTok, and Reels with safe-zone compliance.
12. **Thumbnail QA Agent**: Evaluates visual focal hierarchy, typography contrast, and mobile CTR legibility.
13. **Final Master QA Gatekeeper**: Audits all 12 domains. Enforces a strict minimum **90/100** score threshold.

---

## Automatic Error-Correction Loop

When an error is flagged (e.g. Scene 4 pronunciation of "Riyadh"):
1. The project status shifts to `Auto-Fixing`.
2. The exact affected agent is targeted (Voice-Over QA).
3. The system generates a surgical phoneme correction (`/rɪˈjɑːd/`).
4. **Only Scene 4's audio stem is regenerated**; Scenes 1-3 and 5 remain untouched.
5. Re-verification by Agent 5 and Agent 13 elevates the score to 97/100, unlocking `APPROVED` status.

---

## Running Cloud Backend

```bash
docker build -t cinecraft-backend .
docker run -p 8080:8080 -e PORT=8080 cinecraft-backend
```

Open `http://localhost:8080/docs` to test Swagger REST endpoints.
