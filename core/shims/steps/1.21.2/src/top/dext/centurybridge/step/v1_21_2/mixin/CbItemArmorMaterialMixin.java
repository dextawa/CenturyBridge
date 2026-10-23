package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1741;
import net.minecraft.class_1792;
import net.minecraft.class_6862;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemArmor;

/**
 * ArmorMaterial.humanoidProperties / animalProperties (both shapes) for a material made from a 1.21.1 one
 * (CbItemArmor.toW): 1.21.1's ArmorItem and AnimalArmorItem never set the durability (their Properties carry the
 * mod's), had no enchantability below 1 (1.21.2's Enchantable refuses it; only humanoidProperties sets one) and
 * repaired with the material's ingredient, not a tag. Every other material goes through unchanged.
 */
@Mixin(class_1741.class)
public abstract class CbItemArmorMaterialMixin {

    @Redirect(method = {"method_63991", "method_63992", "method_64503"}, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1792$class_1793;method_7895(I)Lnet/minecraft/class_1792$class_1793;"))
    private class_1792.class_1793 cb$durability(class_1792.class_1793 props, int durability) {
        return CbItemArmor.legacy((class_1741) (Object) this) ? props : props.method_7895(durability);
    }

    @Redirect(method = "method_63991", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1792$class_1793;method_61649(I)Lnet/minecraft/class_1792$class_1793;"))
    private class_1792.class_1793 cb$enchantable(class_1792.class_1793 props, int value) {
        return CbItemArmor.legacy((class_1741) (Object) this) && value <= 0 ? props : props.method_61649(value);
    }

    @Redirect(method = {"method_63991", "method_63992", "method_64503"}, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1792$class_1793;method_61647(Lnet/minecraft/class_6862;)Lnet/minecraft/class_1792$class_1793;"))
    private class_1792.class_1793 cb$repairable(class_1792.class_1793 props, class_6862<class_1792> tag) {
        class_1741 self = (class_1741) (Object) this;
        return CbItemArmor.legacy(self) ? props.method_57349(class_9334.field_53696, CbItemArmor.repairable(self))
            : props.method_61647(tag);
    }
}
