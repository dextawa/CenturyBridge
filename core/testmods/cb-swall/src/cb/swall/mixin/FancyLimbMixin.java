package cb.swall.mixin;

import cb.swall.SwallTestMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21.11's private FancyTrunkPlacer.makeLimb(reader, ..) (class_5212.method_27393), as 1.21.11-era mods mixed it: counts
 * the limbs a fancy oak lays. On 26.1 the selector follows makeLimb(WorldGenLevel, ..) (callReshapes + mixinTargets: the
 * handler's LevelSimulatedReader takes the WorldGenLevel).
 */
@Mixin(net.minecraft.class_5212.class)
public abstract class FancyLimbMixin {
    @Inject(method = "method_27393(Lnet/minecraft/class_3746;Ljava/util/function/BiConsumer;Lnet/minecraft/class_5819;Lnet/minecraft/class_2338;Lnet/minecraft/class_2338;ZLnet/minecraft/class_4643;)Z",
        at = @At("HEAD"), remap = false)
    private void cbswall$countLimb(net.minecraft.class_3746 reader, java.util.function.BiConsumer<net.minecraft.class_2338, net.minecraft.class_2680> setter,
            net.minecraft.class_5819 random, net.minecraft.class_2338 from, net.minecraft.class_2338 to, boolean place,
            net.minecraft.class_4643 config, CallbackInfoReturnable<Boolean> cir) {
        if (reader != null) {
            SwallTestMod.FANCY_LIMB_CALLS.incrementAndGet();
        }
    }
}
