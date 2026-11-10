package com.snowgemm.glad.feature.home

import android.app.Application
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.snowgemm.glad.platform.launcher.AndroidLauncherAppsRepository
import com.snowgemm.glad.platform.launcher.LaunchableApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val launcherRepository = AndroidLauncherAppsRepository(application)
    private val roleManager = application.getSystemService(RoleManager::class.java)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refreshHomeRole()

        viewModelScope.launch {
            launcherRepository.observeApps()
                .catch {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            message = "Unable to load installed apps.",
                        )
                    }
                }
                .collect { apps ->
                    _uiState.update { state ->
                        state.copy(apps = apps, isLoading = false)
                    }
                }
        }
    }

    fun refreshHomeRole() {
        val available = roleManager.isRoleAvailable(RoleManager.ROLE_HOME)
        val held = available && roleManager.isRoleHeld(RoleManager.ROLE_HOME)

        _uiState.update {
            it.copy(
                isHomeRoleAvailable = available,
                isHomeRoleHeld = held,
            )
        }
    }

    fun createHomeRoleRequestIntent(): Intent? {
        return if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        ) {
            roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
        } else {
            null
        }
    }

    suspend fun loadIcon(app: LaunchableApp) = launcherRepository.loadIcon(app)

    fun launchApp(app: LaunchableApp) {
        try {
            launcherRepository.launch(app)
            dismissMessage()
        } catch (_: ActivityNotFoundException) {
            showMessage("This app is no longer available.")
        } catch (_: SecurityException) {
            showMessage("Android blocked this app launch.")
        } catch (_: IllegalStateException) {
            showMessage("This app is currently unavailable.")
        }
    }

    fun showHomeRoleUnavailable() {
        showMessage("The Home role is not available on this device.")
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }
}
