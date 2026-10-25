package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1759;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * CompassItem's 1.21.1 getDescriptionId(stack) override, restored with 1.21.1's body so a mod's mixin on it finds its
 * method and anchors: a compass tracking a lodestone is item.minecraft.lodestone_compass. 1.21.2's getName(stack)
 * override asks it first and follows it only where it answers otherwise than that body (CbItemNameReadsMixin). Priority
 * below every mod's so a mod's mixin may anchor anywhere in it (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1759.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemCompassDescriptionIdMixin extends class_1792 {

    private CbItemCompassDescriptionIdMixin(class_1792.class_1793 properties) {
        super(properties);
    }

    /** getDescriptionId(stack); the super call is Item's restored body (CbItemMembers.super_method_7866) */
    @CbVanillaReads("CbItemNameReadsMixin: CompassItem.getName(stack) names the stack from it where it "
        + "answers otherwise than its body")
    public String method_7866(class_1799 stack) {
        return stack.method_57826(class_9334.field_49614) ? "item.minecraft.lodestone_compass"
            : CbItemMembers.super_method_7866(this, stack);
    }
}
