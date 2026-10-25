package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11's AbstractFurnaceBlockEntity.burn(registryAccess, recipe, input, items, maxStackSize) (class_2609.method_11203):
 * when the recipe is present and canBurn holds, assemble the result, put it into the output slot (or grow the output),
 * turn a bucket in the fuel slot into a water bucket when a wet sponge was smelted, shrink the input and answer true;
 * otherwise answer false, and serverTick marked the recipe used only on a true answer. 26.1 split it: serverTick
 * assembles the result itself, checks canBurn(items, maxStackSize, result) and calls burn(items, input, result) (void),
 * then setRecipeUsed unconditionally.
 *
 * Vanilla's path goes through the restored method: serverTick's burn(items, input, result) call is deferred to its
 * setRecipeUsed(recipe) call, the next instruction, which runs the restored burn with the recipe, a SingleRecipeInput of
 * the same input stack and getMaxStackSize() (the values serverTick holds there) and marks the recipe used only when it
 * answers true, as 1.21.11's serverTick did. The 1.21.11 canBurn precondition is cb$canBurn: input not empty, the
 * assembled result not empty and 26.1's canBurn -- where 1.21.11's canBurn injectors already land (mixinTargets).
 *
 * Known difference: the output grows by the result's count, as 26.1 does (1.21.11 grew it by 1, its canBurn reserved
 * one item); a 26.1 result of several items would otherwise lose all but one. So the body has one int constant 1 less
 * than 1.21.11's, which the judge sees as an anchor-count change for CONSTANT intValue=1 selectors.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class CbWallFurnaceBurnMixin {

    @Shadow
    protected NonNullList<ItemStack> items;

    @Unique
    private ItemStack cb$burnInput;

    @Shadow
    private static boolean canBurn(NonNullList<ItemStack> items, int maxStackSize, ItemStack result) {
        throw new AssertionError();
    }

    /** 1.21.11 AbstractFurnaceBlockEntity.burn (class_2609.method_11203) */
    @CbVanillaReads("CbWallFurnaceBurnMixin: serverTick's burn(items, input, result) runs this method at its "
        + "setRecipeUsed(recipe) call, which it gates on the answer")
    private static boolean burn(RegistryAccess registryAccess, RecipeHolder<? extends AbstractCookingRecipe> recipe,
                                SingleRecipeInput input, NonNullList<ItemStack> items, int maxStackSize) {
        if (recipe != null && cb$canBurn(recipe, input, items, maxStackSize)) {
            ItemStack inputStack = items.get(0);
            ItemStack result = recipe.value().assemble(input);
            ItemStack output = items.get(2);
            if (output.isEmpty()) {
                items.set(2, result.copy());
            } else if (ItemStack.isSameItemSameComponents(output, result)) {
                output.grow(result.getCount());
            }
            if (inputStack.is(Blocks.WET_SPONGE.asItem()) && !items.get(1).isEmpty() && items.get(1).is(Items.BUCKET)) {
                items.set(1, new ItemStack(Items.WATER_BUCKET));
            }
            inputStack.shrink(1);
            return true;
        }
        return false;
    }

    /** 1.21.11 canBurn(registryAccess, recipe, input, items, maxStackSize): the input and the assembled result */
    @Unique
    private static boolean cb$canBurn(RecipeHolder<? extends AbstractCookingRecipe> recipe, SingleRecipeInput input,
                                      NonNullList<ItemStack> items, int maxStackSize) {
        if (items.get(0).isEmpty()) {
            return false;
        }
        ItemStack result = recipe.value().assemble(input);
        return !result.isEmpty() && canBurn(items, maxStackSize, result);
    }

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/"
        + "AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;"
        + "Lnet/minecraft/world/item/ItemStack;)V"))
    private static void cb$deferBurn(NonNullList<ItemStack> items, ItemStack input, ItemStack result, ServerLevel level,
                                     BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity) {
        ((CbWallFurnaceBurnMixin) (Object) entity).cb$burnInput = input;
    }

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/"
        + "AbstractFurnaceBlockEntity;setRecipeUsed(Lnet/minecraft/world/item/crafting/RecipeHolder;)V"))
    @SuppressWarnings("unchecked")
    private static void cb$burnThenUse(AbstractFurnaceBlockEntity self, RecipeHolder<?> recipe, ServerLevel level,
                                       BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity) {
        CbWallFurnaceBurnMixin m = (CbWallFurnaceBurnMixin) (Object) self;
        ItemStack input = m.cb$burnInput;
        m.cb$burnInput = null;
        if (burn(level.registryAccess(), (RecipeHolder<? extends AbstractCookingRecipe>) recipe,
            new SingleRecipeInput(input), m.items, self.getMaxStackSize())) {
            self.setRecipeUsed(recipe);
        }
    }
}
