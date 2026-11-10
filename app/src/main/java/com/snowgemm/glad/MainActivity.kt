package com.snowgemm.glad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.snowgemm.glad.feature.home.GLADApp
import com.snowgemm.glad.feature.home.HomeViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    private val homeRoleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        viewModel.refreshHomeRole()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            GLADApp(
                uiState = uiState,
                onRequestHomeRole = {
                    val requestIntent = viewModel.createHomeRoleRequestIntent()
                    if (requestIntent != null) {
                        homeRoleLauncher.launch(requestIntent)
                    } else {
                        viewModel.showHomeRoleUnavailable()
                    }
                },
                onLaunchApp = viewModel::launchApp,
                onLoadIcon = viewModel::loadIcon,
                onDismissMessage = viewModel::dismissMessage,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshHomeRole()
    }
}
