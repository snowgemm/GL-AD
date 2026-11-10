# Testing and performance protocol

## CI gate
Run:
- ./gradlew :app:testDebugUnitTest
- ./gradlew :app:lintDebug
- ./gradlew :app:assembleDebug

A release candidate additionally requires:
- ./gradlew :app:assembleRelease
- Physical-device install and HOME-role selection.
- ADB log review for startup, package callbacks, and rendering failures.

## Current automated coverage
- App search: blank query, case-insensitive app label, package-name search, no-match behavior, whitespace normalization.
- App identity: same component in different Android profiles receives a distinct identity; labels may change without changing identity.
- Android lint and debug APK build are required CI checks.

## Test layers

### Unit tests
- Workspace placement validation.
- Stable app/component identity.
- Workspace schema migration and recovery.
- Permission/capability state mapping.
- Media session state and supported command mapping.
- Glass token and fallback policy invariants.

### Instrumented/UI tests
- HOME entry point launches.
- App list loads and handles package removal.
- Search filters the real app list.
- Workspace reorder and folder operations preserve all items.
- Permission-denied paths remain usable.
- In-app banner semantics and dismissal.
- Glass fallback remains readable with high contrast/reduced transparency.

### Physical-device journeys
1. Cold start and return via Home gesture.
2. Open app drawer and scroll through a large app set.
3. Swipe between workspace pages.
4. Long-press, drag, reorder, and create a folder.
5. Open/close a glass folder and navigation surface.
6. Display active media and dispatch supported playback actions.
7. Exercise volume state with no active session and fixed-volume device.
8. Add/use/remove Quick Settings tile.
9. Post/dismiss GL-AD-owned notification banner.
10. Add/remove a managed profile and confirm app list refresh.
11. Launch the same app from Personal and Work profiles.

## Performance methodology
- Use release-like builds with R8 and Baseline Profiles.
- Measure 60Hz and high-refresh devices separately.
- Record device model, SoC, Android build, refresh rate, thermal state, brightness, and test build SHA.
- Run at least five warm repetitions after one warm-up.
- Report P50/P90/P95 frame duration, jank percentage, cold-start time, CPU, GPU where available, peak memory, and thermal behavior.
- Keep Perfetto traces and Macrobenchmark outputs as artifacts.
- Change one rendering parameter at a time and compare screenshot quality with frame timing.

## Glass A/B matrix
Compare:
- Haze default/balanced versus quality/performance profiles.
- Static wallpaper source versus live moving source.
- One glass surface versus nested/overlapping surfaces.
- Ordinary source sampling versus experimental source sharing.
- Regular, Clear, Tinted, and high-contrast fallback.
- 60Hz versus 90Hz/120Hz.
- API 33 device versus current API 37 device.

Do not infer GPU performance from CPU frame duration alone. Record GPU/memory traces where available.

## Exit criteria
A test result is valid only when the tested commit SHA and device/build details are recorded. Emulator-only success does not replace physical-device acceptance for glass rendering, gestures, widget behavior, or Home integration.
