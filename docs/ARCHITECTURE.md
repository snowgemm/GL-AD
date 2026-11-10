# Architecture

## Style
Use a small, feature-oriented architecture with strict platform boundaries. Start with package boundaries inside :app; extract Gradle modules only when build time, ownership, or dependency isolation justifies it.

Layers:
- Presentation: Compose screens, components, UI state.
- Domain: use cases and platform-independent models.
- Data: repositories, persistence, cache policies.
- Platform: Android framework adapters and permission gateways.
- Design system: tokens, glass renderer abstraction, motion, accessibility.

UI must not call LauncherApps, AudioManager, NotificationManager, or ContentResolver directly.

## Package layout
~~~
com.snowgemm.glad/
  app/
  core/model/
  platform/launcher/
  platform/audio/
  platform/media/
  platform/notifications/
  platform/quicksettings/
  platform/permissions/
  data/apps/
  data/workspace/
  data/icons/
  domain/apps/
  domain/workspace/
  design/tokens/
  design/glass/
  design/motion/
  feature/home/
  feature/appdrawer/
  feature/folders/
  feature/search/
  feature/media/
  feature/controls/
  feature/settings/
  quality/diagnostics/
~~~

## Launcher integration
- Declare MAIN/HOME/DEFAULT and use RoleManager to guide the user through the default Home role.
- Use LauncherApps for launchable activities and package/profile callbacks.
- Do not add QUERY_ALL_PACKAGES by default.
- Use stable package/component identifiers; never persist list positions as identity.
- Bound icon caches and invalidate on package/version/density changes.
- Persist workspace layout transactionally.
- Keep app discovery separate from workspace ordering.

## Workspace persistence
Persist stable IDs, page index, grid cell, dock slot, folder membership, and visibility. Validate on load: no duplicate placement, no item in both dock and workspace, folder references exist, grid bounds are valid. Unknown apps must not silently erase the saved layout.

A versioned DataStore/JSON representation is acceptable initially if updates are atomic and schema-versioned. Use Room when relational queries and migrations justify it.

## Glass rendering boundary
Expose GL-AD-owned interfaces:
- GlassSource: identifies content eligible for sampling.
- GlassMaterial: semantic style (regular, clear, tinted, high-contrast).
- GlassSurface: Compose component owning shape, semantics, interaction, and fallback.
- GlassRenderer: implementation backed by Haze or a future renderer.

Feature UI must not depend directly on Haze types. Initial candidate is Haze 2.1.0 with core and glass artifacts. Keep HazeInputSharing disabled until memory and frame-time measurements justify it.

Do not sample content across windows or retain pixels from sources that may contain private content. Prefer explicit source selection and clear retained output when a source disappears.

## Audio and media
Separate MediaController, PlaybackCommandGateway, VolumeGateway, and immutable MediaUiState. Do not conflate media, ring, alarm, call, accessibility, and assistant usages. An ordinary launcher cannot change every global stream.

## Controls
In-app controls are ordinary Compose features backed by repositories/use cases. Quick Settings tiles are independent TileService entry points. Each tile refreshes from actual platform state and tolerates service recreation. If direct mutation is restricted, open the relevant Settings page.

## Notifications
App notifications use NotificationCompat/NotificationManager and versioned channels. In-app banners are only for GL-AD-owned events. NotificationListenerService is isolated behind an optional capability and explicit permission state. Do not persist notification text by default. Do not use SYSTEM_ALERT_WINDOW in the baseline.

## State and concurrency
- ViewModels expose StateFlow<UiState>.
- Repositories expose Flow and suspend mutations.
- Use structured concurrency and explicit dispatchers for blocking work.
- Keep high-frequency gesture/animation state local when it does not need persistence.
- Read fast-changing animation state in layout/draw phases where possible.
- Avoid global mutable state and unbounded caches.

## Failure handling
Permission denied: actionable explanation, not crash or fake enabled state.
Unsupported API: documented fallback.
App removed during drag: cancel or reconcile safely.
Corrupt workspace: restore last valid snapshot and preserve diagnostics.
Glass renderer failure: tinted translucent fallback with visible edge.
Media session disappears: clear stale metadata and disable unsupported actions.
