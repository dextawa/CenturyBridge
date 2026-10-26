package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_10255;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's AbstractBoat.elasticRangeLeashBehaviour, restored with 1.21.5's body (javap): toward the holder, normalized
 * and scaled by distance - 6, added to the motion at 0.15 when already moving that way, else 0.2. 1.21.6 pulls boats
 * through checkElasticInteractions. Priority below every mod's.
 */
@Mixin(value = class_10255.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbLeashBoatRestoredMixin {

    @CbVanillaReads("CbLeashRouteMixin: 1.21.6's elastic pull for an entity whose class takes it from a converted mod's "
        + "override (whose super call lands here) or when a converted mod mixed it")
    public void method_61162(class_1297 holder, float distance) {
        class_1297 self = (class_1297) (Object) this;
        class_243 pull = holder.method_19538().method_1020(self.method_19538()).method_1029().method_1021((double) distance - 6.0);
        class_243 motion = self.method_18798();
        boolean same = motion.method_1026(pull) > 0.0;
        self.method_18799(motion.method_1019(pull.method_1021(same ? 0.15000000596046448 : 0.20000000298023224)));
    }
}
