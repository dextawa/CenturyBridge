package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.List;
import net.minecraft.class_1856;
import net.minecraft.class_2248;
import net.minecraft.class_6862;
import net.minecraft.class_9424;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemTiers;

/**
 * 1.21.1's Tier (net/minecraft/class_1832, classRenames), gone in 1.21.2 (ToolMaterial, a record, took its place):
 * what a tool is made of. Mods implement it; CbItemTiers builds 1.21.2 tools from it.
 */
public interface Tier {

    /** getUses() */
    int method_8025();

    /** getSpeed() */
    float method_8027();

    /** getAttackDamageBonus() */
    float method_8028();

    /** getIncorrectBlocksForDrops() */
    class_6862<class_2248> method_58419();

    /** getEnchantmentValue() */
    int method_8026();

    /** getRepairIngredient() */
    class_1856 method_8023();

    /** createToolProperties(mineable): 1.21.1's body -- denies drops of the incorrect blocks, mines and drops the mineable ones */
    default class_9424 method_58418(class_6862<class_2248> mineable) {
        return new class_9424(List.of(CbItemTiers.method_58427(method_58419()), CbItemTiers.method_58428(mineable, method_8027())),
            1.0F, 1);
    }
}
