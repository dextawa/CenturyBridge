package top.dext.centurybridge.shims.v1_20_2;

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
import top.dext.centurybridge.rt.v1_20_2.CbRecipeIds;

/**
 * Keeps every recipe a RecipeManager holds with its id (CbRecipeIds), whenever recipes (re)load on
 * the server or arrive on the client -- backing legacy Recipe.getId() calls and the Recipe -> entry
 * conversions. The id map is keyed by recipe identity and weak, so both managers of a single-player
 * game keep their own recipes and reloads need no clearing. Runtime namespace is intermediary, so
 * injection targets are written literally -- no refmap.
 */
@Mixin(class_1863.class)
public abstract class RecipeManagerBridge {

    @Inject(method = "method_20705", at = @At("TAIL"))
    private void cb$trackApply(Map<class_2960, com.google.gson.JsonElement> map,
                               class_3300 rm, class_3695 profiler, CallbackInfo ci) {
        cb$track();
    }

    @Inject(method = "method_20702", at = @At("TAIL"))
    private void cb$trackSet(Iterable<class_8786<?>> recipes, CallbackInfo ci) {
        cb$track();
    }

    private void cb$track() {
        for (class_8786<?> entry : ((class_1863) (Object) this).method_8126()) {
            CbRecipeIds.remember(entry.comp_1933(), entry.comp_1932());
        }
    }
}
