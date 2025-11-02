package seedu.fintrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.IncomeCategory;
import seedu.fintrack.model.BudgetStatus;

public class FinanceManagerTest {
    
    private FinanceManager fm;
    private Income sampleIncome1;
    private Income sampleIncome2;
    private Expense sampleExpense1;
    private Expense sampleExpense2;
    private Expense sampleExpense3;
    
    @BeforeEach
    void setUp() {
        fm = new FinanceManager();
        
        // Create sample data for testing
        sampleIncome1 = new Income(5000.0, IncomeCategory.SALARY, LocalDate.parse("2025-01-01"), "Monthly salary");
        sampleIncome2 = new Income(200.0, IncomeCategory.SCHOLARSHIP, LocalDate.parse("2025-01-15"), null);
        
        sampleExpense1 = new Expense(1200.0, ExpenseCategory.RENT, LocalDate.parse("2025-01-01"), "Monthly rent");
        sampleExpense2 = new Expense(50.0, ExpenseCategory.FOOD, LocalDate.parse("2025-01-02"), null);
        sampleExpense3 = new Expense(30.0, ExpenseCategory.TRANSPORT, LocalDate.parse("2025-01-03"), "Bus fare");
    }
    
    @Test
    void addIncome_validIncome_addsSuccessfully() {
        fm.addIncome(sampleIncome1);
        assertEquals(5000.0, fm.getTotalIncome());
        
        fm.addIncome(sampleIncome2);
        assertEquals(5200.0, fm.getTotalIncome());
    }
    
    @Test
    void addIncome_nullIncome_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fm.addIncome(null);
        });
        assertEquals("Income cannot be null", exception.getMessage());
    }
    
    @Test
    void addExpense_validExpense_addsSuccessfully() {
        fm.addExpense(sampleExpense1);
        assertEquals(1200.0, fm.getTotalExpense());
        
        fm.addExpense(sampleExpense2);
        assertEquals(1250.0, fm.getTotalExpense());
    }
    
    @Test
    void addExpense_nullExpense_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fm.addExpense(null);
        });
        assertEquals("Expense cannot be null", exception.getMessage());
    }
    
    @Test
    void getTotalIncome_noIncome_returnsZero() {
        assertEquals(0.0, fm.getTotalIncome());
    }
    
    @Test
    void getTotalIncome_multipleIncomes_returnsCorrectSum() {
        fm.addIncome(sampleIncome1); // 5000
        fm.addIncome(sampleIncome2); // 200
        assertEquals(5200.0, fm.getTotalIncome());
    }

    @Test
    void getIncomesView_noIncomes_returnsEmptyList() {
        List<Income> incomes = fm.getIncomesView();
        assertTrue(incomes.isEmpty());
    }

    @Test
    void getIncomesView_withIncomes_returnsUnmodifiableNewestFirstView() {
        fm.addIncome(sampleIncome1); // 2025-01-01
        fm.addIncome(sampleIncome2); // 2025-01-15 (newest)

        List<Income> incomes = fm.getIncomesView();
        assertEquals(2, incomes.size());
        // newest first view
        assertEquals(sampleIncome2, incomes.get(0)); // 2025-01-15 (newest)
        assertEquals(sampleIncome1, incomes.get(1)); // 2025-01-01 (older)
        assertThrows(UnsupportedOperationException.class, () -> incomes.add(sampleIncome1));
    }
    
    @Test
    void getTotalExpense_noExpenses_returnsZero() {
        assertEquals(0.0, fm.getTotalExpense());
    }
    
    @Test
    void getTotalExpense_multipleExpenses_returnsCorrectSum() {
        fm.addExpense(sampleExpense1); // 1200
        fm.addExpense(sampleExpense2); // 50
        fm.addExpense(sampleExpense3); // 30
        assertEquals(1280.0, fm.getTotalExpense());
    }
    
    @Test
    void getBalance_noTransactions_returnsZero() {
        assertEquals(0.0, fm.getBalance());
    }
    
    @Test
    void getBalance_positiveBalance_returnsCorrectValue() {
        fm.addIncome(sampleIncome1); // 5000
        fm.addExpense(sampleExpense1); // 1200
        assertEquals(3800.0, fm.getBalance());
    }
    
    @Test
    void getBalance_negativeBalance_returnsCorrectValue() {
        fm.addIncome(sampleIncome2); // 200
        fm.addExpense(sampleExpense1); // 1200
        assertEquals(-1000.0, fm.getBalance());
    }
    
    @Test
    void getExpensesView_noExpenses_returnsEmptyList() {
        List<Expense> expenses = fm.getExpensesView();
        assertTrue(expenses.isEmpty());
    }
    
    @Test
    void getExpensesView_withExpenses_returnsUnmodifiableNewestFirstView() {
        fm.addExpense(sampleExpense1); // 2025-01-01
        fm.addExpense(sampleExpense2); // 2025-01-02
        fm.addExpense(sampleExpense3); // 2025-01-03
        
        List<Expense> expenses = fm.getExpensesView();
        
        // Should be newest first (by date)
        assertEquals(3, expenses.size());
        assertEquals(sampleExpense3, expenses.get(0)); // 2025-01-03 (newest)
        assertEquals(sampleExpense2, expenses.get(1)); // 2025-01-02
        assertEquals(sampleExpense1, expenses.get(2)); // 2025-01-01 (oldest)
        
        // Verify it's unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> {
            expenses.add(new Expense(100.0, ExpenseCategory.FOOD, LocalDate.now(), "Test"));
        });
    }
    
    @Test
    void deleteExpense_validIndex_deletesAndReturnsExpense() {
        fm.addExpense(sampleExpense1); // index 1 (oldest in view)
        fm.addExpense(sampleExpense2); // index 2
        fm.addExpense(sampleExpense3); // index 1 (newest in view)
        
        // Delete the newest expense (index 1 in view)
        Expense deleted = fm.deleteExpense(1);
        assertEquals(sampleExpense3, deleted);
        
        // Verify remaining expenses
        List<Expense> remaining = fm.getExpensesView();
        assertEquals(2, remaining.size());
        assertEquals(sampleExpense2, remaining.get(0)); // Now the newest
        assertEquals(sampleExpense1, remaining.get(1)); // Still the oldest
        
        // Verify total expense updated
        assertEquals(1250.0, fm.getTotalExpense()); // 1200 + 50 (sampleExpense3 removed)
    }
    
    @Test
    void deleteExpense_indexTooLow_throwsException() {
        fm.addExpense(sampleExpense1);
        
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            fm.deleteExpense(0);
        });
        assertEquals("Expense index out of range. Valid range: 1 to 1", exception.getMessage());
    }
    
    @Test
    void deleteExpense_indexTooHigh_throwsException() {
        fm.addExpense(sampleExpense1);
        
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            fm.deleteExpense(2);
        });
        assertEquals("Expense index out of range. Valid range: 1 to 1", exception.getMessage());
    }
    
    @Test
    void deleteExpense_emptyList_throwsException() {
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            fm.deleteExpense(1);
        });
        assertEquals("Cannot delete expense: The expense list is empty", exception.getMessage());
    }
    
    @Test
    void deleteIncome_validIndex_deletesAndReturnsIncome() {
        fm.addIncome(sampleIncome1); // 2025-01-01
        fm.addIncome(sampleIncome2); // 2025-01-15 (newest)

        // Delete the newest income (index 1 in newest-first view)
        Income deleted = fm.deleteIncome(1);
        assertEquals(sampleIncome2, deleted);
        
        // Verify remaining income
        assertEquals(5000.0, fm.getTotalIncome()); // Only sampleIncome1 remains
        
        // Delete remaining income
        deleted = fm.deleteIncome(1);
        assertEquals(sampleIncome1, deleted);
        assertEquals(0.0, fm.getTotalIncome());
    }
    
    @Test
    void deleteIncome_indexTooLow_throwsException() {
        fm.addIncome(sampleIncome1);
        
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            fm.deleteIncome(0);
        });
        assertEquals("Income index out of range. Valid range: 1 to 1", exception.getMessage());
    }
    
    @Test
    void deleteIncome_indexTooHigh_throwsException() {
        fm.addIncome(sampleIncome1);
        
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            fm.deleteIncome(2);
        });
        assertEquals("Income index out of range. Valid range: 1 to 1", exception.getMessage());
    }
    
    @Test
    void deleteIncome_emptyList_throwsException() {
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            fm.deleteIncome(1);
        });
        assertEquals("Cannot delete income: The income list is empty", exception.getMessage());
    }
    
    @Test
    void complexScenario_addDeleteCalculateBalance() {
        // Add some initial data
        fm.addIncome(sampleIncome1); // 5000
        fm.addIncome(sampleIncome2); // 200
        fm.addExpense(sampleExpense1); // 1200
        fm.addExpense(sampleExpense2); // 50
        fm.addExpense(sampleExpense3); // 30
        
        // Initial state: income=5200, expense=1280, balance=3920
        assertEquals(5200.0, fm.getTotalIncome());
        assertEquals(1280.0, fm.getTotalExpense());
        assertEquals(3920.0, fm.getBalance());
        
        // Delete an expense (newest first, so index 1 = sampleExpense3)
        fm.deleteExpense(1);
        assertEquals(5200.0, fm.getTotalIncome());
        assertEquals(1250.0, fm.getTotalExpense()); // 1280 - 30
        assertEquals(3950.0, fm.getBalance());
        
        // Delete an income (newest first, so index 1 = sampleIncome2)
        fm.deleteIncome(1);
        assertEquals(5000.0, fm.getTotalIncome()); // 5200 - 200
        assertEquals(1250.0, fm.getTotalExpense());
        assertEquals(3750.0, fm.getBalance());
    }
    
    @Test
    void deleteMultipleExpenses_maintainsCorrectIndexing() {
        // Add expenses in chronological order
        Expense e1 = new Expense(100.0, ExpenseCategory.FOOD, LocalDate.parse("2025-01-01"), "First");
        Expense e2 = new Expense(200.0, ExpenseCategory.FOOD, LocalDate.parse("2025-01-02"), "Second");
        Expense e3 = new Expense(300.0, ExpenseCategory.FOOD, LocalDate.parse("2025-01-03"), "Third");
        
        fm.addExpense(e1);
        fm.addExpense(e2);
        fm.addExpense(e3);
        
        // View should be: [e3(newest), e2, e1(oldest)]
        List<Expense> view = fm.getExpensesView();
        assertEquals(e3, view.get(0)); // index 1
        assertEquals(e2, view.get(1)); // index 2
        assertEquals(e1, view.get(2)); // index 3
        
        // Delete index 2 (e2)
        Expense deleted = fm.deleteExpense(2);
        assertEquals(e2, deleted);
        
        // New view should be: [e3, e1]
        view = fm.getExpensesView();
        assertEquals(2, view.size());
        assertEquals(e3, view.get(0)); // still index 1
        assertEquals(e1, view.get(1)); // now index 2
    }

    @Test
    void deleteMultipleIncomes_maintainsCorrectIndexingAndTotals() {
        // Add incomes in chronological order
        Income i1 = new Income(
                1000.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-01-01"),
                "January salary");
        Income i2 = new Income(
                1500.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-01-10"),
                "Mid-month bonus");
        Income i3 = new Income(
                2000.0,
                IncomeCategory.SCHOLARSHIP,
                LocalDate.parse("2025-01-20"),
                "Scholarship disbursement");

        fm.addIncome(i1);
        fm.addIncome(i2);
        fm.addIncome(i3);

        // View should be: [i3 (newest), i2, i1 (oldest)]
        List<Income> view = fm.getIncomesView();
        assertEquals(3, view.size());
        assertEquals(i3, view.get(0)); // index 1
        assertEquals(i2, view.get(1)); // index 2
        assertEquals(i1, view.get(2)); // index 3

        // Check total before deletions
        assertEquals(4500.0, fm.getTotalIncome(), 1e-9);

        // Delete index 2 (i2 = mid-month bonus)
        Income deleted = fm.deleteIncome(2);
        assertEquals(i2, deleted);
        assertEquals(3000.0, fm.getTotalIncome(), 1e-9); // 4500 - 1500

        // Verify new order after deletion
        view = fm.getIncomesView();
        assertEquals(2, view.size());
        assertEquals(i3, view.get(0)); // still newest
        assertEquals(i1, view.get(1)); // now oldest

        // Delete index 1 (i3 = newest)
        deleted = fm.deleteIncome(1);
        assertEquals(i3, deleted);
        assertEquals(1000.0, fm.getTotalIncome(), 1e-9); // 3000 - 2000

        // Final check: only oldest income remains
        view = fm.getIncomesView();
        assertEquals(1, view.size());
        assertEquals(i1, view.get(0));

        // Delete last remaining income
        deleted = fm.deleteIncome(1);
        assertEquals(i1, deleted);
        assertEquals(0.0, fm.getTotalIncome(), 1e-9);
        assertTrue(fm.getIncomesView().isEmpty());
    }


    @Test
    void getExpensesViewForMonth_filtersAndOrdersNewestFirst_unmodifiable() {
        // Jan expenses (3 items)
        fm.addExpense(sampleExpense1); // 2025-01-01
        fm.addExpense(sampleExpense2); // 2025-01-02
        fm.addExpense(sampleExpense3); // 2025-01-03
        // Add a Feb expense to ensure filtering works
        Expense febRent = new Expense(1300.0, ExpenseCategory.RENT,
                LocalDate.parse("2025-02-01"), "Feb rent");
        fm.addExpense(febRent);

        var viewJan = fm.getExpensesViewForMonth(java.time.YearMonth.of(2025, 1));

        // Only Jan entries, newest-first
        assertEquals(3, viewJan.size());
        assertEquals(sampleExpense3, viewJan.get(0)); // 2025-01-03
        assertEquals(sampleExpense2, viewJan.get(1)); // 2025-01-02
        assertEquals(sampleExpense1, viewJan.get(2)); // 2025-01-01

        // Unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> viewJan.add(sampleExpense1));
    }

    @Test
    void getIncomesViewForMonth_filtersAndOrdersNewestFirst_unmodifiable() {
        // Jan incomes (2 items)
        fm.addIncome(sampleIncome1); // 2025-01-01
        fm.addIncome(sampleIncome2); // 2025-01-15 (newest in Jan)
        // Add a Feb income to ensure filtering works
        Income febSalary = new Income(4800.0, IncomeCategory.SALARY,
                LocalDate.parse("2025-02-01"), "Feb pay");
        fm.addIncome(febSalary);

        var viewJan = fm.getIncomesViewForMonth(java.time.YearMonth.of(2025, 1));

        // Only Jan entries, newest-first
        assertEquals(2, viewJan.size());
        assertEquals(sampleIncome2, viewJan.get(0)); // 2025-01-15
        assertEquals(sampleIncome1, viewJan.get(1)); // 2025-01-01

        // Unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> viewJan.add(sampleIncome1));
    }

    @Test
    void getExpensesViewForMonth_noData_returnsEmptyList() {
        // Only add February data
        Expense febRent = new Expense(1300.0, ExpenseCategory.RENT,
                LocalDate.parse("2025-02-01"), "Feb rent");
        fm.addExpense(febRent);

        var viewMar = fm.getExpensesViewForMonth(java.time.YearMonth.of(2025, 3));
        assertTrue(viewMar.isEmpty());
    }

    @Test
    void getIncomesViewForMonth_noData_returnsEmptyList() {
        // Only add March income
        Income marSalary = new Income(4900.0, IncomeCategory.SALARY,
                LocalDate.parse("2025-03-01"), "March pay");
        fm.addIncome(marSalary);

        var viewJan = fm.getIncomesViewForMonth(java.time.YearMonth.of(2025, 1));
        assertTrue(viewJan.isEmpty());
    }

    @Test
    void monthlyTotals_matchExpected_whenSummedFromMonthlyViews() {
        // Jan
        fm.addIncome(sampleIncome1); // 5000 on 2025-01-01
        fm.addIncome(sampleIncome2); // 200 on 2025-01-15
        fm.addExpense(sampleExpense1); // 1200 on 2025-01-01
        fm.addExpense(sampleExpense2); // 50 on 2025-01-02
        fm.addExpense(sampleExpense3); // 30 on 2025-01-03

        // Feb
        Income febIncome = new Income(300.0, IncomeCategory.GIFT,
                LocalDate.parse("2025-02-10"), null);
        Expense febRent = new Expense(1300.0, ExpenseCategory.RENT,
                LocalDate.parse("2025-02-01"), "Feb rent");
        fm.addIncome(febIncome);
        fm.addExpense(febRent);

        // Check Jan totals via monthly views
        var jan = java.time.YearMonth.of(2025, 1);
        double janIncome = fm.getIncomesViewForMonth(jan)
                .stream().mapToDouble(Income::getAmount).sum();
        double janExpense = fm.getExpensesViewForMonth(jan)
                .stream().mapToDouble(Expense::getAmount).sum();

        assertEquals(5200.0, janIncome, 1e-9);  // 5000 + 200
        assertEquals(1280.0, janExpense, 1e-9); // 1200 + 50 + 30

        // Check Feb totals
        var feb = java.time.YearMonth.of(2025, 2);
        double febIncomeSum = fm.getIncomesViewForMonth(feb)
                .stream().mapToDouble(Income::getAmount).sum();
        double febExpenseSum = fm.getExpensesViewForMonth(feb)
                .stream().mapToDouble(Expense::getAmount).sum();

        assertEquals(300.0, febIncomeSum, 1e-9);
        assertEquals(1300.0, febExpenseSum, 1e-9);
    }

    // ============ Tests for Budget Management ============

    @Test
    void setBudget_valid_setsBudgetSuccessfully() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);
        assertEquals(100.0, fm.getBudgetForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void setBudget_update_updatesBudgetSuccessfully() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);
        fm.setBudget(ExpenseCategory.FOOD, 150.0);
        assertEquals(150.0, fm.getBudgetForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void setBudget_setZero_setsBudgetToZero() {
        fm.setBudget(ExpenseCategory.TRANSPORT, 0.0);
        assertEquals(0.0, fm.getBudgetForCategory(ExpenseCategory.TRANSPORT));
    }

    @Test
    void setBudget_nullCategory_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fm.setBudget(null, 100.0);
        });
        assertEquals("Category cannot be null", exception.getMessage());
    }

    @Test
    void setBudget_negativeAmount_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fm.setBudget(ExpenseCategory.FOOD, -50.0);
        });
        assertEquals("Amount cannot be negative", exception.getMessage());
    }

    @Test
    void getBudgetForCategory_noBudgetSet_returnsNull() {
        assertNull(fm.getBudgetForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void getBudgetsView_noBudgets_returnsEmptyMap() {
        assertTrue(fm.getBudgetsView().isEmpty());
    }

    @Test
    void getBudgetsView_withBudgets_returnsUnmodifiableMap() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);
        fm.setBudget(ExpenseCategory.RENT, 500.0);

        Map<ExpenseCategory, Double> budgets = fm.getBudgetsView();
        assertEquals(2, budgets.size());
        assertEquals(100.0, budgets.get(ExpenseCategory.FOOD));

        // Verify unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> {
            budgets.put(ExpenseCategory.TRANSPORT, 50.0);
        });
    }

    @Test
    void getTotalExpenseForCategory_noExpenses_returnsZero() {
        assertEquals(0.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void getTotalExpenseForCategory_withExpenses_returnsCorrectSum() {
        fm.addExpense(sampleExpense2); // 50 FOOD
        fm.addExpense(new Expense(30.0, ExpenseCategory.FOOD, LocalDate.now(), "Snack"));
        fm.addExpense(sampleExpense1); // 1200 RENT

        assertEquals(80.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void getTotalExpenseForCategory_unrelatedCategory_returnsZero() {
        fm.addExpense(sampleExpense1); // 1200 RENT
        assertEquals(0.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void getTotalExpenseForCategory_nullCategory_returnsZero() {
        fm.addExpense(sampleExpense1);
        assertEquals(0.0, fm.getTotalExpenseForCategory(null));
    }

    @Test
    void addExpense_noBudgetSet_returnsNoBudgetStatus() {
        BudgetStatus status = fm.addExpense(sampleExpense2); // 50 FOOD
        assertFalse(status.isOverBudget());
        assertFalse(status.isNearBudget());
    }

    @Test
    void addExpense_budgetSetStaysUnder_returnsNoBudgetStatus() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);
        BudgetStatus status1 = fm.addExpense(sampleExpense2); // 50 FOOD
        assertFalse(status1.isOverBudget());
        assertFalse(status1.isNearBudget());

        BudgetStatus status2 = fm.addExpense(
                new Expense(30.0, ExpenseCategory.FOOD, LocalDate.now(), "Snack"));
        assertFalse(status2.isOverBudget());
        assertFalse(status2.isNearBudget());
        assertEquals(80.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void addExpense_budgetSetHitsExactly_returnsNoBudgetStatus() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);
        fm.addExpense(sampleExpense2); // 50 FOOD
        BudgetStatus status = fm.addExpense(
                new Expense(50.0, ExpenseCategory.FOOD, LocalDate.now(), "Dinner"));
        assertFalse(status.isOverBudget());
        assertTrue(status.isNearBudget());
        assertEquals(100.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void addExpense_budgetSetCrossesThreshold_returnsTrue() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);
        fm.addExpense(sampleExpense2); // 50 FOOD
        // This expense (80) will push the total (50+80=130) over the 100 budget
        BudgetStatus status = fm.addExpense(
                new Expense(80.0, ExpenseCategory.FOOD, LocalDate.now(), "Big meal"));
        assertTrue(status.isOverBudget());
        assertFalse(status.isNearBudget());
        assertEquals(130.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void addExpense_budgetSetAlreadyExceeded_stillReturnsOverBudget() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);
        // 1. Cross the budget
        BudgetStatus status1 = fm.addExpense(
                new Expense(110.0, ExpenseCategory.FOOD, LocalDate.now(), "Big meal"));
        assertTrue(status1.isOverBudget());
        assertFalse(status1.isNearBudget());
        assertEquals(110.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));

        // 2. Add another expense while already over - should still warn
        BudgetStatus status2 = fm.addExpense(
                new Expense(20.0, ExpenseCategory.FOOD, LocalDate.now(), "Snack"));
        assertTrue(status2.isOverBudget()); // Now returns true every time
        assertFalse(status2.isNearBudget());
        assertEquals(130.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void addExpense_budgetSetUnrelatedCategory_returnsNoBudgetStatus() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);
        // Add an expense for a different category
        BudgetStatus status = fm.addExpense(sampleExpense1); // 1200 RENT
        assertFalse(status.isOverBudget());
        assertFalse(status.isNearBudget());
        assertEquals(0.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
        assertEquals(1200.0, fm.getTotalExpenseForCategory(ExpenseCategory.RENT));
    }

    @Test
    void addExpense_budgetSetToZero_returnsOverBudgetOnFirstExpense() {
        fm.setBudget(ExpenseCategory.FOOD, 0.0);

        // Any expense > 0 will cross the 0.0 budget
        BudgetStatus status1 = fm.addExpense(
                new Expense(1.0, ExpenseCategory.FOOD, LocalDate.now(), "Gum"));
        assertTrue(status1.isOverBudget());
        assertFalse(status1.isNearBudget());

        // Adding a second expense should still return over budget
        BudgetStatus status2 = fm.addExpense(
                new Expense(5.0, ExpenseCategory.FOOD, LocalDate.now(), "Coffee"));
        assertTrue(status2.isOverBudget());
        assertFalse(status2.isNearBudget());
        assertEquals(6.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void addExpense_nearBudgetThreshold_returnsNearBudget() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);

        // Add expense that brings us to 90% of budget (exactly at threshold)
        BudgetStatus status = fm.addExpense(
                new Expense(90.0, ExpenseCategory.FOOD, LocalDate.now(), "Big meal"));
        assertFalse(status.isOverBudget());
        assertTrue(status.isNearBudget());
        assertEquals(90.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void addExpense_nearBudgetThreshold95Percent_returnsNearBudget() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);

        // Add expense that brings us to 95% of budget
        BudgetStatus status = fm.addExpense(
                new Expense(95.0, ExpenseCategory.FOOD, LocalDate.now(), "Big meal"));
        assertFalse(status.isOverBudget());
        assertTrue(status.isNearBudget());
        assertEquals(95.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void addExpense_justBelowNearBudgetThreshold_returnsNoBudgetStatus() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);

        // Add expense that brings us to 89% of budget (just below 90% threshold)
        BudgetStatus status = fm.addExpense(
                new Expense(89.0, ExpenseCategory.FOOD, LocalDate.now(), "Meal"));
        assertFalse(status.isOverBudget());
        assertFalse(status.isNearBudget());
        assertEquals(89.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void addExpense_multipleExpensesReachNearBudget_returnsNearBudget() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);

        // Add first expense
        BudgetStatus status1 = fm.addExpense(
                new Expense(50.0, ExpenseCategory.FOOD, LocalDate.now(), "Lunch"));
        assertFalse(status1.isOverBudget());
        assertFalse(status1.isNearBudget());

        // Add second expense that pushes us into near-budget zone
        BudgetStatus status2 = fm.addExpense(
                new Expense(42.0, ExpenseCategory.FOOD, LocalDate.now(), "Dinner"));
        assertFalse(status2.isOverBudget());
        assertTrue(status2.isNearBudget());
        assertEquals(92.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void deleteBudget_valid_deletesSuccessfully() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);
        assertEquals(100.0, fm.getBudgetForCategory(ExpenseCategory.FOOD));

        fm.deleteBudget(ExpenseCategory.FOOD);

        assertNull(fm.getBudgetForCategory(ExpenseCategory.FOOD));
        assertFalse(fm.getBudgetsView().containsKey(ExpenseCategory.FOOD));
    }

    @Test
    void deleteBudget_multipleBudgets_deletesOnlyOne() {
        fm.setBudget(ExpenseCategory.FOOD, 100.0);
        fm.setBudget(ExpenseCategory.RENT, 500.0);

        fm.deleteBudget(ExpenseCategory.FOOD);

        assertNull(fm.getBudgetForCategory(ExpenseCategory.FOOD));
        assertEquals(500.0, fm.getBudgetForCategory(ExpenseCategory.RENT));
        assertEquals(1, fm.getBudgetsView().size());
    }

    @Test
    void deleteBudget_nullCategory_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fm.deleteBudget(null);
        });
        assertEquals("Category cannot be null", exception.getMessage());
    }

    @Test
    void deleteBudget_nonExistentBudget_throwsException() {
        // No budget set for TRANSPORT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fm.deleteBudget(ExpenseCategory.TRANSPORT);
        });
        assertEquals("No budget was set for category: TRANSPORT", exception.getMessage());
    }

    @Test
    void deleteBudget_deleteTwice_throwsException() {
        fm.setBudget(ExpenseCategory.BILLS, 50.0);
        fm.deleteBudget(ExpenseCategory.BILLS); // First delete is fine

        // Second delete should fail
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fm.deleteBudget(ExpenseCategory.BILLS);
        });
        assertEquals("No budget was set for category: BILLS", exception.getMessage());
    }

    // ============ Tests for modifyExpense ============

    @Test
    void modifyExpense_validIndex_replacesExpenseSuccessfully() {
        fm.addExpense(sampleExpense1); // 1200 RENT on 2025-01-01
        fm.addExpense(sampleExpense2); // 50 FOOD on 2025-01-02
        fm.addExpense(sampleExpense3); // 30 TRANSPORT on 2025-01-03

        // Modify the newest expense (index 1 = sampleExpense3)
        Expense newExpense = new Expense(100.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-05"), "Modified expense");

        BudgetStatus status = fm.modifyExpense(1, newExpense);

        // Verify the expense was replaced
        List<Expense> expenses = fm.getExpensesView();
        assertEquals(3, expenses.size());
        
        // The new expense should now be the newest (2025-01-05)
        assertEquals(newExpense, expenses.get(0));
        assertEquals(100.0, newExpense.getAmount());
        assertEquals(ExpenseCategory.FOOD, newExpense.getCategory());
        assertEquals("Modified expense", newExpense.getDescription());

        // Verify total expense updated: 1200 + 50 + 100 = 1350 (old 30 was deleted)
        assertEquals(1350.0, fm.getTotalExpense());
    }

    @Test
    void modifyExpense_emptyList_throwsException() {
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            Expense newExpense = new Expense(100.0, ExpenseCategory.FOOD,
                    LocalDate.now(), "Test");
            fm.modifyExpense(1, newExpense);
        });
        assertEquals("Cannot modify expense: The expense list is empty", exception.getMessage());
    }

    @Test
    void modifyExpense_indexTooLow_throwsException() {
        fm.addExpense(sampleExpense1);

        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            Expense newExpense = new Expense(100.0, ExpenseCategory.FOOD,
                    LocalDate.now(), "Test");
            fm.modifyExpense(0, newExpense);
        });
        assertEquals("Expense index out of range. Valid range: 1 to 1", exception.getMessage());

        // Verify original expense is unchanged
        assertEquals(1, fm.getExpensesView().size());
        assertEquals(sampleExpense1, fm.getExpensesView().get(0));
    }

    @Test
    void modifyExpense_indexTooHigh_throwsException() {
        fm.addExpense(sampleExpense1);

        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            Expense newExpense = new Expense(100.0, ExpenseCategory.FOOD,
                    LocalDate.now(), "Test");
            fm.modifyExpense(2, newExpense);
        });
        assertEquals("Expense index out of range. Valid range: 1 to 1", exception.getMessage());

        // Verify original expense is unchanged
        assertEquals(1, fm.getExpensesView().size());
        assertEquals(sampleExpense1, fm.getExpensesView().get(0));
    }

    @Test
    void modifyExpense_exceedsBudget_returnsOverBudget() {
        // Set a budget for FOOD category
        fm.setBudget(ExpenseCategory.FOOD, 100.0);

        // Add an initial FOOD expense below budget
        fm.addExpense(new Expense(50.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"), "First food"));

        // Modify to exceed budget
        Expense newExpense = new Expense(200.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-02"), "Expensive meal");

        BudgetStatus status = fm.modifyExpense(1, newExpense);

        assertTrue(status.isOverBudget());
        assertFalse(status.isNearBudget());
        assertEquals(200.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void modifyExpense_withinBudget_returnsNoBudgetStatus() {
        // Set a budget for FOOD category
        fm.setBudget(ExpenseCategory.FOOD, 200.0);

        // Add an initial FOOD expense
        fm.addExpense(new Expense(50.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"), "First food"));

        // Modify but stay within budget
        Expense newExpense = new Expense(100.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-02"), "Regular meal");

        BudgetStatus status = fm.modifyExpense(1, newExpense);

        assertFalse(status.isOverBudget());
        assertFalse(status.isNearBudget());
        assertEquals(100.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    void modifyExpense_toNearBudget_returnsNearBudget() {
        // Set a budget for FOOD category
        fm.setBudget(ExpenseCategory.FOOD, 100.0);

        // Add an initial FOOD expense
        fm.addExpense(new Expense(50.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"), "First food"));

        // Modify to bring us to 92% of budget
        Expense newExpense = new Expense(92.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-02"), "Big meal");

        BudgetStatus status = fm.modifyExpense(1, newExpense);

        assertFalse(status.isOverBudget());
        assertTrue(status.isNearBudget());
        assertEquals(92.0, fm.getTotalExpenseForCategory(ExpenseCategory.FOOD));
    }

    @Test
    void modifyExpense_multipleModifications_maintainsCorrectOrder() {
        // Add expenses in chronological order
        Expense e1 = new Expense(100.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"), "First");
        Expense e2 = new Expense(200.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-02"), "Second");
        Expense e3 = new Expense(300.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-03"), "Third");

        fm.addExpense(e1);
        fm.addExpense(e2);
        fm.addExpense(e3);

        // View should be: [e3(newest), e2, e1(oldest)]
        List<Expense> view = fm.getExpensesView();
        assertEquals(e3, view.get(0)); // index 1
        assertEquals(e2, view.get(1)); // index 2
        assertEquals(e1, view.get(2)); // index 3

        // Modify index 2 (e2) with a newer date
        Expense modified = new Expense(250.0, ExpenseCategory.TRANSPORT,
                LocalDate.parse("2025-01-05"), "Modified second");
        fm.modifyExpense(2, modified);

        // New view should have modified expense as newest
        view = fm.getExpensesView();
        assertEquals(3, view.size());
        assertEquals(modified, view.get(0)); // Now newest (2025-01-05)
        assertEquals(e3, view.get(1)); // 2025-01-03
        assertEquals(e1, view.get(2)); // 2025-01-01 (still oldest)

        // Verify total: 100 + 300 + 250 = 650 (old e2=200 was deleted)
        assertEquals(650.0, fm.getTotalExpense());
    }

    // ============ Tests for modifyIncome ============

    @Test
    void modifyIncome_validIndex_replacesIncomeSuccessfully() {
        fm.addIncome(sampleIncome1); // 5000 SALARY on 2025-01-01
        fm.addIncome(sampleIncome2); // 200 SCHOLARSHIP on 2025-01-15

        // Modify the newest income (index 1 = sampleIncome2)
        Income newIncome = new Income(500.0, IncomeCategory.INVESTMENT,
                LocalDate.parse("2025-01-20"), "Modified income");
        
        fm.modifyIncome(1, newIncome);

        // Verify the income was replaced
        List<Income> incomes = fm.getIncomesView();
        assertEquals(2, incomes.size());
        
        // The new income should now be the newest (2025-01-20)
        assertEquals(newIncome, incomes.get(0));
        assertEquals(500.0, newIncome.getAmount());
        assertEquals(IncomeCategory.INVESTMENT, newIncome.getCategory());
        assertEquals("Modified income", newIncome.getDescription());

        // Verify total income updated: 5000 + 500 = 5500 (old 200 was deleted)
        assertEquals(5500.0, fm.getTotalIncome());
    }

    @Test
    void modifyIncome_emptyList_throwsException() {
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            Income newIncome = new Income(1000.0, IncomeCategory.SALARY,
                    LocalDate.now(), "Test");
            fm.modifyIncome(1, newIncome);
        });
        assertEquals("Cannot modify income: The income list is empty", exception.getMessage());
    }

    @Test
    void modifyIncome_indexTooLow_throwsException() {
        fm.addIncome(sampleIncome1);

        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            Income newIncome = new Income(1000.0, IncomeCategory.SALARY,
                    LocalDate.now(), "Test");
            fm.modifyIncome(0, newIncome);
        });
        assertEquals("Income index out of range. Valid range: 1 to 1", exception.getMessage());

        // Verify original income is unchanged
        assertEquals(1, fm.getIncomesView().size());
        assertEquals(sampleIncome1, fm.getIncomesView().get(0));
    }

    @Test
    void modifyIncome_indexTooHigh_throwsException() {
        fm.addIncome(sampleIncome1);

        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            Income newIncome = new Income(1000.0, IncomeCategory.SALARY,
                    LocalDate.now(), "Test");
            fm.modifyIncome(2, newIncome);
        });
        assertEquals("Income index out of range. Valid range: 1 to 1", exception.getMessage());

        // Verify original income is unchanged
        assertEquals(1, fm.getIncomesView().size());
        assertEquals(sampleIncome1, fm.getIncomesView().get(0));
    }

    @Test
    void modifyIncome_multipleModifications_maintainsCorrectOrder() {
        // Add incomes in chronological order
        Income i1 = new Income(1000.0, IncomeCategory.SALARY,
                LocalDate.parse("2025-01-01"), "First");
        Income i2 = new Income(2000.0, IncomeCategory.SALARY,
                LocalDate.parse("2025-01-10"), "Second");
        Income i3 = new Income(3000.0, IncomeCategory.SCHOLARSHIP,
                LocalDate.parse("2025-01-20"), "Third");

        fm.addIncome(i1);
        fm.addIncome(i2);
        fm.addIncome(i3);

        // View should be: [i3(newest), i2, i1(oldest)]
        List<Income> view = fm.getIncomesView();
        assertEquals(i3, view.get(0)); // index 1
        assertEquals(i2, view.get(1)); // index 2
        assertEquals(i1, view.get(2)); // index 3

        // Modify index 2 (i2) with a newer date
        Income modified = new Income(2500.0, IncomeCategory.INVESTMENT,
                LocalDate.parse("2025-01-25"), "Modified second");
        fm.modifyIncome(2, modified);

        // New view should have modified income as newest
        view = fm.getIncomesView();
        assertEquals(3, view.size());
        assertEquals(modified, view.get(0)); // Now newest (2025-01-25)
        assertEquals(i3, view.get(1)); // 2025-01-20
        assertEquals(i1, view.get(2)); // 2025-01-01 (still oldest)

        // Verify total: 1000 + 3000 + 2500 = 6500 (old i2=2000 was deleted)
        assertEquals(6500.0, fm.getTotalIncome());
    }

    @Test
    void modifyIncome_changesCategory_updatesCorrectly() {
        fm.addIncome(sampleIncome1); // SALARY

        // Modify category
        Income newIncome = new Income(5000.0, IncomeCategory.INVESTMENT,
                LocalDate.parse("2025-01-01"), "Changed to investment");
        fm.modifyIncome(1, newIncome);

        List<Income> incomes = fm.getIncomesView();
        assertEquals(1, incomes.size());
        assertEquals(IncomeCategory.INVESTMENT, incomes.get(0).getCategory());
    }

    @Test
    void modifyExpense_changesCategory_updatesCorrectly() {
        fm.addExpense(sampleExpense1); // RENT

        // Modify category
        Expense newExpense = new Expense(1200.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"), "Changed to food");
        fm.modifyExpense(1, newExpense);

        List<Expense> expenses = fm.getExpensesView();
        assertEquals(1, expenses.size());
        assertEquals(ExpenseCategory.FOOD, expenses.get(0).getCategory());
    }

    // ============ Tests for atomic modify operations (rollback) ============

    @Test
    void modifyExpense_addFailsWithNull_revertsToOriginal() {
        fm.addExpense(sampleExpense1); // 1200 RENT on 2025-01-01
        fm.addExpense(sampleExpense2); // 50 FOOD on 2025-01-02

        // Verify initial state
        assertEquals(2, fm.getExpensesView().size());
        assertEquals(1250.0, fm.getTotalExpense());

        // Try to modify with null expense (should fail and revert)
        assertThrows(IllegalArgumentException.class, () -> {
            fm.modifyExpense(1, null);
        });

        // Verify the list is unchanged (rolled back)
        List<Expense> expenses = fm.getExpensesView();
        assertEquals(2, expenses.size());
        assertEquals(1250.0, fm.getTotalExpense());
        assertEquals(sampleExpense2, expenses.get(0)); // Still the newest
        assertEquals(sampleExpense1, expenses.get(1)); // Still the oldest
    }

    @Test
    void modifyExpense_addFailsWithInvalidData_revertsToOriginal() {
        fm.addExpense(sampleExpense1); // 1200 RENT on 2025-01-01
        fm.addExpense(sampleExpense2); // 50 FOOD on 2025-01-02
        fm.addExpense(sampleExpense3); // 30 TRANSPORT on 2025-01-03

        // Verify initial state
        assertEquals(3, fm.getExpensesView().size());
        double initialTotal = fm.getTotalExpense();
        assertEquals(1280.0, initialTotal);

        // Try to modify with invalid expense (negative amount should fail validation)
        // Note: We need to create an expense that passes the constructor but fails on add
        // Since the Expense constructor itself validates, we'll test with null
        assertThrows(IllegalArgumentException.class, () -> {
            fm.modifyExpense(2, null);
        });

        // Verify the list is unchanged (rolled back)
        List<Expense> expenses = fm.getExpensesView();
        assertEquals(3, expenses.size());
        assertEquals(initialTotal, fm.getTotalExpense());
        // Verify order is preserved
        assertEquals(sampleExpense3, expenses.get(0)); // Newest
        assertEquals(sampleExpense2, expenses.get(1)); // Middle
        assertEquals(sampleExpense1, expenses.get(2)); // Oldest
    }

    @Test
    void modifyIncome_addFailsWithNull_revertsToOriginal() {
        fm.addIncome(sampleIncome1); // 5000 SALARY on 2025-01-01
        fm.addIncome(sampleIncome2); // 200 SCHOLARSHIP on 2025-01-15

        // Verify initial state
        assertEquals(2, fm.getIncomesView().size());
        assertEquals(5200.0, fm.getTotalIncome());

        // Try to modify with null income (should fail and revert)
        assertThrows(IllegalArgumentException.class, () -> {
            fm.modifyIncome(1, null);
        });

        // Verify the list is unchanged (rolled back)
        List<Income> incomes = fm.getIncomesView();
        assertEquals(2, incomes.size());
        assertEquals(5200.0, fm.getTotalIncome());
        assertEquals(sampleIncome2, incomes.get(0)); // Still the newest
        assertEquals(sampleIncome1, incomes.get(1)); // Still the oldest
    }

    @Test
    void modifyIncome_addFailsWithInvalidData_revertsToOriginal() {
        fm.addIncome(sampleIncome1); // 5000 SALARY on 2025-01-01
        fm.addIncome(sampleIncome2); // 200 SCHOLARSHIP on 2025-01-15

        Income extraIncome = new Income(1000.0, IncomeCategory.INVESTMENT,
                LocalDate.parse("2025-01-10"), "Extra");
        fm.addIncome(extraIncome);

        // Verify initial state
        assertEquals(3, fm.getIncomesView().size());
        double initialTotal = fm.getTotalIncome();
        assertEquals(6200.0, initialTotal);

        // Try to modify with null income (should fail and revert)
        assertThrows(IllegalArgumentException.class, () -> {
            fm.modifyIncome(2, null);
        });

        // Verify the list is unchanged (rolled back)
        List<Income> incomes = fm.getIncomesView();
        assertEquals(3, incomes.size());
        assertEquals(initialTotal, fm.getTotalIncome());
        // Verify order is preserved
        assertEquals(sampleIncome2, incomes.get(0)); // Newest (2025-01-15)
        assertEquals(extraIncome, incomes.get(1));   // Middle (2025-01-10)
        assertEquals(sampleIncome1, incomes.get(2)); // Oldest (2025-01-01)
    }

    @Test
    void modifyExpense_rollbackMaintainsCorrectOrder() {
        // Create expenses with specific dates
        Expense e1 = new Expense(100.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"), "First");
        Expense e2 = new Expense(200.0, ExpenseCategory.TRANSPORT,
                LocalDate.parse("2025-01-05"), "Second");
        Expense e3 = new Expense(300.0, ExpenseCategory.RENT,
                LocalDate.parse("2025-01-10"), "Third");

        fm.addExpense(e1);
        fm.addExpense(e2);
        fm.addExpense(e3);

        // Initial order: [e3 (Jan-10), e2 (Jan-05), e1 (Jan-01)]
        List<Expense> initial = fm.getExpensesView();
        assertEquals(e3, initial.get(0));
        assertEquals(e2, initial.get(1));
        assertEquals(e1, initial.get(2));

        // Try to modify e2 with null (should fail and rollback)
        assertThrows(IllegalArgumentException.class, () -> {
            fm.modifyExpense(2, null); // Index 2 is e2
        });

        // Verify order is maintained after rollback
        List<Expense> afterRollback = fm.getExpensesView();
        assertEquals(3, afterRollback.size());
        assertEquals(e3, afterRollback.get(0)); // Still newest
        assertEquals(e2, afterRollback.get(1)); // Still middle (not deleted!)
        assertEquals(e1, afterRollback.get(2)); // Still oldest
        assertEquals(600.0, fm.getTotalExpense()); // Total unchanged
    }

    @Test
    void modifyIncome_rollbackMaintainsCorrectOrder() {
        // Create incomes with specific dates
        Income i1 = new Income(1000.0, IncomeCategory.SALARY,
                LocalDate.parse("2025-01-01"), "First");
        Income i2 = new Income(2000.0, IncomeCategory.INVESTMENT,
                LocalDate.parse("2025-01-05"), "Second");
        Income i3 = new Income(3000.0, IncomeCategory.SCHOLARSHIP,
                LocalDate.parse("2025-01-10"), "Third");

        fm.addIncome(i1);
        fm.addIncome(i2);
        fm.addIncome(i3);

        // Initial order: [i3 (Jan-10), i2 (Jan-05), i1 (Jan-01)]
        List<Income> initial = fm.getIncomesView();
        assertEquals(i3, initial.get(0));
        assertEquals(i2, initial.get(1));
        assertEquals(i1, initial.get(2));

        // Try to modify i2 with null (should fail and rollback)
        assertThrows(IllegalArgumentException.class, () -> {
            fm.modifyIncome(2, null); // Index 2 is i2
        });

        // Verify order is maintained after rollback
        List<Income> afterRollback = fm.getIncomesView();
        assertEquals(3, afterRollback.size());
        assertEquals(i3, afterRollback.get(0)); // Still newest
        assertEquals(i2, afterRollback.get(1)); // Still middle (not deleted!)
        assertEquals(i1, afterRollback.get(2)); // Still oldest
        assertEquals(6000.0, fm.getTotalIncome()); // Total unchanged
    }

    // ============ Tests for summary ============
    @Test
    void getExpenseByCategory_empty_returnEmptyMap() {
        Map<ExpenseCategory, Double> testMap = fm.getExpenseByCategory();
        assertTrue(testMap.isEmpty());
    }

    @Test
    void getExpenseByCategory_accumulatesAmountsPerCategory() {
        fm.addExpense(new Expense(10.00, ExpenseCategory.FOOD, LocalDate.of(2025, 10, 1), "Lunch"));
        fm.addExpense(new Expense(5.00, ExpenseCategory.TRANSPORT, LocalDate.of(2025, 10, 2), "Bus"));
        fm.addExpense(new Expense(2.50, ExpenseCategory.FOOD, LocalDate.of(2025, 10, 3), "Snack"));

        Map<ExpenseCategory, Double> testMap = fm.getExpenseByCategory();

        // Only the categories used should appear
        assertEquals(2, testMap.size());
        // Totals per category
        assertEquals(12.50, testMap.get(ExpenseCategory.FOOD));
        assertEquals(5.00, testMap.get(ExpenseCategory.TRANSPORT));
    }

    @Test
    void getIncomeByCategory_empty_returnsEmptyMap() {
        Map<IncomeCategory, Double> testMap = fm.getIncomeByCategory();
        assertTrue(testMap.isEmpty());
    }

    @Test
    void getIncomeByCategory_accumulatesAmountsPerCategory() {
        fm.addIncome(new Income(1000.00, IncomeCategory.SALARY, LocalDate.of(2025, 10, 5), "Monies"));
        fm.addIncome(new Income(50.50, IncomeCategory.INVESTMENT, LocalDate.of(2025, 10, 6), "AMD"));
        fm.addIncome(new Income(500.00, IncomeCategory.SALARY, LocalDate.of(2025, 10, 20), "Bonus"));

        Map<IncomeCategory, Double> byCat = fm.getIncomeByCategory();

        // Only the categories used should appear
        assertEquals(2, byCat.size());
        // Totals per category
        assertEquals(1500.00, byCat.get(IncomeCategory.SALARY));
        assertEquals(50.50, byCat.get(IncomeCategory.INVESTMENT));
    }
}
