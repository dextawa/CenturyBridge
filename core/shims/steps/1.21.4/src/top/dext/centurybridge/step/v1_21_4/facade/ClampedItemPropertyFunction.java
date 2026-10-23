package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.3 ClampedItemPropertyFunction (class_6395, gone in 1.21.4): the unclamped value, clamped to 0..1 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ClampedItemPropertyFunction extends ItemPropertyFunction {

    @Override
    default float call(net.minecraft.class_1799 stack, net.minecraft.class_638 level, net.minecraft.class_1309 entity, int seed) {
        return net.minecraft.class_3532.method_15363(this.unclampedCall(stack, level, entity, seed), 0.0F, 1.0F);
    }

    float unclampedCall(net.minecraft.class_1799 stack, net.minecraft.class_638 level, net.minecraft.class_1309 entity, int seed);
}
