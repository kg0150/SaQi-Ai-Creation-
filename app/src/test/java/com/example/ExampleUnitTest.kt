package com.example

import com.example.data.model.AdPlacement
import com.example.data.model.DownloadStatus
import com.example.data.model.DownloadTask
import com.example.data.model.EqualizerPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun downloadTask_progressCalculation() {
        val task = DownloadTask(
            id = "t1",
            title = "Test Video",
            sourceUrl = "https://example.com/vid.mp4",
            totalBytes = 1000000L,
            downloadedBytes = 500000L,
            status = DownloadStatus.DOWNLOADING
        )
        assertEquals(0.5f, task.progress, 0.01f)
    }

    @Test
    fun equalizerPresets_bandCountVerification() {
        EqualizerPreset.values().forEach { preset ->
            assertEquals(5, preset.bandGains.size)
        }
    }

    @Test
    fun adPlacement_typesExist() {
        val placements = AdPlacement.values()
        assertTrue(placements.contains(AdPlacement.HOME_BANNER))
        assertTrue(placements.contains(AdPlacement.VIDEO_PLAYER_SIDE))
        assertTrue(placements.contains(AdPlacement.MUSIC_PLAYER_BOTTOM))
    }
}
