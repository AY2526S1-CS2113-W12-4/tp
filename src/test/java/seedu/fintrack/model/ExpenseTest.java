package seedu.fintrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class ExpenseTest {

    @Test
    public void constructor_validInputs_initialisesFields() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        Expense expense = new Expense(12.50, "Food", date, "Lunch");

        assertNotNull(expense);
        assertEquals(12.50, expense.getAmount(), 1e-9);
        assertEquals("Food", expense.getCategory());
        assertEquals(date, expense.getDate());
        assertEquals("Lunch", expense.getDescription());
    }

    @Test
    public void getAmount_validExpense_returnsSameAmount() {
        Expense expense = new Expense(99.99, "Shopping", LocalDate.of(2024, 12, 1), "Shoes");
        assertEquals(99.99, expense.getAmount());
    }

    @Test
    public void getCategory_validExpense_returnsSameCategory() {
        Expense expense = new Expense(5.00, "Transport", LocalDate.of(2024, 11, 30), "Bus fare");
        assertEquals("Transport", expense.getCategory());
    }

    @Test
    public void getDate_validExpense_returnsSameDate() {
        LocalDate date = LocalDate.of(2023, 6, 10);
        Expense expense = new Expense(3.20, "Beverage", date, "Coffee");
        assertEquals(date, expense.getDate());
    }

    @Test
    public void getDescription_validExpense_returnsSameDescription() {
        Expense expense = new Expense(42.00, "Utilities", LocalDate.of(2025, 3, 1), "Electricity bill");
        assertEquals("Electricity bill", expense.getDescription());
    }
}
