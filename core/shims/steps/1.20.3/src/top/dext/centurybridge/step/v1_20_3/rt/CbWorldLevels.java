package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_3414;
import net.minecraft.class_3417;

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

    private CbWorldLevels() {
    }
}
