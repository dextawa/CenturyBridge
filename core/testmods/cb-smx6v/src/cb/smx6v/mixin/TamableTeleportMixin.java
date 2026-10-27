package cb.smx6v.mixin;

import cb.smx6v.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** a native 1.21 mod's mixin on TamableAnimal.tryToTeleportToOwner (class_1321.method_60713): counts the calls */
@Mixin(net.minecraft.class_1321.class)
public abstract class TamableTeleportMixin {

    @Inject(method = "method_60713()V", at = @At("HEAD"))
    private void cbsmx6v$tryToTeleport(CallbackInfo ci) {
        Driver.nativeTeleports++;
    }
}
