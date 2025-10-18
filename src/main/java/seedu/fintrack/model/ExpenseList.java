package seedu.fintrack.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maintains list of expenses in reverse chronological order (newest first).
 * </p>Sorting is applied after additions. Removals preserve the existing order.
 */
public class ExpenseList extends ArrayList<Expense> {

    /** Comparator that orders expenses in reverse chronological order (newest first). */
    private static final Comparator<Expense> DATE_DESC =
            Comparator.comparing(Expense::getDate).reversed();

    private static final Logger LOGGER = Logger.getLogger(ExpenseList.class.getName());

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
        validateForUserInput(e, /*positionHint*/ -1);
        boolean changed = super.add(e);
        if (changed) {
            sort(DATE_DESC);
            assertNewestFirst();
            LOGGER.log(Level.FINE,
                    "Added expense dated {0}; size={1}",
                    new Object[]{e.getDate(), size()});
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
        validateForUserInput(element, index);
        super.add(index, element);
        sort(DATE_DESC);
        assertNewestFirst();
        LOGGER.log(Level.FINE,
                "Inserted expense at index {0} (date={1}); size={2}",
                new Object[]{index, element.getDate(), size()});
    }

    /**
     * Adds all expenses and keeps the list newest first.
     *
     * @param c Expenses to add.
     * @return True if the list changed.
     */
    @Override
    public boolean addAll(Collection<? extends Expense> c) {
        validateCollectionForUserInput(c);
        boolean changed = super.addAll(c);
        if (changed) {
            sort(DATE_DESC);
            assertNewestFirst();
            LOGGER.log(Level.FINE,
                    "Added {0} expenses; size={1}",
                    new Object[]{c.size(), size()});
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
        validateCollectionForUserInput(c);
        boolean changed = super.addAll(index, c);
        if (changed) {
            sort(DATE_DESC);
            assertNewestFirst();
            LOGGER.log(Level.FINE,
                    "Inserted {0} expenses at index {1}; size={2}",
                    new Object[]{c.size(), index, size()});
        }
        return changed;
    }

    /* ========== Validation & Invariants ========== */

    /**
     * Validates a single expense as user input and logs at {@code WARNING} before throwing.
     * This method is for <b>user-facing validation</b> (exceptions), not for internal assertions.
     *
     * @param e the expense to validate; may be {@code null}
     * @param positionHint an index or position context for logging (use {@code -1} when not applicable)
     * @throws NullPointerException if {@code e} is {@code null} or if its date/category is {@code null}
     * @throws IllegalArgumentException if {@code e.getCategory().trim().isEmpty()} or the amount is not finite or < 0
     */
    private void validateForUserInput(Expense e, int positionHint) {
        if (e == null) {
            LOGGER.log(Level.WARNING, "Null expense encountered (pos={0})", positionHint);
            throw new NullPointerException("Expense cannot be null");
        }

        Objects.requireNonNull(e.getDate(), "Expense date cannot be null");
        Objects.requireNonNull(e.getCategory(), "Expense category cannot be null");

        ExpenseCategory category = e.getCategory();
        if (category == null) {
            LOGGER.log(Level.WARNING, "Blank category for expense at pos={0}", positionHint);
            throw new IllegalArgumentException("Expense category cannot be blank");
        }

        double amount = e.getAmount();
        if (!Double.isFinite(amount) || amount < 0.0) {
            LOGGER.log(Level.WARNING, "Invalid amount {0} for expense at pos={1}",
                    new Object[]{amount, positionHint});
            throw new IllegalArgumentException("Expense amount must be a finite, non-negative number");
        }
    }

    /**
     * Validates a collection of expenses as user input and logs at {@code WARNING} before throwing.
     * Each element is validated via {@link #validateForUserInput(Expense, int)}.
     *
     * @param c the collection to validate; may be {@code null}
     * @throws NullPointerException if {@code c} is {@code null} or contains a {@code null} element,
     *                              or if any element has {@code null} date/category
     * @throws IllegalArgumentException if any element has a blank category or a non-finite/<0 amount
     */
    private void validateCollectionForUserInput(Collection<? extends Expense> c) {
        if (c == null) {
            LOGGER.warning("Attempted to addAll from null collection.");
            throw new NullPointerException("Collection cannot be null");
        }
        int i = 0;
        for (Expense e : c) {
            validateForUserInput(e, i++);
        }
    }

    /**
     * Asserts (dev-time only) that the list remains sorted newest first by date.
     * No effect when assertions are disabled.
     */
    private void assertNewestFirst() {
        assert isNewestFirst() : "ExpenseList must be sorted newest first by date";
    }

    /**
     * Returns whether the list is currently sorted by {@link #DATE_DESC} (newest first).
     * This method has no side effects and is intended to be called from assertions.
     *
     * @return {@code true} if each adjacent pair is ordered by {@link #DATE_DESC}; {@code false} otherwise
     */
    private boolean isNewestFirst() {
        for (int i = 1; i < size(); i++) {
            Expense prev = get(i - 1);
            Expense cur  = get(i);
            // DATE_DESC sorts newest first; "prev >= cur" must hold
            if (DATE_DESC.compare(prev, cur) > 0) {
                return false;
            }
        }
        return true;
    }
}
