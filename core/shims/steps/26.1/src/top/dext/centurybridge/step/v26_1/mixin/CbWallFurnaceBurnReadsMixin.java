package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbMixedMembers;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * serverTick's smelting step reads the restored 1.21.11 burn (CbWallFurnaceBurnMixin) only when a converted mod mixes
 * it (rt/CbMixedMembers): its burn(items, input, result) call is then deferred to the setRecipeUsed(recipe) call that
 * follows, which runs the restored burn and marks the recipe used only on a true answer. Unmixed, both of 26.1's calls
 * run as they are. Default priority: injectors into a 26.1 method.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class CbWallFurnaceBurnReadsMixin {

    /** the restored member, as rt/CbMixedMembers names it */
    @Unique
    private static final String CB_BURN = "net/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity.burn"
        + "(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;"
        + "Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/core/NonNullList;I)Z";

    @Unique
    private ItemStack cb$burnInput;

    @Shadow
    private static void burn(NonNullList<ItemStack> items, ItemStack input, ItemStack result) {
        throw new AssertionError();
    }

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/"
        + "AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;"
        + "Lnet/minecraft/world/item/ItemStack;)V"))
    private static void cb$deferBurn(NonNullList<ItemStack> items, ItemStack input, ItemStack result, ServerLevel level,
                                     BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity) {
        if (!CbMixedMembers.mixed(CB_BURN)) {
            burn(items, input, result);
            return;
        }
        ((CbWallFurnaceBurnReadsMixin) (Object) entity).cb$burnInput = input;
    }

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/"
        + "AbstractFurnaceBlockEntity;setRecipeUsed(Lnet/minecraft/world/item/crafting/RecipeHolder;)V"))
    private static void cb$burnThenUse(AbstractFurnaceBlockEntity self, RecipeHolder<?> recipe, ServerLevel level,
                                       BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity) {
        CbWallFurnaceBurnReadsMixin m = (CbWallFurnaceBurnReadsMixin) (Object) self;
        ItemStack input = m.cb$burnInput;
        if (input == null) {
            self.setRecipeUsed(recipe);
            return;
        }
        m.cb$burnInput = null;
        if (((CbWallLegacyCalls.FurnaceLegacy) self).cb$burn(level.registryAccess(), recipe, new SingleRecipeInput(input),
            self.getMaxStackSize())) {
            self.setRecipeUsed(recipe);
        }
    }
}
