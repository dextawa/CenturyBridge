package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipes;

/**
 * 1.21.11's CustomRecipe took its crafting book category in its constructor and answered it from category();
 * 26.1's has no constructor argument and answers MISC. A 1.21.11 recipe class's super(category) keeps the
 * category here (rt/CbWallRecipes.customCategory, the constructor reshape's post) and category() answers it.
 */
@Mixin(CustomRecipe.class)
public abstract class CbWallCustomRecipeMixin implements CbWallRecipes.CustomCategory {

    @Unique
    private CraftingBookCategory cb$category;

    @Override
    public void cb$setCategory(CraftingBookCategory category) {
        this.cb$category = category;
    }

    @Inject(method = "category", at = @At("HEAD"), cancellable = true)
    private void cb$category(CallbackInfoReturnable<CraftingBookCategory> cir) {
        if (this.cb$category != null) {
            cir.setReturnValue(this.cb$category);
        }
    }
}
