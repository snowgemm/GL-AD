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
- [ ] Add product, architecture, design, and implementation specifications.
- [ ] Add minimal Android app scaffold and CI.
- [ ] Verify first CI build and lint.

Acceptance: clean Android build, HOME activity declaration, no unnecessary permissions, docs agree with code.

## Phase 1 — launcher core
1. HOME role onboarding and default-role state.
2. LauncherApps discovery and callbacks.
3. Icon loading/cache with memory bounds.
4. Workspace model and versioned persistence.
5. App drawer and search.
6. Dock and page grid.
7. Drag/reorder and folder operations.
8. App info/uninstall handoff and supported shortcuts.
9. Widget host and profile behavior.

Acceptance: app list matches launchable activities; package changes reconcile correctly; layout survives process death; interrupted drags do not lose items; no broad package permission without a documented need.

## Phase 2 — Liquid Glass
1. Implement GL-AD-owned GlassSurface API.
2. Add Haze 2.1.0 behind renderer boundary.
3. Build reference scenes: dock, segmented nav, floating media card, folder, slider, banner.
4. Add Regular, Clear, Tinted, HighContrast variants.
5. Add reduced-transparency and reduced-motion support.
6. Benchmark static/live source, one/multiple surfaces, and source-sharing options.

Acceptance: stable visual snapshots; readable fallback; no full-screen blur every frame; documented release traces.

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
