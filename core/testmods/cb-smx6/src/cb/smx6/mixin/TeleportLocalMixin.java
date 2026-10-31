package cb.smx6.mixin;

import cb.smx6.Driver;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A 1.20.6 mod's @Inject in ServerPlayer.teleportTo(ServerLevel, x, y, z, yaw, pitch) before sendAllPlayerInfo, taking
 * the target's arguments and @Local(ordinal = 1) ServerLevel (the level the player left: teleportTo's serverLevel()
 * local) -- the shape of Kilt's ServerPlayerInject.fireChangeDimensionEvent (Kilt E8g). 1.21 moved that code into
 * changeDimension(DimensionTransition): MixinRepair's guarded descend carries the handler there with the arguments
 * stashed and the @Local matched by role (the local fed by serverLevel()).
 */
@Mixin(net.minecraft.class_3222.class)
public abstract class TeleportLocalMixin {

    @Inject(method = "method_14251(Lnet/minecraft/class_3218;DDDFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3324;method_14594(Lnet/minecraft/class_3222;)V"), remap = false)
    private void cbsmx6$teleported(net.minecraft.class_3218 newLevel, double x, double y, double z, float yaw, float pitch, CallbackInfo ci,
                                   @Local(ordinal = 1) net.minecraft.class_3218 old) {
        Driver.teleFired++;
        Driver.teleOld = old;
        Driver.teleNew = newLevel;
        Driver.teleX = x;
        Driver.teleAt = ((net.minecraft.class_3222) (Object) this).method_51469();
    }
}
