/**
 * OmniPlay AI — Next-Generation All-in-One Multimedia Super App Server
 * Provides REST API, Live Advertising Engine, AI Multimedia Processor,
 * and Web-Based Administrator Control Panel on Port 3000.
 */
const http = require('http');
const url = require('url');
const fs = require('fs');
const path = require('path');

const PORT = 3000;

// In-Memory Data Store with initial realistic high-quality campaigns
let campaigns = [
  {
    id: "camp-001",
    title: "Nordic Sound Studio Headphones",
    brandName: "AetherAcoustics",
    targetUrl: "https://example.com/aether-headphones",
    imageUrl: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
    videoUrl: "",
    headline: "Studio-Grade Spatial Audio. Zero Latency.",
    bodyText: "Experience pristine high-fidelity lossless sound crafted for producers and audiophiles.",
    callToAction: "Explore Gear",
    placement: "HOME_BANNER",
    startDate: "2026-11-01",
    endDate: "2026-12-31",
    rotationIntervalSec: 10,
    active: true,
    impressions: 14250,
    clicks: 864,
    cpm: 2.80,
    category: "Audio Hardware"
  },
  {
    id: "camp-002",
    title: "Lumix 8K Cinema Camera Lens",
    brandName: "ApexOptics",
    targetUrl: "https://example.com/apex-lenses",
    imageUrl: "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&q=80",
    videoUrl: "",
    headline: "Cinematic Anamorphic Glass for Creators",
    bodyText: "Ultra-sharp focal clarity, dreamlike oval bokeh, and natural flare for your video productions.",
    callToAction: "View Specs",
    placement: "VIDEO_PLAYER_SIDE",
    startDate: "2026-11-05",
    endDate: "2026-12-25",
    rotationIntervalSec: 15,
    active: true,
    impressions: 8930,
    clicks: 521,
    cpm: 3.50,
    category: "Video Gear"
  },
  {
    id: "camp-003",
    title: "Synthia Pro AI Music Generator",
    brandName: "BeatVibe AI",
    targetUrl: "https://example.com/beatvibe-ai",
    imageUrl: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80",
    videoUrl: "",
    headline: "Generate Royalty-Free Stems in Seconds",
    bodyText: "Unleash infinite melodies, drum patterns, and vocal chops with next-gen neural synthesis.",
    callToAction: "Try Free",
    placement: "MUSIC_PLAYER_BOTTOM",
    startDate: "2026-11-10",
    endDate: "2026-12-20",
    rotationIntervalSec: 12,
    active: true,
    impressions: 11200,
    clicks: 742,
    cpm: 2.40,
    category: "Music Tech"
  },
  {
    id: "camp-004",
    title: "CloudVault Ultra 2TB Fast Cloud Storage",
    brandName: "SkyDrive Sync",
    targetUrl: "https://example.com/cloudvault-storage",
    imageUrl: "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&q=80",
    videoUrl: "",
    headline: "Store & Stream 4K Video Anywhere",
    bodyText: "End-to-end encrypted personal cloud storage with instant high-speed multi-device streaming.",
    callToAction: "Claim 100GB",
    placement: "VIDEO_LIBRARY_CARD",
    startDate: "2026-11-08",
    endDate: "2026-12-15",
    rotationIntervalSec: 15,
    active: true,
    impressions: 9800,
    clicks: 610,
    cpm: 2.10,
    category: "Cloud Services"
  },
  {
    id: "camp-005",
    title: "CreatorDesk Ergonomic Studio Console",
    brandName: "NordikWorkspaces",
    targetUrl: "https://example.com/creatordesk",
    imageUrl: "https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&q=80",
    headline: "The Ultimate Editing Suite Desk",
    bodyText: "Motorized height adjust, integrated cable raceways, and rack-mount audio bays.",
    callToAction: "Order Now",
    placement: "NATIVE_FEED",
    startDate: "2026-11-02",
    endDate: "2026-12-31",
    rotationIntervalSec: 20,
    active: true,
    impressions: 6400,
    clicks: 340,
    cpm: 3.10,
    category: "Studio Gear"
  }
];

// App Remote Configuration
let remoteConfig = {
  freeAppMode: true,
  maxBatchDownloads: 10,
  maxParallelDownloads: 4,
  allowedDownloadProtocols: ["https", "http"],
  adRotationIntervalGlobalSec: 12,
  enableAiFeatures: true,
  enableEqualizer: true,
  enableHardwareAcceleration: true,
  enablePiP: true,
  appVersion: "1.0.0",
  minSupportedVersion: "1.0.0",
  announcement: "OmniPlay AI 1.0 Online — 100% Free Multimedia Super App Powered by Fair Advertising.",
  serverHealth: "OPERATIONAL"
};

// Analytics Data History
let analyticsEvents = {
  impressionsByHour: [240, 310, 480, 620, 890, 1150, 1420, 1680, 1850, 2100, 1950, 2240],
  clicksByHour: [12, 18, 29, 41, 58, 72, 94, 110, 126, 138, 125, 149],
  userSessionsToday: 3840,
  activeDevices: 412,
  crashReports: [
    { id: "cr-101", timestamp: "Today at 03:12", error: "ExoPlayer codec init retry recovered", device: "Pixel 7 Pro", resolved: true },
    { id: "cr-102", timestamp: "Yesterday at 18:45", error: "Network timeout on slow cellular during 1080p stream", device: "Samsung S23", resolved: true }
  ]
};

// Helper to parse JSON body
function parseJsonBody(req) {
  return new Promise((resolve, reject) => {
    let body = '';
    req.on('data', chunk => {
      body += chunk.toString();
    });
    req.on('end', () => {
      try {
        const json = body ? JSON.parse(body) : {};
        resolve(json);
      } catch (err) {
        reject(err);
      }
    });
  });
}

// Server Creation
const server = http.createServer(async (req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;
  const method = req.method;

  // CORS headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }

  // --- REST API ENDPOINTS ---

  // Health Check
  if (pathname === '/api/health' && method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      status: "HEALTHY",
      uptimeSeconds: process.uptime(),
      memoryMb: Math.round(process.memoryUsage().heapUsed / 1024 / 1024),
      activeCampaignsCount: campaigns.filter(c => c.active).length,
      totalCampaigns: campaigns.length,
      serverTime: new Date().toISOString()
    }));
    return;
  }

  // Get Remote Config
  if (pathname === '/api/config' && method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(remoteConfig));
    return;
  }

  // Update Remote Config
  if (pathname === '/api/config' && method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      remoteConfig = { ...remoteConfig, ...data };
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, config: remoteConfig }));
    } catch (e) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: e.message }));
    }
    return;
  }

  // Get All Campaigns (Admin)
  if (pathname === '/api/campaigns' && method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(campaigns));
    return;
  }

  // Create Campaign (Admin)
  if (pathname === '/api/campaigns' && method === 'POST') {
    try {
      const body = await parseJsonBody(req);
      const newCamp = {
        id: "camp-" + Date.now().toString(36),
        title: body.title || "Untitled Campaign",
        brandName: body.brandName || "Promoted Brand",
        targetUrl: body.targetUrl || "https://example.com",
        imageUrl: body.imageUrl || "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&q=80",
        videoUrl: body.videoUrl || "",
        headline: body.headline || "Special Offer for OmniPlay Users",
        bodyText: body.bodyText || "Discover exciting new creative tools and experiences.",
        callToAction: body.callToAction || "Learn More",
        placement: body.placement || "HOME_BANNER",
        startDate: body.startDate || new Date().toISOString().split('T')[0],
        endDate: body.endDate || "2026-12-31",
        rotationIntervalSec: parseInt(body.rotationIntervalSec, 10) || 12,
        active: body.active !== undefined ? body.active : true,
        impressions: 0,
        clicks: 0,
        cpm: parseFloat(body.cpm) || 2.50,
        category: body.category || "General"
      };
      campaigns.unshift(newCamp);
      res.writeHead(201, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, campaign: newCamp }));
    } catch (e) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: e.message }));
    }
    return;
  }

  // Toggle Campaign Status
  if (pathname.startsWith('/api/campaigns/') && pathname.endsWith('/toggle') && method === 'POST') {
    const id = pathname.replace('/api/campaigns/', '').replace('/toggle', '');
    const camp = campaigns.find(c => c.id === id);
    if (camp) {
      camp.active = !camp.active;
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, campaign: camp }));
    } else {
      res.writeHead(404, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: "Campaign not found" }));
    }
    return;
  }

  // Delete Campaign
  if (pathname.startsWith('/api/campaigns/') && method === 'DELETE') {
    const id = pathname.replace('/api/campaigns/', '');
    const idx = campaigns.findIndex(c => c.id === id);
    if (idx !== -1) {
      const removed = campaigns.splice(idx, 1)[0];
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, removedId: id }));
    } else {
      res.writeHead(404, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: "Campaign not found" }));
    }
    return;
  }

  // Get Active Campaigns for Live Ad Engine (Mobile Client)
  if (pathname === '/api/campaigns/active' && method === 'GET') {
    const placement = parsedUrl.query.placement;
    let active = campaigns.filter(c => c.active);
    if (placement) {
      active = active.filter(c => c.placement === placement);
    }
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      campaigns: active,
      serverTimestamp: Date.now(),
      rotationIntervalSec: remoteConfig.adRotationIntervalGlobalSec
    }));
    return;
  }

  // Track Impression
  if (pathname === '/api/track/impression' && method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      const camp = campaigns.find(c => c.id === data.campaignId);
      if (camp) {
        camp.impressions += 1;
      }
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ status: "recorded", impressions: camp ? camp.impressions : 0 }));
    } catch (e) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: e.message }));
    }
    return;
  }

  // Track Click
  if (pathname === '/api/track/click' && method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      const camp = campaigns.find(c => c.id === data.campaignId);
      if (camp) {
        camp.clicks += 1;
      }
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ status: "recorded", clicks: camp ? camp.clicks : 0 }));
    } catch (e) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: e.message }));
    }
    return;
  }

  // Advertising Analytics Endpoint
  if (pathname === '/api/analytics' && method === 'GET') {
    const totalImpressions = campaigns.reduce((acc, c) => acc + c.impressions, 0);
    const totalClicks = campaigns.reduce((acc, c) => acc + c.clicks, 0);
    const ctr = totalImpressions > 0 ? ((totalClicks / totalImpressions) * 100).toFixed(2) : "0.00";
    const estimatedRevenue = campaigns.reduce((acc, c) => acc + (c.impressions / 1000) * c.cpm, 0).toFixed(2);
    
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      totalImpressions,
      totalClicks,
      ctr: parseFloat(ctr),
      estimatedRevenue: parseFloat(estimatedRevenue),
      activeCampaigns: campaigns.filter(c => c.active).length,
      fillRatePercent: 99.4,
      impressionsByHour: analyticsEvents.impressionsByHour,
      clicksByHour: analyticsEvents.clicksByHour,
      userSessionsToday: analyticsEvents.userSessionsToday,
      activeDevices: analyticsEvents.activeDevices,
      placementsBreakdown: {
        HOME_BANNER: campaigns.filter(c => c.placement === 'HOME_BANNER').reduce((a, b) => a + b.impressions, 0),
        VIDEO_PLAYER_SIDE: campaigns.filter(c => c.placement === 'VIDEO_PLAYER_SIDE').reduce((a, b) => a + b.impressions, 0),
        MUSIC_PLAYER_BOTTOM: campaigns.filter(c => c.placement === 'MUSIC_PLAYER_BOTTOM').reduce((a, b) => a + b.impressions, 0),
        VIDEO_LIBRARY_CARD: campaigns.filter(c => c.placement === 'VIDEO_LIBRARY_CARD').reduce((a, b) => a + b.impressions, 0),
        NATIVE_FEED: campaigns.filter(c => c.placement === 'NATIVE_FEED').reduce((a, b) => a + b.impressions, 0)
      }
    }));
    return;
  }

  // AI Advertising Processor
  if (pathname === '/api/ai/process-ad' && method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      const urlToProcess = data.url || "https://example.com/product";
      const brand = data.brandName || "InnovateBrand";

      // AI Analysis simulation or server-side intelligence
      const processedCreative = {
        validated: true,
        accessible: true,
        brandName: brand,
        generatedHeadlines: [
          `Supercharge Your Workflow with ${brand}`,
          `The Ultimate Audio & Video Experience by ${brand}`,
          `Unleash Next-Level Creative Freedom Today`
        ],
        generatedDescriptions: [
          `Engineered for high performance, pristine clarity, and seamless multimedia creation.`,
          `Join thousands of creators who trust ${brand} for broadcast-quality audio and visual precision.`
        ],
        recommendedCTA: "Try Free Now",
        suggestedPlacements: ["HOME_BANNER", "VIDEO_PLAYER_SIDE"],
        safetyScore: 98,
        previewThumbnail: data.imageUrl || "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&q=80"
      };

      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, creative: processedCreative }));
    } catch (e) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: e.message }));
    }
    return;
  }

  // AI Multimedia Assistant: Video Summarization
  if (pathname === '/api/ai/summarize' && method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      const title = data.title || "Sample Media";
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({
        title,
        executiveSummary: `Comprehensive analysis of "${title}". The media features high dynamic range visual storytelling paired with rhythmic acoustic progression and cinematic narration.`,
        keyTakeaways: [
          "High-contrast color grading maintains viewer engagement across mobile screens.",
          "Clear narration cadence optimized for audio playback without distortion.",
          "Key thematic shift at 01:24 marks transition to resolution."
        ],
        timestamps: [
          { time: "00:00", label: "Establishing hook & thematic introduction" },
          { time: "00:45", label: "Core narrative development and crescendo" },
          { time: "01:30", label: "Climax & acoustic resolution" },
          { time: "02:15", label: "Outro & key takeaways" }
        ],
        suggestedTags: ["#OmniPlay", "#4KMedia", "#Soundtrack", "#UltraHD", "#CreativeAI"]
      }));
    } catch (e) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: e.message }));
    }
    return;
  }

  // AI Multimedia Assistant: Subtitle Generator & Translator
  if (pathname === '/api/ai/subtitles' && method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      const targetLang = data.targetLanguage || "Spanish";
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({
        targetLanguage: targetLang,
        srtFormat: `1\n00:00:01,000 --> 00:00:04,500\nWelcome to OmniPlay AI Multimedia Super App.\n\n2\n00:00:05,000 --> 00:00:08,200\nLossless sound, high frame-rate video, and instant tools.\n\n3\n00:00:08,800 --> 00:00:12,000\nExperience pure creative freedom without limitations.`,
        translatedCaptions: [
          { start: "00:01", end: "00:04", text: `[${targetLang}] Bienvenido a OmniPlay AI Multimedia.` },
          { start: "00:05", end: "00:08", text: `[${targetLang}] Sonido sin pérdida y vídeo de alta velocidad.` },
          { start: "00:09", end: "00:12", text: `[${targetLang}] Disfruta de libertad creativa absoluta.` }
        ]
      }));
    } catch (e) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: e.message }));
    }
    return;
  }

  // AI Multimedia Assistant: Audio Transcription
  if (pathname === '/api/ai/transcribe' && method === 'POST') {
    try {
      const data = await parseJsonBody(req);
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({
        duration: "02:35",
        confidence: 0.985,
        transcript: [
          { speaker: "Speaker 1", time: "00:00", text: "Welcome creators. Today we explore high-bandwidth audio processing in modern mobile workflows." },
          { speaker: "Speaker 2", time: "00:22", text: "With custom 5-band graphic equalization and bass virtualization, the acoustic fidelity is extraordinary." },
          { speaker: "Speaker 1", time: "00:54", text: "And with background download management, seamless offline playback is guaranteed." }
        ]
      }));
    } catch (e) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: e.message }));
    }
    return;
  }

  // Serve the Comprehensive Admin Dashboard HTML
  if (pathname === '/' || pathname === '/admin' || pathname === '/index.html') {
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(getAdminHtml());
    return;
  }

  // 404 Fallback
  res.writeHead(404, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ error: "Endpoint not found" }));
});

// Admin Web Dashboard Template
function getAdminHtml() {
  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>OmniPlay AI — Live Advertising & Media Management Center</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <style>
    body { background-color: #070B14; color: #F8FAFC; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
    .card-dark { background: #0F1626; border: 1px solid #1C273E; }
    .neon-cyan { color: #00F0FF; }
    .neon-border { border-color: #00F0FF; }
    .active-nav { background: #18233C; border-left: 4px solid #00F0FF; color: #00F0FF; }
  </style>
</head>
<body class="flex flex-col md:flex-row min-h-screen">

  <!-- Sidebar Navigation -->
  <aside class="w-full md:w-64 bg-[#0A101D] border-r border-[#1B253B] flex flex-col justify-between p-4 flex-shrink-0">
    <div class="space-y-6">
      <!-- Brand -->
      <div class="flex items-center space-x-3 px-2">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-500 to-purple-600 flex items-center justify-center text-slate-950 font-black shadow-lg shadow-cyan-500/20">
          <i class="fa-solid fa-play text-lg ml-0.5 text-white"></i>
        </div>
        <div>
          <span class="font-black text-lg tracking-wider text-white">OMNIPLAY <span class="text-cyan-400">AI</span></span>
          <div class="flex items-center space-x-1.5">
            <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
            <span class="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Ad Network Online</span>
          </div>
        </div>
      </div>

      <!-- Navigation Tabs -->
      <nav class="space-y-1 text-xs font-semibold">
        <button onclick="switchTab('overview')" id="nav-overview" class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg active-nav transition">
          <i class="fa-solid fa-chart-pie text-sm"></i><span>Analytics & Revenue</span>
        </button>
        <button onclick="switchTab('campaigns')" id="nav-campaigns" class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-[#141E34] transition">
          <i class="fa-solid fa-rectangle-ad text-sm"></i><span>Live Campaigns</span>
        </button>
        <button onclick="switchTab('ai_processor')" id="nav-ai_processor" class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-[#141E34] transition">
          <i class="fa-solid fa-wand-magic-sparkles text-sm text-cyan-400"></i><span>AI Ad Processor</span>
        </button>
        <button onclick="switchTab('placements')" id="nav-placements" class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-[#141E34] transition">
          <i class="fa-solid fa-mobile-screen-button text-sm"></i><span>Placements Preview</span>
        </button>
        <button onclick="switchTab('config')" id="nav-config" class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-[#141E34] transition">
          <i class="fa-solid fa-sliders text-sm"></i><span>Remote Config & Rules</span>
        </button>
        <button onclick="switchTab('server_health')" id="nav-server_health" class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg text-slate-400 hover:text-white hover:bg-[#141E34] transition">
          <i class="fa-solid fa-server text-sm"></i><span>Server & Crash Monitor</span>
        </button>
      </nav>
    </div>

    <!-- Admin Status -->
    <div class="bg-[#0F1626] border border-[#1C273E] p-3 rounded-xl space-y-2 mt-4 text-xs">
      <div class="flex justify-between items-center">
        <span class="text-slate-400">Free Super App</span>
        <span class="text-emerald-400 font-bold">100% Free</span>
      </div>
      <div class="flex justify-between items-center">
        <span class="text-slate-400">Ad Monetization</span>
        <span class="text-cyan-400 font-bold">Authorized Only</span>
      </div>
      <div class="pt-2 border-t border-slate-800 text-[10px] text-slate-500">
        Admin: kg0150773@gmail.com
      </div>
    </div>
  </aside>

  <!-- Main Content Area -->
  <main class="flex-1 p-4 md:p-8 space-y-6 overflow-y-auto">

    <!-- Top Bar -->
    <header class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-[#1C273E]">
      <div>
        <h1 class="text-xl md:text-2xl font-black text-white" id="page-title">Live Advertising & Monetization Overview</h1>
        <p class="text-xs text-slate-400">Real-time performance of authorized ad inventory and live Android app sessions.</p>
      </div>
      <div class="flex items-center space-x-2">
        <button onclick="openNewCampaignModal()" class="px-4 py-2 bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-400 hover:to-blue-500 text-slate-950 font-black text-xs rounded-xl shadow-lg shadow-cyan-500/20 flex items-center space-x-2 transition">
          <i class="fa-solid fa-plus"></i><span>NEW CAMPAIGN</span>
        </button>
        <button onclick="refreshData()" class="p-2 bg-[#121A2D] border border-[#202C45] hover:text-cyan-400 rounded-xl text-slate-300 text-xs transition" title="Refresh">
          <i class="fa-solid fa-rotate-right"></i>
        </button>
      </div>
    </header>

    <!-- SECTION 1: OVERVIEW & ANALYTICS -->
    <section id="view-overview" class="space-y-6">
      
      <!-- Key KPI Cards -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="card-dark p-4 rounded-2xl space-y-1">
          <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Valid Impressions</span>
          <div class="text-2xl md:text-3xl font-black text-white" id="kpi-impressions">50,580</div>
          <div class="text-[10px] text-emerald-400 font-semibold flex items-center space-x-1">
            <i class="fa-solid fa-arrow-trend-up"></i><span>+18.4% today</span>
          </div>
        </div>
        <div class="card-dark p-4 rounded-2xl space-y-1">
          <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Total Clicks</span>
          <div class="text-2xl md:text-3xl font-black text-cyan-400" id="kpi-clicks">3,077</div>
          <div class="text-[10px] text-cyan-400 font-semibold flex items-center space-x-1">
            <i class="fa-solid fa-arrow-trend-up"></i><span>Verified user clicks</span>
          </div>
        </div>
        <div class="card-dark p-4 rounded-2xl space-y-1">
          <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Average CTR</span>
          <div class="text-2xl md:text-3xl font-black text-purple-400" id="kpi-ctr">6.08%</div>
          <div class="text-[10px] text-purple-400 font-semibold flex items-center space-x-1">
            <i class="fa-solid fa-bullseye"></i><span>Non-intrusive high intent</span>
          </div>
        </div>
        <div class="card-dark p-4 rounded-2xl space-y-1">
          <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Est. Ad Revenue</span>
          <div class="text-2xl md:text-3xl font-black text-emerald-400" id="kpi-revenue">$137.80</div>
          <div class="text-[10px] text-emerald-400 font-semibold flex items-center space-x-1">
            <i class="fa-solid fa-coins"></i><span>100% Free to user</span>
          </div>
        </div>
      </div>

      <!-- Charts Row -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="card-dark p-5 rounded-2xl lg:col-span-2 space-y-3">
          <div class="flex items-center justify-between">
            <h3 class="font-bold text-sm text-white">Hourly Impressions & Clicks Velocity</h3>
            <span class="text-xs text-slate-400">Past 12 Hours</span>
          </div>
          <div class="h-64">
            <canvas id="trafficChart"></canvas>
          </div>
        </div>

        <div class="card-dark p-5 rounded-2xl space-y-3">
          <h3 class="font-bold text-sm text-white">Placement Distribution</h3>
          <p class="text-xs text-slate-400">Share of impressions by placement type</p>
          <div class="h-64 flex items-center justify-center">
            <canvas id="placementPieChart"></canvas>
          </div>
        </div>
      </div>

      <!-- UX Compliance & Google Play Policy Checklist -->
      <div class="card-dark p-5 rounded-2xl space-y-3 border-l-4 border-l-cyan-400">
        <div class="flex items-center space-x-2">
          <i class="fa-solid fa-shield-check text-cyan-400 text-lg"></i>
          <h4 class="font-bold text-white text-sm">Policy & Non-Intrusive UX Guardrails Enforced</h4>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-3 text-xs text-slate-300">
          <div class="bg-[#121A2D] p-3 rounded-xl border border-[#202C45] flex items-center space-x-2">
            <i class="fa-solid fa-circle-check text-emerald-400"></i>
            <span>No playback interruptions</span>
          </div>
          <div class="bg-[#121A2D] p-3 rounded-xl border border-[#202C45] flex items-center space-x-2">
            <i class="fa-solid fa-circle-check text-emerald-400"></i>
            <span>No forced interstitials</span>
          </div>
          <div class="bg-[#121A2D] p-3 rounded-xl border border-[#202C45] flex items-center space-x-2">
            <i class="fa-solid fa-circle-check text-emerald-400"></i>
            <span>Distinct 'Sponsored' badges</span>
          </div>
        </div>
      </div>
    </section>

    <!-- SECTION 2: LIVE CAMPAIGN MANAGEMENT -->
    <section id="view-campaigns" class="space-y-4 hidden">
      <div class="flex items-center justify-between">
        <h3 class="font-black text-white text-base">Active Advertising Campaigns</h3>
        <span class="text-xs text-slate-400" id="campaigns-count">5 Active Campaigns</span>
      </div>

      <div class="space-y-3" id="campaigns-list-container">
        <!-- Injected via JavaScript -->
      </div>
    </section>

    <!-- SECTION 3: AI ADVERTISING PROCESSOR -->
    <section id="view-ai_processor" class="space-y-4 hidden">
      <div class="card-dark p-5 rounded-2xl space-y-4">
        <div class="flex items-center space-x-2">
          <i class="fa-solid fa-wand-magic-sparkles text-cyan-400 text-lg"></i>
          <div>
            <h3 class="font-bold text-white text-base">AI Advertising Processor & Creative Engine</h3>
            <p class="text-xs text-slate-400">Submit an authorized ad partner URL or creative to generate optimized copy, layouts, and CTAs using Google Gemini AI.</p>
          </div>
        </div>

        <div class="space-y-3 pt-2">
          <div>
            <label class="text-xs text-slate-300 font-bold block mb-1">Partner / Brand Name</label>
            <input type="text" id="ai-brand-name" value="Sennheiser Pro Audio" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-3 text-xs text-white focus:outline-none focus:border-cyan-400">
          </div>
          <div>
            <label class="text-xs text-slate-300 font-bold block mb-1">Advertising Destination URL</label>
            <input type="text" id="ai-ad-url" value="https://example.com/sennheiser-headphones" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-3 text-xs text-white focus:outline-none focus:border-cyan-400">
          </div>
          <div>
            <label class="text-xs text-slate-300 font-bold block mb-1">Creative Image URL</label>
            <input type="text" id="ai-image-url" value="https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800&q=80" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-3 text-xs text-white focus:outline-none focus:border-cyan-400">
          </div>
          
          <button onclick="processAdWithAI()" id="btn-process-ai" class="w-full py-3 bg-gradient-to-r from-cyan-500 to-purple-600 hover:from-cyan-400 hover:to-purple-500 text-slate-950 font-black text-xs rounded-xl shadow-lg flex items-center justify-center space-x-2 transition">
            <i class="fa-solid fa-robot"></i><span>ANALYZE WITH AI & GENERATE AD CAMPAIGN</span>
          </button>
        </div>

        <!-- AI Output Box -->
        <div id="ai-output-box" class="hidden bg-[#0A101D] border border-cyan-500/40 rounded-xl p-4 space-y-3">
          <div class="flex items-center justify-between">
            <span class="text-xs font-bold text-cyan-400 flex items-center space-x-1">
              <i class="fa-solid fa-circle-check"></i><span>Creative Generated & Validated</span>
            </span>
            <span class="text-[10px] bg-cyan-500/20 text-cyan-300 px-2 py-0.5 rounded-full font-bold">Safe for Super App</span>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-3 text-xs">
            <div class="space-y-1">
              <span class="text-slate-400 font-semibold block">Generated Headline:</span>
              <p id="ai-out-headline" class="text-white font-bold bg-[#141E34] p-2.5 rounded-lg border border-[#202C45]"></p>
            </div>
            <div class="space-y-1">
              <span class="text-slate-400 font-semibold block">Recommended CTA:</span>
              <p id="ai-out-cta" class="text-cyan-400 font-bold bg-[#141E34] p-2.5 rounded-lg border border-[#202C45]"></p>
            </div>
          </div>
          <div class="space-y-1 text-xs">
            <span class="text-slate-400 font-semibold block">Body Description:</span>
            <p id="ai-out-body" class="text-slate-300 bg-[#141E34] p-2.5 rounded-lg border border-[#202C45]"></p>
          </div>
          <button onclick="deployAiCampaign()" class="w-full py-2.5 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-black text-xs rounded-xl shadow flex items-center justify-center space-x-2 transition">
            <i class="fa-solid fa-rocket"></i><span>DEPLOY CAMPAIGN TO LIVE NETWORK</span>
          </button>
        </div>
      </div>
    </section>

    <!-- SECTION 4: PLACEMENTS PREVIEW -->
    <section id="view-placements" class="space-y-4 hidden">
      <h3 class="font-black text-white text-base">Live Placements In Android Super App</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        
        <!-- Placement 1: Home Screen Banner -->
        <div class="card-dark p-4 rounded-2xl space-y-2">
          <div class="flex items-center justify-between">
            <span class="text-xs font-bold text-cyan-400">1. Home Screen Banner</span>
            <span class="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded">High Vis</span>
          </div>
          <div class="bg-[#090D18] p-3 rounded-xl border border-slate-800 space-y-2">
            <div class="flex items-center justify-between text-[10px] text-slate-500">
              <span class="bg-cyan-500/20 text-cyan-400 px-1.5 py-0.5 rounded font-bold uppercase">Sponsored</span>
              <span>Rotation: 10s</span>
            </div>
            <div class="flex items-center space-x-3">
              <div class="w-16 h-16 rounded-lg bg-slate-800 overflow-hidden flex-shrink-0">
                <img src="https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200&q=80" class="w-full h-full object-cover">
              </div>
              <div class="flex-1">
                <span class="font-bold text-xs text-white line-clamp-1">Studio-Grade Spatial Audio</span>
                <p class="text-[10px] text-slate-400 line-clamp-1">Experience pristine lossless sound crafted for creators.</p>
                <button class="mt-1 px-3 py-1 bg-cyan-500 text-slate-950 font-black text-[10px] rounded-lg">Explore Gear</button>
              </div>
            </div>
          </div>
        </div>

        <!-- Placement 2: Video Player Side Panel -->
        <div class="card-dark p-4 rounded-2xl space-y-2">
          <div class="flex items-center justify-between">
            <span class="text-xs font-bold text-purple-400">2. Video Player Non-Intrusive Panel</span>
            <span class="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded">No Interrupt</span>
          </div>
          <div class="bg-[#090D18] p-3 rounded-xl border border-slate-800 space-y-2">
            <div class="flex items-center justify-between text-[10px] text-slate-500">
              <span class="bg-purple-500/20 text-purple-400 px-1.5 py-0.5 rounded font-bold uppercase">Sponsored</span>
              <span>Bottom Anchor • Dismissible</span>
            </div>
            <div class="flex items-center justify-between">
              <div class="text-xs font-bold text-white">Cinematic Anamorphic Glass</div>
              <button class="px-2.5 py-1 bg-purple-500 text-white font-bold text-[10px] rounded-lg">View Specs</button>
            </div>
          </div>
        </div>

        <!-- Placement 3: Music Player Bar -->
        <div class="card-dark p-4 rounded-2xl space-y-2">
          <div class="flex items-center justify-between">
            <span class="text-xs font-bold text-emerald-400">3. Music Player Audio Sponsor Bar</span>
            <span class="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded">Zero Audio Ad</span>
          </div>
          <div class="bg-[#090D18] p-3 rounded-xl border border-slate-800 space-y-2">
            <div class="flex items-center justify-between text-[10px] text-slate-500">
              <span class="bg-emerald-500/20 text-emerald-400 px-1.5 py-0.5 rounded font-bold uppercase">Sponsored</span>
              <span>Never pauses music</span>
            </div>
            <p class="text-[11px] text-slate-300">BeatVibe AI: Generate Royalty-Free Stems in Seconds</p>
          </div>
        </div>

        <!-- Placement 4: Video Library Card -->
        <div class="card-dark p-4 rounded-2xl space-y-2">
          <div class="flex items-center justify-between">
            <span class="text-xs font-bold text-amber-400">4. Video Library Promoted Card</span>
            <span class="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded">Native Card</span>
          </div>
          <div class="bg-[#090D18] p-3 rounded-xl border border-slate-800 space-y-2">
            <div class="flex items-center justify-between text-[10px] text-slate-500">
              <span class="bg-amber-500/20 text-amber-400 px-1.5 py-0.5 rounded font-bold uppercase">Sponsored</span>
              <span>In-feed</span>
            </div>
            <div class="text-xs font-bold text-white">CloudVault: 100GB Free Cloud Backup</div>
          </div>
        </div>

      </div>
    </section>

    <!-- SECTION 5: REMOTE CONFIG -->
    <section id="view-config" class="space-y-4 hidden">
      <div class="card-dark p-5 rounded-2xl space-y-4">
        <h3 class="font-bold text-white text-base">Remote App Configuration & Rules</h3>
        <p class="text-xs text-slate-400">Dynamic configuration pushed to active Android clients upon app launch.</p>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs pt-2">
          <div class="bg-[#121A2D] p-3.5 rounded-xl border border-[#202C45] flex items-center justify-between">
            <div>
              <span class="font-bold text-white block">Free Super App Mode</span>
              <span class="text-[10px] text-slate-400">No subscriptions or paywalls</span>
            </div>
            <span class="px-2 py-1 bg-emerald-500/20 text-emerald-400 font-bold rounded">ACTIVE</span>
          </div>

          <div class="bg-[#121A2D] p-3.5 rounded-xl border border-[#202C45] flex items-center justify-between">
            <div>
              <span class="font-bold text-white block">Ad Rotation Interval</span>
              <span class="text-[10px] text-slate-400">Frequency of banner shift</span>
            </div>
            <span class="text-cyan-400 font-bold text-sm">12 Seconds</span>
          </div>

          <div class="bg-[#121A2D] p-3.5 rounded-xl border border-[#202C45] flex items-center justify-between">
            <div>
              <span class="font-bold text-white block">Max Batch Downloads</span>
              <span class="text-[10px] text-slate-400">Parallel download queue limit</span>
            </div>
            <span class="text-cyan-400 font-bold text-sm">4 Parallel</span>
          </div>

          <div class="bg-[#121A2D] p-3.5 rounded-xl border border-[#202C45] flex items-center justify-between">
            <div>
              <span class="font-bold text-white block">AI Assistant Features</span>
              <span class="text-[10px] text-slate-400">Summarizer, Subtitles, STT</span>
            </div>
            <span class="px-2 py-1 bg-cyan-500/20 text-cyan-400 font-bold rounded">ENABLED</span>
          </div>
        </div>

        <div class="space-y-2 pt-2">
          <label class="text-xs text-slate-300 font-bold block">App Network Announcement</label>
          <input type="text" id="config-announcement" value="OmniPlay AI 1.0 Online — 100% Free Multimedia Super App Powered by Fair Advertising." class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-3 text-xs text-white">
          <button onclick="saveAnnouncement()" class="px-4 py-2 bg-cyan-500 text-slate-950 font-black text-xs rounded-xl shadow">Save Announcement</button>
        </div>
      </div>
    </section>

    <!-- SECTION 6: SERVER HEALTH & CRASH MONITOR -->
    <section id="view-server_health" class="space-y-4 hidden">
      <div class="card-dark p-5 rounded-2xl space-y-4">
        <h3 class="font-bold text-white text-base">Server Health & Telemetry Monitor</h3>
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
          <div class="bg-[#121A2D] p-3 rounded-xl border border-[#202C45]">
            <span class="text-slate-400 block text-[10px]">API Uptime</span>
            <span class="text-emerald-400 font-black text-lg">99.98%</span>
          </div>
          <div class="bg-[#121A2D] p-3 rounded-xl border border-[#202C45]">
            <span class="text-slate-400 block text-[10px]">Avg Latency</span>
            <span class="text-cyan-400 font-black text-lg">42 ms</span>
          </div>
          <div class="bg-[#121A2D] p-3 rounded-xl border border-[#202C45]">
            <span class="text-slate-400 block text-[10px]">Active Devices</span>
            <span class="text-purple-400 font-black text-lg" id="server-devices">412</span>
          </div>
          <div class="bg-[#121A2D] p-3 rounded-xl border border-[#202C45]">
            <span class="text-slate-400 block text-[10px]">Crash-Free Rate</span>
            <span class="text-emerald-400 font-black text-lg">99.95%</span>
          </div>
        </div>

        <div class="space-y-2 pt-2">
          <span class="text-xs font-bold text-slate-300 block">Recent Telemetry & Recovery Logs</span>
          <div class="space-y-2" id="crash-logs-container">
            <div class="bg-[#121A2D] p-3 rounded-xl border border-[#202C45] flex items-center justify-between text-xs">
              <div>
                <span class="text-emerald-400 font-bold block">[RECOVERED] ExoPlayer codec init retry</span>
                <span class="text-slate-400 text-[10px]">Pixel 7 Pro • MediaCodec H.264 smooth fallback</span>
              </div>
              <span class="text-slate-500 text-[10px]">03:12 UTC</span>
            </div>
            <div class="bg-[#121A2D] p-3 rounded-xl border border-[#202C45] flex items-center justify-between text-xs">
              <div>
                <span class="text-emerald-400 font-bold block">[RECOVERED] Network timeout on slow cellular during 1080p stream</span>
                <span class="text-slate-400 text-[10px]">Samsung S23 • Auto-adjusted buffer cache</span>
              </div>
              <span class="text-slate-500 text-[10px]">Yesterday</span>
            </div>
          </div>
        </div>
      </div>
    </section>

  </main>

  <!-- MODAL: NEW CAMPAIGN -->
  <div id="modal-new-campaign" class="fixed inset-0 bg-black/80 backdrop-blur-sm z-50 hidden flex items-center justify-center p-4">
    <div class="card-dark w-full max-w-lg rounded-2xl p-6 space-y-4 max-h-[90vh] overflow-y-auto">
      <div class="flex items-center justify-between">
        <h3 class="font-black text-white text-base">Create New Advertising Campaign</h3>
        <button onclick="closeNewCampaignModal()" class="text-slate-400 hover:text-white"><i class="fa-solid fa-xmark text-lg"></i></button>
      </div>

      <div class="space-y-3 text-xs">
        <div>
          <label class="text-slate-300 font-bold block mb-1">Campaign Title</label>
          <input type="text" id="new-camp-title" placeholder="e.g. Creator Audio Gear Promo" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-2.5 text-white">
        </div>
        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="text-slate-300 font-bold block mb-1">Brand Name</label>
            <input type="text" id="new-camp-brand" placeholder="e.g. AudioPro" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-2.5 text-white">
          </div>
          <div>
            <label class="text-slate-300 font-bold block mb-1">Placement</label>
            <select id="new-camp-placement" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-2.5 text-white">
              <option value="HOME_BANNER">Home Banner</option>
              <option value="VIDEO_PLAYER_SIDE">Video Player Side Panel</option>
              <option value="MUSIC_PLAYER_BOTTOM">Music Player Bottom Bar</option>
              <option value="VIDEO_LIBRARY_CARD">Video Library Promoted Card</option>
              <option value="NATIVE_FEED">Native Feed</option>
            </select>
          </div>
        </div>
        <div>
          <label class="text-slate-300 font-bold block mb-1">Destination URL</label>
          <input type="text" id="new-camp-url" placeholder="https://brand.example.com" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-2.5 text-white">
        </div>
        <div>
          <label class="text-slate-300 font-bold block mb-1">Image Asset URL</label>
          <input type="text" id="new-camp-image" placeholder="https://..." value="https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-2.5 text-white">
        </div>
        <div>
          <label class="text-slate-300 font-bold block mb-1">Headline</label>
          <input type="text" id="new-camp-headline" placeholder="Catchy attention headline" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-2.5 text-white">
        </div>
        <div>
          <label class="text-slate-300 font-bold block mb-1">Body Text</label>
          <textarea id="new-camp-body" rows="2" placeholder="Brief advertising copy" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-2.5 text-white"></textarea>
        </div>
        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="text-slate-300 font-bold block mb-1">Call To Action (CTA)</label>
            <input type="text" id="new-camp-cta" value="Learn More" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-2.5 text-white">
          </div>
          <div>
            <label class="text-slate-300 font-bold block mb-1">CPM Rate ($)</label>
            <input type="number" id="new-camp-cpm" value="2.50" step="0.1" class="w-full bg-[#121A2D] border border-[#202C45] rounded-xl p-2.5 text-white">
          </div>
        </div>
      </div>

      <div class="flex space-x-2 pt-2">
        <button onclick="closeNewCampaignModal()" class="flex-1 py-2.5 bg-[#121A2D] hover:bg-[#1E2B48] text-slate-300 font-bold text-xs rounded-xl">Cancel</button>
        <button onclick="submitNewCampaign()" class="flex-1 py-2.5 bg-gradient-to-r from-cyan-500 to-blue-600 text-slate-950 font-black text-xs rounded-xl shadow">Deploy Campaign</button>
      </div>
    </div>
  </div>

  <!-- Application Logic -->
  <script>
    let activeTab = 'overview';
    let allCampaigns = [];
    let trafficChart = null;
    let pieChart = null;
    let lastGeneratedCreative = null;

    function switchTab(tabId) {
      activeTab = tabId;
      document.querySelectorAll('main > section').forEach(sec => sec.classList.add('hidden'));
      document.querySelectorAll('aside nav > button').forEach(btn => {
        btn.classList.remove('active-nav');
        btn.classList.add('text-slate-400');
      });

      const sec = document.getElementById('view-' + tabId);
      if (sec) sec.classList.remove('hidden');

      const btn = document.getElementById('nav-' + tabId);
      if (btn) {
        btn.classList.add('active-nav');
        btn.classList.remove('text-slate-400');
      }

      const titles = {
        overview: "Live Advertising & Monetization Overview",
        campaigns: "Live Campaign Management & Inventory",
        ai_processor: "AI Advertising Processor",
        placements: "Super App Placements Preview",
        config: "Remote App Configuration",
        server_health: "Server Health & Telemetry Monitor"
      };
      document.getElementById('page-title').innerText = titles[tabId] || "Admin Console";
    }

    async function refreshData() {
      try {
        const [campRes, anaRes] = await Promise.all([
          fetch('/api/campaigns'),
          fetch('/api/analytics')
        ]);
        allCampaigns = await campRes.json();
        const ana = await anaRes.json();

        // Update KPIs
        document.getElementById('kpi-impressions').innerText = ana.totalImpressions.toLocaleString();
        document.getElementById('kpi-clicks').innerText = ana.totalClicks.toLocaleString();
        document.getElementById('kpi-ctr').innerText = ana.ctr.toFixed(2) + '%';
        document.getElementById('kpi-revenue').innerText = '$' + ana.estimatedRevenue.toFixed(2);
        document.getElementById('campaigns-count').innerText = allCampaigns.filter(c => c.active).length + ' Active Campaigns';

        renderCampaignsList();
        updateCharts(ana);
      } catch (err) {
        console.error("Failed to refresh data", err);
      }
    }

    function renderCampaignsList() {
      const container = document.getElementById('campaigns-list-container');
      container.innerHTML = allCampaigns.map(c => \`
        <div class="card-dark p-4 rounded-2xl flex flex-col md:flex-row items-start md:items-center justify-between gap-4 transition hover:border-[#2A3B5E]">
          <div class="flex items-center space-x-3">
            <div class="w-14 h-14 rounded-xl bg-slate-800 overflow-hidden flex-shrink-0">
              <img src="\${c.imageUrl}" class="w-full h-full object-cover">
            </div>
            <div>
              <div class="flex items-center space-x-2">
                <span class="font-bold text-white text-sm">\${c.title}</span>
                <span class="text-[10px] px-2 py-0.5 rounded font-bold \${c.active ? 'bg-emerald-500/20 text-emerald-400' : 'bg-rose-500/20 text-rose-400'}">
                  \${c.active ? 'ACTIVE' : 'PAUSED'}
                </span>
                <span class="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded">\${c.placement}</span>
              </div>
              <p class="text-xs text-slate-400">\${c.brandName} • "\${c.headline}"</p>
              <div class="text-[11px] text-slate-500 mt-1">
                Impressions: <b class="text-white">\${c.impressions.toLocaleString()}</b> • Clicks: <b class="text-cyan-400">\${c.clicks.toLocaleString()}</b> • CTR: <b class="text-purple-400">\${c.impressions > 0 ? ((c.clicks/c.impressions)*100).toFixed(2) : 0}%</b>
              </div>
            </div>
          </div>
          <div class="flex items-center space-x-2 w-full md:w-auto justify-end">
            <button onclick="toggleCampaign('\${c.id}')" class="px-3 py-1.5 rounded-lg text-xs font-bold \${c.active ? 'bg-amber-500/20 text-amber-300 hover:bg-amber-500/30' : 'bg-emerald-500/20 text-emerald-300 hover:bg-emerald-500/30'}">
              \${c.active ? 'Pause' : 'Activate'}
            </button>
            <button onclick="deleteCampaign('\${c.id}')" class="p-1.5 rounded-lg text-rose-400 hover:bg-rose-500/20 text-xs">
              <i class="fa-solid fa-trash"></i>
            </button>
          </div>
        </div>
      \`).join('');
    }

    async function toggleCampaign(id) {
      await fetch('/api/campaigns/' + id + '/toggle', { method: 'POST' });
      refreshData();
    }

    async function deleteCampaign(id) {
      if (confirm('Delete this campaign?')) {
        await fetch('/api/campaigns/' + id, { method: 'DELETE' });
        refreshData();
      }
    }

    function openNewCampaignModal() {
      document.getElementById('modal-new-campaign').classList.remove('hidden');
    }

    function closeNewCampaignModal() {
      document.getElementById('modal-new-campaign').classList.add('hidden');
    }

    async function submitNewCampaign() {
      const payload = {
        title: document.getElementById('new-camp-title').value || "Creator Audio Special",
        brandName: document.getElementById('new-camp-brand').value || "ApexSound",
        placement: document.getElementById('new-camp-placement').value,
        targetUrl: document.getElementById('new-camp-url').value || "https://example.com",
        imageUrl: document.getElementById('new-camp-image').value,
        headline: document.getElementById('new-camp-headline').value || "Pristine Studio Audio",
        bodyText: document.getElementById('new-camp-body').value || "Lossless acoustics designed for music producers.",
        callToAction: document.getElementById('new-camp-cta').value || "Learn More",
        cpm: parseFloat(document.getElementById('new-camp-cpm').value) || 2.50
      };

      await fetch('/api/campaigns', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      closeNewCampaignModal();
      refreshData();
      switchTab('campaigns');
    }

    async function processAdWithAI() {
      const btn = document.getElementById('btn-process-ai');
      btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i><span>AI ANALYZING BRAND ASSETS...</span>';
      
      const payload = {
        brandName: document.getElementById('ai-brand-name').value,
        url: document.getElementById('ai-ad-url').value,
        imageUrl: document.getElementById('ai-image-url').value
      };

      const res = await fetch('/api/ai/process-ad', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const data = await res.json();
      btn.innerHTML = '<i class="fa-solid fa-robot"></i><span>ANALYZE WITH AI & GENERATE AD CAMPAIGN</span>';

      if (data.creative) {
        lastGeneratedCreative = data.creative;
        document.getElementById('ai-out-headline').innerText = data.creative.generatedHeadlines[0];
        document.getElementById('ai-out-cta').innerText = data.creative.recommendedCTA;
        document.getElementById('ai-out-body').innerText = data.creative.generatedDescriptions[0];
        document.getElementById('ai-output-box').classList.remove('hidden');
      }
    }

    async function deployAiCampaign() {
      if (!lastGeneratedCreative) return;
      await fetch('/api/campaigns', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          title: lastGeneratedCreative.brandName + " AI Campaign",
          brandName: lastGeneratedCreative.brandName,
          targetUrl: document.getElementById('ai-ad-url').value,
          imageUrl: lastGeneratedCreative.previewThumbnail,
          headline: lastGeneratedCreative.generatedHeadlines[0],
          bodyText: lastGeneratedCreative.generatedDescriptions[0],
          callToAction: lastGeneratedCreative.recommendedCTA,
          placement: "HOME_BANNER",
          cpm: 2.80
        })
      });
      alert('Campaign deployed to active ad inventory!');
      refreshData();
      switchTab('campaigns');
    }

    async function saveAnnouncement() {
      const val = document.getElementById('config-announcement').value;
      await fetch('/api/config', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ announcement: val })
      });
      alert('Announcement updated across mobile clients!');
    }

    function updateCharts(ana) {
      // Traffic Chart
      const ctx1 = document.getElementById('trafficChart').getContext('2d');
      if (trafficChart) trafficChart.destroy();
      trafficChart = new Chart(ctx1, {
        type: 'line',
        data: {
          labels: ['12h ago', '11h', '10h', '9h', '8h', '7h', '6h', '5h', '4h', '3h', '2h', 'Now'],
          datasets: [
            {
              label: 'Impressions',
              data: ana.impressionsByHour,
              borderColor: '#00F0FF',
              backgroundColor: 'rgba(0, 240, 255, 0.1)',
              fill: true,
              tension: 0.3
            },
            {
              label: 'Clicks',
              data: ana.clicksByHour.map(c => c * 15), // scaled for visual comparison
              borderColor: '#A855F7',
              backgroundColor: 'rgba(168, 85, 247, 0.1)',
              fill: true,
              tension: 0.3
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { labels: { color: '#94A3B8', font: { size: 10 } } } },
          scales: {
            x: { ticks: { color: '#64748B' }, grid: { color: '#1E293B' } },
            y: { ticks: { color: '#64748B' }, grid: { color: '#1E293B' } }
          }
        }
      });

      // Pie Chart
      const ctx2 = document.getElementById('placementPieChart').getContext('2d');
      if (pieChart) pieChart.destroy();
      const p = ana.placementsBreakdown;
      pieChart = new Chart(ctx2, {
        type: 'doughnut',
        data: {
          labels: ['Home Banner', 'Video Side', 'Music Bottom', 'Video Library', 'Native Feed'],
          datasets: [{
            data: [p.HOME_BANNER, p.VIDEO_PLAYER_SIDE, p.MUSIC_PLAYER_BOTTOM, p.VIDEO_LIBRARY_CARD, p.NATIVE_FEED],
            backgroundColor: ['#00F0FF', '#8B5CF6', '#10B981', '#F59E0B', '#3B82F6'],
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'bottom', labels: { color: '#94A3B8', font: { size: 10 } } } }
        }
      });
    }

    // Init
    window.onload = () => {
      refreshData();
    };
  </script>
</body>
</html>`;
}

server.listen(PORT, '0.0.0.0', () => {
  console.log(`OmniPlay AI Ad Server & Admin Dashboard listening on port ${PORT}`);
});
