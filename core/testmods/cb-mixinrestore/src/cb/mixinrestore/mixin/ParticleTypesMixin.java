package cb.mixinrestore.mixin;

import cb.mixinrestore.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ParticleTypes.register(String, boolean, ParticleOptions.Deserializer, Function) (class_2398.method_42022),
 * private, called from the static initializer for every particle type with options. 1.20.5 dropped the
 * Deserializer and took a stream codec function instead, with no wiring (private): MixinBridge's body
 * follow moves the selector to the W method holding the same code. The handler takes no arguments.
 */
@Mixin(net.minecraft.class_2398.class)
public abstract class ParticleTypesMixin {
    @Inject(method = "method_42022(Ljava/lang/String;ZLnet/minecraft/class_2394$class_2395;Ljava/util/function/Function;)Lnet/minecraft/class_2396;",
        at = @At("HEAD"), remap = false)
    private static void cbrestore$register(CallbackInfoReturnable<?> cir) {
        Driver.count("particle-types-register");
    }
}
