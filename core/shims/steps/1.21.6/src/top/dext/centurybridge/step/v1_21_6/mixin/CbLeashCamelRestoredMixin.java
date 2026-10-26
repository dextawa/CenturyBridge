package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_7689;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's Camel.handleLeashAtDistance (past 6 blocks a camel that is sitting, not in the pose transition and can stand
 * up stands up; answers true; no super call), restored with 1.21.5's body (javap). 1.21.6 does the same in
 * onElasticLeashPull. Priority below every mod's.
 */
@Mixin(value = class_7689.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbLeashCamelRestoredMixin {

    @CbVanillaReads("CbLeashRouteMixin: 1.21.6's tickLeash asks it for an entity whose class takes it from a converted mod's "
        + "override (whose super call lands here) or when a converted mod mixed it")
    public boolean method_60956(class_1297 holder, float distance) {
        class_7689 self = (class_7689) (Object) this;
        if (distance > 6.0F && self.method_45350() && !self.method_45351() && self.method_52548()) {
            self.method_45353();
        }
        return true;
    }
}
