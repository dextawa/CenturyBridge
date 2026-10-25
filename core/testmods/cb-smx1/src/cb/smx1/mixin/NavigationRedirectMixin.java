package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * A @Redirect inside PathNavigation.isInLiquid() (method_6351): its mob.isInWaterOrBubble() call (method_5816). On
 * 1.20.2 that body is the layer's restored member, so this needs the restoring mixin below every mod's priority;
 * while the driver asks for it, the redirect answers true.
 */
@Mixin(net.minecraft.class_1408.class)
public abstract class NavigationRedirectMixin {
    @Redirect(method = "method_6351()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1308;method_5816()Z"), remap = false)
    private boolean cbsmx1$water(net.minecraft.class_1308 mob) {
        if (Driver.forceWater) {
            Driver.count("navigation-water-redirect");
            return true;
        }
        return mob.method_5816();
    }
}
