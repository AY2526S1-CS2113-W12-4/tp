package seedu.fintrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.IncomeCategory;

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


    /*@Test
    void printBalance_formatsToTwoDecimalPlacesAndLabels() {
        Ui.printBalance(1234.5, 2000.0, 765.4321);
        String ls = System.lineSeparator();
        String expected = ""
                + "Overall Balance: 1234.50" + ls
                + "  Total Income:  2000.00" + ls
                + "  Total Expense: 765.43" + ls;
        assertEquals(expected, out());
    }*/

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
        assertEquals("list-expense", Ui.LIST_EXPENSE_COMMAND);
        assertEquals("bye", Ui.EXIT_COMMAND);
    }

    @Test
    void prefixConstants_haveExpectedValues() {
        assertEquals("a/", Ui.AMOUNT_PREFIX);
        assertEquals("c/", Ui.CATEGORY_PREFIX);
        assertEquals("d/", Ui.DATE_PREFIX);
        assertEquals("des/", Ui.DESCRIPTION_PREFIX);
    }

    @Test
    void printIncomeAdded_withDescription_printsAllFields() {
        Income inc = new Income(123.0, IncomeCategory.SALARY, LocalDate.parse("2025-10-01"), "Oct pay");
        Ui.printIncomeAdded(inc);

        String ls = System.lineSeparator();
        String expected = ""
                + "Income added:" + ls
                + "  Amount: 123.00" + ls
                + "  Category: SALARY" + ls
                + "  Date: 2025-10-01" + ls
                + "  Description: Oct pay" + ls;

        assertEquals(expected, out());
    }

    @Test
    void printIncomeAdded_withoutDescription_whenNull() {
        Income inc = new Income(50.0, IncomeCategory.GIFT, LocalDate.parse("2025-10-07"), null);
        Ui.printIncomeAdded(inc);

        String ls = System.lineSeparator();
        String expected = ""
                + "Income added:" + ls
                + "  Amount: 50.00" + ls
                + "  Category: GIFT" + ls
                + "  Date: 2025-10-07" + ls;

        assertEquals(expected, out());
        assertFalse(out().contains("Description:"));
    }

    @Test
    void printIncomeAdded_blankDescription_omitted() {
        // Income normalises blank -> null
        Income inc = new Income(75.5, IncomeCategory.SALARY, LocalDate.parse("2025-10-07"), "   ");
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
        Income inc = new Income(100.0, IncomeCategory.SALARY, LocalDate.parse("2025-10-05"), "Bonus");
        Ui.printIncomeDeleted(inc, 2);

        String ls = System.lineSeparator();
        String expected = ""
                + "Income deleted (index 2):" + ls
                + "  Amount: 100.00" + ls
                + "  Category: SALARY" + ls
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
    void printListOfIncomes_formatsOutputCorrectly() {
        var i1 = new Income(200.0, IncomeCategory.SALARY,
                LocalDate.parse("2025-01-15"), null);
        var i2 = new Income(4800.0, IncomeCategory.SALARY,
                LocalDate.parse("2025-02-01"), "Monthly salary");

        Ui.printListOfIncomes(java.util.List.of(i1, i2));

        String output = out();
        assertTrue(output.contains("Incomes (Newest first):"));
        assertTrue(output.contains("Amount: $200.00"));
        assertTrue(output.contains("Category: SALARY"));
        assertTrue(output.contains("Date: 2025-01-15"));
        assertTrue(output.contains("#1")); // numbered output present
    }

    @Test
    void printListOfIncomes_usesNewestFirstWhenProvidedFromModel() {
        // Build a proper IncomeList so ordering is reverse-chronological.
        var list = new seedu.fintrack.model.IncomeList();
        var jan = new Income(200.0, IncomeCategory.SALARY,
                LocalDate.parse("2025-01-15"), null);
        var feb = new Income(4800.0, IncomeCategory.SALARY,
                LocalDate.parse("2025-02-01"), "Monthly salary");
        list.add(jan);
        list.add(feb); // newer

        Ui.printListOfIncomes(list.asUnmodifiableView());
        String s = out();

        int febPos = s.indexOf("Date: 2025-02-01");
        int janPos = s.indexOf("Date: 2025-01-15");
        assertTrue(febPos >= 0 && janPos >= 0 && febPos < janPos);
        assertTrue(s.contains("Amount: $4800.00"));
        assertTrue(s.contains("Description: Monthly salary"));
    }

    @Test
    void printListOfIncomes_emptyList_showsNoIncomesMessage() {
        Ui.printListOfIncomes(java.util.List.of());
        assertTrue(out().contains("No incomes recorded."));
    }

    @Test
    void printBudgets_empty_showsNoBudgetsMessage() {
        Ui.printBudgets(java.util.Collections.emptyMap());
        assertTrue(out().contains("No budgets have been set."));
    }

    @Test
    void printBudgets_populated_printsDollarValues() {
        java.util.Map<seedu.fintrack.model.ExpenseCategory, Double> budgets =
                new java.util.HashMap<>();
        budgets.put(seedu.fintrack.model.ExpenseCategory.RENT, 1500.0);
        budgets.put(seedu.fintrack.model.ExpenseCategory.FOOD, 123.45);

        Ui.printBudgets(budgets);
        String s = out();
        assertTrue(s.contains("Current Budgets:"));
        assertTrue(s.contains("RENT"));
        assertTrue(s.contains("$1500.00"));
        assertTrue(s.contains("FOOD"));
        assertTrue(s.contains("$123.45"));
    }

    @Test
    void printBudgetExceededWarning_showsBannerAndNumbers() {
        Ui.printBudgetExceededWarning(
                ExpenseCategory.FOOD, 100.0, 150.0);
        String s = out();
        assertTrue(s.contains("BUDGET ALERT"));
        assertTrue(s.contains("budget of $100.00"));
        assertTrue(s.contains("spent a total of $150.00"));
    }

    @Test
    void printHorizontalLine_printsExactCount() {
        String ls = System.lineSeparator();
        Ui.printHorizontalLine(5);
        assertEquals("-----" + ls, out());
    }

    @Test
    void printHelp_containsAllMainCommands() {
        Ui.printHelp();
        String out = out();
        assertTrue(out.contains("FinTrack Command Summary"));
        assertTrue(out.contains(Ui.ADD_EXPENSE_COMMAND));
        assertTrue(out.contains(Ui.LIST_EXPENSE_COMMAND));
        assertTrue(out.contains(Ui.ADD_INCOME_COMMAND));
        assertTrue(out.contains(Ui.LIST_INCOME_COMMAND));
        assertTrue(out.contains(Ui.DELETE_EXPENSE_COMMAND));
        assertTrue(out.contains(Ui.DELETE_INCOME_COMMAND));
        assertTrue(out.contains(Ui.BALANCE_COMMAND));
        assertTrue(out.contains(Ui.MODIFY_EXPENSE_COMMAND));
        assertTrue(out.contains(Ui.MODIFY_INCOME_COMMAND));
        assertTrue(out.contains(Ui.BUDGET_COMMAND));
        assertTrue(out.contains(Ui.LIST_BUDGET_COMMAND));
        assertTrue(out.contains(Ui.SUMMARY_EXPENSE_COMMAND));
        assertTrue(out.contains(Ui.SUMMARY_INCOME_COMMAND));
        assertTrue(out.contains(Ui.TIPS_COMMAND));
        assertTrue(out.contains(Ui.HELP_COMMAND));
        assertTrue(out.contains(Ui.EXPORT_COMMAND));
        assertTrue(out.contains(Ui.EXIT_COMMAND));
    }

    @Test
    void printExpenseByCategory_normalCase_printsLinesAndTopCategory() {
        double totalExpense = 17.50; // 12.50 + 5.00
        Map<ExpenseCategory, Double> testMap = new HashMap<>();
        testMap.put(ExpenseCategory.FOOD, 12.50);
        testMap.put(ExpenseCategory.TRANSPORT, 5.00);
        Map<ExpenseCategory, Double> testPercentMap = new HashMap<>();
        testPercentMap.put(ExpenseCategory.FOOD, 12.50/totalExpense * 100.0);
        testPercentMap.put(ExpenseCategory.TRANSPORT, 5.00/totalExpense * 100.0);

        Ui.printExpenseByCategory(totalExpense, testMap, testPercentMap);
        String out = out();

        assertTrue(out.contains("FOOD: 12.50 (71.43%)"));
        assertTrue(out.contains("TRANSPORT: 5.00 (28.57%)"));
        assertTrue(out.contains("Your most spent on category is: FOOD"));
    }

    @Test
    void printExpenseByCategory_emptyOrNegative_printsMessage() {
        Ui.printExpenseByCategory(0.0, Map.of(), Map.of());
        String out1 = out();
        assertTrue(out1.contains("You have not spent anything yet!"));

        Ui.printExpenseByCategory(-1.0, Map.of(ExpenseCategory.FOOD, 10.0), Map.of());
        String out2 = out();
        assertTrue(out2.contains("You have not spent anything yet!"));
    }

    @Test
    void printSummaryExpense_printsHeaderTotalBreakdownAndFooter() {
        double totalExpense = 17.50;
        Map<ExpenseCategory, Double> testMap = new HashMap<>();
        testMap.put(ExpenseCategory.FOOD, 12.50);
        testMap.put(ExpenseCategory.TRANSPORT, 5.00);
        Map<ExpenseCategory, Double> testPercentMap = new HashMap<>();
        testPercentMap.put(ExpenseCategory.FOOD, 12.50/totalExpense * 100.0);
        testPercentMap.put(ExpenseCategory.TRANSPORT, 5.00/totalExpense * 100.0);

        Ui.printSummaryExpense(totalExpense, testMap, testPercentMap);
        String out = out();

        assertTrue(out.contains("Here is an overall summary of your expenses!"));
        assertTrue(out.contains("Total Expense: 17.5"));

        assertTrue(out.contains("Here is a breakdown of your expense:"));
        assertTrue(out.contains("FOOD: 12.50 (71.43%)"));
        assertTrue(out.contains("TRANSPORT: 5.00 (28.57%)"));
        assertTrue(out.contains("Your most spent on category is: FOOD"));
    }

    @Test
    void printSummaryExpense_printsHeaderToNoExpenseMessage() {
        Map<ExpenseCategory, Double> testMap = new HashMap<>();
        Ui.printSummaryExpense(0.0, testMap, testMap);
        String out = out();

        assertTrue(out.contains("Here is an overall summary of your expenses!"));
        assertTrue(out.contains("Total Expense: 0.0"));

        assertTrue(out.contains("Here is a breakdown of your expense:"));
        assertTrue(out.contains("You have not spent anything yet!"));
    }

    @Test
    void printSummaryExpense_zeroTotalsWithEntries_doesNotPrintNaN() {
        Map<ExpenseCategory, Double> testMap = Map.of(ExpenseCategory.FOOD, 0.0);
        Ui.printSummaryExpense(0.0, testMap, testMap);
        String out = out();

        assertTrue(out.contains("You have not spent anything yet!"));
        assertFalse(out.contains("NaN"));
    }

    @Test
    void printIncomeByCategory_normalCase_printsLinesAndTopSource() {
        double totalIncome = 1550.50;
        Map<IncomeCategory, Double> testMap = new HashMap<>();
        testMap.put(IncomeCategory.SALARY, 1500.00);
        testMap.put(IncomeCategory.INVESTMENT, 50.50);
        Map<IncomeCategory, Double> testPercentMap = new HashMap<>();
        testPercentMap.put(IncomeCategory.SALARY, 1500/totalIncome * 100.0);
        testPercentMap.put(IncomeCategory.INVESTMENT, 50.50/totalIncome * 100.0);

        Ui.printIncomeByCategory(totalIncome, testMap, testPercentMap);
        String out = out();

        assertTrue(out.contains("SALARY: 1500.00 (96.74%)"));
        assertTrue(out.contains("INVESTMENT: 50.50 (3.26%)"));
        assertTrue(out.contains("Your highest source of income is: SALARY"));
    }

    @Test
    void printIncomeByCategory_emptyOrNegative_printsFriendlyMessage() {
        Ui.printIncomeByCategory(0.0, Map.of(), Map.of());
        String out1 = out();
        assertTrue(out1.contains("You have not recorded any income yet!"));

        Ui.printIncomeByCategory(-1.0, Map.of(IncomeCategory.SALARY, 1.0), Map.of());
        String out2 = out();
        assertTrue(out2.contains("You have not recorded any income yet!"));
    }

    @Test
    void printSummaryIncome_printsHeaderTotalBreakdownAndFooter() {
        double totalIncome = 1550.50;
        Map<IncomeCategory, Double> testMap = new HashMap<>();
        testMap.put(IncomeCategory.SALARY, 1500.00);
        testMap.put(IncomeCategory.INVESTMENT, 50.50);
        Map<IncomeCategory, Double> testPercentMap = new HashMap<>();
        testPercentMap.put(IncomeCategory.SALARY, 1500/totalIncome * 100.0);
        testPercentMap.put(IncomeCategory.INVESTMENT, 50.50/totalIncome * 100.0);

        Ui.printSummaryIncome(totalIncome, testMap, testPercentMap);
        String out = out();

        assertTrue(out.contains("Here is an overall summary of your income!"));
        assertTrue(out.contains("Total Income: 1550.5"));
        assertTrue(out.contains("Here is a breakdown of your income:"));

        assertTrue(out.contains("SALARY: 1500.00 (96.74%)"));
        assertTrue(out.contains("INVESTMENT: 50.50 (3.26%)"));
        assertTrue(out.contains("Your highest source of income is: SALARY"));
    }

    @Test
    void printSummaryExpense_printsHeaderToNoIncomeMessage() {
        Map<IncomeCategory, Double> testMap = new HashMap<>();
        Ui.printSummaryIncome(0.0, testMap, testMap);
        String out = out();

        assertTrue(out.contains("Here is an overall summary of your income!"));
        assertTrue(out.contains("Total Income: 0.0"));

        assertTrue(out.contains("Here is a breakdown of your income:"));
        assertTrue(out.contains("You have not recorded any income yet!"));
    }

    @Test
    void printSummaryIncome_zeroTotalsWithEntries_doesNotPrintNaN() {
        Map<IncomeCategory, Double> testMap = Map.of(IncomeCategory.GIFT, 0.0);
        Ui.printSummaryIncome(0.0, testMap, testMap);
        String out = out();

        assertTrue(out.contains("You have not recorded any income yet!"));
        assertFalse(out.contains("NaN"));
    }

    @Test
    void printTip_printsKnownTips() {
        String knownTip1 = "Don't buy a Mac especially if you are doing EE2026!";
        String knownTip2 = "If you stay on campus, Dining Credits are now available to use in NUS food courts!";
        String knownTip3 = "Keep a lookout for free welfare as exam period is approaching!";
        String knownTip4 = "Remember to track your expenses daily!";
        String knownTip5 = "Take the shuttle bus, it's worth it :(";
        String knownTip6 = "OpenAI credits are too expensive!";
        String knownTip7 = "The Deck banmian is less than $4!";
        String knownTip8 = "You can spend the pass royale money on better food ._.";

        for (int i = 0; i < 1000; i++) {
            Ui.printTip();
        }

        String out = out();
        assertTrue(out.contains(knownTip1));
        assertTrue(out.contains(knownTip2));
        assertTrue(out.contains(knownTip3));
        assertTrue(out.contains(knownTip4));
        assertTrue(out.contains(knownTip5));
        assertTrue(out.contains(knownTip6));
        assertTrue(out.contains(knownTip7));
        assertTrue(out.contains(knownTip8));
    }

    @Test
    void printTip_neverPrintsFakeTip() {
        String fakeTip = "Spend all your money!";
        for (int i = 0; i < 1000; i++) {
            Ui.printTip();
        }

        String out = out();
        assertFalse(out.contains(fakeTip));
    }
}
