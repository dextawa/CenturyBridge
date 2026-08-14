package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.class_1856;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: Ingredient JSON methods moved to codecs.
 * - static fromJson(JsonElement) re-added via codec parse (L3 x29)
 * - toJson() re-added delegating to toJson(boolean) (L2 x21); the permissive
 *   flag approximates the old unrestricted serialization
 */
@Mixin(class_1856.class)
public abstract class IngredientBridge {

    public static class_1856 method_52177(JsonElement json) {
        return class_1856.field_46095.parse(JsonOps.INSTANCE, json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid ingredient: " + json));
    }

    public JsonElement method_8089() {
        return ((class_1856) (Object) this).method_8089(true);
    }
}
