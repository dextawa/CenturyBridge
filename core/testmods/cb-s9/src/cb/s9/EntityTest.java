package cb.s9;

import net.minecraft.class_1299;
import net.minecraft.class_1452;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_3730;
import net.minecraft.server.MinecraftServer;

/** 1.21.8 Entity.lerpMotion / getVisualRotationYInDegrees (calls and overrides) and ServerPlayer.loadGameTypes */
final class EntityTest {

    private EntityTest() {
    }

    /** a 1.21.8 entity class overriding both members */
    static final class OldPig extends class_1452 {
        double lx = Double.NaN, ly, lz;
        int visualCalls;

        OldPig(class_1299<? extends class_1452> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public void method_5750(double x, double y, double z) {
            lx = x;
            ly = y;
            lz = z;
            super.method_5750(x, y, z);
        }

        @Override
        public float method_43078() {
            visualCalls++;
            return super.method_43078() + 1.0F;
        }
    }

    static void run(MinecraftServer server) {
        S9TestMod.run("entity: lerpMotion / visual rotation / loadGameTypes", () -> {
            class_3218 level = server.method_30002();
            class_1452 pig = (class_1452) class_1299.field_6093.method_5883(level, class_3730.field_16462);
            pig.method_5750(1.0, 2.0, 3.0);
            class_243 v = pig.method_18798();
            S9TestMod.check(v.field_1352 == 1.0 && v.field_1351 == 2.0 && v.field_1350 == 3.0, "Entity.lerpMotion(1, 2, 3) sets the delta movement, got " + v);
            pig.field_6283 = 33.0F;
            S9TestMod.check(pig.method_43078() == 33.0F, "LivingEntity.getVisualRotationYInDegrees() is the body rotation, got " + pig.method_43078());

            OldPig old = new OldPig(class_1299.field_6093, level);
            old.method_5750(4.0, 5.0, 6.0);
            class_243 w = old.method_18798();
            S9TestMod.check(old.lx == 4.0 && old.ly == 5.0 && old.lz == 6.0, "a 1.21.8 lerpMotion(x, y, z) override is reached with the values, got " + old.lx + " " + old.ly + " " + old.lz);
            S9TestMod.check(w.field_1352 == 4.0 && w.field_1351 == 5.0 && w.field_1350 == 6.0, "its super.lerpMotion(x, y, z) sets the delta movement, got " + w);
            old.field_6283 = 10.0F;
            float r = old.method_43078();
            S9TestMod.check(old.visualCalls == 1 && r == 11.0F, "a 1.21.8 getVisualRotationYInDegrees override answers (super + 1), got " + r + " after " + old.visualCalls + " calls");

            net.fabricmc.fabric.api.entity.FakePlayer fp = net.fabricmc.fabric.api.entity.FakePlayer.get(level);
            fp.method_32748(null);
            S9TestMod.check(fp.field_13974.method_14257() == server.method_3790(), "ServerPlayer.loadGameTypes(null) gives the server's default game mode, got "
                + fp.field_13974.method_14257() + " (default " + server.method_3790() + ")");
        });
    }
}
