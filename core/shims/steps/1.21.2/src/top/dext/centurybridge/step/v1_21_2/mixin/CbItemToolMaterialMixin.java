package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_6862;
import net.minecraft.class_9886;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.facade.Tier;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemTiers;

/**
 * ToolMaterial.applyToolProperties / applySwordProperties for a material made from a 1.21.1 tier
 * (CbItemTiers.toW): what 1.21.1's TieredItem and DiggerItem / SwordItem constructors set -- durability, a positive
 * enchantment value, the repair ingredient, the tier's Tool (DiggerItem: the tier's createToolProperties for the
 * mineable blocks; SwordItem: 1.21.1's sword tool) -- and not the attribute modifiers, which 1.21.1 left to the
 * caller's Properties. Every other material goes through unchanged.
 */
@Mixin(class_9886.class)
public abstract class CbItemToolMaterialMixin {

    @Inject(method = "method_61663", at = @At("HEAD"), cancellable = true)
    private void cb$tool(class_1792.class_1793 props, class_6862<class_2248> mineable, float attackDamage, float attackSpeed,
                         CallbackInfoReturnable<class_1792.class_1793> cir) {
        class_9886 self = (class_9886) (Object) this;
        Tier tier = CbItemTiers.legacy(self);
        if (tier != null) {
            cir.setReturnValue(CbItemTiers.legacyProperties(self, props, tier.method_58418(mineable)));
        }
    }

    @Inject(method = "method_61662", at = @At("HEAD"), cancellable = true)
    private void cb$sword(class_1792.class_1793 props, float attackDamage, float attackSpeed,
                          CallbackInfoReturnable<class_1792.class_1793> cir) {
        class_9886 self = (class_9886) (Object) this;
        if (CbItemTiers.legacy(self) != null) {
            cir.setReturnValue(CbItemTiers.legacyProperties(self, props, CbItemTiers.swordTool()));
        }
    }
}
