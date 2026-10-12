package top.dext.centurybridge.step.v1_21.rt;

import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_3532;
import net.minecraft.class_5819;

/**
 * Animals: what 1.21 changed in the animal classes' public surface, given back with 1.20.6's behaviour.
 * Wired from s6world.json.
 */
public final class CbEntityAnimals {

    // ------------------------------------------------------------------------------------------------ Tadpole hitbox
    /**
     * Tadpole.HITBOX_WIDTH / HITBOX_HEIGHT: public static, not final, in 1.20.6 (0.4 and 0.3, set by Tadpole's
     * static initializer), public static final constants in 1.21. 1.20.6 read them in EntityType's static
     * initializer (the tadpole's dimensions, fixed before any mod initializer runs) and, for the width, in
     * FrogspawnBlock.getRandomTadpolePositionOffset each time frogspawn hatches; 1.21 compiled that last read into
     * the constant. A 1.20.6 mod's reads and writes land here (fieldGetters, staticFieldSetters), and a written
     * width spaces the hatching tadpoles as in 1.20.6 (CbEntityFrogspawnMixin).
     */
    private static float tadpoleHitboxWidth = 0.4F;
    private static float tadpoleHitboxHeight = 0.3F;
    private static boolean tadpoleHitboxWidthWritten;

    public static float tadpoleHitboxWidth() {
        return tadpoleHitboxWidth;
    }

    public static void setTadpoleHitboxWidth(float width) {
        tadpoleHitboxWidth = width;
        tadpoleHitboxWidthWritten = true;
    }

    public static float tadpoleHitboxHeight() {
        return tadpoleHitboxHeight;
    }

    public static void setTadpoleHitboxHeight(float height) {
        tadpoleHitboxHeight = height;
    }

    /** a 1.20.6 mod wrote Tadpole.HITBOX_WIDTH: frogspawn reads the written width, not 1.21's constant */
    public static boolean tadpoleHitboxWidthWritten() {
        return tadpoleHitboxWidthWritten;
    }

    /** 1.20.6 FrogspawnBlock.getRandomTadpolePositionOffset(random), on the width as it stands */
    public static double tadpolePositionOffset(class_5819 random) {
        double d = tadpoleHitboxWidth / 2.0F;
        return class_3532.method_15350(random.method_43058(), d, 1.0 - d);
    }

    // ------------------------------------------------------------------------------------------------ saddles
    /**
     * Saddleable.equipSaddle(source): 1.20.6's AbstractHorse put a new saddle stack in its saddle slot, Pig and
     * Strider only noted that they have one; 1.21 takes the stack to put there.
     */
    public static class_1799 newSaddle() {
        return new class_1799(class_1802.field_8175);
    }

    private CbEntityAnimals() {
    }
}
