package seedu.fintrack.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.Income;

/**
 * Represents a storage interface for exporting financial data.
 * <p>
 * Implementations of this interface handle the persistence of income and expense data
 * to various formats (CSV, JSON, etc.).
 */
public interface Storage {
    /**
     * Exports financial data to the specified file path.
     *
     * @param filePath The path where the data should be saved
     * @param incomes List of income entries to export
     * @param expenses List of expense entries to export
     * @param totalIncome Total sum of all incomes
     * @param totalExpense Total sum of all expenses
     * @param balance Current balance (income - expense)
     * @throws IOException If there is an error writing to the file
     */
    void export(Path filePath, List<Income> incomes, List<Expense> expenses,
                double totalIncome, double totalExpense, double balance) throws IOException;
}
