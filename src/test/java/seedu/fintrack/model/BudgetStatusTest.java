package seedu.fintrack.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BudgetStatus class.
 */
public class BudgetStatusTest {

    @Test
    public void testConstructor_OverBudget() {
        BudgetStatus budgetStatus = new BudgetStatus(true, false);

        // Check if isOverBudget returns true
        assertTrue(budgetStatus.isOverBudget(), "isOverBudget should return true");

        // Check if isNearBudget returns false
        assertFalse(budgetStatus.isNearBudget(), "isNearBudget should return false");
    }

    @Test
    public void testConstructor_NearBudget() {
        BudgetStatus budgetStatus = new BudgetStatus(false, true);

        // Check if isOverBudget returns false
        assertFalse(budgetStatus.isOverBudget(), "isOverBudget should return false");

        // Check if isNearBudget returns true
        assertTrue(budgetStatus.isNearBudget(), "isNearBudget should return true");
    }

    @Test
    public void testConstructor_NoBudgetIssues() {
        BudgetStatus budgetStatus = new BudgetStatus(false, false);

        // Check if isOverBudget returns false
        assertFalse(budgetStatus.isOverBudget(), "isOverBudget should return false");

        // Check if isNearBudget returns false
        assertFalse(budgetStatus.isNearBudget(), "isNearBudget should return false");
    }

    @Test
    public void testConstructor_AllFlagsTrue() {
        BudgetStatus budgetStatus = new BudgetStatus(true, true);

        // Check if isOverBudget returns true
        assertTrue(budgetStatus.isOverBudget(), "isOverBudget should return true");

        // Check if isNearBudget returns true
        assertTrue(budgetStatus.isNearBudget(), "isNearBudget should return true");
    }
}
