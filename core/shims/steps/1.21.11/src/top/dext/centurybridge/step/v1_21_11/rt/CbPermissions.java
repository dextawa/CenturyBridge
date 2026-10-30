package top.dext.centurybridge.step.v1_21_11.rt;

import net.minecraft.class_11560;
import net.minecraft.class_12086;
import net.minecraft.class_12087;
import net.minecraft.class_12094;
import net.minecraft.class_12096;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.10's int permission levels on 1.21.11's permission sets (javap -c -p of both code jars).
 *
 * <p>1.21.10 kept a command source's permission as an int compared with {@code >=} (CommandSourceStack.hasPermission,
 * method_9259: {@code permissionLevel >= n}); 1.21.11 keeps a PermissionSet (class_12096) and asks it for a Permission
 * (class_12087). The int levels 0..4 are 1.21.11's PermissionLevel ids (class_12094 ALL / MODERATORS / GAMEMASTERS /
 * ADMINS / OWNERS, byId clamps), a level-based set (class_12086.forLevel, method_75010) answers
 * Permission$HasCommandLevel(L) (class_12087$class_12089) with {@code own >= L} (class_12086.hasPermission), so the
 * level-based set of n answers exactly what 1.21.10's int n did. A set read back as an int is the highest level it
 * grants, which for a level-based set is its own level.
 */
public final class CbPermissions {
    private CbPermissions() {
    }

    /** an int level as the 1.21.11 level-based permission set */
    public static class_12096 permissions(int level) {
        return class_12086.method_75010(class_12094.method_75027(level));
    }

    /** whether a permission set grants 1.21.10's int level n: Permission$HasCommandLevel(byId(n)) */
    public static boolean grants(class_12096 set, int level) {
        return set.hasPermission(new class_12087.class_12089(class_12094.method_75027(level)));
    }

    /** a permission set as 1.21.10's int level: the highest level it grants (0 when it grants none) */
    public static int level(class_12096 set) {
        if (set instanceof class_12086 levelBased) {
            return levelBased.method_75009().method_75026();
        }
        for (int l = 4; l > 0; l--) {
            if (grants(set, l)) {
                return l;
            }
        }
        return 0;
    }

    /** CommandSourceStack.hasPermission(int) (1.21.10 method_9259): the source's permission set asked for the level */
    public static boolean method_9259(class_2168 source, int level) {
        return grants(source.method_75037(), level);
    }

    /** MinecraftServer.getOperatorUserPermissionLevel() (1.21.10 method_3798); 1.21.11 answers the set (method_74994) */
    public static int method_3798(MinecraftServer server) {
        return level(server.method_74994());
    }

    /** MinecraftServer.getFunctionCompilationLevel() (1.21.10 method_21714); 1.21.11 answers the set (method_74995) */
    public static int method_21714(MinecraftServer server) {
        return level(server.method_74995());
    }

    /**
     * CommandSourceStack.withMaximumPermission(int) (method_9230). 1.21.10: the source itself when its level is at least
     * n, else the source at level n (max(own, n)). Not 1.21.11's withMaximumPermission(set): it merges through
     * PermissionSet.union (method_75034), and for two level-based sets class_12086's union answers the lower level
     * (javap: "own.level >= other.level ? other : own"; a real 1.21.11 boot, cb-smx1p, confirmed it). A set that is not
     * level-based keeps its other permissions through 1.21.11's union, which for such sets is a true union (class_12340).
     */
    public static class_2168 method_9230(class_2168 source, int level) {
        class_12096 own = source.method_75037();
        if (grants(own, level)) {
            return source;
        }
        if (own instanceof class_12086) {
            return source.method_9206(permissions(level));
        }
        return source.method_9230(permissions(level));
    }

    /** MinecraftServer.getProfilePermissions(NameAndId) (method_3835): 1.21.11 answers the level-based set */
    public static int method_3835(MinecraftServer server, class_11560 profile) {
        return level(server.method_3835(profile));
    }
}
