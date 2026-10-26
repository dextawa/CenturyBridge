package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * TurtleRenderer.render(turtle, yaw, partialTick, pose, buffers, light): 1.20.4's own render, which halved
 * the shadow radius for a baby and rendered as MobRenderer does. 1.20.5 dropped it: the baby's half shadow is
 * its getShadowRadius(turtle) now, applied on every render path. The method comes back as MobRenderer's
 * render, so a 1.20.4 call, override (reached through the subclass's own render bridges) or super call has
 * the turtle rendered with the shadow 1.20.5 gives it -- 1.20.4's halving of the field on each baby render
 * compounded frame after frame, which 1.20.5's per-entity radius does not reproduce.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_958.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientTurtleRendererMixin extends net.minecraft.class_927<net.minecraft.class_1481, net.minecraft.class_614<net.minecraft.class_1481>> {

    private CbClientTurtleRendererMixin() {
        super(null, null, 0.0F);
    }

    /** 1.20.4 TurtleRenderer.render(turtle, ...) */
    public void method_4138(net.minecraft.class_1481 turtle, float yaw, float partialTick, net.minecraft.class_4587 pose,
                            net.minecraft.class_4597 buffers, int light) {
        super.method_4072(turtle, yaw, partialTick, pose, buffers, light);
    }
}
