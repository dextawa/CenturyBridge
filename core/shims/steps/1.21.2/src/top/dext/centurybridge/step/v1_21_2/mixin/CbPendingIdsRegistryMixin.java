package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_9248;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbPendingIds;

/**
 * MappedRegistry.register(key, value, info): where every registration ends (Registry.register and
 * registerForHolder go through WritableRegistry.register, which MappedRegistry -- DefaultedMappedRegistry
 * by super -- implements). HEAD, before the value binds: a value built under a placeholder gets its real
 * key (CbPendingIds).
 */
@Mixin(class_2370.class)
public abstract class CbPendingIdsRegistryMixin<T> {

    @Shadow
    @Final
    private class_5321<? extends class_2378<T>> field_41126;

    @Inject(method = "method_10272", at = @At("HEAD"))
    private void cb$backfill(class_5321<T> key, T value, class_9248 info, CallbackInfoReturnable<class_6880.class_6883<T>> cir) {
        CbPendingIds.register(this.field_41126, key, value);
    }
}
