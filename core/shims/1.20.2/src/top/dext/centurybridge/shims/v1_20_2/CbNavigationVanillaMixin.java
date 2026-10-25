package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1308;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyNavigation;

/**
 * 1.20.2's ground / flying / water-bound navigations' canUpdatePath through 1.20.1's PathNavigation.isInLiquid()
 * (restored by CbNavigationLegacy). 1.20.1's canUpdatePath asked the navigation's own isInLiquid(); 1.20.2 asks the
 * mob (Entity.isInLiquid, the same isInWaterOrBubble() || isInLava()). Answering that one call from the restored
 * member gives the same value while nothing changes it, and a 1.20.1 mod's mixin on isInLiquid() decides again
 * whether the navigation may update its path, as it did on 1.20.1. AmphibiousPathNavigation's canMoveDirectly:
 * CbNavigationAmphibiousVanillaMixin.
 */
@Mixin({net.minecraft.class_1409.class, net.minecraft.class_1407.class, net.minecraft.class_1412.class})
public abstract class CbNavigationVanillaMixin {
    @Redirect(method = "method_6358()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1308;method_52535()Z"), require = 1)
    private boolean cb$inLiquidThroughLegacy(class_1308 mob) {
        return ((CbLegacyNavigation) (Object) this).cb$legacyIsInLiquid();
    }
}
