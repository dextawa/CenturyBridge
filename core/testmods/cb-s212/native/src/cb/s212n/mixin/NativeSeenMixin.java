package cb.s212n.mixin;

import cb.s212n.Counts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21.2 ServerExplosion.getSeenPercent(Vec3, Entity) */
@Mixin(net.minecraft.class_9892.class)
public abstract class NativeSeenMixin {
    @Inject(method = "method_61731(Lnet/minecraft/class_243;Lnet/minecraft/class_1297;)F", at = @At("RETURN"), remap = false)
    private static void cbs212n$seen(net.minecraft.class_243 center, net.minecraft.class_1297 entity, CallbackInfoReturnable<Float> cir) {
        Counts.seen++;
    }
}
