package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1508;
import net.minecraft.class_1510;
import net.minecraft.class_1937;
import net.minecraft.class_1923;
import net.minecraft.class_1966;
import net.minecraft.class_2794;
import net.minecraft.class_2960;
import net.minecraft.class_3195;
import net.minecraft.class_3485;
import net.minecraft.class_5321;
import net.minecraft.class_5455;
import net.minecraft.class_5539;
import net.minecraft.class_6880;
import net.minecraft.class_7138;
import net.minecraft.class_7924;
import java.util.function.Predicate;
import top.dext.centurybridge.step.v1_21_4.mixin.CbRestLevelInvoker;

/**
 * Level.dragonParts(), abstract since 1.21.4 (abstractFills): 1.21.4's getEntities(..) adds the dragon parts
 * from it, where 1.21.3's added each ender dragon's parts as it met the dragon. A 1.21.3 level subclass (a
 * mod's fake or preview level) gets the parts of the ender dragons it holds, the parts 1.21.3's lookup could
 * find there.
 */
public final class CbRestLevel {

    private CbRestLevel() {
    }

    public static Collection<class_1508> dragonParts(class_1937 level) {
        List<class_1508> parts = new ArrayList<>();
        for (class_1297 e : ((CbRestLevelInvoker) level).cb$entities().method_31803()) {
            if (e instanceof class_1510 dragon) {
                for (class_1508 part : dragon.method_5690()) {
                    parts.add(part);
                }
            }
        }
        return parts;
    }

    // ---- frameDefaults of the structure generation hooks ----

    private static final class_5321<class_1937> UNKNOWN_LEVEL =
        class_5321.method_29179(class_7924.field_41223, class_2960.method_60655("centurybridge", "unknown"));

    /**
     * the level key 1.21.4's ChunkGenerator.createStructures / Structure.generate take, when a 1.21.3 override
     * is entered directly (no frame): 1.21.3 passed none, and 1.21.4 reads it only for its JFR structure
     * generation event, which then names no real dimension but this placeholder (never null: the event reads it)
     */
    public static class_5321<class_1937> unknownLevel() {
        return UNKNOWN_LEVEL;
    }

    /** the structure's own registry holder, the holder 1.21.4's callers pass to Structure.generate */
    public static class_6880<class_3195> structureHolder(class_3195 self, class_5455 registries, class_2794 generator, class_1966 biomes,
            class_7138 random, class_3485 templates, long seed, class_1923 pos, int refs, class_5539 height, Predicate<?> valid) {
        return registries.method_30530(class_7924.field_41246).method_47983(self);
    }

    /**
     * ChunkGenerator.createStructures(..) of 1.21.3, as called by a mod: 1.21.4 also takes the level key, which
     * its own caller takes from the generating level; the structure manager's level is that level.
     */
    public static class_5321<class_1937> dimensionOf(net.minecraft.class_5138 structures) {
        net.minecraft.class_1936 level = ((top.dext.centurybridge.step.v1_21_4.mixin.CbRestStructureManagerAccess) structures).cb$level();
        return level instanceof net.minecraft.class_5425 server ? server.method_8410().method_27983() : UNKNOWN_LEVEL;
    }

    /** Structure.generate(..) of 1.21.3, as called by a mod: 1.21.4 also takes the structure's holder and the level key */
    public static net.minecraft.class_3449 method_41614(class_3195 self, class_5455 registries, class_2794 generator, class_1966 biomes,
            class_7138 random, class_3485 templates, long seed, class_1923 pos, int refs, class_5539 height,
            Predicate<class_6880<net.minecraft.class_1959>> valid) {
        return self.method_41614(structureHolder(self, registries, generator, biomes, random, templates, seed, pos, refs, height, valid),
            UNKNOWN_LEVEL, registries, generator, biomes, random, templates, seed, pos, refs, height, valid);
    }

    /** MultifaceBlock.getSpreader() of 1.21.3: every 1.21.3 MultifaceBlock spreads, a MultifaceSpreadeableBlock in 1.21.4 */
    public static net.minecraft.class_7118 method_41432(net.minecraft.class_5778 block) {
        return ((net.minecraft.class_10376) block).method_41432();
    }
}
