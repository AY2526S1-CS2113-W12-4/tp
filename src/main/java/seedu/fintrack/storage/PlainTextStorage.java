package seedu.fintrack.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.fintrack.FinanceManager;
import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.IncomeCategory;

/**
 * Plain-text persistence layer that stores FinTrack data beside the application.
 *
 * <p>Each line represents a record separated by {@code |}:</p>
 * <ul>
 *     <li>{@code INCOME|amount|category|date|description}</li>
 *     <li>{@code EXPENSE|amount|category|date|description}</li>
 *     <li>{@code BUDGET|category|amount}</li>
 * </ul>
 *
 * <p>Descriptions are written verbatim; {@code null} descriptions are serialized as empty tokens.</p>
 */
public class PlainTextStorage {

    private static final Logger LOGGER = Logger.getLogger(PlainTextStorage.class.getName());
    private static final String TYPE_INCOME = "INCOME";
    private static final String TYPE_EXPENSE = "EXPENSE";
    private static final String TYPE_BUDGET = "BUDGET";
    private static final String SEPARATOR = "|";

    /**
     * Resolves the default persistence file path alongside the originating code location.
     *
     * <p>When packaged as a jar, this points to the jar's parent directory. During development,
     * it resolves to the compiled classes directory. On failure, the current working directory is used.</p>
     *
     * @return absolute path to {@code fintrack-data.txt}
     */
    public Path resolveDefaultFile() {
        try {
            Path source = Paths.get(
                    PlainTextStorage.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI());

            Path base = source;
            if (Files.isRegularFile(source)) {
                base = source.getParent();
            }
            if (base == null) {
                base = Paths.get(System.getProperty("user.dir"));
            }
            return base.resolve("fintrack-data.txt");
        } catch (Exception ex) {
            return Paths.get(System.getProperty("user.dir")).resolve("fintrack-data.txt");
        }
    }

    /**
     * Verifies that the application can write to {@code file}'s parent directory.
     *
     * @param file target persistence file
     * @return {@code true} if a probe write succeeds, {@code false} otherwise
     */
    public boolean canWrite(Path file) {
        Path parent = file.toAbsolutePath().getParent();
        if (parent == null) {
            parent = Path.of(".");
        }
        try {
            Files.createDirectories(parent);
            Path probe = Files.createTempFile(parent, "fintrack", ".tmp");
            Files.deleteIfExists(probe);
        } catch (IOException e) {
            return false;
        }

        if (Files.exists(file)) {
            return Files.isWritable(file);
        }
        return true;
    }

    /**
     * Loads persisted data from {@code file} into the supplied {@link FinanceManager}.
     * Malformed lines are skipped with a warning.
     *
     * @param file persistence file
     * @param manager finance manager to populate
     */
    public void load(Path file, FinanceManager manager) {
        if (file == null || manager == null) {
            throw new IllegalArgumentException("File and manager must not be null.");
        }
        if (!Files.exists(file)) {
            LOGGER.log(Level.INFO,
                    "No persistence file found at {0}; starting with empty state.",
                    file.toAbsolutePath());
            return;
        }

        boolean sawNonAscii = false;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                if (!sawNonAscii && containsNonAscii(line)) {
                    sawNonAscii = true;
                }

                String[] tokens = line.split("\\|", -1);
                try {
                    parseRecord(tokens, manager);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING,
                            "Skipping malformed persistence entry at line {0}: {1}",
                            new Object[]{lineNumber, ex.getMessage()});
                }
            }
        } catch (IOException ioException) {
            LOGGER.log(Level.WARNING, "Failed to read persistence file: {0}", ioException.toString());
        }

        if (sawNonAscii) {
            System.out.println("Warning: Non-ASCII characters detected in persistence file; "
                    + "they may appear incorrectly.");
        }
    }

    private boolean containsNonAscii(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > 0x7F) {
                return true;
            }
        }
        return false;
    }

    private void parseRecord(String[] tokens, FinanceManager manager) {
        if (tokens.length == 0) {
            throw new IllegalArgumentException("Empty record.");
        }

        switch (tokens[0]) {
        case TYPE_INCOME -> loadIncome(tokens, manager);
        case TYPE_EXPENSE -> loadExpense(tokens, manager);
        case TYPE_BUDGET -> loadBudget(tokens, manager);
        default -> throw new IllegalArgumentException("Unknown record type: " + tokens[0]);
        }
    }

    private void loadIncome(String[] tokens, FinanceManager manager) {
        if (tokens.length != 5) {
            throw new IllegalArgumentException("Expected 5 tokens for income, found " + tokens.length);
        }
        double amount = Double.parseDouble(tokens[1]);
        IncomeCategory category = IncomeCategory.parse(tokens[2]);
        LocalDate date = LocalDate.parse(tokens[3]);
        String description = tokens[4].isBlank() ? null : tokens[4];
        manager.addIncome(new Income(amount, category, date, description));
    }

    private void loadExpense(String[] tokens, FinanceManager manager) {
        if (tokens.length != 5) {
            throw new IllegalArgumentException("Expected 5 tokens for expense, found " + tokens.length);
        }
        double amount = Double.parseDouble(tokens[1]);
        ExpenseCategory category = ExpenseCategory.parse(tokens[2]);
        LocalDate date = LocalDate.parse(tokens[3]);
        String description = tokens[4].isBlank() ? null : tokens[4];
        manager.addExpense(new Expense(amount, category, date, description));
    }

    private void loadBudget(String[] tokens, FinanceManager manager) {
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Expected 3 tokens for budget, found " + tokens.length);
        }
        ExpenseCategory category = ExpenseCategory.parse(tokens[1]);
        double amount = Double.parseDouble(tokens[2]);
        manager.setBudget(category, amount);
    }

    /**
     * Saves all data tracked by {@code manager} to {@code file}.
     *
     * @param file destination file
     * @param manager finance manager providing current state
     */
    public void save(Path file, FinanceManager manager) {
        if (file == null || manager == null) {
            throw new IllegalArgumentException("File and manager must not be null.");
        }

        Path parent = file.toAbsolutePath().getParent();
        if (parent == null) {
            parent = Path.of(".");
        }
        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to create persistence directory: {0}", e.toString());
            System.out.println("Warning: Unable to create data directory; persistence is disabled.");
            return;
        }

        Path tempFile = parent.resolve(file.getFileName() + ".tmp");
        List<String> records = buildRecords(manager);

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            for (String record : records) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException ioException) {
            LOGGER.log(Level.WARNING, "Failed to write persistence data: {0}", ioException.toString());
            System.out.println("Warning: Unable to write persistence file; changes will not be saved.");
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException deleteException) {
                LOGGER.log(Level.WARNING, "Unable to delete temporary persistence file: {0}",
                        deleteException.toString());
            }
            return;
        }

        try {
            Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ioException) {
            LOGGER.log(Level.WARNING, "Failed to move persistence file into place: {0}", ioException.toString());
            System.out.println("Warning: Persistence file could not be updated; changes may be lost.");
        }
    }

    private List<String> buildRecords(FinanceManager manager) {
        List<String> lines = new ArrayList<>();

        for (Income income : manager.getIncomesView()) {
            lines.add(String.join(SEPARATOR,
                    TYPE_INCOME,
                    String.valueOf(income.getAmount()),
                    income.getCategory().name(),
                    income.getDate().toString(),
                    income.getDescription() == null ? "" : income.getDescription()));
        }

        for (Expense expense : manager.getExpensesView()) {
            lines.add(String.join(SEPARATOR,
                    TYPE_EXPENSE,
                    String.valueOf(expense.getAmount()),
                    expense.getCategory().name(),
                    expense.getDate().toString(),
                    expense.getDescription() == null ? "" : expense.getDescription()));
        }

        for (Map.Entry<ExpenseCategory, Double> entry : manager.getBudgetsView().entrySet()) {
            lines.add(String.join(SEPARATOR,
                    TYPE_BUDGET,
                    entry.getKey().name(),
                    String.valueOf(entry.getValue())));
        }

        return lines;
    }
}
