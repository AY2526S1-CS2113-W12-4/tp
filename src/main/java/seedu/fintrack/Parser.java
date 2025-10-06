package seedu.fintrack;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import seedu.fintrack.model.Income;

final class Parser {
    private Parser() {}

    /**
     * Expected format:
     * {@code add-income a/<amount> c/<category> d/<YYYY-MM-DD> [desc/<text>]}
     */
    static Income parseAddIncome(String input) {
        String cmd = Ui.getAddIncomeCommand();
        if (!input.startsWith(cmd)) {
            throw new IllegalArgumentException("Invalid command. Use 'help' to see usage.");
        }
        String args = input.substring(cmd.length()).trim();
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Missing parameters. See 'help'.");
        }

        String amountStr = getValue(args, Ui.getAmountPrefix());
        String category = getValue(args, Ui.getCategoryPrefix());
        String dateStr = getValue(args, Ui.getDatePrefix());
        String description = getOptionalValue(args, Ui.getDescriptionPrefix());

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
                Ui.getAmountPrefix(), Ui.getCategoryPrefix(), Ui.getDatePrefix(), Ui.getDescriptionPrefix()
        };
        for (String p : prefixes) {
            int idx = args.indexOf(p, fromIndex);
            if (idx >= 0 && (next < 0 || idx < next)) {
                next = idx;
            }
        }
        return next;
    }
}
