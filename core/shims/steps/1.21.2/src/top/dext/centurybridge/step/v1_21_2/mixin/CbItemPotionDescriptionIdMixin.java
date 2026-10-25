package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1812;
import net.minecraft.class_1844;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemPotions;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * PotionItem's 1.21.1 getDescriptionId(stack) override, restored with 1.21.1's body so a mod's mixin on it finds its
 * method and anchors: the description id + ".effect." + the potion's name (1.21.1's Potion.getName, CbItemPotions).
 * Splash and lingering potions inherit it, as on 1.21.1. 1.21.2's getName(stack) override asks it first and follows
 * it only where it answers otherwise than that body (CbItemMembers.nameRead).
 */
@Mixin(value = class_1812.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemPotionDescriptionIdMixin extends class_1792 {

    private CbItemPotionDescriptionIdMixin(class_1792.class_1793 properties) {
        super(properties);
    }

    /** getDescriptionId(stack) */
    @CbVanillaReads("CbItemNameReadsMixin: PotionItem.getName(stack) names the stack from it where it "
        + "answers otherwise than its body")
    public String method_7866(class_1799 stack) {
        return CbItemPotions.getName(stack.method_57825(class_9334.field_49651, class_1844.field_49274).comp_2378(),
            this.method_7876() + ".effect.");
    }
}
