package cb.s6b.mixin;

import net.minecraft.class_2499;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.5 mod's mixin on Inventory.save / load(ListTag): counts both */
@Mixin(net.minecraft.class_1661.class)
public abstract class S6bInventoryMixin {

    @Inject(method = "method_7384(Lnet/minecraft/class_2499;)Lnet/minecraft/class_2499;", at = @At("RETURN"))
    private void cbs6b$save(class_2499 list, CallbackInfoReturnable<class_2499> cir) {
        cb.s6b.S6bTestMod.saveHits[0]++;
    }

    @Inject(method = "method_7397(Lnet/minecraft/class_2499;)V", at = @At("TAIL"))
    private void cbs6b$load(class_2499 list, CallbackInfo ci) {
        cb.s6b.S6bTestMod.saveHits[1]++;
    }
}
