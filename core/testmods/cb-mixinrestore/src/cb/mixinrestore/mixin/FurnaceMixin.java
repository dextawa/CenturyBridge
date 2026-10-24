package cb.mixinrestore.mixin;

import cb.mixinrestore.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * AbstractFurnaceBlockEntity.stillValid(Player) (method_5443): its own override through 1.20.4; 1.20.5
 * dropped it (inherited from BaseContainerBlockEntity). A HEAD injector there, as 1.20.1-era mods wrote
 * it: answers true for a null player and records that it ran.
 */
@Mixin(net.minecraft.class_2609.class)
public abstract class FurnaceMixin {
    @Inject(method = "method_5443(Lnet/minecraft/class_1657;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbrestore$stillValid(net.minecraft.class_1657 player, CallbackInfoReturnable<Boolean> cir) {
        Driver.ran("furnace-stillValid-head", "player=" + player);
        if (player == null) {
            cir.setReturnValue(true);
        }
    }
}
