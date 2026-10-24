package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 ItemPropertyFunction (class_1800): a model predicate's value for a stack. 1.21.4 reused class_1800 for
 * RangeSelectItemModelProperty (a codec-registered property), so every 1.21.3 reference is renamed here.
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ItemPropertyFunction {
    float call(net.minecraft.class_1799 stack, net.minecraft.class_638 level, net.minecraft.class_1309 entity, int seed);
}
