package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.class_1927;
import net.minecraft.class_1937;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_8889;
import net.minecraft.class_8891;
import net.minecraft.class_8921;

/**
 * Levels: the 1.20.2 level API on 1.20.3. Wired from s2world.json.
 */
public final class CbWorldLevels {

    /**
     * Level.explode(.., interaction, spawnParticles) gained its particles and sound as arguments in 1.20.3.
     * 1.20.2's explosions always used these: EXPLOSION for small or blockless ones, EXPLOSION_EMITTER for
     * the others, and GENERIC_EXPLODE -- what 1.20.3's own overloads without them pass as well.
     */
    public static class_2394 explosionSmallParticles() {
        return class_2398.field_11236;
    }

    public static class_2394 explosionLargeParticles() {
        return class_2398.field_11221;
    }

    public static class_3414 explosionSound() {
        return class_3417.field_15152;
    }

    /** Explosion(level, entity, x, y, z, radius, toBlow) was this(.., false, DESTROY_WITH_DECAY, toBlow) in 1.20.2 */
    public static class_1927.class_4179 destroyWithDecay() {
        return class_1927.class_4179.field_40879;
    }

    // ------------------------------------------------------------------ Level.tickRateManager

    private static final Map<class_1937, class_8921> TICK_RATES = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * 1.20.3's Level demands tickRateManager() of every level; a 1.20.2 Level subclass has none. 1.20.2 ran at a
     * fixed 20 ticks a second, never frozen: such a level answers a TickRateManager of its own in its initial
     * state, which is exactly that (the game's own /tick command reaches only the server's levels).
     */
    public static class_8921 tickRateManager(class_1937 level) {
        return TICK_RATES.computeIfAbsent(level, k -> new class_8921());
    }

    // ------------------------------------------------------------------ jigsaw structures

    /** 1.20.3 jigsaw structures may alias their pools; 1.20.2's had no aliases */
    public static class_8891 noPoolAliasLookup() {
        return class_8891.field_46826;
    }

    public static List<class_8889> noPoolAliases() {
        return List.of();
    }

    private CbWorldLevels() {
    }
}
