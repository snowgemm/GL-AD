package com.snowgemm.glad.data.workspace

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.snowgemm.glad.domain.workspace.WorkspaceOperations
import com.snowgemm.glad.domain.workspace.WorkspaceSnapshot
import java.io.File

/**
 * Owns one DataStore instance per application process.
 *
 * DataStore must not be constructed repeatedly for the same file.
 */
object WorkspaceDataStore {
    @Volatile
    private var instance: DataStore<WorkspaceSnapshot>? = null

    fun get(context: Context): DataStore<WorkspaceSnapshot> {
        return instance ?: synchronized(this) {
            instance ?: create(context.applicationContext).also { instance = it }
        }
    }

    private fun create(context: Context): DataStore<WorkspaceSnapshot> {
        val workspaceDirectory = File(context.filesDir, "workspace").apply {
            if (!exists()) mkdirs()
            check(isDirectory) { "Unable to create workspace storage directory." }
        }

        return DataStoreFactory.create(
            serializer = WorkspaceSerializer,
            produceFile = { File(workspaceDirectory, "workspace.json") },
        )
    }
}

class WorkspaceRepository(context: Context) {
    private val store = WorkspaceDataStore.get(context)

    val snapshot = store.data

    suspend fun pinToHome(appId: String) {
        store.updateData { current ->
            WorkspaceOperations.pinToHome(current, appId)
        }
    }

    suspend fun moveToDock(appId: String) {
        store.updateData { current ->
            WorkspaceOperations.moveToDock(current, appId)
        }
    }

    suspend fun removeApp(appId: String) {
        store.updateData { current ->
            WorkspaceOperations.removeApp(current, appId)
        }
    }

    suspend fun reorderPage(pageId: String, fromIndex: Int, toIndex: Int) {
        store.updateData { current ->
            WorkspaceOperations.reorderPage(current, pageId, fromIndex, toIndex)
        }
    }
}
