package top.dext.centurybridge.step.v26_1.rt;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.DecoratedPotRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.FireworkStarFadeRecipe;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.crafting.ShieldDecorationRecipe;

/**
 * 1.21.11's special recipe constructors, which took only a crafting book category: 26.1's take the recipe's
 * ingredients and result (its special recipes are data). A 1.21.11 `new FireworkRocketRecipe(category)` -- or a
 * subclass's super(category) -- builds 26.1's recipe with the parameters 26.1's own data gives it (data/minecraft/
 * recipe/firework_rocket.json ...), which match the grids 1.21.11's hard-coded body matched, and keeps the category
 * (CbWallRecipes.customCategory). Banner duplication was one recipe for every colour in 1.21.11 and is one per colour
 * in 26.1: a 1.21.11-built one takes any banner and duplicates as 1.21.11 did (mixin/CbWallBannerDuplicateMixin).
 */
public final class CbWallSpecialRecipes {

    private CbWallSpecialRecipes() {
    }

    private static Ingredient tag(TagKey<Item> tag) {
        return Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(tag));
    }

    // ---- banner duplicate: any banner, the patterned one copied (result only names the kind)

    public static Ingredient banners() {
        return tag(ItemTags.BANNERS);
    }

    public static ItemStackTemplate whiteBanner() {
        return new ItemStackTemplate(Items.WHITE_BANNER);
    }

    public static void category(BannerDuplicateRecipe recipe, CraftingBookCategory category) {
        CbWallRecipes.customCategory(recipe, category);
    }

    // ---- book cloning: written books onto writable books, original or copy

    public static Ingredient writtenBook() {
        return Ingredient.of(Items.WRITTEN_BOOK);
    }

    public static Ingredient bookCloningTarget() {
        return tag(ItemTags.BOOK_CLONING_TARGET);
    }

    public static MinMaxBounds.Ints bookGenerations() {
        return BookCloningRecipe.DEFAULT_BOOK_GENERATION_RANGES;
    }

    public static ItemStackTemplate writtenBookResult() {
        return new ItemStackTemplate(Items.WRITTEN_BOOK);
    }

    public static void category(BookCloningRecipe recipe, CraftingBookCategory category) {
        CbWallRecipes.customCategory(recipe, category);
    }

    // ---- decorated pot

    public static Ingredient potIngredient() {
        return tag(ItemTags.DECORATED_POT_INGREDIENTS);
    }

    public static ItemStackTemplate decoratedPot() {
        return new ItemStackTemplate(Items.DECORATED_POT);
    }

    public static void category(DecoratedPotRecipe recipe, CraftingBookCategory category) {
        CbWallRecipes.customCategory(recipe, category);
    }

    // ---- firework rocket: paper, gunpowder, stars; three rockets

    public static Ingredient paper() {
        return Ingredient.of(Items.PAPER);
    }

    public static Ingredient gunpowder() {
        return Ingredient.of(Items.GUNPOWDER);
    }

    public static Ingredient fireworkStar() {
        return Ingredient.of(Items.FIREWORK_STAR);
    }

    public static ItemStackTemplate threeRockets() {
        return new ItemStackTemplate(Items.FIREWORK_ROCKET, 3);
    }

    public static void category(FireworkRocketRecipe recipe, CraftingBookCategory category) {
        CbWallRecipes.customCategory(recipe, category);
    }

    // ---- firework star fade / firework star

    public static Ingredient dyes() {
        return tag(ItemTags.DYES);
    }

    public static ItemStackTemplate fireworkStarResult() {
        return new ItemStackTemplate(Items.FIREWORK_STAR);
    }

    public static void category(FireworkStarFadeRecipe recipe, CraftingBookCategory category) {
        CbWallRecipes.customCategory(recipe, category);
    }

    public static Map<FireworkExplosion.Shape, Ingredient> starShapes() {
        Map<FireworkExplosion.Shape, Ingredient> shapes = new EnumMap<>(FireworkExplosion.Shape.class);
        shapes.put(FireworkExplosion.Shape.BURST, Ingredient.of(Items.FEATHER));
        shapes.put(FireworkExplosion.Shape.CREEPER, tag(ItemTags.SKULLS));
        shapes.put(FireworkExplosion.Shape.LARGE_BALL, Ingredient.of(Items.FIRE_CHARGE));
        shapes.put(FireworkExplosion.Shape.STAR, Ingredient.of(Items.GOLD_NUGGET));
        return shapes;
    }

    public static Ingredient diamond() {
        return Ingredient.of(Items.DIAMOND);
    }

    public static Ingredient glowstoneDust() {
        return Ingredient.of(Items.GLOWSTONE_DUST);
    }

    public static void category(FireworkStarRecipe recipe, CraftingBookCategory category) {
        CbWallRecipes.customCategory(recipe, category);
    }

    // ---- map extending: a filled map amid paper

    public static Ingredient filledMap() {
        return Ingredient.of(Items.FILLED_MAP);
    }

    public static ItemStackTemplate filledMapResult() {
        return new ItemStackTemplate(Items.FILLED_MAP);
    }

    public static void category(MapExtendingRecipe recipe, CraftingBookCategory category) {
        CbWallRecipes.customCategory(recipe, category);
    }

    // ---- repair item / shield decoration

    public static void category(RepairItemRecipe recipe, CraftingBookCategory category) {
        CbWallRecipes.customCategory(recipe, category);
    }

    public static Ingredient shield() {
        return Ingredient.of(Items.SHIELD);
    }

    public static ItemStackTemplate shieldResult() {
        return new ItemStackTemplate(Items.SHIELD);
    }

    public static void category(ShieldDecorationRecipe recipe, CraftingBookCategory category) {
        CbWallRecipes.customCategory(recipe, category);
    }
}
