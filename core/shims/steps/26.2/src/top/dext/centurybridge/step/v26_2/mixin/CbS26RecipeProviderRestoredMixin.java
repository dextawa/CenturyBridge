package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2 RecipeProvider.hangingSign(sign, planks): shaped DECORATIONS x6, group "hanging_sign", '#' = planks, 'X' = iron
 * chain, "X X" / "###" / "###", unlocked by has_stripped_logs = has(planks), saved to the provider's output. 26.2's
 * hangingSignBuilder(sign, Ingredient) builds the same recipe up to the pattern (javap: identical through @51, 26.1.2's
 * define(ItemLike) being define(Ingredient.of(item))) and returns it; restored as that builder plus 26.1.2's unlock and save.
 * Priority below every mod's.
 */
@Mixin(value = RecipeProvider.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26RecipeProviderRestoredMixin {

    @Shadow
    @Final
    protected RecipeOutput output;

    @Shadow
    protected abstract net.minecraft.data.recipes.RecipeBuilder hangingSignBuilder(ItemLike sign, Ingredient planks);

    @Shadow
    protected abstract Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item);

    /** hangingSign(ItemLike, ItemLike) */
    protected void hangingSign(ItemLike sign, ItemLike planks) {
        this.hangingSignBuilder(sign, Ingredient.of(planks)).unlockedBy("has_stripped_logs", this.has(planks)).save(this.output);
    }
}
