package seedu.fintrack;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    static {
        // Suppresses INFO and FINER log messages
        LOGGER.setLevel(Level.WARNING);
    }

    private Parser() {}

    /**
     * Extracts the first word from the given input string, which is typically the command word.
     * Handles leading spaces and tab characters by trimming them first,
     * ensuring that the command is correctly recognized even if there
     * are extra spaces or tabs before it. Also supports command aliases for faster typing.
     *
     * @param input The full user input string. Must not be null.
     * @return The first word of the input string (the command), expanded from alias if applicable.
     */
    public static String returnFirstWord(String input) {
        assert input != null : "Input cannot be null.";

        // Normalize tabs and multiple spaces to single spaces
        String normalized = input.stripLeading().replaceAll("\\s+", " ");
        int firstSpaceIndex = normalized.indexOf(' ');

        String commandWord;
        if (firstSpaceIndex == -1) {
            commandWord = normalized; // Only one token (command only)
        } else {
            commandWord = normalized.substring(0, firstSpaceIndex);
        }

        // Map aliases to full command names for consistency
        return expandCommandAlias(commandWord);
    }

    /**
     * Expands command aliases in the input string to their full command names.
     * This ensures that parsing methods receive the full command name.
     *
     * @param input The user input string that may contain aliases
     * @return Input string with aliases expanded to full command names
     */
    public static String expandAliasesInInput(String input) {
        assert input != null : "Input cannot be null.";

        // Normalize tabs and multiple spaces to single spaces
        String normalized = input.stripLeading().replaceAll("\\s+", " ");
        int firstSpaceIndex = normalized.indexOf(' ');

        String commandWord;
        String restOfInput;
        if (firstSpaceIndex == -1) {
            commandWord = normalized; // Only one token (command only)
            restOfInput = "";
        } else {
            commandWord = normalized.substring(0, firstSpaceIndex);
            restOfInput = normalized.substring(firstSpaceIndex);
        }

        // Map aliases to full command names
        String expandedCommand = expandCommandAlias(commandWord);

        // Return the full input with expanded command
        return expandedCommand + restOfInput;
    }

    /**
     * Expands command aliases to their full command names.
     * Supports lightning-fast keyboard-first workflow with minimal typing.
     *
     * @param command The command or alias entered by user
     * @return Full command name for processing
     */
    private static String expandCommandAlias(String command) {
        return switch (command) {
        // Core data entry commands (highest frequency)
        case "ae" -> "add-expense";
        case "ai" -> "add-income";
        case "le" -> "list-expense";
        case "li" -> "list-income";

        // Modification commands
        case "me" -> "modify-expense";
        case "mi" -> "modify-income";

        // Deletion commands
        case "de" -> "delete-expense";
        case "di" -> "delete-income";

        // Other high-frequency commands
        case "bg" -> "budget";
        case "ex" -> "export";
        case "b" -> "balance";

        // Commands that are already short enough
        default -> command;
        };
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

        // Find the next prefix or end-of-string. Description (des/) always consumes the rest.
        int next = Ui.DESCRIPTION_PREFIX.equals(prefix)
                ? -1
                : findNextPrefixIndex(args, start);
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
            int searchIndex = fromIndex;
            while (searchIndex >= 0) {
                int idx = args.indexOf(p, searchIndex);
                if (idx < 0) {
                    break;
                }
                if (isPrefixStart(args, idx)) {
                    if (next < 0 || idx < next) {
                        next = idx;
                    }
                    break;
                }
                searchIndex = idx + 1;
            }
        }
        return next;
    }

    /**
     * Locates the first usable occurrence of a prefix in the argument string.
     *
     * <p>The search ignores prefix lookalikes that are embedded in descriptions by
     * requiring the match to be at the string start or immediately preceded by whitespace.</p>
     *
     * @param args argument segment to scan; must not be {@code null}
     * @param prefix prefix token to look for (e.g. {@code c/}); must not be {@code null}
     * @return zero-based index of the first real prefix, or {@code -1} when absent
     */
    private static int findFirstPrefixIndex(String args, String prefix) {
        int searchIndex = 0;
        while (searchIndex >= 0 && searchIndex < args.length()) {
            int idx = args.indexOf(prefix, searchIndex);
            if (idx < 0) {
                return -1;
            }
            if (isPrefixStart(args, idx)) {
                return idx;
            }
            searchIndex = idx + 1;
        }
        return -1;
    }

    /**
     * Verifies that {@code des/} appears after every compulsory prefix.
     *
     * <p>If a description token is followed by any other recognised prefix, an
     * {@link IllegalArgumentException} is thrown so callers can surface a stable error.</p>
     *
     * @param args raw argument string following the command word
     * @throws IllegalArgumentException when {@code des/} is not the final prefix
     */
    private static void ensureDescriptionLast(String args) {
        int descriptionIndex = findFirstPrefixIndex(args, Ui.DESCRIPTION_PREFIX);
        if (descriptionIndex < 0) {
            return;
        }
        String[] otherPrefixes = {
            Ui.AMOUNT_PREFIX,
            Ui.CATEGORY_PREFIX,
            Ui.DATE_PREFIX
        };
        for (String prefix : otherPrefixes) {
            int idx = findFirstPrefixIndex(args, prefix);
            if (idx > descriptionIndex) {
                throw new IllegalArgumentException("Description (des/) must be the last parameter.");
            }
        }
    }

    /**
     * Validates that the argument string:
     *  - contains exactly the required prefixes (each exactly once),
     *  - may contain optional prefixes at most once each,
     *  - contains no unknown prefixes, and
     *  - has no stray text before the first recognised prefix.
     *
     * A "prefix" is any token of the form {@code <letters>/} that appears at the
     * start of the argument string or immediately after whitespace.
     */
    private static void validatePrefixesExactly(
            String args, String[] required, String[] optional, String usageForError) {

        Objects.requireNonNull(args, "args");
        Objects.requireNonNull(required, "required");
        Objects.requireNonNull(optional, "optional");

        final Set<String> requiredSet = new HashSet<>(Arrays.asList(required));
        final Set<String> optionalSet = new HashSet<>(Arrays.asList(optional));
        final Set<String> allowedSet  = new HashSet<>();
        allowedSet.addAll(requiredSet);
        allowedSet.addAll(optionalSet);

        // Find where description starts (if present) - don't validate within description
        int descIndex = findFirstPrefixIndex(args, Ui.DESCRIPTION_PREFIX);
        String argsToValidate = (descIndex >= 0) ? args.substring(0, descIndex) : args;

        // Disallow stray text before the first recognised prefix
        int firstIdx = Integer.MAX_VALUE;
        for (String p : allowedSet) {
            int idx = findFirstPrefixIndex(argsToValidate, p);
            if (idx >= 0 && idx < firstIdx) {
                firstIdx = idx;
            }
        }
        if (firstIdx != Integer.MAX_VALUE) {
            String before = argsToValidate.substring(0, firstIdx);
            if (!before.trim().isEmpty()) {
                throw new IllegalArgumentException("Unexpected text before arguments: '"
                        + before.trim() + "'. " + usageForError);
            }
        } else {
            return;
        }

        // Reject unknown prefixes and duplicates (only in non-description part)
        Pattern prefixPattern = Pattern.compile("(?<=^|\\s)([A-Za-z]+)/");
        Matcher m = prefixPattern.matcher(argsToValidate);
        Set<String> seen = new HashSet<>();
        while (m.find()) {
            String token = m.group(1) + "/";
            if (!allowedSet.contains(token)) {
                throw new IllegalArgumentException("Unexpected argument prefix: " + token + ". " + usageForError);
            }
            if (!seen.add(token)) {
                throw new IllegalArgumentException("Duplicate argument: " + token
                        + ". Each prefix must appear at most once.");
            }
        }

        // Ensure all required prefixes are present
        for (String req : required) {
            String v = getValue(args, req);
            if (v == null) {
                throw new IllegalArgumentException("Missing required parameter: " + req + ". " + usageForError);
            }
        }

        // Reject stray text after all valid arguments (no extra text allowed)
        String remainingArgs = args.trim();
        for (String prefix : requiredSet) {
            remainingArgs = removePrefixAndValue(remainingArgs, prefix);
        }

        for (String prefix : optionalSet) {
            remainingArgs = removePrefixAndValue(remainingArgs, prefix);
        }

        remainingArgs = remainingArgs.trim();

        // If there is any remaining non-empty text (after removing required/optional prefixes), it's invalid
        if (!remainingArgs.isEmpty()) {
            throw new IllegalArgumentException("Unexpected text after valid arguments: '"
                    + remainingArgs + "'. " + usageForError);
        }
    }

    /**
     * Helper method to remove the prefix and its value from the argument string.
     */
    private static String removePrefixAndValue(String args, String prefix) {
        // Special case for description, which consumes the rest of the string
        if (Ui.DESCRIPTION_PREFIX.equals(prefix)) {
            int prefixIndex = findFirstPrefixIndex(args, prefix);
            if (prefixIndex != -1) {
                // Return everything before the description prefix
                return args.substring(0, prefixIndex).trim();
            }
            return args; // Prefix not found
        }

        // Logic for single-token prefixes (a/, c/, d/)
        // We use replaceFirst to remove only the first valid occurrence
        Pattern pattern = Pattern.compile("(?<=^|\\s)" + Pattern.quote(prefix) + "[^\\s]*");
        return pattern.matcher(args).replaceFirst("").trim();
    }

    private static boolean isPrefixStart(String args, int index) {
        if (index < 0 || index >= args.length()) {
            return false;
        }
        if (index == 0) {
            return true;
        }
        return Character.isWhitespace(args.charAt(index - 1));
    }

    /**
     * Returns the substring that follows the command literal, trimming any leading whitespace.
     *
     * <p>The command word itself must match exactly; everything after the first block of
     * whitespace is preserved verbatim so downstream parsers retain the user’s spacing.</p>
     *
     * @param input full user input; must not be {@code null}
     * @param commandLiteral expected command word (e.g. {@code list-expense}); must not be {@code null}
     * @return argument portion of the input (possibly empty) with leading whitespace removed
     * @throws IllegalArgumentException if the input does not begin with the command literal
     */
    private static String extractArgumentsAfterCommand(String input, String commandLiteral) {
        Objects.requireNonNull(input, "input cannot be null");
        Objects.requireNonNull(commandLiteral, "commandLiteral cannot be null");

        if (!input.startsWith(commandLiteral)) {
            throw new IllegalArgumentException("Invalid command. See 'help'.");
        }

        int idx = commandLiteral.length();
        while (idx < input.length() && Character.isWhitespace(input.charAt(idx))) {
            idx++;
        }
        return idx >= input.length() ? "" : input.substring(idx);
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

        validatePrefixesExactly(
                args,
                new String[]{Ui.CATEGORY_PREFIX, Ui.AMOUNT_PREFIX},
                new String[]{},
                "Usage: budget c/<category> a/<amount>"
        );


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

        if (!Double.isFinite(amount)) {
            LOGGER.log(Level.WARNING, "Non-finite amount provided for budget command: {0}.", amount);
            throw new IllegalArgumentException("Amount must be finite.");
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
     * {@code add-expense a/<amount> c/<category> d/<YYYY-MM-DD> [des/<text>]}
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

        ensureDescriptionLast(args);

        validatePrefixesExactly(
                args,
                new String[]{Ui.AMOUNT_PREFIX, Ui.CATEGORY_PREFIX, Ui.DATE_PREFIX},
                new String[]{Ui.DESCRIPTION_PREFIX},
                "Usage: add-expense a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]"
        );

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
        if (!Double.isFinite(amount)) {
            LOGGER.log(Level.WARNING, "Non-finite amount provided: {0}.", amount);
            throw new IllegalArgumentException("Amount must be finite.");
        }
        if (amount <= 0) {
            LOGGER.log(Level.WARNING, "Negative/Zero amount provided: {0}.", amount);
            throw new IllegalArgumentException("Amount must be more than 0.");
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
     * {@code add-income a/<amount> c/<category> d/<YYYY-MM-DD> [des/<text>]}
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

        ensureDescriptionLast(args);

        validatePrefixesExactly(
                args,
                new String[]{Ui.AMOUNT_PREFIX, Ui.CATEGORY_PREFIX, Ui.DATE_PREFIX},
                new String[]{Ui.DESCRIPTION_PREFIX},
                "Usage: add-income a/<amount> c/<category> d/<YYYY-MM-DD> [des/<description>]"
        );


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
        if (!Double.isFinite(amount)) {
            LOGGER.log(Level.WARNING, "Non-finite amount provided: {0}.", amount);
            throw new IllegalArgumentException("Amount must be finite.");
        }
        if (amount <= 0) {
            LOGGER.log(Level.WARNING, "Negative/Zero amount provided: {0}.", amount);
            throw new IllegalArgumentException("Amount must be more than 0.");
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
     *     not a number, not a positive integer, or exceeds the computer's integer range.
     */
    public static int parseDeleteExpense(String input) throws IllegalArgumentException {
        assert input != null : "Input for parsing delete-expense cannot be null.";
        LOGGER.log(Level.INFO, "Parsing expense input: {0}", input);

        String args = input.substring(Ui.DELETE_EXPENSE_COMMAND.length()).trim();
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING,"Missing index for delete-expense command.");
            throw new IllegalArgumentException("Missing expense index. Usage: delete-expense <index>");
        }

        // Check if the number is clearly beyond Java's long range by string length
        if (isBeyondLongRange(args)) {
            boolean isNegative = args.startsWith("-");
            if (isNegative) {
                throw new IllegalArgumentException("Expense index is too small. " +
                        "Please use a larger number (minimum: 1).");
            } else {
                throw new IllegalArgumentException("Expense index is too large. " +
                        "Please use a smaller number (maximum: " + Integer.MAX_VALUE + ").");
            }
        }

        // Check if the number is within long range but outside int range
        if (isOutsideIntRange(args)) {
            long testValue = Long.parseLong(args);
            if (testValue > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Expense index is too large. " +
                        "Please use a smaller number (maximum: " + Integer.MAX_VALUE + ").");
            } else {
                throw new IllegalArgumentException("Expense index is too small. " +
                        "Please use a larger number (minimum: 1).");
            }
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
     *     not a number, not a positive integer, or exceeds the computer's integer range.
     */
    public static int parseDeleteIncome(String input) throws IllegalArgumentException {
        assert input != null : "Input for parsing delete-income cannot be null.";

        String args = input.substring(Ui.DELETE_INCOME_COMMAND.length()).trim();
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING,"Missing index for delete-income command.");
            throw new IllegalArgumentException("Missing income index. Usage: delete-income <index>");
        }

        // Check if the number is clearly beyond Java's long range by string length
        if (isBeyondLongRange(args)) {
            boolean isNegative = args.startsWith("-");
            if (isNegative) {
                throw new IllegalArgumentException("Income index is too small. " +
                        "Please use a larger number (minimum: 1).");
            } else {
                throw new IllegalArgumentException("Income index is too large. " +
                        "Please use a smaller number (maximum: " + Integer.MAX_VALUE + ").");
            }
        }

        // Check if the number is within long range but outside int range
        if (isOutsideIntRange(args)) {
            long testValue = Long.parseLong(args);
            if (testValue > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Income index is too large. " +
                        "Please use a smaller number (maximum: " + Integer.MAX_VALUE + ").");
            } else {
                throw new IllegalArgumentException("Income index is too small. " +
                        "Please use a larger number (minimum: 1).");
            }
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

        LOGGER.log(Level.INFO, "Parsing optional month after command: {0}", commandLiteral);

        final String rest = extractArgumentsAfterCommand(input, commandLiteral);

        if (rest.isEmpty()) {
            LOGGER.log(Level.FINE, "No month argument detected for {0}", commandLiteral);
            return Optional.empty();
        }

        if (!rest.startsWith(Ui.DATE_PREFIX)) {
            LOGGER.log(Level.WARNING, "Missing d/ prefix for month after {0}: {1}",
                    new Object[]{commandLiteral, rest});
            throw new IllegalArgumentException("Usage: " + commandLiteral + " [" + Ui.DATE_PREFIX + "YYYY-MM]");
        }

        String monthToken = rest.substring(Ui.DATE_PREFIX.length()).trim();
        if (monthToken.isEmpty()) {
            LOGGER.log(Level.WARNING, "Month missing after d/ for command {0}", commandLiteral);
            throw new IllegalArgumentException("Month must follow d/ in YYYY-MM format.");
        }
        if (monthToken.contains(" ")) {
            LOGGER.log(Level.WARNING, "Unexpected extra arguments after {0}: {1}",
                    new Object[]{commandLiteral, rest});
            throw new IllegalArgumentException("Usage: " + commandLiteral + " [" + Ui.DATE_PREFIX + "YYYY-MM]");
        }

        try {
            YearMonth parsed = YearMonth.parse(monthToken, YEAR_MONTH_FORMATTER);
            LOGGER.log(Level.FINE, "Parsed YearMonth {0} for command {1}",
                    new Object[]{parsed, commandLiteral});
            return Optional.of(parsed);
        } catch (DateTimeParseException ex) {
            LOGGER.log(Level.WARNING, "Invalid month format after {0}: {1}",
                    new Object[]{commandLiteral, monthToken});
            throw new IllegalArgumentException("Month must be in YYYY-MM format.");
        }
    }

    public static Optional<YearMonth> parseOptionalMonthForBalance(String input) {
        LOGGER.fine("Entered parseOptionalMonthForBalance");
        return parseOptionalYearMonthAfterCommand(input, Ui.BALANCE_COMMAND);
    }

    public static Optional<YearMonth> parseOptionalMonthForExpenseList(String input) {
        LOGGER.fine("Entered parseOptionalMonthForExpenseList");
        return parseOptionalYearMonthAfterCommand(input, Ui.LIST_EXPENSE_COMMAND);
    }

    public static Optional<YearMonth> parseOptionalMonthForIncomeList(String input) {
        LOGGER.fine("Entered parseOptionalMonthForIncomeList");
        return parseOptionalYearMonthAfterCommand(input, Ui.LIST_INCOME_COMMAND);
    }

    /**
     * Parses a modify-expense command and extracts the index only.
     * Expected format: {@code modify-expense <index> [a/<amount>] [c/<category>] [d/<YYYY-MM-DD>] [des/<text>]}
     *
     * @param input The full command string. Must not be null.
     * @return The 1-based index of the expense to modify.
     * @throws IllegalArgumentException If the index is missing or invalid.
     */
    public static int parseModifyExpenseIndex(String input) {
        assert input != null : "Input for parsing modify-expense cannot be null.";
        LOGGER.log(Level.INFO, "Parsing modify-expense index from input: ''{0}''.", input);

        String args = extractArgumentsAfterCommand(input, Ui.MODIFY_EXPENSE_COMMAND);
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing index for modify-expense command.");
            throw new IllegalArgumentException(
                    "Missing index. Usage: modify-expense <index> [a/<amount>] [c/<category>] [d/<YYYY-MM-DD>]"
            );
        }

        // Extract the index (first token)
        int split = -1;
        for (int i = 0; i < args.length(); i++) {
            if (Character.isWhitespace(args.charAt(i))) {
                split = i;
                break;
            }
        }

        String indexStr = (split == -1) ? args : args.substring(0, split);

        // Check if the number is clearly beyond Java's long range by string length
        if (isBeyondLongRange(indexStr)) {
            boolean isNegative = indexStr.startsWith("-");
            if (isNegative) {
                throw new IllegalArgumentException("Expense index is too small. " +
                        "Please use a larger number (minimum: 1).");
            } else {
                throw new IllegalArgumentException("Expense index is too large. " +
                        "Please use a smaller number (maximum: " + Integer.MAX_VALUE + ").");
            }
        }

        // Check if the number is within long range but outside int range
        if (isOutsideIntRange(indexStr)) {
            long testValue = Long.parseLong(indexStr);
            if (testValue > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Expense index is too large. " +
                        "Please use a smaller number (maximum: " + Integer.MAX_VALUE + ").");
            } else {
                throw new IllegalArgumentException("Expense index is too small. " +
                        "Please use a larger number (minimum: 1).");
            }
        }

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

        return index;
    }

    /**
     * Parses a modify-expense command with optional fields, merging with old expense values.
     * Expected format: {@code modify-expense <index> [a/<amount>] [c/<category>] [d/<YYYY-MM-DD>] [des/<text>]}
     *
     * @param input The full command string. Must not be null.
     * @param oldExpense The existing expense to use as defaults for unspecified fields.
     * @return A new Expense object with updated fields merged with old values.
     * @throws IllegalArgumentException If any provided parameters are invalid.
     */
    public static Expense parseModifyExpenseWithDefaults(String input, Expense oldExpense) {
        assert input != null : "Input for parsing modify-expense cannot be null.";
        assert oldExpense != null : "Old expense cannot be null.";
        LOGGER.log(Level.INFO, "Parsing modify-expense with defaults: ''{0}''.", input);

        String args = extractArgumentsAfterCommand(input, Ui.MODIFY_EXPENSE_COMMAND);

        // Skip the index to get to the field arguments
        int split = -1;
        for (int i = 0; i < args.length(); i++) {
            if (Character.isWhitespace(args.charAt(i))) {
                split = i;
                break;
            }
        }

        String remainingArgs = (split == -1) ? "" : args.substring(split).stripLeading();

        // Parse optional fields
        String amountStr = getValue(remainingArgs, Ui.AMOUNT_PREFIX);
        String categoryString = getValue(remainingArgs, Ui.CATEGORY_PREFIX);
        String dateStr = getValue(remainingArgs, Ui.DATE_PREFIX);
        String description = getValue(remainingArgs, Ui.DESCRIPTION_PREFIX);

        // Use old values as defaults
        double amount = oldExpense.getAmount();
        ExpenseCategory category = oldExpense.getCategory();
        LocalDate date = oldExpense.getDate();
        String finalDescription = oldExpense.getDescription();

        // Override with new values if provided
        if (amountStr != null) {
            try {
                amount = Double.parseDouble(amountStr);
                if (!Double.isFinite(amount)) {
                    LOGGER.log(Level.WARNING, "Non-finite amount provided: {0}.", amount);
                    throw new IllegalArgumentException("Amount must be finite.");
                }
                if (amount <= 0) {
                    LOGGER.log(Level.WARNING, "Negative/Zero amount provided: {0}.", amount);
                    throw new IllegalArgumentException("Amount must be more than 0.");
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid amount format: {0}.", amountStr);
                throw new IllegalArgumentException("Amount must be a valid number.");
            }
        }

        if (categoryString != null) {
            category = ExpenseCategory.parse(categoryString);
        }

        if (dateStr != null) {
            try {
                date = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Invalid date format: {0}.", dateStr);
                throw new IllegalArgumentException("Date must be in YYYY-MM-DD format.");
            }
        }

        if (description != null) {
            finalDescription = description.isBlank() ? null : description;
        }

        Expense newExpense = new Expense(amount, category, date, finalDescription);
        LOGGER.log(Level.INFO, "Successfully parsed modified expense with merged values.");
        return newExpense;
    }

    /**
     * Parses a modify-income command and extracts the index only.
     * Expected format: {@code modify-income <index> [a/<amount>] [c/<category>] [d/<YYYY-MM-DD>] [des/<text>]}
     *
     * @param input The full command string. Must not be null.
     * @return The 1-based index of the income to modify.
     * @throws IllegalArgumentException If the index is missing or invalid.
     */
    public static int parseModifyIncomeIndex(String input) {
        assert input != null : "Input for parsing modify-income cannot be null.";
        LOGGER.log(Level.INFO, "Parsing modify-income index from input: ''{0}''.", input);

        String args = extractArgumentsAfterCommand(input, Ui.MODIFY_INCOME_COMMAND);
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing index for modify-income command.");
            throw new IllegalArgumentException(
                    "Missing index. Usage: modify-income <index> [a/<amount>] [c/<category>] [d/<YYYY-MM-DD>]"
            );
        }

        // Extract the index (first token)
        int split = -1;
        for (int i = 0; i < args.length(); i++) {
            if (Character.isWhitespace(args.charAt(i))) {
                split = i;
                break;
            }
        }

        String indexStr = (split == -1) ? args : args.substring(0, split);

        // Check if the number is clearly beyond Java's long range by string length
        if (isBeyondLongRange(indexStr)) {
            boolean isNegative = indexStr.startsWith("-");
            if (isNegative) {
                throw new IllegalArgumentException("Income index is too small. " +
                        "Please use a larger number (minimum: 1).");
            } else {
                throw new IllegalArgumentException("Income index is too large. " +
                        "Please use a smaller number (maximum: " + Integer.MAX_VALUE + ").");
            }
        }

        // Check if the number is within long range but outside int range
        if (isOutsideIntRange(indexStr)) {
            long testValue = Long.parseLong(indexStr);
            if (testValue > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Income index is too large. " +
                        "Please use a smaller number (maximum: " + Integer.MAX_VALUE + ").");
            } else {
                throw new IllegalArgumentException("Income index is too small. " +
                        "Please use a larger number (minimum: 1).");
            }
        }

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

        return index;
    }

    /**
     * Parses a modify-income command with optional fields, merging with old income values.
     * Expected format: {@code modify-income <index> [a/<amount>] [c/<category>] [d/<YYYY-MM-DD>] [des/<text>]}
     *
     * @param input The full command string. Must not be null.
     * @param oldIncome The existing income to use as defaults for unspecified fields.
     * @return A new Income object with updated fields merged with old values.
     * @throws IllegalArgumentException If any provided parameters are invalid.
     */
    public static Income parseModifyIncomeWithDefaults(String input, Income oldIncome) {
        assert input != null : "Input for parsing modify-income cannot be null.";
        assert oldIncome != null : "Old income cannot be null.";
        LOGGER.log(Level.INFO, "Parsing modify-income with defaults: ''{0}''.", input);

        String args = extractArgumentsAfterCommand(input, Ui.MODIFY_INCOME_COMMAND);

        // Skip the index to get to the field arguments
        int split = -1;
        for (int i = 0; i < args.length(); i++) {
            if (Character.isWhitespace(args.charAt(i))) {
                split = i;
                break;
            }
        }

        String remainingArgs = (split == -1) ? "" : args.substring(split).stripLeading();

        // Parse optional fields
        String amountStr = getValue(remainingArgs, Ui.AMOUNT_PREFIX);
        String categoryString = getValue(remainingArgs, Ui.CATEGORY_PREFIX);
        String dateStr = getValue(remainingArgs, Ui.DATE_PREFIX);
        String description = getValue(remainingArgs, Ui.DESCRIPTION_PREFIX);

        // Use old values as defaults
        double amount = oldIncome.getAmount();
        IncomeCategory category = oldIncome.getCategory();
        LocalDate date = oldIncome.getDate();
        String finalDescription = oldIncome.getDescription();

        // Override with new values if provided
        if (amountStr != null) {
            try {
                amount = Double.parseDouble(amountStr);
                if (!Double.isFinite(amount)) {
                    LOGGER.log(Level.WARNING, "Non-finite amount provided: {0}.", amount);
                    throw new IllegalArgumentException("Amount must be finite.");
                }
                if (amount <= 0) {
                    LOGGER.log(Level.WARNING, "Negative/Zero amount provided: {0}.", amount);
                    throw new IllegalArgumentException("Amount must be more than 0.");
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid amount format: {0}.", amountStr);
                throw new IllegalArgumentException("Amount must be a valid number.");
            }
        }

        if (categoryString != null) {
            category = IncomeCategory.parse(categoryString);
        }

        if (dateStr != null) {
            try {
                date = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Invalid date format: {0}.", dateStr);
                throw new IllegalArgumentException("Date must be in YYYY-MM-DD format.");
            }
        }

        if (description != null) {
            finalDescription = description.isBlank() ? null : description;
        }

        Income newIncome = new Income(amount, category, date, finalDescription);
        LOGGER.log(Level.INFO, "Successfully parsed modified income with merged values.");
        return newIncome;
    }

    /**
     * Parses the export command input to extract the filename.
     * Expected format: export {@code <filename>}
     *
     * @param input The full export command input
     * @return The Path object representing the export file in current directory
     * @throws IllegalArgumentException if the command format is invalid or filename is invalid
     */
    public static Path parseExport(String input) {
        assert input != null : "Input cannot be null.";
        LOGGER.log(Level.FINER, "Parsing export command: ''{0}''.", input);

        String args = input.substring(Ui.EXPORT_COMMAND.length()).trim();
        if (args.isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing filename for export command.");
            throw new IllegalArgumentException("Missing filename. Usage: export <filename>");
        }

        // Validate that it's just a filename (no directory separators)
        if (args.contains(java.io.File.separator) || args.contains("/") || args.contains("\\")) {
            LOGGER.log(Level.WARNING, "Path contains directory separators: {0}", args);
            throw new IllegalArgumentException(
                    "Invalid filename. Please provide only a filename (no paths). Usage: export <filename>"
            );
        }

        // Validate filename format
        if (!isValidFilename(args)) {
            LOGGER.log(Level.WARNING, "Invalid filename format: {0}", args);
            throw new IllegalArgumentException(
                    "Invalid filename. Please use only letters, numbers, hyphens, underscores, and dots."
            );
        }

        try {
            String filename = args;
            if (!filename.toLowerCase().endsWith(".csv")) {
                filename = filename + ".csv";
            }

            // Create path in current working directory
            Path path = Paths.get(filename);
            return path.toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            LOGGER.log(Level.WARNING, "Invalid filename provided: {0}", args);
            throw new IllegalArgumentException("Invalid filename. Please provide a valid filename for the CSV file.");
        }
    }

    /**
     * Validates that a filename contains only safe characters.
     * Allows letters, numbers, hyphens, underscores, and dots.
     *
     * @param filename The filename to validate
     * @return true if the filename is valid, false otherwise
     */
    private static boolean isValidFilename(String filename) {
        // Allow letters, numbers, hyphens, underscores, and dots
        // Disallow directory separators, control characters, and other special characters
        return filename.matches("^[a-zA-Z0-9._-]+$");
    }

    /**
     * Checks if a string represents a number that is clearly beyond Java's long range
     * by examining its string length and content.
     *
     * @param numberStr The string to check
     * @return true if the number is beyond Java's long range, false otherwise
     */
    private static boolean isBeyondLongRange(String numberStr) {
        // Check if the number is negative
        boolean isNegative = numberStr.startsWith("-");
        String absNumberStr = isNegative ? numberStr.substring(1) : numberStr;

        // Check if the number has more digits than the maximum possible Java long
        // Long.MAX_VALUE = 9223372036854775807 (19 digits)
        // Long.MIN_VALUE = -9223372036854775808 (19 digits + minus sign)
        // Any number with more than 19 digits is definitely beyond Java's long range
        if (absNumberStr.length() > 19) {
            return true;
        }

        // For numbers with exactly 19 digits, check if they exceed long range
        if (absNumberStr.length() == 19) {
            if (isNegative) {
                // For negative numbers, check if they exceed Long.MIN_VALUE
                // Long.MIN_VALUE = -9223372036854775808
                return absNumberStr.compareTo("9223372036854775808") > 0;
            } else {
                // For positive numbers, check if they exceed Long.MAX_VALUE
                return absNumberStr.compareTo("9223372036854775807") > 0;
            }
        }

        return false;
    }

    /**
     * Checks if a string represents a number that is within long range but outside int range.
     *
     * @param numberStr The string to check
     * @return true if the number is outside int range but within long range, false otherwise
     */
    private static boolean isOutsideIntRange(String numberStr) {
        try {
            long testValue = Long.parseLong(numberStr);
            return testValue > Integer.MAX_VALUE || testValue < Integer.MIN_VALUE;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
