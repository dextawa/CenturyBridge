package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.3 ItemColor (class_326, gone in 1.21.4): an item stack's ARGB colour for one tint index */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ItemColor {
    int getColor(net.minecraft.class_1799 stack, int tintIndex);
}
