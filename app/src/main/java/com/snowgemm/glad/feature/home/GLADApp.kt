package com.snowgemm.glad.feature.home

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyRowItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.snowgemm.glad.domain.workspace.WorkspacePage
import com.snowgemm.glad.design.glass.GLADGlassMaterial
import com.snowgemm.glad.design.glass.GLADGlassNavigation
import com.snowgemm.glad.design.glass.GLADGlassSurface
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import com.snowgemm.glad.platform.launcher.LaunchableApp

private enum class LauncherTab {
    Home,
    Apps,
}

@Composable
fun GLADApp(
    uiState: HomeUiState,
    onRequestHomeRole: () -> Unit,
    onLaunchApp: (LaunchableApp) -> Unit,
    onLoadIcon: suspend (LaunchableApp) -> Bitmap?,
    onPinAppToHome: (String) -> Unit,
    onMoveAppToDock: (String) -> Unit,
    onRemoveAppFromWorkspace: (String) -> Unit,
    onDismissMessage: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(LauncherTab.Home) }
    var query by rememberSaveable { mutableStateOf("") }
    val hazeState = rememberHazeState()

    val appsById = remember(uiState.apps) {
        uiState.apps.associateBy(LaunchableApp::id)
    }
    val pinnedIds = remember(uiState.workspace) {
        uiState.workspace.pages.flatMapTo(HashSet()) { it.appIds }
    }
    val dockIds = remember(uiState.workspace) {
        uiState.workspace.dockAppIds.toSet()
    }
    val visibleApps = remember(uiState.apps, query) {
        uiState.apps.filter { app ->
            appMatchesQuery(app.label, app.packageName, query)
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color.Black,
            surface = Color(0xFF151518),
            primary = Color(0xFFD9E2FF),
            onSurface = Color.White,
        ),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.20f))
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LauncherHeader(isHomeRoleHeld = uiState.isHomeRoleHeld)

                if (!uiState.isHomeRoleHeld) {
                    HomeRoleCard(
                        isAvailable = uiState.isHomeRoleAvailable,
                        onRequestHomeRole = onRequestHomeRole,
                    )
                }

                GLADGlassNavigation(
                    selectedIndex = if (selectedTab == LauncherTab.Home) 0 else 1,
                    labels = listOf("Home", "Apps"),
                    onSelect = { index ->
                        selectedTab = if (index == 0) LauncherTab.Home else LauncherTab.Apps
                    },
                )

                if (selectedTab == LauncherTab.Apps) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Search apps") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.White.copy(alpha = 0.09f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.06f),
                            focusedBorderColor = Color.White.copy(alpha = 0.28f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            cursorColor = Color.White,
                        ),
                    )
                }

                when {
                    uiState.isLoading || uiState.isWorkspaceLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }

                    selectedTab == LauncherTab.Home -> {
                        HomeWorkspace(
                            pages = uiState.workspace.pages,
                            dockIds = uiState.workspace.dockAppIds,
                            appsById = appsById,
                            onLaunchApp = onLaunchApp,
                            onLoadIcon = onLoadIcon,
                            onMoveToDock = onMoveAppToDock,
                            onRemove = onRemoveAppFromWorkspace,
                            hazeState = hazeState,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    else -> {
                        AppDrawer(
                            apps = visibleApps,
                            pinnedIds = pinnedIds,
                            dockIds = dockIds,
                            onLaunchApp = onLaunchApp,
                            onLoadIcon = onLoadIcon,
                            onPinToHome = onPinAppToHome,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                uiState.message?.let { message ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF29292E))
                            .padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.weight(1f),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        TextButton(onClick = onDismissMessage) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LauncherHeader(isHomeRoleHeld: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "GL-AD",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Text(
                text = if (isHomeRoleHeld) "Default launcher" else "Launcher preview",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.68f),
            )
        }
        Text(
            text = "ANDROID",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.58f),
        )
    }
}

@Composable
private fun TabItem(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Color.White.copy(alpha = 0.16f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun HomeWorkspace(
    pages: List<WorkspacePage>,
    dockIds: List<String>,
    appsById: Map<String, LaunchableApp>,
    onLaunchApp: (LaunchableApp) -> Unit,
    onLoadIcon: suspend (LaunchableApp) -> Bitmap?,
    onMoveToDock: (String) -> Unit,
    onRemove: (String) -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "HOME",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )

        val pagerState = rememberPagerState(pageCount = { pages.size.coerceAtLeast(1) })
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .hazeSource(hazeState),
            key = { pageIndex -> pages.getOrNull(pageIndex)?.id ?: "empty-home" },
        ) { pageIndex ->
            val page = pages.getOrNull(pageIndex) ?: WorkspacePage("empty-home")
            if (page.appIds.isEmpty()) {
                EmptyHomePage()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 78.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(
                        items = page.appIds,
                        key = { it },
                        contentType = { "workspace-app" },
                    ) { appId ->
                        val app = appsById[appId]
                        if (app == null) {
                            MissingAppTile(
                                onRemove = { onRemove(appId) },
                            )
                        } else {
                            AppTile(
                                app = app,
                                onLoadIcon = onLoadIcon,
                                onClick = { onLaunchApp(app) },
                                actionLabel = "Dock",
                                onAction = { onMoveToDock(appId) },
                            )
                        }
                    }
                }
            }
        }

        if (pages.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (pagerState.currentPage == index) 7.dp else 5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (pagerState.currentPage == index) {
                                    Color.White
                                } else {
                                    Color.White.copy(alpha = 0.34f)
                                },
                            ),
                    )
                }
            }
        }

        DockSurface(
            dockIds = dockIds,
            appsById = appsById,
            onLaunchApp = onLaunchApp,
            onLoadIcon = onLoadIcon,
            onRemove = onRemove,
            hazeState = hazeState,
        )
    }
}

@Composable
private fun EmptyHomePage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Your Home is ready",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
            Text(
                text = "Open Apps and add the apps you use most.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DockSurface(
    dockIds: List<String>,
    appsById: Map<String, LaunchableApp>,
    onLaunchApp: (LaunchableApp) -> Unit,
    onLoadIcon: suspend (LaunchableApp) -> Bitmap?,
    onRemove: (String) -> Unit,
    hazeState: HazeState,
) {
    GLADGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        material = GLADGlassMaterial.Tinted,
        shape = RoundedCornerShape(28.dp),
        input = HazeInput.Backdrop(hazeState),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
        Text(
            text = "DOCK",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.62f),
        )
        if (dockIds.isEmpty()) {
            Text(
                text = "Add up to five apps from Home",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.62f),
                textAlign = TextAlign.Center,
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                lazyRowItems(
                    items = dockIds,
                    key = { it },
                    contentType = { "dock-app" },
                ) { appId ->
                    val app = appsById[appId]
                    if (app == null) {
                        MissingDockItem(onRemove = { onRemove(appId) })
                    } else {
                        DockAppItem(
                            app = app,
                            onLoadIcon = onLoadIcon,
                            onClick = { onLaunchApp(app) },
                            onRemove = { onRemove(appId) },
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun DockAppItem(
    app: LaunchableApp,
    onLoadIcon: suspend (LaunchableApp) -> Bitmap?,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    val bitmap by produceState<Bitmap?>(initialValue = null, app.id) {
        value = onLoadIcon(app)
    }
    Column(
        modifier = Modifier
            .width(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                )
            } else {
                Text(
                    text = app.label.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Text(
            text = app.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
        )
        Text(
            text = "Remove",
            modifier = Modifier.clickable(onClick = onRemove),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.62f),
        )
    }
}

@Composable
private fun MissingDockItem(onRemove: () -> Unit) {
    Column(
        modifier = Modifier.width(58.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("?", color = Color.White.copy(alpha = 0.7f))
        }
        Text("Unavailable", color = Color.White, style = MaterialTheme.typography.labelSmall)
        Text(
            "Remove",
            modifier = Modifier.clickable(onClick = onRemove),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.62f),
        )
    }
}

@Composable
private fun MissingAppTile(onRemove: () -> Unit) {
    Column(
        modifier = Modifier
            .width(78.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(horizontal = 3.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("?", color = Color.White.copy(alpha = 0.7f))
        }
        Text(
            text = "Unavailable",
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
        )
        TextButton(onClick = onRemove, contentPadding = PaddingValues(0.dp)) {
            Text("Remove", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun AppDrawer(
    apps: List<LaunchableApp>,
    pinnedIds: Set<String>,
    dockIds: Set<String>,
    onLaunchApp: (LaunchableApp) -> Unit,
    onLoadIcon: suspend (LaunchableApp) -> Bitmap?,
    onPinToHome: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "ALL APPS",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = apps.size.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.58f),
            )
        }

        if (apps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No apps match this search.",
                    color = Color.White.copy(alpha = 0.68f),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 78.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    items = apps,
                    key = LaunchableApp::id,
                    contentType = { "app-drawer-item" },
                ) { app ->
                    val isPinned = app.id in pinnedIds
                    val isDocked = app.id in dockIds
                    AppTile(
                        app = app,
                        onLoadIcon = onLoadIcon,
                        onClick = { onLaunchApp(app) },
                        actionLabel = when {
                            isDocked -> "Move Home"
                            isPinned -> "On Home"
                            else -> "+ Home"
                        },
                        actionEnabled = !isPinned,
                        onAction = { onPinToHome(app.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppTile(
    app: LaunchableApp,
    onLoadIcon: suspend (LaunchableApp) -> Bitmap?,
    onClick: () -> Unit,
    actionLabel: String? = null,
    actionEnabled: Boolean = true,
    onAction: () -> Unit = {},
) {
    val bitmap by produceState<Bitmap?>(initialValue = null, app.id) {
        value = onLoadIcon(app)
    }

    Column(
        modifier = Modifier
            .width(78.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .semantics { contentDescription = app.label }
            .padding(horizontal = 3.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                )
            } else {
                Text(
                    text = app.label.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
            }
        }
        Text(
            text = app.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null) {
            TextButton(
                onClick = onAction,
                enabled = actionEnabled,
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        } else {
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

@Composable
private fun HomeRoleCard(
    isAvailable: Boolean,
    onRequestHomeRole: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.09f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = if (isAvailable) "Make GL-AD your Home app" else "Home role unavailable",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
        Text(
            text = if (isAvailable) {
                "Choose GL-AD in Android's system prompt to use it as your launcher."
            } else {
                "This Android build does not expose the standard Home role."
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.72f),
        )
        if (isAvailable) {
            Button(onClick = onRequestHomeRole) {
                Text("Set as default")
            }
        }
    }
}
