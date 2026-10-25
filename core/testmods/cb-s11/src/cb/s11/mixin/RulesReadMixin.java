package cb.s11.mixin;

import cb.s11.Driver;
import net.minecraft.class_1928;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.10 mod's mixin on GameRules.getBoolean: forces keepInventory on while Driver.forceKeep is set */
@Mixin(class_1928.class)
public abstract class RulesReadMixin {
    @Inject(method = "method_8355(Lnet/minecraft/class_1928$class_4313;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbs11$forceKeep(class_1928.class_4313<class_1928.class_4310> key, CallbackInfoReturnable<Boolean> cir) {
        if (Driver.forceKeep && key == class_1928.field_19389) {
            cir.setReturnValue(true);
        }
    }
}
