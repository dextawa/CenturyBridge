package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import top.dext.centurybridge.step.v26_1.facade.ArmorDyeRecipe;
import top.dext.centurybridge.step.v26_1.facade.CustomRecipeSerializer;
import top.dext.centurybridge.step.v26_1.facade.MapCloningRecipe;
import top.dext.centurybridge.step.v26_1.facade.TippedArrowRecipe;

/**
 * The special recipes 26.1 removed -- crafting_special_armordye, crafting_special_mapcloning,
 * crafting_special_tippedarrow -- given back: this layer's 1.21.11 recipe classes, their CustomRecipe serializers
 * registered under 1.21.11's ids right after 26.1's own (mixin/CbWallRecipeKindsMixin), so a 1.21.11 data pack
 * naming the type and a mod reading RecipeSerializer.ARMOR_DYE get them. 26.1's own recipes for the same crafts
 * (its data dye / transmute / imbue recipes) stay as they are; both match the same grids and craft the same item.
 * Calls of matches / assemble naming the 1.21.11 classes: a plain call dispatches to the object, a mod's super call
 * runs 1.21.11's body.
 */
public final class CbWallRecipeKinds {

    // made on first use, not in this class's initializer: the serializers implement 1.21.11's RecipeSerializer,
    // whose constants (field_9028 ...) are these -- an initializer cycle would hand the constants null
    private static CustomRecipeSerializer<ArmorDyeRecipe> armorDye;
    private static CustomRecipeSerializer<MapCloningRecipe> mapCloning;
    private static CustomRecipeSerializer<TippedArrowRecipe> tippedArrow;

    /**
     * 1.21.11's RecipeSerializer initialized first (its initializer asks for these, so the instances it holds
     * are the ones made here)
     */
    @SuppressWarnings("unused")
    private static Object facadeInit;

    private static void facade() {
        facadeInit = top.dext.centurybridge.step.v26_1.facade.RecipeSerializer.field_9035;
    }

    public static synchronized CustomRecipeSerializer<ArmorDyeRecipe> armorDye() {
        facade();
        if (armorDye == null) {
            armorDye = new CustomRecipeSerializer<>(ArmorDyeRecipe::new);
        }
        return armorDye;
    }

    public static synchronized CustomRecipeSerializer<MapCloningRecipe> mapCloning() {
        facade();
        if (mapCloning == null) {
            mapCloning = new CustomRecipeSerializer<>(MapCloningRecipe::new);
        }
        return mapCloning;
    }

    public static synchronized CustomRecipeSerializer<TippedArrowRecipe> tippedArrow() {
        facade();
        if (tippedArrow == null) {
            tippedArrow = new CustomRecipeSerializer<>(TippedArrowRecipe::new);
        }
        return tippedArrow;
    }

    private CbWallRecipeKinds() {
    }

    /** at the end of RecipeSerializers.bootstrap: the three under 1.21.11's ids */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void register(Registry registry) {
        Registry.register(registry, Identifier.withDefaultNamespace("crafting_special_armordye"), CbWallRecipes.record(armorDye()));
        Registry.register(registry, Identifier.withDefaultNamespace("crafting_special_mapcloning"), CbWallRecipes.record(mapCloning()));
        Registry.register(registry, Identifier.withDefaultNamespace("crafting_special_tippedarrow"), CbWallRecipes.record(tippedArrow()));
    }

    // ---- matches(input, level) / assemble(input, registries) named through the 1.21.11 classes

    public static boolean matches(ArmorDyeRecipe recipe, CraftingInput input, Level level) {
        return recipe.matches(input, level);
    }

    public static boolean superMatches(ArmorDyeRecipe recipe, CraftingInput input, Level level) {
        return recipe.cb$matches(input, level);
    }

    public static ItemStack assemble(ArmorDyeRecipe recipe, CraftingInput input, HolderLookup.Provider registries) {
        return recipe.assemble(input, registries);
    }

    public static ItemStack superAssemble(ArmorDyeRecipe recipe, CraftingInput input, HolderLookup.Provider registries) {
        return recipe.cb$assemble(input);
    }

    public static boolean matches(MapCloningRecipe recipe, CraftingInput input, Level level) {
        return recipe.matches(input, level);
    }

    public static boolean superMatches(MapCloningRecipe recipe, CraftingInput input, Level level) {
        return recipe.cb$matches(input, level);
    }

    public static ItemStack assemble(MapCloningRecipe recipe, CraftingInput input, HolderLookup.Provider registries) {
        return recipe.assemble(input, registries);
    }

    public static ItemStack superAssemble(MapCloningRecipe recipe, CraftingInput input, HolderLookup.Provider registries) {
        return recipe.cb$assemble(input);
    }

    public static boolean matches(TippedArrowRecipe recipe, CraftingInput input, Level level) {
        return recipe.matches(input, level);
    }

    public static boolean superMatches(TippedArrowRecipe recipe, CraftingInput input, Level level) {
        return recipe.cb$matches(input, level);
    }

    public static ItemStack assemble(TippedArrowRecipe recipe, CraftingInput input, HolderLookup.Provider registries) {
        return recipe.assemble(input, registries);
    }

    public static ItemStack superAssemble(TippedArrowRecipe recipe, CraftingInput input, HolderLookup.Provider registries) {
        return recipe.cb$assemble(input);
    }

    // ---- getSerializer() named through the 1.21.11 classes: the 1.21.11 serializer of the object's record

    public static top.dext.centurybridge.step.v26_1.facade.RecipeSerializer<?> getSerializer(ArmorDyeRecipe recipe) {
        return CbWallRecipes.getSerializer(recipe);
    }

    public static top.dext.centurybridge.step.v26_1.facade.RecipeSerializer<?> getSerializer(MapCloningRecipe recipe) {
        return CbWallRecipes.getSerializer(recipe);
    }

    public static top.dext.centurybridge.step.v26_1.facade.RecipeSerializer<?> getSerializer(TippedArrowRecipe recipe) {
        return CbWallRecipes.getSerializer(recipe);
    }
}
