package seedu.fintrack.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Maintains list of expenses in reverse chronological order (newest first).
 * </p>Sorting is applied after additions. Removals preserve the existing order.
 */
public class ExpenseList extends ArrayList<Expense> {

    private static final Comparator<Expense> DATE_DESC =
            Comparator.comparing(Expense::getDate).reversed();

    /**
     * Returns an unmodifiable view of the current list (already newest first).
     *
     * @return Unmodifiable list view.
     */
    public List<Expense> asUnmodifiableView() {
        return Collections.unmodifiableList(this);
    }

    /**
     * Adds an expense and keeps the list newest first.
     *
     * @param e Expense to add.
     * @return True if the list changed.
     */
    @Override
    public boolean add(Expense e) {
        boolean changed = super.add(e);
        if (changed) {
            sort(DATE_DESC);
        }
        return changed;
    }

    /**
     * Adds an expense at a given position, then restores newest-first order.
     * The final position is determined by the date, not the provided index.
     *
     * @param index Ignored for ordering; list will be sorted after insertion.
     * @param element Expense to add.
     */
    @Override
    public void add(int index, Expense element) {
        super.add(index, element);
        sort(DATE_DESC);
    }

    /**
     * Adds all expenses and keeps the list newest first.
     *
     * @param c Expenses to add.
     * @return True if the list changed.
     */
    @Override
    public boolean addAll(Collection<? extends Expense> c) {
        boolean changed = super.addAll(c);
        if (changed) {
            sort(DATE_DESC);
        }
        return changed;
    }

    /**
     * Adds all expenses at a given position, then restores newest-first order.
     * The final positions are determined by the dates, not the provided index.
     *
     * @param index Ignored for ordering; list will be sorted after insertions.
     * @param c Expenses to add.
     * @return True if the list changed.
     */
    @Override
    public boolean addAll(int index, Collection<? extends Expense> c) {
        boolean changed = super.addAll(index, c);
        if (changed) {
            sort(DATE_DESC);
        }
        return changed;
    }
}
