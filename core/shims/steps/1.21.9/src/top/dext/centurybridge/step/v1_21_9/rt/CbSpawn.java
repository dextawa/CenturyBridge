package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_3218;
import net.minecraft.class_5217;
import net.minecraft.class_5269;

/**
 * 1.21.9's RespawnData (dimension, position, yaw, pitch) for 1.21.8's world spawn (position and angle).
 *
 * 1.21.8's LevelData answered getSpawnPos() / getSpawnAngle() and WritableLevelData took setSpawn(pos, angle);
 * Level answered the shared spawn clamped into its world border (getSharedSpawnPos / getSharedSpawnAngle) and
 * ServerLevel.setDefaultSpawnPos(pos, angle) set it. 1.21.9 keeps one RespawnData: LevelData.getRespawnData() /
 * WritableLevelData.setSpawn(RespawnData), Level.getRespawnData() / setRespawnData() (on a server level the
 * server's, which is what 1.21.8's shared overworld data was), and Level.clampToBorder (1.21.8's
 * getSharedSpawnPos clamp, the same code). Setting the spawn the 1.21.8 way changes the position and angle and
 * keeps what 1.21.8 had no notion of (the dimension, the pitch) as it was.
 *
 * Overrides: a 1.21.8 class answering getSpawnPos / getSpawnAngle (or Level's shared ones) answers
 * getRespawnData through the adapters below (adapterHooks): the old methods' values over what the class
 * answered without them (the guard's fall-through to super's getRespawnData); a class implementing LevelData
 * itself (no super to ask) places its spawn in the overworld, the only place 1.21.8's world spawn could be.
 */
public final class CbSpawn {

    private CbSpawn() {
    }

    // ------------------------------------------------------------------ calls

    /** LevelData.getSpawnPos() */
    public static class_2338 method_56126(class_5217 data) {
        return data.method_74893().method_74897();
    }

    /** LevelData.getSpawnAngle() */
    public static float method_30656(class_5217 data) {
        return data.method_74893().comp_4916();
    }

    /**
     * WritableLevelData.setSpawn(pos, angle): the dimension and pitch stay. 1.21.8's levels read the shared spawn
     * straight from the overworld's data; 1.21.9's server keeps a copy (MinecraftServer.respawnData, refreshed by
     * its own setRespawnData), so a set on that data refreshes the copy too -- without the spawn packet, which
     * 1.21.8's setSpawn did not send either
     */
    public static void method_187(class_5269 data, class_2338 pos, float angle) {
        class_5217.class_12064 cur = data.method_74893();
        data.method_187(class_5217.class_12064.method_74895(cur.method_74894(), pos, angle, cur.comp_4917()));
        net.minecraft.server.MinecraftServer s = CbProfiles.cbServer();
        if (s != null && s.method_27728() != null && s.method_27728().method_27859() == data && s.method_74944() != null) {
            s.method_74946();
        }
    }

    /** Level.getSharedSpawnPos(): the spawn clamped into this level's border (1.21.8's body is 1.21.9's clampToBorder) */
    public static class_2338 method_43126(class_1937 level) {
        return level.method_74891(level.method_74854()).method_74897();
    }

    /** Level.getSharedSpawnAngle() */
    public static float method_43127(class_1937 level) {
        return level.method_74854().comp_4916();
    }

    /** ServerLevel.setDefaultSpawnPos(pos, angle): the spawn in this level; the pitch stays when the dimension does */
    public static void method_8554(class_3218 level, class_2338 pos, float angle) {
        class_5217.class_12064 cur = level.method_74854();
        float pitch = cur != null && cur.method_74894() == level.method_27983() ? cur.comp_4917() : 0.0F;
        level.method_27873(class_5217.class_12064.method_74895(level.method_27983(), pos, angle, pitch));
    }

    // the same calls named through the vanilla classes declaring them (the redirect takes the receiver as named)

    public static class_2338 method_56126(net.minecraft.class_31 data) {
        return method_56126((class_5217) data);
    }

    public static float method_30656(net.minecraft.class_31 data) {
        return method_30656((class_5217) data);
    }

    public static void method_187(net.minecraft.class_31 data, class_2338 pos, float angle) {
        method_187((class_5269) data, pos, angle);
    }

    public static class_2338 method_56126(net.minecraft.class_5268 data) {
        return method_56126((class_5217) data);
    }

    public static float method_30656(net.minecraft.class_5268 data) {
        return method_30656((class_5217) data);
    }

    public static void method_187(net.minecraft.class_5268 data, class_2338 pos, float angle) {
        method_187((class_5269) data, pos, angle);
    }

    // ------------------------------------------------------------------ override bridges

    /** a 1.21.8 LevelData answering both getSpawnPos and getSpawnAngle */
    public interface LegacySpawn extends LegacySpawnPos, LegacySpawnAngle {
        @Override
        class_2338 method_56126();

        @Override
        float method_30656();
    }

    public interface LegacySpawnPos {
        class_2338 method_56126();
    }

    public interface LegacySpawnAngle {
        float method_30656();
    }

    private static final ClassValue<Boolean> VANILLA_DATA = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> c) {
            return vanillaConcrete(c, "method_74893");
        }
    };

    private static final ClassValue<Boolean> VANILLA_LEVEL = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> c) {
            return vanillaConcrete(c, "method_74854");
        }
    };

    /** whether a Minecraft superclass of c implements the no-argument method (what the guard's fall-through reaches) */
    private static boolean vanillaConcrete(Class<?> c, String name) {
        for (Class<?> k = c.getSuperclass(); k != null; k = k.getSuperclass()) {
            if (!k.getName().startsWith("net.minecraft.")) {
                continue;
            }
            for (java.lang.reflect.Method m : k.getDeclaredMethods()) {
                if (m.getName().equals(name) && m.getParameterCount() == 0) {
                    return !java.lang.reflect.Modifier.isAbstract(m.getModifiers());
                }
            }
        }
        return false;
    }

    /**
     * what the class answers without its old methods: a vanilla superclass's getRespawnData (the guard's re-entry
     * falls through to it); a class implementing LevelData itself has none: the overworld at the origin
     * (RespawnData.DEFAULT), the only dimension 1.21.8's world spawn lived in
     */
    private static class_5217.class_12064 base(Object self) {
        if (self instanceof class_5217 d && VANILLA_DATA.get(self.getClass())) {
            class_5217.class_12064 b = d.method_74893();
            if (b != null) {
                return b;
            }
        }
        return class_5217.class_12064.field_63048;
    }

    public static class_5217.class_12064 cbRespawnData(LegacySpawn self) {
        class_5217.class_12064 b = base(self);
        return class_5217.class_12064.method_74895(b.method_74894(), self.method_56126(), self.method_30656(), b.comp_4917());
    }

    public static class_5217.class_12064 cbRespawnDataPos(LegacySpawnPos self) {
        class_5217.class_12064 b = base(self);
        return class_5217.class_12064.method_74895(b.method_74894(), self.method_56126(), b.comp_4916(), b.comp_4917());
    }

    public static class_5217.class_12064 cbRespawnDataAngle(LegacySpawnAngle self) {
        class_5217.class_12064 b = base(self);
        return class_5217.class_12064.method_74895(b.method_74894(), b.method_74897(), self.method_30656(), b.comp_4917());
    }

    /** WritableLevelData.setSpawn(pos, angle) override, reached from setSpawn(RespawnData) */
    public interface LegacySetSpawn {
        void method_187(class_2338 pos, float angle);
    }

    public static void cbSetSpawn(LegacySetSpawn self, class_5217.class_12064 data) {
        self.method_187(data.method_74897(), data.comp_4916());
    }

    /** a 1.21.8 Level answering getSharedSpawnPos / getSharedSpawnAngle */
    public interface LegacySharedSpawn extends LegacySharedSpawnPos, LegacySharedSpawnAngle {
        @Override
        class_2338 method_43126();

        @Override
        float method_43127();
    }

    public interface LegacySharedSpawnPos {
        class_2338 method_43126();
    }

    public interface LegacySharedSpawnAngle {
        float method_43127();
    }

    /** a vanilla level's getRespawnData (guarded re-entry), else what a plain Level subclass answers (its level data's) */
    private static class_5217.class_12064 levelBase(Object self) {
        if (self instanceof class_1937 l) {
            class_5217.class_12064 b = VANILLA_LEVEL.get(self.getClass()) ? l.method_74854() : cbFillRespawnData(l);
            if (b != null) {
                return b;
            }
        }
        return class_5217.class_12064.field_63048;
    }

    public static class_5217.class_12064 cbLevelRespawnData(LegacySharedSpawn self) {
        class_5217.class_12064 b = levelBase(self);
        return class_5217.class_12064.method_74895(b.method_74894(), self.method_43126(), self.method_43127(), b.comp_4917());
    }

    public static class_5217.class_12064 cbLevelRespawnDataPos(LegacySharedSpawnPos self) {
        class_5217.class_12064 b = levelBase(self);
        return class_5217.class_12064.method_74895(b.method_74894(), self.method_43126(), b.comp_4916(), b.comp_4917());
    }

    public static class_5217.class_12064 cbLevelRespawnDataAngle(LegacySharedSpawnAngle self) {
        class_5217.class_12064 b = levelBase(self);
        return class_5217.class_12064.method_74895(b.method_74894(), b.method_74897(), self.method_43127(), b.comp_4917());
    }

    /** ServerLevel.setDefaultSpawnPos(pos, angle) override, reached from setRespawnData(RespawnData) */
    public interface LegacyDefaultSpawn {
        void method_8554(class_2338 pos, float angle);
    }

    public static void cbDefaultSpawn(LegacyDefaultSpawn self, class_5217.class_12064 data) {
        self.method_8554(data.method_74897(), data.comp_4916());
    }

    // ------------------------------------------------------------------ Level's new abstract members (abstractFills)

    /** a 1.21.8 Level subclass (neither server nor client level) answers its level data's spawn, as 1.21.8's did */
    public static class_5217.class_12064 cbFillRespawnData(class_1937 level) {
        return level.method_8401().method_74893();
    }

    /** a 1.21.8 Level subclass sets its level data's spawn when that data is writable, as 1.21.8's setDefaultSpawnPos path did */
    public static void cbFillSetRespawnData(class_1937 level, class_5217.class_12064 data) {
        if (level.method_8401() instanceof class_5269 w) {
            w.method_187(level.method_74891(data));
        }
    }
}
