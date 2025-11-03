package seedu.fintrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class ReverseChronoListTest {

    private static final class DummyEntry {
        private final LocalDate date;
        private final double amount;

        private DummyEntry(LocalDate date, double amount) {
            this.date = date;
            this.amount = amount;
        }

        LocalDate getDate() {
            return date;
        }

        double getAmount() {
            return amount;
        }
    }

    private static final class DummyReverseList extends ReverseChronoList<DummyEntry> {
        DummyReverseList() {
            super(DummyEntry::getDate);
        }

        @Override
        protected void validateForUserInput(DummyEntry element, int positionHint) {
            if (element == null) {
                throw new NullPointerException("Element cannot be null");
            }
            if (!Double.isFinite(element.getAmount()) || element.getAmount() <= 0.0) {
                throw new IllegalArgumentException("Amount must be positive and finite");
            }
        }
    }

    @Test
    void add_allPaths_keepSortedAndValidate() {
        DummyReverseList list = new DummyReverseList();
        DummyEntry jan = new DummyEntry(LocalDate.parse("2025-01-02"), 10.0);
        DummyEntry feb = new DummyEntry(LocalDate.parse("2025-02-15"), 20.0);
        DummyEntry mar = new DummyEntry(LocalDate.parse("2025-03-10"), 30.0);

        list.add(jan);
        list.add(feb);
        list.add(mar);

        assertEquals(Arrays.asList(mar, feb, jan), list);

        DummyEntry future = new DummyEntry(LocalDate.parse("2025-04-01"), 40.0);
        list.add(0, future);
        assertEquals(future, list.get(0));

        List<DummyEntry> newItems = List.of(
                new DummyEntry(LocalDate.parse("2024-12-31"), 5.0),
                new DummyEntry(LocalDate.parse("2025-01-05"), 15.0)
        );
        list.addAll(newItems);
        assertTrue(list.get(0).getDate().isAfter(list.get(1).getDate()));

        list.addAll(2, List.of(
                new DummyEntry(LocalDate.parse("2025-03-15"), 25.0)));
        assertEquals(LocalDate.parse("2025-03-15"), list.get(1).getDate());

        assertThrows(NullPointerException.class, () -> list.add(null));
        assertThrows(NullPointerException.class, () -> list.addAll(null));
        assertThrows(IllegalArgumentException.class, () -> list.add(new DummyEntry(LocalDate.now(), 0.0)));
        assertThrows(NullPointerException.class,
                () -> list.addAll(List.of(new DummyEntry(LocalDate.now(), 1.0), null)));
    }

    @Test
    void isNewestFirst_returnsFalseWhenOrderBroken() throws Exception {
        DummyReverseList list = new DummyReverseList();
        DummyEntry older = new DummyEntry(LocalDate.parse("2025-01-01"), 10.0);
        DummyEntry newer = new DummyEntry(LocalDate.parse("2025-02-01"), 20.0);
        list.add(older);
        list.add(newer);

        java.util.Collections.swap(list, 0, 1);

        Method m = ReverseChronoList.class.getDeclaredMethod("isNewestFirst");
        m.setAccessible(true);
        boolean sorted = (boolean) m.invoke(list);
        assertFalse(sorted);
    }
}
