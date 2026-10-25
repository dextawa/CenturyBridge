package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1767;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1819;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * ShieldItem's 1.21.1 getDescriptionId(stack) override, restored with 1.21.1's body so a mod's mixin on it finds its
 * method and anchors: a shield with a base colour is the description id + "." + the colour's name. 1.21.2's
 * getName(stack) override asks it first and follows it only where it answers otherwise than that body
 * (CbItemNameReadsMixin). Priority below every mod's so a mod's mixin may anchor anywhere in it
 * (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1819.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemShieldDescriptionIdMixin extends class_1792 {

    private CbItemShieldDescriptionIdMixin(class_1792.class_1793 properties) {
        super(properties);
    }

    /** getDescriptionId(stack); the super call is Item's restored body (CbItemMembers.super_method_7866) */
    @CbVanillaReads("CbItemNameReadsMixin: ShieldItem.getName(stack) names the stack from it where it "
        + "answers otherwise than its body")
    public String method_7866(class_1799 stack) {
        class_1767 base = stack.method_57824(class_9334.field_49620);
        return base != null ? this.method_7876() + "." + base.method_7792() : CbItemMembers.super_method_7866(this, stack);
    }
}
