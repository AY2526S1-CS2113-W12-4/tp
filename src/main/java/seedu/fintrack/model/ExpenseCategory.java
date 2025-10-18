package seedu.fintrack.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum ExpenseCategory {
    FOOD, STUDY, TRANSPORT, BILLS, ENTERTAINMENT, RENT, GROCERIES, OTHERS;

    private static final Logger LOGGER = Logger.getLogger(ExpenseCategory.class.getName());
    public static ExpenseCategory parse(String categoryString) {
        try {
            assert categoryString != null : "String cannot be null.";
            String stringToParse = categoryString.trim().toUpperCase(Locale.ROOT);
            assert !stringToParse.isBlank() : "Cannot parse blank string.";
            return ExpenseCategory.valueOf(stringToParse);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "ExpenseCategory parse called with unknown category");
            throw new IllegalArgumentException("Unknown expense category!\n" +
                    "Available categories: " + Arrays.toString(ExpenseCategory.values()));
        }
    }
}
