# FinTrack User Guide

## Introduction

FinTrack is a lightweight command-line assistant that helps you keep an eye on day-to-day spending and income. It is designed for users who prefer a fast, keyboard-first workflow without navigating complex spreadsheets. This guide explains how to install FinTrack, enter your transactions, and understand the feedback shown in the terminal.

**Important:** FinTrack does not save data between sessions - closing the app immediately clears all records.

## Quick Start

1. **Install Java 17.** FinTrack requires Java 17. Confirm your version with `java -version`.
2. **Download FinTrack.**
   - **Option 1: Clone the project.** Place the project folder anywhere on your computer.
   - **Option 2: Download the JAR file.** Download `fintrack.jar` from the releases page and place it anywhere on your computer.
3. **Open a terminal at the project root (for Option 1) or where the JAR is located (for Option 2).** On Windows you can use Command Prompt or PowerShell; on macOS/Linux use your preferred shell.
4. **Run FinTrack.**
   - **From source (Option 1):**
     - Windows: `.\gradlew.bat run`
     - macOS/Linux: `./gradlew run`
       FinTrack will compile (on first run) and display a welcome banner followed by a `>` prompt.
   - **From JAR (Option 2):** `java -jar fintrack.jar`
5. _(Optional, for Option 1)_ Build a runnable JAR with `./gradlew shadowJar` (macOS/Linux) or `.\gradlew.bat shadowJar` (Windows). The application JAR is created under `build/libs/`.

**Warning:** FinTrack keeps data in memory only. Leave the application running or export your records if you need to retain them after you exit.

Tip: Type `help` after launch to see every available command.

## Getting to Know FinTrack

- FinTrack is fully keyboard-driven. Each command is entered on a single line and confirmed with Enter.
- Commands are **case-sensitive**. Use lowercase as shown in this guide (e.g., `add-expense`, not `Add-Expense`).
- Parameters use prefixes:
  - `a/` for amount (decimals allowed; for expenses and incomes the amount must be positive, for budgets the amount must be non-negative).
  - `c/` for category (must be from the valid list of categories).
  - `d/` for date in `YYYY-MM-DD` format.
  - `des/` for an optional description. If omitted, the entry has no description.
- Square brackets like `[d/<YYYY-MM>]` in the command formats mark optional parameters; leave them out entirely if you do not need that option.
- Compulsory parameters can be input in **any order**. For example, if `add-expense` requires the `a/<amount>`, `c/<category>` and `d/<YYYY-MM-DD>` parameters, it can be input in any order (e.g. `c/<category>`, `a/<amount>`, `d/<YYYY-MM-DD>`).
  - However, if you include the optional description (`des/`), place it after all other parameters—everything after `des/` is treated as part of the description.
- Dates must be valid calendar dates (for example, `2025-02-29` is invalid). Dates set in the future (for example, `2026-10-12`) are also accepted.
- FinTrack keeps data only while it is running. Closing the application clears all records.

## Features

### Viewing the built-in help: `help`

Shows a command overview in the terminal.

- **Format:** `help`
- **Example usage:** `help`
- **Sample output:**

  ```
  === FinTrack Command Summary ===
  --------------------------------------------------------------------------------
  1. Add an expense:
     add-expense a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]
     Example: add-expense a/12.50 c/Food d/2025-10-08 des/Lunch
     Available categories: FOOD, STUDY, TRANSPORT, BILLS, ENTERTAINMENT, RENT, GROCERIES, OTHERS

  2. Add an income:
     add-income a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]
     Example: add-income a/2000 c/Salary d/2025-10-01 des/Monthly pay
     Available categories: SALARY, SCHOLARSHIP, INVESTMENT, GIFT, OTHERS

  3. View all expenses (from latest to earliest date):
     Usage: list-expense [d/<YYYY-MM>]
     Example: list-expense
     To view by month: list-expense d/<YYYY-MM>
     Example: list-expense d/2025-10

  4. View all incomes (from latest to earliest date):
     Usage: list-income [d/<YYYY-MM>]
     Example: list-income
     To view by month: list-income d/<YYYY-MM>
     Example: list-income d/2025-10

  5. Delete an expense:
     delete-expense <index>
     Deletes the expense shown at that index in 'list-expense'.
     Example: delete-expense 1

  6. Delete an income:
     delete-income <index>
     Deletes the income shown at that index in 'list-income'.
     Example: delete-income 1

  7. Modify an expense:
     modify-expense <index> a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]
     Modifies the expense shown at that index in 'list-expense'.
     Example: modify-expense 1 a/1300 c/Rent d/2024-01-01 des/Monthly rent increased

  8. Modify an income:
     modify-income <index> a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]
     Modifies the income shown at that index in 'list-income'.
     Example: modify-income 3 a/250 c/Salary d/2024-01-15 des/Extra performance bonus

  9. View balance summary:
     balance
     Shows total income, total expenses, and current balance.
     To view by month: balance d/<YYYY-MM>
     Example: balance d/2025-10

  10. Set budget for expense categories:
      budget
      Example: budget c/FOOD a/1000
      Available categories: FOOD, STUDY, TRANSPORT, BILLS, ENTERTAINMENT, RENT, GROCERIES, OTHERS

  11. List budgets for expense categories:
      list-budget
      Example: list-budget

  12. Show a summary of your total expenses:
      summary-expense
      Example: summary-expense

  13. Show a summary of your total income:
      summary-income
      Example: summary-income

  14. Provides a useful tip:
      tips
      Example: tips

  15. Show this help menu:
      help
      Example: help

  16. Exit the program:
      bye
      Example: bye

  17. Export data to CSV file:
      export <filepath>
      Example: export financial_data.csv
  --------------------------------------------------------------------------------
  ```

### Adding an expense: `add-expense`

Creates a new expense. Expenses are automatically sorted so the newest date appears first when listed.

- **Format:** `add-expense a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]`
- **Example usage:** `add-expense a/12.50 c/Food d/2025-10-08 des/Lunch with friends`
- **Sample output:**
  ```
  Expense added:
    Amount: 12.50
    Category: FOOD
    Date: 2025-10-08
    Description: Lunch with friends
  ```

Validation notes:

- Amount must be a positive number (e.g., `5`, `14.20`).
- Category and date are mandatory.
- Description is optional; omit it entirely if not needed.
  - Note that entering an empty description field (e.g. `add-expense a/10 c/food d/2025-10-13 des/`) 
    will be treated as if there is no description field at all.
- Categories (not case-sensitive) must be any one of the following (if not, an error message is presented to the user):
  - FOOD
  - STUDY
  - TRANSPORT
  - BILLS
  - ENTERTAINMENT
  - RENT
  - GROCERIES
  - OTHERS

### Adding an income: `add-income`

Records income that contributes to your balance.

- **Format:** `add-income a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]`
- **Example usage:** `add-income a/3200 c/Salary d/2025-10-01 des/October salary`
- **Sample output:**

  ```
  Income added:
    Amount: 3200.00
    Category: SALARY
    Date: 2025-10-01
    Description: October salary
  ```

The same validation rules as `add-expense` apply to amount, date, and description.

- Categories must be any of the following (if not, an error message is presented to the user):
  - SALARY
  - SCHOLARSHIP
  - INVESTMENT
  - GIFT
  - OTHERS

### Listing expenses: `list-expense`

Shows every expense in reverse chronological order (newest first) with numbered entries. Use the index numbers when deleting expenses.

- **Format:** `list-expense [d/<YYYY-MM>]`
- **Example usage:** `list-expense`
- **Sample output:**

  ```
  Expenses (Newest first):
  --------------------------------------------------
  #1
  Date: 2025-10-11
  Amount: $42.00
  Category: GROCERIES
  Description: Weekly shop
  --------------------------------------------------
  #2
  Date: 2025-10-08
  Amount: $12.50
  Category: FOOD
  Description: Lunch with friends
  --------------------------------------------------
  ```

If there are no expenses, FinTrack prints `No expenses recorded.`

#### Filtering by month (for list-expense)

You can add the optional `d/<YYYY-MM>` field to show expenses only from that month.
For example, `list-expense d/2025-10` lists only expenses recorded in **October 2025**.

- **Example usage:** `list-expense d/2025-10`
- **Sample output:**

  ```
  Expenses for the month 2025-10 (Newest first):
  --------------------------------------------------
  #1
  Date: 2025-10-08
  Amount: $12.50
  Category: FOOD
  Description: Lunch
  --------------------------------------------------
  ```

### Listing incomes: `list-income`

Shows every income in reverse chronological order (newest first) with numbered entries. Use the index numbers when deleting or modifying incomes. Filter by month with the optional `d/<YYYY-MM>` parameter.

- **Format:** `list-income [d/<YYYY-MM>]`
- **Example usage:** `list-income`
- **Sample output:**

  ```
  Incomes (Newest first):
  --------------------------------------------------
  #1
  Date: 2025-10-15
  Amount: $500.00
  Category: SALARY
  Description: Design project
  --------------------------------------------------
  #2
  Date: 2025-10-01
  Amount: $3200.00
  Category: SALARY
  Description: October salary
  --------------------------------------------------
  ```

If there are no incomes, FinTrack prints `No incomes recorded.`

#### Filtering by month (for list-income)

You can add the optional `d/<YYYY-MM>` field to show incomes only from that month.
For example, `list-income d/2025-12` lists only incomes recorded in **December 2025**.

- **Example usage:** `list-income d/2025-12`
- **Sample output:**

  ```
  Incomes for the month 2025-12 (Newest first):
  --------------------------------------------------
  #1
  Date: 2025-12-01
  Amount: $3200.00
  Category: SALARY
  Description: December pay
  --------------------------------------------------

  ```

### Showing your balance: `balance`

Summarises total income, total expenses, and the resulting balance (`income - expense`).

- **Format:** `balance [d/<YYYY-MM>]`
- **Example usage:** `balance`
- **Sample output:**

  ```
  Overall Balance: 3158.00
    Total Income:  3200.00
    Total Expense: 42.00
  ```

#### Filtering by month (for balance)

You can add the optional `d/<YYYY-MM>` field to show balance and totals only for that month.
For example, `balance d/2025-01` displays the total income, expenses, and balance for **January 2025**.

- **Example usage:** `balance d/2025-01`
- **Sample output:**

  ```
  Overall Balance for the month 2025-01: 3158.00
  Total Income: 3200.00
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
    Category: FOOD
    Date: 2025-10-08
    Description: Lunch with friends
  ```

FinTrack rejects zero or negative indexes and any index larger than the number of expenses.

### Deleting an income: `delete-income`

Removes an income by its 1-based index as seen in the most recent `list-income` output.

- **Format:** `delete-income <index>`
- **Example usage:** `delete-income 1`
- **Sample output:**

  ```
  Income deleted (index 1):
    Amount: 3200.00
    Category: SALARY
    Date: 2025-10-01
    Description: October salary
  ```

  FinTrack rejects zero or negative indexes and any index larger than the number of incomes.

### Modifying an expense: `modify-expense`

Updates an existing expense entry at a specified index. The new entry replaces the old one and is re-sorted by date if necessary.

- **Format:** `modify-expense <index> a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]`
- **Example usage:** `modify-expense 1 a/1300 c/Rent d/2024-01-01 des/Monthly rent increased`
- **Sample output:**
  ```
  Expense at index 1 modified to:
    Amount: 1300.00
    Category: RENT
    Date: 2024-01-01
    Description: Monthly rent increased
  ```

Validation notes:

- Index must be a valid, existing expense index (as shown in `list-expense`).
- All other validation rules are the same as for `add-expense`.
- If the modification causes the category budget to be exceeded, a warning is shown.

### Modifying an income: `modify-income`

Updates an existing income entry at a specified index. The new entry replaces the old one and is re-sorted by date if necessary.

- **Format:** `modify-income <index> a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]`
- **Example usage:** `modify-income 3 a/250 c/Salary d/2024-01-15 des/Extra performance bonus`
- **Sample output:**
  ```
  Income at index 3 modified to:
    Amount: 250.00
    Category: SALARY
    Date: 2024-01-15
    Description: Extra performance bonus
  ```

Validation notes:

- Index must be a valid, existing income index (as shown in `list-income`).
- All other validation rules are the same as for `add-income`.

### Exporting your data: `export`

Exports all incomes and expenses to a CSV file for use in spreadsheet applications or backups.

- **Format:** `export <filepath>`
- **Example usage:** `export financial_data.csv`
- **Sample output:**
  ```
  Successfully exported data to: /path/to/financial_data.csv
  ```

Notes:

- The CSV file will contain all incomes and expenses in a single table, with a `Type` column to distinguish between them.
- The file can be opened in Excel, Google Sheets, or any compatible application.
- If the export fails (e.g., due to permissions), an error message is shown.

### Setting budgets: `budget`

Allows the user to set budgets for expense categories available.

- **Format:** `budget c/<category> a/<amount>`
- **Example usage:** `budget c/food a/500`
- **Sample output:**

  ```
  Budget set for FOOD: $500.00
  ```

Validation notes:

- Amount must be a non-negative number (e.g., `5`, `14.20`).
- Categories (not case-sensitive) must be any one of the following (if not, an error message is presented to the user):
  - FOOD
  - STUDY
  - TRANSPORT
  - BILLS
  - ENTERTAINMENT
  - RENT
  - GROCERIES
  - OTHERS

### Viewing list of budgets: `list-budget`

Allows the user to view the budgets set for each expense category (if applicable).

- **Format:** `list-budget`
- **Example usage:** `list-budget`
- **Sample output:**

  ```
  Current Budgets:
  --------------------------------------------------------------------------------
  FOOD                : $500.00
  TRANSPORT           : $100.00
  ENTERTAINMENT       : $200.00
  --------------------------------------------------------------------------------
  ```

  If the user has not set budgets for any category, FinTrack prints `No budgets have been set.`

### Viewing Summary of Expenses `summary-expense`

Gives a summary of your overall expenses.

- **Format:** `summary-expense`
- **Example usage:** `summary-expense`
- **Sample Output:**

  ```
  -----------------------------------------------
  Here is an overall summary of your expenses!
  Total Expense: 40.0

  Here is a breakdown of your expense:
  TRANSPORT: 10.00 (25.00%)
  FOOD: 30.00 (75.00%)

  Your most spent on category is: FOOD
  -----------------------------------------------
  ```

If no expense has been tracked, `summary-expense` will let you know as well.

### View Summary of Income `summary-income`

Gives a summary of your overall income.

- **Format:** `summary-income`
- **Example usage:** `summary-income`
- **Sample Output:**

  ```
  ----------------------------------------------
  Here is an overall summary of your income!
  Total Income: 30.0

  Here is a breakdown of your income:
  SALARY: 20.00 (66.67%)
  INVESTMENT: 10.00 (33.33%)

  Your highest source of income is: SALARY
  ----------------------------------------------
  ```

If no income has been tracked, `summary-income` will let you know as well.

### Get some money saving tips `tips`

Get a random tip.

- **Format:** `tips`
- **Example usage:** `tips`
- **Sample Output:**

  ```
  Take the shuttle bus, it's worth it :(
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
- If any test pertaining to `tips` fails, you should try to run the test again. As tips relies on random number generation, there is a chance that the test can fail due to sheer unluckiness. While it is statistically unlikely the test fails, it is not impossible.

## FAQ

**Q: Does FinTrack save my data between sessions?**  
A: Not yet. All data resides in memory. Export important figures before exiting.

**Q: How do I update an entry?**  
A: Modify the entry (`modify-expense` or `modify-income`) with the correct format.

**Q: Why do I see "Amount must be a valid number"?**  
A: FinTrack only accepts standard numbers without currency symbols (e.g., use `a/15.90`, not `a/$15.90`).

**Q: I input a high-precision decimal, and it is being rounded. Why does this happen?**
A: As a finance tracker, we show our amounts up to 2 decimal points.

**Q: Why does my percentage in summary show as 0.00% even though I have input some amount?**
A: Our percentages are shown to 2 decimal points and hence any precision below 2 decimal points may get rounded down to 0.00%. Similarly, if a category takes up a significantly large percentage, it may be shown as 100.00% due to the percentage being rounded up to the closest 2 decimal points.

**Q: Why does the Error:<XXX> must be a valid number appear even though I input a valid number**
A: Computers can only hold a certain range of values for numerical data types. Hence, you may see some unexpected warning for numbers that fall out of this range. 

**Q: Can I enter dates in other formats such as DD-MM-YYYY?**  
A: No. FinTrack currently requires ISO format `YYYY-MM-DD`.

**Q: How can I review transactions for a specific month?**  
A: Use the optional month filter on incomes (`list-income d/<YYYY-MM>`).

**Q: Can descriptions include spaces?**  
A: Yes. However, descriptions must be the last argument declared, as `des/` will consume the rest of the line.

**Q: Why was my date rejected even though it looks correct?**  
A: Ensure the date is valid on the calendar and in `YYYY-MM-DD` format. Future dates (for example, `2026-10-12`) are allowed, so you can plan upcoming transactions.

## Command Summary

| Command                    | Format                                                                   | Example                                                      |
| :------------------------- | ------------------------------------------------------------------------ | ------------------------------------------------------------ |
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
| `budget`                   | `budget c/<category> a/<amount>`                                         | `budget c/food a/500`                                        |
| `list-budget`              | `list-budget`                                                            | `list-budget`                                                |
| `summary-expense`          | `summary-expense`                                                        | `summary-expense`                                            |
| `summary-income`           | `summary-income`                                                         | `summary-income`                                             |
| `tips`                     | `tips`                                                                   | `tips`                                                       |
| `export`                   | `export <filepath>`                                                      | `export financial_data.csv`                                  |
| `bye`                      | `bye`                                                                    | `bye`                                                        |

### Command Aliases

For faster typing, you can use these shortcuts:

| Alias | Full Command     | Example Usage           |
|-------|------------------|-------------------------|
| `ae`  | `add-expense`    | `ae a/12.50 c/Food d/2025-10-08` |
| `ai`  | `add-income`     | `ai a/3200 c/Salary d/2025-10-01` |
| `le`  | `list-expense`   | `le`                     |
| `li`  | `list-income`    | `li`                     |
| `me`  | `modify-expense` | `me 1 a/15.00 c/Food`    |
| `mi`  | `modify-income`  | `mi 2 a/3500 c/Salary`   |
| `de`  | `delete-expense` | `de 1`                   |
| `di`  | `delete-income`  | `di 2`                   |
| `bg`  | `budget`         | `bg c/Food a/500`        |
| `ex`  | `export`         | `ex financial_data.csv`  |
| `b`   | `balance`        | `b`                      |

Stay tuned to the project repository for upcoming enhancements such as persistent storage and advanced summaries.
