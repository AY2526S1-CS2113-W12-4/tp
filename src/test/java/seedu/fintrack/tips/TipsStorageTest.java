package seedu.fintrack.tips;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TipsStorageTest {

    private static final List<String> EXPECTED_TIPS = List.of(
            "Don't buy a Mac especially if you are doing EE2026!",
            "If you stay on campus, Dining Credits are now available to use in NUS food courts!",
            "Keep a lookout for free welfare as exam period is approaching!",
            "Remember to track your expenses daily!",
            "Take the shuttle bus, it's worth it :(",
            "OpenAI credits are too expensive!",
            "The Deck banmian is less than $4!",
            "You can spend the pass royale money on better food ._."
    );

    private static final String fakeTip = "Spend all your money!";

    @Test
    void returnTip_isFromPredefinedList() {
        TipsStorage tipsStorage = new TipsStorage();
        for (int i = 0; i < 500; i++) {
            String tip = tipsStorage.returnTip();
            assertNotNull(tip);
            assertTrue(EXPECTED_TIPS.contains(tip), "Unexpected tip: " + tip);
        }
    }

    @Test
    void returnTip_isNeverBlank() {
        TipsStorage tipsStorage = new TipsStorage();
        for (int i = 0; i < 100; i++) {
            String tip = tipsStorage.returnTip();
            assertNotNull(tip);
            assertFalse(tip.isBlank());
        }
    }

    @Test
    void returnTip_variesAcrossCalls() {
        TipsStorage tipsStorage = new TipsStorage();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            seen.add(tipsStorage.returnTip());
        }
        assertTrue(seen.size() >= 2, "Should observe at least two different tips across calls.");
    }

    @Test
    void returnTip_doesNotContainFakeTip() {
        TipsStorage tipsStorage = new TipsStorage();
        for (int i = 0; i < 1000; i++) {
            assertNotEquals(fakeTip, tipsStorage.returnTip());
        }
    }
}
