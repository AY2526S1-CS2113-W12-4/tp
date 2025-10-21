package seedu.fintrack;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Optional;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.IncomeCategory;

/**
 * A utility class for parsing user input commands into structured data.
 * It handles the parsing of commands such as adding or deleting incomes and expenses.
 */
final class Parser {
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());
    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    static {
        // Suppresses INFO and FINER log messages
        LOGGER.setLevel(Level.WARNING);
    }

    private Parser() {}

    /**
     * Extracts the first word from the given input string, which is typically the command word.
     * It handles leading spaces by trimming the input first.
     *
     * @param input The full user input string. Must not be null.
     * @return The first word of the input string.
     */
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

    /**
     * Finds the index of the first space character in a string.
     *
     * @param input The string to search within. Must not be null.
     * @return The index of the first space, or -1 if no space is found.
     */
    public static int getFirstSpaceIndex(String input) {
        assert input != null : "Input cannot be null.";

        return input.indexOf(' ');
    }

    /**
     * Extracts a value from an argument string based on a given prefix.
     * The value is the substring that starts immediately after the prefix and ends
     * just before the next known prefix or at the end of the string.
     *
     * @param args The argument string to parse. Must not be null.
     * @param prefix The prefix to search for (e.g., "a/"). Must not be null.
     * @return The trimmed value associated with the prefix, or {@code null} if the prefix
     *     is not found or has no value.
     */
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

    /**
     * Extracts an optional value from an argument string based on a given prefix.
     * If the prefix or its value is not found, returns an empty string instead of null.
     *
     * @param args The argument string to parse.
     * @param prefix The prefix to search for.
     * @return The trimmed value associated with the prefix, or an empty string if not found.
     */
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

    public static Map.Entry<ExpenseCategory, Double> parseSetBudget(String input) throws IllegalArgumentException {
        assert input != null : "Input for parsing budget cannot be null.";
        LOGGER.log(Level.FINER, "Parsing budget entry: ''{0}''.", input);

        String args = input.substring(Ui.BUDGET_COMMAND.length()).trim();
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing parameters for budget command.");
            throw new IllegalArgumentException("Missing parameters for budget command. " +
                    "Usage: budget c/<category> a/<amount>");
        }

        String categoryStr = getValue(args, Ui.CATEGORY_PREFIX);
        String amountStr = getValue(args, Ui.AMOUNT_PREFIX);

        if (categoryStr == null || amountStr == null) {
            LOGGER.log(Level.WARNING, "Missing one or more required parameters for budget command.");
            throw new IllegalArgumentException("Usage: budget c/<category> a/<amount>");
        }

        ExpenseCategory category = ExpenseCategory.parse(categoryStr);

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid amount for budget command: {0}.", amountStr);
            throw new IllegalArgumentException("Amount must be a valid number.");
        }

        if (amount < 0) {
            LOGGER.log(Level.WARNING, "Negative amount provided for budget command: {0}.", amount);
            throw new IllegalArgumentException("Amount must be non-negative.");
        }

        LOGGER.log(Level.INFO, "Successfully parsed budget command for {0} with amount {1}.",
                new Object[]{category, amount});
        return Map.entry(category, amount);
    }

    /**
     * Parses the user input for adding an expense.
     * Expected format:
     * {@code add-expense a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<text>]}
     *
     * @param input The full user command string. Must not be null.
     * @return A new {@code Expense} object created from the parsed data.
     * @throws IllegalArgumentException If any required parameters are missing or if the
     *     amount or date are in an invalid format.
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
        String categoryString = getValue(args, Ui.CATEGORY_PREFIX);
        String dateStr = getValue(args, Ui.DATE_PREFIX);
        String description = getOptionalValue(args, Ui.DESCRIPTION_PREFIX);

        if (amountStr == null || categoryString == null || dateStr == null) {
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

        ExpenseCategory category = ExpenseCategory.parse(categoryString);
        Expense newExpense = new Expense(amount, category, date, description);
        LOGGER.log(Level.INFO, "Successfully parsed new expense: {0}.", newExpense);
        return newExpense;
    }

    /**
     * Parses the user input for adding an income.
     * Expected format:
     * {@code add-income a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<text>]}
     *
     * @param input The full user command string. Must not be null.
     * @return A new {@code Income} object created from the parsed data.
     * @throws IllegalArgumentException If any required parameters are missing or if the
     *     amount or date are in an invalid format.
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
        String categoryString = getValue(args, Ui.CATEGORY_PREFIX);
        String dateStr = getValue(args, Ui.DATE_PREFIX);
        String description = getOptionalValue(args, Ui.DESCRIPTION_PREFIX);

        if (amountStr == null || categoryString == null || dateStr == null) {
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

        IncomeCategory category = IncomeCategory.parse(categoryString);
        Income newIncome = new Income(amount, category, date, description);
        LOGGER.log(Level.INFO, "Successfully parsed new income: {0}", newIncome);
        return newIncome;
    }

    /**
     * Parses the 'delete-expense' command and returns the index of the expense to delete.
     * Expected format: {@code delete-expense <index>}
     *
     * @param input The full command string from the user. Must not be null.
     * @return The 1-based index of the expense to delete.
     * @throws IllegalArgumentException If the format is invalid, the index is missing,
     *     not a number, or not a positive integer.
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
     * Parses the 'delete-income' command and returns the index of the income to delete.
     * Expected format: {@code delete-income <index>}
     *
     * @param input The full command string from the user. Must not be null.
     * @return The 1-based index of the income to delete.
     * @throws IllegalArgumentException If the format is invalid, the index is missing,
     *     not a number, or not a positive integer.
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

    /**
     * Parses an optional {@code YYYY-MM} after the given command literal.
     * <p>If no argument is present, returns {@link Optional#empty()}.
     * If present but invalid, throws {@link IllegalArgumentException}.</p>
     *
     * <p>Logs at {@code INFO} when parsing begins, {@code FINE} on success,
     * and {@code WARNING} if parsing fails or input is missing.</p>
     *
     * @param input full user input (e.g., "balance 2025-10")
     * @param commandLiteral the exact command prefix (e.g., {@code Ui.BALANCE_COMMAND})
     * @return Optional of parsed {@link YearMonth}, or empty if not provided
     * @throws IllegalArgumentException if a non-empty argument is not in {@code YYYY-MM} format
     */
    private static Optional<YearMonth> parseOptionalYearMonthAfterCommand(
            String input, String commandLiteral) {

        assert input != null : "input cannot be null";
        assert commandLiteral != null : "commandLiteral cannot be null";
        
        // Guard: input must be exactly the command or the command followed by a space
        if (!input.equals(commandLiteral) && !input.startsWith(commandLiteral + " ")) {
            LOGGER.log(Level.WARNING, 
                       "Input does not start with expected command literal: {0}", 
                       commandLiteral);
            throw new IllegalArgumentException("Invalid command. See 'help'.");
        }

        LOGGER.log(Level.INFO, "Parsing optional month after command: {0}", commandLiteral);

        final String rest = input.length() >= commandLiteral.length()
                ? input.substring(commandLiteral.length()).trim()
                : "";

        if (rest.isEmpty()) {
            LOGGER.log(Level.FINE, "No month argument detected for {0}", commandLiteral);
            return Optional.empty();
        }
        
        // Disallow extra tokens after the month (enforce exactly one arg if present)
        int sp = rest.indexOf(' ');
        if (sp != -1) {
            LOGGER.log(Level.WARNING, "Unexpected extra arguments after {0}: {1}",
                    new Object[]{commandLiteral, rest});
            throw new IllegalArgumentException("Usage: " + commandLiteral + " [YYYY-MM]");
        }

        try {
            YearMonth parsed = YearMonth.parse(rest, YM_FMT);
            LOGGER.log(Level.FINE, "Parsed YearMonth {0} for command {1}",
                    new Object[]{parsed, commandLiteral});
            return Optional.of(parsed);
        } catch (DateTimeParseException ex) {
            LOGGER.log(Level.WARNING, "Invalid month format after {0}: {1}",
                    new Object[]{commandLiteral, rest});
            throw new IllegalArgumentException("Month must be in YYYY-MM format.");
        }
    }

    public static Optional<YearMonth> parseOptionalMonthForBalance(String input) {
        LOGGER.fine("Entered parseOptionalMonthForBalance");
        return parseOptionalYearMonthAfterCommand(input, Ui.BALANCE_COMMAND);
    }

    public static Optional<YearMonth> parseOptionalMonthForExpenseList(String input) {
        LOGGER.fine("Entered parseOptionalMonthForExpenseList");
        return parseOptionalYearMonthAfterCommand(input, Ui.LIST_COMMAND);
    }

    public static Optional<YearMonth> parseOptionalMonthForIncomeList(String input) {
        LOGGER.fine("Entered parseOptionalMonthForIncomeList");
        return parseOptionalYearMonthAfterCommand(input, Ui.LIST_INCOME_COMMAND);
    }

    /**
     * Parses a modify-expense command into an index and new expense data.
     * Expected format: {@code modify-expense <index> a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<text>]}
     *
     * @param input The full command string. Must not be null.
     * @return A pair containing the 1-based index and the new expense object.
     * @throws IllegalArgumentException If any required parameters are missing or invalid.
     */
    public static Map.Entry<Integer, Expense> parseModifyExpense(String input) {
        assert input != null : "Input for parsing modify-expense cannot be null.";
        LOGGER.log(Level.INFO, "Parsing modify-expense input: ''{0}''.", input);

        String args = input.substring(Ui.MODIFY_EXPENSE_COMMAND.length()).trim();
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing parameters for modify-expense command.");
            throw new IllegalArgumentException(
                    "Missing parameters. Usage: modify-expense <index> a/<amount> c/<category> d/<YYYY-MM-DD>"
            );
        }

        // Extract the index
        int spaceIndex = args.indexOf(' ');
        if (spaceIndex == -1) {
            LOGGER.log(Level.WARNING, "Missing parameters after index in modify-expense command.");
            throw new IllegalArgumentException(
                    "Missing parameters. Usage: modify-expense <index> a/<amount> c/<category> d/<YYYY-MM-DD>"
            );
        }

        String indexStr = args.substring(0, spaceIndex);
        String remainingArgs = args.substring(spaceIndex + 1);

        int index;
        try {
            index = Integer.parseInt(indexStr);
            if (index <= 0) {
                LOGGER.log(Level.WARNING, "Non-positive index for modify-expense command: {0}", index);
                throw new IllegalArgumentException("Expense index must be a positive number.");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid index for modify-expense command: {0}", indexStr);
            throw new IllegalArgumentException("Expense index must be a valid number.");
        }

        // Parse the new expense data using existing add-expense logic
        Expense newExpense = parseAddExpense(Ui.ADD_EXPENSE_COMMAND + " " + remainingArgs);
        return Map.entry(index, newExpense);
    }

    /**
     * Parses a modify-income command into an index and new income data.
     * Expected format: {@code modify-income <index> a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<text>]}
     *
     * @param input The full command string. Must not be null.
     * @return A pair containing the 1-based index and the new income object.
     * @throws IllegalArgumentException If any required parameters are missing or invalid.
     */
    public static Map.Entry<Integer, Income> parseModifyIncome(String input) {
        assert input != null : "Input for parsing modify-income cannot be null.";
        LOGGER.log(Level.INFO, "Parsing modify-income input: ''{0}''.", input);

        String args = input.substring(Ui.MODIFY_INCOME_COMMAND.length()).trim();
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing parameters for modify-income command.");
            throw new IllegalArgumentException(
                    "Missing parameters. Usage: modify-income <index> a/<amount> c/<category> d/<YYYY-MM-DD>"
            );
        }

        // Extract the index
        int spaceIndex = args.indexOf(' ');
        if (spaceIndex == -1) {
            LOGGER.log(Level.WARNING, "Missing parameters after index in modify-income command.");
            throw new IllegalArgumentException(
                    "Missing parameters. Usage: modify-income <index> a/<amount> c/<category> d/<YYYY-MM-DD>"
            );
        }

        String indexStr = args.substring(0, spaceIndex);
        String remainingArgs = args.substring(spaceIndex + 1);

        int index;
        try {
            index = Integer.parseInt(indexStr);
            if (index <= 0) {
                LOGGER.log(Level.WARNING, "Non-positive index for modify-income command: {0}", index);
                throw new IllegalArgumentException("Income index must be a positive number.");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid index for modify-income command: {0}", indexStr);
            throw new IllegalArgumentException("Income index must be a valid number.");
        }

        // Parse the new income data using existing add-income logic
        Income newIncome = parseAddIncome(Ui.ADD_INCOME_COMMAND + " " + remainingArgs);
        return Map.entry(index, newIncome);
    }
}
