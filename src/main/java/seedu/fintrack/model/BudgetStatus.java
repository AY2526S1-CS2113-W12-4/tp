package seedu.fintrack.model;

/**
 * Represents the budget status after adding an expense.
 */
public class BudgetStatus {
    private final boolean isOverBudget;
    private final boolean isNearBudget;

    public BudgetStatus(boolean isOverBudget, boolean isNearBudget) {
        this.isOverBudget = isOverBudget;
        this.isNearBudget = isNearBudget;
    }

    public boolean isOverBudget() {
        return isOverBudget;
    }

    public boolean isNearBudget() {
        return isNearBudget;
    }
}
