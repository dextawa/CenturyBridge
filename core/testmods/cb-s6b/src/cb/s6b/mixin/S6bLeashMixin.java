package cb.s6b.mixin;

import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * a 1.21.5 mod's mixin on PathfinderMob.handleLeashAtDistance: counts its runs (S6bTestMod.pathLeashHits); in 1.21.5 a
 * standing wolf reaches it through TamableAnimal's super call, a sitting one does not
 */
@Mixin(net.minecraft.class_1314.class)
public abstract class S6bLeashMixin {

    @Inject(method = "method_60956(Lnet/minecraft/class_1297;F)Z", at = @At("HEAD"))
    private void cbs6b$handle(class_1297 holder, float distance, CallbackInfoReturnable<Boolean> cir) {
        cb.s6b.S6bTestMod.pathLeashHits++;
    }
}
