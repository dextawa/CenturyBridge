package cb.s7gear.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_3414;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** records the last sound an entity played (GearTestMod.soundReads) */
@Mixin(class_1297.class)
public abstract class PlaySoundRecorderMixin {

    @Inject(method = "method_5783(Lnet/minecraft/class_3414;FF)V", at = @At("HEAD"))
    private void cbs7gear$record(class_3414 sound, float volume, float pitch, CallbackInfo ci) {
        cb.s7gear.GearTestMod.lastSound = sound;
    }
}
