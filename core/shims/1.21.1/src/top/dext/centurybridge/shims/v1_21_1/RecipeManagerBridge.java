package top.dext.centurybridge.shims.v1_21_1;

import top.dext.centurybridge.rt.v1_21_1.Trackers;

import java.util.Map;
import net.minecraft.class_1863;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_3695;
import net.minecraft.class_8786;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Rebuilds the recipe identity map whenever recipes (re)load, backing legacy
 * Recipe.getId() calls (redirected to Statics.method_8114). Runtime namespace
 * is intermediary, so injection targets are written literally -- no refmap.
 */
@Mixin(class_1863.class)
public abstract class RecipeManagerBridge {

    @Inject(method = "method_20705", at = @At("TAIL"))
    private void cb$trackApply(Map<class_2960, com.google.gson.JsonElement> map,
                               class_3300 rm, class_3695 profiler, CallbackInfo ci) {
        cb$rebuild();
    }

    @Inject(method = "method_20702", at = @At("TAIL"))
    private void cb$trackSet(Iterable<class_8786<?>> recipes, CallbackInfo ci) {
        cb$rebuild();
    }

    private void cb$rebuild() {
        Trackers.RECIPE_IDS.clear();
        for (class_8786<?> entry : ((class_1863) (Object) this).method_8126()) {
            Trackers.RECIPE_IDS.put(entry.comp_1933(), entry.comp_1932());
        }
    }
}
