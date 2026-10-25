package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * OozingMobEffect (class_9456) and its slime count. 1.20.6's onMobRemoved counted the nearby slimes itself
 * (Level.getEntities(SLIME, box, filter, maxEntityCramming).size()) and asked numberOfSlimesToSpawn(maxEntityCramming,
 * nearbySlimes, spawnCount) (method_59652(III)I) how many to spawn. 1.21 passes a counting function instead
 * (method_59652(I NearbySlimes I)I, NearbySlimes.count(limit) runs the same capped search) and answers spawnCount
 * unchanged when the maxEntityCramming game rule is below 1. This mixin restores the 1.20.6 helper with its body and
 * routes 1.21's call in onMobRemoved (method_58617) through it, with the count taken as 1.21 takes it. With nothing
 * mixed in the answer is 1.21's: Mth.clamp(0, cramming - count, spawnCount) for a cramming rule of 1 or more, spawnCount
 * below 1.
 */
@Mixin(value = net.minecraft.class_9456.class, priority = 1)
public abstract class CbOozingMixin {

    /**
     * 1.20.6 OozingMobEffect.numberOfSlimesToSpawn(maxEntityCramming, nearbySlimes, spawnCount) (protected static in 1.20.6;
     * private here: Mixin merges no other static from a class mixin, and a mod's injector, @Invoker or @Shadow reaches it all
     * the same)
     */
    @CbVanillaReads(value = "CbOozingMixin: onMobRemoved (method_58617) takes its slime count from this method (cb$count replaces "
        + "1.21's numberOfSlimesToSpawn(int, NearbySlimes, int)); the body is 1.20.6's. With the maxEntityCramming rule below 1, "
        + "1.21 spawns spawnCount without asking (1.20.6 asked and got 0): there the method is still called, but its answer is not used",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN", "Inject@INVOKE", "Redirect@INVOKE",
            "ModifyArg@INVOKE", "ModifyArgs@INVOKE", "ModifyExpressionValue@INVOKE", "WrapOperation@INVOKE"})
    private static int method_59652(int maxEntityCramming, int nearbySlimes, int spawnCount) {
        return net.minecraft.class_3532.method_15340(0, maxEntityCramming - nearbySlimes, spawnCount);
    }

    /** 1.21's ask, routed through the restored 1.20.6 helper */
    @Redirect(method = "method_58617(Lnet/minecraft/class_1309;ILnet/minecraft/class_1297$class_5529;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9456;method_59652(ILnet/minecraft/class_9456$class_9775;I)I"))
    private static int cb$numberOfSlimesToSpawn(int maxEntityCramming, net.minecraft.class_9456.class_9775 nearby, int spawnCount) {
        int count = maxEntityCramming < 1 ? 0 : nearby.count(maxEntityCramming);
        int asked = method_59652(maxEntityCramming, count, spawnCount);
        return maxEntityCramming < 1 ? spawnCount : asked;
    }
}
