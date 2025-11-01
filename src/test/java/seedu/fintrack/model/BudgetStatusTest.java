package seedu.fintrack.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for the BudgetStatus class.
 */
public class BudgetStatusTest {

    @Test
    public void constructor_overBudgetOnly_setsFlagsCorrectly() {
        BudgetStatus budgetStatus = new BudgetStatus(true, false);

        // Check if isOverBudget returns true
        assertTrue(budgetStatus.isOverBudget(), "isOverBudget should return true");

        // Check if isNearBudget returns false
        assertFalse(budgetStatus.isNearBudget(), "isNearBudget should return false");
    }

    @Test
    public void constructor_nearBudgetOnly_setsFlagsCorrectly() {
        BudgetStatus budgetStatus = new BudgetStatus(false, true);

        // Check if isOverBudget returns false
        assertFalse(budgetStatus.isOverBudget(), "isOverBudget should return false");

        // Check if isNearBudget returns true
        assertTrue(budgetStatus.isNearBudget(), "isNearBudget should return true");
    }

    @Test
    public void constructor_noFlagsSet_setsFlagsCorrectly() {
        BudgetStatus budgetStatus = new BudgetStatus(false, false);

        // Check if isOverBudget returns false
        assertFalse(budgetStatus.isOverBudget(), "isOverBudget should return false");

        // Check if isNearBudget returns false
        assertFalse(budgetStatus.isNearBudget(), "isNearBudget should return false");
    }

    @Test
    public void constructor_allFlagsSet_setsFlagsCorrectly() {
        BudgetStatus budgetStatus = new BudgetStatus(true, true);

        // Check if isOverBudget returns true
        assertTrue(budgetStatus.isOverBudget(), "isOverBudget should return true");

        // Check if isNearBudget returns true
        assertTrue(budgetStatus.isNearBudget(), "isNearBudget should return true");
    }
}
