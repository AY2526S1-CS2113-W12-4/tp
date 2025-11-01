package seedu.fintrack.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maintains list of expenses in reverse chronological order (newest first).
 * <p>Sorting is applied after additions. Removals preserve the existing order.</p>
 */
public class ExpenseList extends ReverseChronoList<Expense> {

    private static final Logger LOGGER = Logger.getLogger(ExpenseList.class.getName());

    /**
     * Constructs an {@code ExpenseList} that orders by {@link Expense#getDate()} (newest first).
     */
    public ExpenseList() {
        super(Expense::getDate);
    }

    /**
     * Returns an unmodifiable view of the current list (already newest first).
     *
     * @return Unmodifiable list view.
     */
    public List<Expense> asUnmodifiableView() {
        return super.asUnmodifiableView();
    }

    /* ========== Validation & Invariants ========== */

    /**
     * Validates a single expense as user input and logs at {@code WARNING} before throwing.
     * This method is for <b>user-facing validation</b> (exceptions), not for internal assertions.
     *
     * @param expense the expense to validate; may be {@code null}
     * @param positionHint an index or position context for logging (use {@code -1} when not applicable)
     * @throws NullPointerException if {@code e} is {@code null} or if its date/category is {@code null}
     * @throws IllegalArgumentException if the amount is not finite or &lt; 0
     */
    @Override
    protected void validateForUserInput(Expense expense, int positionHint) {
        if (expense == null) {
            LOGGER.log(Level.WARNING, "Null expense encountered (pos={0})", positionHint);
            throw new NullPointerException("Expense cannot be null");
        }

        Objects.requireNonNull(expense.getDate(), "Expense date cannot be null");
        Objects.requireNonNull(expense.getCategory(), "Expense category cannot be null");

        double amount = expense.getAmount();
        if (!Double.isFinite(amount) || amount <= 1e-9) {
            LOGGER.log(Level.WARNING, "Invalid amount {0} for expense at pos={1}",
                    new Object[]{amount, positionHint});
            throw new IllegalArgumentException("Expense amount must be a finite, positive number");
        }
    }

    /**
     * Validates a collection of expenses as user input and logs at {@code WARNING} before throwing.
     * Each element is validated via {@link #validateForUserInput(Expense, int)}.
     *
     * @param collection the collection to validate; may be {@code null}
     * @throws NullPointerException if {@code c} is {@code null} or contains a {@code null} element,
     *                              or if any element has {@code null} date/category
     * @throws IllegalArgumentException if any element has a non-finite/&lt;0 amount
     */
    @Override
    protected void validateCollectionForUserInput(Collection<? extends Expense> collection) {
        if (collection == null) {
            LOGGER.warning("Attempted to addAll from null collection.");
            throw new NullPointerException("Collection cannot be null");
        }
        int i = 0;
        for (Expense e : collection) {
            validateForUserInput(e, i++);
        }
    }
}
