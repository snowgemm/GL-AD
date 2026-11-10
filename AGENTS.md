# GL-AD agent guidance

Read README.md, CLAUDE.md, and relevant documents under docs/ before changing code.

- Inspect branches, open PRs, CI, and unfinished work first.
- Verify current Android, Compose, and dependency documentation before implementation.
- Preserve native Android permission and system-UI boundaries.
- Keep UI independent from platform APIs and the selected Glass library.
- No fake toggles, fake media state, broad package visibility, or unnecessary overlays.
- Centralize design and motion tokens.
- Re-read changed files and run tests, lint, and builds after each coherent change.
- Do not create overlapping PRs or merge/publish without authorization.
- Explain results to the owner in Vietnamese; keep code and repository documentation in English.
