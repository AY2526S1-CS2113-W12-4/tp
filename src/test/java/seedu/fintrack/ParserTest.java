package seedu.fintrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.IncomeCategory;

/**
 * Contains JUnit tests for the {@link Parser} class.
 * This class tests the parsing logic for various user commands,
 * including valid inputs, invalid inputs, and edge cases.
 */
public class ParserTest {

    // ============ Helpers for command strings ============

    /**
     * Constructs a valid 'add-expense' command string for testing.
     *
     * @param amount The amount of the expense.
     * @param category The category of the expense.
     * @param date The date of the expense in 'YYYY-MM-DD' format.
     * @param desc The description of the expense. Can be null for no description.
     * @return A formatted 'add-expense' command string.
     */
    private static String addExpense(String amount, String category, String date, String desc) {
        String base = Ui.ADD_EXPENSE_COMMAND + " "
                + Ui.AMOUNT_PREFIX + amount + " "
                + Ui.CATEGORY_PREFIX + category + " "
                + Ui.DATE_PREFIX + date;

        if (desc != null) {
            base += " " + Ui.DESCRIPTION_PREFIX + desc;
        }

        return base;
    }

    /**
     * Constructs a valid 'add-income' command string for testing.
     *
     * @param amount The amount of the income.
     * @param category The category of the income.
     * @param date The date of the income in 'YYYY-MM-DD' format.
     * @param desc The description of the income. Can be null for no description.
     * @return A formatted 'add-income' command string.
     */
    private static String addIncome(String amount, String category, String date, String desc) {
        String base = Ui.ADD_INCOME_COMMAND + " "
                + Ui.AMOUNT_PREFIX + amount + " "
                + Ui.CATEGORY_PREFIX + category + " "
                + Ui.DATE_PREFIX + date;
        if (desc != null) {
            base += " " + Ui.DESCRIPTION_PREFIX + desc;
        }
        return base;
    }

    /**
     * Constructs a 'budget' command string for testing.
     *
     * @param category The category for the budget.
     * @param amount The amount for the budget.
     * @return A formatted 'budget' command string.
     */
    private static String setBudget(String category, String amount) {
        return Ui.BUDGET_COMMAND + " "
                + Ui.CATEGORY_PREFIX + category + " "
                + Ui.AMOUNT_PREFIX + amount;
    }

    /// ============ Tests for returnFirstWord ============

    /**
     * Tests {@code returnFirstWord} with a standard command string without leading spaces.
     * Expects the command word to be extracted correctly.
     */
    @Test
    public void returnFirstWord_basic_noLeadingSpaces() {
        String input = Ui.ADD_EXPENSE_COMMAND + " a/12.3 c/Food d/2025-10-01";
        String first = Parser.returnFirstWord(input);
        assertEquals(Ui.ADD_EXPENSE_COMMAND, first);
    }

    /**
     * Tests {@code returnFirstWord} with a single-word input.
     * Expects the method to return the entire string.
     */
    @Test
    public void returnFirstWord_noSpace_returnsWhole() {
        String input = "help";
        String first = Parser.returnFirstWord(input);
        assertEquals("help", first);
    }

    /**
     * Tests {@code returnFirstWord} with a command string that has leading spaces.
     * Expects the spaces to be ignored and the command word correctly extracted.
     */
    @Test
    public void returnFirstWord_leadingSpaces_returnsCommandWord() {
        String input = "  " + Ui.ADD_INCOME_COMMAND + " a/1 c/Salary d/2025-10-01";
        String first = Parser.returnFirstWord(input);
        assertEquals(Ui.ADD_INCOME_COMMAND, first);
    }

    // ============ Test input handling for parseAddExpense ============

    /**
     * Tests parsing of a valid 'add-expense' command with an optional description.
     * Expects a correctly configured {@link Expense} object.
     */
    @Test
    public void parseAddExpense_valid_withDescription() {
        String input = addExpense("12.50", "food", "2025-10-01", "lunch");
        Expense e = Parser.parseAddExpense(input);

        assertEquals(12.50, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.FOOD, e.getCategory());
        assertEquals(LocalDate.of(2025, 10, 1), e.getDate());
        assertEquals("lunch", e.getDescription());
    }

    /**
     * Ensures descriptions can contain prefix-like substrings without being truncated.
     */
    @Test
    public void parseAddExpense_descriptionContainsPrefix_textPreserved() {
        String input = addExpense("12.50", "food", "2025-10-01", "Lunch with c/friends");
        Expense e = Parser.parseAddExpense(input);
        assertEquals("Lunch with c/friends", e.getDescription());
    }

    /**
     * Ensures descriptions with embedded spaces and prefix-like substrings remain intact.
     */
    @Test
    public void parseAddExpense_descriptionContainsPrefixWithSpaces_textPreserved() {
        String input = addExpense("12.50", "food", "2025-10-01",
                "Lunch with c/      friends");
        Expense e = Parser.parseAddExpense(input);
        assertEquals("Lunch with c/      friends", e.getDescription());
    }

    /**
     * Tests parsing of a valid 'add-expense' command without an optional description.
     * Expects the description field of the resulting {@link Expense} object to be an empty string.
     */
    @Test
    public void parseAddExpense_validWithoutDescription_descriptionNull() {
        String input = addExpense("10", "Transport", "2025-01-02", null);
        Expense e = Parser.parseAddExpense(input);

        assertEquals(10.0, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.TRANSPORT, e.getCategory());
        assertEquals(LocalDate.of(2025, 1, 2), e.getDate());
        assertEquals(null, e.getDescription());
    }

    /**
     * Tests 'add-expense' parsing with missing required parameters.
     * Expects an {@link IllegalArgumentException} with a specific message for each case.
     */
    @Test
    public void parseAddExpense_missingParams_throws() {
        String input1 = Ui.ADD_EXPENSE_COMMAND;
        String input2 = Ui.ADD_EXPENSE_COMMAND + " " + Ui.AMOUNT_PREFIX + "10 " + Ui.CATEGORY_PREFIX + "Food";
        String input3 = Ui.ADD_EXPENSE_COMMAND + " " + Ui.CATEGORY_PREFIX + "Food " + Ui.DATE_PREFIX + "2025-10-01";
        String input4 = Ui.ADD_EXPENSE_COMMAND + " " + Ui.AMOUNT_PREFIX + "10 " + Ui.DATE_PREFIX + "2025-10-01";

        // Missing everything after command
        try {
            Parser.parseAddExpense(input1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Missing parameters. See 'help'.", e.getMessage());
        }

        // Missing date
        try {
            Parser.parseAddExpense(input2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.", e.getMessage());
        }

        // Missing amount
        try {
            Parser.parseAddExpense(input3);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.", e.getMessage());
        }

        // Missing category
        try {
            Parser.parseAddExpense(input4);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.", e.getMessage());
        }
    }

    /**
     * Tests 'add-expense' parsing when required prefixes are present but their values are empty.
     * This tests the behavior of the underlying {@code getValue} method.
     * Expects an {@link IllegalArgumentException} for each case.
     */
    @Test
    public void parseAddExpense_emptyParams_throws() {
        // Missing amount value
        String input1 = Ui.ADD_EXPENSE_COMMAND + " a/ c/Food d/2025-10-10";
        // Missing category value
        String input2 = Ui.ADD_EXPENSE_COMMAND + " a/10 c/ d/2025-10-10";
        // Missing date value
        String input3 = Ui.ADD_EXPENSE_COMMAND + " a/10 c/Food d/";

        String expectedMsg = "Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.";

        try {
            Parser.parseAddExpense(input1);
            fail("Should have failed due to empty amount");
        } catch (IllegalArgumentException e) {
            assertEquals(expectedMsg, e.getMessage());
        }

        try {
            Parser.parseAddExpense(input2);
            fail("Should have failed due to empty category");
        } catch (IllegalArgumentException e) {
            assertEquals(expectedMsg, e.getMessage());
        }

        try {
            Parser.parseAddExpense(input3);
            fail("Should have failed due to empty date");
        } catch (IllegalArgumentException e) {
            assertEquals(expectedMsg, e.getMessage());
        }
    }

    /**
     * add-expense should tolerate tabs/multiple spaces between prefixes and values.
     */
    @Test
    public void parseAddExpense_whitespaceVariants_ok() {
        String input = Ui.ADD_EXPENSE_COMMAND + "\t"
                + Ui.AMOUNT_PREFIX + "12.34  "
                + Ui.CATEGORY_PREFIX + "Food\t"
                + Ui.DATE_PREFIX + "2025-10-09  "
                + Ui.DESCRIPTION_PREFIX + "messy spacing";

        Expense e = Parser.parseAddExpense(input);
        assertEquals(12.34, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.FOOD, e.getCategory());
        assertEquals(LocalDate.of(2025, 10, 9), e.getDate());
        assertEquals("messy spacing", e.getDescription());
    }

    /**
     * add-expense should accept compulsory parameters in any order because prefixes drive parsing.
     */
    @Test
    public void parseAddExpense_parameterOrder_ok() {
        String input = Ui.ADD_EXPENSE_COMMAND + " "
                + Ui.DATE_PREFIX + "2025-10-10 "
                + Ui.AMOUNT_PREFIX + "9.99 "
                + Ui.CATEGORY_PREFIX + "Transport "
                + Ui.DESCRIPTION_PREFIX + "Out-of-order prefixes ";

        Expense e = Parser.parseAddExpense(input);
        assertEquals(9.99, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.TRANSPORT, e.getCategory());
        assertEquals(LocalDate.of(2025, 10, 10), e.getDate());
        assertEquals("Out-of-order prefixes", e.getDescription());
    }

    /**
     * add-expense should reject NaN or infinite amounts.
     */
    @Test
    public void parseAddExpense_nonFiniteAmount_throws() {
        String[] inputs = {
            Ui.ADD_EXPENSE_COMMAND + " "
                    + Ui.AMOUNT_PREFIX + "NaN "
                    + Ui.CATEGORY_PREFIX + "Food "
                    + Ui.DATE_PREFIX + "2025-10-10",
            Ui.ADD_EXPENSE_COMMAND + " "
                    + Ui.AMOUNT_PREFIX + "Infinity "
                    + Ui.CATEGORY_PREFIX + "Food "
                    + Ui.DATE_PREFIX + "2025-10-10",
            Ui.ADD_EXPENSE_COMMAND + " "
                    + Ui.AMOUNT_PREFIX + "-Infinity "
                    + Ui.CATEGORY_PREFIX + "Food "
                    + Ui.DATE_PREFIX + "2025-10-10"
        };

        for (String input : inputs) {
            try {
                Parser.parseAddExpense(input);
                fail();
            } catch (IllegalArgumentException e) {
                assertEquals("Amount must be finite.", e.getMessage());
            }
        }
    }

    /**
     * add-expense should reject descriptions that appear before other prefixes.
     */
    @Test
    public void parseAddExpense_descriptionNotLast_throws() {
        String input = Ui.ADD_EXPENSE_COMMAND + " "
                + Ui.DESCRIPTION_PREFIX + "Should fail "
                + Ui.AMOUNT_PREFIX + "12 "
                + Ui.CATEGORY_PREFIX + "Food "
                + Ui.DATE_PREFIX + "2025-10-10";

        try {
            Parser.parseAddExpense(input);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Description (des/) must be the last parameter.", e.getMessage());
        }
    }

    /**
     * Description should survive multiple prefix-like tokens when placed last.
     */
    @Test
    public void parseAddExpense_descriptionWithMultiplePrefixes_textPreserved() {
        String input = addExpense(
                "18.90",
                "food",
                "2025-10-12",
                "Gym c/membership renewal a/15 d/2025-10-12 c/MISC");

        Expense e = Parser.parseAddExpense(input);
        assertEquals("Gym c/membership renewal a/15 d/2025-10-12 c/MISC", e.getDescription());
    }

    /**
     * Helper method to construct a valid 'modify-expense' command string for testing.
     *
     * @param index The index of the expense to modify.
     * @param amount The new amount of the expense.
     * @param category The new category of the expense.
     * @param date The new date of the expense in 'YYYY-MM-DD' format.
     * @param desc The new description of the expense. Can be null for no description.
     * @return A formatted 'modify-expense' command string.
     */
    private static String modifyExpense(String index, String amount, String category, String date, String desc) {
        String base = Ui.MODIFY_EXPENSE_COMMAND + " " + index + " "
                + Ui.AMOUNT_PREFIX + amount + " "
                + Ui.CATEGORY_PREFIX + category + " "
                + Ui.DATE_PREFIX + date;
        if (desc != null) {
            base += " " + Ui.DESCRIPTION_PREFIX + desc;
        }
        return base;
    }

    /**
     * Helper method to construct a valid 'modify-income' command string for testing.
     *
     * @param index The index of the income to modify.
     * @param amount The new amount of the income.
     * @param category The new category of the income.
     * @param date The new date of the income in 'YYYY-MM-DD' format.
     * @param desc The new description of the income. Can be null for no description.
     * @return A formatted 'modify-income' command string.
     */
    private static String modifyIncome(String index, String amount, String category, String date, String desc) {
        String base = Ui.MODIFY_INCOME_COMMAND + " " + index + " "
                + Ui.AMOUNT_PREFIX + amount + " "
                + Ui.CATEGORY_PREFIX + category + " "
                + Ui.DATE_PREFIX + date;
        if (desc != null) {
            base += " " + Ui.DESCRIPTION_PREFIX + desc;
        }
        return base;
    }

    /**
     * Tests parsing index from a valid 'modify-expense' command.
     */
    @Test
    public void parseModifyExpenseIndex_valid_returnsCorrectIndex() {
        String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 a/50 c/FOOD d/2025-10-01";
        int index = Parser.parseModifyExpenseIndex(input);
        assertEquals(1, index);

        input = Ui.MODIFY_EXPENSE_COMMAND + " 42";
        index = Parser.parseModifyExpenseIndex(input);
        assertEquals(42, index);
    }

    /**
     * Tests parsing of a valid 'modify-expense' command with all fields specified.
     */
    @Test
    public void parseModifyExpenseWithDefaults_allFieldsSpecified_overridesAll() {
        Expense oldExpense = new Expense(100, ExpenseCategory.TRANSPORT,
                LocalDate.of(2025, 1, 1), "old description");

        String input = modifyExpense("1", "12.50", "food", "2025-10-01", "lunch");
        Expense e = Parser.parseModifyExpenseWithDefaults(input, oldExpense);

        assertEquals(12.50, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.FOOD, e.getCategory());
        assertEquals(LocalDate.of(2025, 10, 1), e.getDate());
        assertEquals("lunch", e.getDescription());
    }

    /**
     * Tests modify-expense with only amount field specified.
     */
    @Test
    public void parseModifyExpenseWithDefaults_onlyAmount_keepsOtherFields() {
        Expense oldExpense = new Expense(100, ExpenseCategory.TRANSPORT,
                LocalDate.of(2025, 1, 15), "original");

        String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 a/50.25";
        Expense e = Parser.parseModifyExpenseWithDefaults(input, oldExpense);

        assertEquals(50.25, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.TRANSPORT, e.getCategory());
        assertEquals(LocalDate.of(2025, 1, 15), e.getDate());
        assertEquals("original", e.getDescription());
    }

    /**
     * Tests modify-expense with only category field specified.
     */
    @Test
    public void parseModifyExpenseWithDefaults_onlyCategory_keepsOtherFields() {
        Expense oldExpense = new Expense(75.50, ExpenseCategory.FOOD,
                LocalDate.of(2025, 2, 20), "dinner");

        String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 c/TRANSPORT";
        Expense e = Parser.parseModifyExpenseWithDefaults(input, oldExpense);

        assertEquals(75.50, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.TRANSPORT, e.getCategory());
        assertEquals(LocalDate.of(2025, 2, 20), e.getDate());
        assertEquals("dinner", e.getDescription());
    }

    /**
     * Tests modify-expense with only date field specified.
     */
    @Test
    public void parseModifyExpenseWithDefaults_onlyDate_keepsOtherFields() {
        LocalDate originalDate = LocalDate.now().minusDays(10);
        LocalDate updatedDate = LocalDate.now().minusDays(1);
        Expense oldExpense = new Expense(200, ExpenseCategory.ENTERTAINMENT,
                originalDate, "concert");

        String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 d/" + updatedDate;
        Expense e = Parser.parseModifyExpenseWithDefaults(input, oldExpense);

        assertEquals(200, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.ENTERTAINMENT, e.getCategory());
        assertEquals(updatedDate, e.getDate());
        assertEquals("concert", e.getDescription());
    }

    /**
     * Tests modify-expense with only description field specified.
     */
    @Test
    public void parseModifyExpenseWithDefaults_onlyDescription_keepsOtherFields() {
        Expense oldExpense = new Expense(45, ExpenseCategory.FOOD,
                LocalDate.of(2025, 4, 5), "old desc");

        String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 des/updated description";
        Expense e = Parser.parseModifyExpenseWithDefaults(input, oldExpense);

        assertEquals(45, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.FOOD, e.getCategory());
        assertEquals(LocalDate.of(2025, 4, 5), e.getDate());
        assertEquals("updated description", e.getDescription());
    }

    /**
     * Tests modify-expense with multiple fields specified.
     */
    @Test
    public void parseModifyExpenseWithDefaults_multipleFields_overridesOnlySpecified() {
        Expense oldExpense = new Expense(100, ExpenseCategory.TRANSPORT,
                LocalDate.of(2025, 5, 1), "old");

        String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 a/150 d/2025-06-15";
        Expense e = Parser.parseModifyExpenseWithDefaults(input, oldExpense);

        assertEquals(150, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.TRANSPORT, e.getCategory());
        assertEquals(LocalDate.of(2025, 6, 15), e.getDate());
        assertEquals("old", e.getDescription());
    }

    /**
     * Tests modify-expense with no fields specified (only index).
     */
    @Test
    public void parseModifyExpenseWithDefaults_noFields_keepsAllFields() {
        Expense oldExpense = new Expense(88.88, ExpenseCategory.BILLS,
                LocalDate.of(2025, 7, 7), "checkup");

        String input = Ui.MODIFY_EXPENSE_COMMAND + " 1";
        Expense e = Parser.parseModifyExpenseWithDefaults(input, oldExpense);

        assertEquals(88.88, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.BILLS, e.getCategory());
        assertEquals(LocalDate.of(2025, 7, 7), e.getDate());
        assertEquals("checkup", e.getDescription());
    }

    /**
     * Tests 'modify-expense' index parsing with invalid parameters.
     * Expects an IllegalArgumentException for each case.
     */
    @Test
    public void parseModifyExpenseIndex_invalidParams_throws() {
        // Missing index
        try {
            Parser.parseModifyExpenseIndex(Ui.MODIFY_EXPENSE_COMMAND);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                "Missing index. Usage: modify-expense <index> [a/<amount>] [c/<category>] [d/<YYYY-MM-DD>]",
                e.getMessage()
            );
        }

        // Invalid index
        try {
            Parser.parseModifyExpenseIndex(Ui.MODIFY_EXPENSE_COMMAND + " abc a/10 c/Food d/2025-10-01");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Expense index must be a valid number.", e.getMessage());
        }

        // Negative index
        try {
            Parser.parseModifyExpenseIndex(Ui.MODIFY_EXPENSE_COMMAND + " -1 a/10 c/Food d/2025-10-01");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Expense index must be a positive number.", e.getMessage());
        }

        // Zero index
        try {
            Parser.parseModifyExpenseIndex(Ui.MODIFY_EXPENSE_COMMAND + " 0");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Expense index must be a positive number.", e.getMessage());
        }
    }

    /**
     * Tests modify-expense with invalid amount values.
     */
    @Test
    public void parseModifyExpenseWithDefaults_invalidAmount_throws() {
        Expense oldExpense = new Expense(100, ExpenseCategory.FOOD,
                LocalDate.of(2025, 1, 1), "test");

        // Invalid number format
        try {
            String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 a/abc";
            Parser.parseModifyExpenseWithDefaults(input, oldExpense);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be a valid number.", e.getMessage());
        }

        // Negative amount
        try {
            String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 a/-50";
            Parser.parseModifyExpenseWithDefaults(input, oldExpense);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be non-negative.", e.getMessage());
        }

        // Non-finite amount
        try {
            String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 a/Infinity";
            Parser.parseModifyExpenseWithDefaults(input, oldExpense);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be finite.", e.getMessage());
        }
    }

    /**
     * Tests modify-expense with invalid date format.
     */
    @Test
    public void parseModifyExpenseWithDefaults_invalidDate_throws() {
        Expense oldExpense = new Expense(100, ExpenseCategory.FOOD,
                LocalDate.of(2025, 1, 1), "test");

        try {
            String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 d/2025-13-45";
            Parser.parseModifyExpenseWithDefaults(input, oldExpense);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date must be in YYYY-MM-DD format.", e.getMessage());
        }
    }

    /**
     * Tests modify-expense with a future date.
     * Expects an {@link IllegalArgumentException} with the appropriate message.
     */
    @Test
    public void parseModifyExpenseWithDefaults_futureDate_throws() {
        Expense oldExpense = new Expense(25, ExpenseCategory.FOOD,
                LocalDate.now(), "existing");

        try {
            String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 d/" + LocalDate.now().plusDays(1);
            Parser.parseModifyExpenseWithDefaults(input, oldExpense);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date cannot be in the future.", e.getMessage());
        }
    }

    /**
     * Tests 'add-income' parsing with a future date.
     * Expects an {@link IllegalArgumentException} with the appropriate message.
     */
    @Test
    public void parseAddIncome_futureDate_throws() {
        String future = addIncome("10", "Salary", LocalDate.now().plusDays(1).toString(), null);

        try {
            Parser.parseAddIncome(future);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date cannot be in the future.", e.getMessage());
        }
    }

    /**
     * Tests modify-expense with invalid category.
     */
    @Test
    public void parseModifyExpenseWithDefaults_invalidCategory_throws() {
        Expense oldExpense = new Expense(100, ExpenseCategory.FOOD,
                LocalDate.of(2025, 1, 1), "test");

        try {
            String input = Ui.MODIFY_EXPENSE_COMMAND + " 1 c/INVALID_CATEGORY";
            Parser.parseModifyExpenseWithDefaults(input, oldExpense);
            fail();
        } catch (IllegalArgumentException e) {
            // Will throw from ExpenseCategory.parse()
            assertTrue(e.getMessage().contains("Unknown expense category"));
        }
    }

    /**
     * Tests modify-expense with whitespace handling.
     */
    @Test
    public void parseModifyExpenseWithDefaults_whitespaceVariants_ok() {
        Expense oldExpense = new Expense(100, ExpenseCategory.TRANSPORT,
                LocalDate.of(2025, 1, 1), "old");

        String input = Ui.MODIFY_EXPENSE_COMMAND + "\t1   a/42.0";
        Expense e = Parser.parseModifyExpenseWithDefaults(input, oldExpense);

        assertEquals(42.0, e.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.TRANSPORT, e.getCategory());
    }

    /**
     * Tests parsing index from a valid 'modify-income' command.
     */
    @Test
    public void parseModifyIncomeIndex_valid_returnsCorrectIndex() {
        String input = Ui.MODIFY_INCOME_COMMAND + " 1 a/5000 c/SALARY d/2025-10-01";
        int index = Parser.parseModifyIncomeIndex(input);
        assertEquals(1, index);

        input = Ui.MODIFY_INCOME_COMMAND + " 99";
        index = Parser.parseModifyIncomeIndex(input);
        assertEquals(99, index);
    }

    /**
     * Tests parsing of a valid 'modify-income' command with all fields specified.
     */
    @Test
    public void parseModifyIncomeWithDefaults_allFieldsSpecified_overridesAll() {
        Income oldIncome = new Income(1000, IncomeCategory.GIFT,
                LocalDate.of(2025, 1, 1), "old description");

        String input = modifyIncome("1", "5000", "Salary", "2025-10-01", "Monthly pay");
        Income i = Parser.parseModifyIncomeWithDefaults(input, oldIncome);

        assertEquals(5000.0, i.getAmount(), 1e-9);
        assertEquals(IncomeCategory.SALARY, i.getCategory());
        assertEquals(LocalDate.of(2025, 10, 1), i.getDate());
        assertEquals("Monthly pay", i.getDescription());
    }

    /**
     * Tests modify-income with only amount field specified.
     */
    @Test
    public void parseModifyIncomeWithDefaults_onlyAmount_keepsOtherFields() {
        Income oldIncome = new Income(3000, IncomeCategory.SALARY,
                LocalDate.of(2025, 2, 15), "original");

        String input = Ui.MODIFY_INCOME_COMMAND + " 1 a/3500.50";
        Income i = Parser.parseModifyIncomeWithDefaults(input, oldIncome);

        assertEquals(3500.50, i.getAmount(), 1e-9);
        assertEquals(IncomeCategory.SALARY, i.getCategory());
        assertEquals(LocalDate.of(2025, 2, 15), i.getDate());
        assertEquals("original", i.getDescription());
    }

    /**
     * Tests modify-income with only category field specified.
     */
    @Test
    public void parseModifyIncomeWithDefaults_onlyCategory_keepsOtherFields() {
        Income oldIncome = new Income(500, IncomeCategory.GIFT,
                LocalDate.of(2025, 3, 20), "birthday");

        String input = Ui.MODIFY_INCOME_COMMAND + " 1 c/INVESTMENT";
        Income i = Parser.parseModifyIncomeWithDefaults(input, oldIncome);

        assertEquals(500, i.getAmount(), 1e-9);
        assertEquals(IncomeCategory.INVESTMENT, i.getCategory());
        assertEquals(LocalDate.of(2025, 3, 20), i.getDate());
        assertEquals("birthday", i.getDescription());
    }

    /**
     * Tests modify-income with only date field specified.
     */
    @Test
    public void parseModifyIncomeWithDefaults_onlyDate_keepsOtherFields() {
        Income oldIncome = new Income(1200, IncomeCategory.SCHOLARSHIP,
                LocalDate.of(2025, 4, 10), "semester grant");

        String input = Ui.MODIFY_INCOME_COMMAND + " 1 d/2025-09-01";
        Income i = Parser.parseModifyIncomeWithDefaults(input, oldIncome);

        assertEquals(1200, i.getAmount(), 1e-9);
        assertEquals(IncomeCategory.SCHOLARSHIP, i.getCategory());
        assertEquals(LocalDate.of(2025, 9, 1), i.getDate());
        assertEquals("semester grant", i.getDescription());
    }

    /**
     * Tests modify-income with only description field specified.
     */
    @Test
    public void parseModifyIncomeWithDefaults_onlyDescription_keepsOtherFields() {
        Income oldIncome = new Income(850, IncomeCategory.OTHERS,
                LocalDate.of(2025, 5, 5), "old desc");

        String input = Ui.MODIFY_INCOME_COMMAND + " 1 des/updated allowance description";
        Income i = Parser.parseModifyIncomeWithDefaults(input, oldIncome);

        assertEquals(850, i.getAmount(), 1e-9);
        assertEquals(IncomeCategory.OTHERS, i.getCategory());
        assertEquals(LocalDate.of(2025, 5, 5), i.getDate());
        assertEquals("updated allowance description", i.getDescription());
    }

    /**
     * Tests modify-income with multiple fields specified.
     */
    @Test
    public void parseModifyIncomeWithDefaults_multipleFields_overridesOnlySpecified() {
        Income oldIncome = new Income(2000, IncomeCategory.SALARY,
                LocalDate.of(2025, 6, 1), "old");

        String input = Ui.MODIFY_INCOME_COMMAND + " 1 a/2500 d/2025-07-01";
        Income i = Parser.parseModifyIncomeWithDefaults(input, oldIncome);

        assertEquals(2500, i.getAmount(), 1e-9);
        assertEquals(IncomeCategory.SALARY, i.getCategory());
        assertEquals(LocalDate.of(2025, 7, 1), i.getDate());
        assertEquals("old", i.getDescription());
    }

    /**
     * Tests modify-income with no fields specified (only index).
     */
    @Test
    public void parseModifyIncomeWithDefaults_noFields_keepsAllFields() {
        Income oldIncome = new Income(777.77, IncomeCategory.INVESTMENT,
                LocalDate.of(2025, 8, 8), "dividends");

        String input = Ui.MODIFY_INCOME_COMMAND + " 1";
        Income i = Parser.parseModifyIncomeWithDefaults(input, oldIncome);

        assertEquals(777.77, i.getAmount(), 1e-9);
        assertEquals(IncomeCategory.INVESTMENT, i.getCategory());
        assertEquals(LocalDate.of(2025, 8, 8), i.getDate());
        assertEquals("dividends", i.getDescription());
    }

    /**
     * Tests 'modify-income' index parsing with invalid parameters.
     * Expects an IllegalArgumentException for each case.
     */
    @Test
    public void parseModifyIncomeIndex_invalidParams_throws() {
        // Missing index
        try {
            Parser.parseModifyIncomeIndex(Ui.MODIFY_INCOME_COMMAND);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                "Missing index. Usage: modify-income <index> [a/<amount>] [c/<category>] [d/<YYYY-MM-DD>]",
                e.getMessage()
            );
        }

        // Invalid index
        try {
            Parser.parseModifyIncomeIndex(Ui.MODIFY_INCOME_COMMAND + " abc a/10 c/Salary d/2025-10-01");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Income index must be a valid number.", e.getMessage());
        }

        // Negative index
        try {
            Parser.parseModifyIncomeIndex(Ui.MODIFY_INCOME_COMMAND + " -1 a/10 c/Salary d/2025-10-01");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Income index must be a positive number.", e.getMessage());
        }

        // Zero index
        try {
            Parser.parseModifyIncomeIndex(Ui.MODIFY_INCOME_COMMAND + " 0");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Income index must be a positive number.", e.getMessage());
        }
    }

    /**
     * Tests modify-income with invalid amount values.
     */
    @Test
    public void parseModifyIncomeWithDefaults_invalidAmount_throws() {
        Income oldIncome = new Income(1000, IncomeCategory.SALARY,
                LocalDate.of(2025, 1, 1), "test");

        // Invalid number format
        try {
            String input = Ui.MODIFY_INCOME_COMMAND + " 1 a/xyz";
            Parser.parseModifyIncomeWithDefaults(input, oldIncome);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be a valid number.", e.getMessage());
        }

        // Negative amount
        try {
            String input = Ui.MODIFY_INCOME_COMMAND + " 1 a/-100";
            Parser.parseModifyIncomeWithDefaults(input, oldIncome);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be non-negative.", e.getMessage());
        }

        // Non-finite amount
        try {
            String input = Ui.MODIFY_INCOME_COMMAND + " 1 a/NaN";
            Parser.parseModifyIncomeWithDefaults(input, oldIncome);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be finite.", e.getMessage());
        }
    }

    /**
     * Tests modify-income with invalid date format.
     */
    @Test
    public void parseModifyIncomeWithDefaults_invalidDate_throws() {
        Income oldIncome = new Income(1000, IncomeCategory.SALARY,
                LocalDate.of(2025, 1, 1), "test");

        try {
            String input = Ui.MODIFY_INCOME_COMMAND + " 1 d/01-31-2025";
            Parser.parseModifyIncomeWithDefaults(input, oldIncome);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date must be in YYYY-MM-DD format.", e.getMessage());
        }
    }

    /**
     * Tests modify-income with a future date.
     * Expects an {@link IllegalArgumentException} with the appropriate message.
     */
    @Test
    public void parseModifyIncomeWithDefaults_futureDate_throws() {
        Income oldIncome = new Income(1000, IncomeCategory.SALARY,
                LocalDate.now(), "existing");

        try {
            String input = Ui.MODIFY_INCOME_COMMAND + " 1 d/" + LocalDate.now().plusDays(1);
            Parser.parseModifyIncomeWithDefaults(input, oldIncome);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date cannot be in the future.", e.getMessage());
        }
    }

    /**
     * Tests modify-income with invalid category.
     */
    @Test
    public void parseModifyIncomeWithDefaults_invalidCategory_throws() {
        Income oldIncome = new Income(1000, IncomeCategory.SALARY,
                LocalDate.of(2025, 1, 1), "test");

        try {
            String input = Ui.MODIFY_INCOME_COMMAND + " 1 c/UNKNOWN_CATEGORY";
            Parser.parseModifyIncomeWithDefaults(input, oldIncome);
            fail();
        } catch (IllegalArgumentException e) {
            // Will throw from IncomeCategory.parse()
            assertTrue(e.getMessage().contains("Unknown income category"));
        }
    }

    /**
     * Tests modify-income with whitespace handling.
     */
    @Test
    public void parseModifyIncomeWithDefaults_whitespaceVariants_ok() {
        Income oldIncome = new Income(1000, IncomeCategory.SALARY,
                LocalDate.of(2025, 1, 1), "old");

        String input = Ui.MODIFY_INCOME_COMMAND + "\t1   a/1500.75";
        Income i = Parser.parseModifyIncomeWithDefaults(input, oldIncome);

        assertEquals(1500.75, i.getAmount(), 1e-9);
        assertEquals(IncomeCategory.SALARY, i.getCategory());
    }

    /**
     * Tests 'add-expense' parsing with invalid amount formats (non-numeric, negative).
     * Expects an {@link IllegalArgumentException} for each case.
    */
    @Test
    public void parseAddExpense_invalidAmount_throws() {
        String notNumber = addExpense("twelve", "Food", "2025-10-01", null);
        String negative = addExpense("-1", "Food", "2025-10-01", null);

        try {
            Parser.parseAddExpense(notNumber);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be a valid number.", e.getMessage());
        }

        try {
            Parser.parseAddExpense(negative);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be non-negative.", e.getMessage());
        }
    }

    /**
     * Tests 'add-expense' parsing with invalid date formats.
     * Expects an {@link IllegalArgumentException} for each invalid format.
     */
    @Test
    public void parseAddExpense_invalidDate_throws() {
        String bad1 = addExpense("10", "Food", "01-10-2025", null);
        String bad2 = addExpense("10", "Food", "2025/10/01", null);
        String bad3 = addExpense("10", "Food", "2025-13-40", null);

        try {
            Parser.parseAddExpense(bad1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date must be in YYYY-MM-DD format.", e.getMessage());
        }

        try {
            Parser.parseAddExpense(bad2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date must be in YYYY-MM-DD format.", e.getMessage());
        }

        try {
            Parser.parseAddExpense(bad3);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date must be in YYYY-MM-DD format.", e.getMessage());
        }
    }

    /**
     * Tests 'add-expense' parsing with a future date.
     * Expects an {@link IllegalArgumentException} with the appropriate message.
     */
    @Test
    public void parseAddExpense_futureDate_throws() {
        String future = addExpense("10", "Food", LocalDate.now().plusDays(1).toString(), null);

        try {
            Parser.parseAddExpense(future);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date cannot be in the future.", e.getMessage());
        }
    }

    /*
     * Tests for parseAddIncome
     */

    /**
     * Tests parsing of a valid 'add-income' command with an optional description.
     * Expects a correctly configured {@link Income} object.
     */
    @Test
    public void parseAddIncome_valid_withDescription() {
        String input = addIncome("999.99", "Salary", "2025-09-30", "September payroll");
        Income inc = Parser.parseAddIncome(input);

        assertEquals(999.99, inc.getAmount(), 1e-9);
        assertEquals(IncomeCategory.SALARY, inc.getCategory());
        assertEquals(LocalDate.of(2025, 9, 30), inc.getDate());
        assertEquals("September payroll", inc.getDescription());
    }

    /**
     * Tests parsing of a valid 'add-income' command without an optional description.
     * Expects the description field of the resulting {@link Income} object to be an empty string.
     */
    @Test
    public void parseAddIncome_validWithoutDescription_descriptionNull() {
        String input = addIncome("100", "Gift", "2025-10-05", null);
        Income inc = Parser.parseAddIncome(input);

        assertEquals(100.0, inc.getAmount(), 1e-9);
        assertEquals(IncomeCategory.GIFT, inc.getCategory());
        assertEquals(LocalDate.of(2025, 10, 5), inc.getDate());
        assertNull(inc.getDescription());
    }

    /**
     * Tests 'add-income' parsing with missing required parameters.
     * Expects an {@link IllegalArgumentException} with a specific message for each case.
     */
    @Test
    public void parseAddIncome_missingParams_throws() {
        String input1 = Ui.ADD_INCOME_COMMAND;
        String input2 = Ui.ADD_INCOME_COMMAND + " " + Ui.AMOUNT_PREFIX + "10 " + Ui.CATEGORY_PREFIX + "Gift";
        String input3 = Ui.ADD_INCOME_COMMAND + " " + Ui.CATEGORY_PREFIX + "Gift " + Ui.DATE_PREFIX + "2025-10-05";
        String input4 = Ui.ADD_INCOME_COMMAND + " " + Ui.AMOUNT_PREFIX + "10 " + Ui.DATE_PREFIX + "2025-10-05";

        try {
            Parser.parseAddIncome(input1);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Missing parameters. See 'help'.", e.getMessage());
        }

        try {
            Parser.parseAddIncome(input2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.", e.getMessage());
        }

        try {
            Parser.parseAddIncome(input3);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.", e.getMessage());
        }

        try {
            Parser.parseAddIncome(input4);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Required fields: a/<amount> c/<category> d/<YYYY-MM-DD>.", e.getMessage());
        }
    }

    /**
     * Tests 'add-income' parsing with invalid amount or date formats.
     * Expects an {@link IllegalArgumentException} for each case.
     */
    @Test
    public void parseAddIncome_invalidAmountOrDate_throws() {
        String badAmt = addIncome("abc", "Gift", "2025-10-05", null);
        String negative = addIncome("-0.01", "Gift", "2025-10-05", null);
        String badDate = addIncome("10", "Gift", "05-10-2025", null);

        try {
            Parser.parseAddIncome(badAmt);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be a valid number.", e.getMessage());
        }

        try {
            Parser.parseAddIncome(negative);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be non-negative.", e.getMessage());
        }

        try {
            Parser.parseAddIncome(badDate);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Date must be in YYYY-MM-DD format.", e.getMessage());
        }
    }

    /**
     * add-income must tolerate mixed whitespace between command/prefix segments.
     */
    @Test
    public void parseAddIncome_whitespaceVariants_ok() {
        String input = Ui.ADD_INCOME_COMMAND + "  \t"
                + Ui.AMOUNT_PREFIX + "1500 "
                + Ui.CATEGORY_PREFIX + "Salary\t "
                + Ui.DATE_PREFIX + "2025-11-01   "
                + Ui.DESCRIPTION_PREFIX + "tab heavy";

        Income income = Parser.parseAddIncome(input);
        assertEquals(1500.0, income.getAmount(), 1e-9);
        assertEquals(IncomeCategory.SALARY, income.getCategory());
        assertEquals(LocalDate.of(2025, 11, 1), income.getDate());
        assertEquals("tab heavy", income.getDescription());
    }

    /**
     * add-income should parse compulsory prefixes correctly even when the order is shuffled.
     */
    @Test
    public void parseAddIncome_parameterOrder_ok() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        String input = Ui.ADD_INCOME_COMMAND + " "
                + Ui.CATEGORY_PREFIX + "Gift "
                + Ui.DATE_PREFIX + pastDate + " "
                + Ui.AMOUNT_PREFIX + "88.8 "
                + Ui.DESCRIPTION_PREFIX + "Reordered prefixes ";


        Income income = Parser.parseAddIncome(input);
        assertEquals(88.8, income.getAmount(), 1e-9);
        assertEquals(IncomeCategory.GIFT, income.getCategory());
        assertEquals(pastDate, income.getDate());
        assertEquals("Reordered prefixes", income.getDescription());
    }

    /**
     * add-income should reject NaN or infinite amounts.
     */
    @Test
    public void parseAddIncome_nonFiniteAmount_throws() {
        String[] inputs = {
            Ui.ADD_INCOME_COMMAND + " "
                    + Ui.AMOUNT_PREFIX + "NaN "
                    + Ui.CATEGORY_PREFIX + "Salary "
                    + Ui.DATE_PREFIX + "2025-11-01",
            Ui.ADD_INCOME_COMMAND + " "
                    + Ui.AMOUNT_PREFIX + "Infinity "
                    + Ui.CATEGORY_PREFIX + "Salary "
                    + Ui.DATE_PREFIX + "2025-11-01",
            Ui.ADD_INCOME_COMMAND + " "
                    + Ui.AMOUNT_PREFIX + "-Infinity "
                    + Ui.CATEGORY_PREFIX + "Salary "
                    + Ui.DATE_PREFIX + "2025-11-01"
        };

        for (String input : inputs) {
            try {
                Parser.parseAddIncome(input);
                fail();
            } catch (IllegalArgumentException e) {
                assertEquals("Amount must be finite.", e.getMessage());
            }
        }
    }

    /**
     * add-income should reject descriptions that appear before other prefixes.
     */
    @Test
    public void parseAddIncome_descriptionNotLast_throws() {
        String input = Ui.ADD_INCOME_COMMAND + " "
                + Ui.DESCRIPTION_PREFIX + "Should fail "
                + Ui.AMOUNT_PREFIX + "500 "
                + Ui.CATEGORY_PREFIX + "Salary "
                + Ui.DATE_PREFIX + "2025-11-01";

        try {
            Parser.parseAddIncome(input);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Description (des/) must be the last parameter.", e.getMessage());
        }
    }

    /**
     * Income description should keep prefix-looking substrings intact.
     */
    @Test
    public void parseAddIncome_descriptionWithPrefixes_textPreserved() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        String input = addIncome(
                "2500",
                "salary",
                pastDate.toString(),
                "Bonus with a/500 c/SALARY \t d/" + pastDate);

        Income income = Parser.parseAddIncome(input);
        assertEquals("Bonus with a/500 c/SALARY \t d/" + pastDate, income.getDescription());
    }

    /*
     * Tests for parseDeleteExpense
     */

    /**
     * Tests 'delete-expense' parsing with valid positive integer indices.
     * Expects the parsing to complete without throwing an exception.
     */
    @Test
    public void parseDeleteExpense_validPositiveId_ok() {
        try {
            Parser.parseDeleteExpense(Ui.DELETE_EXPENSE_COMMAND + " 1");
            Parser.parseDeleteExpense(Ui.DELETE_EXPENSE_COMMAND + " 42");
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests 'delete-expense' parsing when the index argument is missing.
     * Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void parseDeleteExpense_missingArgs_throws() {
        try {
            Parser.parseDeleteExpense(Ui.DELETE_EXPENSE_COMMAND);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Missing expense index. Usage: delete-expense <index>", e.getMessage());
        }

        try {
            Parser.parseDeleteExpense(Ui.DELETE_EXPENSE_COMMAND + " ");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Missing expense index. Usage: delete-expense <index>", e.getMessage());
        }
    }

    /**
     * Tests 'delete-expense' parsing with non-numeric or non-positive indices.
     * Expects an {@link IllegalArgumentException} for each invalid case.
     */
    @Test
    public void parseDeleteExpense_nonNumericOrNonPositive_throws() {
        try {
            Parser.parseDeleteExpense(Ui.DELETE_EXPENSE_COMMAND + " abc");
            fail();
        } catch (IllegalArgumentException e) {
            // message from Integer.parseInt can vary; just ensure we got the right exception type.
            assertEquals("Expense index must be a valid number.", e.getMessage());
        }

        try {
            Parser.parseDeleteExpense(Ui.DELETE_EXPENSE_COMMAND + " 0");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Expense index must be a positive number.", e.getMessage());
        }

        try {
            Parser.parseDeleteExpense(Ui.DELETE_EXPENSE_COMMAND + " -5");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Expense index must be a positive number.", e.getMessage());
        }
    }

    /*
     * Tests for parseDeleteIncome
     */

    /**
     * Tests 'delete-income' parsing with a valid positive integer index.
     * Expects the parsing to complete without throwing an exception.
     */
    @Test
    public void parseDeleteIncome_validPositiveId_ok() {
        try {
            Parser.parseDeleteIncome(Ui.DELETE_INCOME_COMMAND + " 3");
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests 'delete-income' parsing when the index argument is missing.
     * Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void parseDeleteIncome_missingArgs_throws() {
        try {
            Parser.parseDeleteIncome(Ui.DELETE_INCOME_COMMAND);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Missing income index. Usage: delete-income <index>", e.getMessage());
        }

        try {
            Parser.parseDeleteIncome(Ui.DELETE_INCOME_COMMAND + " ");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Missing income index. Usage: delete-income <index>", e.getMessage());
        }
    }

    /**
     * Tests 'delete-income' parsing with non-numeric or non-positive indices.
     * Expects an {@link IllegalArgumentException} for each invalid case.
     */
    @Test
    public void parseDeleteIncome_nonNumericOrNonPositive_throws() {
        try {
            Parser.parseDeleteIncome(Ui.DELETE_INCOME_COMMAND + " x1");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Income index must be a valid number.", e.getMessage());
        }

        try {
            Parser.parseDeleteIncome(Ui.DELETE_INCOME_COMMAND + " 0");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Income index must be a positive number.", e.getMessage());
        }

        try {
            Parser.parseDeleteIncome(Ui.DELETE_INCOME_COMMAND + " -7");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Income index must be a positive number.", e.getMessage());
        }
    }

    /*
     * Tests for parseOptionalMonthForBalance / ExpenseList / IncomeList
     */

    /**
     * balance / balance d/YYYY-MM:
     * - empty arg -> Optional.empty()
     * - valid month -> parsed YearMonth
     * - invalid month -> IllegalArgumentException
     */
    @Test
    public void parseOptionalMonthForBalance_cases() {
        // No month provided
        Optional<YearMonth> empty = Parser.parseOptionalMonthForBalance(Ui.BALANCE_COMMAND);
        assertTrue(empty.isEmpty());

        // Extra spaces but still empty
        Optional<YearMonth> empty2 = Parser.parseOptionalMonthForBalance(Ui.BALANCE_COMMAND + "   ");
        assertTrue(empty2.isEmpty());

        // Valid month
        Optional<YearMonth> ok = Parser.parseOptionalMonthForBalance(Ui.BALANCE_COMMAND + " d/2025-10");
        assertEquals(YearMonth.of(2025, 10), ok.get());

        // Missing prefix
        try {
            Parser.parseOptionalMonthForBalance(Ui.BALANCE_COMMAND + " 2025-10");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Usage: balance [d/YYYY-MM]", e.getMessage());
        }

        // Invalid month formats
        String[] bads = {Ui.BALANCE_COMMAND + " d/25-10", "empty", "empty"};
        bads[1] = Ui.BALANCE_COMMAND + " d/2025/10";
        bads[2] = Ui.BALANCE_COMMAND + " d/202510";
        for (String bad : bads) {
            try {
                Parser.parseOptionalMonthForBalance(bad);
                fail();
            } catch (IllegalArgumentException e) {
                assertEquals("Month must be in YYYY-MM format.", e.getMessage());
            }
        }
    }

    /**
     * list-expense / list-expense d/YYYY-MM:
     * - valid, invalid, trimming
     */
    @Test
    public void parseOptionalMonthForExpenseList_cases() {
        // Valid
        Optional<YearMonth> ok = Parser.parseOptionalMonthForExpenseList(Ui.LIST_EXPENSE_COMMAND + " d/2024-01");
        assertEquals(YearMonth.of(2024, 1), ok.get());

        // Leading/trailing spaces
        Optional<YearMonth> okSp = Parser.parseOptionalMonthForExpenseList(
                Ui.LIST_EXPENSE_COMMAND + "   d/ 2024-12   ");
        assertEquals(YearMonth.of(2024, 12), okSp.get());

        // Empty -> Optional.empty()
        Optional<YearMonth> none = Parser.parseOptionalMonthForExpenseList(Ui.LIST_EXPENSE_COMMAND);
        assertTrue(none.isEmpty());

        // Invalid format
        try {
            Parser.parseOptionalMonthForExpenseList(Ui.LIST_EXPENSE_COMMAND + " d/2024/01");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Month must be in YYYY-MM format.", e.getMessage());
        }

        // Missing prefix
        try {
            Parser.parseOptionalMonthForExpenseList(Ui.LIST_EXPENSE_COMMAND + " 2024-01");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Usage: list-expense [d/YYYY-MM]", e.getMessage());
        }
    }

    /**
     * list-income / list-income d/YYYY-MM:
     * - valid month
     * - empty
     * - invalid month
     */
    @Test
    public void parseOptionalMonthForIncomeList_cases() {
        // Valid
        Optional<YearMonth> ok = Parser.parseOptionalMonthForIncomeList(Ui.LIST_INCOME_COMMAND + " d/2030-07");
        assertEquals(YearMonth.of(2030, 7), ok.get());

        // Empty -> Optional.empty()
        Optional<YearMonth> none = Parser.parseOptionalMonthForIncomeList(Ui.LIST_INCOME_COMMAND + "   ");
        assertTrue(none.isEmpty());

        // Invalid
        String bad = Ui.LIST_INCOME_COMMAND + " d/2030-7"; // not zero-padded month
        try {
            Parser.parseOptionalMonthForIncomeList(bad);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Month must be in YYYY-MM format.", e.getMessage());
        }

        // Missing prefix
        try {
            Parser.parseOptionalMonthForIncomeList(Ui.LIST_INCOME_COMMAND + " 2030-07");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Usage: list-income [d/YYYY-MM]", e.getMessage());
        }
    }

    /**
     * Optional month parsing should accept tabs and multiple spaces after the command.
     */
    @Test
    public void parseOptionalMonth_whitespaceVariants_ok() {
        Optional<YearMonth> withTab = Parser.parseOptionalMonthForExpenseList(
                Ui.LIST_EXPENSE_COMMAND + "\t" + Ui.DATE_PREFIX + "2025-10");
        assertEquals(YearMonth.of(2025, 10), withTab.orElseThrow());

        Optional<YearMonth> manySpaces = Parser.parseOptionalMonthForIncomeList(
                Ui.LIST_INCOME_COMMAND + "      " + Ui.DATE_PREFIX + "2027-01");
        assertEquals(YearMonth.of(2027, 1), manySpaces.orElseThrow());

        Optional<YearMonth> balanceSpaced = Parser.parseOptionalMonthForBalance(
                Ui.BALANCE_COMMAND + "  \t  " + Ui.DATE_PREFIX + "2024-12");
        assertEquals(YearMonth.of(2024, 12), balanceSpaced.orElseThrow());
    }

    // ============ Tests for parseSetBudget ============

    /**
     * Tests parsing of a valid 'budget' command.
     */
    @Test
    public void parseSetBudget_valid_ok() {
        String input = setBudget("food", "500.75");
        var result = Parser.parseSetBudget(input);
        assertEquals(ExpenseCategory.FOOD, result.getKey());
        assertEquals(500.75, result.getValue(), 1e-9);
    }

    /**
     * Tests parsing of a valid 'budget' command with parameters in reverse order.
     */
    @Test
    public void parseSetBudget_paramsReversed_ok() {
        String input = Ui.BUDGET_COMMAND + " "
                + Ui.AMOUNT_PREFIX + "150" + " "
                + Ui.CATEGORY_PREFIX + "transport";
        var result = Parser.parseSetBudget(input);
        assertEquals(ExpenseCategory.TRANSPORT, result.getKey());
        assertEquals(150.0, result.getValue(), 1e-9);
    }

    /**
     * budget should reject NaN or infinite amounts.
     */
    @Test
    public void parseSetBudget_nonFinite_throws() {
        String[] inputs = {
            Ui.BUDGET_COMMAND + " "
                    + Ui.CATEGORY_PREFIX + "food "
                    + Ui.AMOUNT_PREFIX + "NaN",
            Ui.BUDGET_COMMAND + " "
                    + Ui.CATEGORY_PREFIX + "food "
                    + Ui.AMOUNT_PREFIX + "Infinity",
            Ui.BUDGET_COMMAND + " "
                    + Ui.CATEGORY_PREFIX + "food "
                    + Ui.AMOUNT_PREFIX + "-Infinity"
        };

        for (String input : inputs) {
            try {
                Parser.parseSetBudget(input);
                fail();
            } catch (IllegalArgumentException e) {
                assertEquals("Amount must be finite.", e.getMessage());
            }
        }
    }

    /**
     * Tests 'budget' parsing with missing parameters.
     * Expects an {@link IllegalArgumentException} for each case.
     */
    @Test
    public void parseSetBudget_missingParams_throws() {
        String usageMsg = "Usage: budget c/<category> a/<amount>";

        // Missing all parameters
        try {
            Parser.parseSetBudget(Ui.BUDGET_COMMAND);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("Missing parameters for budget command."));
        }

        // Missing amount
        try {
            Parser.parseSetBudget(Ui.BUDGET_COMMAND + " " + Ui.CATEGORY_PREFIX + "food");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(usageMsg, e.getMessage());
        }

        // Missing category
        try {
            Parser.parseSetBudget(Ui.BUDGET_COMMAND + " " + Ui.AMOUNT_PREFIX + "100");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(usageMsg, e.getMessage());
        }
    }

    /**
     * Tests 'budget' parsing with invalid amount (non-numeric, negative).
     * Expects an {@link IllegalArgumentException} for each case.
     */
    @Test
    public void parseSetBudget_invalidAmount_throws() {
        // Non-numeric amount
        try {
            Parser.parseSetBudget(setBudget("food", "abc"));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be a valid number.", e.getMessage());
        }

        // Negative amount
        try {
            Parser.parseSetBudget(setBudget("food", "-100"));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Amount must be non-negative.", e.getMessage());
        }
    }

    /**
     * Tests 'budget' parsing with an invalid expense category.
     * Expects an {@link IllegalArgumentException}.
     */
    @Test
    public void parseSetBudget_invalidCategory_throws() {
        try {
            Parser.parseSetBudget(setBudget("invalidCategory", "100"));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Unknown expense category!" +
                            "\nAvailable categories:" +
                                " [FOOD, STUDY, TRANSPORT, BILLS, ENTERTAINMENT, RENT, GROCERIES, OTHERS]",
                    e.getMessage());
        }
    }

    // ============ Tests for parseExport ============

    @Test
    public void parseExport_validPath_ok() {
        String input = Ui.EXPORT_COMMAND + " data.csv";
        java.nio.file.Path path = Parser.parseExport(input);
        assertTrue(path.endsWith("data.csv"));
    }

    @Test
    public void parseExport_validPathNoExtension_appendsCsv() {
        String input = Ui.EXPORT_COMMAND + " myDataFile";
        java.nio.file.Path path = Parser.parseExport(input);
        assertTrue(path.endsWith("myDataFile.csv"));
    }

    @Test
    public void parseExport_missingPath_throws() {
        try {
            Parser.parseExport(Ui.EXPORT_COMMAND);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Missing file path. Usage: export <filepath>", e.getMessage());
        }

        try {
            Parser.parseExport(Ui.EXPORT_COMMAND + "   ");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Missing file path. Usage: export <filepath>", e.getMessage());
        }
    }

    @Test
    public void parseExport_invalidPathChars_throws() {
        // Using a null character, which is invalid in most filesystems
        String input = Ui.EXPORT_COMMAND + " data\0.csv";
        try {
            Parser.parseExport(input);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid file path. Please provide a valid path for the CSV file.", e.getMessage());
        }
    }
}
