package seedu.fintrack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.time.YearMonth;
import java.util.Objects;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

import seedu.fintrack.model.ExpenseList;
import seedu.fintrack.model.IncomeList;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.IncomeCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.Expense;

/**
 * Manages all financial data including incomes and expenses.
 * <p>
 * Provides core operations to add, delete, and retrieve transactions,
 * compute totals and balances, and expose read-only views of stored data.
 * Ensures that all financial records are maintained in reverse
 * chronological order through {@code IncomeList} and {@code ExpenseList}.
 */
public class FinanceManager {
    private static final Logger LOGGER = Logger.getLogger(FinanceManager.class.getName());
    private final IncomeList incomes = new IncomeList();
    private final ExpenseList expenses = new ExpenseList(); // always newest->oldest
    private final Map<ExpenseCategory, Double> budgets = new HashMap<>();

    public void addIncome(Income income) {
        if (income == null) {
            LOGGER.log(Level.WARNING, "addIncome called with null");
            throw new IllegalArgumentException("Income cannot be null");
        }
        incomes.add(income);
        LOGGER.log(Level.INFO, "Income added.");
        assert incomes.contains(income) : "Income should have been added.";
    }

    /**
     * Adds an expense and checks if it exceeds the budget for its category.
     *
     * @param expense The expense to add. Must not be null.
     * @return {@code true} if adding this expense causes the total spending in its
     *     category to exceed the set budget for the first time, {@code false} otherwise.
     */
    public boolean addExpense(Expense expense) {
        if (expense == null) {
            LOGGER.log(Level.WARNING, "addExpense called with null");
            throw new IllegalArgumentException("Expense cannot be null");
        }

        ExpenseCategory category = expense.getCategory();
        boolean budgetExceeded = false;

        if (budgets.containsKey(category)) {
            double budget = budgets.get(category);
            double totalBefore = getTotalExpenseForCategory(category);
            double newTotal = totalBefore + expense.getAmount();

            if (totalBefore <= budget && newTotal > budget) {
                budgetExceeded = true;
            }
        }

        expenses.add(expense);
        LOGGER.log(Level.INFO, "Expense added");
        assert expenses.contains(expense);
        return budgetExceeded;
    }

    /**
     * Sets or updates the budget for a specific expense category.
     *
     * @param category The expense category to set the budget for.
     * @param amount The budget amount, which must be non-negative.
     * @throws IllegalArgumentException if the category is null or the amount is negative.
     */
    public void setBudget(ExpenseCategory category, double amount) {
        if (category == null) {
            LOGGER.log(Level.WARNING, "setBudget called with null");
            throw new IllegalArgumentException("Category cannot be null");
        }

        if (amount < 0) {
            LOGGER.log(Level.WARNING, "setBudget called with negative amount: {0}", amount);
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        budgets.put(category, amount);
        LOGGER.log(Level.INFO, "Budget set for {0}: {1}", new Object[]{category, amount});
    }

    /**
     * Returns an unmodifiable view of the current budgets.
     *
     * @return An unmodifiable map of expense categories to their budget amounts.
     */
    public Map<ExpenseCategory, Double> getBudgetsView() {
        return Collections.unmodifiableMap(budgets);
    }

    /**
     * Retrieves the budget amount for a specific category.
     *
     * @param category The category to check.
     * @return The budget amount as a {@code Double}, or {@code null} if no budget is set.
     */
    public Double getBudgetForCategory(ExpenseCategory category) {
        return budgets.get(category);
    }

    /**
     * Calculates the total expenses for a given category.
     *
     * @param category The category to sum expenses for.
     * @return The total amount spent in the given category.
     */
    public double getTotalExpenseForCategory(ExpenseCategory category) {
        if (category == null) {
            return 0.0;
        }

        return getExpensesView().stream()
                .filter(expense -> expense.getCategory() == category)
                .mapToDouble(Expense::getAmount)
                .sum();
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

    /** Returns an unmodifiable newest-first view list of incomes for printing. */
    public List<Income> getIncomesView() {
        List<Income> incomeList = Collections.unmodifiableList(incomes);
        assert incomeList != null : "Income list should not be null.";
        return incomeList;
    }

    /**
     * Returns an unmodifiable newest-first view of incomes for the given month.
     *
     * <p>Filters by year and month of {@code Income#getDate()}, preserving
     * reverse-chronological order via {@link IncomeList} logic.</p>
     *
     * @param yearMonth target month; must not be {@code null}.
     * @return unmodifiable newest-first list of incomes for that month.
     * @throws NullPointerException if {@code yearMonth} is {@code null}.
     */
    public List<Income> getIncomesViewForMonth(YearMonth yearMonth) {
        Objects.requireNonNull(yearMonth, "Month (YearMonth) cannot be null");
        LOGGER.log(Level.FINE, "Filtering incomes for {0}", yearMonth);

        IncomeList monthlyIncomes = new IncomeList();
        for (Income i : incomes.asUnmodifiableView()) {
            if (i.getDate().getYear() == yearMonth.getYear()
                    && i.getDate().getMonthValue() == yearMonth.getMonthValue()) {
                monthlyIncomes.add(i);
            }
        }

        assert monthlyIncomes != null : "IncomeList for month should not be null";
        LOGGER.log(Level.FINE, "Found {0} incomes for {1}",
                new Object[]{monthlyIncomes.size(), yearMonth});
        return monthlyIncomes.asUnmodifiableView();
    }

    /** Returns an unmodifiable newest-first view list of expenses for printing. */
    public List<Expense> getExpensesView() {
        List<Expense> expenseList = expenses.asUnmodifiableView();
        assert expenseList != null : "Expense list should not be null.";
        return expenseList;
    }

    /**
     * Returns an unmodifiable newest-first view of expenses for the given month.
     *
     * <p>Filters by year and month of {@code Expense#getDate()}, preserving
     * reverse-chronological order via {@link ExpenseList} logic.</p>
     *
     * @param yearMonth target month; must not be {@code null}.
     * @return unmodifiable newest-first list of expenses for that month.
     * @throws NullPointerException if {@code yearMonth} is {@code null}.
     */
    public List<Expense> getExpensesViewForMonth(YearMonth yearMonth) {
        Objects.requireNonNull(yearMonth, "Month (YearMonth) cannot be null");
        LOGGER.log(Level.FINE, "Filtering expenses for {0}", yearMonth);

        ExpenseList monthlyExpenses = new ExpenseList();
        for (Expense e : expenses.asUnmodifiableView()) {
            if (e.getDate().getYear() == yearMonth.getYear()
                    && e.getDate().getMonthValue() == yearMonth.getMonthValue()) {
                monthlyExpenses.add(e);
            }
        }

        assert monthlyExpenses != null : "ExpenseList for month should not be null";
        LOGGER.log(Level.FINE, "Found {0} expenses for {1}",
                new Object[]{monthlyExpenses.size(), yearMonth});
        return monthlyExpenses.asUnmodifiableView();
    }

    /**
     * Deletes the expense at the specified visible index in the newest-first view.
     *
     * <p>The index corresponds to the numbering shown in the printed expense list,
     * where index 1 refers to the chronologically latest expense.</p>
     *
     * @param index 1-based index of the expense to delete.
     * @return The deleted expense.
     * @throws IndexOutOfBoundsException If the index is not within the valid range.
     */
    public Expense deleteExpense(int index) {
        int oldExpensesSize = expenses.size();
        if (expenses.size() == 0) {
            LOGGER.log(Level.WARNING, "deleteExpense called on empty expense list");
            throw new IndexOutOfBoundsException("Cannot delete expense: The expense list is empty");
        }
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
     * Deletes the income at the specified visible index in the newest-first view.
     *
     * <p>The index corresponds to the numbering shown in the printed income list,
     * where index 1 refers to the chronologically latest income.</p>
     *
     * @param index 1-based index of the income to delete.
     * @return The deleted income.
     * @throws IndexOutOfBoundsException If the index is not within the valid range.
     */
    public Income deleteIncome(int index) {
        int oldIncomeSize = incomes.size();
        if (incomes.size() == 0) {
            LOGGER.log(Level.WARNING, "deleteIncome called on empty income list");
            throw new IndexOutOfBoundsException("Cannot delete income: The income list is empty");
        }
        if (index < 1 || index > incomes.size()) {
            LOGGER.log(Level.WARNING, "deleteIncome called with out of range index");
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

    /**
     * Modifies an existing expense by replacing it with a new one.
     * This is equivalent to deleting the old expense and adding the new one.
     *
     * @param index The 1-based index of the expense to modify
     * @param newExpense The new expense data
     * @return True if adding the new expense exceeds its category's budget
     * @throws IndexOutOfBoundsException If the index is invalid
     */
    public boolean modifyExpense(int index, Expense newExpense) {
        Expense oldExpense;
        try {
            oldExpense = deleteExpense(index); // This will throw if index is invalid
        } catch (IndexOutOfBoundsException e) {
            if (expenses.size() == 0) {
                throw new IndexOutOfBoundsException("Cannot modify expense: The expense list is empty");
            }
            throw new IndexOutOfBoundsException("Expense index out of range. Valid range: 1 to " + expenses.size());
        }
        
        try {
            return addExpense(newExpense);
        } catch (Exception e) {
            // Restore the old expense if adding the new one fails
            expenses.add(oldExpense);
            throw e;
        }
    }

    /**
     * Modifies an existing income by replacing it with a new one.
     * This is equivalent to deleting the old income and adding the new one.
     *
     * @param index The 1-based index of the income to modify
     * @param newIncome The new income data
     * @throws IndexOutOfBoundsException If the index is invalid
     */
    public void modifyIncome(int index, Income newIncome) {
        Income oldIncome;
        try {
            oldIncome = deleteIncome(index); // This will throw if index is invalid
        } catch (IndexOutOfBoundsException e) {
            if (incomes.size() == 0) {
                throw new IndexOutOfBoundsException("Cannot modify income: The income list is empty");
            }
            throw new IndexOutOfBoundsException("Income index out of range. Valid range: 1 to " + incomes.size());
        }
        
        try {
            addIncome(newIncome);
        } catch (Exception e) {
            // Restore the old income if adding the new one fails
            incomes.add(oldIncome);
            throw e;
        }
    }

    /**
     * Computes the total amount spent in each ExpenseCategory.
     *
     * <p>Iterates over all recorded expenses and sums their amounts by category.
     * The resulting map contains each category as a key and the aggregated amount as
     * the value.
     *
     * @return a map from expense category to the total amount spent in that category
     */

    public Map<ExpenseCategory, Double> getExpenseByCategory() {
        Map<ExpenseCategory, Double> expenseByCategory = new HashMap<>();

        for (Expense expense: expenses) {
            ExpenseCategory category = expense.getCategory();
            double currentAmount = expense.getAmount();
            double currentTotalAmount = expenseByCategory.getOrDefault(category, 0.0);
            expenseByCategory.put(category, currentTotalAmount + currentAmount);
        }

        LOGGER.log(Level.INFO, "Expense for each Category calculated successfully.");
        return expenseByCategory;
    }

    /**
     * Computes the total amount earn in each ExpenseCategory.
     *
     * <p>Iterates over all recorded incomes and sums their amounts by category.
     * The resulting map contains each category as a key and the aggregated amount as
     * the value.
     *
     * @return a map from income category to the total amount earn in that category
     */
    public Map<IncomeCategory, Double> getIncomeByCategory() {
        Map<IncomeCategory, Double> incomeByCategory = new HashMap<>();

        for (Income income: incomes) {
            IncomeCategory category = income.getCategory();
            double currentAmount = income.getAmount();
            double currentTotalAmount = incomeByCategory.getOrDefault(category, 0.0);
            incomeByCategory.put(category, currentTotalAmount + currentAmount);
        }

        LOGGER.log(Level.INFO, "Expense for each Category calculated successfully.");
        return incomeByCategory;
    }

    /**
     * Exports all financial data (incomes and expenses) to a CSV file.
     * The CSV will contain two sections:
     * 1. Incomes: date, amount, category, description
     * 2. Expenses: date, amount, category, description
     *
     * @param filePath The path where the CSV file should be saved
     * @throws IOException If there is an error writing to the file
     */
    public void exportToCSV(Path filePath) throws IOException {
        LOGGER.log(Level.INFO, "Exporting data to CSV: " + filePath);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Create parent directories if they don't exist
        Path parentDir = filePath.getParent();
        if (parentDir != null && !java.nio.file.Files.exists(parentDir)) {
            try {
                java.nio.file.Files.createDirectories(parentDir);
                LOGGER.log(Level.INFO, "Created directory: " + parentDir);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to create directory: " + parentDir, e);
                throw new IOException("Could not create directory " + parentDir + ". Please check your permissions.");
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {
            // Write header for incomes
            writer.println("INCOMES");
            writer.println("Date,Amount,Category,Description");
            
            // Write income data
            for (Income income : incomes) {
                writer.printf("%s,%.2f,%s,%s%n",
                    income.getDate().format(dateFormatter),
                    income.getAmount(),
                    income.getCategory(),
                    income.getDescription() != null ? income.getDescription().replace(",", ";") : "");
            }
            
            // Write header for expenses
            writer.println(); // blank line separator
            writer.println("EXPENSES");
            writer.println("Date,Amount,Category,Description");
            
            // Write expense data
            for (Expense expense : expenses) {
                writer.printf("%s,%.2f,%s,%s%n",
                    expense.getDate().format(dateFormatter),
                    expense.getAmount(),
                    expense.getCategory(),
                    expense.getDescription() != null ? expense.getDescription().replace(",", ";") : "");
            }

            // Write summary section
            writer.println();
            writer.println("SUMMARY");
            writer.printf("Total Income,%.2f%n", getTotalIncome());
            writer.printf("Total Expenses,%.2f%n", getTotalExpense());
            writer.printf("Balance,%.2f%n", getBalance());

            LOGGER.log(Level.INFO, "Successfully exported data to CSV");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to export data to CSV", e);
            throw e;
        }
    }
}
