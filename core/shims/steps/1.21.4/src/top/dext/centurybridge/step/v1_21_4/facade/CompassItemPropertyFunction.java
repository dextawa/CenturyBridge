package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 CompassItemPropertyFunction (class_7391, gone in 1.21.4): the needle angle of a compass pointing at a
 * {@link CompassTarget}, wobbling towards it, spinning at random without a target in the holder's dimension.
 * 1.21.3's code, member for member.
 */
@Environment(EnvType.CLIENT)
public class CompassItemPropertyFunction implements ClampedItemPropertyFunction {

    /** DEFAULT_ROTATION */
    public static final int field_38798 = 0;
    /** wobble */
    public final CompassWobble field_24449 = new CompassWobble();
    /** wobbleRandom */
    public final CompassWobble field_24450 = new CompassWobble();
    /** compassTarget */
    public final CompassTarget field_38799;

    public CompassItemPropertyFunction(CompassTarget target) {
        this.field_38799 = target;
    }

    @Override
    public float unclampedCall(net.minecraft.class_1799 stack, net.minecraft.class_638 level, net.minecraft.class_1309 living, int seed) {
        net.minecraft.class_1297 entity = living != null ? living : stack.method_27319();
        if (entity == null) {
            return 0.0F;
        }
        level = this.method_43215(entity, level);
        return level == null ? 0.0F : this.method_43218(stack, level, seed, entity);
    }

    /** getCompassRotation */
    public float method_43218(net.minecraft.class_1799 stack, net.minecraft.class_638 level, int seed, net.minecraft.class_1297 entity) {
        net.minecraft.class_4208 pos = this.field_38799.getPos(level, stack, entity);
        long time = level.method_8510();
        return !this.method_43217(entity, pos) ? this.method_43212(seed, time) : this.method_43214(entity, time, pos.comp_2208());
    }

    /** getRandomlySpinningRotation */
    public float method_43212(int seed, long time) {
        if (this.field_24450.method_27316(time)) {
            this.field_24450.method_27190(time, Math.random());
        }
        double rot = this.field_24450.field_23980 + (double) ((float) this.method_32800(seed) / 2.14748365E9F);
        return net.minecraft.class_3532.method_15341((float) rot, 1.0F);
    }

    /** getRotationTowardsCompassTarget */
    public float method_43214(net.minecraft.class_1297 entity, long time, net.minecraft.class_2338 target) {
        double angle = this.method_43216(entity, target);
        double facing = this.method_43213(entity);
        double rot;
        if (entity instanceof net.minecraft.class_1657 player && player.method_7340() && player.method_37908().method_54719().method_54751()) {
            if (this.field_24449.method_27316(time)) {
                this.field_24449.method_27190(time, 0.5 - (facing - 0.25));
            }
            rot = angle + this.field_24449.field_23980;
        } else {
            rot = 0.5 - (facing - 0.25 - angle);
        }
        return net.minecraft.class_3532.method_15341((float) rot, 1.0F);
    }

    /** tryFetchLevelIfMissing */
    public net.minecraft.class_638 method_43215(net.minecraft.class_1297 entity, net.minecraft.class_638 level) {
        return level == null && entity.method_37908() instanceof net.minecraft.class_638 cl ? cl : level;
    }

    /** isValidCompassTargetPos */
    public boolean method_43217(net.minecraft.class_1297 entity, net.minecraft.class_4208 pos) {
        return pos != null && pos.comp_2207() == entity.method_37908().method_27983()
            && !(pos.comp_2208().method_19770(entity.method_19538()) < 9.999999747378752E-6);
    }

    /** getAngleFromEntityToPos */
    public double method_43216(net.minecraft.class_1297 entity, net.minecraft.class_2338 pos) {
        net.minecraft.class_243 c = net.minecraft.class_243.method_24953(pos);
        return Math.atan2(c.method_10215() - entity.method_23321(), c.method_10216() - entity.method_23317()) / 6.2831854820251465;
    }

    /** getWrappedVisualRotationY */
    public double method_43213(net.minecraft.class_1297 entity) {
        return net.minecraft.class_3532.method_15367((double) (entity.method_43078() / 360.0F), 1.0);
    }

    /** hash */
    public int method_32800(int seed) {
        return seed * 1327217883;
    }

    /** CompassTarget (class_7391$class_7392) */
    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface CompassTarget {
        net.minecraft.class_4208 getPos(net.minecraft.class_638 level, net.minecraft.class_1799 stack, net.minecraft.class_1297 entity);
    }

    /** CompassWobble (class_7391$class_5171) */
    @Environment(EnvType.CLIENT)
    public static class CompassWobble {
        /** rotation */
        public double field_23980;
        /** deltaRotation */
        public double field_23981;
        /** lastUpdateTick */
        public long field_23982;

        public CompassWobble() {
        }

        /** shouldUpdate */
        public boolean method_27316(long time) {
            return this.field_23982 != time;
        }

        /** update */
        public void method_27190(long time, double target) {
            this.field_23982 = time;
            double d = target - this.field_23980;
            d = net.minecraft.class_3532.method_15367(d + 0.5, 1.0) - 0.5;
            this.field_23981 += d * 0.1;
            this.field_23981 *= 0.8;
            this.field_23980 = net.minecraft.class_3532.method_15367(this.field_23980 + this.field_23981, 1.0);
        }
    }
}
