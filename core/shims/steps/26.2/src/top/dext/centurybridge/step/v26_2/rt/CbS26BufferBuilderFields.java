package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 26.1.2 BufferBuilder fields 26.2 holds in another form (javap of both constructors and addVertex):
 * <ul>
 * <li>fastFormat = format == ENTITY || format == BLOCK; 26.2 keeps the two halves, blockFormat (format == BLOCK) and
 *     entityFormat (format == ENTITY, 26.1.2's fullFormat, a fieldRenames entry). 26.1.2's addVertex took its fast path
 *     when fastFormat held and chose the entity layout by fullFormat; 26.2's takes the block path on blockFormat, else the
 *     entity path on entityFormat. A write of fastFormat sets blockFormat: fast and not the entity format (26.1.2 only
 *     ever held fast for those two formats, fullFormat telling them apart).</li>
 * <li>offsetsByElement[id] = the element's byte offset in the vertex, -1 when the format lacks it (26.1.2
 *     VertexFormat.getOffsetsByElement, 32 slots by element id). 26.2 holds elements[semantic id] (Position, Color, UV0,
 *     UV1, UV2, Normal, LineWidth: ids 0..6, the ids 26.1.2's VertexFormatElement constants carried) and reads each
 *     element's offset(); the array is read off them. A write (26.1.2 code setting a builder up for a format) refills
 *     elements[] from the builder's format, as 26.2's constructor does.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbS26BufferBuilderFields {
    private CbS26BufferBuilderFields() {}

    /** 26.1.2 VertexFormatElement.MAX_COUNT: the slots of offsetsByElement */
    private static final int MAX_COUNT = 32;

    public static boolean fastFormat(BufferBuilder b) {
        return b.blockFormat || b.entityFormat;
    }

    public static void setFastFormat(BufferBuilder b, boolean fast) {
        b.blockFormat = fast && b.format != DefaultVertexFormat.ENTITY;
    }

    public static int[] offsetsByElement(BufferBuilder b) {
        int[] out = new int[MAX_COUNT];
        Arrays.fill(out, -1);
        VertexFormatElement[] elements = b.elements;
        for (int id = 0; id < elements.length && id < MAX_COUNT; id++) {
            if (elements[id] != null) {
                out[id] = elements[id].offset();
            }
        }
        return out;
    }

    public static void setOffsetsByElement(BufferBuilder b, int[] offsets) {
        VertexFormatElement[] elements = b.elements;
        for (int id = 0; id < elements.length; id++) {
            elements[id] = b.format.getElement(BufferBuilder.elementNames[id]);
        }
    }

    // ---- 26.1.2 VertexFormatElement.id() / mask(), VertexFormat.getElementsMask() / getOffsetsByElement(): gone at 26.2
    // (javap: present 1.21.5 .. 26.1.2). 26.1.2 elements carried a registered id (POSITION 0, COLOR 1, UV0 2, UV1 3, UV2 4,
    // NORMAL 5, LINE_WIDTH 6; register(..) took further ids below MAX_COUNT); 26.2 elements are named, and BufferBuilder's
    // elementNames lists the seven standard names in that same id order. A name outside them gets the next free id on first
    // sight, as 26.1.2's register handed ids out (refused at MAX_COUNT, as register refused).

    private static final Map<String, Integer> EXTRA_IDS = new ConcurrentHashMap<>();

    /** 26.1.2 VertexFormatElement.id() */
    public static int id(VertexFormatElement e) {
        String[] names = BufferBuilder.elementNames;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(e.name())) {
                return i;
            }
        }
        return EXTRA_IDS.computeIfAbsent(e.name(), n -> {
            int id = BufferBuilder.elementNames.length + EXTRA_IDS.size();
            if (id >= MAX_COUNT) {
                throw new IllegalArgumentException("Element ID must be in range [0; " + MAX_COUNT + ")");
            }
            return id;
        });
    }

    /** 26.1.2 VertexFormatElement.mask() */
    public static int mask(VertexFormatElement e) {
        return 1 << id(e);
    }

    /** 26.1.2 VertexFormat.getElementsMask(): the masks of the format's elements */
    public static int getElementsMask(VertexFormat format) {
        int m = 0;
        for (VertexFormatElement e : format.getElements()) {
            m |= mask(e);
        }
        return m;
    }

    /** 26.1.2 VertexFormat.getOffsetsByElement(): each element's offset at its id, -1 where the format lacks the id */
    public static int[] getOffsetsByElement(VertexFormat format) {
        int[] out = new int[MAX_COUNT];
        Arrays.fill(out, -1);
        for (VertexFormatElement e : format.getElements()) {
            out[id(e)] = e.offset();
        }
        return out;
    }

    // ---- 26.1.2 VertexFormatElement constants (gone at 26.2, javap: 26.2 has no static elements). 26.1.2 registered
    // POSITION (FLOAT x3), COLOR (UBYTE x4 normalized), UV0 = UV (FLOAT x2), UV1 / UV2 (SHORT x2), NORMAL (BYTE x3 normalized),
    // LINE_WIDTH (FLOAT x1); 26.2's DefaultVertexFormat adds the same roles by name with the GpuFormats below. A 26.1.2 element
    // had no offset of its own (the format held it): offset 0. Lookups that took a constant as a key go by name (id() here,
    // VertexFormat.getOffset / contains in rt/CbS205fBlaze3d), since record equality includes the offset.

    private static final VertexFormatElement POSITION = new VertexFormatElement("Position", 0, GpuFormat.RGB32_FLOAT);
    private static final VertexFormatElement COLOR = new VertexFormatElement("Color", 0, GpuFormat.RGBA8_UNORM);
    private static final VertexFormatElement UV0 = new VertexFormatElement("UV0", 0, GpuFormat.RG32_FLOAT);
    private static final VertexFormatElement UV1 = new VertexFormatElement("UV1", 0, GpuFormat.RG16_SINT);
    private static final VertexFormatElement UV2 = new VertexFormatElement("UV2", 0, GpuFormat.RG16_SINT);
    private static final VertexFormatElement NORMAL = new VertexFormatElement("Normal", 0, GpuFormat.RGBA8_SNORM);
    private static final VertexFormatElement LINE_WIDTH = new VertexFormatElement("LineWidth", 0, GpuFormat.R32_FLOAT);

    public static VertexFormatElement position() {
        return POSITION;
    }

    public static VertexFormatElement color() {
        return COLOR;
    }

    /** UV0, and 26.1.2's UV alias of it (the same object) */
    public static VertexFormatElement uv0() {
        return UV0;
    }

    public static VertexFormatElement uv1() {
        return UV1;
    }

    public static VertexFormatElement uv2() {
        return UV2;
    }

    public static VertexFormatElement normal() {
        return NORMAL;
    }

    public static VertexFormatElement lineWidth() {
        return LINE_WIDTH;
    }
}
