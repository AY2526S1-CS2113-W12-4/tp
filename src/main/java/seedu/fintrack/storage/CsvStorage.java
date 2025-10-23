package seedu.fintrack.storage;

import java.io.FileWriter;
import java.io.IOException;
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
     * Exports financial data to a CSV file.
     * <p>
     * The CSV will contain three sections:
     * 1. INCOMES: date, amount, category, description
     * 2. EXPENSES: date, amount, category, description
     * 3. SUMMARY: total income, total expenses, balance
     *
     * @param filePath The path where the CSV file should be saved
     * @param incomes List of income entries to export
     * @param expenses List of expense entries to export
     * @param totalIncome Total sum of all incomes
     * @param totalExpense Total sum of all expenses
     * @param balance Current balance (income - expense)
     * @throws IOException If there is an error writing to the file
     */
    @Override
    public void export(Path filePath, List<Income> incomes, List<Expense> expenses,
                      double totalIncome, double totalExpense, double balance) throws IOException {
        LOGGER.log(Level.INFO, "Exporting data to CSV: " + filePath);

        // Create parent directories if they don't exist
        Path parentDir = filePath.getParent();
        if (parentDir != null && !java.nio.file.Files.exists(parentDir)) {
            try {
                java.nio.file.Files.createDirectories(parentDir);
                LOGGER.log(Level.INFO, "Created directory: " + parentDir);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to create directory: " + parentDir, e);
                throw new IOException("Could not create directory " + parentDir 
                        + ". Please check your permissions.");
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {
            writeIncomeSection(writer, incomes);
            writeExpenseSection(writer, expenses);
            writeSummarySection(writer, totalIncome, totalExpense, balance);
            LOGGER.log(Level.INFO, "Successfully exported data to CSV");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to export data to CSV", e);
            throw e;
        }
    }

    /**
     * Writes the income section to the CSV file.
     *
     * @param writer The PrintWriter to write to
     * @param incomes List of income entries
     */
    private void writeIncomeSection(PrintWriter writer, List<Income> incomes) {
        writer.println("INCOMES");
        writer.println("Date,Amount,Category,Description");
        
        for (Income income : incomes) {
            writer.printf("%s,%.2f,%s,%s%n",
                income.getDate().format(DATE_FORMATTER),
                income.getAmount(),
                income.getCategory(),
                escapeCommas(income.getDescription()));
        }
    }

    /**
     * Writes the expense section to the CSV file.
     *
     * @param writer The PrintWriter to write to
     * @param expenses List of expense entries
     */
    private void writeExpenseSection(PrintWriter writer, List<Expense> expenses) {
        writer.println(); // blank line separator
        writer.println("EXPENSES");
        writer.println("Date,Amount,Category,Description");
        
        for (Expense expense : expenses) {
            writer.printf("%s,%.2f,%s,%s%n",
                expense.getDate().format(DATE_FORMATTER),
                expense.getAmount(),
                expense.getCategory(),
                escapeCommas(expense.getDescription()));
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
