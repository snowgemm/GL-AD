# Architecture decision records

## ADR-001 — Native Android launcher
Status: Accepted.

GL-AD is a regular Android HOME application. It does not replace Android System UI. System volume, Quick Settings, notification shade, lock screen, and protected settings remain under Android control.

## ADR-002 — Kotlin + Jetpack Compose
Status: Accepted.

Use Kotlin and Jetpack Compose for launcher UI and design system. Android Framework APIs remain behind platform adapters.

## ADR-003 — Haze 2.1.0 candidate
Status: Accepted for evaluation; integration remains conditional.

Use Haze core + haze-glass 2.1.0 as the first candidate because Glass is stable in 2.1.0, styles are typed, fallbacks are documented, and performance guidance is public.

Do not enable experimental HazeInputSharing by default. Haze documentation reports about 25–33 MiB additional peak GPU memory per active full-screen shared source on Pixel 8a in its measured scenario. Compare with ordinary Sources on actual GL-AD screens.

Kyant0 Backdrop remains a comparison/reference implementation. Feature UI must not couple to either library API.

## ADR-004 — API and build baseline
Status: Accepted for the current foundation.

- minSdk 33 (Android 13).
- compileSdk 37.
- targetSdk 37.
- Java 17.
- Android Gradle Plugin 9.5.1 and Gradle 9.6.0.
- Kotlin Compose compiler plugin 2.4.21.
- Compose BOM 2026.09.00.

Android 17 (API 37) is released. Revalidate against official compatibility tables during upgrades. Do not use dynamic dependency versions.

## ADR-005 — Permission minimization
Status: Accepted.

No QUERY_ALL_PACKAGES, SYSTEM_ALERT_WINDOW, notification listener, accessibility service, root, or Shizuku requirement in the initial scaffold. Use a narrow MAIN/LAUNCHER package-visibility query. Add a special permission only with a concrete feature, user-facing explanation, and test plan.

## ADR-006 — License and source provenance
Status: Open.

The repository has no selected project license. Do not add a license or copy third-party source code until the owner decides.

Haze is Apache-2.0. Kyant0 AndroidLiquidGlass is Apache-2.0. QWEA0 Liquid-Glass-Android and PrismalAGSL are MIT. Liquidy is GPL-3.0 and remains reference-only unless the owner explicitly chooses GPL-3.0 compatibility. Do not copy source from reference repositories.

Fonts, icons, wallpapers, and screenshots need separate provenance records.

## ADR-007 — No hidden global UI replacement
Status: Accepted.

Volume panel, system notification banners, and Quick Settings host cannot be freely restyled by an ordinary app. GL-AD uses in-app glass surfaces, MediaSession, AudioManager within platform limits, Notification APIs, and optional notification access only where appropriate. A custom Control Center, Quick Settings replacement, and custom Quick Settings tiles are out of scope.

## ADR-008 — Stable profile identity and platform boundary
Status: Accepted.

Do not use hidden UserHandle fields or persist UserHandle objects as app identity. Use UserManager.getSerialNumberForUser() with flattened ComponentName to produce profile-scoped app identity. Keep LauncherActivityInfo and UserHandle inside the platform repository; expose plain app models to the presentation layer.

## ADR-009 — Pinned CI actions
Status: Accepted.

Use verified GitHub Action release commit SHAs, not mutable major-version tags, for checkout, JDK setup, Android SDK setup, and Gradle setup. Keep the release tag in a YAML comment for maintenance.

## ADR-010 — Haze Glass backend and native backdrop fallback
Status: Accepted for initial implementation; physical-device validation pending.

Use Haze 2.1.0 `haze-glass` through `GLADGlassSurface`. The application enables the experimental platform-backdrop feature flag before Compose nodes attach. This makes same-window native backdrop rendering eligible; it does not guarantee that the native path is available or succeeds on every OEM build.

Provide a source-backed fallback where an explicit Haze source exists. Use `HazeSourceRetention.ClearWhenUnavailable` for the dock backdrop so stale app imagery is not retained after source loss. Always draw a translucent or high-contrast fill and edge beneath the effect.

Use `HazePerformanceMode.Default` (Balanced). Keep source sharing disabled (Never) until physical-device measurements establish that the GPU-memory tradeoff is worthwhile.

## ADR-011 — Optional Rust domain core
Status: Planned; not implemented.

Keep Android lifecycle, UI, framework APIs, and platform adapters in Kotlin. Evaluate Rust only for platform-independent logic that demonstrates a measurable performance, safety, or maintainability benefit. Prefer UniFFI for a typed Kotlin boundary; use NDK/cargo-ndk for Android targets. No Rust module is part of the current scaffold, and no native build dependency should be introduced until a small prototype passes ABI, lifecycle, test, and performance checks.
