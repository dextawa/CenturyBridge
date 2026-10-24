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

    /**
     * the key a vanilla builder saves its recipe under in an output: Fabric API 0.116 saved it under the output's
     * FabricRecipeExporter.getRecipeIdentifier(id) (FabricRecipeProvider's output puts it in the mod's namespace)
     */
    public static class_5321<class_1860<?>> exportedKey(class_8790 output, class_5321<class_1860<?>> key) {
        class_2960 id = key.method_29177();
        class_2960 exported = ((top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricRecipeExporter) (Object) output).getRecipeIdentifier(id);
        return exported.equals(id) ? key : key(exported);
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


    // ---- loot sub-provider helpers (dev-time datagen) ----

    /**
     * BlockLootSubProvider.HAS_SHEARS (1.21.1's static field_40604): MatchTool of an item predicate of shears, items
     * resolved in the built-in registry -- 1.21.2 builds the same condition per provider (hasShears())
     */
    public static net.minecraft.class_5341.class_210 field_40604() {
        return net.minecraft.class_223.method_945(net.minecraft.class_2073.class_2074.method_8973()
            .method_8977(class_7923.field_41178, net.minecraft.class_1802.field_8868));
    }

    /** BlockLootSubProvider.createShearsOnlyDrop(item), static in 1.21.1: one roll of the item, only with shears */
    public static net.minecraft.class_52.class_53 method_45995(class_1935 item) {
        return net.minecraft.class_52.method_324().method_336(net.minecraft.class_55.method_347()
            .method_352(net.minecraft.class_44.method_32448(1.0F)).method_356(field_40604())
            .method_351(net.minecraft.class_77.method_411(item)));
    }

    /**
     * EntityLootSubProvider.createSheepTable(wool), static in 1.21.1: a roll of the wool, then a roll of the sheep's
     * default loot table (1.21.2 dispatches sheep loot by colour instead)
     */
    public static net.minecraft.class_52.class_53 method_46031(class_1935 wool) {
        return net.minecraft.class_52.method_324()
            .method_336(net.minecraft.class_55.method_347().method_352(net.minecraft.class_44.method_32448(1.0F))
                .method_351(net.minecraft.class_77.method_411(wool)))
            .method_336(net.minecraft.class_55.method_347().method_352(net.minecraft.class_44.method_32448(1.0F))
                .method_351(net.minecraft.class_83.method_428(net.minecraft.class_1299.field_6115.method_16351().orElseThrow())));
    }

    /** BiomeDefaultFeatures.addDefaultSeagrass(builder) (1.21.1): the seagrass_simple placed feature at VEGETAL_DECORATION */
    @SuppressWarnings("unchecked")
    public static void method_16992(net.minecraft.class_5485.class_5495 builder) {
        builder.method_30992(net.minecraft.class_2893.class_2895.field_13178,
            (class_5321<net.minecraft.class_6796>) field_35975());
    }


    /** new VanillaRecipeProvider(output, registries) (1.21.1): the layer's 1.21.1 provider */
    public static net.minecraft.class_7803 newVanillaRecipeProvider(net.minecraft.class_7784 output,
            java.util.concurrent.CompletableFuture<net.minecraft.class_7225.class_7874> registries) {
        return new top.dext.centurybridge.step.v1_21_2.facade.VanillaRecipeProvider(output, registries);
    }

}
