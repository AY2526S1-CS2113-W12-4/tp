package seedu.fintrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

import seedu.fintrack.model.ExpenseList;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.Expense;

public class FinanceManager {
    private static final Logger LOGGER = Logger.getLogger(FinanceManager.class.getName());
    private final List<Income> incomes = new ArrayList<>();
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
        assert newExpense != null : "New expense cannot be null";
        deleteExpense(index); // This will throw if index is invalid
        return addExpense(newExpense);
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
        assert newIncome != null : "New income cannot be null";
        deleteIncome(index); // This will throw if index is invalid
        addIncome(newIncome);
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
