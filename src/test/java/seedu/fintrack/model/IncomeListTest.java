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
 * Tests the responsibilities of IncomeList (ordering, view, list-level validation, logging).
 */
public class IncomeListTest {

    /* ============== Logger tap for IncomeList ============== */

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
        // IncomeList logger (subclass)
        listLogger = Logger.getLogger(IncomeList.class.getName());
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
        IncomeList list = new IncomeList();

        Income i1 = new Income(1200.0, IncomeCategory.SALARY,
                LocalDate.parse("2025-09-30"), "September salary");
        Income i2 = new Income(500.0, IncomeCategory.INVESTMENT,
                LocalDate.parse("2025-10-05"), "Stock dividends");
        Income i3 = new Income(200.0, IncomeCategory.GIFT,
                LocalDate.parse("2025-10-10"), "Birthday gift");

        list.add(i1);
        list.add(i2);
        list.add(i3);

        assertEquals(i3, list.get(0)); // 10 Oct
        assertEquals(i2, list.get(1)); // 05 Oct
        assertEquals(i1, list.get(2)); // 30 Sep
    }

    @Test
    void add_withIndex_stillSortedNewestFirst() {
        IncomeList list = new IncomeList();

        Income jan = new Income(200.0,
                IncomeCategory.SALARY, LocalDate.parse("2025-01-01"),
                "");
        Income mar = new Income(400.0,
                IncomeCategory.INVESTMENT,
                LocalDate.parse("2025-03-01"),
                "");
        Income feb = new Income(300.0,
                IncomeCategory.GIFT,
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
        IncomeList list = new IncomeList();
        Income i1 = new Income(100.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-01-01"),
                "");
        Income i3 = new Income(300.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-02-01"),
                "");
        Income i2 = new Income(200.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-03-01"),
                "");

        list.addAll(List.of(i1, i2, i3));

        assertEquals(i2, list.get(0)); // Mar
        assertEquals(i3, list.get(1)); // Feb
        assertEquals(i1, list.get(2)); // Jan
    }

    @Test
    void addAll_withIndex_stillSortedNewestFirst() {
        IncomeList list = new IncomeList();
        Income jan = new Income(100.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-01-01"),
                "");
        Income mar = new Income(300.0,
                IncomeCategory.INVESTMENT,
                LocalDate.parse("2025-03-01"),
                "");
        Income feb = new Income(200.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-02-01"),
                "");

        list.addAll(0, List.of(jan, mar, feb));

        assertEquals(mar, list.get(0));
        assertEquals(feb, list.get(1));
        assertEquals(jan, list.get(2));
    }

    @Test
    void add_amountTooSmall_throwsIllegalArgumentException() {
        IncomeList list = new IncomeList();
        Income tiny = new Income(1e-10,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-02-01"),
                "Tiny");

        assertThrows(IllegalArgumentException.class, () -> list.add(tiny));
    }

    @Test
    void addAll_collectionContainingNull_throwsNullPointerException() {
        IncomeList list = new IncomeList();
        Income income = new Income(50.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-03-01"),
                "Pay");

        List<Income> withNull = new ArrayList<>();
        withNull.add(income);
        withNull.add(null);

        assertThrows(NullPointerException.class, () -> list.addAll(withNull));
    }

    /* ===================== Unmodifiable view ===================== */

    @Test
    void asUnmodifiableView_cannotMutate() {
        IncomeList list = new IncomeList();
        Income i = new Income(50.0,
                IncomeCategory.GIFT,
                LocalDate.parse("2025-01-01"),
                "");
        list.add(i);

        var view = list.asUnmodifiableView();
        assertEquals(1, view.size());
        assertThrows(UnsupportedOperationException.class, () -> view.add(i));
    }

    /* ===================== List-level exceptions ===================== */

    @Test
    void add_nullIncome_throwsNPEandLogsWarning() {
        IncomeList list = new IncomeList();
        assertThrows(NullPointerException.class, () -> list.add(null));
        assertTrue(listHandler.any(Level.WARNING, "Null income"));
    }

    @Test
    void addAll_nullCollection_throwsNPEandLogsWarning() {
        IncomeList list = new IncomeList();
        assertThrows(NullPointerException.class, () -> list.addAll(null));
        assertTrue(listHandler.any(Level.WARNING, "null collection"));
    }

    @Test
    void addAll_collectionContainingNull_throwsNPEandLogsWarning() {
        IncomeList list = new IncomeList();
        Income good = new Income(500.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-01-01"),
                "");
        List<Income> mixed = new ArrayList<>();
        mixed.add(good);
        mixed.add(null); // triggers element validation in the list
        assertThrows(NullPointerException.class, () -> list.addAll(mixed));
        assertTrue(listHandler.any(Level.WARNING, "Null income"));
    }

    /* ===================== Logging (happy path) ===================== */

    @Test
    void add_logsFineOnSuccess() {
        IncomeList list = new IncomeList();
        Income i = new Income(250.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-10-15"),
                "");
        list.add(i);
        assertTrue(anyFineContains("Added item dated"));
    }

    /* ===== Optional: dev-only assertion hook (no failure forced) ===== */

    @Test
    void assertionsHook_runsWhenEaEnabled() throws Exception {
        // Only run if JVM was launched with -ea; otherwise this is a no-op.
        assumeTrue(IncomeList.class.desiredAssertionStatus());

        IncomeList list = new IncomeList();
        list.add(new Income(10.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-01-01"),
                ""));
        list.add(new Income(20.0,
                IncomeCategory.SALARY,
                LocalDate.parse("2025-02-01"),
                ""));

        // Invoke the internal check reflectively; should not throw for a correctly sorted list.
        var m = ReverseChronoList.class.getDeclaredMethod("assertNewestFirst");
        m.setAccessible(true);
        m.invoke(list);
    }
}
