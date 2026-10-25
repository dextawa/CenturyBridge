package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1308;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyNavigation;

/**
 * 1.20.2's AmphibiousPathNavigation.canMoveDirectly through 1.20.1's PathNavigation.isInLiquid() (restored by
 * CbNavigationLegacy): 1.20.1 asked the navigation's own isInLiquid(), 1.20.2 the mob's Entity.isInLiquid (the same
 * test); see CbNavigationVanillaMixin.
 */
@Mixin(net.minecraft.class_5766.class)
public abstract class CbNavigationAmphibiousVanillaMixin {
    @Redirect(method = "method_6341(Lnet/minecraft/class_243;Lnet/minecraft/class_243;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1308;method_52535()Z"), require = 1)
    private boolean cb$inLiquidThroughLegacy(class_1308 mob) {
        return ((CbLegacyNavigation) (Object) this).cb$legacyIsInLiquid();
    }
}
