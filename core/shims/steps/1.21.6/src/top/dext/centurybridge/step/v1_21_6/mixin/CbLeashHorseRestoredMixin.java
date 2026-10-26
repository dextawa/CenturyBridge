package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1496;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's AbstractHorse.handleLeashAtDistance (past 6 blocks an eating horse stops eating; answers true), restored with
 * 1.21.5's body (javap). 1.21.6 does the same in onElasticLeashPull. Priority below every mod's.
 */
@Mixin(value = class_1496.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbLeashHorseRestoredMixin {

    @CbVanillaReads("CbLeashRouteMixin: 1.21.6's tickLeash asks it for an entity whose class takes it from a converted mod's "
        + "override (whose super call lands here) or when a converted mod mixed it")
    public boolean method_60956(class_1297 holder, float distance) {
        class_1496 self = (class_1496) (Object) this;
        if (distance > 6.0F && self.method_6724()) {
            self.method_6740(false);
        }
        return true;
    }
}
