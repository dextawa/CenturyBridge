package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_2814;
import net.minecraft.class_2816;
import net.minecraft.class_2834;
import net.minecraft.class_2835;
import net.minecraft.class_2837;
import net.minecraft.class_2841;
import net.minecraft.class_6564;
import net.minecraft.class_7522;

/**
 * 1.21.1's Palette.copy() on 1.21.2's copy(PaletteResize). 1.21.1's palettes kept the resize handler they were made
 * with and copy() handed it to the copy; 1.21.2's still keep it (same fields) but copy takes the handler the copy
 * reports to -- the new container, where 1.21.1's copy went on reporting to the old one. A 1.21.1 call (and a
 * 1.21.1 override's super call entered without a frame, frameDefaults) passes the palette's own handler, 1.21.1's
 * value: the hash map, linear and single-value palettes' field (the layer's access widener); the global palette
 * never resizes and ignores it (1.21.2's copy returns itself), as does a mod's own palette, whose 1.21.1 copy()
 * answers 1.21.2's copy (the override hook).
 */
public final class CbLevelPalette {
    private CbLevelPalette() {
    }

    /** the resize handler 1.21.1's copy() handed on: the palette's own, where it keeps one */
    public static <T> class_2835<T> resize(class_2837<T> palette) {
        if (palette instanceof class_2814<T> p) {
            return p.field_12825;
        }
        if (palette instanceof class_2834<T> p) {
            return p.field_12905;
        }
        if (palette instanceof class_6564<T> p) {
            return p.field_34575;
        }
        return null;
    }

    public static <T> class_2837<T> method_39956(class_2837<T> palette) {
        return palette.method_39956(resize(palette));
    }

    public static <T> class_2837<T> method_39956(class_2816<T> palette) {
        return palette.method_39956(resize(palette));
    }

    public static <T> class_2837<T> method_39956(class_2814<T> palette) {
        return palette.method_39956(resize(palette));
    }

    public static <T> class_2837<T> method_39956(class_2834<T> palette) {
        return palette.method_39956(resize(palette));
    }

    public static <T> class_2837<T> method_39956(class_6564<T> palette) {
        return palette.method_39956(resize(palette));
    }

    /**
     * PalettedContainer.Data.copy() (package-private record, reached by mixin invokers): 1.21.1 copied the storage
     * and the palette, the palette with its own resize handler (copy()); 1.21.2's copy(resize) hands the palette copy
     * the handler it is given -- here the palette's own, 1.21.1's value
     */
    public static <T> class_2841.class_6561<T> method_44338(class_2841.class_6561<T> data) {
        return data.method_44338(resize(data.comp_119()));
    }

    /**
     * PalettedContainerRO.copy(), abstract since 1.21.2, for a 1.21.1 mod's own read-only container (abstractFills):
     * what 1.21.2's PalettedContainer.copy() answers -- a container of the same registry and strategy holding the same
     * value at every position -- made from the mod's recreate() (an empty container of its registry and strategy)
     * and its get(x, y, z). The strategy's size is the cube of its edge (1 << sizeBits * 3).
     */
    public static <T> class_2841<T> copy(class_7522<T> container) {
        class_2841<T> copy = container.method_44350();
        int bits = Integer.numberOfTrailingZeros(copy.field_34561.method_38312()) / 3;
        int edge = 1 << bits;
        for (int y = 0; y < edge; y++) {
            for (int z = 0; z < edge; z++) {
                for (int x = 0; x < edge; x++) {
                    copy.method_35321(x, y, z, container.method_12321(x, y, z));
                }
            }
        }
        return copy;
    }
}
