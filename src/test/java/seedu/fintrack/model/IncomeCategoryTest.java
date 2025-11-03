package seedu.fintrack.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IncomeCategoryTest {

    @Test
    void parse_acceptsMixedCaseAndWhitespace() {
        IncomeCategory category = IncomeCategory.parse("  salary \t");
        assertEquals(IncomeCategory.SALARY, category);
    }

    @Test
    void parse_unknownCategory_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> IncomeCategory.parse("lottery"));
    }

    @Test
    void parse_nullOrBlank_throwsIllegalArgumentException() {
        assertParseFails(() -> IncomeCategory.parse(null));
        assertParseFails(() -> IncomeCategory.parse("   "));
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
