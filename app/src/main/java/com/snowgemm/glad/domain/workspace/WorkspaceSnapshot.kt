package com.snowgemm.glad.domain.workspace

data class WorkspacePage(
    val id: String,
    val appIds: List<String> = emptyList(),
)

data class WorkspaceSnapshot(
    val schemaVersion: Int = WorkspaceRules.CURRENT_SCHEMA_VERSION,
    val pages: List<WorkspacePage> = listOf(WorkspacePage(id = "page-1")),
    val dockAppIds: List<String> = emptyList(),
)

object WorkspaceRules {
    const val CURRENT_SCHEMA_VERSION = 1
    const val MAX_PAGES = 12
    const val MAX_APPS_PER_PAGE = 24
    const val MAX_DOCK_APPS = 5
    const val MAX_APP_ID_LENGTH = 512

    fun validate(snapshot: WorkspaceSnapshot): WorkspaceSnapshot {
        require(snapshot.schemaVersion == CURRENT_SCHEMA_VERSION) {
            "Unsupported workspace schema version: ${snapshot.schemaVersion}"
        }
        require(snapshot.pages.isNotEmpty()) { "Workspace must contain at least one page." }
        require(snapshot.pages.size <= MAX_PAGES) { "Workspace exceeds the page limit." }
        require(snapshot.dockAppIds.size <= MAX_DOCK_APPS) { "Dock exceeds the item limit." }

        val pageIds = HashSet<String>()
        val placedAppIds = HashSet<String>()

        snapshot.pages.forEach { page ->
            require(page.id.isNotBlank()) { "Workspace page ID cannot be blank." }
            require(pageIds.add(page.id)) { "Duplicate workspace page ID: ${page.id}" }
            require(page.appIds.size <= MAX_APPS_PER_PAGE) {
                "Workspace page ${page.id} exceeds the item limit."
            }

            page.appIds.forEach { appId ->
                validateAppId(appId)
                require(placedAppIds.add(appId)) { "App is placed more than once: $appId" }
            }
        }

        snapshot.dockAppIds.forEach { appId ->
            validateAppId(appId)
            require(placedAppIds.add(appId)) {
                "App cannot be present in both workspace and dock: $appId"
            }
        }

        return snapshot.copy(
            pages = snapshot.pages.dropLastWhile { it.appIds.isEmpty() }
                .ifEmpty { listOf(WorkspacePage(id = snapshot.pages.first().id)) },
        )
    }

    private fun validateAppId(appId: String) {
        require(appId.isNotBlank()) { "App ID cannot be blank." }
        require(appId.length <= MAX_APP_ID_LENGTH) { "App ID is too long." }
    }
}

object WorkspaceOperations {
    fun pinToHome(
        snapshot: WorkspaceSnapshot,
        appId: String,
    ): WorkspaceSnapshot {
        val current = WorkspaceRules.validate(snapshot)
        require(appId.isNotBlank()) { "App ID cannot be blank." }
        if (current.pages.any { appId in it.appIds }) return current

        val pages = current.pages.toMutableList()
        val dock = current.dockAppIds - appId
        val targetIndex = pages.indexOfFirst {
            it.appIds.size < WorkspaceRules.MAX_APPS_PER_PAGE
        }

        if (targetIndex >= 0) {
            val target = pages[targetIndex]
            pages[targetIndex] = target.copy(appIds = target.appIds + appId)
        } else {
            require(pages.size < WorkspaceRules.MAX_PAGES) {
                "Workspace has reached its page limit."
            }
            pages += WorkspacePage(
                id = nextPageId(pages),
                appIds = listOf(appId),
            )
        }

        return WorkspaceRules.validate(
            current.copy(pages = pages, dockAppIds = dock),
        )
    }

    fun moveToDock(
        snapshot: WorkspaceSnapshot,
        appId: String,
    ): WorkspaceSnapshot {
        val current = WorkspaceRules.validate(snapshot)
        require(appId.isNotBlank()) { "App ID cannot be blank." }
        if (appId in current.dockAppIds) return current

        require(current.dockAppIds.size < WorkspaceRules.MAX_DOCK_APPS) {
            "Dock is full."
        }

        val pages = current.pages.map { page ->
            page.copy(appIds = page.appIds - appId)
        }

        return WorkspaceRules.validate(
            current.copy(
                pages = pages,
                dockAppIds = current.dockAppIds + appId,
            ),
        )
    }

    fun removeApp(
        snapshot: WorkspaceSnapshot,
        appId: String,
    ): WorkspaceSnapshot {
        val current = WorkspaceRules.validate(snapshot)
        return WorkspaceRules.validate(
            current.copy(
                pages = current.pages.map { page ->
                    page.copy(appIds = page.appIds - appId)
                },
                dockAppIds = current.dockAppIds - appId,
            ),
        )
    }

    fun reorderPage(
        snapshot: WorkspaceSnapshot,
        pageId: String,
        fromIndex: Int,
        toIndex: Int,
    ): WorkspaceSnapshot {
        val current = WorkspaceRules.validate(snapshot)
        val pageIndex = current.pages.indexOfFirst { it.id == pageId }
        require(pageIndex >= 0) { "Unknown workspace page: $pageId" }

        val page = current.pages[pageIndex]
        require(fromIndex in page.appIds.indices) { "Source index is outside the page." }
        require(toIndex in page.appIds.indices) { "Destination index is outside the page." }

        if (fromIndex == toIndex) return current

        val reordered = page.appIds.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
        val pages = current.pages.toMutableList().apply {
            this[pageIndex] = page.copy(appIds = reordered)
        }

        return current.copy(pages = pages)
    }

    private fun nextPageId(pages: List<WorkspacePage>): String {
        val usedIds = pages.mapTo(HashSet(), WorkspacePage::id)
        var suffix = pages.size + 1
        while ("page-$suffix" in usedIds) suffix++
        return "page-$suffix"
    }
}
