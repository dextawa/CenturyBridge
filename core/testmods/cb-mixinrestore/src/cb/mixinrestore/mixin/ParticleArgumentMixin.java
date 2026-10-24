package cb.mixinrestore.mixin;

import cb.mixinrestore.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ParticleArgument's static initializer (class_2223.<clinit>) builds one DynamicCommandExceptionType
 * through 1.20.4; 1.20.5 builds a second one after it. An unordinaled NEW injector, as written against
 * V, fires once per V site: MixinRepair sites-split pins it to the counterpart of V's site, so it still
 * fires once on 1.20.5, not twice.
 */
@Mixin(net.minecraft.class_2223.class)
public abstract class ParticleArgumentMixin {
    @Inject(method = "<clinit>", at = @At(value = "NEW", target = "com/mojang/brigadier/exceptions/DynamicCommandExceptionType"),
        remap = false)
    private static void cbrestore$newException(CallbackInfo ci) {
        Driver.count("particle-argument-new-exception");
    }
}
