package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import top.dext.centurybridge.step.v1_20_3.facade.FinishedRecipe;
import top.dext.centurybridge.step.v1_20_3.facade.LegacySmithingTransformRecipeBuilder;
import top.dext.centurybridge.step.v1_20_3.facade.LegacySmithingTrimRecipeBuilder;

/**
 * 1.20.2 recipe data generation on 1.20.3's.
 *
 * 1.20.2's builders saved a FinishedRecipe -- id, serializer, advancement and the recipe's JSON --
 * into the RecipeOutput; 1.20.3's save the recipe object itself, built by a factory where 1.20.2's
 * builders took the serializer. Both ways are carried across here. A FinishedRecipe handed to a
 * 1.20.3 output becomes the recipe its JSON describes (decoded by the recipe codec, which reads the
 * JSON 1.20.2's game loaded the same file with) -- unless the output is a 1.20.2 one (its own
 * accept) or vanilla's RecipeProvider (which writes the JSON as is, as 1.20.2's did). A 1.20.2 output
 * handed a 1.20.3 recipe gets it as a FinishedRecipe whose JSON is the recipe codec's. Where 1.20.2
 * took a serializer the factory decodes, through that serializer, the JSON 1.20.2's builder wrote
 * for those values: the recipe that serializer made of it. The smithing builders, whose 1.20.2
 * constructors took the serializer, save 1.20.2's Result itself ({@link #finishing}).
 */
public final class CbDataRecipes {

    /** a 1.20.2 RecipeOutput implementation's own accept(FinishedRecipe) (adapterHooks) */
    public interface LegacyOutput {
        void method_53819(FinishedRecipe recipe);
    }

    /** vanilla RecipeProvider's output with 1.20.2's accept(FinishedRecipe) (CbDataRecipeProviderOutputMixin) */
    public interface RawRecipeSink {
        void cb$acceptFinished(FinishedRecipe recipe);
    }

    /** RecipeProvider's recipe / advancement path providers (CbDataRecipeProviderAccessor) */
    public interface RecipeProviderPaths {
        net.minecraft.class_7784.class_7489 cb$recipePathProvider();

        net.minecraft.class_7784.class_7489 cb$advancementPathProvider();
    }

    /** BlockFamily's 1.20.2 required features (CbDataBlockFamilyMixin) */
    public interface FamilyFeatures {
        void cb$setRequiredFeatures(net.minecraft.class_7699 features);
    }

    // ---------------------------------------------------------------- RecipeOutput (class_8790)

    /** RecipeOutput.accept(FinishedRecipe) */
    public static void accept(net.minecraft.class_8790 output, FinishedRecipe recipe) {
        if (output instanceof LegacyOutput legacy) {
            legacy.method_53819(recipe);
        } else if (output instanceof RawRecipeSink sink) {
            sink.cb$acceptFinished(recipe);
        } else if (recipe instanceof RecipeResult result) {
            output.method_53819(result.comp_1230(), result.recipe(), result.comp_1235());
        } else {
            output.method_53819(recipe.comp_1230(), decode(recipe.method_17799()), recipe.comp_1235());
        }
    }

    /** accept(id, recipe, advancement) synthesized into a 1.20.2 RecipeOutput implementation */
    public static void acceptRecipe(LegacyOutput output, net.minecraft.class_2960 id, net.minecraft.class_1860<?> recipe,
            net.minecraft.class_8779 advancement) {
        output.method_53819(new RecipeResult(id, recipe, advancement));
    }

    /** a 1.20.3 recipe as the FinishedRecipe 1.20.2 handed an output: its JSON is the recipe codec's */
    public record RecipeResult(net.minecraft.class_2960 comp_1230, net.minecraft.class_1860<?> recipe,
            net.minecraft.class_8779 comp_1235) implements FinishedRecipe {

        @Override
        public net.minecraft.class_1865<?> comp_1231() {
            return recipe.method_8119();
        }

        @Override
        public void method_10416(JsonObject json) {
            for (Map.Entry<String, JsonElement> e : encode(recipe).entrySet()) {
                if (!e.getKey().equals("type")) {
                    json.add(e.getKey(), e.getValue());
                }
            }
        }
    }

    static JsonObject encode(net.minecraft.class_1860<?> recipe) {
        return net.minecraft.class_156.method_47526(net.minecraft.class_1860.field_47319.encodeStart(JsonOps.INSTANCE, recipe),
            IllegalStateException::new).getAsJsonObject();
    }

    static net.minecraft.class_1860<?> decode(JsonObject json) {
        return net.minecraft.class_156.method_47526(net.minecraft.class_1860.field_47319.parse(JsonOps.INSTANCE, json),
            JsonParseException::new);
    }

    static <T extends net.minecraft.class_1860<?>> T decode(net.minecraft.class_1865<T> serializer, JsonObject json) {
        return net.minecraft.class_156.method_47526(serializer.method_53736().parse(JsonOps.INSTANCE, json), JsonParseException::new);
    }

    /** 1.20.2 Ingredient.toJson(allowEmpty): the ingredient codec's encoding (1.20.2's own body) */
    public static JsonElement ingredientJson(net.minecraft.class_1856 ingredient, boolean allowEmpty) {
        return net.minecraft.class_156.method_47526((allowEmpty ? net.minecraft.class_1856.field_46095
            : net.minecraft.class_1856.field_46096).encodeStart(JsonOps.INSTANCE, ingredient), IllegalStateException::new);
    }

    private static String itemId(net.minecraft.class_1935 item) {
        return net.minecraft.class_7923.field_41178.method_10221(item.method_8389()).toString();
    }

    // ---------------------------------------------------------------- serializer -> factory

    /**
     * The cooking-recipe factory for a 1.20.2 serializer: the recipe that serializer reads from the
     * JSON 1.20.2's SimpleCookingRecipeBuilder.Result wrote (group unless empty, book category,
     * ingredient, result item, experience, cooking time).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_1874.class_3958 cookingFactory(net.minecraft.class_1865 serializer) {
        return (group, category, ingredient, result, experience, cookingTime) -> {
            JsonObject json = new JsonObject();
            if (group != null && !group.isEmpty()) {
                json.addProperty("group", group);
            }
            json.addProperty("category", category.method_15434());
            json.add("ingredient", ingredientJson(ingredient, false));
            json.addProperty("result", itemId(result.method_7909()));
            json.addProperty("experience", experience);
            json.addProperty("cookingtime", cookingTime);
            return (net.minecraft.class_1874) decode(serializer, json);
        };
    }

    /**
     * The single-item-recipe factory for a 1.20.2 serializer: the recipe it reads from the JSON
     * 1.20.2's SingleItemRecipeBuilder.Result wrote (group unless empty, ingredient, result item, count).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_3972.class_3974 singleItemFactory(net.minecraft.class_1865 serializer) {
        return (group, ingredient, result) -> {
            JsonObject json = new JsonObject();
            if (group != null && !group.isEmpty()) {
                json.addProperty("group", group);
            }
            json.add("ingredient", ingredientJson(ingredient, false));
            json.addProperty("result", itemId(result.method_7909()));
            json.addProperty("count", result.method_7947());
            return (net.minecraft.class_3972) decode(serializer, json);
        };
    }

    /**
     * The special-recipe factory for a 1.20.2 serializer: the recipe it reads from the JSON 1.20.2's
     * SpecialRecipeBuilder saved (its book category; 1.20.2 always saved MISC, as 1.20.3's builder
     * asks for).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Function<net.minecraft.class_7710, net.minecraft.class_1860<?>> specialFactory(net.minecraft.class_1865 serializer) {
        return category -> {
            JsonObject json = new JsonObject();
            json.addProperty("category", category.method_15434());
            return (net.minecraft.class_1860<?>) decode(serializer, json);
        };
    }

    /** new SmithingTransformRecipeBuilder(serializer, template, base, addition, category, result) */
    public static net.minecraft.class_8074 smithingTransform(net.minecraft.class_1865<?> serializer, net.minecraft.class_1856 template,
            net.minecraft.class_1856 base, net.minecraft.class_1856 addition, net.minecraft.class_7800 category,
            net.minecraft.class_1792 result) {
        return new LegacySmithingTransformRecipeBuilder(serializer, template, base, addition, category, result);
    }

    /** new SmithingTrimRecipeBuilder(serializer, category, template, base, addition) */
    public static net.minecraft.class_8076 smithingTrim(net.minecraft.class_1865<?> serializer, net.minecraft.class_7800 category,
            net.minecraft.class_1856 template, net.minecraft.class_1856 base, net.minecraft.class_1856 addition) {
        return new LegacySmithingTrimRecipeBuilder(serializer, category, template, base, addition);
    }

    /**
     * {@code output} as a 1.20.2 builder saving through its 1.20.3 superclass's save sees it: what that
     * save hands over (id, recipe object, advancement) goes in as the FinishedRecipe 1.20.2's save
     * built for the same id and advancement instead ({@link #accept}: a 1.20.2 output gets it as
     * such, vanilla's provider writes its JSON, any other output the recipe its JSON describes).
     */
    public static net.minecraft.class_8790 finishing(net.minecraft.class_8790 output,
            BiFunction<net.minecraft.class_2960, net.minecraft.class_8779, FinishedRecipe> finished) {
        return new net.minecraft.class_8790() {
            @Override
            public void method_53819(net.minecraft.class_2960 id, net.minecraft.class_1860<?> recipe, net.minecraft.class_8779 advancement) {
                accept(output, finished.apply(id, advancement));
            }

            @Override
            public net.minecraft.class_161.class_162 method_53818() {
                return output.method_53818();
            }
        };
    }

    // ---------------------------------------------------------------- RecipeProvider / VanillaRecipeProvider

    /**
     * The feature set 1.20.2's RecipeProvider.generateRecipes(output, family) / waxRecipes(output)
     * stand for in 1.20.3's (output, .., features) forms: every flag. 1.20.3 skips the blocks whose
     * features the set lacks; 1.20.2 generated every one (the caller checked the family itself).
     */
    public static net.minecraft.class_7699 allFeatures() {
        return net.minecraft.class_7701.field_40180.method_45383();
    }

    /** VanillaRecipeProvider.smithingTrims(): 1.20.3's (template, id) stream as 1.20.2's template -> id map */
    public static Map<net.minecraft.class_1792, net.minecraft.class_2960> trimTemplates(Stream<net.minecraft.class_7803.class_8905> templates) {
        return templates.collect(Collectors.toMap(net.minecraft.class_7803.class_8905::comp_2012,
            net.minecraft.class_7803.class_8905::comp_2013));
    }

    private CbDataRecipes() {
    }
}
