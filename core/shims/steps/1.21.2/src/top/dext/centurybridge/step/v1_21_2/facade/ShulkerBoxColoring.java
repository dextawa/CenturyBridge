package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1767;
import net.minecraft.class_1769;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1852;
import net.minecraft.class_1865;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2480;
import net.minecraft.class_7225;
import net.minecraft.class_7710;
import net.minecraft.class_9694;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeSerializers;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipes;

/**
 * 1.21.1's ShulkerBoxColoring (net/minecraft/class_1871, classRenames), gone in 1.21.2 (a transmute recipe in the
 * data pack): one shulker box and one dye make the box of that colour, its contents and components kept. 1.21.1's
 * bodies; its serializer is 1.21.1's "minecraft:crafting_special_shulkerboxcoloring" (CbItemRecipeSerializers).
 */
public class ShulkerBoxColoring extends class_1852 implements CbItemRecipes.LegacyDims {

    public ShulkerBoxColoring(class_7710 category) {
        super(category);
    }

    /** matches(input, level): exactly one shulker box and one dye, nothing else */
    public boolean method_17734(class_9694 input, class_1937 level) {
        int boxes = 0;
        int dyes = 0;
        for (int i = 0; i < input.method_59983(); i++) {
            class_1799 stack = input.method_59984(i);
            if (stack.method_7960()) {
                continue;
            }
            if (class_2248.method_9503(stack.method_7909()) instanceof class_2480) {
                boxes++;
            } else if (stack.method_7909() instanceof class_1769) {
                dyes++;
            } else {
                return false;
            }
            if (dyes > 1 || boxes > 1) {
                return false;
            }
        }
        return boxes == 1 && dyes == 1;
    }

    /** assemble(input, registries): the box, recoloured by the dye (white without one) */
    public class_1799 method_17733(class_9694 input, class_7225.class_7874 registries) {
        class_1799 box = class_1799.field_8037;
        class_1769 dye = (class_1769) class_1802.field_8446;
        for (int i = 0; i < input.method_59983(); i++) {
            class_1799 stack = input.method_59984(i);
            if (stack.method_7960()) {
                continue;
            }
            if (class_2248.method_9503(stack.method_7909()) instanceof class_2480) {
                box = stack;
            } else if (stack.method_7909() instanceof class_1769 d) {
                dye = d;
            }
        }
        class_1767 color = dye.method_7802();
        return box.method_56701(class_2480.method_10525(color), 1);
    }

    @Override
    public boolean method_8115(class_9694 input, class_1937 level) {
        return method_17734(input, level);
    }

    @Override
    public class_1799 method_8116(class_9694 input, class_7225.class_7874 registries) {
        return method_17733(input, registries);
    }

    /** canCraftInDimensions(width, height): two slots */
    @Override
    public boolean method_8113(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public class_1865<? extends class_1852> method_8119() {
        return CbItemRecipeSerializers.field_9041();
    }
}
