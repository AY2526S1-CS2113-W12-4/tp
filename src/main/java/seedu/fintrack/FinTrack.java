package seedu.fintrack;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Entry point of the FinTrack application.
 * <p>
 * Handles the main CLI loop for receiving and processing user commands.
 * Supports:
 * <ul>
 *   <li><code>add-income</code>: Adds a new income entry.</li>
 *   <li><code>balance</code>: Displays overall balance (total income minus total expense).</li>
 *   <li><code>bye</code>: Exits the program.</li>
 * </ul>
 */
public class FinTrack {
    private static final String INVALID_COMMAND_MESSAGE =
            "Invalid command. Type 'help' for a list of available commands.";
    private static final String NO_ARGUMENTS_MESSAGE_TEMPLATE =
            "The '%s' command does not take additional arguments.";

    /*
     * Initialises java.util.logging from a classpath resource ('logging.properties') when no
     * logging configuration is provided via system properties. Falls back to JDK defaults if the
     * resource is absent or cannot be read.
     */
    static {
        if (System.getProperty("java.util.logging.config.file") == null &&
                System.getProperty("java.util.logging.config.class") == null) {
            try (InputStream is =
                         FinTrack.class.getClassLoader().getResourceAsStream("logging.properties")) {
                if (is != null) {
                    LogManager.getLogManager().readConfiguration(is);
                }
            } catch (IOException e) {
                Logger.getLogger(FinTrack.class.getName())
                        .log(Level.WARNING, "Could not load logging.properties; using defaults.", e);
            }
        }
    }


    /**
     * Main entry-point for the FinTrack application.
     */
    public static void main(String[] args) {
        Ui.printWelcome();
        FinanceManager fm = new FinanceManager();

        while (true) {
            String input = Ui.waitForInput();
            String firstWord = Parser.returnFirstWord(input);
            if (firstWord.equals(Ui.EXIT_COMMAND)) {
                if (hasUnexpectedArguments(input, Ui.EXIT_COMMAND)) {
                    Ui.printError(formatNoArgumentsMessage(Ui.EXIT_COMMAND));
                    continue;
                }
                break;
            }
            switch (firstWord) {
            case Ui.ADD_EXPENSE_COMMAND:
                try {
                    var expense = Parser.parseAddExpense(input);
                    boolean isOverBudget = fm.addExpense(expense);
                    Ui.printExpenseAdded(expense);

                    if (isOverBudget) {
                        double totalSpent = fm.getTotalExpenseForCategory(expense.getCategory());
                        double budget = fm.getBudgetForCategory(expense.getCategory());
                        Ui.printBudgetExceededWarning(expense.getCategory(), budget, totalSpent);
                    }
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.ADD_INCOME_COMMAND:
                try {
                    var income = Parser.parseAddIncome(input);
                    fm.addIncome(income);
                    Ui.printIncomeAdded(income);
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.BALANCE_COMMAND:
                if (hasUnexpectedArguments(input, Ui.BALANCE_COMMAND)) {
                    Ui.printError(formatNoArgumentsMessage(Ui.BALANCE_COMMAND));
                    break;
                }
                double balance = fm.getBalance();
                Ui.printBalance(balance, fm.getTotalIncome(), fm.getTotalExpense());
                break;
            case Ui.BUDGET_COMMAND:
                try {
                    var budgetInfo = Parser.parseSetBudget(input);
                    fm.setBudget(budgetInfo.getKey(), budgetInfo.getValue());
                    Ui.printBudgetSet(budgetInfo.getKey(), budgetInfo.getValue());
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.LIST_BUDGET_COMMAND:
                if (hasUnexpectedArguments(input, Ui.LIST_BUDGET_COMMAND)) {
                    Ui.printError(formatNoArgumentsMessage(Ui.LIST_BUDGET_COMMAND));
                    break;
                }
                Ui.printBudgets(fm.getBudgetsView());
                break;
            case Ui.DELETE_EXPENSE_COMMAND:
                try {
                    int expenseIndex = Parser.parseDeleteExpense(input);
                    var deletedExpense = fm.deleteExpense(expenseIndex);
                    Ui.printExpenseDeleted(deletedExpense, expenseIndex);
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.DELETE_INCOME_COMMAND:
                try {
                    int incomeIndex = Parser.parseDeleteIncome(input);
                    var deletedIncome = fm.deleteIncome(incomeIndex);
                    Ui.printIncomeDeleted(deletedIncome, incomeIndex);
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.LIST_COMMAND:
                if (hasUnexpectedArguments(input, Ui.LIST_COMMAND)) {
                    Ui.printError(formatNoArgumentsMessage(Ui.LIST_COMMAND));
                    break;
                }
                Ui.printListOfExpenses(fm.getExpensesView());
                break;
            case Ui.LIST_INCOME_COMMAND:
                if (hasUnexpectedArguments(input, Ui.LIST_INCOME_COMMAND)) {
                    Ui.printError(formatNoArgumentsMessage(Ui.LIST_INCOME_COMMAND));
                    break;
                }
                Ui.printListOfIncomes(fm.getIncomesView());
                break;
            case Ui.HELP_COMMAND:
                if (hasUnexpectedArguments(input, Ui.HELP_COMMAND)) {
                    Ui.printError(formatNoArgumentsMessage(Ui.HELP_COMMAND));
                    break;
                }
                Ui.printHelp();
                break;
            default:
                Ui.printError(INVALID_COMMAND_MESSAGE);
            }
        }

        Ui.printExit();
    }

    private static boolean hasUnexpectedArguments(String input, String commandWord) {
        if (!input.startsWith(commandWord)) {
            return false;
        }
        if (input.length() == commandWord.length()) {
            return false;
        }
        String arguments = input.substring(commandWord.length()).trim();
        return !arguments.isEmpty();
    }

    private static String formatNoArgumentsMessage(String commandWord) {
        return String.format(NO_ARGUMENTS_MESSAGE_TEMPLATE, commandWord);
    }
}
