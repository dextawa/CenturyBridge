package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_47;
import net.minecraft.class_5670;
import net.minecraft.class_9015;

/**
 * Scoreboard: the 1.20.2 scoreboard API (score owners as names) on 1.20.3's ScoreHolder one.
 */
public final class CbWorldScores {

    /**
     * ScoreboardNameProvider.getScoreboardName(context) became getScoreHolder(context). A 1.20.2 call gets the
     * holder's scoreboard name, which is what 1.20.2's providers answered (the entity's scoreboard name, or the
     * fixed name).
     */
    public static String getScoreboardName(class_5670 provider, class_47 context) {
        class_9015 holder = provider.method_55379(context);
        return holder == null ? null : holder.method_5820();
    }

    /** a 1.20.2 provider's name, as the holder 1.20.3 asks it for: one known by name only */
    public static class_9015 nameHolder(String name) {
        return name == null ? null : class_9015.method_55422(name);
    }

    private CbWorldScores() {
    }
}
