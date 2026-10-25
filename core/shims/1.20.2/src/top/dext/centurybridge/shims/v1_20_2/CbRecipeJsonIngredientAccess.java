package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import net.minecraft.class_1856;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Ingredient's 1.20.1 fromJson statics, restored as private statics (CbRecipeJsonIngredientStatics), for the restored
 * readers and ShapedRecipe statics on other classes. The restoring mixin applies first (CbVanillaReads.PRIORITY).
 */
@Mixin(class_1856.class)
public interface CbRecipeJsonIngredientAccess {

    @Invoker("method_8102")
    static class_1856 cb$fromJson(JsonElement json, boolean canBeEmpty) {
        throw new AssertionError();
    }

    @Invoker("method_52177")
    static class_1856 cb$fromJson(JsonElement json) {
        throw new AssertionError();
    }
}
