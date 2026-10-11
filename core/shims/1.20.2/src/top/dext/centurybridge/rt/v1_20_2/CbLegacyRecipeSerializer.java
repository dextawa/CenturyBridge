package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 RecipeSerializer read shapes: read(id, json) and read(id, buf).
 * A mod serializer that still declares them gets this interface added by the
 * converter (adapterHooks), so its own methods implement it and {@link CbRecipes}
 * reaches them typed. Member names are intermediary tokens.
 */
public interface CbLegacyRecipeSerializer {

    /** read(id, json) */
    net.minecraft.class_1860<?> method_8121(net.minecraft.class_2960 id, com.google.gson.JsonObject json);

    /** read(id, buf) */
    net.minecraft.class_1860<?> method_8122(net.minecraft.class_2960 id, net.minecraft.class_2540 buf);
}
