# GL-AD — Claude Code instructions

## Mission
Build GL-AD as a production-quality Android HOME launcher with a coherent Liquid Glass design system. Native Android behavior, privacy, performance, and correctness take priority over visual imitation.

## Read before changing code
1. Read README.md and relevant files in docs/.
2. Inspect current branch, open PRs, CI status, and unfinished work.
3. Read current source and surrounding call sites before editing.
4. Verify current Android, Compose, and dependency documentation.
5. Check license/provenance before copying source, icons, fonts, shaders, or images.

## Engineering rules
- Kotlin + Jetpack Compose; Android APIs behind platform adapters.
- Keep feature UI independent from Haze types. Use the GL-AD Glass abstraction.
- No hidden APIs, root, Shizuku, or AccessibilityService workarounds in the baseline.
- Do not add QUERY_ALL_PACKAGES or SYSTEM_ALERT_WINDOW without a reviewed requirement.
- Never fake system state or show success before platform confirmation.
- Use stable IDs for apps/workspace entries; never list indexes as identity.
- Keep caches bounded and release resources with lifecycle.
- Keep high-frequency animation state out of broad recomposition scopes.
- Use lambda-based layout/draw modifiers where appropriate.
- Centralize design tokens and motion constants.
- Comments explain non-obvious decisions; do not narrate obvious code.
- Avoid unnecessary abstractions and Gradle module proliferation.

## Liquid Glass
- Glass is a functional layer, not a background for every content card.
- Prefer Haze Regular for text-heavy surfaces and Clear for wallpaper/photo controls.
- Start with Haze default/balanced performance.
- Do not enable HazeInputSharing until measured memory/frame-time gains justify it.
- Provide reduced-transparency and high-contrast fallbacks.
- Never depend on refraction/dispersion to communicate state.
- Avoid per-frame full-screen backdrop rendering.

## Workflow
1. State the concrete task and acceptance criteria.
2. Implement one coherent slice.
3. Re-read every changed file and surrounding context.
4. Run unit tests, lint, build, and relevant UI/device checks.
5. Inspect CI results and fix regressions.
6. Summarize changed files, tests, limitations, and next step.
7. Do not create another PR when an existing branch/PR covers the same work.
8. Do not merge or publish without explicit authorization.

## Communication
Explain results to the owner in Vietnamese. Keep code, identifiers, technical docs, and commit messages in English. Be precise about what was built, tested, and remains unverified.
