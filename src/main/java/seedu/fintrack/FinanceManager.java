package seedu.fintrack;

import java.util.ArrayList;
import java.util.List;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.Expense;

public class FinanceManager {
    private final List<Income> incomes = new ArrayList<>();
    private final List<Expense> expenses = new ArrayList<>(); // kept for balance math; not adding features

    public void addIncome(Income income) {
        if (income == null) {
            throw new IllegalArgumentException("Income cannot be null");
        }
        incomes.add(income);
    }

    public double getTotalIncome() {
        return incomes.stream().mapToDouble(Income::getAmount).sum();
    }

    public double getTotalExpense() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    /** View Overall Balance (income - expense). */
    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }
}
