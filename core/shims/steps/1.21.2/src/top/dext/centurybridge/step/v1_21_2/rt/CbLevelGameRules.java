package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.class_1928;
import net.minecraft.class_1937;
import net.minecraft.class_3218;

/**
 * 1.21.1's Level.getGameRules() on 1.21.2, where only ServerLevel has game rules.
 *
 * <p>1.21.1's Level answered its LevelData's rules. A server level's are 1.21.2's ServerLevel.getGameRules(). A client
 * level's were ClientLevel.ClientLevelData's own GameRules, built with default values when the level data was made and
 * never synchronized from the server (1.21.1's client packet handling writes none of them): here the same, one
 * default-valued GameRules per client level, made for the level's enabled features as 1.21.2's GameRules is.
 */
public final class CbLevelGameRules {

    private static final Map<class_1937, class_1928> CLIENT = new WeakHashMap<>();

    /** level.getGameRules() as 1.21.1 answered it */
    public static class_1928 of(class_1937 level) {
        if (level instanceof class_3218 server) {
            return server.method_64395();
        }
        synchronized (CLIENT) {
            return CLIENT.computeIfAbsent(level, l -> new class_1928(l.method_45162()));
        }
    }

    private CbLevelGameRules() {
    }
}
