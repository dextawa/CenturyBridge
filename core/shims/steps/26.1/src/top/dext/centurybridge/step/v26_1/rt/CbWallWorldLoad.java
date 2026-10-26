package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.WorldData;

/**
 * 1.21.11's world loading handed one WorldData around; 26.1 hands LevelDataAndDimensions.WorldDataAndGenSettings
 * (the data and, split off, the WorldGenSettings: world options and dimensions) and the new world's initial game
 * rules beside it (Optional&lt;GameRules&gt;). 1.21.11's PrimaryLevelData carried the options itself, its
 * LevelSettings the game rules, and the dimensions were the LEVEL_STEM registry of the stem's registries (javap of
 * 1.21.11 WorldStem / WorldOpenFlows.createLevelFromExistingSettings / LevelStorageSource.getLevelDataAndDimensions
 * and their 26.1 counterparts).
 * <ul>
 * <li>A WorldData 26.1 hands out where 1.21.11 handed one (WorldStem.worldData(), a 1.21.11 CreateWorldCallback,
 *     WorldOpenFlows.createLevelFromExistingSettings overrides) keeps the generation settings and initial rules it
 *     came with ({@link #data}); handed back, it gets exactly those again ({@link #withGenSettings},
 *     {@link #initialRules(WorldData)}).</li>
 * <li>A WorldData a 1.21.11 caller built (PrimaryLevelData constructor / parse, rt/CbWallWorldData) gets the options
 *     it was built with and the dimensions of the registries it is handed back with -- 26.1's own
 *     WorldGenSettings.of(options, registries) -- and the game rules its settings were built with
 *     (rt/CbWallLevelSettings.builtRules).</li>
 * </ul>
 */
public final class CbWallWorldLoad {

    private CbWallWorldLoad() {
    }

    // ---- WorldData <-> WorldDataAndGenSettings ----

    private static final Map<WorldData, WorldGenSettings> GEN = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<WorldData, Optional<GameRules>> RULES = Collections.synchronizedMap(new WeakHashMap<>());

    /** the generation settings 26.1 handed this data out with, else null */
    static WorldGenSettings keptGenSettings(WorldData data) {
        return GEN.get(data);
    }

    /** the initial game rules 26.1 handed this data out with (a 1.21.11 CreateWorldCallback), else null */
    static Optional<GameRules> keptRules(WorldData data) {
        return RULES.get(data);
    }

    static void keepRules(WorldData data, Optional<GameRules> rules) {
        RULES.put(data, rules);
    }

    /** 26.1's pair handed to a 1.21.11 caller as its WorldData: the data, remembering the generation settings */
    public static WorldData data(LevelDataAndDimensions.WorldDataAndGenSettings pair) {
        WorldData data = pair.data();
        GEN.put(data, pair.genSettings());
        return data;
    }

    /** the same, with the initial game rules 26.1 passed beside the pair */
    public static WorldData data(LevelDataAndDimensions.WorldDataAndGenSettings pair, Optional<GameRules> rules) {
        WorldData data = data(pair);
        RULES.put(data, rules);
        return data;
    }

    /** a 1.21.11 WorldData as 26.1's pair, its dimensions from {@code registries} (see the class comment) */
    public static LevelDataAndDimensions.WorldDataAndGenSettings withGenSettings(RegistryAccess registries, WorldData data) {
        WorldGenSettings kept = GEN.get(data);
        if (kept != null) {
            return new LevelDataAndDimensions.WorldDataAndGenSettings(data, kept);
        }
        return new LevelDataAndDimensions.WorldDataAndGenSettings(data, WorldGenSettings.of(CbWallLevelData.worldGenOptions(data), registries));
    }

    /** the same for a stem's registries */
    public static LevelDataAndDimensions.WorldDataAndGenSettings withGenSettings(LayeredRegistryAccess<RegistryLayer> registries, WorldData data) {
        return withGenSettings(registries.compositeAccess(), data);
    }

    /**
     * The initial game rules 26.1 takes beside a 1.21.11 WorldData: the ones it was handed out with, else the rules a
     * 1.21.11 caller built its settings with; empty for a world read from disk, whose own rules 26.1 loads (as
     * 1.21.11 loaded them from level.dat).
     */
    public static Optional<GameRules> initialRules(WorldData data) {
        Optional<GameRules> kept = RULES.get(data);
        if (kept != null) {
            return kept;
        }
        return CbWallLevelSettings.builtRules(data.getLevelSettings());
    }

    /** the same for a stem's world data (server constructors, Minecraft.doWorldLoad) */
    public static Optional<GameRules> initialRules(WorldStem stem) {
        return initialRules(stem.worldDataAndGenSettings().data());
    }

    /** 1.21.11 new WorldStem(resourceManager, dataPackResources, registries, worldData) */
    public static WorldStem worldStem(CloseableResourceManager resourceManager, ReloadableServerResources dataPackResources,
                                      LayeredRegistryAccess<RegistryLayer> registries, WorldData worldData) {
        return new WorldStem(resourceManager, dataPackResources, registries, withGenSettings(registries, worldData));
    }

    /**
     * 1.21.11 new LevelDataAndDimensions(worldData, dimensions): the generation settings the data came with, else its
     * options with these dimensions (1.21.11's were the baked registry of the same dimensions)
     */
    public static LevelDataAndDimensions levelDataAndDimensions(WorldData worldData, WorldDimensions.Complete dimensions) {
        WorldGenSettings gen = GEN.get(worldData);
        if (gen == null) {
            gen = new WorldGenSettings(CbWallLevelData.worldGenOptions(worldData), new WorldDimensions(dimensions.dimensions()));
        }
        return new LevelDataAndDimensions(new LevelDataAndDimensions.WorldDataAndGenSettings(worldData, gen), dimensions);
    }

    // ---- servers ----

    /**
     * MinecraftServer's event loop argument 26.1 added (propagatesCrashes: throw the delayed fatal crash from the
     * server's own waits). 1.21.11's every MinecraftServer threw the fatal exception from its blocking waits
     * (rt/CbWallFatal), so a 1.21.11 server subclass propagates, as 26.1's DedicatedServer does.
     */
    public static boolean propagatesCrashes() {
        return true;
    }

    /**
     * GameTestServer.create's repeat count 26.1 added: 1.21.11 ran each selected test once with the direct batch
     * decorator (no verify), which 26.1 does for a repeat count of 0 (javap of both).
     */
    public static int noRepeats() {
        return 0;
    }

    // ---- level storage ----

    /**
     * The rest of 1.21.11's LevelStorageAccess.saveDataTag(registries, worldData[, player]): 1.21.11's level.dat also
     * held the world's generation settings and game rules, which 26.1 keeps in the world's saved data files
     * (world_gen_settings, game_rules) and its server writes with the rest of its saved data. Written beside
     * level.dat with 26.1's own writers when they are known for the data: the running server's own, or the ones the
     * data stands for (handed out with, or a 1.21.11 constructor's options and built rules). Data with none of them
     * known leaves the world's files as they are. A write failure is logged, as 26.1 logs a failed level.dat save.
     */
    public static void saveSplitData(LevelStorageSource.LevelStorageAccess access, RegistryAccess registries, WorldData data) {
        net.minecraft.server.MinecraftServer s = CbWallLevelData.SERVER;
        boolean running = s != null && s.getWorldData() == data;
        WorldGenSettings gen = running ? s.getWorldGenSettings() : GEN.get(data);
        if (gen == null) {
            net.minecraft.world.level.levelgen.WorldOptions options = CbWallWorldData.options(data);
            if (options != null) {
                gen = WorldGenSettings.of(options, registries);
            }
        }
        Optional<GameRules> rules = running ? Optional.of(s.getGameRules()) : initialRules(data);
        java.nio.file.Path root = access.getLevelPath(net.minecraft.world.level.storage.LevelResource.ROOT);
        try {
            if (gen != null) {
                LevelStorageSource.writeWorldGenSettings(registries, root, gen);
            }
            if (rules.isPresent()) {
                LevelStorageSource.writeGameRules(data, root, rules.get());
            }
        } catch (IOException | RuntimeException e) {
            com.mojang.logging.LogUtils.getLogger().error("Failed to save the generation settings / game rules of level {}", root, e);
        }
    }

    /** tags 1.21.11's getDataTag answered, and the access each was read through (identity; few at a time) */
    private static final List<WeakReference<?>[]> TAGS = new ArrayList<>();

    private static void readThrough(Dynamic<?> tag, LevelStorageSource.LevelStorageAccess access) {
        synchronized (TAGS) {
            TAGS.removeIf(e -> e[0].get() == null || e[1].get() == null);
            TAGS.add(new WeakReference<?>[] {new WeakReference<>(tag.getValue()), new WeakReference<>(access)});
        }
    }

    private static LevelStorageSource.LevelStorageAccess accessOf(Dynamic<?> tag, String what) {
        Object value = tag.getValue();
        synchronized (TAGS) {
            for (Iterator<WeakReference<?>[]> it = TAGS.iterator(); it.hasNext(); ) {
                WeakReference<?>[] e = it.next();
                if (e[0].get() == value && e[1].get() instanceof LevelStorageSource.LevelStorageAccess access) {
                    return access;
                }
            }
        }
        return CbWallLevelAccess.onlyOpen(what);
    }

    /** 1.21.11 LevelStorageAccess.getDataTag() (method_54545): level.dat's data, fixed to the current version */
    public static Dynamic<?> getDataTag(LevelStorageSource.LevelStorageAccess access) throws IOException {
        return fixedDataTag(access, false);
    }

    /** 1.21.11 LevelStorageAccess.getDataTagFallback() (method_54546): the same from level.dat_old */
    public static Dynamic<?> getDataTagFallback(LevelStorageSource.LevelStorageAccess access) throws IOException {
        return fixedDataTag(access, true);
    }

    /**
     * 1.21.11's readLevelDataTagFixed: the LEVEL data fix of the "Data" tag, then the PLAYER and WORLD_GEN_SETTINGS
     * fixes of its "Player" / "WorldGenSettings" entries, in memory. 26.1 reads the tag unfixed and fixes it the same
     * way (WorldOpenFlows.recreateWorldData) once the world's files are fixed; a world whose files still need 26.1's
     * file fixing (it moves files on disk) is refused, as 26.1's own readers refuse it.
     */
    private static Dynamic<?> fixedDataTag(LevelStorageSource.LevelStorageAccess access, boolean fallback) throws IOException {
        Dynamic<?> tag = access.getUnfixedDataTag(fallback);
        int version = NbtUtils.getDataVersion(tag);
        if (DataFixers.getFileFixer().requiresFileFixing(version)) {
            throw new IllegalStateException(access.getLevelId() + ": 1.21.11's LevelStorageAccess.getDataTag fixed level.dat in memory;"
                + " 26.1 must first fix this world's files on disk (data version " + version + "), which only opening the world does");
        }
        Dynamic<?> fixed = fix(DataFixers.getDataFixer(), tag, version);
        readThrough(fixed, access);
        return fixed;
    }

    private static <T> Dynamic<T> fix(DataFixer fixer, Dynamic<T> tag, int version) {
        Dynamic<T> fixed = DataFixTypes.LEVEL.updateToCurrentVersion(fixer, tag, version);
        fixed = fixed.update("Player", d -> DataFixTypes.PLAYER.updateToCurrentVersion(fixer, d, version));
        return fixed.update("WorldGenSettings", d -> DataFixTypes.WORLD_GEN_SETTINGS.updateToCurrentVersion(fixer, d, version));
    }

    /**
     * 1.21.11 LevelStorageAccess.getSummary(tag) (method_29584): checkLock, then the level source's summary of the tag
     * as given (no fix; 1.21.11's callers pass getDataTag's fixed tag). 26.1's makeLevelSummary also takes the data
     * version it decides file fixing by: the tag's own.
     */
    public static LevelSummary getSummary(LevelStorageSource.LevelStorageAccess access, Dynamic<?> tag) {
        access.checkLock();
        return access.parent().makeLevelSummary(tag, access.getLevelDirectory(), false, NbtUtils.getDataVersion(tag));
    }

    /**
     * 1.21.11 LevelStorageSource.getLevelDataAndDimensions(tag, dataConfiguration, levelStems, registries)
     * (method_54523): 1.21.11 read the generation settings from the tag's "WorldGenSettings"; 26.1 reads them from the
     * world's saved data through the level storage access. That access is the one the tag was read through
     * (getDataTag), else the one open access (rt/CbWallLevelAccess).
     */
    public static LevelDataAndDimensions getLevelDataAndDimensions(Dynamic<?> tag, WorldDataConfiguration dataConfiguration,
                                                                   Registry<LevelStem> levelStems, HolderLookup.Provider registries) {
        LevelStorageSource.LevelStorageAccess access = accessOf(tag, "LevelStorageSource.getLevelDataAndDimensions(tag, ..)");
        LevelDataAndDimensions result = LevelStorageSource.getLevelDataAndDimensions(access, tag, dataConfiguration, levelStems, registries);
        data(result.worldDataAndGenSettings());
        return result;
    }
}
