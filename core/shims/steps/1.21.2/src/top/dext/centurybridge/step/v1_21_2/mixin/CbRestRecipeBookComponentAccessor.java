package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1729;
import net.minecraft.class_507;
import net.minecraft.class_9934;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.21.2 RecipeBookComponent internals the 1.21.1 API over it reaches (facade/RecipeBookComponent.Vanilla) */
@Environment(EnvType.CLIENT)
@Mixin(class_507.class)
public interface CbRestRecipeBookComponentAccessor {

    @Accessor("field_3095")
    class_1729 cb$menu();

    @Accessor("field_52843")
    class_9934 cb$ghostSlots();

    @Invoker("method_2579")
    void cb$initVisuals();

    @Invoker("method_2588")
    void cb$sendUpdateSettings();

    @Invoker("method_17064")
    net.minecraft.class_2561 cb$recipeFilterName();
}
