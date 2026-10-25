package cb.s11n.mixin;

import cb.s11n.NativeDriver;
import net.minecraft.class_12279;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a native 1.21.11 mod's mixins on GameRule.serialize: count calls at HEAD and RETURN, keep the answer it saw */
@Mixin(class_12279.class)
public abstract class NativeGameRuleMixin {
    @Inject(method = "method_76146", at = @At("HEAD"), remap = false)
    private void cbs11n$serializeHead(Object value, CallbackInfoReturnable<String> cir) {
        NativeDriver.serializeHeads++;
    }

    @Inject(method = "method_76146", at = @At("RETURN"), remap = false)
    private void cbs11n$serializeReturn(Object value, CallbackInfoReturnable<String> cir) {
        NativeDriver.serializeReturns++;
        NativeDriver.lastSerialized = cir.getReturnValue();
    }
}
