package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_630;
import net.minecraft.class_7280;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_9.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * The constructor injector that goes with {@link CbWardenModelRestoredMixin} (its restored lists and getters stay
 * there, at CbVanillaReads.PRIORITY): at WardenModel's constructor RETURN it fills 1.21.8's four emissive part lists.
 * Priority CbVanillaReads.ROUTE_FIRST: 1.21.8 filled them in the constructor body, so a mod's constructor RETURN
 * injector (or anything it calls) found them filled; running first keeps that.
 */
@Mixin(value = class_7280.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbWardenModelRestoredMixinHooks {

    @Inject(method = "<init>(Lnet/minecraft/class_630;)V", at = @At("RETURN"))
    private void cb$v1_21_9$fillPartLists(class_630 root, CallbackInfo ci) {
        ((CbPrioritySplitClient.WardenLists) this).cb$v1_21_9$fillWardenLists();
    }
}
