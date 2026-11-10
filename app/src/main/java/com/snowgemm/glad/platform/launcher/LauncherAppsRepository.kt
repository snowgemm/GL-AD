package com.snowgemm.glad.platform.launcher

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.content.pm.LauncherApps
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface LauncherAppsRepository {
    fun observeApps(): Flow<List<LaunchableApp>>

    suspend fun loadIcon(app: LaunchableApp): Bitmap?

    fun launch(app: LaunchableApp)
}

class AndroidLauncherAppsRepository(
    context: Context,
) : LauncherAppsRepository {
    private val appContext = context.applicationContext
    private val launcherApps = appContext.getSystemService(LauncherApps::class.java)
    private val densityDpi = appContext.resources.displayMetrics.densityDpi
    private val iconSizePx = (52 * appContext.resources.displayMetrics.density)
        .toInt()
        .coerceIn(96, 192)

    private val iconCache = object : LruCache<String, Bitmap>(8 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return (value.byteCount / 1024).coerceAtLeast(1)
        }
    }

    override fun observeApps(): Flow<List<LaunchableApp>> = callbackFlow {
        val callback = object : LauncherApps.Callback() {
            private fun notifyPackagesChanged() {
                iconCache.evictAll()
                trySend(Unit)
            }

            override fun onPackageAdded(packageName: String, user: android.os.UserHandle) {
                notifyPackagesChanged()
            }

            override fun onPackageChanged(packageName: String, user: android.os.UserHandle) {
                notifyPackagesChanged()
            }

            override fun onPackageRemoved(packageName: String, user: android.os.UserHandle) {
                notifyPackagesChanged()
            }

            override fun onPackagesAvailable(
                packageNames: Array<out String>,
                user: android.os.UserHandle,
                replacing: Boolean,
            ) {
                notifyPackagesChanged()
            }

            override fun onPackagesUnavailable(
                packageNames: Array<out String>,
                user: android.os.UserHandle,
                replacing: Boolean,
            ) {
                notifyPackagesChanged()
            }
        }

        launcherApps.registerCallback(callback, Handler(Looper.getMainLooper()))
        trySend(Unit)

        awaitClose {
            launcherApps.unregisterCallback(callback)
        }
    }
        .conflate()
        .map {
            withContext(Dispatchers.IO) {
                queryLaunchableApps()
            }
        }
        .distinctUntilChanged { previous, current ->
            previous.map { it.id to it.label } == current.map { it.id to it.label }
        }

    private fun queryLaunchableApps(): List<LaunchableApp> {
        return launcherApps.profiles
            .flatMap { profile ->
                try {
                    launcherApps.getActivityList(null, profile).map { activityInfo ->
                        val component = activityInfo.componentName
                        val profileId = activityInfo.user.identifier
                        val id = "${component.flattenToString()}@$profileId"

                        LaunchableApp(
                            id = id,
                            label = activityInfo.label
                                .toString()
                                .ifBlank { component.packageName },
                            activityInfo = activityInfo,
                        )
                    }
                } catch (_: SecurityException) {
                    emptyList()
                } catch (_: IllegalStateException) {
                    emptyList()
                }
            }
            .distinctBy(LaunchableApp::id)
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, LaunchableApp::label))
    }

    override suspend fun loadIcon(app: LaunchableApp): Bitmap? = withContext(Dispatchers.IO) {
        iconCache.get(app.id)?.let { return@withContext it }

        try {
            val drawable: Drawable = app.activityInfo.getBadgedIcon(densityDpi)
            val bitmap = drawable.toBitmap(
                width = iconSizePx,
                height = iconSizePx,
                config = Bitmap.Config.ARGB_8888,
            )
            iconCache.put(app.id, bitmap)
            bitmap
        } catch (_: RuntimeException) {
            null
        }
    }

    override fun launch(app: LaunchableApp) {
        launcherApps.startMainActivity(
            app.componentName,
            app.user,
            null,
            null,
        )
    }
}
