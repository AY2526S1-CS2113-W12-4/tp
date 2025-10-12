// src/main/java/seedu/fintrack/Ui.java
package seedu.fintrack;

import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.Income;

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
    public static final String LIST_COMMAND = "list";
    public static final String EXIT_COMMAND = "bye";

    // Parameter prefixes
    public static final String AMOUNT_PREFIX = "a/";
    public static final String CATEGORY_PREFIX = "c/";
    public static final String DATE_PREFIX = "d/";
    public static final String DESCRIPTION_PREFIX = "desc/"; // optional

    private static final Logger LOGGER = Logger.getLogger(Ui.class.getName());
    private static final Scanner SCANNER = new Scanner(System.in);

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
     * Waits for a single line of user input and returns it.
     */
    public static String waitForInput() {
        System.out.print("> ");
        try {
            String line = SCANNER.nextLine();
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
     * Prints the exit message.
     */
    public static void printExit() {
        LOGGER.info("Exiting application by user request.");
        System.out.println("Bye. Hope to see you again soon!");
    }

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
     * Prints a confirmation message for an added income entry.
     * <p>
     * Performs basic defensive checks (non-null fields and finite amount) and logs at FINE level.
     * </p>
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
     * Prints a confirmation message for an added expense entry.
     * <p>
     * Performs basic defensive checks (non-null fields and finite amount) and logs at FINE level.
     * </p>
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

    static void printBalance(double balance, double totalIncome, double totalExpense) {
        assert Double.isFinite(balance) : "balance must be finite";
        assert Double.isFinite(totalIncome) : "totalIncome must be finite";
        assert Double.isFinite(totalExpense) : "totalExpense must be finite";

        System.out.println("Overall Balance: " + String.format("%.2f", balance));
        System.out.println("  Total Income:  " + String.format("%.2f", totalIncome));
        System.out.println("  Total Expense: " + String.format("%.2f", totalExpense));
        LOGGER.fine("Printed balance summary.");
    }

    /**
     * Prints a user-visible error line and logs a structured warning.
     * <p>
     * This keeps the CLI output stable while still emitting a machine-readable log entry.
     * </p>
     */
    static void printError(String message) {
        // Keep user-visible output identical; add structured logging.
        System.out.println("Error: " + message);
        LOGGER.log(Level.WARNING, "User-visible error: {0}", message == null ? "<null>" : message);
    }

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
     * Prints the list of expenses in reverse chronological order (newest first).
     * <p>
     * Each entry is separated by a horizontal line and shows index, date, amount, category,
     * and (if present) description. Malformed entries are skipped with a warning log.
     * </p>
     */
    static void printListOfExpenses(java.util.List<seedu.fintrack.model.Expense> expensesView) {
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

                printHorizontalLine(50);
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
        printHorizontalLine(50);
        LOGGER.fine("Finished printing expenses list (count=" + expensesView.size() + ").");
    }

    /**
     * Prints a concise summary of available commands and their usage.
     */
    static void printHelp() {
        System.out.println("=== FinTrack Command Summary ===");
        printHorizontalLine(80);

        System.out.println("1. Add an expense:");
        System.out.print("   " + ADD_EXPENSE_COMMAND);
        System.out.println(" a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<description>]");
        System.out.println("   Example: add-expense a/12.50 c/Food d/2025-10-08 desc/Lunch");

        System.out.println();
        System.out.println("2. Add an income:");
        System.out.println("   " + ADD_INCOME_COMMAND + " a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<description>]");
        System.out.println("   Example: add-income a/2000 c/Salary d/2025-10-01 desc/Monthly pay");

        System.out.println();
        System.out.println("3. View all expenses (from latest to earliest date):");
        System.out.println("   " + LIST_COMMAND);

        System.out.println();
        System.out.println("4. Delete an expense:");
        System.out.println("   " + DELETE_EXPENSE_COMMAND + " <index>");
        System.out.println("   Deletes the expense shown at that index in 'list'.");
        System.out.println("   Example: delete-expense 1");

        System.out.println();
        System.out.println("5. Delete an income:");
        System.out.println("   " + DELETE_INCOME_COMMAND + " <index>");
        System.out.println("   Example: delete-income 1");

        System.out.println();
        System.out.println("6. View balance summary:");
        System.out.println("   " + BALANCE_COMMAND);
        System.out.println("   Shows total income, total expenses, and current balance.");

        System.out.println();
        System.out.println("7. Show this help menu:");
        System.out.println("   " + HELP_COMMAND);

        System.out.println();
        System.out.println("8. Exit the program:");
        System.out.println("   " + EXIT_COMMAND);

        printHorizontalLine(80);
    }
}
