package cb.s212.mixin;

import cb.s212.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.1 Explosion.getSeenPercent(Vec3 center, Entity) (public static; 1.21.2 moved it, the same code, to
 * ServerExplosion), selected by name only (the usual form): counts the calls for the driver's entity.
 */
@Mixin(net.minecraft.class_1927.class)
public abstract class ExplosionSeenMixin {
    @Inject(method = "method_17752(Lnet/minecraft/class_243;Lnet/minecraft/class_1297;)F", at = @At("RETURN"), remap = false)
    private static void cbs212$seen(net.minecraft.class_243 center, net.minecraft.class_1297 entity, CallbackInfoReturnable<Float> cir) {
        if (entity == Driver.seenTarget) {
            Driver.seenAsked++;
        }
    }
}
