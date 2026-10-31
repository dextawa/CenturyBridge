package cb.smx6v.mixin;

import cb.smx6v.Driver;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A native 1.21 mod's @Inject in ServerPlayer.changeDimension(DimensionTransition) before sendAllPlayerInfo, with
 * @Local(ordinal = 1) ServerLevel (the level left) -- the place native Kilt 21.1 fires its changed-dimension event, and the
 * method cb-smx6's TeleportLocalMixin descends into (guarded) with the layer. Must stay untouched: it fires on every
 * cross-level move, teleportTo or portal.
 */
@Mixin(net.minecraft.class_3222.class)
public abstract class ChangeDimensionLocalMixin {

    @Inject(method = "method_5731(Lnet/minecraft/class_5454;)Lnet/minecraft/class_1297;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3324;method_14594(Lnet/minecraft/class_3222;)V"), remap = false)
    private void cbsmx6v$moved(net.minecraft.class_5454 transition, CallbackInfoReturnable<net.minecraft.class_1297> cir,
                               @Local(ordinal = 1) net.minecraft.class_3218 old) {
        Driver.nativeDimFired++;
        Driver.nativeDimOld = old;
        Driver.nativeDimNew = transition.comp_2820();
    }
}
