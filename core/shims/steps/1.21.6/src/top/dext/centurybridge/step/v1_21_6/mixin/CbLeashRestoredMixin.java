package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_9817;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbLeash;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's Leashable.handleLeashAtDistance(holder, distance) (answers true) and elasticRangeLeashBehaviour(holder,
 * distance) (adds to the entity's motion, per axis, 0.4 x the squared distance per block along that axis, signed;
 * 1.21.5's private static helper inlined), restored as default methods of Leashable with 1.21.5's bodies (javap): a
 * 1.21.5 call or super call links to them, a 1.21.5 entity overrides them, a 1.21.5 mixin anchors in them.
 */
@Mixin(class_9817.class)
public interface CbLeashRestoredMixin extends CbLeash.Legacy {

    @CbVanillaReads("CbLeashRouteMixin: 1.21.6's tickLeash asks it after whenLeashedTo, with 1.21.5's distance, for an entity "
        + "whose class takes it from a converted mod or whose declaration a converted mod mixed; false ends that tick")
    @Override
    default boolean method_60956(class_1297 holder, float distance) {
        return true;
    }

    @CbVanillaReads("CbLeashRouteMixin: 1.21.6's elastic pull for an entity whose class takes it from a converted mod or whose "
        + "declaration a converted mod mixed: the motion goes back to what it was before checkElasticInteractions and this "
        + "pull applies instead, before 1.21.6's onElasticLeashPull")
    @Override
    default void method_61162(class_1297 holder, float distance) {
        class_1297 self = (class_1297) this;
        double dx = (holder.method_23317() - self.method_23317()) / (double) distance;
        double dy = (holder.method_23318() - self.method_23318()) / (double) distance;
        double dz = (holder.method_23321() - self.method_23321()) / (double) distance;
        class_243 m = self.method_18798();
        self.method_18799(m.method_1031(Math.copySign(dx * dx * 0.4, dx), Math.copySign(dy * dy * 0.4, dy), Math.copySign(dz * dz * 0.4, dz)));
    }
}
