package seedu.fintrack.storage;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.IncomeCategory;

class CsvStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void export_includesSummarySection() throws Exception {
        Path csvPath = tempDir.resolve("report.csv");
        CsvStorage storage = new CsvStorage();

        List<Income> incomes = List.of(
                new Income(1000.0, IncomeCategory.SALARY, LocalDate.of(2025, 1, 1), null)
        );
        List<Expense> expenses = List.of(
                new Expense(100.0, ExpenseCategory.FOOD, LocalDate.of(2025, 1, 2), null)
        );

        storage.export(csvPath, incomes, expenses, 1000.0, 100.0, 900.0);
        List<String> lines = java.nio.file.Files.readAllLines(csvPath);

        assertTrue(lines.contains("SUMMARY"), "CSV should contain summary header");
        assertTrue(lines.contains("Total Income,1000.00"), "CSV should include total income line");
        assertTrue(lines.contains("Total Expenses,100.00"), "CSV should include total expense line");
        assertTrue(lines.contains("Balance,900.00"), "CSV should include balance line");
    }
}
