package seedu.fintrack.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.fintrack.FinanceManager;
import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.IncomeCategory;

/**
 * Unit tests for {@link PlainTextStorage}.
 */
class PlainTextStorageTest {
    private static final String SAMPLE_INCOME =
            "INCOME|1200.00|SALARY|2024-10-01|Monthly salary";
    private static final String SAMPLE_EXPENSE =
            "EXPENSE|50.50|FOOD|2024-10-02|Lunch";
    private static final String SAMPLE_BUDGET =
            "BUDGET|FOOD|500.00";

    @TempDir
    Path tempDir;

    @Test
    void load_validRecords_populatesFinanceManager() throws IOException {
        Path dataFile = tempDir.resolve("fintrack-data.txt");
        Files.write(dataFile, List.of(SAMPLE_INCOME, SAMPLE_EXPENSE, SAMPLE_BUDGET),
                StandardCharsets.UTF_8);

        PlainTextStorage storage = new PlainTextStorage();
        FinanceManager manager = new FinanceManager();

        storage.load(dataFile, manager);

        assertEquals(1, manager.getIncomesView().size(), "Should load one income");
        Income income = manager.getIncome(1);
        assertEquals(1200.00, income.getAmount());
        assertEquals(IncomeCategory.SALARY, income.getCategory());
        assertEquals(LocalDate.parse("2024-10-01"), income.getDate());
        assertEquals("Monthly salary", income.getDescription());

        assertEquals(1, manager.getExpensesView().size(), "Should load one expense");
        Expense expense = manager.getExpense(1);
        assertEquals(50.50, expense.getAmount());
        assertEquals(ExpenseCategory.FOOD, expense.getCategory());
        assertEquals(LocalDate.parse("2024-10-02"), expense.getDate());
        assertEquals("Lunch", expense.getDescription());

        assertTrue(manager.getBudgetsView().containsKey(ExpenseCategory.FOOD),
                "Budget for FOOD should be populated");
        assertEquals(500.0, manager.getBudgetsView().get(ExpenseCategory.FOOD));
    }

    @Test
    void load_skipsMalformedRecords() throws IOException {
        Path dataFile = tempDir.resolve("fintrack-data-malformed.txt");
        Files.write(dataFile, List.of(
                        SAMPLE_INCOME,
                        "",
                        "EXPENSE|missing|tokens",
                        "UNKNOWN|some|data",
                        SAMPLE_BUDGET),
                StandardCharsets.UTF_8);

        PlainTextStorage storage = new PlainTextStorage();
        FinanceManager manager = new FinanceManager();

        storage.load(dataFile, manager);

        assertEquals(1, manager.getIncomesView().size(), "Valid income should be loaded");
        assertEquals(0, manager.getExpensesView().size(),
                "Malformed expense should be skipped");
        assertEquals(1, manager.getBudgetsView().size(),
                "Valid budget should still be loaded");
    }

    @Test
    void resolveDefaultFile_returnsPathEndingWithPersistenceName() {
        PlainTextStorage storage = new PlainTextStorage();
        Path resolved = storage.resolveDefaultFile();

        assertTrue(resolved.isAbsolute(), "Default file should resolve to absolute path");
        assertEquals("fintrack-data.txt", resolved.getFileName().toString(),
                "Expected default file name to be fintrack-data.txt");
    }

    @Test
    void canWrite_returnsTrueWhenWritable() {
        PlainTextStorage storage = new PlainTextStorage();
        Path target = tempDir.resolve("nested/folder/data.txt");

        assertTrue(storage.canWrite(target), "Writable directories should return true");
        assertTrue(Files.exists(target.getParent()), "Parent directories should be created");
    }

    @Test
    void canWrite_returnsFalseWhenParentIsFile() throws IOException {
        PlainTextStorage storage = new PlainTextStorage();
        Path blockingFile = tempDir.resolve("blocked");
        Files.createFile(blockingFile);
        Path target = blockingFile.resolve("data.txt");

        assertFalse(storage.canWrite(target), "Parent that is a file should fail writability check");
    }

    @Test
    void load_handlesNonAsciiRecords() throws IOException {
        Path dataFile = tempDir.resolve("fintrack-data-nonascii.txt");
        String incomeWithUnicode = "INCOME|500.00|GIFT|2024-11-01|Birthday 🎉";
        Files.write(dataFile, List.of(incomeWithUnicode), StandardCharsets.UTF_8);

        PlainTextStorage storage = new PlainTextStorage();
        FinanceManager manager = new FinanceManager();

        storage.load(dataFile, manager);

        assertEquals(1, manager.getIncomesView().size());
        assertEquals("Birthday 🎉", manager.getIncome(1).getDescription());
    }

    @Test
    void load_returnsEarlyWhenFileMissing() {
        Path missingFile = tempDir.resolve("non-existent.txt");
        PlainTextStorage storage = new PlainTextStorage();
        FinanceManager manager = new FinanceManager();

        storage.load(missingFile, manager);

        assertEquals(0, manager.getIncomesView().size());
        assertEquals(0, manager.getExpensesView().size());
    }

    @Test
    void load_ignoresIoErrorsAndKeepsWorking() throws IOException {
        Path directoryPath = Files.createDirectory(tempDir.resolve("asFile"));

        PlainTextStorage storage = new PlainTextStorage();
        FinanceManager manager = new FinanceManager();

        storage.load(directoryPath, manager);

        assertEquals(0, manager.getIncomesView().size(),
                "Unreadable source should not populate incomes");
        assertEquals(0, manager.getExpensesView().size(),
                "Unreadable source should not populate expenses");
    }

    @Test
    void save_writesExpectedPlaintextFormat() throws IOException {
        FinanceManager manager = new FinanceManager();
        manager.addIncome(new Income(900.0, IncomeCategory.GIFT,
                LocalDate.parse("2024-12-25"), "Holiday gift"));
        manager.addExpense(new Expense(120.75, ExpenseCategory.GROCERIES,
                LocalDate.parse("2024-12-24"), "Festive groceries"));
        manager.setBudget(ExpenseCategory.GROCERIES, 800.0);

        PlainTextStorage storage = new PlainTextStorage();
        Path dataFile = tempDir.resolve("fintrack-save.txt");

        storage.save(dataFile, manager);

        List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        assertFalse(lines.isEmpty(), "Saved file should have content");
        assertEquals(3, lines.size(), "Expected exactly three records in saved file");
        // Order: incomes, expenses, then budgets (per buildRecords implementation)
        assertEquals("INCOME|900.0|GIFT|2024-12-25|Holiday gift", lines.get(0));
        assertEquals("EXPENSE|120.75|GROCERIES|2024-12-24|Festive groceries", lines.get(1));
        assertEquals("BUDGET|GROCERIES|800.0", lines.get(2));
    }

    @Test
    void save_handlesWriteFailuresAndCleansUpTempFile() throws IOException {
        FinanceManager manager = new FinanceManager();
        manager.addExpense(new Expense(10.0, ExpenseCategory.FOOD,
                LocalDate.parse("2024-10-10"), "Snack"));

        PlainTextStorage storage = new PlainTextStorage();
        Path dataFile = tempDir.resolve("blocked.txt");
        Path tempFile = tempDir.resolve(dataFile.getFileName().toString() + ".tmp");

        Files.createDirectories(tempFile);
        Path locked = tempFile.resolve("locked");
        Files.writeString(locked, "keep directory non-empty", StandardCharsets.UTF_8);

        storage.save(dataFile, manager);

        assertFalse(Files.exists(dataFile), "Final persistence file should not be created on failure");
        assertTrue(Files.exists(locked),
                "Temp directory should remain because deletion of non-empty dir fails");
    }
}
