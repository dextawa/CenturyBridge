package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_846;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_6.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21_6.rt.CbPrioritySplit;

/**
 * The injector into 1.21.6's own RenderSection.hasAllNeighbors that goes with {@link CbSectionDistanceRestoredMixin}
 * (its restored getDistToPlayerSqr stays there, at {@code CbVanillaReads.PRIORITY}): when a converted mod mixed
 * getDistToPlayerSqr (CbMixedMembers), hasAllNeighbors runs whole and at its return asks the restored member
 * (CbPrioritySplit.SectionDistance, virtually) and answers true within 24 blocks, 1.21.5's answer. Unmixed: exactly 1.21.6.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(class_846.class_851.class)
public abstract class CbSectionDistanceRestoredMixinHooks {

    private static final String CB$KEY = "net/minecraft/class_846$class_851.method_3668()D";

    @Inject(method = "method_3673()Z", at = @At("RETURN"), cancellable = true)
    private void cb$closeSectionsHaveNeighbours(CallbackInfoReturnable<Boolean> cir) {
        if (CbMixedMembers.mixed(CB$KEY)) {
            double distance = ((CbPrioritySplit.SectionDistance) (Object) this).cb$v1_21_6$distToPlayerSqr();
            if (!cir.getReturnValueZ() && distance <= 576.0) {
                cir.setReturnValue(true);
            }
        }
    }
}
