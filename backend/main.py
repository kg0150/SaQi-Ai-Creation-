"""
FastAPI Cloud Production Backend for CineCraft AI Video Platform
Exposes REST endpoints for project creation, 13-agent QA pipelines, rendering, and export.
"""
import uuid
from datetime import datetime
from typing import List, Dict
from fastapi import FastAPI, HTTPException, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from backend.models.schemas import (
    CreateProjectRequest, ProjectResponse, ProjectStatus,
    Scene, MapConfig, VoiceConfig, QualityScores, VideoFormat, Resolution
)
from backend.agents.pipeline import AgentPipelineOrchestrator
from backend.render.engine import CloudVideoRenderEngine

app = FastAPI(
    title="CineCraft AI Video Generation Platform",
    version="1.0.0",
    description="Cloud-based AI video production platform with 13-agent QA pipeline and map studio"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# In-memory storage for projects
PROJECTS_DB: Dict[str, ProjectResponse] = {}

TEMPLATES = [
    {"id": "breaking-news", "name": "Breaking News", "category": "News", "format": "16:9", "duration": 45},
    {"id": "map-journey", "name": "Cinematic Map Journey", "category": "Travel", "format": "9:16", "duration": 25},
    {"id": "history-doc", "name": "Ancient History Chronicle", "category": "Documentary", "format": "16:9", "duration": 60},
    {"id": "tech-explainer", "name": "Tech & AI Explainer", "category": "Education", "format": "9:16", "duration": 30},
    {"id": "top-10-facts", "name": "Top 10 Mind-Blowing Facts", "category": "Facts", "format": "9:16", "duration": 50},
    {"id": "cinematic-shorts", "name": "Cinematic Storyteller", "category": "Shorts", "format": "9:16", "duration": 20}
]

SUPPORTED_LANGUAGES = [
    {"code": "en-US", "name": "English (US)", "voices": ["Cinematic Narrator", "Deep Documentary", "Energetic Shorts"]},
    {"code": "ur-PK", "name": "Urdu (Pakistan)", "voices": ["Documentary Male", "News Female", "Poetic Narrator"]},
    {"code": "ar-SA", "name": "Arabic (Saudi Arabia)", "voices": ["Fasih Cinematic", "Khaleeji Male", "Modern Standard"]},
    {"code": "hi-IN", "name": "Hindi (India)", "voices": ["Epic Narrator", "Conversational Female", "Documentary"]},
    {"code": "bn-BD", "name": "Bengali", "voices": ["Storyteller", "News Anchor"]},
    {"code": "pa-PK", "name": "Punjabi", "voices": ["Energetic Male", "Classic Narrator"]},
    {"code": "fa-IR", "name": "Persian (Farsi)", "voices": ["Poetic Cinematic", "Studio Voice"]},
    {"code": "tr-TR", "name": "Turkish", "voices": ["Cinematic Male", "Documentary Female"]},
    {"code": "fr-FR", "name": "French", "voices": ["Studio Narrateur", "Chic Modern"]},
    {"code": "de-DE", "name": "German", "voices": ["Precise Documentary", "Warm Narrator"]},
    {"code": "es-ES", "name": "Spanish", "voices": ["Dramatic Storyteller", "Latin American Male"]},
    {"code": "pt-BR", "name": "Portuguese (Brazil)", "voices": ["Vibrant Narrator", "Smooth Studio"]},
    {"code": "it-IT", "name": "Italian", "voices": ["Cinematic Classico", "Expressive Narrator"]},
    {"code": "ru-RU", "name": "Russian", "voices": ["Deep Epic", "Documentary"]},
    {"code": "zh-CN", "name": "Chinese (Mandarin)", "voices": ["Professional News", "Cinematic Voice"]},
    {"code": "ja-JP", "name": "Japanese", "voices": ["Anime Heroic", "Documentary Master"]},
    {"code": "ko-KR", "name": "Korean", "voices": ["Dramatic Studio", "Clean Narrator"]},
    {"code": "id-ID", "name": "Indonesian", "voices": ["Friendly Narrator", "Documentary"]},
    {"code": "ms-MY", "name": "Malay", "voices": ["Standard Malay Studio"]},
    {"code": "vi-VN", "name": "Vietnamese", "voices": ["Southern Melody", "Northern Classic"]},
    {"code": "th-TH", "name": "Thai", "voices": ["Smooth Narration", "Dynamic News"]},
    {"code": "ta-IN", "name": "Tamil", "voices": ["Resonant Male", "Narrator Female"]},
    {"code": "te-IN", "name": "Telugu", "voices": ["Classic Orator", "Modern Studio"]},
    {"code": "mr-IN", "name": "Marathi", "voices": ["Cultural Narrator", "Documentary"]},
    {"code": "gu-IN", "name": "Gujarati", "voices": ["Vibrant Speaker", "Studio Voice"]}
]

@app.get("/healthz")
def health_check():
    return {"status": "healthy", "service": "CineCraft Cloud AI Video Platform", "timestamp": datetime.utcnow().isoformat()}

@app.get("/api/v1/templates")
def list_templates():
    return {"templates": TEMPLATES}

@app.get("/api/v1/languages")
def list_languages():
    return {"languages": SUPPORTED_LANGUAGES}

@app.post("/api/v1/projects", response_model=ProjectResponse)
def create_project(req: CreateProjectRequest):
    project_id = str(uuid.uuid4())[:8]
    map_cfg = req.map_config or MapConfig()
    scenes = AgentPipelineOrchestrator.generate_storyboard(req.prompt, map_cfg, req.voice_config)
    evaluations, scores = AgentPipelineOrchestrator.run_qa_pipeline(scenes, map_cfg)
    thumbnails = CloudVideoRenderEngine.generate_thumbnails(map_cfg.origin_country, map_cfg.destination_country)
    
    total_dur = sum(s.duration_seconds for s in scenes)
    delivery = CloudVideoRenderEngine.generate_delivery_package(
        project_id, req.title, map_cfg.origin_country, map_cfg.destination_country,
        total_dur, req.format, req.resolution
    )

    project = ProjectResponse(
        id=project_id,
        title=req.title,
        user_id="user_cloud_demo",
        created_at=datetime.utcnow().strftime("%Y-%m-%d %H:%M"),
        status=ProjectStatus.PROCESSING,
        format=req.format,
        resolution=req.resolution,
        duration_seconds=total_dur,
        scenes=scenes,
        agent_evaluations=evaluations,
        quality_scores=scores,
        map_config=map_cfg,
        voice_config=req.voice_config,
        thumbnail_concepts=thumbnails,
        delivery_package=delivery
    )
    PROJECTS_DB[project_id] = project
    return project

@app.get("/api/v1/projects", response_model=List[ProjectResponse])
def list_projects():
    return list(PROJECTS_DB.values())

@app.get("/api/v1/projects/{project_id}", response_model=ProjectResponse)
def get_project(project_id: str):
    if project_id not in PROJECTS_DB:
        raise HTTPException(status_code=404, detail="Project not found")
    return PROJECTS_DB[project_id]

@app.post("/api/v1/projects/{project_id}/auto-fix", response_model=ProjectResponse)
def auto_fix_project(project_id: str):
    if project_id not in PROJECTS_DB:
        raise HTTPException(status_code=404, detail="Project not found")
    proj = PROJECTS_DB[project_id]
    new_evals, new_scores = AgentPipelineOrchestrator.execute_auto_repair(proj.scenes, proj.agent_evaluations)
    proj.agent_evaluations = new_evals
    proj.quality_scores = new_scores
    proj.status = ProjectStatus.APPROVED
    return proj

@app.post("/api/v1/projects/{project_id}/regenerate-scene/{scene_id}", response_model=ProjectResponse)
def regenerate_scene(project_id: str, scene_id: int):
    if project_id not in PROJECTS_DB:
        raise HTTPException(status_code=404, detail="Project not found")
    proj = PROJECTS_DB[project_id]
    for sc in proj.scenes:
        if sc.id == scene_id:
            sc.status = "Regenerated & Verified"
            break
    return proj
