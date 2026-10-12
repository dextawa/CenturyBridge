package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 AbstractFurnaceMenu progress on 1.20.2 (s1misc). 1.20.1's getBurnProgress() /
 * getLitProgress() answered pixels of the furnace screen's arrow (of 24) and flame (of 13); 1.20.2's
 * answer fractions, which its screen multiplies back and rounds up (Mth.ceil(fraction * 24),
 * Mth.ceil(fraction * 13)). A converted menu's pixel override answers 1.20.2's as the largest float
 * below pixels / size, so the screen's ceil lands on exactly the override's pixel count -- the
 * correctly rounded quotient can come out a hair above and round up a pixel too many.
 */
public final class CbMenus {

    /** getBurnProgress(): pixels of 24 -> fraction */
    public static float burnProgress(int pixels) {
        return fraction(pixels, 24.0F);
    }

    /** getLitProgress(): pixels of 13 -> fraction */
    public static float litProgress(int pixels) {
        return fraction(pixels, 13.0F);
    }

    private static float fraction(int pixels, float size) {
        if (pixels <= 0) {
            return 0.0F;
        }
        return Math.nextDown((float) pixels / size);
    }

    private CbMenus() {
    }
}
