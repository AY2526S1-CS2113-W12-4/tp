package seedu.fintrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;

/**
 * Tests printing and constants in Ui, including add/delete entry outputs.
 */
public class UiTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;
    private Locale originalLocale;

    @BeforeEach
    void setUp() {
        originalLocale = Locale.getDefault();
        // Ensure decimal dot for String.format("%.2f", ...)
        Locale.setDefault(Locale.US);

        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        Locale.setDefault(originalLocale);
    }

    private String out() {
        return outContent.toString();
    }

    @Test
    void printWelcome_printsBannerAndHelpHint() {
        Ui.printWelcome();
        String s = out();
        assertTrue(s.contains("Welcome to FinTrack!"));
        assertTrue(s.contains("Type 'help' for available commands."));
    }

    @Test
    void printExit_hasTrailingNewline() {
        Ui.printExit();
        assertEquals("Bye. Hope to see you again soon!" + System.lineSeparator(), out());
    }


    @Test
    void printBalance_formatsToTwoDecimalPlacesAndLabels() {
        Ui.printBalance(1234.5, 2000.0, 765.4321);
        String ls = System.lineSeparator();
        String expected = ""
                + "Overall Balance: 1234.50" + ls
                + "  Total Income:  2000.00" + ls
                + "  Total Expense: 765.43" + ls;
        assertEquals(expected, out());
    }

    @Test
    void printError_prefixesWithError() {
        Ui.printError("Something went wrong");
        assertEquals("Error: Something went wrong" + System.lineSeparator(), out());
    }

    @Test
    void commandConstants_haveExpectedValues() {
        assertEquals("help", Ui.HELP_COMMAND);
        assertEquals("add-expense", Ui.ADD_EXPENSE_COMMAND);
        assertEquals("add-income", Ui.ADD_INCOME_COMMAND);
        assertEquals("delete-expense", Ui.DELETE_EXPENSE_COMMAND);
        assertEquals("delete-income", Ui.DELETE_INCOME_COMMAND);
        assertEquals("balance", Ui.BALANCE_COMMAND);
        assertEquals("list", Ui.LIST_COMMAND);
        assertEquals("bye", Ui.EXIT_COMMAND);
    }

    @Test
    void prefixConstants_haveExpectedValues() {
        assertEquals("a/", Ui.AMOUNT_PREFIX);
        assertEquals("c/", Ui.CATEGORY_PREFIX);
        assertEquals("d/", Ui.DATE_PREFIX);
        assertEquals("desc/", Ui.DESCRIPTION_PREFIX);
    }

    @Test
    void printIncomeAdded_withDescription_printsAllFields() {
        Income inc = new Income(123.0, "Salary", LocalDate.parse("2025-10-01"), "Oct pay");
        Ui.printIncomeAdded(inc);

        String ls = System.lineSeparator();
        String expected = ""
                + "Income added:" + ls
                + "  Amount: 123.00" + ls
                + "  Category: Salary" + ls
                + "  Date: 2025-10-01" + ls
                + "  Description: Oct pay" + ls;

        assertEquals(expected, out());
    }

    @Test
    void printIncomeAdded_withoutDescription_whenNull() {
        Income inc = new Income(50.0, "Gift", LocalDate.parse("2025-10-07"), null);
        Ui.printIncomeAdded(inc);

        String ls = System.lineSeparator();
        String expected = ""
                + "Income added:" + ls
                + "  Amount: 50.00" + ls
                + "  Category: Gift" + ls
                + "  Date: 2025-10-07" + ls;

        assertEquals(expected, out());
        assertFalse(out().contains("Description:"));
    }

    @Test
    void printIncomeAdded_blankDescription_omitted() {
        // Income normalises blank -> null
        Income inc = new Income(75.5, "Other", LocalDate.parse("2025-10-07"), "   ");
        Ui.printIncomeAdded(inc);
        assertFalse(out().contains("Description:"));
    }

    @Test
    void printExpenseAdded_withDescription_printsAllFields() {
        Expense ex = new Expense(9.5, ExpenseCategory.parse("Food"), LocalDate.parse("2025-10-02"), "Lunch");
        Ui.printExpenseAdded(ex);

        String ls = System.lineSeparator();
        String expected = ""
                + "Expense added:" + ls
                + "  Amount: 9.50" + ls
                + "  Category: FOOD" + ls
                + "  Date: 2025-10-02" + ls
                + "  Description: Lunch" + ls;

        assertEquals(expected, out());
    }

    @Test
    void printExpenseAdded_withoutDescription_whenNull() {
        Expense ex = new Expense(12.3, ExpenseCategory.TRANSPORT, LocalDate.parse("2025-10-03"), null);
        Ui.printExpenseAdded(ex);

        String text = out();
        assertTrue(text.contains("Expense added:"));
        assertTrue(text.contains("Amount: 12.30"));
        assertTrue(text.contains("Category: TRANSPORT"));
        assertTrue(text.contains("Date: 2025-10-03"));
        assertFalse(text.contains("Description:"));
    }

    @Test
    void printExpenseAdded_blankDescription_omitted() {
        Expense ex = new Expense(12.3, ExpenseCategory.TRANSPORT, LocalDate.parse("2025-10-03"), "   ");
        Ui.printExpenseAdded(ex);
        assertFalse(out().contains("Description:"));
    }

    @Test
    void printIncomeDeleted_withIndex_andDescription() {
        Income inc = new Income(100.0, "Salary", LocalDate.parse("2025-10-05"), "Bonus");
        Ui.printIncomeDeleted(inc, 2);

        String ls = System.lineSeparator();
        String expected = ""
                + "Income deleted (index 2):" + ls
                + "  Amount: 100.00" + ls
                + "  Category: Salary" + ls
                + "  Date: 2025-10-05" + ls
                + "  Description: Bonus" + ls;

        assertEquals(expected, out());
    }

    @Test
    void printExpenseDeleted_withoutDescription() {
        Expense ex = new Expense(7.0, ExpenseCategory.FOOD, LocalDate.parse("2025-10-06"), null);
        Ui.printExpenseDeleted(ex, 1);

        String ls = System.lineSeparator();
        String expected = ""
                + "Expense deleted (index 1):" + ls
                + "  Amount: 7.00" + ls
                + "  Category: FOOD" + ls
                + "  Date: 2025-10-06" + ls;

        assertEquals(expected, out());
        assertFalse(out().contains("Description:"));
    }

    @Test
    void printListOfExpenses_formatsOutputCorrectly() {
        var e1 = new Expense(10.0, ExpenseCategory.parse("Food"), LocalDate.parse("2025-10-05"), "Lunch");
        var e2 = new Expense(20.0, ExpenseCategory.parse("Transport"), LocalDate.parse("2025-10-08"), "Grab");

        Ui.printListOfExpenses(java.util.List.of(e1, e2));

        String output = out();
        assertTrue(output.contains("Expenses (Newest first):"));
        assertTrue(output.contains("Amount: $10.00"));
        assertTrue(output.contains("Category: FOOD"));
        assertTrue(output.contains("Date: 2025-10-05"));
        assertTrue(output.contains("Description: Lunch"));
        assertTrue(output.contains("#1")); // numbered output
    }

    @Test
    void printListOfExpenses_emptyList_showsNoExpensesMessage() {
        Ui.printListOfExpenses(java.util.List.of());
        assertTrue(out().contains("No expenses recorded."));
    }

    @Test
    void printHelp_containsAllMainCommands() {
        Ui.printHelp();
        String s = out();
        assertTrue(s.contains("FinTrack Command Summary"));
        assertTrue(s.contains(Ui.ADD_EXPENSE_COMMAND));
        assertTrue(s.contains(Ui.ADD_INCOME_COMMAND));
        assertTrue(s.contains(Ui.DELETE_EXPENSE_COMMAND));
        assertTrue(s.contains(Ui.LIST_COMMAND));
        assertTrue(s.contains(Ui.BALANCE_COMMAND));
        assertTrue(s.contains(Ui.EXIT_COMMAND));
    }
}
