package seedu.fintrack;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.Income;

final class Parser {
    private Parser() {}

    public static String returnFirstWord(String input) {
        int firstSpaceIndex = getFirstSpaceIndex(input);
        if (firstSpaceIndex == 0) {
            return returnFirstWord(input.substring(firstSpaceIndex + 1));
        } else if (firstSpaceIndex != -1) {
            return input.substring(0, firstSpaceIndex);
        } else {
            return input;
        }
    }

    public static int getFirstSpaceIndex(String input) {
        return input.indexOf(' ');
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

    /**
     * Parses delete-expense command and returns the index.
     * Expected format: {@code delete-expense <index>}
     * @param input The full command string
     * @return The 1-based index of the expense to delete
     * @throws IllegalArgumentException if the format is invalid or index is not positive
     */
    public static int parseDeleteExpense(String input) {
        String args = input.substring(Ui.DELETE_EXPENSE_COMMAND.length()).trim();
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Missing expense index. Usage: delete-expense <index>");
        }

        try {
            int id = Integer.parseInt(args);
            if (id <= 0) {
                throw new IllegalArgumentException("Expense index must be a positive number.");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expense index must be a valid number.");
        }
    }

    /**
     * Parses delete-income command and returns the index.
     * Expected format: {@code delete-income <index>}
     * @param input The full command string
     * @return The 1-based index of the income to delete
     * @throws IllegalArgumentException if the format is invalid or index is not positive
     */
    public static int parseDeleteIncome(String input) {
        String args = input.substring(Ui.DELETE_INCOME_COMMAND.length()).trim();
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Missing income index. Usage: delete-income <index>");
        }

        try {
            int id = Integer.parseInt(args);
            if (id <= 0) {
                throw new IllegalArgumentException("Income index must be a positive number.");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Income index must be a valid number.");
        }
    }
}
