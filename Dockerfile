# Multi-stage Dockerfile for Cloud AI Video Production Platform
FROM python:3.11-slim as base

# Install system dependencies for FFmpeg, Pillow, and audio processing
RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    libsm6 \
    libxext6 \
    libgl1-mesa-glx \
    fonts-dejavu-core \
    fonts-liberation \
    curl \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY backend/ ./backend/
COPY templates/ ./templates/

# Environment variables
ENV PYTHONUNBUFFERED=1 \
    PORT=8080 \
    APP_ENV=production \
    STORAGE_DIR=/tmp/cinecraft_storage

EXPOSE 8080

CMD ["uvicorn", "backend.main:app", "--host", "0.0.0.0", "--port", "8080"]
