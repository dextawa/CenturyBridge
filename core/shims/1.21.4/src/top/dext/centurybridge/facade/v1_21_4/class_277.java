package top.dext.centurybridge.facade.v1_21_4;

import java.util.Locale;
import java.util.Objects;

public class class_277 {
    public static class_277 field_1484;
    public final int field_1491;
    public final int field_1490;
    public final int field_1489;
    public final int field_1488;
    public final int field_1486;
    public final boolean field_1487;
    public final boolean field_1485;

    public class_277(
            boolean separateBlend,
            boolean blendDisabled,
            int srcRgb,
            int dstRgb,
            int srcAlpha,
            int dstAlpha,
            int mode
    ) {
        this.field_1487 = separateBlend;
        this.field_1485 = blendDisabled;
        this.field_1491 = srcRgb;
        this.field_1489 = dstRgb;
        this.field_1490 = srcAlpha;
        this.field_1488 = dstAlpha;
        this.field_1486 = mode;
    }

    public class_277() {
        this(false, true, 1, 0, 1, 0, 32774);
    }

    public class_277(int srcRgb, int dstRgb, int mode) {
        this(false, false, srcRgb, dstRgb, srcRgb, dstRgb, mode);
    }

    public class_277(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int mode) {
        this(true, false, srcRgb, dstRgb, srcAlpha, dstAlpha, mode);
    }

    public void method_1244() {
        throw new UnsupportedOperationException(
                "CenturyBridge: applying legacy OpenGL blend state is incompatible with Minecraft 1.21.4"
        );
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof class_277)) {
            return false;
        }

        class_277 other = (class_277) object;
        return this.field_1491 == other.field_1491
                && this.field_1490 == other.field_1490
                && this.field_1489 == other.field_1489
                && this.field_1488 == other.field_1488
                && this.field_1486 == other.field_1486
                && this.field_1487 == other.field_1487
                && this.field_1485 == other.field_1485;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.field_1491,
                this.field_1490,
                this.field_1489,
                this.field_1488,
                this.field_1486,
                this.field_1487,
                this.field_1485
        );
    }

    public boolean method_1245() {
        return this.field_1485;
    }

    public static int method_1247(String name) {
        if (name == null) {
            return 32774;
        }

        switch (name.trim().toLowerCase(Locale.ROOT)) {
            case "subtract":
                return 32778;
            case "reversesubtract":
            case "reverse_subtract":
            case "reverse-subtract":
                return 32779;
            case "min":
                return 32775;
            case "max":
                return 32776;
            case "add":
            default:
                return 32774;
        }
    }

    public static int method_1243(String expression) {
        if (expression == null) {
            return 1;
        }

        String value = expression.trim()
                .toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "");

        switch (value) {
            case "0":
            case "zero":
                return 0;
            case "1":
            case "one":
                return 1;
            case "srccolor":
                return 768;
            case "1srccolor":
                return 769;
            case "srcalpha":
                return 770;
            case "1srcalpha":
                return 771;
            case "dstalpha":
                return 772;
            case "1dstalpha":
                return 773;
            case "dstcolor":
                return 774;
            case "1dstcolor":
                return 775;
            default:
                return 1;
        }
    }
}