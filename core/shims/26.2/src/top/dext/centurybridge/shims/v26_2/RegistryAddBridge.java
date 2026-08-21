package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v26_2.CbPending;

/**
 * Every registration funnels through SimpleRegistry.add; pending-keyed
 * blocks/items get their real key backfilled here before the entry binds
 * (see CbPending).
 */
@Mixin(net.minecraft.core.MappedRegistry.class)
public abstract class RegistryAddBridge {

    @Inject(method = "register", at = @At("HEAD"))
    private void cb$backfill(net.minecraft.resources.ResourceKey<Object> key, Object entry, net.minecraft.core.RegistrationInfo info,
                             CallbackInfoReturnable<?> cir) {
        CbPending.backfill(key, entry);
    }
}
