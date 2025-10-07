package seedu.fintrack;

import java.util.ArrayList;
import java.util.List;

import seedu.fintrack.model.ExpenseList;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.Expense;

public class FinanceManager {
    private final List<Income> incomes = new ArrayList<>();
    private final ExpenseList expenses = new ExpenseList(); // always newest->oldest

    public void addIncome(Income income) {
        if (income == null) {
            throw new IllegalArgumentException("Income cannot be null");
        }
        incomes.add(income);
    }

    public void addExpense(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null");
        }
        expenses.add(expense);
    }

    public double getTotalIncome() {
        return incomes.stream().mapToDouble(Income::getAmount).sum();
    }

    public double getTotalExpense() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    /** Returns an unmodifiable newest-first view for printing. */
    public List<Expense> getExpensesView() {
        return expenses.asUnmodifiableView();
    }

    /** View Overall Balance (income - expense). */
    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    /**
     * Deletes the expense at the specified visible index in the newest-first view.
     *
     * <p>The index corresponds to the numbering shown in the printed expense list,
     * where index 1 refers to the most recently added expense.</p>
     *
     * @param index 1-based index of the expense to delete.
     * @return The deleted expense.
     * @throws IndexOutOfBoundsException If the index is not within the valid range.
     */
    public Expense deleteExpense(int index) {
        if (index < 1 || index > expenses.size()) {
            throw new IndexOutOfBoundsException("Expense index out of range. Valid range: 1 to " + expenses.size());
        }
        return expenses.remove(index - 1); // Convert to 0-based index
    }

    /**
     * Deletes an income at the given index (1-based).
     * @param index The 1-based index of the income to delete
     * @return The deleted income
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public Income deleteIncome(int index) {
        if (index < 1 || index > incomes.size()) {
            throw new IndexOutOfBoundsException("Income index out of range. Valid range: 1 to " + incomes.size());
        }
        return incomes.remove(index - 1); // Convert to 0-based index
    }
}
