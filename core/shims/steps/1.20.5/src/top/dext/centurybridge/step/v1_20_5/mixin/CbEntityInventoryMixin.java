package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Inventory: 1.20.4's hurtArmor(source, damage, slots) -- a quarter of the damage (at least 1) to each named armor
 * piece that is armor and the source can hurt -- which 1.20.5 moved onto LivingEntity. 1.20.4's body over 1.20.5's
 * item damage, breaking in the same armor slot.
 */
@Mixin(net.minecraft.class_1661.class)
public abstract class CbEntityInventoryMixin {

    @Shadow
    @Final
    public net.minecraft.class_2371<net.minecraft.class_1799> field_7548;

    @Shadow
    @Final
    public net.minecraft.class_1657 field_7546;

    /** was hurtArmor(source, damage, slots) */
    public void method_7375(net.minecraft.class_1282 source, float damage, int[] slots) {
        if (damage <= 0.0F) {
            return;
        }
        damage /= 4.0F;
        if (damage < 1.0F) {
            damage = 1.0F;
        }
        for (int i : slots) {
            net.minecraft.class_1799 stack = this.field_7548.get(i);
            if (stack.method_58407(source) && stack.method_7909() instanceof net.minecraft.class_1738) {
                stack.method_7970((int) damage, this.field_7546,
                    net.minecraft.class_1304.method_20234(net.minecraft.class_1304.class_1305.field_6178, i));
            }
        }
    }
}
