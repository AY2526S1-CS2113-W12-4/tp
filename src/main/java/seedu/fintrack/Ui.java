package seedu.fintrack;

import java.util.Scanner;

public class Ui {
    // Commands (class/static variables)
    private static final String HELP_COMMAND = "help";
    private static final String ADD_EXPENSE_COMMAND = "add-expense";
    private static final String ADD_INCOME_COMMAND = "add-income";
    private static final String DELETE_COMMAND = "delete";
    private static final String BALANCE_COMMAND = "balance";
    private static final String LIST_COMMAND = "list";
    private static final String EXIT_COMMAND = "bye";

    // Parameter prefixes
    private static final String AMOUNT_PREFIX = "a/";
    private static final String CATEGORY_PREFIX = "c/";
    private static final String DATE_PREFIX = "d/";

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void printWelcome() {
        System.out.println("Welcome to FinTrack!");
        System.out.println("Type '" + HELP_COMMAND + "' for available commands.");
        System.out.println();
    }

    /**
     * Waits for a single line of user input and returns it.
     * Prints a simple > for formatting before waiting.
     */
    public static String waitForInput() {
        System.out.print("> ");
        return SCANNER.nextLine();
    }

    /**
     * Prints the exit message.
     */
    public static void printExit() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    static String getHelpCommand() { return HELP_COMMAND; }
    static String getAddExpenseCommand() { return ADD_EXPENSE_COMMAND; }
    static String getAddIncomeCommand() { return ADD_INCOME_COMMAND; }
    static String getDeleteCommand() { return DELETE_COMMAND; }
    static String getBalanceCommand() { return BALANCE_COMMAND; }
    static String getListCommand() { return LIST_COMMAND; }

    static String getAmountPrefix() { return AMOUNT_PREFIX; }
    static String getCategoryPrefix() { return CATEGORY_PREFIX; }
    static String getDatePrefix() { return DATE_PREFIX; }
    static String getExitCommand() { return EXIT_COMMAND; }
}
