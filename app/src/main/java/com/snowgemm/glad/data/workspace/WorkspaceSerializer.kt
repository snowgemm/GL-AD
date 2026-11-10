package com.snowgemm.glad.data.workspace

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.snowgemm.glad.domain.workspace.WorkspacePage
import com.snowgemm.glad.domain.workspace.WorkspaceRules
import com.snowgemm.glad.domain.workspace.WorkspaceSnapshot
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

object WorkspaceSerializer : Serializer<WorkspaceSnapshot> {
    override val defaultValue: WorkspaceSnapshot = WorkspaceSnapshot()

    override suspend fun readFrom(input: InputStream): WorkspaceSnapshot {
        val source = input.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        if (source.isBlank()) return defaultValue

        return try {
            val root = JSONObject(source)
            val version = root.getInt("schemaVersion")
            val pagesJson = root.getJSONArray("pages")
            val dockJson = root.getJSONArray("dockAppIds")

            val pages = buildList {
                for (index in 0 until pagesJson.length()) {
                    val pageJson = pagesJson.getJSONObject(index)
                    add(
                        WorkspacePage(
                            id = pageJson.getString("id"),
                            appIds = pageJson.getJSONArray("appIds").toStringList(),
                        ),
                    )
                }
            }

            WorkspaceRules.validate(
                WorkspaceSnapshot(
                    schemaVersion = version,
                    pages = pages,
                    dockAppIds = dockJson.toStringList(),
                ),
            )
        } catch (exception: CorruptionException) {
            throw exception
        } catch (exception: Exception) {
            throw CorruptionException("Workspace data is invalid.", exception)
        }
    }

    override suspend fun writeTo(
        t: WorkspaceSnapshot,
        output: OutputStream,
    ) {
        val snapshot = WorkspaceRules.validate(t)
        val pagesJson = JSONArray().apply {
            snapshot.pages.forEach { page ->
                put(
                    JSONObject()
                        .put("id", page.id)
                        .put("appIds", JSONArray(page.appIds)),
                )
            }
        }

        val root = JSONObject()
            .put("schemaVersion", snapshot.schemaVersion)
            .put("pages", pagesJson)
            .put("dockAppIds", JSONArray(snapshot.dockAppIds))

        output.write(root.toString().toByteArray(StandardCharsets.UTF_8))
    }

    private fun JSONArray.toStringList(): List<String> = buildList {
        for (index in 0 until length()) {
            add(getString(index))
        }
    }
}
