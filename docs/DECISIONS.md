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
Status: Proposed for initial scaffold.

- minSdk 33 (Android 13).
- compileSdk 37.
- targetSdk 37.
- Java 17.
- Android Gradle Plugin 9.5.1 and Gradle 9.6.0; Android SDK Platform 37.0 and Build Tools 37.0.0.
- Kotlin Compose compiler plugin 2.4.21.
- Compose BOM 2026.09.00.

These versions are pinned from current stable release information. Revalidate against official compatibility tables during upgrades. Do not use dynamic dependency versions.

## ADR-005 — Permission minimization
Status: Accepted.

No QUERY_ALL_PACKAGES, SYSTEM_ALERT_WINDOW, notification listener, accessibility service, root, or Shizuku requirement in the initial scaffold. Add a special permission only with a concrete feature, user-facing explanation, and test plan.

## ADR-006 — License and source provenance
Status: Open.

The repository has no selected project license. Do not add a license or copy third-party source code until the owner decides.

Haze is Apache-2.0. Kyant0 AndroidLiquidGlass is Apache-2.0. QWEA0 Liquid-Glass-Android and PrismalAGSL are MIT. Liquidy is GPL-3.0 and remains reference-only unless the owner explicitly chooses GPL-3.0 compatibility. Do not copy source from reference repositories.

Fonts, icons, wallpapers, and screenshots need separate provenance records.

## ADR-007 — No hidden global UI replacement
Status: Accepted.

Volume panel, system notification banners, and Quick Settings host cannot be freely restyled by an ordinary app. GL-AD uses in-app glass surfaces, MediaSession, TileService, Notification APIs, and optional notification access only where appropriate.
