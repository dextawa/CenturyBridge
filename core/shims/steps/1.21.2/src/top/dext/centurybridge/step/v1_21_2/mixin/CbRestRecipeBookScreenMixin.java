package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10260;
import net.minecraft.class_507;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent;
import top.dext.centurybridge.step.v1_21_2.facade.RecipeUpdateListener;

/**
 * 1.21.2's recipe-book screens (AbstractRecipeBookScreen: inventory, crafting table, furnaces) are 1.21.1
 * RecipeUpdateListeners, as 1.21.1's were: getRecipeBookComponent() answers their component -- the 1.21.1 one a
 * 1.21.1 screen handed in, else 1.21.2's through the 1.21.1 API (RecipeBookComponent.of).
 */
@Environment(EnvType.CLIENT)
@Mixin(class_10260.class)
public abstract class CbRestRecipeBookScreenMixin implements RecipeUpdateListener {

    @Shadow
    @Final
    private class_507<?> field_54474;

    @Override
    public RecipeBookComponent method_2659() {
        return RecipeBookComponent.of(this.field_54474);
    }
}
