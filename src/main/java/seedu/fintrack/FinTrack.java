package seedu.fintrack;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.time.YearMonth;
import java.util.Optional;

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
                try {
                    Optional<YearMonth> ymOpt = Parser.parseOptionalMonthForBalance(input); // parses optional YYYY-MM
                    if (ymOpt.isPresent()) {
                        YearMonth ym = ymOpt.get();
                        double monthlyIncome = fm.getIncomesViewForMonth(ym).stream()
                                .mapToDouble(seedu.fintrack.model.Income::getAmount).sum();
                        double monthlyExpense = fm.getExpensesViewForMonth(ym).stream()
                                .mapToDouble(seedu.fintrack.model.Expense::getAmount).sum();
                        Ui.printBalance(monthlyIncome - monthlyExpense, monthlyIncome, monthlyExpense);
                    } else {
                        Ui.printBalance(
                                fm.getBalance(),
                                fm.getTotalIncome(),
                                fm.getTotalExpense()
                        );
                    }
                } catch (IllegalArgumentException ex) {
                    Ui.printError(ex.getMessage());
                }
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
                try {
                    Optional<YearMonth> ymOpt = Parser.parseOptionalMonthForExpenseList(input);
                    if (ymOpt.isPresent()) {
                        YearMonth ym = ymOpt.get();
                        Ui.printListOfExpenses(fm.getExpensesViewForMonth(ym));
                    } else {
                        Ui.printListOfExpenses(fm.getExpensesView());
                    }
                } catch (IllegalArgumentException ex) {
                    Ui.printError(ex.getMessage());
                }
                break;
            case Ui.LIST_INCOME_COMMAND:
                try {
                    Optional<YearMonth> ymOpt = Parser.parseOptionalMonthForIncomeList(input);
                    if (ymOpt.isPresent()) {
                        YearMonth ym = ymOpt.get();
                        Ui.printListOfIncomes(fm.getIncomesViewForMonth(ym));
                    } else {
                        Ui.printListOfIncomes(fm.getIncomesView());
                    }
                } catch (IllegalArgumentException ex) {
                    Ui.printError(ex.getMessage());
                }
                break;
            case Ui.HELP_COMMAND:
                Ui.printHelp();
                break;
            default:
                Ui.printError("Invalid command. Type 'help' for a list of available commands.");
            }
        }

        Ui.printExit();
    }
}
