package seedu.fintrack.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic list that maintains its elements in reverse chronological order (newest first)
 * according to a date extractor provided at construction.
 * <p>
 * Sorting is applied after every addition. Removals preserve the existing order.
 * Subclasses must implement {@link #validateForUserInput(Object, int)} to enforce
 * domain-specific constraints (e.g., null checks, valid amounts).
 *
 * @param <T> the element type (e.g., Expense, Income) that has a date
 */
public abstract class ReverseChronoList<T> extends ArrayList<T> {
    private static final Logger LOGGER = Logger.getLogger(ReverseChronoList.class.getName());

    /** Extracts the {@link LocalDate} for sorting. */
    private final Function<T, LocalDate> dateExtractor;

    /**
     * Constructs a new {@code ReverseChronoList} with a date extractor.
     *
     * @param dateExtractor function mapping elements to their dates (must not be {@code null})
     * @throws NullPointerException if {@code dateExtractor} is {@code null}
     */
    protected ReverseChronoList(Function<T, LocalDate> dateExtractor) {
        this.dateExtractor = Objects.requireNonNull(dateExtractor, "dateExtractor cannot be null");
    }

    /**
     * Comparator that orders elements newest first according to {@link #dateExtractor}.
     */
    private Comparator<T> dateDesc() {
        return Comparator.comparing(dateExtractor).reversed();
    }

    /**
     * Returns an unmodifiable view of the current list (already newest first).
     *
     * @return Unmodifiable list view.
     */
    public List<T> asUnmodifiableView() {
        return Collections.unmodifiableList(this);
    }

    /**
     * Adds an element and keeps the list newest first.
     *
     * @param element element to add
     * @return {@code true} if the list changed
     * @throws NullPointerException     if {@code e} or its date (via extractor) is {@code null}
     * @throws IllegalArgumentException if subclass validation fails
     */
    @Override public boolean add(T element) {
        validateForUserInput(element, -1);
        boolean changed = super.add(element);
        if (changed) {
            sort(dateDesc());
            assertNewestFirst();
            LOGGER.log(Level.FINE, "Added item dated {0}; size={1}",
                    new Object[]{dateExtractor.apply(element), size()});
        }
        return changed;
    }

    /**
     * Adds an element at a given position, then restores newest-first order.
     * The final position is determined by the date, not the provided index.
     *
     * @param index   ignored for ordering; list will be sorted after insertion
     * @param element element to add
     * @throws NullPointerException     if {@code element} or its date (via extractor) is {@code null}
     * @throws IllegalArgumentException if subclass validation fails
     */
    @Override public void add(int index, T element) {
        validateForUserInput(element, index);
        super.add(index, element);
        sort(dateDesc());
        assertNewestFirst();
        LOGGER.log(Level.FINE, "Inserted item at index {0} (date={1}); size={2}",
                new Object[]{index, dateExtractor.apply(element), size()});
    }

    /**
     * Adds all elements and keeps the list newest first.
     *
     * @param collection collection of elements to add
     * @return {@code true} if the list changed
     * @throws NullPointerException     if {@code c} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if any element fails subclass validation
     */
    @Override public boolean addAll(Collection<? extends T> collection) {
        validateCollectionForUserInput(collection);
        boolean changed = super.addAll(collection);
        if (changed) {
            sort(dateDesc());
            assertNewestFirst();
            LOGGER.log(Level.FINE, "Added {0} items; size={1}", new Object[]{collection.size(), size()});
        }
        return changed;
    }

    /**
     * Adds all elements at a given position, then restores newest-first order.
     * The final positions are determined by the dates, not the provided index.
     *
     * @param index ignored for ordering; list will be sorted after insertions
     * @param collection     collection of elements to add
     * @return {@code true} if the list changed
     * @throws NullPointerException     if {@code c} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if any element fails subclass validation
     */
    @Override public boolean addAll(int index, Collection<? extends T> collection) {
        validateCollectionForUserInput(collection);
        boolean changed = super.addAll(index, collection);
        if (changed) {
            sort(dateDesc());
            assertNewestFirst();
            LOGGER.log(Level.FINE, "Inserted {0} items at index {1}; size={2}",
                    new Object[]{collection.size(), index, size()});
        }
        return changed;
    }

    /* ===== Validation & invariants ===== */

    /**
     * Validates a single element as user input and logs at {@code WARNING} before throwing.
     * This method is for <b>user-facing validation</b> (exceptions), not for internal assertions.
     * <p>
     * Subclasses must implement this method to enforce domain-specific invariants.
     *
     * @param element             element to validate; may be {@code null}
     * @param positionHint  an index or position context for logging (use {@code -1} when not applicable)
     * @throws NullPointerException     if {@code e} or its date (via extractor) is {@code null}
     * @throws IllegalArgumentException if subclass validation fails
     */
    protected abstract void validateForUserInput(T element, int positionHint);

    /**
     * Validates a collection of elements as user input and logs at {@code WARNING} before throwing.
     * Each element is validated via {@link #validateForUserInput(Object, int)}.
     *
     * @param collection the collection to validate; may be {@code null}
     * @throws NullPointerException     if {@code c} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if any element fails subclass validation
     */
    protected void validateCollectionForUserInput(Collection<? extends T> collection) {
        if (collection == null) {
            LOGGER.warning("Attempted to addAll from null collection.");
            throw new NullPointerException("Collection cannot be null");
        }
        int i = 0;
        for (T e : collection) {
            validateForUserInput(e, i++);
        }
    }

    /**
     * Asserts (dev-time only) that the list remains sorted newest first by date.
     * No effect when assertions are disabled.
     */
    private void assertNewestFirst() {
        assert isNewestFirst() : "List must be sorted newest first by date";
    }

    /**
     * Returns whether the list is currently sorted by {@link #dateDesc()} (newest first).
     * This method has no side effects and is intended to be called from assertions.
     *
     * @return {@code true} if each adjacent pair is ordered newest first; {@code false} otherwise
     */
    private boolean isNewestFirst() {
        Comparator<T> comparator = dateDesc();
        for (int i = 1; i < size(); i++) {
            if (comparator.compare(get(i - 1), get(i)) > 0) {
                return false;
            }
        }
        return true;
    }
}
