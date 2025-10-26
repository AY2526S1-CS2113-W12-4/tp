# FinTrack User Guide

## Introduction

FinTrack is a lightweight command-line assistant that helps you keep an eye on day-to-day spending and income. It is designed for users who prefer a fast, keyboard-first workflow without navigating complex spreadsheets. This guide explains how to install FinTrack, enter your transactions, and understand the feedback shown in the terminal.

## Quick Start

1. **Install Java 17.** FinTrack requires Java 17. Confirm your version with `java -version`.
2. **Download or clone FinTrack.** Place the project folder anywhere on your computer.
3. **Open a terminal at the project root.** On Windows you can use Command Prompt or PowerShell; on macOS/Linux use your preferred shell.
4. **Run FinTrack from source.**
   - Windows: `.\gradlew.bat run`
   - macOS/Linux: `./gradlew run`
   FinTrack will compile (on first run) and display a welcome banner followed by a `>` prompt.
5. *(Optional)* Build a runnable JAR with `./gradlew shadowJar` (macOS/Linux) or `.\gradlew.bat shadowJar` (Windows). The application JAR is created under `build/libs/`.

Tip: Type `help` after launch to see every available command.

## Getting to Know FinTrack

- FinTrack is fully keyboard-driven. Each command is entered on a single line and confirmed with Enter.
- Commands are **case-sensitive**. Use lowercase as shown in this guide (e.g., `add-expense`, not `Add-Expense`).
- Parameters use prefixes:
  - `a/` for amount (non-negative number, decimals allowed).
  - `c/` for category (single word or phrase without leading/trailing spaces).
  - `d/` for date in `YYYY-MM-DD` format.
  - `des/` for an optional description. If omitted, the entry has no description.
- Dates must be valid calendar dates (for example, `2025-02-29` is invalid).
- FinTrack keeps data only while it is running. Closing the application clears all records.

## Features

### Viewing the built-in help: `help`

Shows a command overview in the terminal.

- **Format:** `help`
- **Example usage:** `help`
- **Sample output:**
  ```
  === FinTrack Command Summary ===
  -------------------------------------------------------------------------------
  1. Add an expense:
     add-expense a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]
  ...
  ```

### Adding an expense: `add-expense`

Creates a new expense. Expenses are automatically sorted so the newest date appears first when listed.

- **Format:** `add-expense a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]`
- **Example usage:** `add-expense a/12.50 c/Food d/2025-10-08 des/Lunch with friends`
- **Sample output:**
  ```
  Expense added:
    Amount: 12.50
    Category: Food
    Date: 2025-10-08
    Description: Lunch with friends
  ```

Validation notes:
- Amount must be a non-negative number (e.g., `5`, `14.20`).
- Category and date are mandatory.
- Valid expense categories (not case-sensitive): FOOD, STUDY, TRANSPORT, BILLS, ENTERTAINMENT, RENT, GROCERIES, OTHERS.
- Description is optional; omit it entirely if not needed.

### Adding an income: `add-income`

Records income that contributes to your balance.

- **Format:** `add-income a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]`
- **Example usage:** `add-income a/3200 c/Salary d/2025-10-01 des/October salary`
- **Sample output:**
  ```
  Income added:
    Amount: 3200.00
    Category: Salary
    Date: 2025-10-01
    Description: October salary
  ```

The same validation rules as `add-expense` apply to amount, date, category and description.
Valid income categories (not case-sensitive): SALARY, SCHOLARSHIP, INVESTMENT, GIFT.

### Listing expenses: `list-expense`

Shows every expense in reverse chronological order (newest first) with numbered entries. Use the index numbers when deleting expenses.

- **Format:** `list-expense`
- **Example usage:** `list-expense`
- **Sample output:**
  ```
  Expenses (Newest first):
  --------------------------------------------------
  #1
  Date: 2025-10-11
  Amount: $42.00
  Category: Groceries
  Description: Weekly shop
  --------------------------------------------------
  #2
  Date: 2025-10-08
  Amount: $12.50
  Category: Food
  Description: Lunch with friends
  --------------------------------------------------
  ```

If there are no expenses, FinTrack prints `No expenses recorded.`

### Showing your balance: `balance`

Summarises total income, total expenses, and the resulting balance (`income - expense`).

- **Format:** `balance`
- **Example usage:** `balance`
- **Sample output:**
  ```
  Overall Balance: 3158.00
    Total Income:  3200.00
    Total Expense: 42.00
  ```

### Deleting an expense: `delete-expense`

Removes an expense by its 1-based index as seen in the most recent `list-expense` output.

- **Format:** `delete-expense <index>`
- **Example usage:** `delete-expense 2`
- **Sample output:**
  ```
  Expense deleted (index 2):
    Amount: 12.50
    Category: Food
    Date: 2025-10-08
    Description: Lunch with friends
  ```

FinTrack rejects zero or negative indexes and any index larger than the number of expenses.

### Deleting an income: `delete-income`

Removes an income entry. Incomes are numbered in the order they were added (the first income added is index 1).

- **Format:** `delete-income <index>`
- **Example usage:** `delete-income 1`
- **Sample output:**
  ```
  Income deleted (index 1):
    Amount: 3200.00
    Category: Salary
    Date: 2025-10-01
    Description: October salary
  ```

### Leaving FinTrack: `bye`

Closes the application safely.

- **Format:** `bye`
- **Example usage:** `bye`
- **Sample output:**
  ```
  Bye. Hope to see you again soon!
  ```

You can also close the terminal window, but `bye` ensures the farewell message is shown.

## Error Handling

- Invalid commands or parameters print a single line beginning with `Error:`, for example `Error: Amount must be a valid number.`
- The original data remains unchanged when an error occurs.
- Use `help` whenever you are unsure of the required format.

## FAQ

**Q: Does FinTrack save my data between sessions?**  
A: Not yet. All data resides in memory. Export important figures before exiting.

**Q: How do I update an entry?**  
A: Delete the incorrect entry (`delete-expense` or `delete-income`) and add a new one with the corrected values.

**Q: Why do I see "Amount must be a valid number"?**  
A: FinTrack only accepts standard numbers without currency symbols (e.g., use `a/15.90`, not `a/$15.90`).

**Q: Can I enter dates in other formats such as DD-MM-YYYY?**  
A: No. FinTrack currently requires ISO format `YYYY-MM-DD`.

## Command Summary

| Command                    | Format                                                                   | Example                                                      |
|:---------------------------|--------------------------------------------------------------------------|--------------------------------------------------------------|
| `help`                     | `help`                                                                   | `help`                                                       |
| `add-expense`              | `add-expense a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]` | `add-expense a/12.50 c/Food d/2025-10-08 des/Lunch`          |
| `add-income`               | `add-income a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]`  | `add-income a/3200 c/Salary d/2025-10-01 des/October salary` |
| `list-expense`             | `list-expense`                                                           | `list-expense`                                               |
| `list-expense d/<YYYY-MM>` | `list-expense d/<YYYY-MM>`                                               | `list-expense d/2025-11`                                     |
| `list-income`              | `list-income`                                                            | `list-income`                                                |
| `list-income d/<YYYY-MM>`  | `list-income d/<YYYY-MM>`                                                | `list-income d/2025-12`                                      |
| `balance`                  | `balance`                                                                | `balance`                                                    |
| `balance d/<YYYY-MM>`      | `balance d/<YYYY-MM>`                                                    | `balance d/2025-01`                                          |
| `delete-expense`           | `delete-expense <index>`                                                 | `delete-expense 2`                                           |
| `delete-income`            | `delete-income <index>`                                                  | `delete-income 1`                                            |
| `bye`                      | `bye`                                                                    | `bye`                                                        |

Stay tuned to the project repository for upcoming enhancements such as persistent storage and income listings.
