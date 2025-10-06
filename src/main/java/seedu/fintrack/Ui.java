package seedu.fintrack;

import java.util.Scanner;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.Income;

/**
 * Handles all user interaction for FinTrack.
 * <p>
 * Provides methods for displaying messages, reading input, and formatting output
 * for features such as adding income and viewing the overall balance.
 */
public class Ui {
    // Commands
    private static final String ADD_INCOME_COMMAND = "add-income";
    private static final String BALANCE_COMMAND = "balance";
    private static final String EXIT_COMMAND = "bye";

    // Parameter prefixes
    private static final String AMOUNT_PREFIX = "a/";
    private static final String CATEGORY_PREFIX = "c/";
    private static final String DATE_PREFIX = "d/";
    private static final String DESCRIPTION_PREFIX = "desc/"; // optional

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void printWelcome() {
        System.out.println("Welcome to FinTrack!");
        System.out.println("Type 'help' for available commands.");
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

    static String getAddIncomeCommand() { return ADD_INCOME_COMMAND; }
    static String getBalanceCommand() { return BALANCE_COMMAND; }
    static String getExitCommand() { return EXIT_COMMAND; }

    static String getAmountPrefix() { return AMOUNT_PREFIX; }
    static String getCategoryPrefix() { return CATEGORY_PREFIX; }
    static String getDatePrefix() { return DATE_PREFIX; }
    static String getDescriptionPrefix() { return DESCRIPTION_PREFIX; }

    // Output formatting
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
        System.out.println("Income added:");
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
}
