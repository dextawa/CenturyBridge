package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.List;
import net.minecraft.class_2359;
import net.minecraft.class_2540;
import net.minecraft.class_2814;
import net.minecraft.class_2816;
import net.minecraft.class_2834;
import net.minecraft.class_2835;
import net.minecraft.class_2837;
import net.minecraft.class_6564;

/**
 * Palettes for 1.21.8 code. A 1.21.8 palette held the registry its ids index and the resize handler it reported growth
 * to (javap -c 1.21.8 HashMapPalette / LinearPalette / SingleValuePalette: both stored by the constructor; GlobalPalette:
 * the registry); its idFor(value), read(buf), write(buf) and getSerializedSize() used them. 1.21.9's palettes keep
 * neither: every such call takes them (idFor(value, resize), read(buf, registry), ...). The layer gives the two fields
 * back under 1.21.8's names (CbPalette*FieldsMixin) and fills them wherever a palette is made with them known:
 * <ul>
 * <li>by PalettedContainer, with its strategy's registry and itself as the handler, as 1.21.8's container built its
 * palettes (CbPalettedContainerTagMixin);</li>
 * <li>by a 1.21.8 call that names them: the constructors, the static create methods, Palette.Factory.create and
 * copy(resize), each redirected here.</li>
 * </ul>
 * 1.21.8's calls then hand 1.21.9's methods the palette's own registry and handler, as 1.21.8's bodies did. A palette
 * 1.21.9 code made outside a container has none: its handler is 1.21.9's own PaletteResize.noResizeExpected() (the
 * one 1.21.9 hands such palettes), and a registry call on it fails naming the cause.
 */
public final class CbPalettes {
    private CbPalettes() {
    }

    /** a vanilla palette with 1.21.8's registry / resize fields (CbPalette*FieldsMixin) */
    public interface Legacy {
        class_2359<?> cb$registry();

        class_2835<?> cb$resize();

        void cb$legacy(class_2359<?> registry, class_2835<?> resize);
    }

    /** gives a palette 1.21.8's registry and resize handler; returns it */
    @SuppressWarnings("rawtypes")
    public static <P extends class_2837> P tag(P palette, class_2359<?> registry, class_2835<?> resize) {
        if (palette instanceof Legacy legacy) {
            legacy.cb$legacy(registry, resize);
        }
        return palette;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static class_2835 resize(class_2837 palette) {
        class_2835 resize = palette instanceof Legacy legacy ? legacy.cb$resize() : null;
        return resize != null ? resize : class_2835.method_74153();
    }

    @SuppressWarnings("rawtypes")
    static class_2359 registry(class_2837 palette) {
        class_2359 registry = palette instanceof Legacy legacy ? legacy.cb$registry() : null;
        if (registry == null && palette instanceof class_2816 global) {
            registry = global.field_12828;
        }
        if (registry == null) {
            throw new IllegalStateException("a 1.21.8 call on " + palette.getClass().getName() + " needs the registry the palette "
                + "indexes, and this palette was made by 1.21.9 code outside a PalettedContainer, which gives it none (CenturyBridge)");
        }
        return registry;
    }

    /** Palette.idFor(value): 1.21.9's idFor with the palette's own resize handler */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static int method_12291(class_2837 palette, Object value) {
        return palette.method_12291(value, resize(palette));
    }

    /** Palette.read(buf): 1.21.9's read with the palette's own registry */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void method_12289(class_2837 palette, class_2540 buf) {
        palette.method_12289(buf, registry(palette));
    }

    /** Palette.write(buf): 1.21.9's write with the palette's own registry */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void method_12287(class_2837 palette, class_2540 buf) {
        palette.method_12287(buf, registry(palette));
    }

    /** Palette.getSerializedSize(): 1.21.9's with the palette's own registry */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static int method_12290(class_2837 palette) {
        return palette.method_12290(registry(palette));
    }

    /** Palette.copy(resize): 1.21.9's copy, carrying the registry, with the handler 1.21.8 code handed it */
    @SuppressWarnings("rawtypes")
    public static class_2837 method_39956(class_2837 palette, class_2835 resize) {
        class_2359 registry = palette instanceof Legacy legacy ? legacy.cb$registry() : null;
        return tag(palette.method_39956(), registry, resize);
    }

    /** Palette.Factory.create(bits, registry, resize, values) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_2837 create(class_2837.class_6559 factory, int bits, class_2359 registry, class_2835 resize, List values) {
        return tag(factory.create(bits, values), registry, resize);
    }

    /** HashMapPalette.create(bits, registry, resize, values) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_2837 method_38287(int bits, class_2359 registry, class_2835 resize, List values) {
        return tag(class_2814.method_38287(bits, values), registry, resize);
    }

    /** LinearPalette.create(bits, registry, resize, values) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_2837 method_38295(int bits, class_2359 registry, class_2835 resize, List values) {
        return tag(class_2834.method_38295(bits, values), registry, resize);
    }

    /** SingleValuePalette.create(bits, registry, resize, values) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_2837 method_38316(int bits, class_2359 registry, class_2835 resize, List values) {
        return tag(class_6564.method_38316(bits, values), registry, resize);
    }

    /** GlobalPalette.create(bits, registry, resize, values): 1.21.8's was new GlobalPalette(registry) (javap -c) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_2837 method_38286(int bits, class_2359 registry, class_2835 resize, List values) {
        return new class_2816(registry);
    }

    /** new HashMapPalette(registry, bits, resize, values) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_2814 hashMap(class_2359 registry, int bits, class_2835 resize, List values) {
        return tag(new class_2814(bits, values), registry, resize);
    }

    /** new HashMapPalette(registry, bits, resize) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_2814 hashMap(class_2359 registry, int bits, class_2835 resize) {
        return tag(new class_2814(bits), registry, resize);
    }

    /** new SingleValuePalette(registry, resize, values) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_6564 singleValue(class_2359 registry, class_2835 resize, List values) {
        return tag(new class_6564(values), registry, resize);
    }

    /** 1.21.8's copy(resize) of a palette class a converted mod wrote (overrideHooks bridge) */
    public interface LegacyCopy {
        @SuppressWarnings("rawtypes")
        class_2837 method_39956(class_2835 resize);
    }

    /** the 1.21.9 copy() of such a class: its 1.21.8 override with the palette's own resize handler */
    @SuppressWarnings("rawtypes")
    public static class_2837 copy(LegacyCopy palette) {
        class_2837 self = (class_2837) palette;
        class_2835 resize = palette instanceof Legacy legacy ? legacy.cb$resize() : null;
        class_2359 registry = palette instanceof Legacy legacy ? legacy.cb$registry() : null;
        return tag(palette.method_39956(resize != null ? resize : resize(self)), registry, resize);
    }
}
