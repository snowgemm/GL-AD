# GL-AD

**GL-AD** is an Android-first launcher project exploring a high-quality Liquid Glass design language while keeping Android system behavior native, permission-aware, and measurable.

> Status: repository bootstrap. The initial repository was empty; implementation is being prepared on a dedicated foundation branch.

## Product direction

- A real Android Home app built with Kotlin and Jetpack Compose.
- A Liquid Glass design system for dock, panels, controls, media surfaces, and in-app notification banners.
- Real app discovery, workspace persistence, folders, shortcuts, and Android widgets.
- Native media-session integration and honest boundaries around system volume, Quick Settings, and notifications.
- Performance-first rendering with release-build benchmarks on physical devices.

## Important platform boundaries

A regular third-party app cannot replace Android System UI wholesale. GL-AD will not pretend to own the system volume panel, notification shade, or Quick Settings. It will use supported Android APIs, optional user-granted access where appropriate, and in-app surfaces for features it can genuinely control.

## Documentation

- [Product specification](docs/PRODUCT_SPEC.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Liquid Glass design system](docs/DESIGN_SYSTEM.md)
- [Implementation plan](docs/IMPLEMENTATION_PLAN.md)
- [Architecture decisions](docs/DECISIONS.md)
- [Agent instructions](CLAUDE.md)

## Development baseline

The proposed baseline is Android API 33+ (Android 13+) with compile/target API 37, Kotlin, Jetpack Compose, Material 3, and Haze 2.1.0 for the first Glass renderer evaluation. Versions are pinned and must be revalidated against official release notes before upgrades.

## License

No project license has been selected yet. Do not copy or redistribute third-party source code until the project owner chooses a license and all dependency notices are reviewed.
