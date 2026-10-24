package top.dext.centurybridge.step.v26_1.rt;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.saveddata.WanderingTraderData;
import net.minecraft.world.level.saveddata.WeatherData;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.timers.TimerQueue;

/**
 * 1.21.11's LevelData / ServerLevelData weather, day time, game rules, scheduled events and wandering
 * trader members on 26.1, which moved them out of the level data:
 * <ul>
 * <li>weather (raining, thundering and their timers) into the server's WeatherData saved data;</li>
 * <li>day time into the world clocks: 1.21.11's day time was the overworld's, shared by every dimension
 *     (DerivedLevelData answered the primary data's), which is 26.1's overworld clock;</li>
 * <li>game rules and scheduled events onto the server, the wandering trader's spawn delay and chance into
 *     the WanderingTraderData saved data.</li>
 * </ul>
 * 1.21.11 kept one primary level data per server, which every dimension's data read through; 26.1 keeps one
 * of each of the above per server. A server-side level data therefore answers from the running server
 * ({@link #SERVER}, set when a server is constructed and cleared when it stops: one per process, as
 * vanilla runs them). Client level data answer from the client level (CbWallLevelDataClient).
 */
public final class CbWallLevelData {

    private static final boolean CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    /** the running server (CbWallServerMixin) */
    public static volatile MinecraftServer SERVER;

    private CbWallLevelData() {
    }

    private static MinecraftServer server(LevelData data) {
        MinecraftServer s = SERVER;
        if (s == null) {
            throw new IllegalStateException(data + ": 1.21.11 server level data read with no server running");
        }
        return s;
    }

    private static WeatherData weather(LevelData data) {
        return server(data).getWeatherData();
    }

    /**
     * a dimension's DerivedLevelData: 1.21.11's answered the primary data's values but ignored every write to
     * them (its setters were empty) and had no wandering trader of its own (0 / null)
     */
    private static boolean derived(LevelData data) {
        return data instanceof DerivedLevelData;
    }

    private static boolean client(LevelData data) {
        return CLIENT && !(data instanceof ServerLevelData) && CbWallLevelDataClient.isClient(data);
    }

    // ---- LevelData ----

    /** 1.21.11 LevelData.isRaining() */
    public static boolean isRaining(LevelData data) {
        return client(data) ? CbWallLevelDataClient.isRaining(data) : weather(data).isRaining();
    }

    /** 1.21.11 LevelData.setRaining(boolean) (WritableLevelData's in 1.21.11; the client set it from the weather packets) */
    public static void setRaining(LevelData data, boolean raining) {
        if (client(data)) {
            CbWallLevelDataClient.setRaining(data, raining);
        } else if (!derived(data)) {
            weather(data).setRaining(raining);
        }
    }

    /** 1.21.11 LevelData.isThundering(): the client's level data answered false, the server's its weather */
    public static boolean isThundering(LevelData data) {
        return !client(data) && weather(data).isThundering();
    }

    /** 1.21.11 LevelData.getDayTime(): the overworld's time, as every 1.21.11 level data answered */
    public static long getDayTime(LevelData data) {
        if (client(data)) {
            return CbWallLevelDataClient.getDayTime(data);
        }
        return server(data).overworld().getOverworldClockTime();
    }

    // ---- WorldData ----

    /**
     * 1.21.11 WorldData.worldGenOptions() (seed, structures, bonus chest): 26.1 keeps them in the server's
     * WorldGenSettings saved data. Answered for the running server's own world data; 1.21.11's primary
     * level data carried them from before a server existed (world creation), which 26.1's no longer does.
     */
    public static WorldOptions worldGenOptions(WorldData data) {
        WorldOptions built = CbWallWorldData.options(data);
        if (built != null) {
            return built; // a level data a 1.21.11 constructor or parse built with these options
        }
        MinecraftServer s = SERVER;
        if (s == null || s.getWorldData() != data) {
            throw new IllegalStateException(data + ": 1.21.11 world data of no running server has no world generation options in 26.1");
        }
        return s.getWorldGenSettings().options();
    }

    /**
     * 1.21.11 WorldData.getGameRules(): the primary level data's game rules, which 26.1 keeps on the server.
     * Answered for the running server's own world data (as {@link #worldGenOptions}).
     */
    public static GameRules getGameRules(WorldData data) {
        MinecraftServer s = SERVER;
        if (s == null || s.getWorldData() != data) {
            throw new IllegalStateException(data + ": 1.21.11 world data of no running server has no game rules in 26.1");
        }
        return s.getGameRules();
    }

    /** 1.21.11 PrimaryLevelData.WORLD_GEN_SETTINGS (the level.dat key 1.21.11 wrote them under) */
    public static String field_31843() {
        return "WorldGenSettings";
    }

    /** 1.21.11 PrimaryLevelData.PLAYER (the level.dat key of the single player's data) */
    public static String field_36349() {
        return "Player";
    }

    // ---- ServerLevelData ----

    /** 1.21.11 ServerLevelData.setDayTime(long) */
    public static void setDayTime(ServerLevelData data, long time) {
        if (!derived(data)) {
            setOverworldTime(server(data), time);
        }
    }

    /** the overworld clock's time: 1.21.11's day time, shared by every level */
    public static void setOverworldTime(MinecraftServer s, long time) {
        Holder<WorldClock> clock = s.registryAccess().get(WorldClocks.OVERWORLD).orElseThrow();
        s.clockManager().setTotalTicks(clock, time);
    }

    /** 1.21.11 ServerLevelData.getClearWeatherTime() */
    public static int getClearWeatherTime(ServerLevelData data) {
        return weather(data).getClearWeatherTime();
    }

    /** 1.21.11 ServerLevelData.setClearWeatherTime(int) */
    public static void setClearWeatherTime(ServerLevelData data, int time) {
        if (!derived(data)) {
            weather(data).setClearWeatherTime(time);
        }
    }

    /** 1.21.11 ServerLevelData.setThundering(boolean) */
    public static void setThundering(ServerLevelData data, boolean thundering) {
        if (!derived(data)) {
            weather(data).setThundering(thundering);
        }
    }

    /** 1.21.11 ServerLevelData.getThunderTime() */
    public static int getThunderTime(ServerLevelData data) {
        return weather(data).getThunderTime();
    }

    /** 1.21.11 ServerLevelData.setThunderTime(int) */
    public static void setThunderTime(ServerLevelData data, int time) {
        if (!derived(data)) {
            weather(data).setThunderTime(time);
        }
    }

    /** 1.21.11 ServerLevelData.getRainTime() */
    public static int getRainTime(ServerLevelData data) {
        return weather(data).getRainTime();
    }

    /** 1.21.11 ServerLevelData.setRainTime(int) */
    public static void setRainTime(ServerLevelData data, int time) {
        if (!derived(data)) {
            weather(data).setRainTime(time);
        }
    }

    /** 1.21.11 ServerLevelData.getGameRules() */
    public static GameRules getGameRules(ServerLevelData data) {
        return server(data).getGameRules();
    }

    /** 1.21.11 ServerLevelData.getScheduledEvents() */
    public static TimerQueue<MinecraftServer> getScheduledEvents(ServerLevelData data) {
        return server(data).getScheduledEvents();
    }

    private static WanderingTraderData trader(ServerLevelData data) {
        return server(data).getDataStorage().computeIfAbsent(WanderingTraderData.TYPE);
    }

    /** 1.21.11 ServerLevelData.getWanderingTraderSpawnDelay() */
    public static int getWanderingTraderSpawnDelay(ServerLevelData data) {
        return derived(data) ? 0 : trader(data).spawnDelay();
    }

    /** 1.21.11 ServerLevelData.setWanderingTraderSpawnDelay(int) */
    public static void setWanderingTraderSpawnDelay(ServerLevelData data, int delay) {
        if (!derived(data)) {
            trader(data).setSpawnDelay(delay);
        }
    }

    /** 1.21.11 ServerLevelData.getWanderingTraderSpawnChance() */
    public static int getWanderingTraderSpawnChance(ServerLevelData data) {
        return derived(data) ? 0 : trader(data).spawnChance();
    }

    /** 1.21.11 ServerLevelData.setWanderingTraderSpawnChance(int) */
    public static void setWanderingTraderSpawnChance(ServerLevelData data, int chance) {
        if (!derived(data)) {
            trader(data).setSpawnChance(chance);
        }
    }

    /** 1.21.11 ServerLevelData.getWanderingTraderId(): the last wandering trader the server spawned (rt/CbWallTraderId) */
    public static UUID getWanderingTraderId(ServerLevelData data) {
        return derived(data) ? null : CbWallTraderId.of(server(data)).id();
    }

    /** 1.21.11 ServerLevelData.setWanderingTraderId(id) */
    public static void setWanderingTraderId(ServerLevelData data, UUID id) {
        if (!derived(data)) {
            CbWallTraderId.of(server(data)).setId(id);
        }
    }

    /** level data a mod gave 1.21.11's legacy world border settings this session */
    private static final Map<ServerLevelData, Optional<WorldBorder.Settings>> LEGACY_BORDER = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * 1.21.11 ServerLevelData.getLegacyWorldBorderSettings(): world border settings level.dat carried from
     * before the border was saved data. 1.21.11's server took them over when it created its levels and
     * cleared them, and 26.1's data fixer moves them into the border's saved data before a world loads: from
     * then on empty, unless a mod set some since (the derived data answers the primary's)
     */
    public static Optional<WorldBorder.Settings> getLegacyWorldBorderSettings(ServerLevelData data) {
        ServerLevelData primary = derived(data) ? primary(data) : data;
        return primary == null ? Optional.empty() : LEGACY_BORDER.getOrDefault(primary, Optional.empty());
    }

    /**
     * 1.21.11 ServerLevelData.setLegacyWorldBorderSettings(settings): kept on the level data until the server next
     * creates its levels (which cleared them, using them only for an overworld with no border saved data -- which
     * a world 26.1 has run has); the derived data ignored it
     */
    public static void setLegacyWorldBorderSettings(ServerLevelData data, Optional<WorldBorder.Settings> settings) {
        if (!derived(data)) {
            LEGACY_BORDER.put(data, settings);
        }
    }

    private static ServerLevelData primary(ServerLevelData data) {
        MinecraftServer s = SERVER;
        return s == null ? null : s.getWorldData().overworldData();
    }

    /** the server is creating its levels: 1.21.11 cleared the legacy border settings here (CbWallServerMixin) */
    public static void levelsCreated(MinecraftServer s) {
        LEGACY_BORDER.remove(s.getWorldData().overworldData());
    }
}
