package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Arrays;
import java.util.List;

/**
 * 1.20.2 server API (MinecraftServer, ServerPlayer, ServerLevel) on 1.20.3.
 */
public final class CbServer {

    /** was MinecraftServer.MS_PER_TICK (field_33206), a constant 1.20.3 dropped with the tick-rate rework */
    public static final int field_33206 = 50;

    /**
     * was MinecraftServer.tickTimes (field_4573): the same ring of 100 per-tick durations in nanoseconds,
     * a private field behind getTickTimesNanos() since 1.20.3 -- the live array, as the field read gave.
     */
    public static long[] field_4573(net.minecraft.server.MinecraftServer server) {
        return server.method_54835();
    }

    /**
     * was MinecraftServer.getAverageTickTime() (method_3830): the smoothed milliseconds per tick (0.8 old
     * + 0.2 new), which 1.20.3 keeps under getCurrentSmoothedTickTime(). Virtual: a mod server class
     * overriding the old name answers through the override hook that reconnects it to the new name.
     */
    public static float method_3830(net.minecraft.server.MinecraftServer server) {
        return server.method_54832();
    }

    /** ServerPlayer.awardRecipesByKey took an array in 1.20.2 and takes a List of the same ids since 1.20.3 */
    public static List<net.minecraft.class_2960> cbRecipeIds(net.minecraft.class_2960[] ids) {
        return Arrays.asList(ids);
    }

    /** the other direction, for a mod's old-shape override that vanilla now calls with a List */
    public static net.minecraft.class_2960[] cbRecipeIdArray(List<net.minecraft.class_2960> ids) {
        return ids.toArray(new net.minecraft.class_2960[0]);
    }

    /**
     * was ServerLevel.explode(Entity, DamageSource, ExplosionDamageCalculator, double, double, double,
     * float, boolean, ExplosionInteraction) (method_8454, 9 arguments): 1.20.3 keeps that shape on Level
     * under another name (method_55117), which hands the explosion to the 12-argument explode with
     * vanilla's default particles and sound -- what 1.20.2's ServerLevel override showed its players.
     */
    public static net.minecraft.class_1927 method_8454(net.minecraft.class_3218 level, net.minecraft.class_1297 source,
            net.minecraft.class_1282 damage, net.minecraft.class_5362 calculator, double x, double y, double z,
            float radius, boolean fire, net.minecraft.class_1937.class_7867 interaction) {
        return level.method_55117(source, damage, calculator, x, y, z, radius, fire, interaction);
    }

    private CbServer() {
    }
}
