package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's BlendMode (net/minecraft/class_277, classRenames), gone in 1.21.2 (blending is a render-state shard there):
 * a GL blend state that applies itself through RenderSystem, skipping the GL calls when it is the one last applied.
 * 1.21.1's fields (public: an access widener never reaches a CenturyBridge class) and bodies; the RenderSystem calls it
 * makes are all 1.21.2's too.
 */
@Environment(EnvType.CLIENT)
public class BlendMode {

    /** lastApplied */
    public static BlendMode field_1484;

    /** srcColorFactor */
    public final int field_1491;

    /** srcAlphaFactor */
    public final int field_1490;

    /** dstColorFactor */
    public final int field_1489;

    /** dstAlphaFactor */
    public final int field_1488;

    /** blendFunc (equation) */
    public final int field_1486;

    /** separateBlend */
    public final boolean field_1487;

    /** opaque */
    public final boolean field_1485;

    public BlendMode(boolean separateBlend, boolean opaque, int srcColorFactor, int dstColorFactor, int srcAlphaFactor, int dstAlphaFactor,
                      int blendFunc) {
        this.field_1487 = separateBlend;
        this.field_1491 = srcColorFactor;
        this.field_1489 = dstColorFactor;
        this.field_1490 = srcAlphaFactor;
        this.field_1488 = dstAlphaFactor;
        this.field_1485 = opaque;
        this.field_1486 = blendFunc;
    }

    /** opaque: no blending */
    public BlendMode() {
        this(false, true, 1, 0, 1, 0, 32774);
    }

    public BlendMode(int srcFactor, int dstFactor, int blendFunc) {
        this(false, false, srcFactor, dstFactor, srcFactor, dstFactor, blendFunc);
    }

    public BlendMode(int srcColorFactor, int dstColorFactor, int srcAlphaFactor, int dstAlphaFactor, int blendFunc) {
        this(true, false, srcColorFactor, dstColorFactor, srcAlphaFactor, dstAlphaFactor, blendFunc);
    }

    /** apply() */
    public void method_1244() {
        if (this.equals(field_1484)) {
            return;
        }
        if (field_1484 == null || this.field_1485 != field_1484.method_1245()) {
            field_1484 = this;
            if (this.field_1485) {
                com.mojang.blaze3d.systems.RenderSystem.disableBlend();
                return;
            }
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        }
        com.mojang.blaze3d.systems.RenderSystem.blendEquation(this.field_1486);
        if (this.field_1487) {
            com.mojang.blaze3d.systems.RenderSystem.blendFuncSeparate(this.field_1491, this.field_1489, this.field_1490, this.field_1488);
        } else {
            com.mojang.blaze3d.systems.RenderSystem.blendFunc(this.field_1491, this.field_1489);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlendMode b)) {
            return false;
        }
        return this.field_1486 == b.field_1486 && this.field_1488 == b.field_1488 && this.field_1489 == b.field_1489
            && this.field_1485 == b.field_1485 && this.field_1487 == b.field_1487 && this.field_1490 == b.field_1490
            && this.field_1491 == b.field_1491;
    }

    @Override
    public int hashCode() {
        int h = this.field_1491;
        h = 31 * h + this.field_1490;
        h = 31 * h + this.field_1489;
        h = 31 * h + this.field_1488;
        h = 31 * h + this.field_1486;
        h = 31 * h + (this.field_1487 ? 1 : 0);
        h = 31 * h + (this.field_1485 ? 1 : 0);
        return h;
    }

    /** isOpaque() */
    public boolean method_1245() {
        return this.field_1485;
    }

    /** stringToBlendFunc(name): the GL blend equation of add / subtract / reversesubtract / min / max, add for anything else */
    public static int method_1247(String name) {
        String s = name.trim().toLowerCase(Locale.ROOT);
        if (s.equals("add")) {
            return 32774;
        }
        if (s.equals("subtract")) {
            return 32778;
        }
        if (s.equals("reversesubtract") || s.equals("reverse_subtract")) {
            return 32779;
        }
        if (s.equals("min")) {
            return 32775;
        }
        if (s.equals("max")) {
            return 32776;
        }
        return 32774;
    }

    /** stringToBlendFactor(name): the GL blend factor of 0 / 1 / [1-]src|dst color|alpha, -1 for anything else */
    public static int method_1243(String name) {
        String s = name.trim().toLowerCase(Locale.ROOT).replaceAll("_", "").replaceAll("one", "1").replaceAll("zero", "0").replaceAll("minus", "-");
        return switch (s) {
            case "0" -> 0;
            case "1" -> 1;
            case "srccolor" -> 768;
            case "1-srccolor" -> 769;
            case "dstcolor" -> 774;
            case "1-dstcolor" -> 775;
            case "srcalpha" -> 770;
            case "1-srcalpha" -> 771;
            case "dstalpha" -> 772;
            case "1-dstalpha" -> 773;
            default -> -1;
        };
    }
}
