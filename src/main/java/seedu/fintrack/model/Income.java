package seedu.fintrack.model;

import java.time.LocalDate;

public class Income {
    private final double amount;
    private final String category;
    private final LocalDate date;
    private final String description; // optional

    public Income(double amount, String category, LocalDate date, String description) {
        assert amount >= 0 : "Amount must be non-negative.";
        assert category != null : "Category cannot be null.";
        assert date != null : "Date cannot be null.";
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
