package seedu.fintrack;

import java.nio.file.Path;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.IncomeCategory;
import seedu.fintrack.tips.TipsStorage;

/**
 * Handles all user interaction for FinTrack.
 * <p>Provides methods for displaying messages, reading input, and formatting output.
 */
public class Ui {
    // Commands
    public static final String HELP_COMMAND = "help";
    public static final String ADD_EXPENSE_COMMAND = "add-expense";
    public static final String ADD_INCOME_COMMAND = "add-income";
    public static final String DELETE_EXPENSE_COMMAND = "delete-expense";
    public static final String DELETE_INCOME_COMMAND = "delete-income";
    public static final String BALANCE_COMMAND = "balance";
    public static final String BUDGET_COMMAND = "budget";
    public static final String LIST_BUDGET_COMMAND = "list-budget";
    public static final String LIST_EXPENSE_COMMAND = "list-expense";
    public static final String LIST_INCOME_COMMAND = "list-income";
    public static final String MODIFY_EXPENSE_COMMAND = "modify-expense";
    public static final String MODIFY_INCOME_COMMAND = "modify-income";
    public static final String SUMMARY_EXPENSE_COMMAND = "summary-expense";
    public static final String SUMMARY_INCOME_COMMAND = "summary-income";
    public static final String TIPS_COMMAND = "tips";
    public static final String EXPORT_COMMAND = "export";
    public static final String EXIT_COMMAND = "bye";

    // Parameter prefixes
    public static final String AMOUNT_PREFIX = "a/";
    public static final String CATEGORY_PREFIX = "c/";
    public static final String DATE_PREFIX = "d/";
    public static final String DESCRIPTION_PREFIX = "des/"; // optional

    private static Scanner uiScanner = new Scanner(System.in);

    private static final Logger LOGGER = Logger.getLogger(Ui.class.getName());
    private static final TipsStorage TIPS = new TipsStorage();
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Reassigns the {@link #uiScanner} used by {@link Ui#waitForInput()} to a new {@link java.util.Scanner}.
     * <p>
     * This method is intended <strong>only for automated testing</strong>, where {@code System.in} is replaced
     * with a scripted {@link java.io.InputStream}. Calling this method ensures that {@code Ui} reads input from
     * the test-provided stream instead of the original standard input.
     * </p>
     * <p>
     * In normal program execution, {@link #uiScanner} remains bound to {@code System.in} and should not be changed.
     * </p>
     *
     * @param scanner the new {@link java.util.Scanner} to assign; ignored if {@code null}
     */
    static void test_setScanner(Scanner scanner) {
        if (scanner != null) {
            uiScanner = scanner;
        }
    }

    /**
     * Prints the welcome banner and a hint to show available commands.
     * <p>
     * Also logs an INFO record indicating that the banner has been shown.
     * </p>
     */
    public static void printWelcome() {
        LOGGER.info("Showing welcome banner.");
        System.out.println("Welcome to FinTrack!");
        System.out.println("Type '" + HELP_COMMAND + "' for available commands.");
        System.out.println();
    }


    /**
     * Reads a single line of user input from {@code System.in}.
     *
     * <p>The returned line is {@linkplain String#trim() trimmed}. If the input
     * stream is unavailable (e.g., closed), this method logs a {@code SEVERE}
     * message and returns the {@link #EXIT_COMMAND} sentinel so callers can
     * shut down cleanly.</p>
     *
     * @return the trimmed line entered by the user; never {@code null}. If the
     *         input stream is unavailable, returns {@link #EXIT_COMMAND}.
     * @throws RuntimeException if an unexpected runtime error occurs while reading.
     * @implNote {@link NoSuchElementException} and {@link IllegalStateException}
     *           are handled internally to produce {@link #EXIT_COMMAND}.
     */
    public static String waitForInput() {
        System.out.print("> ");
        try {
            String line = uiScanner.nextLine();
            return line == null ? "" : line.trim();
        } catch (NoSuchElementException | IllegalStateException e) {
            // stdin closed or scanner unusable — log and signal upstream to exit cleanly.
            LOGGER.log(Level.SEVERE, "Input stream unavailable; requesting shutdown.", e);
            return EXIT_COMMAND;
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while reading input.", e);
            throw e;
        }
    }

    /**
     * Prints the exit message and logs that the application is exiting.
     *
     * @apiNote This method writes to standard output and logs via {@link Logger}.
     */
    public static void printExit() {
        LOGGER.info("Exiting application by user request.");
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Prints a horizontal divider line composed of {@code '-'} characters.
     *
     * @param length number of dashes to print; must be non-negative.
     * @throws IllegalArgumentException if {@code length} is negative.
     */
    static void printHorizontalLine(int length) {
        if (length < 0) {
            LOGGER.warning("Negative line length requested: " + length);
            throw new IllegalArgumentException("length must be non-negative");
        }
        assert length >= 0 : "length must be non-negative";
        for (int i = 0; i < length; i++) {
            System.out.print('-');
        }
        System.out.println();
    }

    /**
     * Prints next line character.
     */
    static void printNextLine() {
        System.out.println();
    }

    /**
     * Prints a confirmation block for a newly added {@link Income}.
     *
     * @param income the persisted income to summarize; must not be {@code null}.
     * @throws NullPointerException if {@code income}, its category, or date is {@code null}.
     * @throws AssertionError if the amount is not finite when assertions are enabled.
     * @implNote Logs at {@code FINE} level after printing.
     */
    static void printIncomeAdded(Income income) {
        Objects.requireNonNull(income, "income cannot be null");
        Objects.requireNonNull(income.getCategory(), "income category cannot be null");
        Objects.requireNonNull(income.getDate(), "income date cannot be null");
        assert Double.isFinite(income.getAmount()) : "income amount must be finite";

        System.out.println("Income added:");
        System.out.println("  Amount: " + String.format("%.2f", income.getAmount()));
        System.out.println("  Category: " + income.getCategory());
        System.out.println("  Date: " + income.getDate());
        if (income.getDescription() != null && !income.getDescription().isBlank()) {
            System.out.println("  Description: " + income.getDescription());
        }
        LOGGER.fine("Printed income added confirmation.");
    }

    /**
     * Prints a confirmation block for a newly added {@link Expense}.
     *
     * @param expense the persisted expense to summarize; must not be {@code null}.
     * @throws NullPointerException if {@code expense}, its category, or date is {@code null}.
     * @throws AssertionError if the amount is not finite when assertions are enabled.
     * @implNote Logs at {@code FINE} level after printing.
     */
    static void printExpenseAdded(Expense expense) {
        Objects.requireNonNull(expense, "expense cannot be null");
        Objects.requireNonNull(expense.getCategory(), "expense category cannot be null");
        Objects.requireNonNull(expense.getDate(), "expense date cannot be null");
        assert Double.isFinite(expense.getAmount()) : "expense amount must be finite";

        System.out.println("Expense added:");
        System.out.println("  Amount: " + String.format("%.2f", expense.getAmount()));
        System.out.println("  Category: " + expense.getCategory());
        System.out.println("  Date: " + expense.getDate());
        if (expense.getDescription() != null && !expense.getDescription().isBlank()) {
            System.out.println("  Description: " + expense.getDescription());
        }
        LOGGER.fine("Printed expense added confirmation.");
    }

    /**
     * Prints a summary of totals and overall balance.
     *
     * @param balance the computed net balance (income − expense); must be finite.
     * @param totalIncome sum of all incomes; must be finite.
     * @param totalExpense sum of all expenses; must be finite.
     * @throws AssertionError if any parameter is non-finite when assertions are enabled.
     * @implNote Logs at {@code FINE} level after printing.
     */
    static void printBalance(double balance, double totalIncome, double totalExpense) {
        assert Double.isFinite(balance) : "balance must be finite";
        assert Double.isFinite(totalIncome) : "totalIncome must be finite";
        assert Double.isFinite(totalExpense) : "totalExpense must be finite";

        System.out.println("Overall Balance: " + String.format("%.2f", balance));
        System.out.println("Total Income: " + String.format("%.2f", totalIncome));
        System.out.println("Total Expense: " + String.format("%.2f", totalExpense));
        LOGGER.fine("Printed balance summary.");
    }

    /** Overloaded version: Prints a balance summary for a specific month. */
    static void printBalance(double balance, double totalIncome, double totalExpense, YearMonth month) {
        Objects.requireNonNull(month, "month cannot be null");
        assert Double.isFinite(balance) : "balance must be finite";
        assert Double.isFinite(totalIncome) : "totalIncome must be finite";
        assert Double.isFinite(totalExpense) : "totalExpense must be finite";

        System.out.println("Overall Balance for the month " // indicates the specific month
                + month.format(YEAR_MONTH_FORMATTER)
                + ": "
                + String.format("%.2f", balance));
        System.out.println("Total Income: " + String.format("%.2f", totalIncome));
        System.out.println("Total Expense: " + String.format("%.2f", totalExpense));
        LOGGER.fine("Printed balance summary.");
    }

    /**
     * Prints a confirmation that a budget has been set for a category.
     *
     * @param category The expense category for which the budget was set.
     * @param amount The budget amount.
     */
    static void printBudgetSet(ExpenseCategory category, double amount) {
        Objects.requireNonNull(category, "category cannot be null");
        assert Double.isFinite(amount) : "amount must be finite";

        System.out.println("Budget set for " + category + ": $" + String.format("%.2f", amount));
        LOGGER.fine("Printed budget set confirmation.");
    }

    /**
     * Prints a confirmation block for a modified Expense.
     *
     * @param expense the modified expense to summarize; must not be null.
     * @param index the index of the modified expense
     * @throws NullPointerException if expense, its category, or date is null.
     * @throws AssertionError if the amount is not finite when assertions are enabled.
     */
    static void printExpenseModified(Expense expense, int index) {
        Objects.requireNonNull(expense, "expense cannot be null");
        Objects.requireNonNull(expense.getCategory(), "expense category cannot be null");
        Objects.requireNonNull(expense.getDate(), "expense date cannot be null");
        assert Double.isFinite(expense.getAmount()) : "expense amount must be finite";

        System.out.println("Expense at index " + index + " modified to:");
        System.out.println("  Amount: " + String.format("%.2f", expense.getAmount()));
        System.out.println("  Category: " + expense.getCategory());
        System.out.println("  Date: " + expense.getDate());
        if (expense.getDescription() != null && !expense.getDescription().isBlank()) {
            System.out.println("  Description: " + expense.getDescription());
        }
        LOGGER.fine("Printed expense modified confirmation.");
    }

    /**
     * Prints a confirmation block for a modified Income.
     *
     * @param income the modified income to summarize; must not be null.
     * @param index the index of the modified income
     * @throws NullPointerException if income, its category, or date is null.
     * @throws AssertionError if the amount is not finite when assertions are enabled.
     */
    static void printIncomeModified(Income income, int index) {
        Objects.requireNonNull(income, "income cannot be null");
        Objects.requireNonNull(income.getCategory(), "income category cannot be null");
        Objects.requireNonNull(income.getDate(), "income date cannot be null");
        assert Double.isFinite(income.getAmount()) : "income amount must be finite";

        System.out.println("Income at index " + index + " modified to:");
        System.out.println("  Amount: " + String.format("%.2f", income.getAmount()));
        System.out.println("  Category: " + income.getCategory());
        System.out.println("  Date: " + income.getDate());
        if (income.getDescription() != null && !income.getDescription().isBlank()) {
            System.out.println("  Description: " + income.getDescription());
        }
        LOGGER.fine("Printed income modified confirmation.");
    }

    /**
     * Prints a warning that the user has exceeded their budget for a category.
     * This warning appears every time an expense causes the budget to be exceeded.
     *
     * @param category The category for which the budget was exceeded.
     * @param budget The budget amount.
     * @param totalSpent The new total amount spent in that category.
     */
    static void printBudgetExceededWarning(ExpenseCategory category, double budget, double totalSpent) {
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ BUDGET ALERT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        if (budget == 0) {
            System.out.printf("Warning: You've set a $0 budget for the %s category.%n", category);
            System.out.printf("You have now spent a total of $%.2f in this category.%n", totalSpent);
            System.out.println("Consider setting a reasonable budget or reducing your spending!");
        } else {
            System.out.printf("Warning: You are over budget for the %s category!%n", category);
            System.out.printf("Budget: $%.2f | Total Spent: $%.2f | Overspent: $%.2f%n",
                    budget, totalSpent, totalSpent - budget);
            System.out.println();
            System.out.println("REMINDER: You have already exceeded your budget.");
            System.out.println("Please start spending less to get back on track with your finances.");
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println();
        LOGGER.fine("Printed budget exceeded warning.");
    }

    /**
     * Prints a warning that the user is approaching their budget limit for a category.
     * This warning appears when spending reaches 90% or more of the budget.
     *
     * @param category The category for which the budget is nearly exceeded.
     * @param budget The budget amount.
     * @param totalSpent The new total amount spent in that category.
     */
    static void printBudgetNearWarning(ExpenseCategory category, double budget, double totalSpent) {
        double percentUsed = (totalSpent / budget) * 100.0;
        double remaining = budget - totalSpent;

        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~ BUDGET CAUTION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.printf("Caution: You're approaching your budget limit for the %s category!%n", category);
        System.out.printf("Budget: $%.2f | Total Spent: $%.2f (%.1f%%) | Remaining: $%.2f%n",
                budget, totalSpent, percentUsed, remaining);
        System.out.println();
        System.out.println("TIP: Consider saving your money and reducing unnecessary expenses");
        System.out.println("     to avoid going over budget.");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println();
        LOGGER.fine("Printed budget near warning.");
    }

    /**
     * Prints the list of all set budgets for expense categories, sorted by category name.
     *
     * @param budgets A map view of the budgets. Must not be null.
     */
    static void printBudgets(Map<ExpenseCategory, Double> budgets) {
        Objects.requireNonNull(budgets, "budgets cannot be null");

        if (budgets.isEmpty()) {
            System.out.println("No budgets have been set.");
            LOGGER.fine("No budgets to list.");
            return;
        }

        System.out.println("Current Budgets:");
        printHorizontalLine(80);
        budgets.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.printf("%-20s: $%.2f%n", entry.getKey(), entry.getValue()));
        printHorizontalLine(80);
        LOGGER.fine("Finished printing budgets list (count=" + budgets.size() + ").");
    }

    /**
     * Prints a user-visible error line and logs a structured warning.
     *
     * <p>User sees a single, stable line beginning with {@code "Error: "}.
     * A parallel {@code WARNING}-level log entry includes the same message for
     * tooling and troubleshooting.</p>
     *
     * @param message the human-readable error message to display; may be {@code null}.
     */
    static void printError(String message) {
        // Keep user-visible output identical; add structured logging.
        System.out.println("Error: " + message);
        LOGGER.log(Level.WARNING, "User-visible error: {0}", message == null ? "<null>" : message);
    }

    /**
     * Prints a confirmation block for a deleted {@link Expense}.
     *
     * @param expense the deleted expense; must not be {@code null}.
     * @param index the 1-based index previously shown to the user; positive if available.
     * @throws NullPointerException if {@code expense}, its category, or date is {@code null}.
     * @throws AssertionError if {@code index} is non-positive or amount is non-finite
     *                        when assertions are enabled.
     * @implNote Logs at {@code FINE} and warns if a non-positive index is provided.
     */
    static void printExpenseDeleted(Expense expense, int index) {
        Objects.requireNonNull(expense, "expense cannot be null");
        if (index <= 0) {
            LOGGER.warning("Expense deletion printed with non-positive index: " + index);
            assert index > 0 : "index should be 1-based and positive";
        }
        Objects.requireNonNull(expense.getCategory(), "expense category cannot be null");
        Objects.requireNonNull(expense.getDate(), "expense date cannot be null");
        assert Double.isFinite(expense.getAmount()) : "expense amount must be finite";

        System.out.println("Expense deleted (index " + index + "):");
        System.out.println("  Amount: " + String.format("%.2f", expense.getAmount()));
        System.out.println("  Category: " + expense.getCategory());
        System.out.println("  Date: " + expense.getDate());
        if (expense.getDescription() != null && !expense.getDescription().isBlank()) {
            System.out.println("  Description: " + expense.getDescription());
        }
        LOGGER.fine("Printed expense deleted confirmation for index " + index + ".");
    }

    /**
     * Prints a confirmation block for a deleted {@link Income}.
     *
     * @param income the deleted income; must not be {@code null}.
     * @param index the 1-based index previously shown to the user; positive if available.
     * @throws NullPointerException if {@code income}, its category, or date is {@code null}.
     * @throws AssertionError if {@code index} is non-positive or amount is non-finite
     *                        when assertions are enabled.
     * @implNote Logs at {@code FINE} and warns if a non-positive index is provided.
     */
    static void printIncomeDeleted(Income income, int index) {
        Objects.requireNonNull(income, "income cannot be null");
        if (index <= 0) {
            LOGGER.warning("Income deletion printed with non-positive index: " + index);
            assert index > 0 : "index should be 1-based and positive";
        }
        Objects.requireNonNull(income.getCategory(), "income category cannot be null");
        Objects.requireNonNull(income.getDate(), "income date cannot be null");
        assert Double.isFinite(income.getAmount()) : "income amount must be finite";

        System.out.println("Income deleted (index " + index + "):");
        System.out.println("  Amount: " + String.format("%.2f", income.getAmount()));
        System.out.println("  Category: " + income.getCategory());
        System.out.println("  Date: " + income.getDate());
        if (income.getDescription() != null && !income.getDescription().isBlank()) {
            System.out.println("  Description: " + income.getDescription());
        }
        LOGGER.fine("Printed income deleted confirmation for index " + index + ".");
    }

    /**
     * Prints the list of incomes in reverse chronological order (newest first).
     *
     * <p>Each entry shows 1-based index, date ({@code yyyy-MM-dd}), amount, category,
     * and optional description. Malformed entries are skipped with a {@code WARNING}
     * log record; printing continues for the remaining items.</p>
     *
     * @param incomesView read-only view of incomes to print; must not be {@code null}.
     * @throws NullPointerException if {@code incomesView} is {@code null}.
     * @implNote Logs at {@code FINE} when the list is empty and after completion.
     */
    static void printListOfIncomes(List<Income> incomesView) {
        Objects.requireNonNull(incomesView, "incomesView cannot be null");

        if (incomesView.isEmpty()) {
            System.out.println("No incomes recorded.");
            LOGGER.fine("No incomes to list.");
            return;
        }
        System.out.println("Incomes (Newest first):");
        var fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < incomesView.size(); i++) {
            try {
                var income = incomesView.get(i);
                int idx = i + 1;
                Objects.requireNonNull(income, "income entry cannot be null");
                Objects.requireNonNull(income.getCategory(), "income category cannot be null");
                Objects.requireNonNull(income.getDate(), "income date cannot be null");
                assert Double.isFinite(income.getAmount()) : "income amount must be finite";

                printHorizontalLine(80);
                System.out.println("#" + idx);
                System.out.println("Date: " + income.getDate().format(fmt));
                System.out.println("Amount: $" + String.format("%.2f", income.getAmount()));
                System.out.println("Category: " + income.getCategory());
                if (income.getDescription() != null && !income.getDescription().isBlank()) {
                    System.out.println("Description: " + income.getDescription());
                }
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Skipping malformed income at position {0}: {1}",
                        new Object[]{i, ex.toString()});
            }
        }
        printHorizontalLine(80);
        LOGGER.fine("Finished printing incomes list (count=" + incomesView.size() + ").");
    }

    /** Overloaded version: Prints a list of incomes for a specific month. */
    static void printListOfIncomes(List<Income> incomesView, YearMonth month) {
        Objects.requireNonNull(incomesView, "incomesView cannot be null");

        if (incomesView.isEmpty()) {
            System.out.println("No incomes recorded.");
            LOGGER.fine("No incomes to list.");
            return;
        }
        System.out.println("Incomes for the month "
                + month.format(YEAR_MONTH_FORMATTER)
                + " (Newest first):"); // indicates the specific month
        var fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < incomesView.size(); i++) {
            try {
                var income = incomesView.get(i);
                int idx = i + 1;
                Objects.requireNonNull(income, "income entry cannot be null");
                Objects.requireNonNull(income.getCategory(), "income category cannot be null");
                Objects.requireNonNull(income.getDate(), "income date cannot be null");
                assert Double.isFinite(income.getAmount()) : "income amount must be finite";

                printHorizontalLine(80);
                System.out.println("#" + idx);
                System.out.println("Date: " + income.getDate().format(fmt));
                System.out.println("Amount: $" + String.format("%.2f", income.getAmount()));
                System.out.println("Category: " + income.getCategory());
                if (income.getDescription() != null && !income.getDescription().isBlank()) {
                    System.out.println("Description: " + income.getDescription());
                }
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Skipping malformed income at position {0}: {1}",
                        new Object[]{i, ex.toString()});
            }
        }
        printHorizontalLine(80);
        LOGGER.fine("Finished printing incomes list (count=" + incomesView.size() + ").");
    }

    /**
     * Prints the list of expenses in reverse chronological order (newest first).
     *
     * <p>Each entry shows 1-based index, date ({@code yyyy-MM-dd}), amount, category,
     * and optional description. Malformed entries are skipped with a {@code WARNING}
     * log record; printing continues for the remaining items.</p>
     *
     * @param expensesView read-only view of expenses to print; must not be {@code null}.
     * @throws NullPointerException if {@code expensesView} is {@code null}.
     * @implNote Logs at {@code FINE} when the list is empty and after completion.
     */
    static void printListOfExpenses(List<Expense> expensesView) {
        Objects.requireNonNull(expensesView, "expensesView cannot be null");

        if (expensesView.isEmpty()) {
            System.out.println("No expenses recorded.");
            LOGGER.fine("No expenses to list.");
            return;
        }
        System.out.println("Expenses (Newest first):");
        var fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < expensesView.size(); i++) {
            try {
                var e = expensesView.get(i);
                int idx = i + 1;
                Objects.requireNonNull(e, "expense entry cannot be null");
                Objects.requireNonNull(e.getCategory(), "expense category cannot be null");
                Objects.requireNonNull(e.getDate(), "expense date cannot be null");
                assert Double.isFinite(e.getAmount()) : "expense amount must be finite";

                printHorizontalLine(80);
                System.out.println("#" + idx);
                System.out.println("Date: " + e.getDate().format(fmt));
                System.out.println("Amount: $" + String.format("%.2f", e.getAmount()));
                System.out.println("Category: " + e.getCategory());
                if (e.getDescription() != null && !e.getDescription().isBlank()) {
                    System.out.println("Description: " + e.getDescription());
                }
            } catch (RuntimeException ex) {
                // Defensive: skip malformed entries but keep the list usable.
                LOGGER.log(Level.WARNING, "Skipping malformed expense at position {0}: {1}",
                        new Object[]{i, ex.toString()});
            }
        }
        printHorizontalLine(80);
        LOGGER.fine("Finished printing expenses list (count=" + expensesView.size() + ").");
    }

    /** Overloaded version: Prints a list of expenses for a specific month. */
    static void printListOfExpenses(List<Expense> expensesView, YearMonth month) {
        Objects.requireNonNull(expensesView, "expensesView cannot be null");

        if (expensesView.isEmpty()) {
            System.out.println("No expenses recorded.");
            LOGGER.fine("No expenses to list.");
            return;
        }
        System.out.println("Expenses for the month "
                + month.format(YEAR_MONTH_FORMATTER)
                + " (Newest first):"); // indicates the specific month
        var fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < expensesView.size(); i++) {
            try {
                var e = expensesView.get(i);
                int idx = i + 1;
                Objects.requireNonNull(e, "expense entry cannot be null");
                Objects.requireNonNull(e.getCategory(), "expense category cannot be null");
                Objects.requireNonNull(e.getDate(), "expense date cannot be null");
                assert Double.isFinite(e.getAmount()) : "expense amount must be finite";

                printHorizontalLine(80);
                System.out.println("#" + idx);
                System.out.println("Date: " + e.getDate().format(fmt));
                System.out.println("Amount: $" + String.format("%.2f", e.getAmount()));
                System.out.println("Category: " + e.getCategory());
                if (e.getDescription() != null && !e.getDescription().isBlank()) {
                    System.out.println("Description: " + e.getDescription());
                }
            } catch (RuntimeException ex) {
                // Defensive: skip malformed entries but keep the list usable.
                LOGGER.log(Level.WARNING, "Skipping malformed expense at position {0}: {1}",
                        new Object[]{i, ex.toString()});
            }
        }
        printHorizontalLine(80);
        LOGGER.fine("Finished printing expenses list (count=" + expensesView.size() + ").");
    }

    /**
     * Prints a breakdown of expenses by category and the top-spend category.
     *
     * @param totalExpense      total amount spent (used to compute percentages)
     * @param expenseByCategory map of each category to its total amount
     */

    static void printExpenseByCategory(Double totalExpense,
            Map<ExpenseCategory, Double> expenseByCategory,
            Map<ExpenseCategory, Double> expensePercentageByCategory) {
        if (totalExpense <= 0 || expenseByCategory.isEmpty()) {
            System.out.println("You have not spent anything yet!");
            return;
        }

        ExpenseCategory topCategory = null;
        double topAmount = Double.NEGATIVE_INFINITY;

        for (Map.Entry<ExpenseCategory, Double> mapEntry : expenseByCategory.entrySet()) {
            ExpenseCategory category = mapEntry.getKey();
            double amount = (mapEntry.getValue());
            double percentOfTotal = expensePercentageByCategory.get(category);
            System.out.printf("%s: %.2f (%.2f%%)%n", category, amount, percentOfTotal);

            if (amount > topAmount) {
                topAmount = amount;
                topCategory = category;
            }
        }

        printNextLine();
        System.out.print("Your most spent on category is: ");
        System.out.println(topCategory);
    }

    /**
     * Prints an overall summary of expenses to the console.
     *
     * <p>The summary includes:
     * <li>The total expense amount</li>
     * <li>A category-by-category breakdown (via printExpenseByCategory)</li>
     *
     * @param totalExpense        the total amount spent across all expenses
     * @param expenseByCategory   a map of each ExpenseCategory to its aggregated amount
     * @throws NullPointerException if totalExpense or expenseByCategory is null
     */
    static void printSummaryExpense(Double totalExpense,
            Map<ExpenseCategory, Double> expenseByCategory,
            Map<ExpenseCategory, Double> expensePercentageByCategory) {
        try {
            printHorizontalLine(80);
            System.out.println("Here is an overall summary of your expenses!");
            System.out.printf("Total Expense: %.2f%n", totalExpense);
            printNextLine();
            System.out.println("Here is a breakdown of your expense:");
            printExpenseByCategory(totalExpense, expenseByCategory, expensePercentageByCategory);
            printHorizontalLine(80);
            LOGGER.log(Level.INFO, "summary-expense called successfully.");
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARNING, "totalExpense or expenseByCategory should not be null.");
            printError(e.getMessage());
        }
    }

    /**
     * Prints a breakdown of income by category and the top-earn category.
     *
     * @param totalIncome      total amount spent (used to compute percentages)
     * @param incomeByCategory map of each category to its total amount
     */
    static void printIncomeByCategory(Double totalIncome,
            Map<IncomeCategory, Double> incomeByCategory,
            Map<IncomeCategory, Double> incomePercentByCategory) {
        if (totalIncome <= 0 || incomeByCategory.isEmpty()) {
            System.out.println("You have not recorded any income yet!");
            return;
        }

        IncomeCategory topCategory = null;
        double topAmount = Double.NEGATIVE_INFINITY;

        for (Map.Entry<IncomeCategory, Double> mapEntry : incomeByCategory.entrySet()) {
            IncomeCategory category = mapEntry.getKey();
            double amount = (mapEntry.getValue());
            double percentOfTotal = incomePercentByCategory.get(category);
            System.out.printf("%s: %.2f (%.2f%%)%n", category, amount, percentOfTotal);

            if (amount > topAmount) {
                topAmount = amount;
                topCategory = category;
            }
        }

        printNextLine();
        System.out.print("Your highest source of income is: ");
        System.out.println(topCategory);
    }

    /**
     * Prints an overall summary of incomes to the console.
     *
     * <p>The summary includes:
     * <li>The total income amount</li>
     * <li>A category-by-category breakdown (via printIncomeByCategory)</li>
     *
     * @param totalIncome        the total amount spent across all expenses
     * @param incomeByCategory   a map of each IncomeCategory to its aggregated amount
     * @throws NullPointerException if totalIncome or incomeByCategory is null
     */
    static void printSummaryIncome(double totalIncome,
            Map<IncomeCategory, Double> incomeByCategory,
            Map<IncomeCategory, Double> incomePercentByCategory) {
        try {
            printHorizontalLine(80);
            System.out.println("Here is an overall summary of your income!");
            System.out.printf("Total Income: %.2f%n", totalIncome);
            printNextLine();
            System.out.println("Here is a breakdown of your income:");
            printIncomeByCategory(totalIncome, incomeByCategory, incomePercentByCategory);
            printHorizontalLine(80);
            LOGGER.log(Level.INFO, "summary-income called successfully.");
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARNING, "totalIncome or incomeByCategory should not be null.");
            printError(e.getMessage());
        }
    }

    static void printTip() {
        String tip = TIPS.returnTip();
        assert tip != null : "tip should not be null.";
        System.out.println(tip);
    }

    /**
     * Prints a concise summary of available commands and their usage.
     *
     * <p>Includes examples for each command and the canonical parameter prefixes:
     * {@link #AMOUNT_PREFIX}, {@link #CATEGORY_PREFIX}, {@link #DATE_PREFIX},
     * and {@link #DESCRIPTION_PREFIX}.</p>
     *
     */
    static void printHelp() {
        System.out.println("=== FinTrack Command Summary ===");
        printHorizontalLine(80);

        System.out.println("1. Add an expense:");
        System.out.print("   " + ADD_EXPENSE_COMMAND + " a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]");
        System.out.println("   Example: add-expense a/12.50 c/Food d/2025-10-08 des/Lunch");
        System.out.println("   Available categories: " +
                "FOOD, STUDY, TRANSPORT, BILLS, ENTERTAINMENT, RENT, GROCERIES, OTHERS");

        System.out.println();
        System.out.println("2. Add an income:");
        System.out.println("   " + ADD_INCOME_COMMAND + " a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]");
        System.out.println("   Example: add-income a/2000 c/Salary d/2025-10-01 des/Monthly pay");
        System.out.println("   Available categories: SALARY, SCHOLARSHIP, INVESTMENT, GIFT, OTHERS");

        System.out.println();
        System.out.println("3. View all expenses (from latest to earliest date):");
        System.out.println("   " + LIST_EXPENSE_COMMAND);
        System.out.println("   To view by month: " + LIST_EXPENSE_COMMAND + " d/<YYYY-MM>");
        System.out.println("   Example: list-expense d/2025-10");

        System.out.println();
        System.out.println("4. View all incomes (from latest to earliest date):");
        System.out.println("   " + LIST_INCOME_COMMAND);
        System.out.println("   To view by month: " + LIST_INCOME_COMMAND + " d/<YYYY-MM>");
        System.out.println("   Example: list-income d/2025-10");

        System.out.println();
        System.out.println("5. Delete an expense:");
        System.out.println("   " + DELETE_EXPENSE_COMMAND + " <index>");
        System.out.println("   Deletes the expense shown at that index in 'list-expense'.");
        System.out.println("   Example: delete-expense 1");

        System.out.println();
        System.out.println("6. Delete an income:");
        System.out.println("   " + DELETE_INCOME_COMMAND + " <index>");
        System.out.println("   Deletes the income shown at that index in 'list-income'.");
        System.out.println("   Example: delete-income 1");

        System.out.println();
        System.out.println("7. Modify an expense:");
        System.out.println("   "
                + MODIFY_EXPENSE_COMMAND
                + " <index> a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]");
        System.out.println("   Modifies the expense shown at that index in 'list-expense'.");
        System.out.println("   Example: modify-expense 1 a/1300 c/Rent d/2024-01-01 des/Monthly rent increased");

        System.out.println();
        System.out.println("8. Modify an income:");
        System.out.println("   "
                + MODIFY_INCOME_COMMAND
                + " <index> a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]");
        System.out.println("   Modifies the income shown at that index in 'list-income'.");
        System.out.println("   Example: modify-income 3 a/250 c/Salary d/2024-01-15 des/Extra performance bonus");

        System.out.println();
        System.out.println("9. View balance summary:");
        System.out.println("   " + BALANCE_COMMAND);
        System.out.println("   Shows total income, total expenses, and current balance.");
        System.out.println("   To view by month: " + BALANCE_COMMAND + " d/<YYYY-MM>");
        System.out.println("   Example: balance d/2025-10");

        System.out.println();
        System.out.println("10. Set budget for expense categories:");
        System.out.println("    " + BUDGET_COMMAND);
        System.out.println("    Example: budget c/FOOD a/1000");
        System.out.println("    Available categories: " +
                "FOOD, STUDY, TRANSPORT, BILLS, ENTERTAINMENT, RENT, GROCERIES, OTHERS");

        System.out.println();
        System.out.println("11. List budgets for expense categories:");
        System.out.println("    " + LIST_BUDGET_COMMAND);
        System.out.println("    Example: list-budget");


        System.out.println();
        System.out.println("12. Show a summary of your total expenses:");
        System.out.println("    " + SUMMARY_EXPENSE_COMMAND);
        System.out.println("    Example: summary-expense");

        System.out.println();
        System.out.println("13. Show a summary of your total income:");
        System.out.println("    " + SUMMARY_INCOME_COMMAND);
        System.out.println("    Example: summary-income");

        System.out.println();
        System.out.println("14. Provides a useful tip:");
        System.out.println("    " + TIPS_COMMAND);
        System.out.println("    Example: tips");


        System.out.println();
        System.out.println("15. Show this help menu:");
        System.out.println("    " + HELP_COMMAND);
        System.out.println("    Example: help");

        System.out.println();
        System.out.println("16. Exit the program:");
        System.out.println("    " + EXIT_COMMAND);
        System.out.println("    Example: bye");

        System.out.println();
        System.out.println("17. Export data to CSV file:");
        System.out.println("    " + EXPORT_COMMAND + " <filepath>");
        System.out.println("    Example: export financial_data.csv");

        printHorizontalLine(80);
    }

    /**
     * Prints a success message after exporting data to a CSV file.
     *
     * @param path The path where the data was exported
     */
    static void printExportSuccess(Path path) {
        System.out.println("Successfully exported data to: " + path.toAbsolutePath().normalize());
    }
}
