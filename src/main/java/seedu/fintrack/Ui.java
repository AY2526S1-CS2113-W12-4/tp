// src/main/java/seedu/fintrack/Ui.java
package seedu.fintrack;

import java.util.Scanner;

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

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void printWelcome() {
        System.out.println("Welcome to FinTrack!");
        System.out.println("Type '" + HELP_COMMAND + "' for available commands.");
        System.out.println();
    }

    /** Waits for a single line of user input and returns it. */
    public static String waitForInput() {
        System.out.print("> ");
        return SCANNER.nextLine().trim();
    }

    /** Prints the exit message. */
    public static void printExit() {
        System.out.print("Bye. Hope to see you again soon!");
    }

    static void printHorizontalLine(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print('─');
        }
        System.out.println();
    }

    static void printIncomeAdded(Income income) {
        System.out.println("Income added:");
        System.out.println("  Amount: " + String.format("%.2f", income.getAmount()));
        System.out.println("  Category: " + income.getCategory());
        System.out.println("  Date: " + income.getDate());
        if (income.getDescription() != null && !income.getDescription().isBlank()) {
            System.out.println("  Description: " + income.getDescription());
        }
    }

    static void printExpenseAdded(Expense expense) {
        System.out.println("Expense added:");
        System.out.println("  Amount: " + String.format("%.2f", expense.getAmount()));
        System.out.println("  Category: " + expense.getCategory());
        System.out.println("  Date: " + expense.getDate());
        if (expense.getDescription() != null && !expense.getDescription().isBlank()) {
            System.out.println("  Description: " + expense.getDescription());
        }
    }

    static void printBalance(double balance, double totalIncome, double totalExpense) {
        System.out.println("Overall Balance: " + String.format("%.2f", balance));
        System.out.println("  Total Income:  " + String.format("%.2f", totalIncome));
        System.out.println("  Total Expense: " + String.format("%.2f", totalExpense));
    }

    static void printError(String message) {
        System.out.println("Error: " + message);
    }

    static void printExpenseDeleted(Expense expense, int index) {
        System.out.println("Expense deleted (index " + index + "):");
        System.out.println("  Amount: " + String.format("%.2f", expense.getAmount()));
        System.out.println("  Category: " + expense.getCategory());
        System.out.println("  Date: " + expense.getDate());
        if (expense.getDescription() != null && !expense.getDescription().isBlank()) {
            System.out.println("  Description: " + expense.getDescription());
        }
    }

    static void printIncomeDeleted(Income income, int index) {
        System.out.println("Income deleted (index " + index + "):");
        System.out.println("  Amount: " + String.format("%.2f", income.getAmount()));
        System.out.println("  Category: " + income.getCategory());
        System.out.println("  Date: " + income.getDate());
        if (income.getDescription() != null && !income.getDescription().isBlank()) {
            System.out.println("  Description: " + income.getDescription());
        }
    }

    static void printListOfExpenses(java.util.List<seedu.fintrack.model.Expense> expensesView) {
        if (expensesView.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }
        System.out.println("Expenses (Newest first):");
        var fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < expensesView.size(); i++) {
            var e = expensesView.get(i);
            int idx = i + 1;
            printHorizontalLine(50);
            System.out.println("#" + idx);
            System.out.println("Date: " + e.getDate().format(fmt));
            System.out.println("Amount: $" + String.format("%.2f", e.getAmount()));
            System.out.println("Category: " + e.getCategory());
            if (e.getDescription() != null && !e.getDescription().isBlank()) {
                System.out.println("Description: " + e.getDescription());
            }
        }
        printHorizontalLine(50);
    }

}
