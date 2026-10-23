package top.dext.centurybridge.step.v1_21_4.compat.fapi.rendering.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Fabric API rendering-v1 BuiltinItemRenderer (removed in 1.21.4's Fabric API) */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface BuiltinItemRenderer {
    void render(net.minecraft.class_1799 stack, net.minecraft.class_4587 matrices, net.minecraft.class_4597 vertexConsumers, int light, int overlay);
}
