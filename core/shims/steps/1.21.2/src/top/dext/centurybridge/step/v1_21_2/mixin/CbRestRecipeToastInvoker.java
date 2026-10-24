package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_366;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** RecipeToast's private constructor and addItem: a 1.21.1 new RecipeToast(recipe) (CbRestRecipeBookClient) */
@Environment(EnvType.CLIENT)
@Mixin(class_366.class)
public interface CbRestRecipeToastInvoker {

    @Invoker("<init>")
    static class_366 cb$new() {
        throw new AssertionError();
    }

    @Invoker("method_1984")
    void cb$addItem(class_1799 station, class_1799 result);
}
