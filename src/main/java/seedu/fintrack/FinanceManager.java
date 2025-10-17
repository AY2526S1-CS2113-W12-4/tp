package seedu.fintrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import seedu.fintrack.model.ExpenseList;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.Expense;

public class FinanceManager {
    private static final Logger LOGGER = Logger.getLogger(FinanceManager.class.getName());
    private final List<Income> incomes = new ArrayList<>();
    private final ExpenseList expenses = new ExpenseList(); // always newest->oldest

    public void addIncome(Income income) {
        if (income == null) {
            LOGGER.log(Level.WARNING, "addIncome called with null");
            throw new IllegalArgumentException("Income cannot be null");
        }
        incomes.add(income);
        LOGGER.log(Level.INFO, "Income added.");
        assert incomes.contains(income) : "Income should have been added.";
    }

    public void addExpense(Expense expense) {
        if (expense == null) {
            LOGGER.log(Level.WARNING, "addExpense called with null");
            throw new IllegalArgumentException("Expense cannot be null");
        }
        expenses.add(expense);
        LOGGER.log(Level.INFO, "Expense added");
        assert expenses.contains(expense);
    }

    public double getTotalIncome() {
        double sum = 0;
        for (Income income : incomes) {
            assert income != null : "Income should not be null.";
            double incomeAmount = income.getAmount();
            assert !Double.isNaN(incomeAmount) : "Income amount should be a number";
            sum+=incomeAmount;
        }
        LOGGER.log(Level.INFO, "Total income calculated: " + sum);
        return sum;
    }

    public double getTotalExpense() {
        double sum = 0;
        for (Expense expense : expenses) {
            assert expense != null : "Expense should not be null.";
            double expenseAmount = expense.getAmount();
            assert !Double.isNaN(expenseAmount) : "Expense should be a number.";
            sum+=expenseAmount;
        }
        LOGGER.log(Level.INFO, "Total Expense calculated: " + sum);
        return sum;
    }

    /** View Overall Balance (income - expense). */
    public double getBalance() {
        double totalIncome = getTotalIncome();
        double totalExpense = getTotalExpense();
        assert !Double.isNaN(totalIncome) : "Total income should be a number.";
        assert !Double.isNaN(totalExpense) : "Total expense should be a number.";
        return totalIncome - totalExpense;
    }

    /** Returns an unmodifiable view of incomes in the order they were added. */
    public List<Income> getIncomesView() {
        List<Income> incomeList = Collections.unmodifiableList(incomes);
        assert incomeList != null : "Income list should not be null.";
        return incomeList;
    }

    /** Returns an unmodifiable newest-first view for printing. */
    public List<Expense> getExpensesView() {
        List<Expense> expenseList = expenses.asUnmodifiableView();
        assert expenseList != null : "Expense list should not be null.";
        return expenseList;
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
        int oldExpensesSize = expenses.size();
        if (index < 1 || index > expenses.size()) {
            LOGGER.log(Level.WARNING, "deleteExpense called with out of range index");
            throw new IndexOutOfBoundsException("Expense index out of range. Valid range: 1 to " + expenses.size());
        }

        Expense expectedDeletedExpense = expenses.get(index - 1);
        Expense deletedExpense = expenses.remove(index - 1);
        assert expenses.size() == oldExpensesSize - 1 : "Expenses should have exactly one less item";
        assert deletedExpense != null : "Deleted expense should not have been a null";
        assert deletedExpense.equals(expectedDeletedExpense) :
                "Removed expense should have been the one at index - 1";
        return deletedExpense;
    }

    /**
     * Deletes an income at the given index (1-based).
     * @param index The 1-based index of the income to delete
     * @return The deleted income
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public Income deleteIncome(int index) {
        int oldIncomeSize = incomes.size();
        if (index < 1 || index > incomes.size()) {
            LOGGER.log(Level.WARNING, "deleteIncome called with out of range index.");
            throw new IndexOutOfBoundsException("Income index out of range. Valid range: 1 to " + incomes.size());
        }

        Income expectedDeletedIncome = incomes.get(index - 1);
        Income deletedIncome = incomes.remove(index - 1);
        assert incomes.size() == oldIncomeSize - 1 : "Incomes should have exactly one less item";
        assert deletedIncome != null : "Deleted income should not have been a null";
        assert deletedIncome.equals(expectedDeletedIncome) :
                "Removed income should have been the one at index - 1";
        return deletedIncome;
    }
}
