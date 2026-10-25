package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod on OozingMobEffect.numberOfSlimesToSpawn(maxEntityCramming, nearbySlimes, spawnCount)
 * (class_9456.method_59652(III)I): records what vanilla asked with, and answers 0 while Driver.noSlimes is set.
 */
@Mixin(targets = "net.minecraft.class_9456")
public abstract class OozingMixin {
    @Inject(method = "method_59652(III)I", at = @At("HEAD"), cancellable = true, remap = false)
    private static void cbsmx6$slimes(int maxEntityCramming, int nearbySlimes, int spawnCount, CallbackInfoReturnable<Integer> cir) {
        Driver.slimesAsked++;
        Driver.slimesCramming = maxEntityCramming;
        Driver.slimesSpawnCount = spawnCount;
        if (Driver.noSlimes) {
            cir.setReturnValue(0);
        }
    }
}
