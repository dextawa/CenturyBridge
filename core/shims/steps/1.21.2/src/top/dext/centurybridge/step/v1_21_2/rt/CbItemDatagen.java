package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1860;
import net.minecraft.class_1935;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7871;
import net.minecraft.class_7923;
import net.minecraft.class_8790;

/**
 * 1.21.1's data generation API for recipes on 1.21.2 (data/recipes; dev-time datagen, run by a mod's runDatagen).
 * <ul>
 * <li>Recipes are saved under a ResourceKey in the recipe registry where 1.21.1 took an id: RecipeBuilder.save(output,
 *     id), RecipeOutput.accept(id, recipe, advancement), the special and smithing builders' save, VanillaRecipeProvider's
 *     trim templates -- the key is the id's ({@link #key}); a 1.21.1 override or implementation is handed the key's
 *     location ({@link #id}).</li>
 * <li>The shaped / shapeless builders resolve their ingredients' items in an item lookup 1.21.2 passes in; 1.21.1's
 *     resolved them in the built-in item registry, which is the lookup passed here ({@link #items}).</li>
 * <li>RecipeOutput.includeRootAdvancement() is new: 1.21.1's outputs had no root advancement of their own (1.21.1's
 *     RecipeProvider wrote it), so a 1.21.1 output does nothing on it.</li>
 * </ul>
 */
public final class CbItemDatagen {
    private CbItemDatagen() {
    }

    /** a recipe id as 1.21.2's recipe key */
    public static class_5321<class_1860<?>> key(class_2960 id) {
        return CbItemRecipeBook.key(id);
    }

    /** a recipe key as 1.21.1's id */
    public static class_2960 id(class_5321<class_1860<?>> key) {
        return key.method_29177();
    }

    /** the item lookup 1.21.1's builders resolved items in: the built-in item registry */
    public static class_7871<class_1792> items() {
        return class_7923.field_41178;
    }

    /** ShapelessRecipeBuilder's result: count of the item, as 1.21.1's constructor took them */
    public static class_1799 stack(class_1935 item, int count) {
        return new class_1799(item, count);
    }

    /** shapeless(category, item): one of the item */
    public static class_1799 stack(class_1935 item) {
        return new class_1799(item, 1);
    }

    /** RecipeOutput.includeRootAdvancement() of a 1.21.1 output (abstractFills): nothing to include */
    public static void includeRootAdvancement(class_8790 output) {
    }

    // ---- worldgen keys 1.21.2 dropped (dev-time datagen bootstrap): 1.21.1's keys, the same ids ----

    /** AquaticFeatures.SEAGRASS_SIMPLE: the configured feature key minecraft:seagrass_simple */
    public static class_5321<?> field_35767() {
        return class_5321.method_29179(net.minecraft.class_7924.field_41239, class_2960.method_60656("seagrass_simple"));
    }

    /** AquaticPlacements.SEAGRASS_SIMPLE: the placed feature key minecraft:seagrass_simple */
    public static class_5321<?> field_35975() {
        return class_5321.method_29179(net.minecraft.class_7924.field_41245, class_2960.method_60656("seagrass_simple"));
    }

    /** TrialChambersStructurePools field_47509: the template pool key minecraft:trial_chambers/chamber/entrance_cap */
    public static class_5321<?> field_47509() {
        return class_5321.method_29179(net.minecraft.class_7924.field_41249, class_2960.method_60656("trial_chambers/chamber/entrance_cap"));
    }


    // ---- advancement and biome bootstrap helpers (dev-time datagen) ----

    /** the entity type lookup 1.21.2's advancement helpers take; 1.21.1's resolved in the built-in registry */
    public static class_7871<net.minecraft.class_1299<?>> entityTypes() {
        return class_7923.field_41177;
    }

    /**
     * OverworldBiomes.darkForest(features, carvers): 1.21.2's takes whether it is the pale garden (new in 1.21.2); the
     * dark forest 1.21.1 built is its other branch
     */
    public static boolean notPaleGarden() {
        return false;
    }

}
