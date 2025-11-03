package seedu.fintrack;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.BudgetStatus;
import seedu.fintrack.model.IncomeCategory;
import seedu.fintrack.storage.CsvStorage;
import seedu.fintrack.storage.Storage;
import seedu.fintrack.storage.PlainTextStorage;

import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.time.YearMonth;
import java.util.Optional;
import java.util.Map;

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
            "The '%s' command does not take additional arguments. " +
                    "Just input the command by itself with no other stray text.";
    private static final String UNSUPPORTED_CHARACTER_MESSAGE =
            "Unsupported characters detected. Please use standard ASCII text only.";

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
        FinanceManager fm = new FinanceManager();

        boolean persistenceEnabled = !isPersistenceDisabled();
        PlainTextStorage persistence = null;
        Path dataFile = null;
        boolean persistenceWritable = false;

        if (persistenceEnabled) {
            persistence = new PlainTextStorage();
            dataFile = persistence.resolveDefaultFile();
            persistenceWritable = persistence.canWrite(dataFile);
            if (!persistenceWritable) {
                disableFileLogging();
            }
            persistence.load(dataFile, fm);
            if (!persistenceWritable) {
                Ui.printPersistenceWarning("Unable to write to persistence file: \n'"
                        + dataFile
                        + "'.\nChanges made in this session may not be saved.");
            }
        }

        Ui.printWelcome();

        while (true) {
            String input = Ui.waitForInput();
            if (Parser.containsForbiddenSeparator(input)) {
                Ui.printError("The '|' character is reserved for persistence. "
                        + "Please remove it from your command.");
                continue;
            }
            if (!isAsciiSafe(input)) {
                Ui.printError(UNSUPPORTED_CHARACTER_MESSAGE);
                continue;
            }
            String firstWord = Parser.returnFirstWord(input);
            if (firstWord.equals(Ui.EXIT_COMMAND)) {
                if (hasUnexpectedArguments(input, Ui.EXIT_COMMAND)) {
                    Ui.printError(formatNoArgumentsMessage(Ui.EXIT_COMMAND));
                    continue;
                }
                break;
            }

            String expandedInput = Parser.expandAliasesInInput(input);

            switch (firstWord) {
            case Ui.ADD_EXPENSE_COMMAND:
                try {
                    var expense = Parser.parseAddExpense(expandedInput);
                    BudgetStatus status = fm.addExpense(expense);
                    Ui.printExpenseAdded(expense);

                    double totalSpent = fm.getTotalExpenseForCategory(expense.getCategory());
                    Double budget = fm.getBudgetForCategory(expense.getCategory());

                    if (status.isOverBudget() && budget != null) {
                        Ui.printBudgetExceededWarning(expense.getCategory(), budget, totalSpent);
                    } else if (status.isNearBudget() && budget != null) {
                        Ui.printBudgetNearWarning(expense.getCategory(), budget, totalSpent);
                    }
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.ADD_INCOME_COMMAND:
                try {
                    var income = Parser.parseAddIncome(expandedInput);
                    fm.addIncome(income);
                    Ui.printIncomeAdded(income);
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.BALANCE_COMMAND:
                try {
                    Optional<YearMonth> ymOpt = Parser.parseOptionalMonthForBalance(expandedInput);
                    if (ymOpt.isPresent()) {
                        YearMonth ym = ymOpt.get();
                        double monthlyIncome = fm.getIncomesViewForMonth(ym).stream()
                                .mapToDouble(seedu.fintrack.model.Income::getAmount).sum();
                        double monthlyExpense = fm.getExpensesViewForMonth(ym).stream()
                                .mapToDouble(seedu.fintrack.model.Expense::getAmount).sum();
                        Ui.printBalance(monthlyIncome - monthlyExpense, monthlyIncome, monthlyExpense, ym);
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
                    var budgetInfo = Parser.parseSetBudget(expandedInput);
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
            case Ui.DELETE_BUDGET_COMMAND:
                try {
                    ExpenseCategory category = Parser.parseDeleteBudget(expandedInput);
                    fm.deleteBudget(category);
                    Ui.printBudgetDeleted(category);
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.DELETE_EXPENSE_COMMAND:
                try {
                    int expenseIndex = Parser.parseDeleteExpense(expandedInput);
                    var deletedExpense = fm.deleteExpense(expenseIndex);
                    Ui.printExpenseDeleted(deletedExpense, expenseIndex);
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.DELETE_INCOME_COMMAND:
                try {
                    int incomeIndex = Parser.parseDeleteIncome(expandedInput);
                    var deletedIncome = fm.deleteIncome(incomeIndex);
                    Ui.printIncomeDeleted(deletedIncome, incomeIndex);
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.MODIFY_EXPENSE_COMMAND:
                try {
                    int index = Parser.parseModifyExpenseIndex(expandedInput);
                    Expense oldExpense = fm.getExpense(index);
                    Expense newExpense = Parser.parseModifyExpenseWithDefaults(expandedInput, oldExpense);
                    BudgetStatus status = fm.modifyExpense(index, newExpense);
                    Ui.printExpenseModified(newExpense, index);
                    double totalSpent = fm.getTotalExpenseForCategory(newExpense.getCategory());
                    Double budget = fm.getBudgetForCategory(newExpense.getCategory());

                    if (status.isOverBudget() && budget != null) {
                        Ui.printBudgetExceededWarning(newExpense.getCategory(), budget, totalSpent);
                    } else if (status.isNearBudget() && budget != null) {
                        Ui.printBudgetNearWarning(newExpense.getCategory(), budget, totalSpent);
                    }
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.MODIFY_INCOME_COMMAND:
                try {
                    int index = Parser.parseModifyIncomeIndex(expandedInput);
                    Income oldIncome = fm.getIncome(index);
                    Income newIncome = Parser.parseModifyIncomeWithDefaults(expandedInput, oldIncome);
                    fm.modifyIncome(index, newIncome);
                    Ui.printIncomeModified(newIncome, index);
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.LIST_EXPENSE_COMMAND:
                try {
                    Optional<YearMonth> ymOpt = Parser.parseOptionalMonthForExpenseList(expandedInput);
                    if (ymOpt.isPresent()) {
                        YearMonth ym = ymOpt.get();
                        Ui.printListOfExpenses(fm.getExpensesViewForMonth(ym), ym);
                    } else {
                        Ui.printListOfExpenses(fm.getExpensesView());
                    }
                } catch (IllegalArgumentException ex) {
                    Ui.printError(ex.getMessage());
                }
                break;
            case Ui.LIST_INCOME_COMMAND:
                try {
                    Optional<YearMonth> ymOpt = Parser.parseOptionalMonthForIncomeList(expandedInput);
                    if (ymOpt.isPresent()) {
                        YearMonth ym = ymOpt.get();
                        Ui.printListOfIncomes(fm.getIncomesViewForMonth(ym), ym);
                    } else {
                        Ui.printListOfIncomes(fm.getIncomesView());
                    }
                } catch (IllegalArgumentException ex) {
                    Ui.printError(ex.getMessage());
                }
                break;
            case Ui.HELP_COMMAND:
                if (hasUnexpectedArguments(input, Ui.HELP_COMMAND)) {
                    Ui.printError(formatNoArgumentsMessage(Ui.HELP_COMMAND));
                    break;
                }
                Ui.printHelp();
                break;
            case Ui.EXPORT_COMMAND:
                try {
                    var exportPath = Parser.parseExport(expandedInput);
                    Storage storage = new CsvStorage();
                    storage.export(exportPath, 
                                 fm.getIncomesView(), 
                                 fm.getExpensesView(),
                                 fm.getTotalIncome(),
                                 fm.getTotalExpense(),
                                 fm.getBalance());
                    Ui.printExportSuccess(exportPath);
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.SUMMARY_EXPENSE_COMMAND:
                try {
                    if (hasUnexpectedArguments(input, Ui.SUMMARY_EXPENSE_COMMAND)) {
                        Ui.printError(formatNoArgumentsMessage(Ui.SUMMARY_EXPENSE_COMMAND));
                        break;
                    }

                    Map<ExpenseCategory, Double> expenseByCategory = fm.getExpenseByCategory();
                    double totalExpense = fm.getTotalExpense();
                    Map<ExpenseCategory, Double>expensePercentByCategory = fm.getExpensePercentageByCategory(
                            expenseByCategory, totalExpense);
                    Ui.printSummaryExpense(totalExpense, expenseByCategory, expensePercentByCategory);
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.SUMMARY_INCOME_COMMAND:
                try {
                    if (hasUnexpectedArguments(input, Ui.SUMMARY_INCOME_COMMAND)) {
                        Ui.printError(formatNoArgumentsMessage(Ui.SUMMARY_INCOME_COMMAND));
                        break;
                    }

                    Map<IncomeCategory, Double> incomeByCategory = fm.getIncomeByCategory();
                    double totalIncome = fm.getTotalIncome();
                    Map<IncomeCategory, Double> incomePercentByCategory = fm.getIncomePercentageByCategory(
                            incomeByCategory, totalIncome);
                    Ui.printSummaryIncome(totalIncome, incomeByCategory, incomePercentByCategory);
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            case Ui.TIPS_COMMAND:
                try {
                    if (hasUnexpectedArguments(input, Ui.TIPS_COMMAND)) {
                        Ui.printError(formatNoArgumentsMessage(Ui.TIPS_COMMAND));
                        break;
                    }

                    Ui.printTip();
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                break;
            default:
                Ui.printError(INVALID_COMMAND_MESSAGE);
            }
        }

        if (persistenceEnabled) {
            if (persistenceWritable) {
                persistence.save(dataFile, fm);
            } else {
                Ui.printPersistenceWarning("Unable to save data; persistence file could not be written.");
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

    private static boolean isAsciiSafe(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }
        return input.chars().allMatch(codePoint -> codePoint >= 0 && codePoint <= 0x7F);
    }

    /**
     * Returns whether persistence should be skipped for this run.
     *
     * <p>Controlled via the {@code fintrack.disablePersistence} system property. When set to
     * {@code true}, loading and saving of the plain-text data file is suppressed. This is used
     * by the automated tests to keep runs isolated.</p>
     *
     * @return {@code true} if persistence is disabled, {@code false} otherwise
     */
    private static boolean isPersistenceDisabled() {
        return Boolean.getBoolean("fintrack.disablePersistence");
    }

    private static void disableFileLogging() {
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        java.util.logging.Handler[] handlers = rootLogger.getHandlers();
        for (java.util.logging.Handler handler : handlers) {
            if (handler instanceof java.util.logging.FileHandler) {
                handler.close();
                rootLogger.removeHandler(handler);
            }
        }
    }
}
