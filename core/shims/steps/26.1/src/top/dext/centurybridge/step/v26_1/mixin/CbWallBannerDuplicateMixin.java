package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipes;

/**
 * A banner duplicate recipe built by a 1.21.11 constructor (rt/CbWallSpecialRecipes: any banner) duplicates as
 * 1.21.11's did -- the patterned banner of the grid (1 to 6 layers), copied as one -- instead of 26.1's per-colour
 * result item; 26.1's own (data) recipes are untouched.
 */
@Mixin(BannerDuplicateRecipe.class)
public abstract class CbWallBannerDuplicateMixin {

    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("HEAD"), cancellable = true)
    private void cb$legacyAssemble(CraftingInput input, CallbackInfoReturnable<ItemStack> cir) {
        if (((CbWallRecipes.CustomCategory) (Object) this).cb$legacyCategory() == null) {
            return;
        }
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                int layers = stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY).layers().size();
                if (layers > 0 && layers <= 6) {
                    cir.setReturnValue(stack.copyWithCount(1));
                    return;
                }
            }
        }
        cir.setReturnValue(ItemStack.EMPTY);
    }
}
