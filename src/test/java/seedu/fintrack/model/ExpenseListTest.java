package seedu.fintrack.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import org.junit.jupiter.api.*;

/**
 * Tests the responsibilities of ExpenseList (ordering, view, list-level validation, logging).
 */
public class ExpenseListTest {

    /* ============== Logger tap for ExpenseList ============== */

    private Logger listLogger;
    private TestLogHandler listHandler;
    private Level prevLevel;
    private boolean prevUseParent;

    @BeforeEach
    void setUpLoggerTap() {
        listLogger = Logger.getLogger(ExpenseList.class.getName());
        listHandler = new TestLogHandler();

        prevLevel = listLogger.getLevel();
        prevUseParent = listLogger.getUseParentHandlers();

        listLogger.setUseParentHandlers(false); // keep test output clean
        listLogger.setLevel(Level.FINE);        // capture FINE and above
        listHandler.setLevel(Level.FINE);
        listLogger.addHandler(listHandler);
    }

    @AfterEach
    void tearDownLoggerTap() {
        listLogger.removeHandler(listHandler);
        listLogger.setLevel(prevLevel);
        listLogger.setUseParentHandlers(prevUseParent);
    }

    private static class TestLogHandler extends Handler {
        private final List<LogRecord> records = new ArrayList<>();
        @Override public void publish(LogRecord record) { records.add(record); }
        @Override public void flush() {}
        @Override public void close() {}

        boolean any(Level level, String contains) {
            return records.stream().anyMatch(r ->
                    r.getLevel().equals(level) &&
                            r.getMessage() != null &&
                            r.getMessage().contains(contains));
        }
    }

    /* ===================== Ordering behaviour ===================== */

    @Test
    void add_keepsSortedNewestFirst() {
        ExpenseList list = new ExpenseList();

        Expense e1 = new Expense(10.0, "Food",
                LocalDate.parse("2025-10-05"),
                "Lunch");
        Expense e2 = new Expense(20.0,
                "Transport",
                LocalDate.parse("2025-10-08"),
                "Grab");
        Expense e3 = new Expense(15.0,
                "Groceries",
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
                "A", LocalDate.parse("2025-01-01"),
                "");
        Expense mar = new Expense(30.0,
                "C",
                LocalDate.parse("2025-03-01"),
                "");
        Expense feb = new Expense(20.0,
                "B",
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
                "A",
                LocalDate.parse("2025-01-01"),
                "");
        Expense e3 = new Expense(30.0,
                "C",
                LocalDate.parse("2025-02-01"),
                "");
        Expense e2 = new Expense(20.0,
                "B",
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
                "A",
                LocalDate.parse("2025-01-01"),
                "");
        Expense mar = new Expense(30.0,
                "C",
                LocalDate.parse("2025-03-01"),
                "");
        Expense feb = new Expense(20.0,
                "B",
                LocalDate.parse("2025-02-01"),
                "");

        list.addAll(0, List.of(jan, mar, feb)); // mixed order on insert

        assertEquals(mar, list.get(0));
        assertEquals(feb, list.get(1));
        assertEquals(jan, list.get(2));
    }

    /* ===================== Unmodifiable view ===================== */

    @Test
    void asUnmodifiableView_cannotMutate() {
        ExpenseList list = new ExpenseList();
        Expense e = new Expense(10.0,
                "Test",
                LocalDate.parse("2025-01-01"),
                "");
        list.add(e);

        var view = list.asUnmodifiableView();
        assertEquals(1, view.size());
        assertThrows(UnsupportedOperationException.class, () -> view.add(e));
    }

    /* ===================== List-level exceptions ===================== */

    @Test
    void add_nullExpense_throwsNPE_andLogsWarning() {
        ExpenseList list = new ExpenseList();
        assertThrows(NullPointerException.class, () -> list.add(null));
        assertTrue(listHandler.any(Level.WARNING, "Null expense"));
    }

    @Test
    void addAll_nullCollection_throwsNPE_andLogsWarning() {
        ExpenseList list = new ExpenseList();
        assertThrows(NullPointerException.class, () -> list.addAll(null));
        assertTrue(listHandler.any(Level.WARNING, "null collection"));
    }

    @Test
    void addAll_collectionContainingNull_throwsNPE_andLogsWarning() {
        ExpenseList list = new ExpenseList();
        Expense good = new Expense(10.0,
                "A",
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
                "Food",
                LocalDate.parse("2025-10-10"),
                "");
        list.add(e);
        assertTrue(listHandler.any(Level.FINE, "Added expense dated"));
    }

    /* ===== Optional: dev-only assertion hook (no failure forced) ===== */

    @Test
    void assertionsHook_runsWhenEaEnabled() throws Exception {
        // Only run if JVM was launched with -ea; otherwise this is a no-op.
        Assumptions.assumeTrue(ExpenseList.class.desiredAssertionStatus());

        ExpenseList list = new ExpenseList();
        list.add(new Expense(10.0,
                "A",
                LocalDate.parse("2025-01-01"),
                ""));
        list.add(new Expense(20.0,
                "B",
                LocalDate.parse("2025-02-01"),
                ""));

        // Invoke the internal check reflectively; should not throw for a correctly sorted list.
        var m = ExpenseList.class.getDeclaredMethod("assertNewestFirst");
        m.setAccessible(true);
        m.invoke(list);
    }
}
