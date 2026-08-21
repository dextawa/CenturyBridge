package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_2370;
import net.minecraft.class_5321;
import net.minecraft.class_9248;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_21_4.CbPending;

/**
 * Every registration funnels through SimpleRegistry.add; pending-keyed
 * blocks/items get their real key backfilled here before the entry binds
 * (see CbPending).
 */
@Mixin(class_2370.class)
public abstract class RegistryAddBridge {

    @Inject(method = "method_10272", at = @At("HEAD"))
    private void cb$backfill(class_5321<Object> key, Object entry, class_9248 info,
                             CallbackInfoReturnable<?> cir) {
        CbPending.backfill(key, entry);
    }
}
