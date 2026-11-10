package com.snowgemm.glad.domain.workspace

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class WorkspaceOperationsTest {
    @Test
    fun pinningAnAppIsIdempotent() {
        val once = WorkspaceOperations.pinToHome(WorkspaceSnapshot(), "maps")
        val twice = WorkspaceOperations.pinToHome(once, "maps")

        assertEquals(once, twice)
    }

    @Test
    fun pinningMovesAppOutOfDock() {
        val initial = WorkspaceSnapshot(dockAppIds = listOf("maps"))
        val result = WorkspaceOperations.pinToHome(initial, "maps")

        assertEquals(listOf("maps"), result.pages.single().appIds)
        assertEquals(emptyList<String>(), result.dockAppIds)
    }

    @Test
    fun movingToDockRemovesAppFromHome() {
        val initial = WorkspaceSnapshot(
            pages = listOf(WorkspacePage("page-1", listOf("maps", "camera"))),
        )
        val result = WorkspaceOperations.moveToDock(initial, "maps")

        assertEquals(listOf("camera"), result.pages.single().appIds)
        assertEquals(listOf("maps"), result.dockAppIds)
    }

    @Test
    fun dockCapacityIsEnforced() {
        val fullDock = WorkspaceSnapshot(
            dockAppIds = listOf("a", "b", "c", "d", "e"),
        )

        assertThrows(IllegalArgumentException::class.java) {
            WorkspaceOperations.moveToDock(fullDock, "f")
        }
    }

    @Test
    fun duplicatePlacementIsRejected() {
        val invalid = WorkspaceSnapshot(
            pages = listOf(WorkspacePage("page-1", listOf("maps"))),
            dockAppIds = listOf("maps"),
        )

        assertThrows(IllegalArgumentException::class.java) {
            WorkspaceRules.validate(invalid)
        }
    }

    @Test
    fun pageReorderPreservesAppIdentity() {
        val initial = WorkspaceSnapshot(
            pages = listOf(WorkspacePage("page-1", listOf("maps", "camera", "clock"))),
        )
        val result = WorkspaceOperations.reorderPage(initial, "page-1", 0, 2)

        assertEquals(listOf("camera", "clock", "maps"), result.pages.single().appIds)
    }

    @Test
    fun removingLastAppKeepsOneEmptyPage() {
        val initial = WorkspaceSnapshot(
            pages = listOf(
                WorkspacePage("page-1", listOf("maps")),
                WorkspacePage("page-2", emptyList()),
            ),
        )
        val result = WorkspaceOperations.removeApp(initial, "maps")

        assertEquals(1, result.pages.size)
        assertEquals(emptyList<String>(), result.pages.single().appIds)
    }

    @Test
    fun schemaVersionIsValidated() {
        assertThrows(IllegalArgumentException::class.java) {
            WorkspaceRules.validate(WorkspaceSnapshot(schemaVersion = 2))
        }
    }
}
