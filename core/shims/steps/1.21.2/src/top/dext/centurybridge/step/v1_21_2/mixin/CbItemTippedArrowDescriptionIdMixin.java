package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1744;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1833;
import net.minecraft.class_1844;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemPotions;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * TippedArrowItem's 1.21.1 getDescriptionId(stack) override, restored with 1.21.1's body so a mod's mixin on it finds
 * its method and anchors: the description id + ".effect." + the potion's name (1.21.1's Potion.getName,
 * CbItemPotions). 1.21.2's getName(stack) override asks it first and follows it only where it answers otherwise than
 * that body (CbItemNameReadsMixin). Priority below every mod's so a mod's mixin may anchor anywhere in it
 * (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1833.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemTippedArrowDescriptionIdMixin extends class_1744 {

    private CbItemTippedArrowDescriptionIdMixin(class_1792.class_1793 properties) {
        super(properties);
    }

    /** getDescriptionId(stack) */
    @CbVanillaReads("CbItemNameReadsMixin: TippedArrowItem.getName(stack) names the stack from it "
        + "where it answers otherwise than its body")
    public String method_7866(class_1799 stack) {
        return CbItemPotions.getName(stack.method_57825(class_9334.field_49651, class_1844.field_49274).comp_2378(),
            this.method_7876() + ".effect.");
    }
}
