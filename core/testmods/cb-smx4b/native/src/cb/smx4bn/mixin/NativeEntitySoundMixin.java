package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.5 LivingEntity.makeSound(sound), which Piglin.playAngrySound calls (counted for piglins) */
@Mixin(net.minecraft.class_1309.class)
public abstract class NativeEntitySoundMixin {
    @Inject(method = "method_56078(Lnet/minecraft/class_3414;)V", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$sound(net.minecraft.class_3414 sound, CallbackInfo ci) {
        if ((Object) this instanceof net.minecraft.class_4836) {
            cb.smx4bn.Driver.makeSound++;
        }
    }
}
