package com.snowgemm.glad.platform.launcher

import android.content.ComponentName
import android.content.pm.LauncherActivityInfo
import android.os.UserHandle

/**
 * A launchable activity in a specific Android user/profile.
 *
 * Keep the profile handle with the component so launches and identity remain correct
 * for personal and managed-profile copies of the same package.
 */
data class LaunchableApp internal constructor(
    val id: String,
    val label: String,
    internal val activityInfo: LauncherActivityInfo,
) {
    val componentName: ComponentName
        get() = activityInfo.componentName

    val user: UserHandle
        get() = activityInfo.user

    val packageName: String
        get() = componentName.packageName
}
