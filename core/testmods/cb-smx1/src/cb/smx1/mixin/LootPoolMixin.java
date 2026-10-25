package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * LootPool's private constructor (class_55, arrays of entries / conditions / functions in 1.20.1; Lists in 1.20.2,
 * where the codec builds every pool through it). A RETURN injector taking none of its arguments: counts the pools
 * built while the server loads its loot tables.
 */
@Mixin(net.minecraft.class_55.class)
public abstract class LootPoolMixin {
    @Inject(method = "<init>([Lnet/minecraft/class_79;[Lnet/minecraft/class_5341;[Lnet/minecraft/class_117;Lnet/minecraft/class_5658;Lnet/minecraft/class_5658;)V",
        at = @At("RETURN"), remap = false)
    private void cbsmx1$built(CallbackInfo ci) {
        Driver.count("lootpool-init");
    }
}
