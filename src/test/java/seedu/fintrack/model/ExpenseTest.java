package seedu.fintrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class ExpenseTest {

    @Test
    public void constructor_validInputs_initialisesFields() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        Expense expense = new Expense(12.50, ExpenseCategory.parse("Food"), date, "Lunch");

        assertNotNull(expense);
        assertEquals(12.50, expense.getAmount(), 1e-9);
        assertEquals(ExpenseCategory.FOOD, expense.getCategory());
        assertEquals(date, expense.getDate());
        assertEquals("Lunch", expense.getDescription());
    }

    @Test
    public void getAmount_validExpense_returnsSameAmount() {
        Expense expense = new Expense(99.99, ExpenseCategory.ENTERTAINMENT, LocalDate.of(2024, 12, 1), "Shoes");
        assertEquals(99.99, expense.getAmount());
    }

    @Test
    public void getCategory_validExpense_returnsSameCategory() {
        Expense expense = new Expense(5.00, ExpenseCategory.parse("Transport"), LocalDate.of(2024, 11, 30), "Bus fare");
        assertEquals(ExpenseCategory.TRANSPORT, expense.getCategory());
    }

    @Test
    public void getDate_validExpense_returnsSameDate() {
        LocalDate date = LocalDate.of(2023, 6, 10);
        Expense expense = new Expense(3.20, ExpenseCategory.FOOD, date, "Coffee");
        assertEquals(date, expense.getDate());
    }

    @Test
    public void getDescription_validExpense_returnsSameDescription() {
        Expense expense = new Expense(42.00, ExpenseCategory.BILLS, LocalDate.of(2025, 3, 1), "Electricity bill");
        assertEquals("Electricity bill", expense.getDescription());
    }
}
