package top.dext.centurybridge.step.v1_20_5.rt;

import org.joml.Vector3f;

/**
 * 1.20.4's CrossbowAttackMob on 1.20.5's (area s4fin world/entity). 1.20.4's CrossbowItem handed each
 * projectile a crossbow mob shot to the mob's shootCrossbowProjectile(target, crossbow, projectile, angle),
 * whose vanilla implementations (Pillager, Piglin) aimed with the interface's default
 * shootCrossbowProjectile(user, target, projectile, angle, velocity) and getProjectileShotVector. 1.20.5's
 * CrossbowItem aims for every mob itself (the same computation). A 1.20.4 mob class that declares the
 * four-argument method is handed its projectiles again (CbEntityCrossbowItemMixin, adapter {@link Shooter});
 * calls of the three methods take 1.20.4's bodies, a class's own override of a default first.
 */
public final class CbEntityCrossbow {

    /** a 1.20.4 mob class declaring shootCrossbowProjectile(target, crossbow, projectile, angle) */
    public interface Shooter {
        void method_18811(net.minecraft.class_1309 target, net.minecraft.class_1799 crossbow, net.minecraft.class_1676 projectile, float angle);
    }

    /** a 1.20.4 mob class overriding the default shootCrossbowProjectile(user, target, projectile, angle, velocity) */
    public interface Aims {
        void method_24652(net.minecraft.class_1309 user, net.minecraft.class_1309 target, net.minecraft.class_1676 projectile, float angle, float velocity);
    }

    /** a 1.20.4 mob class overriding the default getProjectileShotVector(user, direction, angle) */
    public interface Vectors {
        Vector3f method_24653(net.minecraft.class_1309 user, net.minecraft.class_243 direction, float angle);
    }

    /** the crossbow of the shot being made (ProjectileWeaponItem.shoot, CbEntityCrossbowItemMixin) */
    private static final ThreadLocal<net.minecraft.class_1799> WEAPON = new ThreadLocal<>();

    public static void shooting(net.minecraft.class_1799 weapon) {
        WEAPON.set(weapon);
    }

    public static void shot() {
        WEAPON.remove();
    }

    public static net.minecraft.class_1799 weapon() {
        net.minecraft.class_1799 w = WEAPON.get();
        return w != null ? w : net.minecraft.class_1799.field_8037;
    }

    // ---------------------------------------------------------------- shootCrossbowProjectile(target, crossbow, projectile, angle)

    /** mob.shootCrossbowProjectile(target, crossbow, projectile, angle): the class's own, else Pillager's / Piglin's 1.20.4 body */
    public static void method_18811(net.minecraft.class_3745 mob, net.minecraft.class_1309 target, net.minecraft.class_1799 crossbow,
                                    net.minecraft.class_1676 projectile, float angle) {
        if (mob instanceof Shooter s) {
            s.method_18811(target, crossbow, projectile, angle);
        } else {
            superShoot(mob, target, crossbow, projectile, angle);
        }
    }

    public static void method_18811(net.minecraft.class_1604 mob, net.minecraft.class_1309 target, net.minecraft.class_1799 crossbow,
                                    net.minecraft.class_1676 projectile, float angle) {
        method_18811((net.minecraft.class_3745) mob, target, crossbow, projectile, angle);
    }

    public static void method_18811(net.minecraft.class_4836 mob, net.minecraft.class_1309 target, net.minecraft.class_1799 crossbow,
                                    net.minecraft.class_1676 projectile, float angle) {
        method_18811((net.minecraft.class_3745) mob, target, crossbow, projectile, angle);
    }

    /** Pillager's and Piglin's 1.20.4 body: aim with velocity 1.6 */
    public static void superShoot(net.minecraft.class_3745 mob, net.minecraft.class_1309 target, net.minecraft.class_1799 crossbow,
                                  net.minecraft.class_1676 projectile, float angle) {
        method_24652(mob, (net.minecraft.class_1309) mob, target, projectile, angle, 1.6F);
    }

    public static void superShoot(net.minecraft.class_1604 mob, net.minecraft.class_1309 target, net.minecraft.class_1799 crossbow,
                                  net.minecraft.class_1676 projectile, float angle) {
        superShoot((net.minecraft.class_3745) mob, target, crossbow, projectile, angle);
    }

    public static void superShoot(net.minecraft.class_4836 mob, net.minecraft.class_1309 target, net.minecraft.class_1799 crossbow,
                                  net.minecraft.class_1676 projectile, float angle) {
        superShoot((net.minecraft.class_3745) mob, target, crossbow, projectile, angle);
    }

    // ---------------------------------------------------------------- the defaults

    /** mob.shootCrossbowProjectile(user, target, projectile, angle, velocity): the class's override, else 1.20.4's default */
    public static void method_24652(net.minecraft.class_3745 mob, net.minecraft.class_1309 user, net.minecraft.class_1309 target,
                                    net.minecraft.class_1676 projectile, float angle, float velocity) {
        if (mob instanceof Aims a) {
            a.method_24652(user, target, projectile, angle, velocity);
        } else {
            superAim(mob, user, target, projectile, angle, velocity);
        }
    }

    /** CrossbowAttackMob.shootCrossbowProjectile(user, target, projectile, angle, velocity)'s 1.20.4 body */
    public static void superAim(net.minecraft.class_3745 mob, net.minecraft.class_1309 user, net.minecraft.class_1309 target,
                                net.minecraft.class_1676 projectile, float angle, float velocity) {
        double dx = target.method_23317() - user.method_23317();
        double dz = target.method_23321() - user.method_23321();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        double dy = target.method_23323(0.3333333333333333) - projectile.method_23318() + horizontal * 0.20000000298023224;
        Vector3f v = method_24653(mob, user, new net.minecraft.class_243(dx, dy, dz), angle);
        projectile.method_7485(v.x(), v.y(), v.z(), velocity, (float) (14 - user.method_37908().method_8407().method_5461() * 4));
        user.method_5783(net.minecraft.class_3417.field_15187, 1.0F, 1.0F / (user.method_6051().method_43057() * 0.4F + 0.8F));
    }

    /** mob.getProjectileShotVector(user, direction, angle): the class's override, else 1.20.4's default */
    public static Vector3f method_24653(net.minecraft.class_3745 mob, net.minecraft.class_1309 user, net.minecraft.class_243 direction, float angle) {
        return mob instanceof Vectors v ? v.method_24653(user, direction, angle) : superVector(mob, user, direction, angle);
    }

    /** CrossbowAttackMob.getProjectileShotVector(user, direction, angle)'s 1.20.4 body */
    public static Vector3f superVector(net.minecraft.class_3745 mob, net.minecraft.class_1309 user, net.minecraft.class_243 direction, float angle) {
        Vector3f dir = direction.method_46409().normalize();
        Vector3f axis = new Vector3f(dir).cross(new Vector3f(0.0F, 1.0F, 0.0F));
        if (axis.lengthSquared() <= 1.0E-7) {
            net.minecraft.class_243 up = user.method_18864(1.0F);
            axis = new Vector3f(dir).cross(up.method_46409());
        }
        Vector3f perpendicular = new Vector3f(dir).rotateAxis(1.5707964F, axis.x, axis.y, axis.z);
        return new Vector3f(dir).rotateAxis(angle * 0.017453292F, perpendicular.x, perpendicular.y, perpendicular.z);
    }

    private CbEntityCrossbow() {
    }
}
