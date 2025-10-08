package seedu.fintrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class IncomeTest {

    @Test
    public void constructor_validInputs_initialisesFields() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        Income income = new Income(2500.75, "Salary", date, "January payroll");

        assertNotNull(income);
        assertEquals(2500.75, income.getAmount(), 1e-9);
        assertEquals("Salary", income.getCategory());
        assertEquals(date, income.getDate());
        assertEquals("January payroll", income.getDescription());
    }

    @Test
    public void constructor_nullDescription_setsDescriptionNull() {
        Income income = new Income(100.00, "Gift", LocalDate.of(2024, 12, 25), null);
        assertNull(income.getDescription());
    }

    @Test
    public void constructor_blankDescription_setsDescriptionNull() {
        Income income = new Income(80.00, "Rebate", LocalDate.of(2024, 11, 1), "   ");
        assertNull(income.getDescription());
    }

    @Test
    public void getAmount_validIncome_returnsSameAmount() {
        Income income = new Income(19.99, "Other", LocalDate.of(2023, 6, 10), "Misc");
        assertEquals(19.99, income.getAmount(), 1e-9);
    }

    @Test
    public void getCategory_validIncome_returnsSameCategory() {
        Income income = new Income(50.00, "Allowance", LocalDate.of(2023, 6, 1), "Weekly");
        assertEquals("Allowance", income.getCategory());
    }

    @Test
    public void getDate_validIncome_returnsSameDate() {
        LocalDate date = LocalDate.of(2022, 9, 30);
        Income income = new Income(120.00, "Refund", date, "Course fee");
        assertEquals(date, income.getDate());
    }
}
