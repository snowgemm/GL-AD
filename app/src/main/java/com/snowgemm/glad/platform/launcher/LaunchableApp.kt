package com.snowgemm.glad.platform.launcher

/**
 * A launchable activity scoped to one Android user/profile.
 *
 * The profile serial keeps two copies of the same package distinct and can be
 * persisted without storing framework UserHandle instances.
 */
data class LaunchableApp(
    val componentName: String,
    val packageName: String,
    val userSerial: Long,
    val label: String,
) {
    val id: String
        get() = "$componentName@$userSerial"
}
