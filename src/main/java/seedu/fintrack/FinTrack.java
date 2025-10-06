package seedu.fintrack;

public class FinTrack {
    /**
     * Main entry-point for the java.finTrack.FinTrack application.
     */
    public static void main(String[] args) {
        Ui.printWelcome();
        while (true) {
            String input = Ui.waitForInput();
            if (input.equals(Ui.getExitCommand())) {
                break;
            }
            System.out.println("You entered: " + input);
        }
        Ui.printExit();
    }
}
