package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1314;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's PathfinderMob.handleLeashAtDistance (restrictTo(holder's block, 5), answer true), restored with 1.21.5's body
 * (javap) over the restored Leashable default (CbLeashRestoredMixin): a 1.21.5 subclass's super call and a 1.21.5 mixin
 * on it behave as in 1.21.5. 1.21.6 does the same restriction in whenLeashedTo (leashElasticDistance - 1 = 5).
 * Priority below every mod's.
 */
@Mixin(value = class_1314.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbLeashPathfinderRestoredMixin {

    @CbVanillaReads("CbLeashRouteMixin: 1.21.6's tickLeash asks it for an entity whose class takes it from a converted mod's "
        + "override (whose super call lands here) or when a converted mod mixed it")
    public boolean method_60956(class_1297 holder, float distance) {
        // 1.21.5's call site as it was (owner PathfinderMob; 1.21.6 declares restrictTo on Mob), so a 1.21.5 mixin's
        // INVOKE anchor on it matches
        ((class_1314) (Object) this).method_18408(holder.method_24515(), 5);
        return true;
    }
}
