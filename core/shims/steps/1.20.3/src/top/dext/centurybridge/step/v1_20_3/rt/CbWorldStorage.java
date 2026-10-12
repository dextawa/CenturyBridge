package top.dext.centurybridge.step.v1_20_3.rt;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.class_155;
import net.minecraft.class_156;
import net.minecraft.class_1940;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2505;
import net.minecraft.class_2507;
import net.minecraft.class_2509;
import net.minecraft.class_2512;
import net.minecraft.class_2520;
import net.minecraft.class_31;
import net.minecraft.class_32;
import net.minecraft.class_34;
import net.minecraft.class_4284;
import net.minecraft.class_5219;
import net.minecraft.class_5285;
import net.minecraft.class_5315;
import net.minecraft.class_5363;
import net.minecraft.class_7712;
import net.minecraft.class_7723;
import net.minecraft.class_7726;
import net.minecraft.class_8579;
import net.minecraft.class_8581;
import org.slf4j.Logger;
import top.dext.centurybridge.step.v1_20_3.mixin.CbWorldLevelStorageAccessInvoker;
import top.dext.centurybridge.step.v1_20_3.mixin.CbWorldLevelStorageSourceAccess;
import top.dext.centurybridge.step.v1_20_3.mixin.CbWorldPrimaryLevelDataAccess;

/**
 * World storage: 1.20.2's LevelStorageAccess readers (getDataTag(ops, ..), getSummary(), getDataConfiguration())
 * and PrimaryLevelData.parse on 1.20.3, which reads level.dat once into a fixed Dynamic and parses from that.
 * Each reader keeps its 1.20.2 contract: nothing when the world folder is gone, level.dat first and level.dat_old
 * when level.dat is missing or yields nothing, the same data fixes, the same failures. Wired from s2world.json.
 */
public final class CbWorldStorage {

    private static final Logger LOGGER = LogUtils.getLogger();

    @FunctionalInterface
    private interface Reader<T> {
        T read(Path file);
    }

    /** 1.20.2's LevelStorageSource.readLevelData(directory, reader) */
    private static <T> T readLevelData(class_32.class_7411 dir, Reader<T> reader) {
        if (!Files.exists(dir.comp_732())) {
            return null;
        }
        Path data = dir.method_43425();
        if (Files.exists(data)) {
            T t = reader.read(data);
            if (t != null) {
                return t;
            }
        }
        Path old = dir.method_43426();
        return Files.exists(old) ? reader.read(old) : null;
    }

    private static DataFixer fixerOf(class_32.class_5143 access) {
        return ((CbWorldLevelStorageSourceAccess) access.method_52617()).cb$fixerUpper();
    }

    // ------------------------------------------------------------------ getDataTag(ops, dataConfiguration, dimensions, lifecycle)

    /**
     * 1.20.2's LevelStorageAccess.getDataTag(ops, dataConfiguration, dimensions, lifecycle): the world data and its
     * dimensions, parsed with the caller's ops (registry ops over the world's registries) and lifecycle, where
     * 1.20.3's getLevelDataAndDimensions takes the frozen registry access both came from. A level.dat that cannot be
     * read is an UncheckedIOException, as in 1.20.2 (no fallback to level.dat_old then).
     */
    public static Pair<class_5219, class_7723.class_7725> getDataTag(class_32.class_5143 access, DynamicOps<class_2520> ops,
                                                                    class_7712 dataConfiguration, class_2378<class_5363> dimensions,
                                                                    Lifecycle lifecycle) {
        ((CbWorldLevelStorageAccessInvoker) access).cb$checkLock();
        DataFixer fixer = fixerOf(access);
        return readLevelData(access.method_54543(), file -> readWorldData(file, fixer, ops, dataConfiguration, dimensions, lifecycle));
    }

    private static Pair<class_5219, class_7723.class_7725> readWorldData(Path file, DataFixer fixer, DynamicOps<class_2520> ops,
                                                                        class_7712 dataConfiguration, class_2378<class_5363> dimensions,
                                                                        Lifecycle lifecycle) {
        class_2487 root;
        try {
            root = class_2507.method_30613(file, class_2505.method_53898());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        class_2487 data = root.method_10562("Data");
        class_2487 player = data.method_10573("Player", 10) ? data.method_10562("Player") : null;
        data.method_10551("Player");
        int version = class_2512.method_48309(data, -1);
        Dynamic<class_2520> fixed = class_4284.field_19212.method_48128(fixer, new Dynamic<>(ops, data), version);
        // 1.20.2 fixed WorldGenSettings on its own (after copying pre-1.16 top-level keys into it, which 1.20.3's
        // LEVEL fix now moves there itself), then parsed it
        Dynamic<class_2520> genSettings = class_4284.field_24640.method_48128(fixer, fixed.get("WorldGenSettings").orElseEmptyMap(), version);
        class_7726 worldGen = class_7726.field_40366.parse(genSettings)
            .getOrThrow(false, class_156.method_29188("WorldGenSettings: ", LOGGER::error));
        class_5315 levelVersion = class_5315.method_29023(fixed);
        class_1940 settings = class_1940.method_28383(fixed, dataConfiguration);
        class_7723.class_7725 complete = worldGen.comp_1020().method_45518(dimensions);
        Lifecycle worldLifecycle = complete.method_45536().add(lifecycle);
        class_31 primary = parsePrimaryLevelData(fixed, fixer, version, player, settings, levelVersion, complete.comp_1018(),
            worldGen.comp_1019(), worldLifecycle);
        return Pair.of(primary, complete);
    }

    // ------------------------------------------------------------------ getSummary()

    /**
     * 1.20.2's LevelStorageAccess.getSummary(): the world list entry of this world, read as 1.20.2's summary reader
     * read it (a symlinked level.dat failing validation gives the symlink entry; an unreadable file, an invalid root
     * tag or an unknown level format gives none, and level.dat_old is tried). 1.20.3's getSummary(Dynamic) builds
     * the entry from the fixed data exactly as 1.20.2's reader did.
     */
    public static class_34 getSummary(class_32.class_5143 access) {
        ((CbWorldLevelStorageAccessInvoker) access).cb$checkLock();
        DataFixer fixer = fixerOf(access);
        class_32.class_7411 dir = access.method_54543();
        return readLevelData(dir, file -> readSummary(access, dir, fixer, file));
    }

    private static class_34 readSummary(class_32.class_5143 access, class_32.class_7411 dir, DataFixer fixer, Path file) {
        try {
            if (Files.isSymbolicLink(file)) {
                List<class_8581> issues = access.method_52617().method_52237().method_52618(file);
                if (!issues.isEmpty()) {
                    LOGGER.warn("{}", class_8579.method_52241(file, issues));
                    return new class_34.class_8578(dir.method_43422(), dir.method_43427());
                }
            }
            class_2520 tag = CbWorldLevelStorageSourceAccess.cb$readLightweightData(file);
            if (tag instanceof class_2487 root) {
                class_2487 data = root.method_10562("Data");
                int version = class_2512.method_48309(data, -1);
                Dynamic<class_2520> fixed = class_4284.field_19212.method_48128(fixer, new Dynamic<>(class_2509.field_11560, data), version);
                int levelDataVersion = class_5315.method_29023(fixed).method_29022();
                if (levelDataVersion == 19132 || levelDataVersion == 19133) {
                    return access.method_29584(fixed);
                }
            } else {
                LOGGER.warn("Invalid root tag in {}", file);
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Exception reading {}", file, e);
            return null;
        }
    }

    // ------------------------------------------------------------------ getDataConfiguration()

    /** 1.20.2's LevelStorageAccess.getDataConfiguration(): the world's data packs and features, read off level.dat */
    public static class_7712 getDataConfiguration(class_32.class_5143 access) {
        ((CbWorldLevelStorageAccessInvoker) access).cb$checkLock();
        DataFixer fixer = fixerOf(access);
        return readLevelData(access.method_54543(), file -> readDataConfiguration(file, fixer));
    }

    private static class_7712 readDataConfiguration(Path file, DataFixer fixer) {
        try {
            class_2520 tag = CbWorldLevelStorageSourceAccess.cb$readLightweightData(file);
            if (tag instanceof class_2487 root) {
                class_2487 data = root.method_10562("Data");
                int version = class_2512.method_48309(data, -1);
                Dynamic<class_2520> fixed = class_4284.field_19212.method_48128(fixer, new Dynamic<>(class_2509.field_11560, data), version);
                return class_32.method_29580(fixed);
            }
        } catch (Exception e) {
            LOGGER.error("Exception reading {}", file, e);
        }
        return null;
    }

    // ------------------------------------------------------------------ PrimaryLevelData.parse

    /**
     * 1.20.2's PrimaryLevelData.parse(dynamic, fixer, playerDataVersion, playerTag, settings, levelVersion, ..):
     * 1.20.3's parse reads the player tag (already fixed) and the level version out of the data itself; here the
     * caller's player tag -- fixed up from its data version, which 1.20.2 did on first read -- and level version are
     * what the world data keeps, as in 1.20.2.
     */
    public static <T> class_31 parsePrimaryLevelData(Dynamic<T> dynamic, DataFixer fixer, int playerDataVersion, class_2487 playerTag,
                                                     class_1940 settings, class_5315 levelVersion, class_31.class_7729 special,
                                                     class_5285 options, Lifecycle lifecycle) {
        class_31 data = class_31.method_29029(dynamic, settings, special, options, lifecycle);
        CbWorldPrimaryLevelDataAccess access = (CbWorldPrimaryLevelDataAccess) data;
        access.cb$setLoadedPlayerTag(upgradedPlayerTag(fixer, playerTag, playerDataVersion));
        access.cb$setVersion(levelVersion.method_29022());
        return data;
    }

    /** 1.20.2's PrimaryLevelData.updatePlayerTag(), run at once */
    private static class_2487 upgradedPlayerTag(DataFixer fixer, class_2487 tag, int playerDataVersion) {
        if (tag == null || playerDataVersion >= class_155.method_16673().method_37912().method_38494()) {
            return tag;
        }
        if (fixer == null) {
            throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
        }
        return class_4284.field_19213.method_48130(fixer, tag, playerDataVersion);
    }

    private CbWorldStorage() {
    }
}
