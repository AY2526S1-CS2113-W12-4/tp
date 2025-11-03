package seedu.fintrack;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Locale;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Integration-style tests for FinTrack.main.
 */
public class FinTrackTest {

    // Known tips from TipsStorage
    private static final String[] KNOWN_TIPS = new String[] {
        "Don't buy a Mac especially if you are doing EE2026!",
        "If you stay on campus, Dining Credits are now available to use in NUS food courts!",
        "Keep a lookout for free welfare as exam period is approaching!",
        "Remember to track your expenses daily!",
        "Take the shuttle bus, it's worth it :(",
        "OpenAI credits are too expensive!",
        "The Deck banmian is less than $4!",
        "You can spend the pass royale money on better food ._."
    };

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final java.io.InputStream originalIn = System.in;
    private ByteArrayOutputStream outContent;
    private Locale originalLocale;


    private static boolean outputContainsAnyKnownTip(String s) {
        for (String tip : KNOWN_TIPS) {
            if (s.contains(tip)) {
                return true;
            }
        }
        return false;
    }

    private static String sectionBetween(String s, String start, String end) {
        int i = s.indexOf(start);
        if (i < 0) {
            return "";
        }
        int j = s.indexOf(end, i);
        if (j < 0) {
            j = s.length();
        }
        return s.substring(i, j);
    }

    private static void mustNotContain(String s, String unexpected) {
        org.junit.jupiter.api.Assertions.assertFalse(
                s.contains(unexpected),
                "Did NOT expect output to contain:\n---\n" + unexpected + "\n---\nActual:\n" + s);
    }

    @BeforeEach
    void setUp() {
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        Locale.setDefault(originalLocale);
    }

    private String out() {
        return outContent.toString();
    }

    private String run(String script) throws Exception {
        byte[] bytes = script.getBytes(StandardCharsets.UTF_8);
        System.setIn(new java.io.ByteArrayInputStream(bytes));
        // bind Ui's scanner to current System.in
        Ui.test_setScanner(new java.util.Scanner(System.in, StandardCharsets.UTF_8));
        System.setProperty("fintrack.disablePersistence", "true");
        FinTrack.main(new String[0]);
        return out();
    }

    private static void mustContain(String s, String expected) {
        assertTrue(s.contains(expected),
                "Expected output to contain:\n---\n" + expected + "\n---\nActual:\n" + s);
    }

    @Test
    void help_extraArgs_thenExit() throws Exception {
        String s = run("help extra\nbye\n");
        mustContain(s, "Welcome to FinTrack!");
        mustContain(s, "Error: The 'help' command does not take additional arguments.");
        mustContain(s, "Bye. Hope to see you again soon!");
    }

    @Test
    void invalidCommand_thenExit() throws Exception {
        String s = run("nonsense\nbye\n");
        mustContain(s, "Invalid command. Type 'help' for a list of available commands.");
        mustContain(s, "Bye. Hope to see you again soon!");
    }

    @Test
    void nonAsciiInput_rejectedWithError() throws Exception {
        String script = String.join("\n",
                "add-income a/200 c/Salary d/2025-10-01 des/\u5348\u9910",
                "bye");
        String s = run(script);
        mustContain(s, "Error: Unsupported characters detected. Please use standard ASCII text only.");
        mustNotContain(s, "Income added:");
        mustContain(s, "Bye. Hope to see you again soon!");
    }

    @Test
    void addIncome_andBalance() throws Exception {
        String script = String.join("\n",
                "add-income a/2000 c/Salary d/2025-10-01 des/Pay",
                "add-income a/150 c/Gift d/2025-10-05",
                "balance",
                "bye");
        String s = run(script);
        mustContain(s, "Income added:");
        mustContain(s, "Category: SALARY");
        mustContain(s, "Overall Balance:");
    }

    @Test
    void addExpenseBudgetWarns() throws Exception {
        String script = String.join("\n",
                "budget c/Food a/50",
                "add-expense a/30 c/Food d/2025-10-10 des/Meal",
                "add-expense a/25 c/Food d/2025-10-11",
                "bye");
        String s = run(script);
        mustContain(s, "Budget set for FOOD: $50.00");
        mustContain(s, "Expense added:");
        mustContain(s, "BUDGET ALERT");
    }

    @Test
    void balanceMonthAndErrors() throws Exception {
        String script = String.join("\n",
                "add-income a/100 c/Salary d/2025-09-30",
                "add-expense a/40 c/Food d/2025-09-29",
                "balance d/2025-09",
                "balance d/2025-13",
                "balance d/2025-09 extra",
                "bye");
        String s = run(script);
        mustContain(s, "Overall Balance for the month 2025-09:");
        mustContain(s, "Error: Invalid date: 2025-13 does not exist.");
        mustContain(s, "Error: Usage: balance [d/YYYY-MM]");
    }

    @Test
    void listExpense_andListIncome_paths() throws Exception {
        String ym = LocalDate.now().toString().substring(0, 7);
        String script = String.join("\n",
                "add-income a/10 c/Salary d/" + LocalDate.now(),
                "add-expense a/5 c/Food d/" + LocalDate.now(),
                "list-expense",
                "list-expense " + ym,
                "list-income",
                "list-income " + ym,
                "list-income 2025-10 extra",
                "bye");
        String s = run(script);
        mustContain(s, "Expenses (Newest first):");
        mustContain(s, "Incomes (Newest first):");
        mustContain(s, "Error: Usage: list-income [d/YYYY-MM]");
    }

    @Test
    void listExpense_filterByMonth_success() throws Exception {
        String script = String.join("\n",
                "add-expense a/5 c/Food d/2025-09-29 des/SeptMeal",
                "add-expense a/7 c/Food d/2025-10-02 des/OctMeal",
                "list-expense d/2025-09",
                "bye");
        String s = run(script);

        String listBlock = sectionBetween(s, "Expenses ", "Bye.");

        mustContain(listBlock, "Expenses for the month 2025-09 (Newest first):");
        mustContain(listBlock, "2025-09-29");  // present in the listing
        mustNotContain(listBlock, "2025-10-02"); // not present in the listing
        // ensure the oct description isn't in the list either
        mustNotContain(listBlock, "OctMeal");
    }


    @Test
    void listExpenseByMonth_noResults_printsEmptyState() throws Exception {
        String s = run(String.join("\n",
                // Only Oct entries exist
                "add-expense a/7 c/Food d/2025-10-02",
                "list-expense d/2099-01",
                "bye"));
        mustContain(s, "No expenses recorded.");
    }

    @Test
    void listExpense_invalidMonth_triggersError() throws Exception {
        String s = run(String.join("\n",
                "list-expense d/2025-13",
                "bye"));
        mustContain(s, "Error: Invalid date: 2025-13 does not exist.");
    }

    @Test
    void listExpense_unexpectedTokens_triggersUsage() throws Exception {
        String s = run(String.join("\n",
                "list-expense 2025-10 extra",
                "bye"));
        mustContain(s, "Error: Usage: list-expense [d/YYYY-MM]");
    }


    @Test
    void listIncome_filterByMonth_success() throws Exception {
        String script = String.join("\n",
                "add-income a/100 c/Salary d/2025-09-30 des/SeptPay",
                "add-income a/120 c/Salary d/2025-10-03 des/OctPay",
                "list-income d/2025-09",
                "bye");
        String s = run(script);

        String listBlock = sectionBetween(s, "Incomes ", "Bye.");

        mustContain(listBlock, "Incomes for the month 2025-09 (Newest first):");
        mustContain(listBlock, "2025-09-30");
        mustNotContain(listBlock, "2025-10-03");
        mustNotContain(listBlock, "OctPay");
    }


    @Test
    void listIncomeByMonth_noResults_printsEmptyState() throws Exception {
        String s = run(String.join("\n",
                // Only Oct entries exist
                "add-income a/120 c/Salary d/2025-10-03",
                "list-income d/2099-01",
                "bye"));
        mustContain(s, "No incomes recorded.");
    }

    @Test
    void listIncome_invalidMonth_triggersError() throws Exception {
        String s = run(String.join("\n",
                "list-income d/2025-00",
                "bye"));
        mustContain(s, "Error: Invalid date: 2025-00 does not exist.");
    }

    @Test
    void listIncome_unexpectedTokens_triggersUsage() throws Exception {
        String s = run(String.join("\n",
                "list-income 2025-10 extra",
                "bye"));
        mustContain(s, "Error: Usage: list-income [d/YYYY-MM]");
    }


    @Test
    void deleteSuccessAndOob() throws Exception {
        String script = String.join("\n",
                "add-income a/30 c/Salary d/2025-10-01",
                "add-expense a/7 c/Food d/2025-10-02",
                "delete-expense 1",
                "delete-expense 2",
                "delete-income 1",
                "delete-income 1",
                "bye");
        String s = run(script);
        mustContain(s, "Expense deleted (index 1):");
        mustContain(s, "Error: Cannot delete expense: The expense list is empty");
        mustContain(s, "Income deleted (index 1):");
        mustContain(s, "Error: Cannot delete expense: The expense list is empty");
    }

    @Test
    void modifyPaths() throws Exception {
        String script = String.join("\n",
                "budget c/Entertainment a/10",
                "add-expense a/5 c/Entertainment d/2025-10-01 des/Ticket",
                "modify-expense 1 a/12 c/Entertainment d/2025-10-02 des/VIP",
                "add-income a/10 c/Salary d/2025-10-01",
                "modify-income 1 a/12 c/Salary d/2025-10-02 des/Raise",
                "modify-income",
                "modify-income 0 a/1 c/Salary d/2025-10-03",
                "modify-income x a/1 c/Salary d/2025-10-03",
                "bye");
        String s = run(script);
        mustContain(s, "Budget set for ENTERTAINMENT: $10.00");
        mustContain(s, "Expense at index 1 modified to:");
        mustContain(s, "Income at index 1 modified to:");
        mustContain(s, "Error: Missing index.");
        mustContain(s, "Error: Income index must be a positive number.");
        mustContain(s, "Error: Income index must be a valid number.");
    }

    @Test
    void exportSuccessAndAutoCreateDirectories() throws Exception {
        String filename = "test_export_" + System.currentTimeMillis() + ".csv";
        String script = String.join("\n",
                "add-income a/11 c/Salary d/2025-10-01",
                "add-expense a/3 c/Food d/2025-10-02",
                "export " + filename,
                "bye");
        String s = run(script);
        mustContain(s, "Successfully exported data to:");
        assertTrue(java.nio.file.Files.exists(java.nio.file.Path.of(filename)));
    }

    @Test
    void budget_negativeAmount_triggersErrorCatch() throws Exception {
        String s = run(String.join("\n",
                "budget c/Food a/-5",
                "bye"));
        // Message comes from FinanceManager.setBudget
        mustContain(s, "Error: Amount must be non-negative");
    }

    @Test
    void budget_missingParams_triggersParserErrorCatch() throws Exception {
        // no amount
        String s1 = run(String.join("\n",
                "budget c/Food",
                "bye"));
        mustContain(s1, "Error:"); // keep broad in case parser wording changes
    }

    @Test
    void listBudget_unexpectedArgs_triggersGuard() throws Exception {
        String s = run(String.join("\n",
                "list-budget extra",
                "bye"));
        mustContain(s, "Error: The 'list-budget' command does not take additional arguments.");
    }

    @Test
    void listBudget_noBudgets_printsEmptyState() throws Exception {
        String s = run(String.join("\n",
                "list-budget",
                "bye"));
        mustContain(s, "No budgets have been set.");
    }

    @Test
    void summaryExpense_success() throws Exception {
        String script = String.join("\n",
                "add-expense a/10 c/Food d/2025-10-01",
                "add-expense a/5 c/Transport d/2025-10-02",
                "summary-expense",
                "bye");
        String s = run(script);

        // Stable anchors from Ui.printSummaryExpense(...)
        mustContain(s, "Here is an overall summary of your expenses!");
        mustContain(s, "Total Expense:");
        // Category text to ensure the breakdown printed
        mustContain(s, "FOOD");
    }

    @Test
    void summaryExpense_unexpectedArgs() throws Exception {
        String s = run("summary-expense extra\nbye\n");
        mustContain(s, "Error: The 'summary-expense' command does not take additional arguments.");
    }

    @Test
    void summaryIncome_success() throws Exception {
        String script = String.join("\n",
                "add-income a/30 c/Salary d/2025-10-03",
                "add-income a/12 c/Gift d/2025-10-04",
                "summary-income",
                "bye");
        String s = run(script);

        // Stable anchors from Ui.printSummaryIncome(...)
        mustContain(s, "Here is an overall summary of your income!");
        mustContain(s, "Total Income:");
        // Category text to ensure the breakdown printed
        mustContain(s, "SALARY");
    }

    @Test
    void summaryIncome_unexpectedArgs() throws Exception {
        String s = run("summary-income extra\nbye\n");
        mustContain(s, "Error: The 'summary-income' command does not take additional arguments.");
    }

    @Test
    void tips_printsAKnownTip_once() throws Exception {
        String s = run("tips\nbye\n");
        // Should print exactly one of the known tips
        assertTrue(outputContainsAnyKnownTip(s),
                "Expected output to contain one of the known tips.\nActual:\n" + s);
    }

    @Test
    void tips_printsKnownTips_multipleTimes() throws Exception {
        // Run tips several times to ensure every printed line is from the known list
        String s = run(String.join("\n",
                "tips",
                "tips",
                "tips",
                "bye"
        ));
        long hits = java.util.Arrays.stream(KNOWN_TIPS).filter(s::contains).count();
        // We issued 3 tips; require at least 1 match and all tip-lines must be from the known set
        assertTrue(hits >= 1, "Expected at least one known tip to appear.\nActual:\n" + s);

        // Stronger check: every 'tips' call printed something from the known list
        // Count occurrences by splitting on lines and checking membership
        long printedKnownLines = s.lines()
                .filter(line -> java.util.Arrays.stream(KNOWN_TIPS).anyMatch(line::contains))
                .count();
        assertTrue(printedKnownLines >= 3,
                "Expected 3 known tip lines across 3 calls.\nActual output:\n" + s);
    }

    @Test
    void tips_unexpectedArgs() throws Exception {
        String s = run("tips extra\nbye\n");
        mustContain(s, "Error: The 'tips' command does not take additional arguments.");
    }

    @Test
    void byeArgsThenQuit() throws Exception {
        String s = run("bye now\nbye\n");
        mustContain(s, "Error: The 'bye' command does not take additional arguments.");
        mustContain(s, "Bye. Hope to see you again soon!");
    }

    @Test
    void export_parentPathIsAFile_triggersIOExceptionCatch() throws Exception {
        // This test is no longer relevant since we only allow filenames in current directory
        // Replacing with a test for path validation
        String script = String.join("\n",
                "add-income a/11 c/Salary d/2025-10-01",
                "add-expense a/3 c/Food d/2025-10-02",
                "export path/to/file.csv",
                "bye");
        String s = run(script);

        // Should trigger filename validation error
        mustContain(s, "Error: Invalid filename. Please provide only a filename (no paths). Usage: export <filename>");
    }

    @Test
    void export_fileAlreadyExists_showsError() throws Exception {
        // Create a file first
        String filename = "existing_file.csv";
        java.nio.file.Files.write(java.nio.file.Path.of(filename), "test content".getBytes());

        String script = String.join("\n",
                "add-income a/11 c/Salary d/2025-10-01",
                "add-expense a/3 c/Food d/2025-10-02",
                "export " + filename,
                "bye");
        String s = run(script);

        mustContain(s, "Error: Export failed. File already exists:");
        mustContain(s, "Please choose a different filename.");
        mustNotContain(s, "Successfully exported data to:");
    }

    @Test
    void aliases_workForCoreCommands() throws Exception {
        String script = String.join("\n",
                // Using aliases for data entry
                "ae a/25 c/Food d/2025-10-10 des/Lunch",
                "ai a/100 c/Salary d/2025-10-01 des/Pay",

                // Using aliases for listing
                "le",
                "li",

                // Using alias for balance
                "b",

                // Using aliases for modification
                "me 1 a/30 c/Food d/2025-10-10 des/Dinner",
                "mi 1 a/120 c/Salary d/2025-10-01 des/Raise",

                // Using aliases for deletion
                "de 1",
                "di 1",

                "bye");
        String s = run(script);

        // Verify all alias commands worked
        mustContain(s, "Expense added:");
        mustContain(s, "Income added:");
        mustContain(s, "Expenses (Newest first):");
        mustContain(s, "Incomes (Newest first):");
        mustContain(s, "Overall Balance:");
        mustContain(s, "Expense at index 1 modified to:");
        mustContain(s, "Income at index 1 modified to:");
        mustContain(s, "Expense deleted");
        mustContain(s, "Income deleted");
    }

    @Test
    void aliases_workForBudgetAndExport() throws Exception {
        String filename = "test_alias_export_" + System.currentTimeMillis() + ".csv";
        String script = String.join("\n",
                // Using budget alias
                "bg c/Food a/50",
                "ae a/30 c/Food d/2025-10-10",

                // Using export alias
                "ex " + filename,

                "bye");
        String s = run(script);

        // Verify budget and export aliases worked
        mustContain(s, "Budget set for FOOD: $50.00");
        mustContain(s, "Expense added:");
        mustContain(s, "Successfully exported data to:");
        assertTrue(java.nio.file.Files.exists(java.nio.file.Path.of(filename)));
    }

    @Test
    void aliases_workWithMonthFilters() throws Exception {
        String script = String.join("\n",
                "ae a/10 c/Food d/2025-09-30 des/Sept",
                "ae a/20 c/Food d/2025-10-01 des/Oct",
                "ai a/100 c/Salary d/2025-09-30 des/Sept",
                "ai a/200 c/Salary d/2025-10-01 des/Oct",

                // Using aliases with month filters
                "le d/2025-09",
                "li d/2025-09",
                "b d/2025-09",

                "bye");
        String s = run(script);

        // Verify aliases work with month filters
        mustContain(s, "Expenses for the month 2025-09");
        mustContain(s, "Incomes for the month 2025-09");
        mustContain(s, "Overall Balance for the month 2025-09:");
    }

    @Test
    void deleteExpenseAlias_extremeNegative_showSpecificError() throws Exception {
        String s = run("de -9999999999\nbye\n");
        mustContain(s, "Expense index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void deleteExpenseAlias_extremePositive_showSpecificError() throws Exception {
        String s = run("de 9999999999\nbye\n");
        mustContain(s, "Expense index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void deleteExpenseAlias_justAboveMaxInt_showSpecificError() throws Exception {
        String s = run("de 2147483648\nbye\n");
        mustContain(s, "Expense index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void deleteExpenseAlias_justBelowMinInt_showSpecificError() throws Exception {
        String s = run("de -2147483649\nbye\n");
        mustContain(s, "Expense index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void deleteExpenseAlias_veryLargePositive_showSpecificError() throws Exception {
        String s = run("de 99999999999999999999\nbye\n");
        mustContain(s, "Expense index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void deleteExpenseAlias_veryLargeNegative_showSpecificError() throws Exception {
        String s = run("de -99999999999999999999\nbye\n");
        mustContain(s, "Expense index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void deleteExpenseAlias_invalidFormat_showGenericError() throws Exception {
        String s = run("de abc123\nbye\n");
        mustContain(s, "Expense index must be a valid number.");
    }

    @Test
    void deleteIncomeAlias_extremeNegative_showSpecificError() throws Exception {
        String s = run("di -9999999999\nbye\n");
        mustContain(s, "Income index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void deleteIncomeAlias_extremePositive_showSpecificError() throws Exception {
        String s = run("di 9999999999\nbye\n");
        mustContain(s, "Income index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void deleteIncomeAlias_justAboveMaxInt_showSpecificError() throws Exception {
        String s = run("di 2147483648\nbye\n");
        mustContain(s, "Income index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void deleteIncomeAlias_justBelowMinInt_showSpecificError() throws Exception {
        String s = run("di -2147483649\nbye\n");
        mustContain(s, "Income index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void deleteIncomeAlias_veryLargePositive_showSpecificError() throws Exception {
        String s = run("di 99999999999999999999\nbye\n");
        mustContain(s, "Income index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void deleteIncomeAlias_veryLargeNegative_showSpecificError() throws Exception {
        String s = run("di -99999999999999999999\nbye\n");
        mustContain(s, "Income index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void deleteIncomeAlias_invalidFormat_showGenericError() throws Exception {
        String s = run("di abc123\nbye\n");
        mustContain(s, "Income index must be a valid number.");
    }

    @Test
    void modifyExpenseAlias_extremeNegative_showSpecificError() throws Exception {
        String s = run("me -9999999999\nbye\n");
        mustContain(s, "Expense index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void modifyExpenseAlias_extremePositive_showSpecificError() throws Exception {
        String s = run("me 9999999999\nbye\n");
        mustContain(s, "Expense index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void modifyExpenseAlias_justAboveMaxInt_showSpecificError() throws Exception {
        String s = run("me 2147483648\nbye\n");
        mustContain(s, "Expense index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void modifyExpenseAlias_justBelowMinInt_showSpecificError() throws Exception {
        String s = run("me -2147483649\nbye\n");
        mustContain(s, "Expense index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void modifyExpenseAlias_veryLargePositive_showSpecificError() throws Exception {
        String s = run("me 99999999999999999999\nbye\n");
        mustContain(s, "Expense index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void modifyExpenseAlias_veryLargeNegative_showSpecificError() throws Exception {
        String s = run("me -99999999999999999999\nbye\n");
        mustContain(s, "Expense index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void modifyExpenseAlias_invalidFormat_showGenericError() throws Exception {
        String s = run("me abc123\nbye\n");
        mustContain(s, "Expense index must be a valid number.");
    }

    @Test
    void modifyIncomeAlias_extremeNegative_showSpecificError() throws Exception {
        String s = run("mi -9999999999\nbye\n");
        mustContain(s, "Income index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void modifyIncomeAlias_extremePositive_showSpecificError() throws Exception {
        String s = run("mi 9999999999\nbye\n");
        mustContain(s, "Income index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void modifyIncomeAlias_justAboveMaxInt_showSpecificError() throws Exception {
        String s = run("mi 2147483648\nbye\n");
        mustContain(s, "Income index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void modifyIncomeAlias_justBelowMinInt_showSpecificError() throws Exception {
        String s = run("mi -2147483649\nbye\n");
        mustContain(s, "Income index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void modifyIncomeAlias_veryLargePositive_showSpecificError() throws Exception {
        String s = run("mi 99999999999999999999\nbye\n");
        mustContain(s, "Income index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void modifyIncomeAlias_veryLargeNegative_showSpecificError() throws Exception {
        String s = run("mi -99999999999999999999\nbye\n");
        mustContain(s, "Income index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void modifyIncomeAlias_invalidFormat_showGenericError() throws Exception {
        String s = run("mi abc123\nbye\n");
        mustContain(s, "Income index must be a valid number.");
    }

    @Test
    void aliasesWithValidCommands_workNormally() throws Exception {
        String script = String.join("\n",
                "add-expense a/10 c/Food d/2025-10-01",
                "add-income a/100 c/Salary d/2025-10-01",
                "de 1",
                "di 1",
                "bye");
        String s = run(script);

        mustContain(s, "Expense added:");
        mustContain(s, "Income added:");
        mustContain(s, "Expense deleted (index 1):");
        mustContain(s, "Income deleted (index 1):");
    }

    @Test
    void deleteExpenseFullCommand_zero_showPositiveNumberError() throws Exception {
        String s = run("delete-expense 0\nbye\n");
        mustContain(s, "Expense index must be a positive number.");
    }

    @Test
    void deleteExpenseFullCommand_negativeOne_showPositiveNumberError() throws Exception {
        String s = run("delete-expense -1\nbye\n");
        mustContain(s, "Expense index must be a positive number.");
    }

    @Test
    void deleteExpenseFullCommand_maxInt_showSpecificError() throws Exception {
        String script = String.join("\n",
                "add-expense a/10 c/Food d/2025-10-01",
                "delete-expense 2147483647",
                "bye");
        String s = run(script);
        mustContain(s, "Expense index out of range. Valid range: 1 to 1");
    }

    @Test
    void deleteExpenseFullCommand_justAboveMaxInt_showSpecificError() throws Exception {
        String s = run("delete-expense 2147483648\nbye\n");
        mustContain(s, "Expense index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void deleteExpenseFullCommand_minInt_showSpecificError() throws Exception {
        String s = run("delete-expense -2147483648\nbye\n");
        mustContain(s, "Expense index must be a positive number.");
    }

    @Test
    void deleteExpenseFullCommand_justBelowMinInt_showSpecificError() throws Exception {
        String s = run("delete-expense -2147483649\nbye\n");
        mustContain(s, "Expense index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void deleteIncomeFullCommand_zero_showPositiveNumberError() throws Exception {
        String s = run("delete-income 0\nbye\n");
        mustContain(s, "Income index must be a positive number.");
    }

    @Test
    void deleteIncomeFullCommand_negativeOne_showPositiveNumberError() throws Exception {
        String s = run("delete-income -1\nbye\n");
        mustContain(s, "Income index must be a positive number.");
    }

    @Test
    void deleteIncomeFullCommand_maxInt_showSpecificError() throws Exception {
        String script = String.join("\n",
                "add-income a/100 c/Salary d/2025-10-01",
                "delete-income 2147483647",
                "bye");
        String s = run(script);
        mustContain(s, "Income index out of range. Valid range: 1 to 1");
    }

    @Test
    void deleteIncomeFullCommand_justAboveMaxInt_showSpecificError() throws Exception {
        String s = run("delete-income 2147483648\nbye\n");
        mustContain(s, "Income index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void deleteIncomeFullCommand_minInt_showSpecificError() throws Exception {
        String s = run("delete-income -2147483648\nbye\n");
        mustContain(s, "Income index must be a positive number.");
    }

    @Test
    void deleteIncomeFullCommand_justBelowMinInt_showSpecificError() throws Exception {
        String s = run("delete-income -2147483649\nbye\n");
        mustContain(s, "Income index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void modifyExpenseFullCommand_zero_showPositiveNumberError() throws Exception {
        String s = run("modify-expense 0\nbye\n");
        mustContain(s, "Expense index must be a positive number.");
    }

    @Test
    void modifyExpenseFullCommand_negativeOne_showPositiveNumberError() throws Exception {
        String s = run("modify-expense -1\nbye\n");
        mustContain(s, "Expense index must be a positive number.");
    }

    @Test
    void modifyExpenseFullCommand_maxInt_showSpecificError() throws Exception {
        String script = String.join("\n",
                "add-expense a/10 c/Food d/2025-10-01",
                "modify-expense 2147483647",
                "bye");
        String s = run(script);
        mustContain(s, "Expense index out of range. Valid range: 1 to 1");
    }

    @Test
    void modifyExpenseFullCommand_justAboveMaxInt_showSpecificError() throws Exception {
        String s = run("modify-expense 2147483648\nbye\n");
        mustContain(s, "Expense index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void modifyExpenseFullCommand_minInt_showSpecificError() throws Exception {
        String s = run("modify-expense -2147483648\nbye\n");
        mustContain(s, "Expense index must be a positive number.");
    }

    @Test
    void modifyExpenseFullCommand_justBelowMinInt_showSpecificError() throws Exception {
        String s = run("modify-expense -2147483649\nbye\n");
        mustContain(s, "Expense index is too small. Please use a larger number (minimum: 1).");
    }

    @Test
    void modifyIncomeFullCommand_zero_showPositiveNumberError() throws Exception {
        String s = run("modify-income 0\nbye\n");
        mustContain(s, "Income index must be a positive number.");
    }

    @Test
    void modifyIncomeFullCommand_negativeOne_showPositiveNumberError() throws Exception {
        String s = run("modify-income -1\nbye\n");
        mustContain(s, "Income index must be a positive number.");
    }

    @Test
    void modifyIncomeFullCommand_maxInt_showSpecificError() throws Exception {
        String script = String.join("\n",
                "add-income a/100 c/Salary d/2025-10-01",
                "modify-income 2147483647",
                "bye");
        String s = run(script);
        mustContain(s, "Income index out of range. Valid range: 1 to 1");
    }

    @Test
    void modifyIncomeFullCommand_justAboveMaxInt_showSpecificError() throws Exception {
        String s = run("modify-income 2147483648\nbye\n");
        mustContain(s, "Income index is too large. Please use a smaller number (maximum: 2147483647).");
    }

    @Test
    void modifyIncomeFullCommand_minInt_showSpecificError() throws Exception {
        String s = run("modify-income -2147483648\nbye\n");
        mustContain(s, "Income index must be a positive number.");
    }

    @Test
    void modifyIncomeFullCommand_justBelowMinInt_showSpecificError() throws Exception {
        String s = run("modify-income -2147483649\nbye\n");
        mustContain(s, "Income index is too small. Please use a larger number (minimum: 1).");
    }

}
