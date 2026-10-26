package cb.s9;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1452;
import net.minecraft.class_1677;
import net.minecraft.class_1680;
import net.minecraft.class_1937;
import net.minecraft.class_3218;
import net.minecraft.class_3730;
import net.minecraft.class_9109;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.8 Projectile.deflect(deflection, deflector, owner, fromAttack) (calls and an override) and
 * Projectile.onDeflection(deflector, fromAttack) (an override vanilla must reach, AbstractHurtingProjectile's
 * acceleration change through a super call), s9c.
 */
final class DeflectionTest {

    private DeflectionTest() {
    }

    /** a 1.21.8 projectile overriding onDeflection(Entity, boolean) */
    static final class OldSnowball extends class_1680 {
        class_1297 deflector;
        boolean fromAttack;
        int calls;

        OldSnowball(class_1299<? extends class_1680> type, class_1937 level) {
            super(type, level);
        }

        @Override
        protected void method_59525(class_1297 deflector, boolean fromAttack) {
            this.deflector = deflector;
            this.fromAttack = fromAttack;
            calls++;
            super.method_59525(deflector, fromAttack);
        }
    }

    /** a 1.21.8 projectile overriding deflect(.., Entity owner, ..) */
    static final class OldDeflectSnowball extends class_1680 {
        class_1297 owner;
        int calls;

        OldDeflectSnowball(class_1299<? extends class_1680> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public boolean method_59859(class_9109 deflection, class_1297 deflector, class_1297 owner, boolean fromAttack) {
            this.owner = owner;
            calls++;
            return super.method_59859(deflection, deflector, owner, fromAttack);
        }
    }

    /** a 1.21.8 small fireball overriding onDeflection and keeping AbstractHurtingProjectile's change by super */
    static final class OldFireball extends class_1677 {
        int calls;

        OldFireball(class_1299<? extends class_1677> type, class_1937 level) {
            super(type, level);
        }

        @Override
        protected void method_59525(class_1297 deflector, boolean fromAttack) {
            calls++;
            super.method_59525(deflector, fromAttack);
        }
    }

    static void run(MinecraftServer server) {
        S9TestMod.run("projectile deflection: deflect owner / onDeflection deflector", () -> {
            class_3218 level = server.method_30002();
            class_1452 deflector = (class_1452) class_1299.field_6093.method_5883(level, class_3730.field_16462);
            class_1452 owner = (class_1452) class_1299.field_6093.method_5883(level, class_3730.field_16462);

            class_1680 plain = new class_1680(class_1299.field_6068, level);
            boolean r = plain.method_59859(class_9109.field_48348, deflector, owner, false);
            S9TestMod.check(r && plain.method_24921() == owner, "Projectile.deflect(REVERSE, deflector, owner, false) answers true and sets the owner, got "
                + r + " " + plain.method_24921());

            OldSnowball old = new OldSnowball(class_1299.field_6068, level);
            old.method_59859(class_9109.field_48348, deflector, owner, true);
            S9TestMod.check(old.calls == 1 && old.deflector == deflector && old.fromAttack,
                "a 1.21.8 onDeflection(deflector, fromAttack) override is reached by deflect with deflect's deflector, got "
                    + old.calls + " calls, " + old.deflector + " " + old.fromAttack);
            S9TestMod.check(old.method_24921() == owner, "deflect set the owner before onDeflection, got " + old.method_24921());

            OldDeflectSnowball od = new OldDeflectSnowball(class_1299.field_6068, level);
            boolean r2 = od.method_59859(class_9109.field_48348, deflector, owner, false);
            S9TestMod.check(r2 && od.calls == 1 && od.owner == owner,
                "a 1.21.8 deflect override gets the owner entity, got " + od.calls + " calls, " + od.owner);
            S9TestMod.check(od.method_24921() == owner, "its super.deflect sets the owner, got " + od.method_24921());

            OldFireball fb = new OldFireball(class_1299.field_6049, level);
            fb.field_51893 = 0.7;
            fb.method_59859(class_9109.field_48348, deflector, owner, false);
            S9TestMod.check(fb.calls == 1 && fb.field_51893 == 0.35,
                "a 1.21.8 fireball onDeflection override's super call halves the acceleration (not from an attack), got "
                    + fb.calls + " calls, " + fb.field_51893);
            fb.method_59859(class_9109.field_48348, deflector, owner, true);
            S9TestMod.check(fb.calls == 2 && fb.field_51893 == 0.1,
                "from an attack it sets the acceleration to 0.1, got " + fb.calls + " calls, " + fb.field_51893);
        });
    }
}
