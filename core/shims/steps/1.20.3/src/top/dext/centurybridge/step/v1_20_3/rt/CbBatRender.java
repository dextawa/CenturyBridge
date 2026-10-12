package top.dext.centurybridge.step.v1_20_3.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * BatRenderer.scale(bat, pose, partialTick) and setupRotations(bat, pose, bob, yRot, partialTick):
 * 1.20.2's BatRenderer specialized both (a 0.35 scale; a resting offset or a bobbing translation, then
 * LivingEntityRenderer's rotations). 1.20.3's builds its model at bat size and animates it, and no longer
 * declares either: what a BatRenderer does there is LivingEntityRenderer's. A 1.20.2 subclass's super call
 * reaches that (s2rest's narrowing hooks); so does its own this.scale(..) / this.setupRotations(..) --
 * protected, called in a subclass that inherits it -- through LivingEntityRenderer's methods, virtually:
 * a 1.20.2 subclass further down that overrides one is reached through the hook that 1.20.3's shape
 * forwards to its override.
 */
@Environment(EnvType.CLIENT)
public final class CbBatRender {

    /** LivingEntityRenderer's protected scale / setupRotations (CbLivingEntityRendererInvoker) */
    public interface LivingRendererAccess {
        void cb$scale(net.minecraft.class_1309 entity, net.minecraft.class_4587 pose, float partialTick);

        void cb$setupRotations(net.minecraft.class_1309 entity, net.minecraft.class_4587 pose, float bob, float yRot,
            float partialTick);
    }

    /** this.scale(bat, pose, partialTick) in a BatRenderer subclass that inherits it */
    public static void method_3884(net.minecraft.class_879 renderer, net.minecraft.class_1420 bat, net.minecraft.class_4587 pose,
            float partialTick) {
        ((LivingRendererAccess) renderer).cb$scale(bat, pose, partialTick);
    }

    /** this.setupRotations(bat, pose, bob, yRot, partialTick) in a BatRenderer subclass that inherits it */
    public static void method_3882(net.minecraft.class_879 renderer, net.minecraft.class_1420 bat, net.minecraft.class_4587 pose,
            float bob, float yRot, float partialTick) {
        ((LivingRendererAccess) renderer).cb$setupRotations(bat, pose, bob, yRot, partialTick);
    }

    private CbBatRender() {
    }
}
