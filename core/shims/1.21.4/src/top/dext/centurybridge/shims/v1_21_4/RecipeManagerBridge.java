package top.dext.centurybridge.shims.v1_21_4;

import top.dext.centurybridge.rt.v1_21_4.Trackers;

import net.minecraft.class_10289;
import net.minecraft.class_1863;
import net.minecraft.class_3300;
import net.minecraft.class_3695;
import net.minecraft.class_8786;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Rebuilds the recipe identity map whenever recipes (re)load, backing legacy
 * Recipe.getId() calls (redirected to Statics.method_8114). 1.21.4 funnels
 * loading through PreparedRecipes (class_10289), so the one apply hook sees
 * every entry. Runtime namespace is intermediary -- no refmap.
 */
@Mixin(class_1863.class)
public abstract class RecipeManagerBridge {

    @Inject(method = "method_20705", at = @At("TAIL"))
    private void cb$trackApply(class_10289 prepared, class_3300 rm, class_3695 profiler, CallbackInfo ci) {
        Trackers.RECIPE_IDS.clear();
        for (class_8786<?> entry : prepared.method_64695()) {
            Trackers.RECIPE_IDS.put(entry.comp_1933(), entry.comp_1932().method_29177());
        }
    }
}
