package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10260;
import net.minecraft.class_507;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** AbstractRecipeBookScreen.recipeBookComponent (private): a furnace screen's component, read by a 1.21.1 mod (CbRestRecipeBookClient) */
@Environment(EnvType.CLIENT)
@Mixin(class_10260.class)
public interface CbRestRecipeBookScreenAccessor {

    @Accessor("field_54474")
    class_507<?> cb$component();
}
