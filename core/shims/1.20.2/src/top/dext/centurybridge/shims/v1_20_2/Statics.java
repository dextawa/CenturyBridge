package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.class_1856;

/**
 * Caller-side homes for dead STATIC methods (Mixin cannot add non-private
 * statics to targets). The converter rewrites old invokestatic call sites to
 * point here; signatures must match the dead originals exactly.
 */
public final class Statics {

    /** 1.20.1 Ingredient.fromJson(JsonElement), removed 1.20.2 (codec era). L3 x29. */
    public static class_1856 method_52177(JsonElement json) {
        return class_1856.field_46095.parse(JsonOps.INSTANCE, json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid ingredient: " + json));
    }

    /** 1.20.1 ShapedRecipe.outputFromJson (item/count/nbt legacy format), removed 1.20.2. L3 x11. */
    public static net.minecraft.class_1799 method_35228(com.google.gson.JsonObject json) {
        String itemId = net.minecraft.class_3518.method_15265(json, "item");
        net.minecraft.class_1792 item = net.minecraft.class_7923.field_41178
            .method_17966(new net.minecraft.class_2960(itemId))
            .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + itemId + "'"));
        net.minecraft.class_1799 stack = new net.minecraft.class_1799(
            item, net.minecraft.class_3518.method_15282(json, "count", 1));
        if (json.has("nbt")) {
            try {
                stack.method_7980(net.minecraft.class_2522.method_10718(
                    net.minecraft.class_3518.method_15265(json, "nbt")));
            } catch (Exception e) {
                throw new JsonSyntaxException("Invalid nbt on output stack", e);
            }
        }
        return stack;
    }

    /** 1.20.1 FluidDrainable.tryDrainFluid gained a leading @Nullable player param in 1.20.2.
     *  Interface methods cannot be re-added via Mixin; call sites are redirected here. L2 x19. */
    public static net.minecraft.class_1799 method_9700(net.minecraft.class_2263 self,
            net.minecraft.class_1936 world, net.minecraft.class_2338 pos, net.minecraft.class_2680 state) {
        return self.method_9700(null, world, pos, state);
    }

    private Statics() {
    }
}
