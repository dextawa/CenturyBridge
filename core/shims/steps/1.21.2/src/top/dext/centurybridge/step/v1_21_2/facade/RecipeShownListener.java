package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** RecipeShownListener of 1.21.1 (net/minecraft/class_515, gone in 1.21.2; classRenamed): told which recipes a recipe button showed */
@Environment(EnvType.CLIENT)
public interface RecipeShownListener {

    /** recipesShown(recipes) */
    void method_2646(List<net.minecraft.class_8786<?>> recipes);
}
