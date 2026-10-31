package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.GpuFormat;
import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v26_2.facade.VertexFormatElement$Type;

/**
 * 26.1.2's VertexFormatElement construction on 26.2 (javap -c -p of both client jars). 26.1.2's element was a record
 * (id, index, type, normalized, count) its constructor only range-checked (id in [0; MAX_COUNT)), register(..) also
 * entered into BY_ID / ELEMENTS (refusing a taken id); 26.2's is a record (name, offset, GpuFormat) a VertexFormat
 * builder places (addAttribute), with no id, index or registry.
 * <ul>
 * <li>The 26.2 element: offset 0 (a 26.1.2 element had none; the format held offsets, and the give-back of
 *     VertexFormat.Builder.add places it at the running offset, rt/CbS205fBlaze3d#add). Name and format: ids 0..6 with
 *     the shape 26.1.2 registered there (javap 26.1.2 &lt;clinit&gt;: POSITION FLOAT x3, COLOR UBYTE x4 normalized, UV0
 *     FLOAT x2, UV1 / UV2 SHORT x2, NORMAL BYTE x3 normalized, LINE_WIDTH FLOAT x1) are that role as 26.2 names it and
 *     formats it (26.2 BufferBuilder.elementNames, DefaultVertexFormat's *_FORMAT: NORMAL is RGBA8_SNORM there, its 26.1.2
 *     formats padded the fourth byte), so 26.2's own lookups by name (VertexFormat.getElement, BufferBuilder) find them.
 *     Any other element is the colour GpuFormat of the same component type and count (FLOAT -&gt; FLOAT_32, UBYTE -&gt;
 *     UNORM_8 / UINT_8 by normalized, BYTE -&gt; SNORM_8 / SINT_8, USHORT -&gt; UNORM_16 / UINT_16, SHORT -&gt; SNORM_16 /
 *     SINT_16, UINT -&gt; UINT_32, INT -&gt; SINT_32), named "cb_element_&lt;id&gt;" (26.2 attributes take their name from
 *     the builder's add, not from the element). A shape 26.2 has no format for (normalized 32-bit integers, FLOAT
 *     normalized, a count outside 1..4) is refused with IllegalArgumentException: no 26.2 vertex array can read it.</li>
 * <li>id / index: kept per element object (identity, weak keys), as the 26.1.2 record held them; the role elements' ids are their
 *     26.1.2 ids. register's BY_ID / ELEMENTS are kept here (the seven roles pre-entered, as 26.1.2's &lt;clinit&gt;
 *     registered them), with 26.1.2's two refusals and messages.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbS205fElements {
    /** 26.1.2 VertexFormatElement.MAX_COUNT */
    public static final int MAX_COUNT = 32;

    private static final String[] ROLE_NAMES = {"Position", "Color", "UV0", "UV1", "UV2", "Normal", "LineWidth"};
    private static final GpuFormat[] ROLE_FORMATS = {GpuFormat.RGB32_FLOAT, GpuFormat.RGBA8_UNORM, GpuFormat.RG32_FLOAT,
        GpuFormat.RG16_SINT, GpuFormat.RG16_SINT, GpuFormat.RGBA8_SNORM, GpuFormat.R32_FLOAT};
    private static final VertexFormatElement$Type[] ROLE_TYPES = {VertexFormatElement$Type.FLOAT, VertexFormatElement$Type.UBYTE,
        VertexFormatElement$Type.FLOAT, VertexFormatElement$Type.SHORT, VertexFormatElement$Type.SHORT, VertexFormatElement$Type.BYTE,
        VertexFormatElement$Type.FLOAT};
    private static final boolean[] ROLE_NORMALIZED = {false, true, false, false, false, true, false};
    private static final int[] ROLE_COUNTS = {3, 4, 2, 2, 2, 3, 1};

    private static final Map<VertexFormatElement, int[]> ID_INDEX = new MapMaker().weakKeys().makeMap();
    private static final VertexFormatElement[] BY_ID = new VertexFormatElement[MAX_COUNT];
    private static final List<VertexFormatElement> ELEMENTS = Collections.synchronizedList(new ArrayList<>());

    static {
        int[] roleIndex = {0, 0, 0, 1, 2, 0, 0};
        for (int id = 0; id < ROLE_NAMES.length; id++) {
            VertexFormatElement e = new VertexFormatElement(ROLE_NAMES[id], 0, ROLE_FORMATS[id]);
            ID_INDEX.put(e, new int[] {id, roleIndex[id]});
            BY_ID[id] = e;
            ELEMENTS.add(e);
        }
    }

    private CbS205fElements() {
    }

    /** 26.1.2 new VertexFormatElement(id, index, type, normalized, count) */
    public static VertexFormatElement create(int id, int index, VertexFormatElement$Type type, boolean normalized, int count) {
        if (id < 0 || id >= MAX_COUNT) {
            throw new IllegalArgumentException("Element ID must be in range [0; " + MAX_COUNT + ")");
        }
        VertexFormatElement e;
        if (id < ROLE_NAMES.length && type == ROLE_TYPES[id] && normalized == ROLE_NORMALIZED[id] && count == ROLE_COUNTS[id]) {
            e = new VertexFormatElement(ROLE_NAMES[id], 0, ROLE_FORMATS[id]);
        } else {
            e = new VertexFormatElement("cb_element_" + id, 0, format(type, normalized, count));
        }
        ID_INDEX.put(e, new int[] {id, index});
        return e;
    }

    /** 26.1.2 VertexFormatElement.register(id, index, type, normalized, count) */
    public static VertexFormatElement register(int id, int index, VertexFormatElement$Type type, boolean normalized, int count) {
        VertexFormatElement e = create(id, index, type, normalized, count);
        synchronized (BY_ID) {
            if (BY_ID[id] != null) {
                throw new IllegalArgumentException("Duplicate element registration for: " + id);
            }
            BY_ID[id] = e;
        }
        ELEMENTS.add(e);
        return e;
    }

    /** 26.1.2 VertexFormatElement.byId(id) */
    public static VertexFormatElement byId(int id) {
        return BY_ID[id];
    }

    /** 26.1.2 VertexFormatElement.index(): as constructed; a 26.2 element of a role, that role's 26.1.2 index; else 0 */
    public static int index(VertexFormatElement e) {
        int[] own = ID_INDEX.get(e);
        if (own != null) {
            return own[1];
        }
        int role = role(e);
        return role == 3 ? 1 : role == 4 ? 2 : 0;
    }

    /**
     * 26.1.2's id of an element: as constructed, else its role's id by 26.2 name and format, else -1 (an element 26.1.2
     * code never built and that stands for no 26.1.2 role)
     */
    public static int id(VertexFormatElement e) {
        int[] own = ID_INDEX.get(e);
        return own != null ? own[0] : role(e);
    }

    /** 26.1.2 VertexFormatElement.elementsFromMask(mask): the registered elements whose id bit is set, in registration order */
    public static Stream<VertexFormatElement> elementsFromMask(int mask) {
        List<VertexFormatElement> snapshot;
        synchronized (ELEMENTS) {
            snapshot = new ArrayList<>(ELEMENTS);
        }
        return snapshot.stream().filter(e -> (mask & 1 << id(e)) != 0);
    }

    /** 26.1.2 VertexFormatElement.MAX_COUNT read with getstatic (javac inlines the constant; bytecode that reads the field lands here) */
    public static int maxCount() {
        return MAX_COUNT;
    }

    private static int role(VertexFormatElement e) {
        for (int id = 0; id < ROLE_NAMES.length; id++) {
            if (ROLE_NAMES[id].equals(e.name()) && ROLE_FORMATS[id] == e.format()) {
                return id;
            }
        }
        return -1;
    }

    private static GpuFormat format(VertexFormatElement$Type type, boolean normalized, int count) {
        GpuFormat.ComponentType component = switch (type) {
            case FLOAT -> normalized ? null : GpuFormat.ComponentType.FLOAT_32;
            case UBYTE -> normalized ? GpuFormat.ComponentType.UNORM_8 : GpuFormat.ComponentType.UINT_8;
            case BYTE -> normalized ? GpuFormat.ComponentType.SNORM_8 : GpuFormat.ComponentType.SINT_8;
            case USHORT -> normalized ? GpuFormat.ComponentType.UNORM_16 : GpuFormat.ComponentType.UINT_16;
            case SHORT -> normalized ? GpuFormat.ComponentType.SNORM_16 : GpuFormat.ComponentType.SINT_16;
            case UINT -> normalized ? null : GpuFormat.ComponentType.UINT_32;
            case INT -> normalized ? null : GpuFormat.ComponentType.SINT_32;
        };
        if (component != null) {
            for (GpuFormat f : GpuFormat.values()) {
                if (f.componentType() == component && f.componentCount() == count && f.hasColorAspect()) {
                    return f;
                }
            }
        }
        throw new IllegalArgumentException("26.2 has no vertex format for " + count + "x" + type + (normalized ? " normalized" : ""));
    }
}
