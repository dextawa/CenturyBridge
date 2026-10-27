package top.dext.centurybridge.step.v1_21.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * The injectors into 1.21's own FollowOwnerGoal (class_1350) methods that go with {@link CbEntityFollowOwnerRestoredMixin}
 * (its restored 1.20.6 members and fields stay there, at {@code CbVanillaReads.PRIORITY}): the constructor sets the
 * restored fields, and tick / canUse / canContinueToUse take the restored members in place of 1.21's TamableAnimal calls
 * when a converted mod mixes them (CbMixedMembers). The routes wrap 1.21's call (MixinExtras @WrapOperation, which Fabric
 * Loader ships and initializes for every mod) instead of replacing it: unmixed, the call goes on to 1.21's
 * Operation. Priority CbVanillaReads.ROUTE_FIRST: the routes are the innermost wrappers of 1.21's calls, so a native
 * 1.21 mod's @WrapOperation of the same call wraps them and runs whether or not the route takes the restored path (at the
 * default priority the route came out outermost on a real 1.21 server and skipped the native wrapper while a converted
 * mod mixed the chain: cb-smx6 with cb-smx6v, s121c 2026-10-27); the constructor's field set then runs before other mods'
 * constructor RETURN injectors, which see the restored fields set.
 */
@Mixin(value = net.minecraft.class_1350.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbEntityFollowOwnerRestoredMixinHooks {

    private static final String TELEPORT = "net/minecraft/class_1350.method_23345()V";
    private static final String MAYBE = "net/minecraft/class_1350.method_23343(III)Z";
    private static final String CAN = "net/minecraft/class_1350.method_23344(Lnet/minecraft/class_2338;)Z";
    private static final String RANDOM = "net/minecraft/class_1350.method_23342(II)I";
    private static final String UNABLE = "net/minecraft/class_1350.method_49121()Z";

    /** 1.20.6's constructor set level from the animal; canFly as CbEntityFollowOwnerRestoredMixin's class note says */
    @Inject(method = "<init>(Lnet/minecraft/class_1321;DFF)V", at = @At("RETURN"))
    private void cb$v1_21$restoreFields(net.minecraft.class_1321 tamable, double speed, float start, float stop, CallbackInfo ci) {
        ((CbPrioritySplit.FollowOwner) (Object) this).cb$v1_21$constructed(tamable.method_37908(), tamable.method_60716());
    }

    /** tick's teleport: the restored 1.20.6 chain when a converted mod mixes it, else 1.21's call */
    @WrapOperation(method = "method_6268()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1321;method_60713()V"))
    private void cb$v1_21$teleport(net.minecraft.class_1321 animal, Operation<Void> original) {
        if (CbMixedMembers.mixed(TELEPORT) || CbMixedMembers.mixed(MAYBE) || CbMixedMembers.mixed(CAN) || CbMixedMembers.mixed(RANDOM)) {
            ((CbPrioritySplit.FollowOwner) (Object) this).cb$v1_21$teleportToOwner();
        } else {
            original.call(animal);
        }
    }

    /**
     * canUse / canContinueToUse: 1.21's unableToMoveToOwner is 1.20.6's unableToMove plus a spectating owner; when a converted
     * mod mixes the restored member, its answer stands for 1.20.6's part and 1.21's spectator test is kept
     */
    @WrapOperation(method = {"method_6264()Z", "method_6266()Z"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1321;method_60715()Z"))
    private boolean cb$v1_21$unable(net.minecraft.class_1321 animal, Operation<Boolean> original) {
        if (!CbMixedMembers.mixed(UNABLE)) {
            return original.call(animal);
        }
        net.minecraft.class_1309 owner = animal.method_35057();
        return ((CbPrioritySplit.FollowOwner) (Object) this).cb$v1_21$unableToMove() || owner != null && owner.method_7325();
    }
}
