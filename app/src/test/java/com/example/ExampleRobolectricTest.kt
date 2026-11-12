package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.EqualizerPreset
import com.example.data.model.MediaType
import com.example.data.repository.OmniRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("OmniPlay AI", appName)
    }

    @Test
    fun `verify repository media initialization`() {
        val repo = OmniRepository()
        val media = repo.mediaList.value
        assertTrue(media.isNotEmpty())
        assertTrue(media.any { it.mediaType == MediaType.VIDEO })
        assertTrue(media.any { it.mediaType == MediaType.AUDIO })
    }

    @Test
    fun `verify equalizer presets configuration`() {
        val repo = OmniRepository()
        repo.setEqualizerPreset(EqualizerPreset.BASS_BOOST)
        assertEquals(EqualizerPreset.BASS_BOOST, repo.equalizerState.value.preset)
        assertEquals(5, repo.equalizerState.value.bands.size)
    }

    @Test
    fun `verify authorized downloads engine`() {
        val repo = OmniRepository()
        repo.startDownload("https://example.com/test.mp4", "Test 4K Video", "MP4", "1080p Full HD")
        val downloads = repo.downloads.value
        assertTrue(downloads.any { it.title == "Test 4K Video" })
    }
}
