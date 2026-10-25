package cb.smixin3;

import java.util.function.Predicate;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import cb.smixin3.mixin.MobAccess;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.zombie.Zombie;

/**
 * A 26.1.2-era server mod for smixin3's guarded descend (mechanism o). GoalSelectorMixin injects into 26.1.2's
 * GoalSelector.removeGoal at its first read of availableGoals. 26.2's removeGoal calls removeAllGoals(goal -> ...),
 * which holds that read and which other code calls too; the converter moves the injector into removeAllGoals,
 * guarded so it acts only while removeGoal's call of it runs. Checks, once the server started:
 * removeGoal fires the handler once; removeAllGoals called directly (another caller) leaves it silent; both
 * remove the goal. The control build (cb-smixin3v: same driver, no GoalSelectorMixin, resource cb_smixin3_control)
 * expects no hits at all. Prints "CB-SMIXIN3 ok|FAIL <case> <detail>" and "CB-SMIXIN3 summary ok=N fail=M".
 */
public class Driver implements ModInitializer {
    public static volatile int hits;
    public static volatile int scaleHits;
    public static volatile int wraps;
    private static int ok;
    private static int fail;

    private static void check(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-SMIXIN3 " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    private static boolean has(GoalSelector gs, Goal g) {
        return gs.getAvailableGoals().stream().anyMatch(w -> w.getGoal() == g);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(Driver::run);
    }

    private static void run(MinecraftServer server) {
        boolean control = Driver.class.getResource("/cb_smixin3_control") != null;
        try {
            ServerLevel level = server.overworld();
            Zombie z = EntityType.ZOMBIE.create(level, EntitySpawnReason.COMMAND);
            GoalSelector gs = ((MobAccess) z).cbsmixin3$goals();
            Goal g = new FloatGoal(z);
            gs.addGoal(9, g);
            check("added", has(gs, g), "goal present after addGoal");
            hits = 0;
            wraps = 0;
            gs.removeGoal(g);
            check("removeGoal-removes", !has(gs, g), "goal gone after removeGoal");
            check("removeGoal-fires", hits == (control ? 0 : 1), "hits=" + hits + " want " + (control ? 0 : 1));
            check("removeGoal-wraps", wraps == (control ? 0 : 1), "wraps=" + wraps + " want " + (control ? 0 : 1));
            Goal g2 = new FloatGoal(z);
            gs.addGoal(9, g2);
            hits = 0;
            wraps = 0;
            Predicate<Goal> p = x -> x == g2;
            gs.removeAllGoals(p);
            check("removeAllGoals-removes", !has(gs, g2), "goal gone after removeAllGoals");
            check("removeAllGoals-silent", hits == 0, "hits=" + hits + " want 0 (another caller of the shared callee)");
            check("removeAllGoals-wrap-silent", wraps == 0, "wraps=" + wraps + " want 0 (the operation ran: goal removed)");
            // the guard is per call: removeGoal again after the direct call still fires once
            Goal g3 = new FloatGoal(z);
            gs.addGoal(9, g3);
            hits = 0;
            gs.removeGoal(g3);
            check("removeGoal-again", hits == (control ? 0 : 1) && !has(gs, g3), "hits=" + hits);
        } catch (Throwable t) {
            check("driver", false, t.toString());
            t.printStackTrace();
        }
        // stage 2: a static callee in another class (NoiseRouterData.peaksAndValleys -> TerrainProvider.peaksAndValleys)
        try {
            float base = -(Math.abs(Math.abs(0.2F) - 0.6666667F) - 0.33333334F) * 3.0F;
            scaleHits = 0;
            float viaTarget = net.minecraft.world.level.levelgen.NoiseRouterData.peaksAndValleys(0.2F);
            float want = control ? base : base * 2.0F;
            check("peaks-target", Math.abs(viaTarget - want) < 1e-6F && scaleHits == (control ? 0 : 1),
                "NoiseRouterData.peaksAndValleys(0.2)=" + viaTarget + " want " + want + " hits=" + scaleHits);
            scaleHits = 0;
            java.lang.reflect.Method tp = Class.forName("net.minecraft.data.worldgen.TerrainProvider")
                .getDeclaredMethod("peaksAndValleys", float.class);
            tp.setAccessible(true);
            float viaOther = (Float) tp.invoke(null, 0.2F);
            check("peaks-other-caller", Math.abs(viaOther - base) < 1e-6F && scaleHits == 0,
                "TerrainProvider.peaksAndValleys(0.2)=" + viaOther + " want " + base + " hits=" + scaleHits);
            scaleHits = 0;
            float again = net.minecraft.world.level.levelgen.NoiseRouterData.peaksAndValleys(0.2F);
            check("peaks-target-again", Math.abs(again - want) < 1e-6F && scaleHits == (control ? 0 : 1), "=" + again + " hits=" + scaleHits);
        } catch (Throwable t) {
            check("peaks", false, t.toString());
            t.printStackTrace();
        }
        System.out.println("CB-SMIXIN3 summary ok=" + ok + " fail=" + fail + (control ? " (control)" : ""));
    }
}
