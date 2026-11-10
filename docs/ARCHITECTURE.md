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
- Keep app discovery separate from workspace ordering.

## Workspace persistence
Workspace state is a small, typed snapshot persisted through DataStore using a versioned JSON serializer. Each update is transactional through the DataStore update API; do not write workspace JSON directly from UI code.

The initial schema contains ordered pages of stable profile-scoped app IDs and a separate dock list. It enforces:
- A schema version.
- Unique page IDs.
- Unique app placement across pages and dock.
- A maximum of 24 app entries per page, 12 pages, and 5 dock items.
- A non-empty page model even when the workspace is empty.
- Stable app IDs based on flattened ComponentName plus UserManager profile serial.

DataStore is appropriate for this small ordered state. Re-evaluate Room if folders, widgets, cross-entity references, partial updates, or larger query requirements make relational integrity materially useful. Schema upgrades must be explicit; do not silently discard unsupported future versions.

## Glass rendering boundary
Expose GL-AD-owned interfaces:
- GlassSource: identifies content eligible for sampling.
- GlassMaterial: semantic style (regular, clear, tinted, high-contrast).
- GlassSurface: Compose component owning shape, semantics, interaction, and fallback.
- GlassRenderer: implementation backed by Haze or a future renderer.

Feature UI must not depend directly on Haze types. The current adapter is `GLADGlassSurface`, which owns material roles, shape, fallback fill, edge treatment, and accessibility-safe opaque mode. Haze 2.1.0 is the renderer behind this boundary.

The application enables Haze's experimental platform window-backdrop flag before attaching Compose content. Native backdrop rendering is eligibility-based, not guaranteed on every device or OEM build. `HazeInput.Backdrop` falls back to a captured source when available; the dock uses `ClearWhenUnavailable` so stale source pixels are not retained. If neither path is available, the GL-AD translucent/opaque fill remains visible.

Use Haze's default Balanced performance profile initially. Keep `HazeInputSharing.Never` (the default) until measured memory and frame-time gains justify enabling it.

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
App removed during drag: cancel or reconcile the operation safely.
Corrupt workspace: preserve the original file and surface a recoverable error; never silently overwrite user layout.
Glass renderer failure: tinted translucent fallback with visible edge.
Media session disappears: clear stale metadata and disable unsupported actions.
