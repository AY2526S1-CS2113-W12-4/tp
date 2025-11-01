package seedu.fintrack.storage;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.Income;

/**
 * Handles exporting financial data to CSV format.
 * <p>
 * This class is responsible for formatting and writing income and expense data
 * to a CSV file with proper headers, data rows, and a summary section.
 */
public class CsvStorage implements Storage {
    private static final Logger LOGGER = Logger.getLogger(CsvStorage.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Exports financial data to a CSV file in proper CSV format.
     * <p>
     * The CSV will contain all transactions in a single table with:
     * Type, Date, Amount, Category, Description columns.
     * Summary statistics are appended at the end after a blank line.
     *
     * @param filePath The path where the CSV file should be saved
     * @param incomes List of income entries to export
     * @param expenses List of expense entries to export
     * @param totalIncome Total sum of all incomes
     * @param totalExpense Total sum of all expenses
     * @param balance Current balance (income - expense)
     * @throws IllegalArgumentException If there is an error writing to the file
     */
    @Override
    public void export(Path filePath, List<Income> incomes, List<Expense> expenses,
                      double totalIncome, double totalExpense, double balance) {
        LOGGER.log(Level.INFO, "Exporting data to CSV: " + filePath);

        // Note: No directory creation needed since we only write to current directory

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {
            // Write CSV header
            writer.println("Type,Date,Amount,Category,Description");

            // Write all income entries
            for (Income income : incomes) {
                writer.printf("INCOME,%s,%.2f,%s,%s%n",
                    income.getDate().format(DATE_FORMATTER),
                    income.getAmount(),
                    income.getCategory(),
                    escapeCommas(income.getDescription()));
            }

            // Write all expense entries
            for (Expense expense : expenses) {
                writer.printf("EXPENSE,%s,%.2f,%s,%s%n",
                    expense.getDate().format(DATE_FORMATTER),
                    expense.getAmount(),
                    expense.getCategory(),
                    escapeCommas(expense.getDescription()));
            }

            writeSummarySection(writer, totalIncome, totalExpense, balance);

            LOGGER.log(Level.INFO, "Successfully exported data to CSV");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to export data to CSV", e);
            throw new IllegalArgumentException("Export failed. Please check that:"
                    + "\n- The file is not open in another program"
                    + "\n- You have write permissions in this directory"
                    + "\n- The filename is valid and not too long");
        }
    }

    /**
     * Writes the summary section to the CSV file.
     *
     * @param writer The PrintWriter to write to
     * @param totalIncome Total sum of all incomes
     * @param totalExpense Total sum of all expenses
     * @param balance Current balance
     */
    private void writeSummarySection(PrintWriter writer, double totalIncome, 
                                     double totalExpense, double balance) {
        writer.println();
        writer.println("SUMMARY");
        writer.printf("Total Income,%.2f%n", totalIncome);
        writer.printf("Total Expenses,%.2f%n", totalExpense);
        writer.printf("Balance,%.2f%n", balance);
    }

    /**
     * Escapes commas in descriptions by replacing them with semicolons.
     * Returns empty string if description is null.
     *
     * @param description The description to escape
     * @return Escaped description or empty string
     */
    private String escapeCommas(String description) {
        return description != null ? description.replace(",", ";") : "";
    }
}
