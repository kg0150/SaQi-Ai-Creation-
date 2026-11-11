package com.example

import com.example.data.model.*
import com.example.data.repository.VideoProductionRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testDefaultProjectInitialization() {
        val repo = VideoProductionRepository()
        val project = repo.currentProject.value
        assertNotNull(project)
        assertEquals("cinemap-pk-sa", project?.id)
        assertEquals(5, project?.scenes?.size)
        assertEquals(13, project?.agentEvaluations?.size)
        assertEquals(ProjectStatus.NEEDS_FIX, project?.status)
        assertEquals(89, project?.qualityScores?.overallQuality)
        assertFalse(project?.qualityScores?.isApproved() == true)
    }

    @Test
    fun testAutoRepairFeedbackLoop() = runBlocking {
        val repo = VideoProductionRepository()
        val project = repo.currentProject.value
        assertNotNull(project)

        // Before auto repair: Agent 5 flagged, overall score 89 (< 90)
        val agent5Before = project?.agentEvaluations?.find { it.agentId == 5 }
        assertEquals(AgentStatus.FLAGGED, agent5Before?.status)

        // Execute auto repair loop
        repo.executeAutoRepairLoop { }

        // After auto repair: Voice-over QA approved (98), overall score 97 (>= 90 approved)
        val agent5After = project?.agentEvaluations?.find { it.agentId == 5 }
        assertEquals(AgentStatus.APPROVED, agent5After?.status)
        assertEquals(98, agent5After?.score)

        val masterAgent = project?.agentEvaluations?.find { it.agentId == 13 }
        assertEquals(AgentStatus.APPROVED, masterAgent?.status)
        assertEquals(97, masterAgent?.score)

        assertEquals(97, project?.qualityScores?.overallQuality)
        assertTrue(project?.qualityScores?.isApproved() == true)
        assertEquals(ProjectStatus.APPROVED, project?.status)
    }

    @Test
    fun testSceneDuplicationAndDeletion() {
        val repo = VideoProductionRepository()
        val initialCount = repo.currentProject.value?.scenes?.size ?: 0

        repo.duplicateScene(1)
        val afterDuplicate = repo.currentProject.value?.scenes?.size ?: 0
        assertEquals(initialCount + 1, afterDuplicate)

        val lastScene = repo.currentProject.value?.scenes?.last()
        assertNotNull(lastScene)
        repo.deleteScene(lastScene!!.id)
        val afterDelete = repo.currentProject.value?.scenes?.size ?: 0
        assertEquals(initialCount, afterDelete)
    }
}
