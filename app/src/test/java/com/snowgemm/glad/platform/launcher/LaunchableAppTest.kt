package com.snowgemm.glad.platform.launcher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class LaunchableAppTest {
    @Test
    fun sameComponentInDifferentProfilesHasDifferentIdentity() {
        val personal = LaunchableApp(
            componentName = "com.example.app/com.example.app.MainActivity",
            packageName = "com.example.app",
            userSerial = 10L,
            label = "Example",
        )
        val work = personal.copy(userSerial = 11L)

        assertNotEquals(personal.id, work.id)
    }

    @Test
    fun identityIsStableForTheSameComponentAndProfile() {
        val first = LaunchableApp(
            componentName = "com.example.app/com.example.app.MainActivity",
            packageName = "com.example.app",
            userSerial = 10L,
            label = "Example",
        )
        val refreshed = first.copy(label = "Example updated")

        assertEquals(first.id, refreshed.id)
    }
}
