package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_10289;
import net.minecraft.class_1863;
import net.minecraft.class_7225;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** RecipeManager's recipe map and registries, for 1.21.1's RecipeManager members (CbItemRecipeManager) */
@Mixin(class_1863.class)
public interface CbItemRecipeManagerAccess {

    @Accessor("field_54638")
    class_10289 cb$recipeMap();

    @Accessor("field_54638")
    void cb$setRecipeMap(class_10289 recipes);

    @Accessor("field_54637")
    class_7225.class_7874 cb$registries();
}
