package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_2334;
import net.minecraft.class_2338;
import net.minecraft.class_3218;
import net.minecraft.class_5454;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPortals;

/**
 * EndPortalBlock: 1.20.6's end portal moved the entity with changeDimension(end / overworld), which asked the
 * entity's findDimensionEntryPoint where to arrive; an entity of a 1.20.6 class that overrides it is asked again.
 */
@Mixin(class_2334.class)
public abstract class CbEntityEndPortalMixin {

    @Inject(method = "method_60770(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;Lnet/minecraft/class_2338;)Lnet/minecraft/class_5454;",
            at = @At("HEAD"), cancellable = true)
    private void cb$legacyDestination(class_3218 level, class_1297 entity, class_2338 pos, CallbackInfoReturnable<class_5454> cir) {
        if (CbEntityPortals.asksLegacyDestination(entity, false)) {
            cir.setReturnValue(CbEntityPortals.legacyPortalDestination(level, entity, false));
        }
    }
}
