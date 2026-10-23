package top.dext.centurybridge.step.v1_21_2.facade;

/**
 * 1.21.1's FastColor (net/minecraft/class_5253) and its ARGB32 / ABGR32 halves, which 1.21.2 folded
 * into net.minecraft.util.ARGB (class_9848). Pure integer arithmetic, so the bodies are 1.21.1's own,
 * read off its bytecode (classRenames: every 1.21.1 reference to the three classes lands here).
 */
public class FastColor {

    public FastColor() {
    }

    /** as8BitChannel: Mth.floor(f * 255) */
    public static int method_59553(float f) {
        return net.minecraft.class_3532.method_15375(f * 255.0F);
    }

    /** FastColor.ARGB32 (class_5253$class_5254) */
    public static class ARGB32 {

        public ARGB32() {
        }

        /** alpha */
        public static int method_27762(int c) {
            return c >>> 24;
        }

        /** red */
        public static int method_27765(int c) {
            return c >> 16 & 255;
        }

        /** green */
        public static int method_27766(int c) {
            return c >> 8 & 255;
        }

        /** blue */
        public static int method_27767(int c) {
            return c & 255;
        }

        /** color(a, r, g, b) */
        public static int method_27764(int a, int r, int g, int b) {
            return a << 24 | r << 16 | g << 8 | b;
        }

        /** color(r, g, b): opaque */
        public static int method_57173(int r, int g, int b) {
            return method_27764(255, r, g, b);
        }

        /** multiply */
        public static int method_27763(int x, int y) {
            return method_27764(method_27762(x) * method_27762(y) / 255, method_27765(x) * method_27765(y) / 255,
                method_27766(x) * method_27766(y) / 255, method_27767(x) * method_27767(y) / 255);
        }

        /** lerp: Mth.lerpInt per channel */
        public static int method_48780(float t, int x, int y) {
            int a = net.minecraft.class_3532.method_48781(t, method_27762(x), method_27762(y));
            int r = net.minecraft.class_3532.method_48781(t, method_27765(x), method_27765(y));
            int g = net.minecraft.class_3532.method_48781(t, method_27766(x), method_27766(y));
            int b = net.minecraft.class_3532.method_48781(t, method_27767(x), method_27767(y));
            return method_27764(a, r, g, b);
        }

        /** opaque */
        public static int method_57174(int c) {
            return c | 0xFF000000;
        }

        /** color(alpha, rgb) */
        public static int method_58144(int a, int rgb) {
            return a << 24 | rgb & 0xFFFFFF;
        }

        /** colorFromFloat(a, r, g, b) */
        public static int method_59554(float a, float r, float g, float b) {
            return method_27764(method_59553(a), method_59553(r), method_59553(g), method_59553(b));
        }

        /** average */
        public static int method_60676(int x, int y) {
            return method_27764((method_27762(x) + method_27762(y)) / 2, (method_27765(x) + method_27765(y)) / 2,
                (method_27766(x) + method_27766(y)) / 2, (method_27767(x) + method_27767(y)) / 2);
        }
    }

    /** FastColor.ABGR32 (class_5253$class_8045) */
    public static class ABGR32 {

        public ABGR32() {
        }

        /** alpha */
        public static int method_48342(int c) {
            return c >>> 24;
        }

        /** red */
        public static int method_48345(int c) {
            return c & 255;
        }

        /** green */
        public static int method_48346(int c) {
            return c >> 8 & 255;
        }

        /** blue */
        public static int method_48347(int c) {
            return c >> 16 & 255;
        }

        /** transparent */
        public static int method_48348(int c) {
            return c & 0xFFFFFF;
        }

        /** opaque */
        public static int method_48548(int c) {
            return c | 0xFF000000;
        }

        /** color(a, b, g, r) */
        public static int method_48344(int a, int b, int g, int r) {
            return a << 24 | b << 16 | g << 8 | r;
        }

        /** color(alpha, bgr) */
        public static int method_48343(int a, int bgr) {
            return a << 24 | bgr & 0xFFFFFF;
        }

        /** fromArgb32: swap red and blue */
        public static int method_60675(int c) {
            return c & 0xFF00FF00 | (c & 0xFF0000) >> 16 | (c & 255) << 16;
        }
    }
}
