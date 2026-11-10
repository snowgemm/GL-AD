# Implementation plan

## Operating rules
- Work on one coherent task branch at a time.
- Inspect branches, open PRs, CI, and unfinished work before starting another task.
- Do not create a PR by default.
- Re-read changed files and run relevant checks after each coherent change.
- Keep production code and docs in English; explain decisions to the owner in Vietnamese.
- Never fake platform state or claim success before the platform confirms it.

## Phase 0 — repository foundation
- [x] Confirm repository metadata and default branch.
- [x] Confirm repository was empty before bootstrap.
- [x] Add project overview and create a dedicated foundation branch.
- [x] Add product, architecture, design, testing, and implementation specifications.
- [x] Add minimal Android HOME app scaffold and CI workflow.
- [x] Verify Android SDK setup, Gradle, unit tests, lint, and debug build in GitHub Actions.
- [x] Add Gradle Wrapper from the official Gradle 9.6.0 distribution; verify it in CI.

Acceptance: clean Android build, HOME activity declaration, no unnecessary permissions, docs agree with code.

## Phase 1 — launcher core

### Implemented
- [x] HOME role availability/held-state and system role request flow.
- [x] Launchable activity discovery through LauncherApps.
- [x] Profile-aware app identity using UserManager serial numbers.
- [x] Package changes, package availability, suspension, and managed-profile lifecycle refresh.
- [x] Bounded icon bitmap cache with profile-badged icons.
- [x] App search by label and package name.
- [x] Launch app activities using the correct UserHandle.
- [x] Unit tests for app search and profile-scoped identity.
- [x] Versioned workspace snapshot and transactional DataStore persistence.
- [x] Workspace rules for unique placement, page/dock limits, and safe reordering.
- [x] Unit tests for workspace operations and validation.
- [x] Bind workspace snapshots to HomeViewModel and render persistent Home pages/dock.
- [x] App drawer actions to pin an app to Home and move docked apps back Home.
- [x] Remove unavailable or docked apps from the workspace without silently losing layout state.

### Remaining
1. Gesture-driven drag/reorder across pages and dock.
2. Folder model, persistence, and interaction.
3. App info/uninstall handoff and supported shortcuts.
4. AppWidgetHost integration.
5. Managed-profile UX, private-space behavior, and physical-device compatibility.

Acceptance: app list matches launchable activities; package/profile changes reconcile correctly; layout survives process death; interrupted drags do not lose items; no broad package permission without a documented need.

## Phase 2 — Liquid Glass

### Implemented
- [x] GL-AD-owned GlassSurface abstraction with material and opaque fallback modes.
- [x] Haze 2.1.0 Glass behind the GL-AD design boundary.
- [x] Merged segmented navigation material via hazeGlassContainer.
- [x] Tinted glass dock with source-backed fallback and clear-on-unavailable retention.

### Remaining
1. Validate Android native window-backdrop rendering on API 33+ physical devices and OEM builds.
2. Compare native backdrop with source-backed fallback and high-contrast mode.
3. Add Clear/Regular/Tinted/HighContrast design tokens and user-facing reduced-transparency setting.
4. Apply glass to contextual panels and media card.
5. Add reduced-motion behavior and screenshot tests.
6. Benchmark static/live source, one/multiple surfaces, and source-sharing options.

Acceptance: stable visual snapshots; readable fallback; no full-screen blur every frame; documented release traces. Do not enable HazeInputSharing until real-device memory/frame-time measurements justify it.

## Phase 3 — media and volume
1. Discover available media sessions through supported APIs.
2. Map metadata and playback state to immutable UI state.
3. Expose only commands supported by the session.
4. Add GL-AD media card.
5. Add volume information and supported in-app controls.
6. Test fixed-volume devices, denied access, no active session, and session removal.

Acceptance: no stale metadata; unsupported controls disabled/omitted; global volume limitations respected; no unnecessary background service.

## Phase 4 — controls
1. Build in-app controls for features GL-AD owns.
2. Add Quick Settings tile prototypes.
3. Synchronize tile state with actual system state.
4. Route restricted actions to system Settings.
5. Add permission and availability education.

Acceptance: tile state remains correct after service recreation; no misleading toggles; permissions are optional and revocable.

## Phase 5 — notifications
1. Create app notification channels.
2. Add runtime notification permission onboarding.
3. Build in-app banners for GL-AD events.
4. Add optional notification listener only if a concrete use case is approved.
5. Add privacy controls and redaction.

Acceptance: no notification content stored/uploaded by default; notification access never requested without explanation; Android owns system presentation.

## Phase 6 — hardening and release
- Unit and UI tests.
- Macrobenchmark journeys: cold start, app drawer scroll, workspace swipe, folder morph, glass dock, media card, banner.
- Perfetto and frame timing captures on physical devices.
- Memory/thermal soak.
- Accessibility, RTL, font scale, tablet/foldable, and edge-to-edge checks.
- Dependency/license inventory and SBOM.
- Release signing and update compatibility.
- Privacy and permission disclosures.

Acceptance: critical checks pass, known limitations documented, release artifacts reproducible.
