package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.item.CbFapiItems;

/**
 * Fabric API 1.20.4's intrinsic enchantments (FabricItem.getIntrinsicEnchantments(stack)) where 1.20.4's
 * Fabric API applied them (its EnchantmentHelperMixin), on 1.20.5's EnchantmentHelper: getLevel answers at
 * least the intrinsic level; forEachEnchantment(consumer, stack) walks the stack's enchantments each at least
 * at its intrinsic level (compat/fapi/impl/item/CbFapiItems). Only items whose class declares the 1.20.4
 * method are touched.
 */
@Mixin(net.minecraft.class_1890.class)
public abstract class CbFapiEnchantmentHelperMixin {

    @Inject(method = "method_8225(Lnet/minecraft/class_1887;Lnet/minecraft/class_1799;)I", at = @At("RETURN"), cancellable = true)
    private static void cb$intrinsicLevel(net.minecraft.class_1887 enchantment, net.minecraft.class_1799 stack, CallbackInfoReturnable<Integer> cir) {
        int intrinsic = CbFapiItems.intrinsicLevel(stack, enchantment);
        if (intrinsic > cir.getReturnValueI()) {
            cir.setReturnValue(intrinsic);
        }
    }

    @Redirect(method = "method_8220(Lnet/minecraft/class_1890$class_1891;Lnet/minecraft/class_1799;)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1799;method_57825(Lnet/minecraft/class_9331;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object cb$intrinsicWalk(net.minecraft.class_1799 stack, net.minecraft.class_9331<?> type, Object fallback) {
        Object enchantments = stack.method_57825((net.minecraft.class_9331) type, fallback);
        return enchantments instanceof net.minecraft.class_9304 e ? CbFapiItems.withIntrinsic(stack, e) : enchantments;
    }
}
