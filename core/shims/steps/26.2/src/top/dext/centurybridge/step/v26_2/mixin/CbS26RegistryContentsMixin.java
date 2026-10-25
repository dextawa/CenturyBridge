package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26SurfaceRules;

/**
 * Registrations of the layer's own built-in registry entries, at the end of BuiltInRegistries.createContents (every
 * registry's bootstrap has run; bootStrap freezes them next): rt/CbS26SurfaceRules' key condition codec.
 */
@Mixin(BuiltInRegistries.class)
public abstract class CbS26RegistryContentsMixin {

    @Inject(method = "createContents()V", at = @At("TAIL"))
    private static void cbS26$contents(CallbackInfo ci) {
        CbS26SurfaceRules.register();
    }
}
