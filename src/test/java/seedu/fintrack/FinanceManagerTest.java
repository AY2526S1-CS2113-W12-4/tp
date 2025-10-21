package seedu.fintrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.fintrack.model.Expense;
import seedu.fintrack.model.ExpenseCategory;
import seedu.fintrack.model.Income;
import seedu.fintrack.model.IncomeCategory;

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
        assertEquals("Expense index out of range. Valid range: 1 to 0", exception.getMessage());
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
        assertEquals("Income index out of range. Valid range: 1 to 0", exception.getMessage());
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
}
