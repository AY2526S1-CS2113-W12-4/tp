package seedu.fintrack.model;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Income {
    private static final Logger LOGGER = Logger.getLogger(Income.class.getName());

    private final double amount;
    private final String category;
    private final LocalDate date;
    private final String description; // optional

    public Income(double amount, String category, LocalDate date, String description) {
        assert amount >= 0 : "Amount must be non-negative.";
        assert category != null : "Category cannot be null.";
        assert date != null : "Date cannot be null.";

        if (amount < 0) {
            LOGGER.log(Level.WARNING, "Attempted to create Income with negative amount: {0}", amount);
            throw new IllegalArgumentException("Amount must be non-negative.");
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

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
