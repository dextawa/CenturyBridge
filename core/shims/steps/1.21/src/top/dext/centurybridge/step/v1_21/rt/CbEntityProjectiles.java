package top.dext.centurybridge.step.v1_21.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1668;
import net.minecraft.class_243;
import net.minecraft.class_9236;
import top.dext.centurybridge.step.v1_21.facade.class_9236$class_9237;
import top.dext.centurybridge.step.v1_21.mixin.CbEntityWindChargeInvoker;

/**
 * Projectiles that fly under their own power (AbstractHurtingProjectile: fireballs, the wither skull, the dragon
 * fireball, wind charges) and the wind charges' explosions. Wired from s6world.json.
 * <p>
 * 1.20.6 drove such a projectile with a power vector (xPower, yPower, zPower): its constructor set it to the
 * direction handed in, scaled to 0.1, and left the projectile at rest; each tick added the vector to the motion
 * and scaled the sum by the inertia; a deflection set the vector to the new motion times 0.1 (an attack) or 0.05
 * (a bounce); the save kept it ("power"). 1.21 dropped the vector: the constructor sets the motion to the
 * direction scaled to accelerationPower (0.1) and each tick adds the motion's own direction times
 * accelerationPower. A projectile made through a 1.20.6 constructor, or whose power a 1.20.6 mod writes, flies
 * 1.20.6's way (CbEntityHurtingProjectileMixin keeps the vector and applies it); any other flies 1.21's.
 */
public final class CbEntityProjectiles {

    /** CbEntityHurtingProjectileMixin's state: the 1.20.6 power vector, null while the projectile flies 1.21's way */
    public interface LegacyPower {
        class_243 cb$power();

        void cb$setPower(class_243 power);
    }

    // ------------------------------------------------------------------------------------------------ power
    /**
     * After a 1.20.6 constructor's 1.21 counterpart (given the same direction): 1.20.6's state -- the power vector
     * the direction made (assignPower: scaled to 0.1, none for a zero direction) and a projectile at rest.
     */
    public static void legacyPower(class_1668 projectile, double dx, double dy, double dz) {
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        class_243 power = length != 0.0 ? new class_243(dx / length * 0.1, dy / length * 0.1, dz / length * 0.1) : class_243.field_1353;
        ((LegacyPower) projectile).cb$setPower(power);
        projectile.method_18799(class_243.field_1353);
        projectile.field_6007 = false;
    }

    /** the same, for the 1.21 constructors that take the direction as a Vec3 */
    public static void legacyPower(class_1668 projectile, class_243 direction) {
        legacyPower(projectile, direction.field_1352, direction.field_1351, direction.field_1350);
    }

    /** a 1.20.6 constructor's direction, as the Vec3 1.21's takes */
    public static class_243 direction(double dx, double dy, double dz) {
        return new class_243(dx, dy, dz);
    }

    /**
     * The power vector as 1.20.6 code reads it: the projectile's own when it flies 1.20.6's way; otherwise the
     * per-tick push 1.21 gives it -- its motion's direction times accelerationPower.
     */
    public static class_243 power(class_1668 projectile) {
        class_243 power = ((LegacyPower) projectile).cb$power();
        return power != null ? power : projectile.method_18798().method_1029().method_1021(projectile.field_51893);
    }

    public static double xPower(class_1668 projectile) {
        return power(projectile).field_1352;
    }

    public static double yPower(class_1668 projectile) {
        return power(projectile).field_1351;
    }

    public static double zPower(class_1668 projectile) {
        return power(projectile).field_1350;
    }

    /** a 1.20.6 write of one component: from then on the projectile flies 1.20.6's way, from the vector it had */
    public static void setXPower(class_1668 projectile, double value) {
        class_243 p = power(projectile);
        ((LegacyPower) projectile).cb$setPower(new class_243(value, p.field_1351, p.field_1350));
    }

    public static void setYPower(class_1668 projectile, double value) {
        class_243 p = power(projectile);
        ((LegacyPower) projectile).cb$setPower(new class_243(p.field_1352, value, p.field_1350));
    }

    public static void setZPower(class_1668 projectile, double value) {
        class_243 p = power(projectile);
        ((LegacyPower) projectile).cb$setPower(new class_243(p.field_1352, p.field_1351, value));
    }

    // ------------------------------------------------------------------------------------------------ wind charges
    /** 1.20.6's AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR: a WindChargeDamageCalculator */
    private static final class_9236$class_9237 EXPLOSION_DAMAGE_CALCULATOR = new class_9236$class_9237();

    public static class_9236$class_9237 field_50137() {
        return EXPLOSION_DAMAGE_CALCULATOR;
    }

    /** 1.20.6 windCharge.explode(), protected: 1.21's explode(pos) at the charge's own position, where 1.20.6 exploded */
    public static void explode(class_9236 charge) {
        ((CbEntityWindChargeInvoker) charge).cb$explode(charge.method_19538());
    }

    /** explode(pos)'s position for a 1.20.6 override entered directly: the charge's own */
    public static class_243 position(class_1297 entity) {
        return entity.method_19538();
    }

    private CbEntityProjectiles() {
    }
}
