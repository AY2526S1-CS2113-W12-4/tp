package seedu.fintrack.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Tests the responsibilities of ExpenseList (ordering, view, list-level validation, logging).
 */
public class ExpenseListTest {

    /* ============== Logger tap for ExpenseList ============== */

    private Logger listLogger;
    private TestLogHandler listHandler;
    private Level prevLevel;
    private boolean prevUseParent;

    private Logger baseLogger;
    private TestLogHandler baseHandler;
    private Level prevBaseLevel;
    private boolean prevBaseUseParent;

    @BeforeEach
    void setUpLoggerTap() {
        // ExpenseList logger (subclass)
        listLogger = Logger.getLogger(ExpenseList.class.getName());
        listHandler = new TestLogHandler();

        prevLevel = listLogger.getLevel();
        prevUseParent = listLogger.getUseParentHandlers();

        listLogger.setUseParentHandlers(false); // keep test output clean
        listLogger.setLevel(Level.FINE);        // capture FINE and above
        listHandler.setLevel(Level.FINE);
        listLogger.addHandler(listHandler);

        // ReverseChronoList logger (base class)
        baseLogger = Logger.getLogger(ReverseChronoList.class.getName());
        baseHandler = new TestLogHandler();

        prevBaseLevel = baseLogger.getLevel();
        prevBaseUseParent = baseLogger.getUseParentHandlers();

        baseLogger.setUseParentHandlers(false);
        baseLogger.setLevel(Level.FINE);
        baseHandler.setLevel(Level.FINE);
        baseLogger.addHandler(baseHandler);
    }


    @AfterEach
    void tearDownLoggerTap() {
        // Remove handlers we attached
        if (listLogger != null && listHandler != null) {
            listLogger.removeHandler(listHandler);
        }
        if (baseLogger != null && baseHandler != null) {
            baseLogger.removeHandler(baseHandler);
        }

        // Restore previous logger state
        if (listLogger != null) {
            listLogger.setLevel(prevLevel);
            listLogger.setUseParentHandlers(prevUseParent);
        }
        if (baseLogger != null) {
            baseLogger.setLevel(prevBaseLevel);
            baseLogger.setUseParentHandlers(prevBaseUseParent);
        }
    }


    private static class TestLogHandler extends Handler {
        private final List<LogRecord> records = new ArrayList<>();
        @Override public void publish(LogRecord record) {
            records.add(record);
        }
        @Override public void flush() {}
        @Override public void close() {}

        boolean any(Level level, String contains) {
            return records.stream().anyMatch(r ->
                    r.getLevel().equals(level) &&
                            r.getMessage() != null &&
                            r.getMessage().contains(contains));
        }
    }

    // helper so assertions can check either logging stream
    private boolean anyFineContains(String s) {
        return (listHandler != null && listHandler.any(Level.FINE, s))
                || (baseHandler != null && baseHandler.any(Level.FINE, s));
    }

    /* ===================== Ordering behaviour ===================== */

    @Test
    void add_keepsSortedNewestFirst() {
        ExpenseList list = new ExpenseList();

        Expense e1 = new Expense(10.0, ExpenseCategory.FOOD,
                LocalDate.parse("2025-10-05"),
                "Lunch");
        Expense e2 = new Expense(20.0,
                ExpenseCategory.TRANSPORT,
                LocalDate.parse("2025-10-08"),
                "Grab");
        Expense e3 = new Expense(15.0,
                ExpenseCategory.GROCERIES,
                LocalDate.parse("2025-10-06"),
                "NTUC");

        list.add(e1);
        list.add(e2);
        list.add(e3);

        assertEquals(e2, list.get(0)); // 08
        assertEquals(e3, list.get(1)); // 06
        assertEquals(e1, list.get(2)); // 05
    }

    @Test
    void add_withIndex_stillSortedNewestFirst() {
        ExpenseList list = new ExpenseList();

        Expense jan = new Expense(10.0,
                ExpenseCategory.FOOD, LocalDate.parse("2025-01-01"),
                "");
        Expense mar = new Expense(30.0,
                ExpenseCategory.TRANSPORT,
                LocalDate.parse("2025-03-01"),
                "");
        Expense feb = new Expense(20.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-02-01"),
                "");

        list.add(0, jan);
        list.add(0, mar);
        list.add(1, feb);

        assertEquals(mar, list.get(0));
        assertEquals(feb, list.get(1));
        assertEquals(jan, list.get(2));
    }

    @Test
    void addAll_keepsSortedNewestFirst() {
        ExpenseList list = new ExpenseList();
        Expense e1 = new Expense(10.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"),
                "");
        Expense e3 = new Expense(30.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-02-01"),
                "");
        Expense e2 = new Expense(20.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-03-01"),
                "");

        list.addAll(List.of(e1, e2, e3));

        assertEquals(e2, list.get(0)); // Mar
        assertEquals(e3, list.get(1)); // Feb
        assertEquals(e1, list.get(2)); // Jan
    }

    @Test
    void addAll_withIndex_stillSortedNewestFirst() {
        ExpenseList list = new ExpenseList();
        Expense jan = new Expense(10.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"),
                "");
        Expense mar = new Expense(30.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-03-01"),
                "");
        Expense feb = new Expense(20.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-02-01"),
                "");

        list.addAll(0, List.of(jan, mar, feb)); // mixed order on insert

        assertEquals(mar, list.get(0));
        assertEquals(feb, list.get(1));
        assertEquals(jan, list.get(2));
    }

    @Test
    void add_amountTooSmall_throwsIllegalArgumentException() {
        ExpenseList list = new ExpenseList();
        Expense tiny = new Expense(1e-10,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"),
                "Tiny");

        assertThrows(IllegalArgumentException.class, () -> list.add(tiny));
    }

    @Test
    void addAll_collectionContainingNull_throwsNullPointerException() {
        ExpenseList list = new ExpenseList();
        Expense expense = new Expense(15.0,
                ExpenseCategory.TRANSPORT,
                LocalDate.parse("2025-01-02"),
                "Train");

        List<Expense> withNull = new ArrayList<>();
        withNull.add(expense);
        withNull.add(null);

        assertThrows(NullPointerException.class, () -> list.addAll(withNull));
    }

    /* ===================== Unmodifiable view ===================== */

    @Test
    void asUnmodifiableView_cannotMutate() {
        ExpenseList list = new ExpenseList();
        Expense e = new Expense(10.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"),
                "");
        list.add(e);

        var view = list.asUnmodifiableView();
        assertEquals(1, view.size());
        assertThrows(UnsupportedOperationException.class, () -> view.add(e));
    }

    /* ===================== List-level exceptions ===================== */

    @Test
    void add_nullExpense_throwsNPEandLogsWarning() {
        ExpenseList list = new ExpenseList();
        assertThrows(NullPointerException.class, () -> list.add(null));
        assertTrue(listHandler.any(Level.WARNING, "Null expense"));
    }

    @Test
    void addAll_nullCollection_throwsNPEandLogsWarning() {
        ExpenseList list = new ExpenseList();
        assertThrows(NullPointerException.class, () -> list.addAll(null));
        assertTrue(listHandler.any(Level.WARNING, "null collection"));
    }

    @Test
    void addAll_collectionContainingNull_throwsNPEandLogsWarning() {
        ExpenseList list = new ExpenseList();
        Expense good = new Expense(10.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"),
                "");
        List<Expense> mixed = new ArrayList<>();
        mixed.add(good);
        mixed.add(null); // triggers element validation in the list
        assertThrows(NullPointerException.class, () -> list.addAll(mixed));
        assertTrue(listHandler.any(Level.WARNING, "Null expense"));
    }

    /* ===================== Logging (happy path) ===================== */

    @Test
    void add_logsFineOnSuccess() {
        ExpenseList list = new ExpenseList();
        Expense e = new Expense(10.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-10-10"),
                "");
        list.add(e);
        assertTrue(anyFineContains("Added item dated"));
    }

    /* ===== Optional: dev-only assertion hook (no failure forced) ===== */

    @Test
    void assertionsHook_runsWhenEaEnabled() throws Exception {
        // Only run if JVM was launched with -ea; otherwise this is a no-op.
        assumeTrue(ExpenseList.class.desiredAssertionStatus());

        ExpenseList list = new ExpenseList();
        list.add(new Expense(10.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-01-01"),
                ""));
        list.add(new Expense(20.0,
                ExpenseCategory.FOOD,
                LocalDate.parse("2025-02-01"),
                ""));

        // Invoke the internal check reflectively; should not throw for a correctly sorted list.
        var m = ReverseChronoList.class.getDeclaredMethod("assertNewestFirst");
        m.setAccessible(true);
        m.invoke(list);
    }
}
