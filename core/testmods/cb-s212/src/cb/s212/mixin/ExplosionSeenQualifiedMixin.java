package cb.s212.mixin;

import cb.s212.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.1 Explosion.getSeenPercent(Vec3 center, Entity), selected with its owner named in the selector (some mods write
 * selectors this way): sees the entity and, while the driver asks, answers 0 (fully covered).
 */
@Mixin(net.minecraft.class_1927.class)
public abstract class ExplosionSeenQualifiedMixin {
    @Inject(method = "Lnet/minecraft/class_1927;method_17752(Lnet/minecraft/class_243;Lnet/minecraft/class_1297;)F", at = @At("RETURN"),
            cancellable = true, remap = false)
    private static void cbs212$seenQualified(net.minecraft.class_243 center, net.minecraft.class_1297 entity, CallbackInfoReturnable<Float> cir) {
        if (entity == Driver.seenTarget) {
            Driver.seenAskedQualified++;
            if (Driver.seenHide) {
                cir.setReturnValue(0.0F);
            }
        }
    }
}
