package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.class_1936;
import net.minecraft.class_1948;
import net.minecraft.class_1937;
import net.minecraft.class_1950;
import net.minecraft.class_1959;
import net.minecraft.class_18;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2378;
import net.minecraft.class_2424;
import net.minecraft.class_2487;
import net.minecraft.class_2507;
import net.minecraft.class_2512;
import net.minecraft.class_2680;
import net.minecraft.class_2709;
import net.minecraft.class_2818;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3499;
import net.minecraft.class_3695;
import net.minecraft.class_3748;
import net.minecraft.class_3751;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6568;
import net.minecraft.class_6749;
import net.minecraft.class_7225;
import net.minecraft.class_8490;
import net.minecraft.class_7923;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.21.1's remaining level API on 1.21.2: biome weather queries, block registration helpers, portal shapes, saved
 * data files, growing plants, coral, material rule lists, teleport transitions.
 * <ul>
 * <li>Biome: 1.21.2's temperature takes the level's sea level and raises the height threshold to sea level + 17;
 *     1.21.1's threshold was 80, which is sea level 63 -- the weather queries are 1.21.1's bodies on that
 *     temperature, asked uncached (1.21.2's per-biome cache is keyed by position alone and shared with other sea
 *     levels; 1.21.1's cache only memoized the same value).</li>
 * <li>PortalShape: 1.21.1 built it from a level, position and axis and kept the level for createPortalBlocks;
 *     1.21.2's findAnyShape computes the same shape and createPortalBlocks takes the level, which is kept here.</li>
 * <li>SavedData.save(file, registries): 1.21.1's body -- when dirty, the "data" compound with the data version,
 *     written compressed to the file, the failure logged, then clean -- on 1.21.2's save(registries), which builds
 *     that compound and clears dirty.</li>
 * </ul>
 */
public final class CbLevelMisc {

    private static final Logger LOGGER = LoggerFactory.getLogger("centurybridge");
    private static final int SEA_LEVEL_1_21_1 = 63;

    // ---- biome ----

    /** Biome's private height-adjusted temperature (CbLevelBiomeInvoker) */
    public interface BiomeAccess {
        float cb$heightAdjustedTemperature(class_2338 pos, int seaLevel);
    }

    private static float temperature(class_1959 biome, class_2338 pos) {
        return ((BiomeAccess) (Object) biome).cb$heightAdjustedTemperature(pos, SEA_LEVEL_1_21_1);
    }

    /** biome.getPrecipitationAt(pos) */
    public static class_1959.class_1963 method_48162(class_1959 self, class_2338 pos) {
        if (!self.method_48163()) {
            return class_1959.class_1963.field_9384;
        }
        return method_33599(self, pos) ? class_1959.class_1963.field_9383 : class_1959.class_1963.field_9382;
    }

    /** biome.coldEnoughToSnow(pos) */
    public static boolean method_33599(class_1959 self, class_2338 pos) {
        return !method_39927(self, pos);
    }

    /** biome.warmEnoughToRain(pos) */
    public static boolean method_39927(class_1959 self, class_2338 pos) {
        return temperature(self, pos) >= 0.15F;
    }

    /** biome.shouldMeltFrozenOceanIcebergSlightly(pos) */
    public static boolean method_39928(class_1959 self, class_2338 pos) {
        return temperature(self, pos) > 0.1F;
    }

    /** the state at the neighbour position Block.shouldRenderFace asks with since 1.21.2 */
    public static class_2680 stateAt(net.minecraft.class_1922 level, class_2338 pos) {
        return level.method_8320(pos);
    }

    // ---- Blocks ----

    /** Blocks.register(id, block) */
    public static class_2248 method_9492(String id, class_2248 block) {
        return class_2378.method_10226(class_7923.field_41175, id, block);
    }

    /** Blocks.register(key, block) */
    public static class_2248 method_52571(class_5321<class_2248> key, class_2248 block) {
        return class_2378.method_39197(class_7923.field_41175, key, block);
    }

    /** Blocks.rebuildCache(): every block state's cache */
    public static void method_26979() {
        class_2248.field_10651.forEach(state -> state.method_26200());
    }

    // ---- PortalShape ----

    private static final Map<class_2424, class_1936> PORTAL_LEVELS = new WeakHashMap<>();

    /** new PortalShape(level, pos, axis) */
    public static class_2424 cbNew(class_1936 level, class_2338 pos, class_2350.class_2351 axis) {
        class_2424 shape = class_2424.method_64314(level, pos, axis);
        remember(shape, level);
        return shape;
    }

    /** the level a 1.21.1 portal shape was built in (also a subclass's super(..), superCtorReshapes post) */
    public static void remember(class_2424 shape, class_1936 level) {
        synchronized (PORTAL_LEVELS) {
            PORTAL_LEVELS.put(shape, level);
        }
    }

    /** portalShape.createPortalBlocks() in the level it was built in */
    public static void method_10363(class_2424 self) {
        self.method_10363(portalLevel(self));
    }

    /** the level a 1.21.1 shape was built in (also frameDefaults of a createPortalBlocks() override entered directly) */
    public static class_1936 portalLevel(class_2424 self) {
        class_1936 level;
        synchronized (PORTAL_LEVELS) {
            level = PORTAL_LEVELS.get(self);
        }
        if (level == null) {
            throw new IllegalStateException("1.21.1's PortalShape.createPortalBlocks() on a shape 1.21.2 made: "
                + "1.21.2 hands the level to createPortalBlocks(level)");
        }
        return level;
    }

    /** the shape a 1.21.1 subclass's super(level, pos, axis) stands for, field by field (CbLevelPortalAccess) */
    public interface PortalAccess {
        class_2350.class_2351 cb$axis();

        int cb$portalBlocks();

        class_2350 cb$rightDir();

        class_2338 cb$bottomLeft();

        int cb$height();

        int cb$width();
    }

    private static PortalAccess shape(class_1936 level, class_2338 pos, class_2350.class_2351 axis) {
        return (PortalAccess) class_2424.method_64314(level, pos, axis);
    }

    public static class_2350.class_2351 portalAxis(class_1936 level, class_2338 pos, class_2350.class_2351 axis) {
        return shape(level, pos, axis).cb$axis();
    }

    public static int portalBlocks(class_1936 level, class_2338 pos, class_2350.class_2351 axis) {
        return shape(level, pos, axis).cb$portalBlocks();
    }

    public static class_2350 portalRightDir(class_1936 level, class_2338 pos, class_2350.class_2351 axis) {
        return shape(level, pos, axis).cb$rightDir();
    }

    public static class_2338 portalBottomLeft(class_1936 level, class_2338 pos, class_2350.class_2351 axis) {
        return shape(level, pos, axis).cb$bottomLeft();
    }

    public static int portalWidth(class_1936 level, class_2338 pos, class_2350.class_2351 axis) {
        return shape(level, pos, axis).cb$width();
    }

    public static int portalHeight(class_1936 level, class_2338 pos, class_2350.class_2351 axis) {
        return shape(level, pos, axis).cb$height();
    }

    // ---- saved data ----

    /**
     * A 1.21.1 SavedData subclass declaring save(file, registries) (adapterHooks). 1.21.1's DimensionDataStorage
     * asked every loaded data to save itself to its file, and save(file, registries) wrote it when dirty; 1.21.2's
     * collects the dirty data's tags and writes them itself. Such an override is asked as 1.21.1 asked it, for every
     * loaded data, with its file (CbLevelSavedDataMixin).
     */
    public interface LegacySaveFile {
        void method_17919(File file, class_7225.class_7874 registries);
    }

    /** savedData.save(file, registries): a 1.21.1 override where the data has one, else 1.21.1's body */
    public static void method_17919(class_18 self, File file, class_7225.class_7874 registries) {
        if (self instanceof LegacySaveFile l) {
            l.method_17919(file, registries);
        } else {
            superSave(self, file, registries);
        }
    }

    /** 1.21.1's save(file, registries) body (also its super call): the dirty data's tag, written to the file */
    public static void superSave(class_18 self, File file, class_7225.class_7874 registries) {
        if (!self.method_79()) {
            return;
        }
        class_2487 tag = self.method_17919(registries);
        try {
            class_2507.method_30614(tag, file.toPath());
        } catch (java.io.IOException e) {
            LOGGER.error("Could not save data {}", self, e);
        }
        self.method_78(false);
    }

    /** a 1.21.1 data storage directory, as the path 1.21.2 takes */
    public static Path path(File file) {
        return file.toPath();
    }

    // ---- pathfinding region ----

    /** PathNavigationRegion's level (CbLevelRegionAccess) */
    public interface RegionAccess {
        class_1937 cb$level();
    }

    /**
     * A 1.21.1 PathNavigationRegion subclass declaring getProfiler() (adapterHooks): 1.21.1's PathFinder.findPath
     * profiled the search on the region's profiler, 1.21.2's on the thread's (Profiler.get()). The region being
     * searched is noted for the search (CbLevelPathFinderMixin) and such an override answers its profiler.
     */
    public interface LegacyRegionProfiler {
        class_3695 method_37233();
    }

    private static final ThreadLocal<class_1950> SEARCHED_REGION = new ThreadLocal<>();

    /** PathFinder.findPath starting (region) and done (null) on this thread */
    public static void searchedRegion(class_1950 region) {
        if (region == null) {
            SEARCHED_REGION.remove();
        } else {
            SEARCHED_REGION.set(region);
        }
    }

    /** the profiler of the search: a 1.21.1 override's on the searched region, else 1.21.2's */
    public static class_3695 searchProfiler(class_3695 profiler) {
        return SEARCHED_REGION.get() instanceof LegacyRegionProfiler l ? l.method_37233() : profiler;
    }

    /** region.getProfiler(): a 1.21.1 override where the region has one, else its level's, as 1.21.1 answered */
    public static class_3695 method_37233(class_1950 self) {
        return self instanceof LegacyRegionProfiler l ? l.method_37233() : superProfiler(self);
    }

    /** 1.21.1's getProfiler body (also its super call): the level's profiler */
    public static class_3695 superProfiler(class_1950 self) {
        return CbLevelLevels.method_16107(((RegionAccess) self).cb$level());
    }

    // ---- growing plants, coral ----

    /** the random 1.21.2 takes where 1.21.1 took the level: the level's */
    public static class_5819 random(class_1936 level) {
        return level.method_8409();
    }

    /** frameDefaults of a 1.21.1 coral's tryScheduleDieTick entered directly: the level as tick access, its random */
    public static net.minecraft.class_10225 coralTicks(Object self, class_2680 state, class_1936 level, class_2338 pos) {
        return level;
    }

    public static class_5819 coralRandom(Object self, class_2680 state, class_1936 level, class_2338 pos) {
        return level.method_8409();
    }

    // ---- material rule list, teleport ----

    /** a 1.21.1 MaterialRuleList's list, as 1.21.2's array */
    public static class_6568.class_6569[] rules(List<class_6568.class_6569> rules) {
        return rules.toArray(new class_6568.class_6569[0]);
    }

    /** materialRuleList.materialRuleList() */
    public static List<class_6568.class_6569> comp_437(net.minecraft.class_6582 self) {
        return List.of(self.comp_437());
    }

    /** a 1.21.1 TeleportTransition: not as a passenger, no relative movement (1.21.2's own defaults) */
    public static boolean notPassenger() {
        return false;
    }

    public static Set<class_2709> noRelatives() {
        return Set.of();
    }

    /**
     * JigsawBlock.canAttach(info, other), 1.21.1's body: 1.21.2 compares JigsawBlockInfo records, their target and name
     * parsed to resource locations; 1.21.1 read the joint, target and name strings off the infos' tags and compared the
     * strings (the joint type by name, CbLevelBlockEntities, else aligned for a horizontal front, rollable otherwise)
     */
    public static boolean method_16546(class_3499.class_3501 info, class_3499.class_3501 other) {
        class_2350 front = class_3748.method_26378(info.comp_1342());
        class_2350 otherFront = class_3748.method_26378(other.comp_1342());
        class_2350 top = class_3748.method_26379(info.comp_1342());
        class_2350 otherTop = class_3748.method_26379(other.comp_1342());
        class_3751.class_4991 joint = CbLevelBlockEntities.method_26401(info.comp_1343().method_10558("joint"))
            .orElseGet(() -> front.method_10166().method_10179() ? class_3751.class_4991.field_23330 : class_3751.class_4991.field_23329);
        boolean rollable = joint == class_3751.class_4991.field_23329;
        return front == otherFront.method_10153() && (rollable || top == otherTop)
            && info.comp_1343().method_10558("target").equals(other.comp_1343().method_10558("name"));
    }

    /**
     * BlendingData.CODEC: 1.21.1's codec of the blending data itself (min_section, max_section, optional heights,
     * validated); 1.21.2 keeps the same fields and validation in BlendingData.Packed's codec and converts with
     * unpack / pack -- composed, the codec 1.21.1 had
     */
    private static final Codec<class_6749> BLENDING_DATA = class_6749.class_9899.field_52655.xmap(class_6749::method_61807,
        class_6749::method_61806);

    public static Codec<class_6749> field_35682() {
        return BLENDING_DATA;
    }

    /**
     * LootDataType.deserialize(id, ops, value), 1.21.1's body (gone in 1.21.2, whose loot data loads through the
     * registry): the type's codec parses the value, an error is logged with the type's registry and the id, and the
     * result is answered
     */
    public static <T, V> Optional<T> method_51204(class_8490<T> self, class_2960 id, DynamicOps<V> ops, V value) {
        DataResult<T> result = self.comp_2520().parse(ops, value);
        result.error().ifPresent(e -> LOGGER.error("Couldn't parse element {}/{} - {}", self.comp_2519().method_29177(), id, e.message()));
        return result.result();
    }

    /**
     * NaturalSpawner.spawnForChunk(level, chunk, state, spawnFriendlies, spawnMonsters, spawnPersistent): 1.21.1 spawned
     * each category the three flags allowed whose global and local caps had room; 1.21.2 takes the categories the
     * flags and the global cap allow (getFilteredSpawningCategories, the same flag tests) and checks the local cap
     * as it spawns them
     */
    public static void method_27821(class_3218 level, class_2818 chunk, class_1948.class_5262 state, boolean spawnFriendlies,
                                    boolean spawnMonsters, boolean spawnPersistent) {
        class_1948.method_27821(level, chunk, state, class_1948.method_61729(state, spawnFriendlies, spawnMonsters, spawnPersistent));
    }

    private CbLevelMisc() {
    }
}
