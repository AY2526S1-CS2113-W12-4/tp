package seedu.fintrack.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maintains list of incomes in reverse chronological order (newest first).
 * <p>Sorting is applied after additions. Removals preserve the existing order.</p>
 */
public class IncomeList extends ReverseChronoList<Income> {

    private static final Logger LOGGER = Logger.getLogger(IncomeList.class.getName());

    /**
     * Constructs an {@code IncomeList} that orders by {@link Income#getDate()} (newest first).
     */
    public IncomeList() {
        super(Income::getDate);
    }

    /**
     * Returns an unmodifiable view of the current list (already newest first).
     *
     * @return Unmodifiable list view.
     */
    public List<Income> asUnmodifiableView() {
        return super.asUnmodifiableView();
    }

    /* ========== Validation & Invariants ========== */

    /**
     * Validates a single income as user input and logs at {@code WARNING} before throwing.
     * This method is for <b>user-facing validation</b> (exceptions), not for internal assertions.
     *
     * @param income the income to validate; may be {@code null}
     * @param positionHint an index or position context for logging (use {@code -1} when not applicable)
     * @throws NullPointerException if {@code income} is {@code null} or if its date is {@code null}
     * @throws IllegalArgumentException if the amount is not finite or &lt; 0
     */
    @Override
    protected void validateForUserInput(Income income, int positionHint) {
        if (income == null) {
            LOGGER.log(Level.WARNING, "Null income encountered (pos={0})", positionHint);
            throw new NullPointerException("Income cannot be null");
        }

        Objects.requireNonNull(income.getDate(), "Income date cannot be null");

        double amount = income.getAmount();
        if (!Double.isFinite(amount) || amount <= 1e-9) {
            LOGGER.log(Level.WARNING, "Invalid amount {0} for income at pos={1}",
                    new Object[]{amount, positionHint});
            throw new IllegalArgumentException("Income amount must be a finite, positive number");
        }
    }

    /**
     * Validates a collection of incomes as user input and logs at {@code WARNING} before throwing.
     * Each element is validated via {@link #validateForUserInput(Income, int)}.
     *
     * @param collection the collection to validate; may be {@code null}
     * @throws NullPointerException if {@code c} is {@code null} or contains a {@code null} element,
     *                              or if any element has {@code null} date
     * @throws IllegalArgumentException if any element has a non-finite/&lt;0 amount
     */
    @Override
    protected void validateCollectionForUserInput(Collection<? extends Income> collection) {
        if (collection == null) {
            LOGGER.warning("Attempted to addAll from null collection.");
            throw new NullPointerException("Collection cannot be null");
        }
        int i = 0;
        for (Income income : collection) {
            validateForUserInput(income, i++);
        }
    }
}
