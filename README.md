# GL-AD

**GL-AD** is an Android-first launcher exploring a high-quality Liquid Glass design language while keeping Android system behavior native, permission-aware, and measurable.

## Current status

The repository foundation, first Launcher Core slice, persistent workspace, and initial Liquid Glass surfaces are implemented on `foundation/plan-and-scaffold`.

Verified by GitHub Actions on the latest completed UI commit:
- Android SDK 37 / Gradle setup.
- Unit tests.
- Android lint.
- Debug APK build.

Implemented:
- Android HOME role onboarding through RoleManager.
- Launchable activity discovery across accessible profiles through LauncherApps.
- Package/profile change callbacks and managed-profile lifecycle refresh.
- Search by app label or package name.
- Badged app icons with a bounded bitmap cache.
- App launch through LauncherApps using the correct profile handle.
- Profile-scoped stable app identity using UserManager serial numbers.
- Versioned DataStore workspace snapshots with atomic updates and validation.
- Persistent Home pages and dock, App drawer pin actions, and move/remove actions.
- GL-AD-owned GlassSurface abstraction backed by Haze Glass 2.1.0.
- Merged glass segmented navigation and a tinted glass dock surface.

Still requiring physical-device validation:
- Native backdrop output and fallback quality across Android versions and OEMs.
- Workspace gestures, folders, widgets, and screen-size adaptation.
- Glass frame time, GPU memory, and thermal behavior.
- Media, volume, Quick Settings, and notification integrations.

## Product direction

- A real Android Home app built with Kotlin and Jetpack Compose.
- A coherent Liquid Glass system for dock, panels, controls, media surfaces, and in-app notification banners.
- Real app discovery, workspace persistence, folders, shortcuts, and Android widgets.
- Native media-session integration and honest boundaries around system volume, Quick Settings, and notifications.
- Performance-first rendering with release-build benchmarks on physical devices.

## Platform boundaries

A regular third-party app cannot replace Android System UI wholesale. GL-AD does not claim ownership of the system volume panel, notification shade, or Quick Settings host. It uses supported Android APIs, optional user-granted access where appropriate, and in-app surfaces for features it can genuinely control.

## Documentation

- [Product specification](docs/PRODUCT_SPEC.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Liquid Glass design system](docs/DESIGN_SYSTEM.md)
- [Implementation plan](docs/IMPLEMENTATION_PLAN.md)
- [Architecture decisions](docs/DECISIONS.md)
- [Testing and benchmark protocol](docs/TESTING.md)
- [Claude Code instructions](CLAUDE.md)
- [Agent instructions](AGENTS.md)

## Development baseline

Android 13+ (API 33), compile/target API 37, Kotlin, Jetpack Compose, Material 3, and Haze 2.1.0 as the first Glass renderer. Versions are pinned and must be revalidated against official release notes before upgrades.

## License

No project license has been selected yet. Do not copy or redistribute third-party source code until the project owner chooses a license and all dependency notices are reviewed.
