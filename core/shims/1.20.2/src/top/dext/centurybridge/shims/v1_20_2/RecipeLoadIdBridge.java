package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_1863;
import net.minecraft.class_2960;
import net.minecraft.class_8786;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbRecipes;

/**
 * RecipeManager.deserialize(id, json) decodes one datapack recipe through the
 * serializer's codec; the id goes nowhere near the codec. A 1.20.1 serializer's
 * read(id, json) needs it: publish it to CbRecipes.CURRENT_ID for the duration.
 */
@Mixin(class_1863.class)
public abstract class RecipeLoadIdBridge {

    @Inject(method = "method_17720", at = @At("HEAD"))
    private static void cb$enter(class_2960 id, JsonObject json, CallbackInfoReturnable<class_8786<?>> cir) {
        CbRecipes.CURRENT_ID.set(id);
    }

    @Inject(method = "method_17720", at = @At("RETURN"))
    private static void cb$exit(class_2960 id, JsonObject json, CallbackInfoReturnable<class_8786<?>> cir) {
        CbRecipes.CURRENT_ID.remove();
    }
}
