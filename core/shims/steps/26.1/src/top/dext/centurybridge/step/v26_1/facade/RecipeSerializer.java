package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipeKinds;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipes;
import top.dext.centurybridge.step.v26_1.rt.CbWallRegistry;

/**
 * 1.21.11's RecipeSerializer (net/minecraft/class_1865), an interface mods implemented for their recipes;
 * 26.1's is a final record of the same two codecs. A 1.21.11 serializer (a mod's, or the one this layer
 * gives back for a 26.1 serializer) is registered, and answered from Recipe.getSerializer(), as the 26.1
 * record of its codecs -- one per serializer, both ways, so a recipe's serializer compares equal to the
 * constant it was registered as ({@link CbWallRecipes}).
 *
 * The constants are the serializers 26.1 registers under 1.21.11's ids. The three whose recipe types 26.1
 * removed (crafting_special_armordye, crafting_special_mapcloning, crafting_special_tippedarrow) are this
 * layer's, registered under those ids with 1.21.11's recipes (rt/CbWallRecipeKinds).
 * Member names are 1.21.11's intermediary names.
 */
@SuppressWarnings({"rawtypes", "unused"})
public interface RecipeSerializer<T extends Recipe<?>> extends CbWallRegistry.Wrapper {

    /** 1.21.11 RecipeSerializer.ARMOR_DYE ("crafting_special_armordye", this layer's: rt/CbWallRecipeKinds) */
    RecipeSerializer field_9028 = CbWallRecipeKinds.armorDye();

    /** 1.21.11 RecipeSerializer.MAP_CLONING ("crafting_special_mapcloning", this layer's) */
    RecipeSerializer field_9044 = CbWallRecipeKinds.mapCloning();

    /** 1.21.11 RecipeSerializer.TIPPED_ARROW ("crafting_special_tippedarrow", this layer's) */
    RecipeSerializer field_9037 = CbWallRecipeKinds.tippedArrow();

    /** 1.21.11 RecipeSerializer constant registered as "crafting_shaped" */
    RecipeSerializer field_9035 = CbWallRecipes.vanilla("crafting_shaped");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_shapeless" */
    RecipeSerializer field_9031 = CbWallRecipes.vanilla("crafting_shapeless");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_special_bookcloning" */
    RecipeSerializer field_9029 = CbWallRecipes.vanilla("crafting_special_bookcloning");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_special_mapextending" */
    RecipeSerializer field_9039 = CbWallRecipes.vanilla("crafting_special_mapextending");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_special_firework_rocket" */
    RecipeSerializer field_9043 = CbWallRecipes.vanilla("crafting_special_firework_rocket");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_special_firework_star" */
    RecipeSerializer field_9036 = CbWallRecipes.vanilla("crafting_special_firework_star");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_special_firework_star_fade" */
    RecipeSerializer field_9034 = CbWallRecipes.vanilla("crafting_special_firework_star_fade");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_special_bannerduplicate" */
    RecipeSerializer field_9038 = CbWallRecipes.vanilla("crafting_special_bannerduplicate");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_special_shielddecoration" */
    RecipeSerializer field_9040 = CbWallRecipes.vanilla("crafting_special_shielddecoration");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_transmute" */
    RecipeSerializer field_54318 = CbWallRecipes.vanilla("crafting_transmute");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_special_repairitem" */
    RecipeSerializer field_19421 = CbWallRecipes.vanilla("crafting_special_repairitem");

    /** 1.21.11 RecipeSerializer constant registered as "smelting" */
    RecipeSerializer field_9042 = CbWallRecipes.vanilla("smelting");

    /** 1.21.11 RecipeSerializer constant registered as "blasting" */
    RecipeSerializer field_17084 = CbWallRecipes.vanilla("blasting");

    /** 1.21.11 RecipeSerializer constant registered as "smoking" */
    RecipeSerializer field_17085 = CbWallRecipes.vanilla("smoking");

    /** 1.21.11 RecipeSerializer constant registered as "campfire_cooking" */
    RecipeSerializer field_17347 = CbWallRecipes.vanilla("campfire_cooking");

    /** 1.21.11 RecipeSerializer constant registered as "stonecutting" */
    RecipeSerializer field_17640 = CbWallRecipes.vanilla("stonecutting");

    /** 1.21.11 RecipeSerializer constant registered as "smithing_transform" */
    RecipeSerializer field_42027 = CbWallRecipes.vanilla("smithing_transform");

    /** 1.21.11 RecipeSerializer constant registered as "smithing_trim" */
    RecipeSerializer field_42028 = CbWallRecipes.vanilla("smithing_trim");

    /** 1.21.11 RecipeSerializer constant registered as "crafting_decorated_pot" */
    RecipeSerializer field_42718 = CbWallRecipes.vanilla("crafting_decorated_pot");

    /** codec() */
    MapCodec<T> method_53736();

    /** streamCodec() */
    StreamCodec<RegistryFriendlyByteBuf, T> method_56104();

    /** register(String, S): RecipeSerializer.register, registering the 26.1 record and answering the serializer */
    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S method_17724(String id, S serializer) {
        return CbWallRecipes.register(id, serializer);
    }

    @Override
    default Object cb$unwrap() {
        return CbWallRecipes.record(this);
    }
}
