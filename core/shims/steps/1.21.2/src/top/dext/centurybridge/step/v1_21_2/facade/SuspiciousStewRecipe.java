package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1852;
import net.minecraft.class_1865;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_3489;
import net.minecraft.class_7225;
import net.minecraft.class_7710;
import net.minecraft.class_7917;
import net.minecraft.class_9334;
import net.minecraft.class_9694;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeSerializers;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipes;

/**
 * 1.21.1's SuspiciousStewRecipe (net/minecraft/class_1873, classRenames), gone in 1.21.2 (shapeless recipes in the
 * data pack): a brown and a red mushroom, a small flower and a bowl make a suspicious stew with the flower's
 * effects. 1.21.1's bodies; its serializer is 1.21.1's "minecraft:crafting_special_suspiciousstew"
 * (CbItemRecipeSerializers).
 */
public class SuspiciousStewRecipe extends class_1852 implements CbItemRecipes.LegacyDims {

    public SuspiciousStewRecipe(class_7710 category) {
        super(category);
    }

    /** matches(input, level): one each of brown mushroom, red mushroom, small flower and bowl, nothing else */
    public boolean method_17739(class_9694 input, class_1937 level) {
        boolean brown = false;
        boolean red = false;
        boolean flower = false;
        boolean bowl = false;
        for (int i = 0; i < input.method_59983(); i++) {
            class_1799 stack = input.method_59984(i);
            if (stack.method_7960()) {
                continue;
            }
            if (stack.method_31574(class_2246.field_10251.method_8389()) && !brown) {
                brown = true;
            } else if (stack.method_31574(class_2246.field_10559.method_8389()) && !red) {
                red = true;
            } else if (stack.method_31573(class_3489.field_15543) && !flower) {
                flower = true;
            } else if (stack.method_31574(class_1802.field_8428) && !bowl) {
                bowl = true;
            } else {
                return false;
            }
        }
        return brown && red && flower && bowl;
    }

    /** assemble(input, registries): a stew with the effects of the first ingredient that has some */
    public class_1799 method_17738(class_9694 input, class_7225.class_7874 registries) {
        class_1799 stew = new class_1799(class_1802.field_8766, 1);
        for (int i = 0; i < input.method_59983(); i++) {
            class_1799 stack = input.method_59984(i);
            if (stack.method_7960()) {
                continue;
            }
            class_7917 holder = class_7917.method_47380(stack.method_7909());
            if (holder != null) {
                stew.method_57379(class_9334.field_49652, holder.method_53233());
                break;
            }
        }
        return stew;
    }

    @Override
    public boolean method_8115(class_9694 input, class_1937 level) {
        return method_17739(input, level);
    }

    @Override
    public class_1799 method_8116(class_9694 input, class_7225.class_7874 registries) {
        return method_17738(input, registries);
    }

    /** canCraftInDimensions(width, height): 2x2 */
    @Override
    public boolean method_8113(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public class_1865<? extends class_1852> method_8119() {
        return CbItemRecipeSerializers.field_9030();
    }
}
