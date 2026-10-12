package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2423;
import net.minecraft.class_3218;
import net.minecraft.class_5454;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPortals;

/**
 * NetherPortalBlock: 1.20.6 asked the travelling entity -- its findDimensionEntryPoint, and through it its
 * getExitPortal -- where a nether portal takes it, and its getPortalWaitTime how long it waits; an entity of a
 * 1.20.6 class that overrides one of them is asked again (CbEntityPortals).
 */
@Mixin(class_2423.class)
public abstract class CbEntityNetherPortalMixin {

    @Inject(method = "method_60770(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;Lnet/minecraft/class_2338;)Lnet/minecraft/class_5454;",
            at = @At("HEAD"), cancellable = true)
    private void cb$legacyDestination(class_3218 level, class_1297 entity, class_2338 pos, CallbackInfoReturnable<class_5454> cir) {
        if (CbEntityPortals.asksLegacyDestination(entity, true)) {
            cir.setReturnValue(CbEntityPortals.legacyPortalDestination(level, entity, true));
        }
    }

    @Inject(method = "method_60772(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)I", at = @At("HEAD"), cancellable = true)
    private void cb$legacyWaitTime(class_3218 level, class_1297 entity, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof CbEntityPortals.LegacyPortalWaitTime legacy) {
            cir.setReturnValue(legacy.method_5741());
        }
    }
}
