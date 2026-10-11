package top.dext.centurybridge.shims.v26_2;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v26_2.CbProvisionalComponents;

/**
 * Components asked for before the data pack load bound them: answered provisionally
 * (CbProvisionalComponents) instead of "Components not bound yet". Vanilla never asks that
 * early; 1.20.1 code did, routinely -- an ItemStack built while registering items.
 */
@Mixin(Holder.Reference.class)
public abstract class HolderComponentsBridge {

    @Shadow
    private DataComponentMap components;

    @Inject(method = "components", at = @At("HEAD"), cancellable = true)
    private void cb$provisional(CallbackInfoReturnable<DataComponentMap> cir) {
        if (this.components == null) {
            DataComponentMap early = CbProvisionalComponents.of((Holder.Reference<?>) (Object) this);
            if (early != null) {
                this.components = early;
                cir.setReturnValue(early);
            }
        }
    }
}
