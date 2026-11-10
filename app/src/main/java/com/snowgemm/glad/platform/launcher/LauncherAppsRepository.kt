package com.snowgemm.glad.platform.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.LauncherApps
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.UserManager
import android.util.LruCache
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
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
    private val userManager = appContext.getSystemService(UserManager::class.java)
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
        fun notifyPackagesChanged() {
            iconCache.evictAll()
            trySend(Unit)
        }

        val callback = object : LauncherApps.Callback() {
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

            override fun onPackagesSuspended(
                packageNames: Array<out String>,
                user: android.os.UserHandle,
            ) {
                notifyPackagesChanged()
            }

            override fun onPackagesUnsuspended(
                packageNames: Array<out String>,
                user: android.os.UserHandle,
            ) {
                notifyPackagesChanged()
            }
        }

        val profileReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                notifyPackagesChanged()
            }
        }
        val profileFilter = IntentFilter().apply {
            addAction(Intent.ACTION_MANAGED_PROFILE_ADDED)
            addAction(Intent.ACTION_MANAGED_PROFILE_REMOVED)
            addAction(Intent.ACTION_MANAGED_PROFILE_AVAILABLE)
            addAction(Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE)
        }

        launcherApps.registerCallback(callback, Handler(Looper.getMainLooper()))
        ContextCompat.registerReceiver(
            appContext,
            profileReceiver,
            profileFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        trySend(Unit)

        awaitClose {
            launcherApps.unregisterCallback(callback)
            appContext.unregisterReceiver(profileReceiver)
        }
    }
        .conflate()
        .map {
            withContext(Dispatchers.IO) {
                queryLaunchableApps()
            }
        }

    private fun queryLaunchableApps(): List<LaunchableApp> {
        val profiles = try {
            launcherApps.profiles
        } catch (_: SecurityException) {
            return emptyList()
        }

        return profiles
            .flatMap { profile ->
                try {
                    launcherApps.getActivityList(null, profile).mapNotNull { activityInfo ->
                        val component = activityInfo.componentName
                        val profileSerial = userManager.getSerialNumberForUser(activityInfo.user)
                        if (profileSerial < 0L) return@mapNotNull null

                        LaunchableApp(
                            id = "${component.flattenToString()}@${profileSerial}",
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
