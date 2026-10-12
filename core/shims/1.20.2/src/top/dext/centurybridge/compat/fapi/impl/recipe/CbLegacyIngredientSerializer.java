package top.dext.centurybridge.compat.fapi.impl.recipe;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;

/**
 * The JSON half of a 1.20.1 CustomIngredientSerializer, which 1.20.2's replaced with getCodec. The
 * converter adds this interface to a mod class implementing the serializer the 1.20.1 way
 * (overrideHooks bridge mode); the class's own read / write implement it.
 */
public interface CbLegacyIngredientSerializer {

    CustomIngredient read(JsonObject json);

    void write(JsonObject json, CustomIngredient ingredient);
}
