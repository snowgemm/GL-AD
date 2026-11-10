package com.snowgemm.glad.feature.home

import com.snowgemm.glad.platform.launcher.LaunchableApp

data class HomeUiState(
    val apps: List<LaunchableApp> = emptyList(),
    val isLoading: Boolean = true,
    val isHomeRoleAvailable: Boolean = true,
    val isHomeRoleHeld: Boolean = false,
    val message: String? = null,
)
