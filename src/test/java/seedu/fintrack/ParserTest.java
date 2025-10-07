package seedu.fintrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.Income;

public class ParserTest {

    /*
     * Helper builders for command strings
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

    /*
     * Tests for returnFirstWord & getFirstSpaceIndex
     */

    @Test
    public void returnFirstWord_basic_noLeadingSpaces() {
        String input = Ui.ADD_EXPENSE_COMMAND + " a/12.3 c/Food d/2025-10-01";
        String first = Parser.returnFirstWord(input);
        assertEquals(Ui.ADD_EXPENSE_COMMAND, first);
    }

    @Test
    public void returnFirstWord_noSpace_returnsWhole() {
        String input = "help";
        String first = Parser.returnFirstWord(input);
        assertEquals("help", first);
    }

    @Test
    public void returnFirstWord_leadingSpaces_returnsCommandWord() {
        String input = "  " + Ui.ADD_INCOME_COMMAND + " a/1 c/Salary d/2025-10-01";
        String first = Parser.returnFirstWord(input);
        assertEquals(Ui.ADD_INCOME_COMMAND, first);
    }

    @Test
    public void getFirstSpaceIndex_examples() {
        assertEquals(4, Parser.getFirstSpaceIndex("echo hi"));  // "echo" + ' ' => 4 (0-based + 1)
        assertEquals(-1, Parser.getFirstSpaceIndex("nospace"));
        assertEquals(0, Parser.getFirstSpaceIndex(" x"));       // leading space
    }

    /*
     * Test input handling for parseAddExpense
     */

    @Test
    public void parseAddExpense_valid_withDescription() {
        String input = addExpense("12.50", "food", "2025-10-01", "lunch");
        Expense e = Parser.parseAddExpense(input);

        assertEquals(12.50, e.getAmount(), 1e-9);
        assertEquals("food", e.getCategory());
        assertEquals(LocalDate.of(2025, 10, 1), e.getDate());
        assertEquals("lunch", e.getDescription());
    }

    @Test
    public void parseAddExpense_validWithoutDescription_descriptionNull() {
        String input = addExpense("0", "Transport", "2025-01-02", null);
        Expense e = Parser.parseAddExpense(input);

        assertEquals(0.0, e.getAmount(), 1e-9);
        assertEquals("Transport", e.getCategory());
        assertEquals(LocalDate.of(2025, 1, 2), e.getDate());
        assertEquals(e.getDescription(), "");
    }

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

    /*
     * parseAddIncome
     */

    @Test
    public void parseAddIncome_valid_withDescription() {
        String input = addIncome("999.99", "Salary", "2025-09-30", "September payroll");
        Income inc = Parser.parseAddIncome(input);

        assertEquals(999.99, inc.getAmount(), 1e-9);
        assertEquals("Salary", inc.getCategory());
        assertEquals(LocalDate.of(2025, 9, 30), inc.getDate());
        assertEquals("September payroll", inc.getDescription());
    }

    @Test
    public void parseAddIncome_validWithoutDescription_descriptionNull() {
        String input = addIncome("100", "Gift", "2025-10-05", null);
        Income inc = Parser.parseAddIncome(input);

        assertEquals(100.0, inc.getAmount(), 1e-9);
        assertEquals("Gift", inc.getCategory());
        assertEquals(LocalDate.of(2025, 10, 5), inc.getDate());
        assertNull(inc.getDescription());
    }

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

    /*
     * parseDeleteExpense
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
     * parseDeleteIncome
     */

    @Test
    public void parseDeleteIncome_validPositiveId_ok() {
        try {
            Parser.parseDeleteIncome(Ui.DELETE_INCOME_COMMAND + " 3");
        } catch (Exception e) {
            fail();
        }
    }

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
}
