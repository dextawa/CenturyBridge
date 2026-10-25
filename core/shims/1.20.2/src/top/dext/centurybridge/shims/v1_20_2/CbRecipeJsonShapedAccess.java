package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Map;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1869;
import net.minecraft.class_2371;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * ShapedRecipe's 1.20.1 statics, restored as private statics (CbRecipeJsonShapedStatics: Mixin merges a class mixin's
 * statics only when private), for the restored readers on other classes. The restoring mixin applies first
 * (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(class_1869.class)
public interface CbRecipeJsonShapedAccess {

    @Invoker("method_8157")
    static Map<String, class_1856> cb$keyFromJson(JsonObject json) {
        throw new AssertionError();
    }

    @Invoker("method_8145")
    static String[] cb$patternFromJson(JsonArray array) {
        throw new AssertionError();
    }

    /** 1.20.2's ShapedRecipe.shrink(List) (1.20.1's shrink(String...), the same code), what 1.20.2's pattern codec calls */
    @Invoker("method_8146")
    static String[] cb$shrink(java.util.List<String> rows) {
        throw new AssertionError();
    }

    @Invoker("method_8148")
    static class_2371<class_1856> cb$dissolvePattern(String[] rows, Map<String, class_1856> key, int width, int height) {
        throw new AssertionError();
    }

    @Invoker("method_35228")
    static class_1799 cb$itemStackFromJson(JsonObject json) {
        throw new AssertionError();
    }

    @Invoker("method_8155")
    static class_1792 cb$itemFromJson(JsonObject json) {
        throw new AssertionError();
    }
}
