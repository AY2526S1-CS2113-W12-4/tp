package seedu.fintrack;

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
                    fm.addExpense(expense);
                    Ui.printExpenseAdded(expense);
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
                double balance = fm.getBalance();
                Ui.printBalance(balance, fm.getTotalIncome(), fm.getTotalExpense());
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
                Ui.printListOfExpenses(fm.getExpensesView());
                break;
            case Ui.HELP_COMMAND:
                break;
            default:
                Ui.printError("Invalid command. Type 'help' for a list of available commands.");
            }
        }

        Ui.printExit();
    }
}
