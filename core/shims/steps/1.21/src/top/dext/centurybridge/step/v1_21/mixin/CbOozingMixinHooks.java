package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;

/**
 * The route into 1.21's own OozingMobEffect.onMobRemoved that goes with {@link CbOozingMixin} (its restored 1.20.6
 * numberOfSlimesToSpawn stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.6 injectors can anchor inside
 * it): 1.21's ask goes through the restored helper, with the count taken as 1.21 takes it. With the maxEntityCramming
 * rule below 1, 1.21 spawns spawnCount without asking: the helper is still called, its answer not used.
 */
@Mixin(net.minecraft.class_9456.class)
public abstract class CbOozingMixinHooks {

    /** 1.21's ask, routed through the restored 1.20.6 helper */
    @Redirect(method = "method_58617(Lnet/minecraft/class_1309;ILnet/minecraft/class_1297$class_5529;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9456;method_59652(ILnet/minecraft/class_9456$class_9775;I)I"))
    private int cb$numberOfSlimesToSpawn(int maxEntityCramming, net.minecraft.class_9456.class_9775 nearby, int spawnCount) {
        int count = maxEntityCramming < 1 ? 0 : nearby.count(maxEntityCramming);
        int asked = ((CbPrioritySplit.OozingCount) (Object) this).cb$v1_21$numberOfSlimesToSpawn(maxEntityCramming, count, spawnCount);
        return maxEntityCramming < 1 ? spawnCount : asked;
    }
}
