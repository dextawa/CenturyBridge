package top.dext.centurybridge.step.v1_21_2.facade;

import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.class_1923;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2808;
import net.minecraft.class_2791;
import net.minecraft.class_2839;
import net.minecraft.class_2852;
import net.minecraft.class_3218;
import net.minecraft.class_4153;
import net.minecraft.class_9240;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelStorage;

/**
 * 1.21.1's ChunkSerializer (net/minecraft/class_2852; classRenames), a static utility with no state whose intermediary
 * name 1.21.2 gives to the final record SerializableChunkData: a 1.21.1 mod's class_2852 always means the utility, so
 * its references come here -- its instances (1.21.1's public no-argument constructor), subclasses and constants. The
 * statics: read and write (CbLevelStorage; calls go there directly, staticRedirects), getChunkTypeFromTag and packOffsets, which
 * 1.21.2's SerializableChunkData keeps with 1.21.1's results (packOffsets private, given back by the layer's access
 * widener). The constants are 1.21.1's tag keys.
 */
public class ChunkSerializer {
    public static final String field_37659 = "xPos";
    public static final String field_37660 = "zPos";
    public static final String field_37661 = "Heightmaps";
    public static final String field_37662 = "isLightOn";
    public static final String field_37663 = "sections";
    public static final String field_37664 = "BlockLight";
    public static final String field_37665 = "SkyLight";

    public ChunkSerializer() {
    }

    /** read(level, poiManager, storageInfo, pos, tag) */
    public static class_2839 method_12395(class_3218 level, class_4153 poi, class_9240 info, class_1923 pos, class_2487 tag) {
        return CbLevelStorage.method_12395(level, poi, info, pos, tag);
    }

    /** write(level, chunk) */
    public static class_2487 method_12410(class_3218 level, class_2791 chunk) {
        return CbLevelStorage.method_12410(level, chunk);
    }

    /** getChunkTypeFromTag(tag) */
    public static class_2808 method_12377(class_2487 tag) {
        return class_2852.method_12377(tag);
    }

    /** packOffsets(lists) */
    public static class_2499 method_12393(ShortList[] lists) {
        return class_2852.method_12393(lists);
    }
}
