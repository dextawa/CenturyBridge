package top.dext.centurybridge.step.v26_1.rt;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

/**
 * ChunkSectionsToRender's draws (generic drift): 1.21.11 kept one list of draws per layer and drew each list in one
 * drawMultipleIndexed call; 26.1 splits a layer's draws into groups (an int-keyed map of lists, one call per group --
 * its sections live in several uber buffers). A 1.21.11 layer list is one group (key 0), drawn in one call as
 * 1.21.11 drew it; read back, a layer's groups are one list, group after group in the order 26.1 draws them (a copy:
 * 26.1's record keeps its groups). Both converters are adaptive: a map already in the other shape passes through.
 */
@Environment(EnvType.CLIENT)
public final class CbWallChunkDraws {

    private CbWallChunkDraws() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static EnumMap groups(EnumMap layers) {
        if (layers == null) {
            return null;
        }
        boolean legacy = false;
        for (Object v : layers.values()) {
            if (v instanceof List) {
                legacy = true;
                break;
            }
        }
        if (!legacy) {
            return layers;
        }
        EnumMap out = new EnumMap<>(ChunkSectionLayer.class);
        for (Map.Entry e : (Iterable<Map.Entry>) layers.entrySet()) {
            Object v = e.getValue();
            if (v instanceof List list) {
                Int2ObjectOpenHashMap group = new Int2ObjectOpenHashMap<>();
                group.put(0, list);
                out.put(e.getKey(), group);
            } else {
                out.put(e.getKey(), v);
            }
        }
        return out;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static EnumMap lists(EnumMap layers) {
        if (layers == null) {
            return null;
        }
        boolean grouped = false;
        for (Object v : layers.values()) {
            if (v instanceof Int2ObjectOpenHashMap) {
                grouped = true;
                break;
            }
        }
        if (!grouped) {
            return layers;
        }
        EnumMap out = new EnumMap<>(ChunkSectionLayer.class);
        for (Map.Entry e : (Iterable<Map.Entry>) layers.entrySet()) {
            Object v = e.getValue();
            if (v instanceof Int2ObjectOpenHashMap groups) {
                List all = new ArrayList<>();
                for (Object list : groups.values()) {
                    all.addAll((List) list);
                }
                out.put(e.getKey(), all);
            } else {
                out.put(e.getKey(), v);
            }
        }
        return out;
    }
}
