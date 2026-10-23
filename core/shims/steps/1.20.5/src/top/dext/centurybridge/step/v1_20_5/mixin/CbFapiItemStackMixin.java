package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.function.BiConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.item.CbFapiItems;

/**
 * Fabric API 1.20.4's stack-aware item hooks where 1.20.5's ItemStack reads what they answered
 * (compat/fapi/impl/item/CbFapiItems):
 * <ul>
 * <li>applyAttributeModifiers(slot, consumer), before anything: a stack whose patch sets no modifiers gets
 *     1.20.4's FabricItem.getAttributeModifiers(stack, slot) and ModifyItemAttributeModifiersCallback when
 *     either has a say (else vanilla goes on, to s4item's Item.getAttributeModifiers() hook);</li>
 * <li>getComponents(), as it returns: a stack of an item whose class overrides 1.20.4's
 *     getFoodComponent(stack) answers FOOD with it (after s4item's HEAD flush of its NBT view).</li>
 * </ul>
 */
@Mixin(net.minecraft.class_1799.class)
public abstract class CbFapiItemStackMixin {

    @Inject(method = "method_57354(Lnet/minecraft/class_1304;Ljava/util/function/BiConsumer;)V", at = @At("HEAD"), cancellable = true)
    private void cb$fapiAttributeModifiers(net.minecraft.class_1304 slot,
                                           BiConsumer<net.minecraft.class_6880<net.minecraft.class_1320>, net.minecraft.class_1322> consumer,
                                           CallbackInfo ci) {
        if (CbFapiItems.feedAttributeModifiers((net.minecraft.class_1799) (Object) this, slot, consumer)) {
            ci.cancel();
        }
    }

    @Inject(method = "method_57353()Lnet/minecraft/class_9323;", at = @At("RETURN"), cancellable = true)
    private void cb$fapiFood(CallbackInfoReturnable<net.minecraft.class_9323> cir) {
        net.minecraft.class_9323 components = cir.getReturnValue();
        net.minecraft.class_9323 view = CbFapiItems.withFood((net.minecraft.class_1799) (Object) this, components);
        if (view != components) {
            cir.setReturnValue(view);
        }
    }
}
