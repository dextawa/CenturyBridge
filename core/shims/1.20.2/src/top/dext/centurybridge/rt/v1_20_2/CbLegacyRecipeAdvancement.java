package top.dext.centurybridge.rt.v1_20_2;

/**
 * The two 1.20.1 RecipeJsonProvider getters 1.20.2 folded into advancement() (an AdvancementEntry):
 * the adapter a 1.20.1 implementation is given so the synthesized advancement() can read them
 * (adapterHooks; the engine makes the implementing methods public).
 */
public interface CbLegacyRecipeAdvancement {

    /** toAdvancementJson() */
    com.google.gson.JsonObject method_10415();

    /** getAdvancementId() */
    net.minecraft.class_2960 method_10418();
}
