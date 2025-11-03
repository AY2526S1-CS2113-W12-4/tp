# Sanjay Shiva Kumar’s Project Portfolio Page

## Project: FinTrack
FinTrack is a lightweight command-line assistant that helps you keep an eye on day-to-day spending and income.
It is designed for users who prefer a fast, keyboard-first workflow without navigating complex spreadsheets.

The following is the link to my code contribution to this project: [RepoSense Link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=sanjay-shiva-kumar&breakdown=true)

Given below are my contributions to the project:

- **PlainTextStorage** – Designed and implemented the persistence layer with atomic temp-file swaps, writeability probes, ASCII validation, whitespace checks, and guarded save/load paths so autosave is reliable even when users edit the data file by hand.
- **Reverse-chronological lists** – Implemented `ReverseChronoList` plus `ExpenseList`/`IncomeList` wrappers that sort on insert, enforce invariants, and expose immutable views so every command (`list`, `summary`, `modify`, `delete`) receives newest-first data without duplicate sorting logic.
- **Month-aware commands** – Added optional month filters for `balance`, `list-income`, and `list-expense`, threading YearMonth parsing through `Parser`, `FinanceManager`, and `Ui` so users can zoom into any month with a single flag.
- **Parser hardening** – Normalised argument whitespace, forced descriptions to stay last, and added helper scanners that prevent stray text from being misinterpreted as prefixes, eliminating whole classes of “des/ eats the rest” bugs.
- **Persistence resilience** – Trimmed record types, amounts, and dates while loading `fintrack-data.txt`, synchronised console warnings, and documented the safe-editing workflow so advanced users can repair data manually without corrupting the file.
- **Amount validation** – Guarded against `NaN`/`Infinity` across add/modify/budget pathways and tightened the error messaging to keep validation consistent.

**Testing**
- Continuously performed extensive manual testing to surface bugs/issues.
- Extended `UiTest` to cover banner rendering, persistence warnings, and input sanitisation logic.
- Wrote `FinTrackTest` to exercise the CLI loop end to end, covering success paths, error propagation, and deterministic tips output.
- Expanded `ParserTest`, `ExpenseListTest`, `IncomeListTest`, and other model suites with whitespace torture cases, month filtering, newest-first guarantees, and non-finite validations.
- Maintained `text-ui-test/EXPECTED.TXT` so scripted regression runs track evolving output precisely.

**Documentation**
- User Guide: explained month filters, authored the advanced persistence editing checklist, refreshed FAQs for malformed storage lines and ASCII warnings, documented persistence dos / don'ts, and provided CLI examples for whitespace tolerance.
- Developer Guide: wrote the persistence section (including the sequence diagram), the `FinanceManager` design narrative, FinTrack overview section (including class diagram), and curated the glossary.
- Appendices: assembled the “Known Issues” appendix to capture deployment edge cases and shell limitations.

**Project Management**
- Coordinated issue triage and milestone assignment, ensuring features landed within iteration scope.
- Performed code reviews and merged teammate pull requests, e.g. [#48](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/48), [#68](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/68), [#84](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/84), [#87](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/87), [#97](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/97), [#123](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/123), [#126](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/126), [#149](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/149), [#166](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/166), [#237](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/237), [#241](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/241), [#245](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/245), [#283](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/283).
