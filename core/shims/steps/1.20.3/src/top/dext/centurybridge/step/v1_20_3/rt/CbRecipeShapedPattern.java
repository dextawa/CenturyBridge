package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.List;

/**
 * ShapedRecipe's 1.20.2 pattern helpers, which 1.20.3 moved, with the same bodies (javap), to ShapedRecipePattern:
 * shrink(rows) (method_8146 -> method_55084), firstNonSpace (method_8151 -> method_55083), lastNonSpace (method_8153
 * -> method_55089). Mods reached them through mixin invokers (they were package-private / private); those land here
 * (staticRedirects), and the step's mixinMoves carry mods' injectors into them to ShapedRecipePattern.
 */
public final class CbRecipeShapedPattern {

    private CbRecipeShapedPattern() {
    }

    /** 1.20.2 ShapedRecipe.shrink(List) */
    public static String[] method_8146(List<String> rows) {
        return net.minecraft.class_8957.method_55084(rows);
    }

    /** 1.20.2 ShapedRecipe.firstNonSpace(String) */
    public static int method_8151(String row) {
        return net.minecraft.class_8957.method_55083(row);
    }

    /** 1.20.2 ShapedRecipe.lastNonSpace(String) */
    public static int method_8153(String row) {
        return net.minecraft.class_8957.method_55089(row);
    }
}
