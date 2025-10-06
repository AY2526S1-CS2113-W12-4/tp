package seedu.fintrack;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.Income;

final class Parser {
    private Parser() {}

    public static String returnFirstWord(String input) {
        int firstSpaceIndex = getFirstSpaceIndex(input);
        if (firstSpaceIndex != 0) {
            return input.substring(0, firstSpaceIndex - 1);
        } else {
            return input;
        }
    }

    public static int getFirstSpaceIndex(String input) {
        return input.indexOf(' ') + 1;
    }

    private static String getValue(String args, String prefix) {
        int start = args.indexOf(prefix);
        if (start < 0) {
            return null;
        }
        start += prefix.length();

        // Find the next prefix or end-of-string
        int next = findNextPrefixIndex(args, start);
        String val = (next < 0) ? args.substring(start) : args.substring(start, next);
        return val.trim().isEmpty() ? null : val.trim();
    }

    private static String getOptionalValue(String args, String prefix) {
        String v = getValue(args, prefix);
        return v == null ? "" : v;
    }

    private static int findNextPrefixIndex(String args, int fromIndex) {
        int next = -1;
        String[] prefixes = {
            Ui.AMOUNT_PREFIX,
            Ui.CATEGORY_PREFIX,
            Ui.DATE_PREFIX,
            Ui.DESCRIPTION_PREFIX
        };
        for (String p : prefixes) {
            int idx = args.indexOf(p, fromIndex);
            if (idx >= 0 && (next < 0 || idx < next)) {
                next = idx;
            }
        }
        return next;
    }

    /**
     * Expected format:
     * {@code add-income a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<text>]}
     */
    public static Expense parseAddExpense(String input) {
        String args = input.substring(Ui.ADD_EXPENSE_COMMAND.length()).trim();
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Missing parameters. See 'help'.");
        }

        String amountStr = getValue(args, Ui.AMOUNT_PREFIX);
        String category = getValue(args, Ui.CATEGORY_PREFIX);
        String dateStr = getValue(args, Ui.DATE_PREFIX);
        String description = getOptionalValue(args, Ui.DESCRIPTION_PREFIX);

        if (amountStr == null || category == null || dateStr == null) {
            throw new IllegalArgumentException("Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.");
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be a valid number.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative.");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format.");
        }

        return new Expense(amount, category, date, description);
    }

    /**
     * Expected format:
     * {@code add-income a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<text>]}
     */
    public static Income parseAddIncome(String input) {
        String args = input.substring(Ui.ADD_INCOME_COMMAND.length()).trim();
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Missing parameters. See 'help'.");
        }

        String amountStr = getValue(args, Ui.AMOUNT_PREFIX);
        String category = getValue(args, Ui.CATEGORY_PREFIX);
        String dateStr = getValue(args, Ui.DATE_PREFIX);
        String description = getOptionalValue(args, Ui.DESCRIPTION_PREFIX);

        if (amountStr == null || category == null || dateStr == null) {
            throw new IllegalArgumentException("Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.");
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be a valid number.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative.");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format.");
        }

        return new Income(amount, category, date, description);
    }

    public static void parseDeleteExpense(String input) {
        String args = input.substring(Ui.DELETE_EXPENSE_COMMAND.length()).trim();
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Missing parameters. See 'help'.");
        }

        int id = Integer.parseInt(args);
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number.");
        }
    }

    public static void parseDeleteIncome(String input) {
        String args = input.substring(Ui.DELETE_INCOME_COMMAND.length()).trim();
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Missing parameters. See 'help'.");
        }

        int id = Integer.parseInt(args);
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number.");
        }
    }
}
