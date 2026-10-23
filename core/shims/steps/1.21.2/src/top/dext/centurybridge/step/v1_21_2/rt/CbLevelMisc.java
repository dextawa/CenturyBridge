package top.dext.centurybridge.step.v1_21_2.rt;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.class_1936;
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
import net.minecraft.class_2960;
import net.minecraft.class_3695;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6568;
import net.minecraft.class_7225;
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
        class_1936 level;
        synchronized (PORTAL_LEVELS) {
            level = PORTAL_LEVELS.get(self);
        }
        if (level == null) {
            throw new IllegalStateException("1.21.1's PortalShape.createPortalBlocks() on a shape 1.21.2 made: "
                + "1.21.2 hands the level to createPortalBlocks(level)");
        }
        self.method_10363(level);
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

    /** savedData.save(file, registries) */
    public static void method_17919(class_18 self, File file, class_7225.class_7874 registries) {
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

    /** region.getProfiler(): its level's, as 1.21.1 answered */
    public static class_3695 method_37233(class_1950 self) {
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

    private CbLevelMisc() {
    }
}
