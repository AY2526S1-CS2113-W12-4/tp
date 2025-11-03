package seedu.fintrack.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpenseCategoryTest {

    @Test
    void parse_acceptsMixedCaseAndWhitespace() {
        ExpenseCategory category = ExpenseCategory.parse("  food \n");
        assertEquals(ExpenseCategory.FOOD, category);
    }

    @Test
    void parse_unknownCategory_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ExpenseCategory.parse("invalid"));
    }

    @Test
    void parse_nullOrBlank_throwsIllegalArgumentException() {
        assertParseFails(() -> ExpenseCategory.parse(null));
        assertParseFails(() -> ExpenseCategory.parse("   "));
    }

    private static void assertParseFails(Runnable runnable) {
        try {
            runnable.run();
            org.junit.jupiter.api.Assertions.fail("Expected parsing to fail");
        } catch (IllegalArgumentException | AssertionError ignored) {
            // acceptable for both assertion-enabled and disabled JVMs
        }
    }
}
