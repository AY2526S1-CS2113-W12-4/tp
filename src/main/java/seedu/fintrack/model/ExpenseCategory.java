package seedu.fintrack.model;

import java.util.Locale;

public enum ExpenseCategory {
    FOOD, STUDY, TRANSPORT, BILLS, ENTERTAINMENT, RENT, GROCERIES;

    public static ExpenseCategory parse(String categoryString) {
        try {
            return ExpenseCategory.valueOf(categoryString.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown expense category!");
        }
    }
}
