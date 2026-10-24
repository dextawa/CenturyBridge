package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 EnderDragonRenderer / EndCrystalRenderer statics whose 1.21.2 forms take the animation time instead of the
 * entity's tick count and the partial tick (1.21.2's render states carry ageInTicks = tickCount + partialTick).
 * 1.21.1's bodies computed that same sum first and used nothing else of either (compared in both jars).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderDragons {

    private CbRenderDragons() {
    }

    /** 1.21.1 EnderDragonRenderer.renderCrystalBeams(x, y, z, partialTick, tickCount, poseStack, buffers, light) */
    public static void method_3917(float x, float y, float z, float partialTick, int tickCount, net.minecraft.class_4587 poseStack,
                                   net.minecraft.class_4597 buffers, int light) {
        net.minecraft.class_895.method_3917(x, y, z, (float) tickCount + partialTick, poseStack, buffers, light);
    }

    /** 1.21.1 EndCrystalRenderer.getY(crystal, partialTick) */
    public static float method_23155(net.minecraft.class_1511 crystal, float partialTick) {
        return net.minecraft.class_892.method_23155((float) crystal.field_7034 + partialTick);
    }
}
