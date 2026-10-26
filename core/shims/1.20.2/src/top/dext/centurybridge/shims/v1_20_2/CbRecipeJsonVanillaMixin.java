package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_1863;
import net.minecraft.class_8786;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;

/**
 * RecipeManager.fromJson(id, json): 1.20.2 decodes the recipe with its serializer's codec() -- unchanged, so a
 * 1.20.2-era mod's mixin on that path acts as without CenturyBridge. The RecipeHolder it returns is what
 * CbRecipeJson.consultHolder keeps unless the serializer's restored 1.20.1 read(id, json) (CbRecipeJson*), changed by a
 * converted mod's mixin, answers otherwise than its unmixed copy: then a holder of 1.20.1's answer, as 1.20.1's
 * RecipeManager.fromJson returned serializer.fromJson(id, json).
 *
 * No handler captures fromJson's arguments by position, so a later release reshaping them leaves both applicable (1.20.5
 * adds a HolderLookup.Provider, 1.21.2 makes the id a ResourceKey): the JSON is taken by its type at HEAD (argsOnly), the
 * id from the returned holder (holder.id(), a call the later steps carry like any mod's).
 */
@Mixin(class_1863.class)
public abstract class CbRecipeJsonVanillaMixin {

    @ModifyVariable(method = "method_17720", at = @At("HEAD"), argsOnly = true, require = 1)
    private static JsonObject cb$carryJson(JsonObject json) {
        CbRecipeJson.carryJson(json);
        return json;
    }

    @Inject(method = "method_17720", at = @At("RETURN"), cancellable = true, require = 1)
    private static void cb$consultLegacyReader(CallbackInfoReturnable<class_8786<?>> cir) {
        cir.setReturnValue(CbRecipeJson.consultHolder(cir.getReturnValue()));
    }
}
