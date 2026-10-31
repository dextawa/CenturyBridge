package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import net.minecraft.class_5365;
import net.minecraft.class_7172;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.21.11: OptionInstance.get() / set(T) heads, for the graphicsMode view and for every ordinary option */
@Mixin(class_7172.class)
public class NativeOptionMixin {
    @Inject(method = "method_41753", at = @At("HEAD"))
    private void cbSmx1dn$get(CallbackInfoReturnable<Object> cir) {
        Probe.bumpRender("optionGet");
    }

    @Inject(method = "method_41748", at = @At("HEAD"))
    private void cbSmx1dn$set(Object value, CallbackInfo ci) {
        Probe.bumpRender("optionSet");
        if (value instanceof class_5365) {
            Probe.bumpRender("optionSetGraphics");
        }
    }
}
