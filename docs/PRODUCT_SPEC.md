# Product specification

## Product definition
GL-AD is an Android-first HOME launcher with a coherent Liquid Glass design system. It combines a real launcher workspace with carefully scoped media, volume, and notification integrations.

The product must look distinctive without pretending to be Android System UI. Android retains ownership of the system volume panel, notification shade, Quick Settings panel, lock screen, and protected settings.

## Principles
1. Real behavior before visual imitation.
2. Android APIs and user consent before overlays or privileged workarounds.
3. One consistent glass material system, not unrelated blur cards.
4. Performance measured on release builds and physical devices.
5. Optional capabilities remain optional and degrade gracefully.
6. No hidden collection or remote upload of notification contents, app inventory, or usage data.
7. No copied Apple artwork, proprietary assets, or unlicensed launcher source.

## Platform baseline
- Phones first; tablets and foldables are adaptive targets.
- Initial minimum: API 33 / Android 13.
- Compile and target: API 37, subject to final compatibility verification.
- Kotlin, Jetpack Compose, Android Framework.
- Dark-first, wallpaper-aware Liquid Glass inspired by the user-provided visual reference.

## P0 — launcher foundation
- HOME role onboarding and default-launcher verification.
- Launchable app discovery through LauncherApps.
- App icon loading and bounded icon cache.
- Persistent home pages and dock.
- App drawer with search and stable sorting.
- App launch, app info, uninstall handoff, supported shortcuts.
- Drag/reorder and folders.
- Restore layout after process death and package changes.
- Work-profile handling within Android's permitted boundaries.

## P1 — Liquid Glass
- GL-AD-owned GlassSurface abstraction with explicit source, style, shape, and fallback.
- Dock, floating navigation, folder, search, and contextual panels.
- Regular, Clear, Tinted, and HighContrast material variants.
- Edge highlight, controlled refraction, restrained dispersion, and depth.
- Spring-driven, interruptible interactions.
- Reduced-transparency and increased-contrast alternatives.
- Renderer boundary so the library can be replaced without rewriting feature UI.

## P1 — media and volume
- Observe active media sessions only through supported APIs and user-granted access where required.
- Display accurate metadata and playback state.
- Dispatch only commands supported by the active session.
- Provide a Volume Center surface without claiming to replace Android's global volume panel.
- Change system volume only where Android explicitly permits it.
- Respect fixed-volume devices and distinguish media, ring, alarm, call, accessibility, and assistant usages.

## Explicitly out of scope — Control Center
- Do not build a custom Control Center or Quick Settings replacement.
- Keep Android's native Quick Settings panel and its existing behavior.
- Do not add custom Quick Settings tiles in the initial product scope.
- Settings shortcuts may open the relevant Android Settings screen; they must not imitate protected system controls.

## P2 — notifications and Dynamic Island
- Standard app notifications with correctly configured channels and runtime permission.
- In-app Notification Center for user-visible GL-AD events and optional notification-listener data, only after explicit opt-in.
- Dynamic Island-style live activity surface for supported, real states such as media playback, timer, charging, or downloads.
- No notification contents stored by default.
- No global overlay banner in the initial release; any future overlay requires a separate platform, privacy, and permission review.

## Non-goals
- Replacing Android System UI, lock screen, notification shade, Quick Settings, or system volume panel.
- Directly toggling protected settings where Android disallows it.
- Requiring root, Shizuku, ADB grants, or privileged installation.
- Using AccessibilityService as a general-purpose interception layer.
- Shipping third-party Apple assets or copying GPL launcher code into a differently licensed project.
- Advertising, analytics, or remote upload of notification contents.

## UX requirements
- Wallpaper/content remains visually dominant; glass is reserved for functional controls.
- Dock and floating controls use tinted translucent materials with readable labels.
- Large surfaces use stronger diffusion; small controls retain more background detail.
- Avoid stacking multiple full-screen blur layers.
- Touch targets are at least 48dp.
- Support font scaling, TalkBack, keyboard/D-pad, RTL, and light/dark appearance.
- Animations are interruptible and follow gesture velocity where practical.

## Privacy and permissions
Every special permission needs a feature-specific explanation, visible enabled/disabled state, revocation path, and documented retention policy. Notification access is optional. Notification content stays on-device and is not logged, exported, or uploaded by default.

## Release acceptance
- Can be selected as default Home and safely switched away from.
- App discovery, launch, workspace persistence, and core gestures work on a physical device.
- Integrations reflect actual platform state and handle denied permissions.
- Glass has a readable fallback and accessible contrast.
- Release-like performance benchmarks exist for a mid-range device.
- Unit, UI, lint, and build checks pass.
- Dependency and asset licenses are recorded.
