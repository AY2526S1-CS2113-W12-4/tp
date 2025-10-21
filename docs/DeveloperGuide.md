# Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

### Ui Module (`Ui.java`)

#### Console Facade Overview
`Ui` (`src/main/java/seedu/fintrack/Ui.java`) is the single entry point for all console interaction in FinTrack. The class is intentionally static: it exposes command keywords, reads raw user input, and renders every message shown to the user without requiring an object to be instantiated. This keeps the rest of the application (parser, command executors, and model layer) free from I/O concerns while guaranteeing that the console state is mutated from a single place.

#### Command Token Registry
All canonical command phrases and parameter prefixes (`HELP_COMMAND`, `ADD_EXPENSE_COMMAND`, `AMOUNT_PREFIX`, and others) are defined as `public static final` constants at the top of the class. Centralising the tokens avoids string drift between the parser, help text, and automated tests. Any new user-visible command must be added here first, followed by updates to `printHelp()` so the usage documentation always reflects reality.

#### Input Loop Integration
`waitForInput()` owns the blocking read from `System.in` via a shared `Scanner`. The method prints a consistent `> ` prompt, trims whitespace, and returns an empty string when the user simply presses enter. If the input stream is closed or the scanner encounters an illegal state, the method logs the failure (`SEVERE`) and returns `EXIT_COMMAND`; this sentinel gives the caller a deterministic way to trigger a graceful shutdown without duplicating exception handling logic. Unexpected runtime exceptions are rethrown after being logged so they can be surfaced during development.

![img_1.png](images/img_1.png)

#### Output Formatters
- **Shared helpers**: `printHorizontalLine(int length)` is the only low-level formatter. It validates its argument, asserts the precondition when assertions are enabled, and writes the divider used by the list renderers.
- **Welcome and exit**: `printWelcome()` and `printExit()` wrap the banner text with INFO-level logs so session start and end are traceable in diagnostic logs.
- **Error surface**: `printError(String message)` prefixes every failure with `"Error: "` for consistent user feedback and mirrors the message to the logger at `WARNING` level. The duplication means log archives can be searched without relying on console captures.

#### Domain Object Renderers
All methods that print `Expense`, `Income`, or `ExpenseCategory` objects follow the same pattern: enforce non-null preconditions with `Objects.requireNonNull`, assert numeric invariants (`Double.isFinite`), and render values with explicit formatting. `printIncomeAdded(...)`, `printExpenseAdded(...)`, `printIncomeModified(...)`, and `printExpenseModified(...)` display the canonical summary after create or update operations. The deletion counterparts (`printIncomeDeleted(...)`, `printExpenseDeleted(...)`) warn if an invalid index slips through so upstream callers can be fixed during testing. Optional descriptions are only printed when non-blank, avoiding empty shells in the console output.

#### Balance and Budget Reporting
`printBalance(...)` centralises the three-line financial summary, keeping the numeric formatting consistent across commands. Budget-related methods highlight different phases: `printBudgetSet(...)` acknowledges successful configuration, `printBudgetExceededWarning(...)` emits an attention-grabbing banner when the spending threshold is crossed, and `printBudgets(...)` lists all configured budgets sorted by category using `Map.entrySet().stream().sorted(...)`. Sorting within the formatter decouples presentation order from the underlying storage implementation and keeps the console output digestible.

#### List Views
`printListOfIncomes(...)` and `printListOfExpenses(...)` render collections supplied by the model. Both methods iterate defensively: each entry is validated inside the loop, and malformed records are skipped with a `WARNING` log instead of aborting the entire render. Income entries are printed oldest first to match insertion order, while expenses are shown newest first to surface recent spending. Each row is wrapped with a 50-character horizontal divider to improve readability, and dates are standardised to `yyyy-MM-dd` via a local `DateTimeFormatter`.

![img.png](images/img.png)

#### Logging and Diagnostics
A dedicated `java.util.logging.Logger` instance records every significant branch at either INFO, FINE, WARNING, or SEVERE. Verbose (`FINE`) messages are emitted after successful renders, making it easy to cross-check console output with the log file when troubleshooting. Assertions complement logging by surfacing programmer errors (e.g., negative indices) during development builds without affecting production behaviour when assertions are disabled.

#### Extensibility Notes
When introducing new user flows:
- Add command keywords and prefixes alongside the existing constants so the parser and help text can reuse them.
- Implement a formatter that mirrors the defensive style (`Objects.requireNonNull`, assertions, and logging) before wiring it into higher-level commands.
- Keep console writing confined to `Ui`; other classes should pass domain objects or DTOs into these helpers instead of printing directly.
- Update or extend unit tests around `Ui` to lock in the new formatting, especially when the output participates in regression tests or scripted demos.

Because every method is static (most with package-private visibility), they can be invoked directly from tests within the same package without faking I/O streams. If a future feature requires richer presentation (e.g., table layout or different locales), the current structure allows the helper methods to be swapped for formatter objects while preserving the public surface area exposed to the rest of the application.


## Product scope
### Target user profile

{Describe the target user profile}

### Value proposition

{Describe the value proposition: what problem does it solve?}

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
