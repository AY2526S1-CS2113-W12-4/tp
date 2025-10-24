package seedu.fintrack.tips;

import java.util.List;
import java.util.Random;

public class TipsStorage {
    private final List<String> tips;
    private final Random random = new Random();

    public TipsStorage() {
        this.tips = List.of (
                "Don't buy a Mac especially if you are doing EE2026!",
                "If you stay on campus, " +
                        "Dining Credits are now available to use in NUS food courts!",
                "Keep a lookout for free welfare as exam period is approaching!",
                "Remember to track your expenses daily!",
                "Take the shuttle bus, it's worth it :(",
                "OpenAI credits are too expensive!",
                "The Deck banmian is less than $4!",
                "You can spend the pass royale money on better food ._."
        );

    }

    public String returnTip() {
        assert tips != null : "tips should not be null";
        assert !tips.isEmpty() : "tips should not be empty";

        int tipId = random.nextInt(tips.size());
        return tips.get(tipId);
    }
}
