package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.Vec3;

/**
 * frameDefaults of this step's override hooks (common side): what a 1.21.11 mod's old override, entered by
 * the mod itself rather than through the synthesized 26.1 shape, passes on in its rewritten super call for
 * an argument 1.21.11 had no place for -- the value 1.21.11's own code used or computed there.
 */
public final class CbWallFrames {

    private CbWallFrames() {
    }

    /** Brain.addActivity(activity, behaviours): 1.21.11's body passed no conditions (an ImmutableSet) ... */
    public static Set<?> noConditions() {
        return ImmutableSet.of();
    }

    /** ... and no memories to erase when stopped (a HashSet) */
    public static Set<?> noMemoriesToErase() {
        return Sets.newHashSet();
    }

    /**
     * Zombie.handleAttributes(difficulty): 1.21.11's never reset the health to the maximum; 26.1's does
     * unless the zombie is converted, loaded or travelled -- CONVERSION keeps 1.21.11's behaviour.
     */
    public static EntitySpawnReason zombieAttributesReason() {
        return EntitySpawnReason.CONVERSION;
    }

    /**
     * Player.setIgnoreFallDamageFromCurrentImpulse(ignore): 1.21.11's left the impulse impact position as
     * it was (callers set it themselves); 26.1's sets it to the position passed.
     */
    public static Vec3 currentImpulseImpactPos(Player player) {
        return player.currentImpulseImpactPos;
    }

    /** Player.isWithinAttackRange(box, buffer): 1.21.11 used the attack range of the active item */
    public static ItemStack activeItem(Player player) {
        return player.getActiveItem();
    }

    /** CustomBossEvents.create(id, name): 1.21.11's event took an insecure random UUID; 26.1's draws it from this */
    public static RandomSource bossEventRandom() {
        return RandomSource.create();
    }

    /** RecipeProvider.oreSmelting: 1.21.11's builder chose the book category from the result (food, block, misc) */
    public static CookingBookCategory smeltingCategory(Object provider, List<?> ingredients, RecipeCategory category,
            ItemLike result, float experience, int cookingTime, String group) {
        Item item = result.asItem();
        if (item.components().has(DataComponents.FOOD)) {
            return CookingBookCategory.FOOD;
        }
        return item instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
    }

    /** RecipeProvider.oreBlasting: 1.21.11's builder chose block for a block result, else misc */
    public static CookingBookCategory blastingCategory(Object provider, List<?> ingredients, RecipeCategory category,
            ItemLike result, float experience, int cookingTime, String group) {
        return result.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
    }

    /**
     * SignApplicator.tryApplyToSign / canApplyToSign: the stack being applied -- 1.21.11's sign used the
     * stack in the hand that held the applicator (the main hand first, as interaction tries it first).
     */
    public static ItemStack applicatorStack(Object applicator, Level level, SignBlockEntity sign, boolean front, Player player) {
        return heldStack(applicator, player);
    }

    public static ItemStack applicatorStack(Object applicator, SignText text, Player player) {
        return heldStack(applicator, player);
    }

    private static ItemStack heldStack(Object applicator, Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() == applicator) {
                return stack;
            }
        }
        return player.getMainHandItem();
    }
}
