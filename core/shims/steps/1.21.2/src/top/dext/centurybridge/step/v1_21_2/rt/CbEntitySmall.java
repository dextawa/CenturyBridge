package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Optional;
import net.minecraft.class_1542;
import net.minecraft.class_1606;
import net.minecraft.class_243;

/**
 * Small 1.21.1 entity members whose 1.21.2 shape changed, answered with 1.21.1's values:
 * <ul>
 * <li>ItemEntity.getSpin(partialTick) = (age + partialTick) / 20 + bobOffs; 1.21.2's static getSpin(ageInTicks,
 *     bobOffset) computes the same from its arguments;</li>
 * <li>Shulker.getRenderPosition(partialTick): 1.21.2 answers the position or null where 1.21.1 answered an
 *     Optional of it (same body);</li>
 * <li>WalkAnimationState.update(speed, factor): 1.21.2 also takes a positionScale its position getters multiply by;
 *     1.21.1 had none, which is 1.21.2's default, 1.</li>
 * </ul>
 */
public final class CbEntitySmall {

    public static float getSpin(class_1542 item, float partialTick) {
        return class_1542.method_27314((float) item.method_6985() + partialTick, item.field_7203);
    }

    public static Optional<class_243> renderPosition(class_243 position) {
        return Optional.ofNullable(position);
    }

    public static class_243 renderPositionOf(Optional<class_243> position) {
        return position == null ? null : position.orElse(null);
    }

    public static Optional<class_243> getRenderPosition(class_1606 shulker, float partialTick) {
        return Optional.ofNullable(shulker.method_33352(partialTick));
    }

    public static float unscaled() {
        return 1.0F;
    }

    /** frameDefaults for a mod WalkAnimationState's super.update outside a hooked call */
    public static float unscaled(net.minecraft.class_8080 state, float speed, float factor) {
        return 1.0F;
    }

    /** a mod Shearable's own 1.21.1 shear(source) (adapter hook) */
    public interface LegacyShear {
        void method_6636(net.minecraft.class_3419 source);
    }

    /**
     * 1.21.1 Shearable.shear(source), called. On a server level 1.21.2's shear with the entity's level and a stack of
     * shears (what 1.21.1's callers held; CbEntityFrames#shears), which a mod class's 1.21.1 override answers through
     * its hook. On a client level 1.21.2's needs a ServerLevel: the mod class's own override answers; of the vanilla
     * bodies only Sheep's did anything visible there (its sheared flag; the drops and sounds were server-side).
     */
    public static void shear(net.minecraft.class_5147 shearable, net.minecraft.class_3419 source) {
        net.minecraft.class_3218 level = CbEntityFrames.levelOfShearable(shearable);
        if (level != null) {
            shearable.method_6636(level, source, CbEntityFrames.shears(shearable));
            return;
        }
        if (shearable instanceof LegacyShear legacy) {
            legacy.method_6636(source);
            return;
        }
        if (shearable instanceof net.minecraft.class_1472 sheep) {
            sheep.method_6635(true);
        }
    }

    private CbEntitySmall() {
    }
}
