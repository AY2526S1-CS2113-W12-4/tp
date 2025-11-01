package seedu.fintrack.model;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Expense {
    private static final Logger LOGGER = Logger.getLogger(Expense.class.getName());

    private final double amount;
    private final ExpenseCategory category;
    private final LocalDate date;
    private final String description;

    /**
     * Creates an expense with the given amount, category, date and description.
     *
     * @param amount Expense amount.
     * @param category Expense category.
     * @param date Expense date.
     * @param description Expense description.
     */
    public Expense(double amount, ExpenseCategory category, LocalDate date, String description) {
        assert amount > 0 : "Amount must be above 0.";
        assert category != null : "Category cannot be null.";
        assert date != null : "Date cannot be null.";

        if (amount <= 0) {
            LOGGER.log(Level.WARNING, "Attempted to create Expense with negative/zero amount: {0}", amount);
            throw new IllegalArgumentException("Amount must be non-negative.");
        }
        if (category == null) {
            LOGGER.log(Level.WARNING, "Attempted to create Expense with null category.");
            throw new IllegalArgumentException("Category cannot be null.");
        }
        if (date == null) {
            LOGGER.log(Level.WARNING, "Attempted to create Expense with null date.");
            throw new IllegalArgumentException("Date cannot be null.");
        }

        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = (description == null || description.isBlank()) ? null : description;
    }

    /**
     * Returns the amount of the expense.
     *
     * @return the amount.
     */
    //@@author goodguyryan
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the category of the expense.
     *
     * @return the amount.
     */
    public ExpenseCategory getCategory() {
        return category;
    }

    /**
     * Returns the date of the expense in YYYY-MM-DD format.
     *
     * @return the date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the description of the expense.
     *
     * @return the expense.
     */
    public String getDescription() {
        return description;
    }
}
