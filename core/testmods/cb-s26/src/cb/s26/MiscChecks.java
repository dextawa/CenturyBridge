package cb.s26;

import java.lang.reflect.Method;
import net.minecraft.CrashReportCategory;
import net.minecraft.SystemReport;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;

/**
 * 26.1.2 members 26.2 dropped or reshaped, restored by the step layer (s26b misc family): SystemReport.setDetail(key,
 * Supplier), CrashReportCategory.trimStacktrace, GroundPathNavigation / Strider navigation hasValidPathType. Run once
 * the server has started (BounceChecks).
 */
final class MiscChecks {
    private MiscChecks() {}

    /** a 26.1.2 mod's navigation: calls and overrides the protected member, going to super */
    static final class ModNavigation extends GroundPathNavigation {
        int overrideCalls;

        ModNavigation(Mob mob, Level level) {
            super(mob, level);
        }

        @Override
        protected boolean hasValidPathType(PathType pathType) {
            overrideCalls++;
            return pathType == PathType.WATER || super.hasValidPathType(pathType);
        }

        boolean ask(PathType pathType) {
            return hasValidPathType(pathType);
        }
    }

    static void run(ServerLevel level) {
        SystemReport report = new SystemReport();
        report.setDetail("cb-s26 detail", () -> "value-1");
        S26TestMod.check("misc.SystemReport.setDetail supplier", report.toLineSeparatedString().contains("cb-s26 detail: value-1"),
            report.toLineSeparatedString());

        CrashReportCategory category = new CrashReportCategory("cb-s26");
        category.fillInStackTrace(0);
        int before = category.getStacktrace().length;
        category.trimStacktrace(1);
        S26TestMod.check("misc.CrashReportCategory.trimStacktrace", String.valueOf(category.getStacktrace().length),
            String.valueOf(before - 1));

        Strider strider = new Strider(EntityType.STRIDER, level);
        ModNavigation nav = new ModNavigation(strider, level);
        S26TestMod.check("misc.hasValidPathType WALKABLE (super)", String.valueOf(nav.ask(PathType.WALKABLE)), "true");
        S26TestMod.check("misc.hasValidPathType LAVA (super)", String.valueOf(nav.ask(PathType.LAVA)), "false");
        S26TestMod.check("misc.hasValidPathType OPEN (super)", String.valueOf(nav.ask(PathType.OPEN)), "false");
        S26TestMod.check("misc.hasValidPathType WATER (override)", String.valueOf(nav.ask(PathType.WATER)), "true");
        S26TestMod.check("misc.hasValidPathType override called", String.valueOf(nav.overrideCalls), "4");

        // Strider's own navigation (package-private class): LAVA / FIRE true, else GroundPathNavigation's answer
        try {
            Method m = GroundPathNavigation.class.getDeclaredMethod("hasValidPathType", PathType.class);
            m.setAccessible(true);
            Object striderNav = strider.getNavigation();
            S26TestMod.check("misc.Strider navigation LAVA", String.valueOf(m.invoke(striderNav, PathType.LAVA)), "true");
            S26TestMod.check("misc.Strider navigation FIRE", String.valueOf(m.invoke(striderNav, PathType.FIRE)), "true");
            S26TestMod.check("misc.Strider navigation WATER (super)", String.valueOf(m.invoke(striderNav, PathType.WATER)), "false");
            S26TestMod.check("misc.Strider navigation WALKABLE (super)", String.valueOf(m.invoke(striderNav, PathType.WALKABLE)), "true");
        } catch (ReflectiveOperationException e) {
            S26TestMod.check("misc.Strider navigation", false, e);
        }
    }
}
