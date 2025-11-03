# Kenneth Gao's Project Portfolio Page

## Project: FinTrack

FinTrack is a lightweight command-line assistant that helps you keep an eye on day-to-day spending and income. It is designed for users who prefer a fast, keyboard-first workflow without navigating complex spreadsheets.

The following is the link to my code contribution to this project: [RepoSense Link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=duckyfuz&breakdown=true)

Given below are my contributions to the project:

- **New Feature**: Implement the `modify-expense` command

  - What it does: Allows users to modify an existing expense entry at a specified index, updating only the specified fields (amount, category, date, and/or description), leaving others unchanged.
  - Justification: Enables users to correct mistakes or update records without deleting and re-adding expenses, improving usability and data integrity.
  - Highlights: Supports partial updates for flexibility, ensures robust validation, preserves chronological order, and provides clear feedback on successful edits or errors.

- **New Feature**: Implement the `modify-income` command

  - What it does: Lets users update an existing income entry at a specified index, changing only the specified fields (amount, category, date, and/or description), leaving others unchanged.
  - Justification: Offers flexibility to maintain accurate income records and reduces friction when updating past entries.
  - Highlights: Supports partial updates, shares a consistent interface and validation logic with `modify-expense`, ensuring a seamless user experience.

- **New Feature**: Implement the `export` command

  - What it does: Exports all financial data (incomes, expenses, and summary) to a CSV file for backup or analysis in spreadsheet applications.
  - Justification: Provides users with data portability and the ability to analyze or archive their finances outside the app.
  - Highlights: Follows Single Responsibility Principle by delegating file I/O to a dedicated storage layer, and produces a well-formatted, standards-compliant CSV output.

- **New Feature**: Implement command aliases for improved user experience.

  - What it does: Allows users to use short aliases like "mi" for "modify-income", "me" for "modify-expense", "ae" for "add-expense", etc., to speed up command entry while maintaining full command support.
  - Justification: Reduces typing effort and improves efficiency for frequent users without breaking existing workflows.
  - Highlights: Maintains backward compatibility with full command names and provides intuitive shortcuts.

- **Enhancements**:

  - Implement atomic `Modify Income / Expense` instructions for enhanced reliability.
  - Wrote tests for coverage of `FinanceManager` class and `Modify Income / Expense` methods. [#38](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/38) , [#87](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/87)
  - Write tests to ensure atomicity of `Modify Income / Expense` methods. [#87](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/87)

- **Documentation**:

  - User Guide:
    - Wrote instructions on how to use the `modify-expense` command.
    - Wrote instructions on how to use the `modify-income` command.
    - Wrote instructions on how to use the `export` command.
  - Developer Guide
    - Wrote design & implementation details on the `modify-expense` feature (with sequence diagrams).
    - Wrote design & implementation details on the `modify-income` feature (with sequence diagrams).
    - Wrote design & implementation details on the `export` feature (with sequence diagrams).
    - Wrote manual testing guide for the project which includes build instructions and data seeding instructions.

- **Project Management**:

  - Oversaw release management, and ensured documentation deadlines were met.
  - Managed project workflow: triaged issues, reviewed PRs with detailed feedback, and facilitated weekly syncs to drive progress and resolve blockers.
  - Implemented improvements to the modify-expense and modify-income commands to support partial modifications after gathering user feedback, enhancing usability by allowing updates to only specified fields without requiring all parameters.

- **Community**:
  - PRs reviewed (with non-trivial comments): [#147](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/147) , [#142](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/142) , [#70](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/70) , [#60](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/60) , [#53](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/53) , [#47](https://github.com/AY2526S1-CS2113-W12-4/tp/pull/47)
  - Smoke-tested peers’ releases and filed detailed reports on github issues [#22](https://github.com/nus-cs2113-AY2526S1/forum/issues/22#issuecomment-3356133185) and [#21](https://github.com/nus-cs2113-AY2526S1/forum/issues/21#issuecomment-3356114983), helping them gain confidence in their deployments.
