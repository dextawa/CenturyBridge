package cb.s6b.mixin;

import net.minecraft.class_2499;
import net.minecraft.class_7225;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.5 mod's mixin on SimpleContainer.createTag / fromTag: counts both (S6bTestMod.saveHits 2 / 3) */
@Mixin(net.minecraft.class_1277.class)
public abstract class S6bContainerMixin {

    @Inject(method = "method_7660(Lnet/minecraft/class_7225$class_7874;)Lnet/minecraft/class_2499;", at = @At("RETURN"))
    private void cbs6b$create(class_7225.class_7874 provider, CallbackInfoReturnable<class_2499> cir) {
        cb.s6b.S6bTestMod.saveHits[2]++;
    }

    @Inject(method = "method_7659(Lnet/minecraft/class_2499;Lnet/minecraft/class_7225$class_7874;)V", at = @At("TAIL"))
    private void cbs6b$from(class_2499 list, class_7225.class_7874 provider, CallbackInfo ci) {
        cb.s6b.S6bTestMod.saveHits[3]++;
    }
}
