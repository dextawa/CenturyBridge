package net.minecraft;

/** Identity-bearing RenderType test doubles, including one non-chunk type. */
public final class class_1921 {
    public static final class_1921 SOLID = new class_1921(class_11515.field_60923);
    public static final class_1921 CUTOUT_MIPPED = new class_1921(class_11515.field_60924);
    public static final class_1921 CUTOUT = new class_1921(class_11515.field_60925);
    public static final class_1921 TRANSLUCENT = new class_1921(class_11515.field_60926);
    public static final class_1921 TRIPWIRE = new class_1921(class_11515.field_60927);
    public static final class_1921 OTHER = new class_1921(null);

    public final class_11515 layer;

    private class_1921(class_11515 layer) {
        this.layer = layer;
    }

    public static class_1921 method_23577() {
        return SOLID;
    }

    public static class_1921 method_23579() {
        return CUTOUT_MIPPED;
    }

    public static class_1921 method_23581() {
        return CUTOUT;
    }

    public static class_1921 method_29997() {
        return TRIPWIRE;
    }

    public static class_1921 method_23589() {
        return OTHER;
    }

    /** A distinct mod-created key deliberately sharing a canonical key's native pipeline. */
    public static class_1921 modCreated(class_11515 layer) {
        return new class_1921(layer);
    }
}
