package top.dext.centurybridge.step.v26_1.rt;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.timers.TimerQueue;

/**
 * 1.21.11 PrimaryLevelData's private fields (class_31, opened by access wideners) on 26.1, which moved what they held
 * out of the level data (javap 26.1 PrimaryLevelData: settings, respawn data, game time and the brand sets remain).
 * Each read and write goes where 26.1 keeps the value, through the members the layer gives 1.21.11's getters and
 * setters back with (CbWallLevelData / CbWallWorldData): weather into WeatherData, day time into the overworld clock,
 * the wandering trader into WanderingTraderData (its id into CbWallTraderId), scheduled events and custom boss events
 * onto the server. difficultyLocked stays on the data (26.1 keeps it in LevelSettings.DifficultySettings, read and
 * written through PrimaryLevelData.isDifficultyLocked / setDifficultyLocked).
 */
public final class CbWallPrimaryLevelFields {
    private CbWallPrimaryLevelFields() {
    }

    /** field_25425 worldOptions */
    public static WorldOptions worldOptions(PrimaryLevelData d) {
        return CbWallLevelData.worldGenOptions(d);
    }

    /** field_198 dayTime */
    public static long dayTime(PrimaryLevelData d) {
        return CbWallLevelData.getDayTime(d);
    }

    public static void setDayTime(PrimaryLevelData d, long v) {
        CbWallLevelData.setDayTime(d, v);
    }

    /** field_170 loadedPlayerTag (final in 1.21.11) */
    public static CompoundTag loadedPlayerTag(PrimaryLevelData d) {
        return CbWallWorldData.getLoadedPlayerTag(d);
    }

    /** field_176 clearWeatherTime */
    public static int clearWeatherTime(PrimaryLevelData d) {
        return CbWallLevelData.getClearWeatherTime(d);
    }

    public static void setClearWeatherTime(PrimaryLevelData d, int v) {
        CbWallLevelData.setClearWeatherTime(d, v);
    }

    /** field_190 raining */
    public static boolean raining(PrimaryLevelData d) {
        return CbWallLevelData.isRaining(d);
    }

    public static void setRaining(PrimaryLevelData d, boolean v) {
        CbWallLevelData.setRaining(d, v);
    }

    /** field_192 rainTime */
    public static int rainTime(PrimaryLevelData d) {
        return CbWallLevelData.getRainTime(d);
    }

    public static void setRainTime(PrimaryLevelData d, int v) {
        CbWallLevelData.setRainTime(d, v);
    }

    /** field_168 thundering */
    public static boolean thundering(PrimaryLevelData d) {
        return CbWallLevelData.isThundering(d);
    }

    public static void setThundering(PrimaryLevelData d, boolean v) {
        CbWallLevelData.setThundering(d, v);
    }

    /** field_173 thunderTime */
    public static int thunderTime(PrimaryLevelData d) {
        return CbWallLevelData.getThunderTime(d);
    }

    public static void setThunderTime(PrimaryLevelData d, int v) {
        CbWallLevelData.setThunderTime(d, v);
    }

    /** field_157 difficultyLocked */
    public static boolean difficultyLocked(PrimaryLevelData d) {
        return d.isDifficultyLocked();
    }

    public static void setDifficultyLocked(PrimaryLevelData d, boolean v) {
        d.setDifficultyLocked(v);
    }

    /** field_24193 legacyWorldBorderSettings */
    public static Optional<WorldBorder.Settings> legacyWorldBorderSettings(PrimaryLevelData d) {
        return CbWallLevelData.getLegacyWorldBorderSettings(d);
    }

    public static void setLegacyWorldBorderSettings(PrimaryLevelData d, Optional<WorldBorder.Settings> v) {
        CbWallLevelData.setLegacyWorldBorderSettings(d, v);
    }

    /** field_156 customBossEvents */
    public static CompoundTag customBossEvents(PrimaryLevelData d) {
        return CbWallWorldData.getCustomBossEvents(d);
    }

    public static void setCustomBossEvents(PrimaryLevelData d, CompoundTag v) {
        CbWallWorldData.setCustomBossEvents(d, v);
    }

    /** field_17736 wanderingTraderSpawnDelay */
    public static int wanderingTraderSpawnDelay(PrimaryLevelData d) {
        return CbWallLevelData.getWanderingTraderSpawnDelay(d);
    }

    public static void setWanderingTraderSpawnDelay(PrimaryLevelData d, int v) {
        CbWallLevelData.setWanderingTraderSpawnDelay(d, v);
    }

    /** field_17737 wanderingTraderSpawnChance */
    public static int wanderingTraderSpawnChance(PrimaryLevelData d) {
        return CbWallLevelData.getWanderingTraderSpawnChance(d);
    }

    public static void setWanderingTraderSpawnChance(PrimaryLevelData d, int v) {
        CbWallLevelData.setWanderingTraderSpawnChance(d, v);
    }

    /** field_17738 wanderingTraderId */
    public static UUID wanderingTraderId(PrimaryLevelData d) {
        return CbWallLevelData.getWanderingTraderId(d);
    }

    public static void setWanderingTraderId(PrimaryLevelData d, UUID v) {
        CbWallLevelData.setWanderingTraderId(d, v);
    }

    /** field_191 scheduledEvents (final in 1.21.11) */
    public static TimerQueue<MinecraftServer> scheduledEvents(PrimaryLevelData d) {
        return CbWallLevelData.getScheduledEvents(d);
    }
}
