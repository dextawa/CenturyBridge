package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemProps;

/**
 * ItemStack for an item whose 1.20.4 class overrides what 1.20.5 reads off the stack's components instead
 * of asking the item (rt/CbItemProps): the stack's rarity is the class's getRarity(stack), what may hurt
 * the dropped item its canBeHurtBy(source) -- as 1.20.4's ItemStack.getRarity and ItemEntity.hurt asked.
 */
@Mixin(net.minecraft.class_1799.class)
public abstract class CbItemStackPropsMixin {

    @Shadow
    public abstract net.minecraft.class_1792 method_7909();

    /** getRarity() */
    @Inject(method = "method_7932", at = @At("HEAD"), cancellable = true)
    private void cbS4$rarity(CallbackInfoReturnable<net.minecraft.class_1814> cir) {
        if (method_7909() instanceof CbItemProps.Rarity r) {
            cir.setReturnValue(r.method_7862((net.minecraft.class_1799) (Object) this));
        }
    }

    /** canBeHurtBy(source) */
    @Inject(method = "method_58407", at = @At("HEAD"), cancellable = true)
    private void cbS4$hurtBy(net.minecraft.class_1282 source, CallbackInfoReturnable<Boolean> cir) {
        if (method_7909() instanceof CbItemProps.HurtBy h) {
            cir.setReturnValue(h.method_24357(source));
        }
    }
}
