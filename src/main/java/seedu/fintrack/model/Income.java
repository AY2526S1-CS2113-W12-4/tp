package seedu.fintrack.model;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Income {
    private static final Logger LOGGER = Logger.getLogger(Income.class.getName());

    private final double amount;
    private final IncomeCategory category;
    private final LocalDate date;
    private final String description; // optional

    /**
     * Creates an income with the given amount, category, date and description.
     *
     * @param amount Income amount.
     * @param category Income category.
     * @param date Income date.
     * @param description Income description.
     */

    public Income(double amount, IncomeCategory category, LocalDate date, String description) {
        assert amount > 0 : "Amount must be positive.";
        assert category != null : "Category cannot be null.";
        assert date != null : "Date cannot be null.";

        if (amount <= 0) {
            LOGGER.log(Level.WARNING, "Attempted to create Income with negative/zero amount: {0}", amount);
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (category == null) {
            LOGGER.log(Level.WARNING, "Attempted to create Income with null category.");
            throw new IllegalArgumentException("Category cannot be null.");
        }
        if (date == null) {
            LOGGER.log(Level.WARNING, "Attempted to create Income with null date.");
            throw new IllegalArgumentException("Date cannot be null.");
        }

        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = (description == null || description.isBlank()) ? null : description;
    }

    /**
     * Returns the amount of the income.
     *
     * @return the amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the category of the income.
     *
     * @return the amount.
     */
    public IncomeCategory getCategory() {
        return category;
    }

    /**
     * Returns the date of the income in YYYY-MM-DD format.
     *
     * @return the date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the description of the income.
     *
     * @return the expense.
     */
    public String getDescription() {
        return description;
    }
}
