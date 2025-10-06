package seedu.fintrack;

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
