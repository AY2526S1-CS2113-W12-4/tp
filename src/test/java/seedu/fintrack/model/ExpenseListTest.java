package seedu.fintrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class ExpenseListTest {

    @Test
    void add_expenses_sortedNewestFirst() {
        ExpenseList list = new ExpenseList();

        Expense e1 = new Expense(10.0, "Food", LocalDate.parse("2025-10-05"), "Lunch");
        Expense e2 = new Expense(20.0, "Transport", LocalDate.parse("2025-10-08"), "Grab");
        Expense e3 = new Expense(15.0, "Groceries", LocalDate.parse("2025-10-06"), "NTUC");

        list.add(e1);
        list.add(e2);
        list.add(e3);

        // Expect: e2 (08), e3 (06), e1 (05)
        assertEquals(e2, list.get(0));
        assertEquals(e3, list.get(1));
        assertEquals(e1, list.get(2));
    }

    @Test
    void addAll_sortsCombinedListNewestFirst() {
        ExpenseList list = new ExpenseList();
        var e1 = new Expense(10.0, "A", LocalDate.parse("2025-01-01"), "");
        var e2 = new Expense(20.0, "B", LocalDate.parse("2025-03-01"), "");
        var e3 = new Expense(30.0, "C", LocalDate.parse("2025-02-01"), "");

        list.addAll(java.util.List.of(e1, e2, e3));
        assertEquals(e2, list.get(0)); // March
        assertEquals(e3, list.get(1)); // February
        assertEquals(e1, list.get(2)); // January
    }

    @Test
    void asUnmodifiableView_cannotModifyUnderlyingList() {
        ExpenseList list = new ExpenseList();
        var e = new Expense(10.0, "Test", LocalDate.parse("2025-01-01"), "");
        list.add(e);

        var view = list.asUnmodifiableView();
        assertEquals(1, view.size());

        try {
            view.add(e);
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }
}
