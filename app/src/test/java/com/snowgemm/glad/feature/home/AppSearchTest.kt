package com.snowgemm.glad.feature.home

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppSearchTest {
    @Test
    fun blankQueryMatchesEveryApp() {
        assertTrue(appMatchesQuery("Maps", "com.example.maps", ""))
        assertTrue(appMatchesQuery("Maps", "com.example.maps", "   "))
    }

    @Test
    fun queryMatchesLabelWithoutCaseSensitivity() {
        assertTrue(appMatchesQuery("YouTube Music", "com.google.android.apps.youtube.music", "youtube"))
    }

    @Test
    fun queryMatchesPackageName() {
        assertTrue(appMatchesQuery("Maps", "com.example.maps", "example.maps"))
    }

    @Test
    fun unmatchedQueryIsRejected() {
        assertFalse(appMatchesQuery("Maps", "com.example.maps", "calendar"))
    }

    @Test
    fun surroundingWhitespaceIsIgnored() {
        assertTrue(appMatchesQuery("Maps", "com.example.maps", "  maps  "))
    }
}
