package top.dext.centurybridge.step.v1_21.rt;

/**
 * ShapedRecipePattern's 1.20.6 record accessors (instanceRedirects): 1.21 made the record a class with
 * width() / height() / ingredients() methods of other names and kept the pattern data private.
 */
public final class CbItemShapedPatterns {

    private CbItemShapedPatterns() {
    }

    /** width() */
    public static int comp_2081(net.minecraft.class_8957 pattern) {
        return pattern.method_59995();
    }

    /** height() */
    public static int comp_2082(net.minecraft.class_8957 pattern) {
        return pattern.method_59996();
    }

    /** ingredients() */
    public static net.minecraft.class_2371<net.minecraft.class_1856> comp_2083(net.minecraft.class_8957 pattern) {
        return pattern.method_59997();
    }

    /** data(): the key and pattern rows it was read from (CbItemShapedPatternAccessor) */
    public static java.util.Optional<net.minecraft.class_8957.class_8958> comp_2084(net.minecraft.class_8957 pattern) {
        return ((top.dext.centurybridge.step.v1_21.mixin.CbItemShapedPatternAccessor) (Object) pattern).cbItem$data();
    }
}
