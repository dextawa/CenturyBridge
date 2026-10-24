package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipes;

/**
 * 1.21.11's ShapedRecipe / ShapelessRecipe / TransmuteRecipe let subclasses override group(), category(),
 * showNotification() and placementInfo(); 26.1's NormalCraftingRecipe made them final. A 1.21.11 subclass's
 * override is renamed (overrideRenames, its adapter) and answers here; its own super call reaches the final
 * member again, which then runs 26.1's body -- 1.21.11's super body (the recipe's own group, category,
 * notification flag and placement, from the same constructor arguments).
 */
@Mixin(NormalCraftingRecipe.class)
public abstract class CbWallNormalCraftingRecipeMixin {

    @Inject(method = "group", at = @At("HEAD"), cancellable = true)
    private void cb$group(CallbackInfoReturnable<String> cir) {
        if (this instanceof CbWallRecipes.LegacyGroup legacy && CbWallRecipes.enterOverride("group", "cb$legacyGroup", this)) {
            try {
                cir.setReturnValue(legacy.cb$legacyGroup());
            } finally {
                CbWallRecipes.exitOverride("group", this);
            }
        }
    }

    @Inject(method = "category", at = @At("HEAD"), cancellable = true)
    private void cb$category(CallbackInfoReturnable<CraftingBookCategory> cir) {
        if (this instanceof CbWallRecipes.LegacyCategory legacy && CbWallRecipes.enterOverride("category", "cb$legacyCategory", this)) {
            try {
                cir.setReturnValue(legacy.cb$legacyCategory());
            } finally {
                CbWallRecipes.exitOverride("category", this);
            }
        }
    }

    @Inject(method = "showNotification", at = @At("HEAD"), cancellable = true)
    private void cb$showNotification(CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof CbWallRecipes.LegacyShowNotification legacy && CbWallRecipes.enterOverride("showNotification", "cb$legacyShowNotification", this)) {
            try {
                cir.setReturnValue(legacy.cb$legacyShowNotification());
            } finally {
                CbWallRecipes.exitOverride("showNotification", this);
            }
        }
    }

    @Inject(method = "placementInfo", at = @At("HEAD"), cancellable = true)
    private void cb$placementInfo(CallbackInfoReturnable<PlacementInfo> cir) {
        if (this instanceof CbWallRecipes.LegacyPlacementInfo legacy && CbWallRecipes.enterOverride("placementInfo", "cb$legacyPlacementInfo", this)) {
            try {
                cir.setReturnValue(legacy.cb$legacyPlacementInfo());
            } finally {
                CbWallRecipes.exitOverride("placementInfo", this);
            }
        }
    }
}
