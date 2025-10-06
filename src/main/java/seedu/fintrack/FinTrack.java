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
            if (input.equals(Ui.getExitCommand())) {
                break;
            }

            if (input.startsWith(Ui.getAddIncomeCommand())) {
                try {
                    var income = Parser.parseAddIncome(input);
                    fm.addIncome(income);
                    Ui.printIncomeAdded(income);
                } catch (IllegalArgumentException e) {
                    Ui.printError(e.getMessage());
                }
                continue;
            }

            if (input.equals(Ui.getBalanceCommand())) {
                double balance = fm.getBalance();
                Ui.printBalance(balance, fm.getTotalIncome(), fm.getTotalExpense());
                continue;
            }

            Ui.printError("Unknown command.");
        }

        Ui.printExit();
    }
}
