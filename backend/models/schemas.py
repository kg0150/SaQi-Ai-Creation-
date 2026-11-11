"""
Pydantic Schemas and Models for Cloud AI Video Generation Platform
"""
from enum import Enum
from typing import List, Optional, Dict, Any
from pydantic import BaseModel, Field

class VideoFormat(str, Enum):
    PORTRAIT_9_16 = "9:16"
    LANDSCAPE_16_9 = "16:9"
    SQUARE_1_1 = "1:1"

class Resolution(str, Enum):
    RES_720P = "720p"
    RES_1080P = "1080p"
    RES_4K = "4K"

class MapStyle(str, Enum):
    CINEMATIC = "Cinematic"
    SATELLITE = "Satellite"
    TERRAIN = "Terrain"
    DARK = "Dark"
    LIGHT = "Light"
    MINIMAL = "Minimal"
    NEWS = "News"
    DOCUMENTARY = "Documentary"
    MAP_3D = "3D Map"

class VoiceStyle(str, Enum):
    NARRATOR = "Narrator"
    NEWS = "News"
    DOCUMENTARY = "Documentary"
    CINEMATIC = "Cinematic"
    ENERGETIC = "Energetic"
    CALM = "Calm"
    EMOTIONAL = "Emotional"

class AgentStatus(str, Enum):
    PENDING = "PENDING"
    RUNNING = "RUNNING"
    PASSED = "PASSED"
    FLAGGED = "FLAGGED"
    REGENERATING = "REGENERATING"
    APPROVED = "APPROVED"

class ProjectStatus(str, Enum):
    DRAFT = "Draft"
    QUEUED = "Queued"
    PROCESSING = "Processing"
    QA_REVIEW = "QA Review"
    AUTO_FIXING = "Auto-Fixing"
    RENDERING = "Rendering"
    APPROVED = "Approved"
    COMPLETED = "Completed"
    FAILED = "Failed"

class Scene(BaseModel):
    id: int
    title: str
    duration_seconds: float = 4.0
    narration: str
    visual_description: str
    camera_movement: str = "Slow Zoom In"
    transition: str = "Cross Dissolve"
    sound_effect: str = "Cinematic Whoosh"
    image_prompt: str
    subtitle_text: str
    is_map_scene: bool = False
    map_origin: Optional[str] = None
    map_destination: Optional[str] = None
    voice_url: Optional[str] = None
    image_url: Optional[str] = None
    status: str = "Ready"

class MapConfig(BaseModel):
    enabled: bool = True
    origin_country: str = "Pakistan"
    origin_city: str = "Islamabad"
    origin_coords: List[float] = [33.6844, 73.0479]
    destination_country: str = "Saudi Arabia"
    destination_city: str = "Riyadh"
    destination_coords: List[float] = [24.7136, 46.6753]
    style: MapStyle = MapStyle.CINEMATIC
    show_flight_path: bool = True
    show_radar_pulse: bool = True
    show_country_labels: bool = True
    show_direction_arrow: bool = True
    elevation_angle: float = 35.0

class VoiceConfig(BaseModel):
    language: str = "English"
    language_code: str = "en-US"
    voice_gender: str = "Male"
    voice_style: VoiceStyle = VoiceStyle.CINEMATIC
    speed: float = 1.0
    pitch: float = 1.0
    pronunciation_dict: Dict[str, str] = Field(default_factory=dict)

class AgentEvaluation(BaseModel):
    agent_id: int
    agent_name: str
    status: AgentStatus
    score: int = Field(ge=0, le=100)
    feedback: str
    detected_errors: List[str] = Field(default_factory=list)
    correction_instructions: Optional[str] = None
    affected_scene_id: Optional[int] = None

class QualityScores(BaseModel):
    script_quality: int = 95
    voice_quality: int = 94
    visual_quality: int = 96
    map_accuracy: int = 98
    subtitle_quality: int = 97
    audio_quality: int = 93
    editing_quality: int = 95
    thumbnail_quality: int = 96
    overall_quality: int = 96

    def is_approved(self) -> bool:
        return self.overall_quality >= 90

class ThumbnailConcept(BaseModel):
    id: int
    title: str
    visual_concept: str
    headline_text: str
    prompt: str
    composition: str
    aspect_ratio: str = "16:9"
    image_url: Optional[str] = None

class DeliveryPackage(BaseModel):
    final_video_url: str
    voiceover_mp3_url: str
    subtitles_srt_url: str
    subtitles_vtt_url: str
    thumbnail_url: str
    suggested_titles: List[str]
    seo_description: str
    hashtags: List[str]
    youtube_tags: List[str]
    keywords: List[str]
    short_caption: str
    project_json: Dict[str, Any]

class CreateProjectRequest(BaseModel):
    title: str
    prompt: str
    format: VideoFormat = VideoFormat.PORTRAIT_9_16
    resolution: Resolution = Resolution.RES_1080P
    voice_config: VoiceConfig = Field(default_factory=VoiceConfig)
    map_config: Optional[MapConfig] = None
    template_name: Optional[str] = None

class ProjectResponse(BaseModel):
    id: str
    title: str
    user_id: str
    created_at: str
    status: ProjectStatus
    format: VideoFormat
    resolution: Resolution
    duration_seconds: float
    scenes: List[Scene]
    agent_evaluations: List[AgentEvaluation]
    quality_scores: QualityScores
    map_config: Optional[MapConfig] = None
    voice_config: VoiceConfig
    thumbnail_concepts: List[ThumbnailConcept]
    delivery_package: Optional[DeliveryPackage] = None
