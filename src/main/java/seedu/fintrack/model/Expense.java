package seedu.fintrack.model;

public class Expense {
    private final double amount;

    public Expense(double amount) {
        this.amount = amount;
    }

    public double getAmount() { return amount; }
}
