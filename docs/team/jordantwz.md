# Jordan Tan's Project Portfolio Page

## Project: FinTrack
FinTrack is a lightweight command-line assistant that helps you keep an eye on day-to-day spending and income. It is designed for users who prefer a fast, keyboard-first workflow without navigating complex spreadsheets.

The following is the link to my code contribution to this project: [RepoSense Link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=jordantwz&breakdown=true)

Given below are my contributions to the project:
- **New Feature**: Income management (`add-income` command): Implemented parser -> model -> storage pipeline, added optional descriptions, surfaced friendly error messages, and wired structured console/file logging so every action is audit-ready.
- **New Feature**: Balance reporting (`balance` command): Calculated totals via `FinanceManager` and formatted a reusable summary block in `Ui.printBalance(...)`, forming the base for monthly breakdowns.
- **New Feature**: Income listing (`list-income` command): Exposed immutable `FinanceManager#getIncomesView()`, added `Ui.printListOfIncomes(...)` with defensive logging, refreshed help text, and updated text UI regression data.
- **Enhancements to existing features**:
  - Standardised `Ui` console experience (command constants, welcome hint, `Error:` prefix) and added `hasUnexpectedArguments(...)` so zero-argument commands reject stray input.
  - Synced `text-ui-test/EXPECTED.TXT` with the refined console output to keep regression tests accurate.
  - Hardened date handling with reusable `YEAR_MONTH_PATTERN`/`DATE_PATTERN` regex guards so month filters fail fast before hitting the parser.
  - Added an ASCII-only input checker in `src/main/java/seedu/fintrack/FinTrack.java` so non-ASCII characters are rejected before parsing.
- **Testing**:
  - Wrote `UiTest` to cover welcome/exit banners, add/delete/list renderers, budget warnings, help output, and locale-dependent formatting safeguards.
  - Added integration-style scripts in `FinTrackTest` that exercise the ASCII input guard, monthly balance validation, budget warnings, and list command flows end-to-end.
  - Extended parser/unit coverage (`ParserTest`, `FinanceManagerTest`, `CsvStorageTest`) to validate month filter helpers, immutable reverse-chronological views, and CSV export summaries.
  - Normalised locale during tests (`Locale.US`) and exercised both populated and empty list scenarios for deterministic output.
- **Documentation**:
  - User Guide: Documented the `list-income` command and refreshed the FAQ so users know how data persistence and correction workflows behave.
  - Developer Guide: Documented Architecture overview and the `Ui` module design (scanner swapping, logging, formatter strategy), and supplied the supporting sequence diagrams.
- **Project Management**:
  - Coordinated weekly syncs to align feature scope, kept the milestone board up to date, and chased blockers until they were assigned an owner.
- **Community**:
  - Merged core PRs after shepherding actionable feedback ([#288](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/288), [#280](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/280), [#278](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/278), [#261](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/261), [#248](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/248), [#176](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/176), [#172](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/172), [#171](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/171), [#164](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/164), [#148](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/148), [#137](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/137), [#131](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/131), [#122](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/122), [#117](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/117), [#109](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/109), [#104](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/104), [#54](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/54), [#51](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/51), [#43](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/43), [#12](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/12), [#7](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/7)) and supported teammates by stress-testing their branches ahead of releases.
  - Smoke-tested peers’ releases and filed detailed reports via forum threads [#16](https://github.com/nus-cs2113-AY2526S1/forum/issues/16) and [#22](https://github.com/nus-cs2113-AY2526S1/forum/issues/22), helping them pin down regressions ahead of milestone deadlines.
- **Tools**:
  - Bootstrapped `java.util.logging` via `FinTrack` static init + `logging.properties`, delivering console and rotating file logs for developers without third-party dependencies.
