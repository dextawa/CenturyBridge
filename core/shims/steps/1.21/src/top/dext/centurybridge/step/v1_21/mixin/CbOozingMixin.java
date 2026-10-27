package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * OozingMobEffect (class_9456) and its slime count. 1.20.6's onMobRemoved counted the nearby slimes itself
 * (Level.getEntities(SLIME, box, filter, maxEntityCramming).size()) and asked numberOfSlimesToSpawn(maxEntityCramming,
 * nearbySlimes, spawnCount) (method_59652(III)I) how many to spawn. 1.21 passes a counting function instead
 * (method_59652(I NearbySlimes I)I, NearbySlimes.count(limit) runs the same capped search) and answers spawnCount
 * unchanged when the maxEntityCramming game rule is below 1. This mixin restores the 1.20.6 helper with its body and
 * routes 1.21's call in onMobRemoved (method_58617) through it, with the count taken as 1.21 takes it. With nothing
 * mixed in the answer is 1.21's: Mth.clamp(0, cramming - count, spawnCount) for a cramming rule of 1 or more, spawnCount
 * below 1. The route (1.21's call in onMobRemoved) is CbOozingMixinHooks (default priority); it asks the restored helper
 * through cb$v1_21$numberOfSlimesToSpawn, whose call to it sits here.
 */
@Mixin(value = net.minecraft.class_9456.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbOozingMixin implements CbPrioritySplit.OozingCount {

    /**
     * 1.20.6 OozingMobEffect.numberOfSlimesToSpawn(maxEntityCramming, nearbySlimes, spawnCount) (protected static in 1.20.6;
     * private here: Mixin merges no other static from a class mixin, and a mod's injector, @Invoker or @Shadow reaches it all
     * the same)
     */
    @CbVanillaReads(value = "CbOozingMixinHooks: onMobRemoved (method_58617) takes its slime count from this method (cb$numberOfSlimesToSpawn replaces "
        + "1.21's numberOfSlimesToSpawn(int, NearbySlimes, int)); the body is 1.20.6's. With the maxEntityCramming rule below 1, "
        + "1.21 spawns spawnCount without asking (1.20.6 asked and got 0): there the method is still called, but its answer is not used",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN", "Inject@INVOKE", "Redirect@INVOKE",
            "ModifyArg@INVOKE", "ModifyArgs@INVOKE", "ModifyExpressionValue@INVOKE", "WrapOperation@INVOKE"})
    private static int method_59652(int maxEntityCramming, int nearbySlimes, int spawnCount) {
        return net.minecraft.class_3532.method_15340(0, maxEntityCramming - nearbySlimes, spawnCount);
    }

    /** the restored helper, for 1.21's call (CbOozingMixinHooks) */
    @Override
    public int cb$v1_21$numberOfSlimesToSpawn(int maxEntityCramming, int nearbySlimes, int spawnCount) {
        return method_59652(maxEntityCramming, nearbySlimes, spawnCount);
    }
}
