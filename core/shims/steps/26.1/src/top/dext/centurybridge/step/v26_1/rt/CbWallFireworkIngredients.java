package top.dext.centurybridge.step.v26_1.rt;

import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 1.21.11 FireworkRocketRecipe / FireworkStarRecipe static ingredient constants (class_1851 / class_1853, opened by
 * access wideners). 26.1 (javap) turned them into per-recipe instance fields filled from recipe data, so a static has
 * no 26.1 home; each answers the value 1.21.11's clinit computed (javap): Ingredient.of(PAPER / GUNPOWDER /
 * FIREWORK_STAR), Ingredient.of(DIAMOND / GLOWSTONE_DUST / GUNPOWDER), and SHAPE_BY_ITEM = Map.of(FIRE_CHARGE LARGE_BALL,
 * FEATHER BURST, GOLD_NUGGET STAR, the seven heads CREEPER). One instance each, made on first read (after bootstrap),
 * as 1.21.11 held one per class. These are the vanilla defaults; a datapack that changes 26.1's firework recipes is not
 * reflected (1.21.11 had no such data either).
 */
public final class CbWallFireworkIngredients {
    private CbWallFireworkIngredients() {
    }

    private static final class Holder {
        static final Ingredient PAPER = Ingredient.of(Items.PAPER);
        static final Ingredient GUNPOWDER = Ingredient.of(Items.GUNPOWDER);
        static final Ingredient STAR = Ingredient.of(Items.FIREWORK_STAR);
        static final Ingredient TRAIL = Ingredient.of(Items.DIAMOND);
        static final Ingredient TWINKLE = Ingredient.of(Items.GLOWSTONE_DUST);
        static final Ingredient STAR_GUNPOWDER = Ingredient.of(Items.GUNPOWDER);
        static final Map<Item, FireworkExplosion.Shape> SHAPE_BY_ITEM = Map.of(
            Items.FIRE_CHARGE, FireworkExplosion.Shape.LARGE_BALL,
            Items.FEATHER, FireworkExplosion.Shape.BURST,
            Items.GOLD_NUGGET, FireworkExplosion.Shape.STAR,
            Items.SKELETON_SKULL, FireworkExplosion.Shape.CREEPER,
            Items.WITHER_SKELETON_SKULL, FireworkExplosion.Shape.CREEPER,
            Items.CREEPER_HEAD, FireworkExplosion.Shape.CREEPER,
            Items.PLAYER_HEAD, FireworkExplosion.Shape.CREEPER,
            Items.DRAGON_HEAD, FireworkExplosion.Shape.CREEPER,
            Items.ZOMBIE_HEAD, FireworkExplosion.Shape.CREEPER,
            Items.PIGLIN_HEAD, FireworkExplosion.Shape.CREEPER);
    }

    public static Ingredient rocketPaper() {
        return Holder.PAPER;
    }

    public static Ingredient rocketGunpowder() {
        return Holder.GUNPOWDER;
    }

    public static Ingredient rocketStar() {
        return Holder.STAR;
    }

    @SuppressWarnings("rawtypes")
    public static Map starShapeByItem() {
        return Holder.SHAPE_BY_ITEM;
    }

    public static Ingredient starTrail() {
        return Holder.TRAIL;
    }

    public static Ingredient starTwinkle() {
        return Holder.TWINKLE;
    }

    public static Ingredient starGunpowder() {
        return Holder.STAR_GUNPOWDER;
    }
}
