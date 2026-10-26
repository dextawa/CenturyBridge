package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.4 Piglin.makeSound(sound): counted, the sound noted */
@Mixin(net.minecraft.class_4836.class)
public abstract class PiglinSoundMixin {
    @Inject(method = "method_30086(Lnet/minecraft/class_3414;)V", at = @At("HEAD"), remap = false)
    private void cbsmx4b$sound(net.minecraft.class_3414 sound, CallbackInfo ci) {
        Driver.piglinAsked++;
        Driver.piglinSound = sound;
    }
}
