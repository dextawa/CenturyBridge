package top.dext.centurybridge.step.v26_1.rt;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.TransmuteRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import top.dext.centurybridge.step.v26_1.facade.CookingRecipeSerializer;
import top.dext.centurybridge.step.v26_1.facade.RecipeSerializer;

/**
 * 1.21.11's data/recipes builder members 26.1 reshaped (javap of class_5797 / class_2447 / class_2450 / class_2454 /
 * class_3981 / class_8074 / class_2456 / class_10243 / class_2446 against 26.1's):
 * <ul>
 * <li>RecipeBuilder.getResult(): the result item. 26.1 builders keep the result as an item stack template and answer
 *     defaultId() = getDefaultRecipeId(result), the result item's id (javap of every 26.1 builder's defaultId); a
 *     1.21.11 builder answers its own getResult.</li>
 * <li>RecipeBuilder.getDefaultRecipeId(item): 1.21.11 answered BuiltInRegistries.ITEM.getKey(item.asItem()).</li>
 * <li>Cooking: 1.21.11 chose the book category itself -- smelting: FOOD for an item with the food component, BLOCKS
 *     for a block item, else MISC (class_2454.method_46216); blasting: BLOCKS for a block item, else MISC
 *     (method_46217); generic(.., serializer, factory): by serializer (smelting / blasting as above, smoking and
 *     campfire FOOD, any other throws, method_46215). 26.1 takes the category. A 1.21.11 factory (the old
 *     Factory(group, category, input, result stack, experience, time)) becomes a 26.1 factory that keeps the
 *     1.21.11 serializer, so the recipe it makes gets the category 1.21.11 chose (LegacyCookingFactory).</li>
 * <li>stonecutting(input, category, result): 1.21.11 made one (count 1).</li>
 * <li>SmithingTransformRecipeBuilder(.., result item) / shapeless(.., result stack): 26.1 takes templates.</li>
 * <li>SpecialRecipeBuilder(factory of the crafting book category): 1.21.11's save applied it to MISC (javap
 *     class_2456.method_10475); 26.1's save asks a supplier.</li>
 * <li>RecipeProvider.oreSmelting / oreBlasting: 1.21.11 categorised each result as smelting / blasting above.</li>
 * </ul>
 */
public final class CbWallRecipeBuilders {
    private CbWallRecipeBuilders() {
    }

    // ---- getResult ----

    /**
     * a 1.21.11 call of builder.getResult(): a 1.21.11 builder's own (every one declares it -- abstract in 1.21.11's
     * interface -- and reaches 26.1 through the defaultId override hook's adapter, CbWallAiFills.LegacyRecipeBuilder);
     * a 26.1 builder's result template's item (the field 1.21.11's getResult answered, now a template; AW)
     */
    public static Item getResult(RecipeBuilder builder) {
        if (builder instanceof CbWallAiFills.LegacyRecipeBuilder legacy) {
            return legacy.getResult();
        }
        return superGetResult(builder);
    }

    public static Item getResult(ShapedRecipeBuilder builder) {
        return getResult((RecipeBuilder) builder);
    }

    public static Item getResult(ShapelessRecipeBuilder builder) {
        return getResult((RecipeBuilder) builder);
    }

    public static Item getResult(SimpleCookingRecipeBuilder builder) {
        return getResult((RecipeBuilder) builder);
    }

    public static Item getResult(SingleItemRecipeBuilder builder) {
        return getResult((RecipeBuilder) builder);
    }

    public static Item getResult(TransmuteRecipeBuilder builder) {
        return getResult((RecipeBuilder) builder);
    }

    /**
     * a 26.1 builder's result item: the result template's item for the five builders 1.21.11 answered getResult for
     * (each keeps it in its result field); for any other builder, the item its default id names (26.1's builders'
     * defaultId is getDefaultRecipeId(result), javap)
     */
    public static Item superGetResult(RecipeBuilder builder) {
        ItemStackTemplate result;
        if (builder instanceof ShapedRecipeBuilder b) {
            result = b.result;
        } else if (builder instanceof ShapelessRecipeBuilder b) {
            result = b.result;
        } else if (builder instanceof SimpleCookingRecipeBuilder b) {
            result = b.result;
        } else if (builder instanceof SingleItemRecipeBuilder b) {
            result = b.result;
        } else if (builder instanceof TransmuteRecipeBuilder b) {
            result = b.result;
        } else {
            return BuiltInRegistries.ITEM.getValue(builder.defaultId().identifier());
        }
        return result.item().value();
    }

    /** a 1.21.11 SingleItemRecipeBuilder subclass's super.getResult() */
    public static Item superGetResult(SingleItemRecipeBuilder builder) {
        return superGetResult((RecipeBuilder) builder);
    }

    /** RecipeBuilder.getDefaultRecipeId(item) */
    public static Identifier getDefaultRecipeId(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }

    // ---- cooking ----

    /** 1.21.11's smelting book category (class_2454.method_46216) */
    public static CookingBookCategory smeltingCategory(ItemLike result) {
        if (result.asItem().components().has(DataComponents.FOOD)) {
            return CookingBookCategory.FOOD;
        }
        return result.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
    }

    /** 1.21.11's blasting book category (class_2454.method_46217) */
    public static CookingBookCategory blastingCategory(ItemLike result) {
        return result.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
    }

    /** 1.21.11's book category by serializer (class_2454.method_46215) */
    public static CookingBookCategory genericCategory(RecipeSerializer<?> serializer, ItemLike result) {
        if (serializer == RecipeSerializer.field_9042) {
            return smeltingCategory(result);
        }
        if (serializer == RecipeSerializer.field_17084) {
            return blastingCategory(result);
        }
        if (serializer == RecipeSerializer.field_17085 || serializer == RecipeSerializer.field_17347) {
            return CookingBookCategory.FOOD;
        }
        throw new IllegalStateException("Unknown cooking recipe type");
    }

    /**
     * A 1.21.11 cooking factory as 26.1's: the recipe gets the book category 1.21.11 chose for the serializer it was
     * given with (or 26.1's, given none), the group of 26.1's book info and the result template's stack.
     */
    public static final class LegacyCookingFactory<T extends AbstractCookingRecipe> implements AbstractCookingRecipe.Factory<T> {
        final RecipeSerializer<?> serializer;
        final CookingRecipeSerializer.Factory<T> factory;

        LegacyCookingFactory(RecipeSerializer<?> serializer, CookingRecipeSerializer.Factory<T> factory) {
            this.serializer = serializer;
            this.factory = factory;
        }

        @Override
        public T create(Recipe.CommonInfo common, AbstractCookingRecipe.CookingBookInfo book, Ingredient input, ItemStackTemplate result,
                        float experience, int cookingTime) {
            CookingBookCategory category = this.serializer != null ? genericCategory(this.serializer, result.item().value()) : book.category();
            return this.factory.create(book.group(), category, input, result.create(), experience, cookingTime);
        }
    }

    /** a 1.21.11 factory passed with its serializer (generic, cookRecipes) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static AbstractCookingRecipe.Factory cookingFactory(RecipeSerializer serializer, CookingRecipeSerializer.Factory factory) {
        return new LegacyCookingFactory(serializer, factory);
    }

    /** 26.1's factory as 1.21.11's (handed to a 1.21.11 override): the recipe of 26.1's infos for the 1.21.11 arguments */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CookingRecipeSerializer.Factory legacyCookingFactory(AbstractCookingRecipe.Factory factory) {
        if (factory instanceof LegacyCookingFactory legacy) {
            return legacy.factory;
        }
        return (group, category, input, result, experience, cookingTime) -> factory.create(CbWallRecipeCtors.notifying(),
            new AbstractCookingRecipe.CookingBookInfo(category, group), input, ItemStackTemplate.fromNonEmptyStack(result), experience, cookingTime);
    }

    /** the 1.21.11 serializer of a 26.1 factory handed to a 1.21.11 cookRecipes override */
    @SuppressWarnings("rawtypes")
    public static RecipeSerializer legacyCookingSerializer(AbstractCookingRecipe.Factory factory, String name) {
        if (factory instanceof LegacyCookingFactory legacy && legacy.serializer != null) {
            return legacy.serializer;
        }
        // 26.1's own cookRecipes callers name the recipe kind by its serializer id ("smoking", "campfire_cooking";
        // javap VanillaRecipeProvider), as 1.21.11's passed that serializer
        return CbWallRecipes.vanilla(name);
    }

    // ---- results as templates ----

    /** stonecutting(input, category, result): one */
    public static int one() {
        return 1;
    }

    /** a result item as a template (SmithingTransformRecipeBuilder) */
    public static ItemStackTemplate template(Item item) {
        return new ItemStackTemplate(item);
    }

    /** new SmithingTransformRecipeBuilder(template, base, addition, category, result) */
    public static net.minecraft.data.recipes.SmithingTransformRecipeBuilder smithing(Ingredient template, Ingredient base, Ingredient addition,
                                                                                       RecipeCategory category, Item result) {
        return new net.minecraft.data.recipes.SmithingTransformRecipeBuilder(template, base, addition, category, template(result));
    }

    // ---- SpecialRecipeBuilder ----

    /** 1.21.11's special recipe factory as 26.1's supplier: applied to MISC, as 1.21.11's save did */
    public static Supplier<Recipe<?>> special(Function<CraftingBookCategory, Recipe<?>> factory) {
        return () -> factory.apply(CraftingBookCategory.MISC);
    }

    /** new SpecialRecipeBuilder(factory) */
    public static net.minecraft.data.recipes.SpecialRecipeBuilder specialBuilder(Function<CraftingBookCategory, Recipe<?>> factory) {
        return new net.minecraft.data.recipes.SpecialRecipeBuilder(special(factory));
    }

    // ---- RecipeProvider overrides ----

    /** 1.21.11's RecipeProvider.cookRecipes(name, serializer, factory, time), as a 1.21.11 provider overrides it */
    public interface LegacyCookRecipes {
        @SuppressWarnings("rawtypes")
        void cookRecipes(String name, RecipeSerializer serializer, CookingRecipeSerializer.Factory factory, int cookingTime);
    }

    /** 26.1's cookRecipes call on a 1.21.11 provider overriding the old shape (overrideHooks bridge) */
    @SuppressWarnings("rawtypes")
    public static void cookRecipes(LegacyCookRecipes provider, String name, AbstractCookingRecipe.Factory factory, int cookingTime) {
        provider.cookRecipes(name, legacyCookingSerializer(factory, name), legacyCookingFactory(factory), cookingTime);
    }

    /** 1.21.11's RecipeProvider.shapeless(category, result stack), as a 1.21.11 provider overrides it */
    public interface LegacyShapeless {
        ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemStack result);
    }

    /** 26.1's shapeless(category, template) call on a 1.21.11 provider overriding the old shape (overrideHooks bridge) */
    public static ShapelessRecipeBuilder shapeless(LegacyShapeless provider, RecipeCategory category, ItemStackTemplate result) {
        return provider.shapeless(category, result.create());
    }
}
