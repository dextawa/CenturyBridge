package top.dext.centurybridge.step.v1_21.rt;

import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.mixin.CbVertexFormatAccess;

/**
 * 1.20.6's vertex formats and elements on 1.21's.
 * <ul>
 * <li>VertexFormatElement was a class built with (index, type, usage, count); 1.21's is a record registered by id
 *     (at most 32), and VertexFormat masks and offsets by that id. A 1.20.6-made element is the registered one of
 *     the same index, type, usage and count, else one newly registered with the next free id. Its getters are the
 *     record's components; clearBufferState(i) disabled attribute i for every usage, as 1.21's VertexFormat does
 *     directly.</li>
 * <li>Padding was an element of usage PADDING in 1.20.6; 1.21 pads with VertexFormat.Builder.padding(bytes) and its
 *     Usage has no PADDING (an enum constant cannot be added). DefaultVertexFormat.ELEMENT_PADDING is a marker
 *     element CenturyBridge knows: a 1.20.6 format built with it pads there; asked for its usage it says it has
 *     none in 1.21.</li>
 * <li>VertexFormat(ImmutableMap) of 1.20.6: 1.21's builder over the map in order. getElements /
 *     getElementAttributeNames answered ImmutableLists, getIntegerSize the size in ints.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbRenderFormats {

    /** the restored virtual clearBufferState, implemented on the native record by CbVertexFormatElementMixin */
    public interface LegacyClearState {
        void method_22653(int elementIndex);
    }

    private CbRenderFormats() {
    }

    // ------------------------------------------------------------------ DefaultVertexFormat.ELEMENT_*

    /** ELEMENT_POSITION */
    public static final net.minecraft.class_296 field_1587 = net.minecraft.class_296.field_52107;

    /** ELEMENT_COLOR */
    public static final net.minecraft.class_296 field_1581 = net.minecraft.class_296.field_52108;

    /** ELEMENT_UV0 */
    public static final net.minecraft.class_296 field_1591 = net.minecraft.class_296.field_52109;

    /** ELEMENT_UV1 */
    public static final net.minecraft.class_296 field_1583 = net.minecraft.class_296.field_52111;

    /** ELEMENT_UV2 */
    public static final net.minecraft.class_296 field_20886 = net.minecraft.class_296.field_52112;

    /** ELEMENT_NORMAL */
    public static final net.minecraft.class_296 field_1579 = net.minecraft.class_296.field_52113;

    /** ELEMENT_UV (1.20.6's was ELEMENT_UV0 itself, as 1.21's UV is UV0) */
    public static final net.minecraft.class_296 field_29335 = net.minecraft.class_296.field_52110;

    /**
     * ELEMENT_PADDING: one byte of padding. An unregistered 1.21 element (id 31, a generic byte) that only stands
     * for padding where a 1.20.6 format is built with it; it never enters a 1.21 format itself.
     */
    public static final net.minecraft.class_296 field_1578 = new net.minecraft.class_296(31, 0, net.minecraft.class_296.class_297.field_1621,
        net.minecraft.class_296.class_298.field_20782, 1);

    // ------------------------------------------------------------------ VertexFormatElement

    /** new VertexFormatElement(index, type, usage, count) of 1.20.6: the registered element of that shape, or a new one */
    public static synchronized net.minecraft.class_296 cbElement(int index, net.minecraft.class_296.class_297 type,
                                                                 net.minecraft.class_296.class_298 usage, int count) {
        int free = -1;
        for (int id = 0; id < 32; id++) {
            net.minecraft.class_296 e = net.minecraft.class_296.method_60844(id);
            if (e == null) {
                if (free < 0) {
                    free = id;
                }
            } else if (e.comp_2843() == index && e.comp_2844() == type && e.comp_2845() == usage && e.comp_2846() == count) {
                return e;
            }
        }
        if (free < 0) {
            throw new IllegalStateException("1.21 registers at most 32 vertex format elements, and all are taken: a 1.20.6 element (index "
                + index + ", " + type + ", " + usage + ", " + count + ") has no id");
        }
        if (index == 0 || usage == net.minecraft.class_296.class_298.field_1636) {
            // what 1.21's constructor accepts: registered through 1.21's own register (method_60845), which every
            // later release's step keeps (26.2: CbS205fElements#register) -- never its private BY_ID / ELEMENTS
            return net.minecraft.class_296.method_60845(free, index, type, usage, count);
        }
        // The native constructor rejects a repeated non-UV usage at index > 0. Register with UV, then restore the
        // actual usage through the record field (the registered object is the one byId answers).
        net.minecraft.class_296 element = net.minecraft.class_296.method_60845(free, index, type,
            net.minecraft.class_296.class_298.field_1636, count);
        try {
            usageField().set(element, usage);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("cannot retain duplicate vertex usage " + usage, e);
        }
        return element;
    }

    /** looked up on use only: a failure must not take the whole class (every other format bridge) with it */
    private static java.lang.reflect.Field usageField() {
        try {
            java.lang.reflect.Field field = net.minecraft.class_296.class.getDeclaredField("comp_2845");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("1.21 VertexFormatElement has no usage field comp_2845", e);
        }
    }

    /** the native record id of that same shape, used by a converted subclass's super constructor */
    public static int cbElementId(int index, net.minecraft.class_296.class_297 type,
                                  net.minecraft.class_296.class_298 usage, int count) {
        return cbElement(index, type, usage, count).comp_2842();
    }

    /** getUsage() */
    public static net.minecraft.class_296.class_298 cbUsage(net.minecraft.class_296 element) {
        if (element == field_1578) {
            throw new UnsupportedOperationException("1.20.6's padding element has usage PADDING, which 1.21's VertexFormatElement.Usage does not have");
        }
        return element.comp_2845();
    }

    /** getIndex() */
    public static int cbIndex(net.minecraft.class_296 element) {
        return element.comp_2843();
    }

    /** getType() */
    public static net.minecraft.class_296.class_297 cbType(net.minecraft.class_296 element) {
        return element.comp_2844();
    }

    /** getCount() */
    public static int cbCount(net.minecraft.class_296 element) {
        return element.comp_2846();
    }

    /** getByteSize() */
    public static int cbByteSize(net.minecraft.class_296 element) {
        return element.method_60847();
    }

    /** isPosition() */
    public static boolean cbIsPosition(net.minecraft.class_296 element) {
        return element.comp_2845() == net.minecraft.class_296.class_298.field_1633;
    }

    /** clearBufferState(elementIndex): the usage's clear, which disabled that attribute for every usage */
    public static void cbClearBufferState(net.minecraft.class_296 element, int elementIndex) {
        com.mojang.blaze3d.platform.GlStateManager._disableVertexAttribArray(elementIndex);
    }

    /** VertexFormat.clearBufferState: virtually dispatch each element's restored method, as 1.20.6 did */
    public static void cbClearFormat(net.minecraft.class_293 format) {
        java.util.List<net.minecraft.class_296> elements = ((CbVertexFormatAccess) (Object) format).cb$elements();
        for (int i = 0; i < elements.size(); i++) {
            ((LegacyClearState) (Object) elements.get(i)).method_22653(i);
        }
    }

    /** Usage.clearBufferState(index, elementIndex) */
    public static void cbUsageClearBufferState(net.minecraft.class_296.class_298 usage, int index, int elementIndex) {
        com.mojang.blaze3d.platform.GlStateManager._disableVertexAttribArray(elementIndex);
    }

    /** Usage.getName(): its display name, which 1.21's toString() answers */
    public static String cbUsageName(net.minecraft.class_296.class_298 usage) {
        return usage.toString();
    }

    /** Type.getName(): its display name, which 1.21's toString() answers */
    public static String cbTypeName(net.minecraft.class_296.class_297 type) {
        return type.toString();
    }

    // ------------------------------------------------------------------ VertexFormat

    /** new VertexFormat(elements) of 1.20.6: the native constructor over the old lists, including duplicate usages */
    public static net.minecraft.class_293 cbNewFormat(ImmutableMap<String, net.minecraft.class_296> elements) {
        // 1.21's builder (method_60833) places exactly what 1.20.6's constructor did: each element at the running
        // offset (method_60842 add), a padding element only advancing it (method_60841 padding); it checks no usage,
        // so V's duplicate usages pass. The builder is what later releases keep (26.2: CbS205fBlaze3d builder / add /
        // padding), not the private constructor
        net.minecraft.class_293.class_9803 b = net.minecraft.class_293.method_60833();
        for (Map.Entry<String, net.minecraft.class_296> e : elements.entrySet()) {
            if (e.getValue() == field_1578) {
                b.method_60841(e.getValue().method_60847());
            } else {
                b.method_60842(e.getKey(), e.getValue());
            }
        }
        return b.method_60840();
    }

    /** the super(elements) of a 1.20.6 VertexFormat subclass: the elements 1.21's constructor takes (padding is none) */
    public static List<net.minecraft.class_296> cbFormatElements(ImmutableMap<String, net.minecraft.class_296> elements) {
        ImmutableList.Builder<net.minecraft.class_296> list = ImmutableList.builder();
        for (Map.Entry<String, net.minecraft.class_296> e : elements.entrySet()) {
            if (e.getValue() != field_1578) {
                list.add(e.getValue());
            }
        }
        return list.build();
    }

    /** the super(elements) of a 1.20.6 VertexFormat subclass: their names */
    public static List<String> cbFormatNames(ImmutableMap<String, net.minecraft.class_296> elements) {
        ImmutableList.Builder<String> list = ImmutableList.builder();
        for (Map.Entry<String, net.minecraft.class_296> e : elements.entrySet()) {
            if (e.getValue() != field_1578) {
                list.add(e.getKey());
            }
        }
        return list.build();
    }

    /** the super(elements) of a 1.20.6 VertexFormat subclass: each element's offset, padding counted */
    public static it.unimi.dsi.fastutil.ints.IntList cbFormatOffsets(ImmutableMap<String, net.minecraft.class_296> elements) {
        it.unimi.dsi.fastutil.ints.IntArrayList offsets = new it.unimi.dsi.fastutil.ints.IntArrayList();
        int offset = 0;
        for (Map.Entry<String, net.minecraft.class_296> e : elements.entrySet()) {
            if (e.getValue() != field_1578) {
                offsets.add(offset);
            }
            offset += e.getValue().method_60847();
        }
        return offsets;
    }

    /** the super(elements) of a 1.20.6 VertexFormat subclass: the vertex size, padding counted */
    public static int cbFormatSize(ImmutableMap<String, net.minecraft.class_296> elements) {
        int size = 0;
        for (net.minecraft.class_296 e : elements.values()) {
            size += e.method_60847();
        }
        return size;
    }

    /** getElements() of 1.20.6: an ImmutableList */
    public static ImmutableList<net.minecraft.class_296> cbImmutableElements(List<net.minecraft.class_296> elements) {
        return ImmutableList.copyOf(elements);
    }

    /** getElementAttributeNames() of 1.20.6: an ImmutableList */
    public static ImmutableList<String> cbImmutableNames(List<String> names) {
        return ImmutableList.copyOf(names);
    }

    /** VertexFormat.getIntegerSize(), as a 1.20.6 format subclass declares it */
    public interface CbLegacyIntegerSize {
        int method_1359();
    }

    /** format.getIntegerSize(), called 1.20.6's way */
    public static int cbIntegerSize(net.minecraft.class_293 format) {
        if (format instanceof CbLegacyIntegerSize own) {
            return own.method_1359();
        }
        return cbIntegerSizeBody(format);
    }

    /** getIntegerSize() of 1.20.6: the vertex size in ints */
    public static int cbIntegerSizeBody(net.minecraft.class_293 format) {
        return format.method_1362() / 4;
    }
}
