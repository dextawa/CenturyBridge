package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v26_1.facade.VertexFormatElement$Usage;

/**
 * 1.21.11 vertex format elements carried a Usage; 26.1's carry whether they are normalized, which is what the
 * usage decided in 1.21.11's GL setup: NORMAL and COLOR elements were passed normalized, POSITION, UV and
 * GENERIC as they are (floats) or as integers. An element a 1.21.11 call made keeps its usage; 26.1's own
 * elements answer 1.21.11's (POSITION, COLOR, the UVs, NORMAL, LINE_WIDTH generic); any other answers by its
 * normalization (COLOR / GENERIC).
 */
@Environment(EnvType.CLIENT)
public final class CbWallVertexFormats {

    private static final Map<VertexFormatElement, VertexFormatElement$Usage> USAGES = new MapMaker().weakKeys().makeMap();

    private CbWallVertexFormats() {
    }

    public static boolean normalized(VertexFormatElement$Usage usage) {
        return usage == VertexFormatElement$Usage.NORMAL || usage == VertexFormatElement$Usage.COLOR;
    }

    /** 1.21.11 new VertexFormatElement(id, index, type, usage, count) */
    public static VertexFormatElement element(int id, int index, VertexFormatElement.Type type, VertexFormatElement$Usage usage, int count) {
        VertexFormatElement element = new VertexFormatElement(id, index, type, normalized(usage), count);
        USAGES.put(element, usage);
        return element;
    }

    /** 1.21.11 VertexFormatElement.register(id, index, type, usage, count) */
    public static VertexFormatElement register(int id, int index, VertexFormatElement.Type type, VertexFormatElement$Usage usage, int count) {
        VertexFormatElement element = VertexFormatElement.register(id, index, type, normalized(usage), count);
        USAGES.put(element, usage);
        return element;
    }

    /** 1.21.11 VertexFormatElement.usage() */
    public static VertexFormatElement$Usage usage(VertexFormatElement element) {
        VertexFormatElement$Usage own = USAGES.get(element);
        if (own != null) {
            return own;
        }
        if (element == VertexFormatElement.POSITION) {
            return VertexFormatElement$Usage.POSITION;
        }
        if (element == VertexFormatElement.COLOR) {
            return VertexFormatElement$Usage.COLOR;
        }
        if (element == VertexFormatElement.UV0 || element == VertexFormatElement.UV1 || element == VertexFormatElement.UV2) {
            return VertexFormatElement$Usage.UV;
        }
        if (element == VertexFormatElement.NORMAL) {
            return VertexFormatElement$Usage.NORMAL;
        }
        if (element == VertexFormatElement.LINE_WIDTH) {
            return VertexFormatElement$Usage.GENERIC;
        }
        return element.normalized() ? VertexFormatElement$Usage.COLOR : VertexFormatElement$Usage.GENERIC;
    }
}
