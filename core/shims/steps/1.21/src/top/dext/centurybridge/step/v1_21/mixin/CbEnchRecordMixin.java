package top.dext.centurybridge.step.v1_21.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.facade.class_1887;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantments;

/**
 * 1.21's Enchantment record: on one of CenturyBridge's records of a 1.20.6 mod's enchantment, the
 * questions 1.20.6 asked the enchantment object go to the object (CbEnchantments.legacyOf):
 * <ul>
 * <li>canEnchant -- the anvil, the enchant command, random enchantment loot (1.20.6 asked canEnchant
 *     at each);</li>
 * <li>isPrimaryItem -- 1.21's enchanting selection (EnchantmentHelper.getAvailableEnchantmentResults)
 *     asks it alone where 1.20.6 asked canEnchant and isPrimaryItem;</li>
 * <li>getSlotItems -- the items an entity's level of the enchantment is read from;</li>
 * <li>areCompatible -- 1.21's single compatibility test (anvil, enchanting) where 1.20.6 asked
 *     isCompatibleWith, both ways;</li>
 * <li>getFullname -- the name in tooltips and messages.</li>
 * </ul>
 * Every other record keeps 1.21's answers.
 */
@Mixin(net.minecraft.class_1887.class)
public abstract class CbEnchRecordMixin {

    private class_1887 cbEnch$legacy() {
        return CbEnchantments.legacyOf((net.minecraft.class_1887) (Object) this);
    }

    /** canEnchant(ItemStack) */
    @Inject(method = "method_8192", at = @At("HEAD"), cancellable = true)
    private void cbEnch$canEnchant(net.minecraft.class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        class_1887 e = cbEnch$legacy();
        if (e != null) {
            cir.setReturnValue(e.method_8192(stack));
        }
    }

    /** isPrimaryItem(ItemStack): 1.20.6's canEnchant and isPrimaryItem together, as its enchanting selection asked */
    @Inject(method = "method_58444", at = @At("HEAD"), cancellable = true)
    private void cbEnch$isPrimaryItem(net.minecraft.class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        class_1887 e = cbEnch$legacy();
        if (e != null) {
            cir.setReturnValue(e.method_8192(stack) && e.method_58444(stack));
        }
    }

    /** getSlotItems(LivingEntity) */
    @Inject(method = "method_8185", at = @At("HEAD"), cancellable = true)
    private void cbEnch$slotItems(net.minecraft.class_1309 entity, CallbackInfoReturnable<Map<net.minecraft.class_1304, net.minecraft.class_1799>> cir) {
        class_1887 e = cbEnch$legacy();
        if (e != null) {
            cir.setReturnValue(e.method_8185(entity));
        }
    }

    /** areCompatible(Holder, Holder): 1.20.6's isCompatibleWith when either side is a mod's enchantment */
    @Inject(method = "method_60033", at = @At("HEAD"), cancellable = true)
    private static void cbEnch$areCompatible(net.minecraft.class_6880<net.minecraft.class_1887> a, net.minecraft.class_6880<net.minecraft.class_1887> b,
            CallbackInfoReturnable<Boolean> cir) {
        class_1887 la = CbEnchantments.legacyOf(a.comp_349());
        class_1887 lb = CbEnchantments.legacyOf(b.comp_349());
        if (la != null || lb != null) {
            class_1887 ea = la != null ? la : CbEnchantments.cbEnchantment(a);
            class_1887 eb = lb != null ? lb : CbEnchantments.cbEnchantment(b);
            cir.setReturnValue(ea.method_8188(eb));
        }
    }

    /** getFullname(Holder, int) */
    @Inject(method = "method_8179", at = @At("HEAD"), cancellable = true)
    private static void cbEnch$fullname(net.minecraft.class_6880<net.minecraft.class_1887> holder, int level,
            CallbackInfoReturnable<net.minecraft.class_2561> cir) {
        class_1887 e = CbEnchantments.legacyOf(holder.comp_349());
        if (e != null) {
            cir.setReturnValue(e.method_8179(level));
        }
    }
}
