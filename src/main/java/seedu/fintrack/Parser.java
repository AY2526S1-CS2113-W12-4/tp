package seedu.fintrack;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.Income;

final class Parser {
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    static {
        // Suppresses INFO and FINER log messages
        LOGGER.setLevel(Level.WARNING);
    }

    private Parser() {}

    public static String returnFirstWord(String input) {
        assert input != null : "Input cannot be null.";

        int firstSpaceIndex = getFirstSpaceIndex(input);
        if (firstSpaceIndex == 0) {
            // Handle leading spaces
            return returnFirstWord(input.substring(firstSpaceIndex + 1));
        } else if (firstSpaceIndex != -1) {
            return input.substring(0, firstSpaceIndex);
        } else {
            return input;
        }
    }

    public static int getFirstSpaceIndex(String input) {
        assert input != null : "Input cannot be null.";

        return input.indexOf(' ');
    }

    private static String getValue(String args, String prefix) {
        assert args != null : "Arguments cannot be null.";
        assert prefix != null : "Prefix cannot be null.";

        LOGGER.log(
                Level.FINER,
                "Attempting to extract value for prefix ''{0}'' from args: ''{1}''",
                new Object[]{prefix, args}
        );

        int start = args.indexOf(prefix);
        if (start < 0) {
            LOGGER.log(Level.FINER, "Prefix not found.");
            return null;
        }
        start += prefix.length();

        // Find the next prefix or end-of-string
        int next = findNextPrefixIndex(args, start);
        String val = (next < 0) ? args.substring(start) : args.substring(start, next);
        val = val.trim();

        if (val.isEmpty()) {
            LOGGER.log(Level.FINER,"Value for prefix is empty.");
            return null;
        }

        LOGGER.log(Level.FINER, "Extracted value: ''{0}''", val);
        return val;
    }

    private static String getOptionalValue(String args, String prefix) {
        String v = getValue(args, prefix);
        return v == null ? "" : v;
    }

    private static int findNextPrefixIndex(String args, int fromIndex) {
        assert args != null : "Arguments cannot be null.";

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
     * {@code add-expense a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<text>]}
     */
    public static Expense parseAddExpense(String input) throws IllegalArgumentException {
        assert input != null : "Input for parsing add-expense cannot be null.";
        LOGGER.log(Level.INFO, "Parsing expense input: ''{0}''.", input);

        String args = input.substring(Ui.ADD_EXPENSE_COMMAND.length()).trim();
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING,"Missing parameters for add-expense command.");
            throw new IllegalArgumentException("Missing parameters. See 'help'.");
        }

        String amountStr = getValue(args, Ui.AMOUNT_PREFIX);
        String category = getValue(args, Ui.CATEGORY_PREFIX);
        String dateStr = getValue(args, Ui.DATE_PREFIX);
        String description = getOptionalValue(args, Ui.DESCRIPTION_PREFIX);

        if (amountStr == null || category == null || dateStr == null) {
            LOGGER.log(Level.WARNING,"Missing one or more required parameters for add-expense command.");
            throw new IllegalArgumentException("Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.");
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid amount format: {0}.", amountStr);
            throw new IllegalArgumentException("Amount must be a valid number.");
        }
        if (amount < 0) {
            LOGGER.log(Level.WARNING, "Negative amount provided: {0}.", amount);
            throw new IllegalArgumentException("Amount must be non-negative.");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Invalid date format: {0}.", dateStr);
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format.");
        }

        Expense newExpense = new Expense(amount, category, date, description);
        LOGGER.log(Level.INFO, "Successfully parsed new expense: {0}.", newExpense);
        return newExpense;
    }

    /**
     * Expected format:
     * {@code add-income a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<text>]}
     */
    public static Income parseAddIncome(String input) throws IllegalArgumentException {
        assert input != null : "Input for parsing add-income cannot be null.";
        LOGGER.log(Level.INFO, "Parsing add-income input: ''{0}''.", input);

        String args = input.substring(Ui.ADD_INCOME_COMMAND.length()).trim();
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING,"Missing parameters for add-income command.");
            throw new IllegalArgumentException("Missing parameters. See 'help'.");
        }

        String amountStr = getValue(args, Ui.AMOUNT_PREFIX);
        String category = getValue(args, Ui.CATEGORY_PREFIX);
        String dateStr = getValue(args, Ui.DATE_PREFIX);
        String description = getOptionalValue(args, Ui.DESCRIPTION_PREFIX);

        if (amountStr == null || category == null || dateStr == null) {
            LOGGER.log(Level.WARNING,"Missing one or more required parameters for add-income.");
            throw new IllegalArgumentException("Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.");
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid amount format: {0}.", amountStr);
            throw new IllegalArgumentException("Amount must be a valid number.");
        }
        if (amount < 0) {
            LOGGER.log(Level.WARNING, "Negative amount provided: {0}.", amount);
            throw new IllegalArgumentException("Amount must be non-negative.");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Invalid date format: {0}.", dateStr);
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format.");
        }

        Income newIncome = new Income(amount, category, date, description);
        LOGGER.log(Level.INFO, "Successfully parsed new income: {0}", newIncome);
        return newIncome;
    }

    /**
     * Parses delete-expense command and returns the index.
     * Expected format: {@code delete-expense <index>}
     * @param input The full command string
     * @return The 1-based index of the expense to delete
     * @throws IllegalArgumentException if the format is invalid or index is not positive
     */
    public static int parseDeleteExpense(String input) throws IllegalArgumentException {
        assert input != null : "Input for parsing delete-expense cannot be null.";
        LOGGER.log(Level.INFO, "Parsing expense input: {0}", input);

        String args = input.substring(Ui.DELETE_EXPENSE_COMMAND.length()).trim();
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING,"Missing index for delete-expense command.");
            throw new IllegalArgumentException("Missing expense index. Usage: delete-expense <index>");
        }

        try {
            int id = Integer.parseInt(args);
            if (id <= 0) {
                LOGGER.log(Level.WARNING, "Non-positive index for delete-expense command: {0}", id);
                throw new IllegalArgumentException("Expense index must be a positive number.");
            }
            LOGGER.log(Level.INFO, "Successfully parsed delete-expense command: {0}", id);
            return id;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid index for delete-expense command: {0}", args);
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
    public static int parseDeleteIncome(String input) throws IllegalArgumentException {
        assert input != null : "Input for parsing delete-income cannot be null.";

        String args = input.substring(Ui.DELETE_INCOME_COMMAND.length()).trim();
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING,"Missing index for delete-income command.");
            throw new IllegalArgumentException("Missing income index. Usage: delete-income <index>");
        }

        try {
            int id = Integer.parseInt(args);
            if (id <= 0) {
                LOGGER.log(Level.WARNING, "Non-positive index for delete-income command: {0}", id);
                throw new IllegalArgumentException("Income index must be a positive number.");
            }
            return id;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid index for delete-income command: {0}", args);
            throw new IllegalArgumentException("Income index must be a valid number.");
        }
    }
}
