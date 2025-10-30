# Sanjay Shiva Kumar’s Project Portfolio Page

## Project: FinTrack
FinTrack is a lightweight command-line assistant that helps you keep an eye on day-to-day spending and income.
It is designed for users who prefer a fast, keyboard-first workflow without navigating complex spreadsheets.

The following is the link to my code contribution to this project: [RepoSense Link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=sanjay-shiva-kumar&breakdown=true)

Given below are my contributions to the project:

- **New Feature**: Reverse-chronological list infrastructure (`ReverseChronoList`, `ExpenseList`, `IncomeList`)
    - What it does: Implemented a reusable, reverse-chronological `ArrayList` derivative (`ReverseChronoList`)
      plus concrete `ExpenseList` / `IncomeList` wrappers that sort on insert, validate user input, and expose
      immutable views for reporting.
    - Justification: Centralises ordering logic so every command (`list-*`, `summary-*`, `modify-*`, `delete-*`)
      can trust the newest-first invariant, enabling features like month filters and balance summaries without
      duplicated sorting.

- **New Feature**: Month-aware balance and listing commands
    - What it does: Added optional month filters to `balance`, `list-income`, and `list-expense`,
      surfaced via `Parser.parseOptionalYearMonthAfterCommand`, `FinanceManager#getIncomesViewForMonth(...)`/
      `#getExpensesViewForMonth(...)`, and new `Ui.printBalance(...)`/listing overloads.
    - Justification: Lets users drill into a specific month without exporting data, improving day-to-day planning.
    - Highlights: Updated CLI output to label the selected month.

- **Feature Hardening**: Robust command parsing and description handling
    - What it does: Normalised post-command whitespace (`extractArgumentsAfterCommand`), enforced `des/` as the
      final prefix (`ensureDescriptionLast`), and created helper utilities (`findFirstPrefixIndex`) so descriptions
      can safely include `a/`, `c/`, or `d/` tokens.
    - Justification: Prevents silent data loss (e.g., `des/... c/...` swallowing later arguments) and allows
      users to paste tab-separated commands.

- **Enhancement**: Validation for non-finite amounts
    - What it does: Rejected `NaN`, `Infinity`, and `-Infinity` across `add-*`, `modify-*`, and `budget` flows
      (`Parser`, `FinanceManager`), surfacing a consistent “Amount must be finite.” message.
    - Highlights: Extended JUnit coverage for add/modify/budget commands to ensure the validation never
      regresses.

- **Testing**
    - Authored `FinTrackTest` (integration-style) exercising the CLI loop, random tip output detection, command
      error handling, and locale consistency.
    - Expanded `ParserTest` with whitespace “torture” cases, description invariants, and non-finite amount
      scenarios.
    - Expanded `ExpenseListTest`, `IncomeListTest`, and `ParserTest` to assert validation paths, newest-first ordering, month filters, and CLI formatting edge cases.
    - Continuously updated `text-ui-test/EXPECTED.TXT` to keep scripted regression runs aligned with new output.

- **Documentation**
    - User Guide: 
      - Documented month-filter usage for `balance`, `list-income`, `list-expense` 
      - Continuously refreshed section such as help snippets, sample outputs and FAQ entries impacted by code changes.
    - Developer Guide: 
      - Wrote the `FinanceManager` design section (with sequence diagram for add-expense). 
      - Created the FinTrack module overview class diagram and wrote accompanying explanation.
      - Wrote the glossary.

- **Project Management**
    - Helped maintain issue tracker and assigning milestones to PRs
    - Helped review and merge teammates' PRs
    - PRs reviewed (with non-trivial comments): [#48](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/48), [#68](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/68), [#84](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/84), [#87](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/87), [#97](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/97), [#97](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/97), [#123](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/123), [#126](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/126), [#149](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/149), [#170](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/170), [#166](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/166)
