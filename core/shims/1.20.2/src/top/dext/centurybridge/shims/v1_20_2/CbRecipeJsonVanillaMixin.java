package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;

/**
 * RecipeManager.fromJson(id, json): 1.20.2 decodes the recipe with its serializer's codec() -- unchanged, so a
 * 1.20.2-era mod's mixin on that path acts as without CenturyBridge. The decoded recipe, as it is stored in fromJson's
 * local, is what CbRecipeJson.consult keeps unless the serializer's restored 1.20.1 read(id, json) (CbRecipeJson*),
 * changed by a converted mod's mixin, answers otherwise than its unmixed copy: then 1.20.1's answer, as 1.20.1's
 * RecipeManager.fromJson returned serializer.fromJson(id, json).
 */
@Mixin(class_1863.class)
public abstract class CbRecipeJsonVanillaMixin {

    @ModifyVariable(method = "method_17720", at = @At("STORE"), ordinal = 0, require = 1)
    private static class_1860<?> cb$consultLegacyReader(class_1860<?> read, class_2960 id, JsonObject json) {
        return CbRecipeJson.consult(read, id, json);
    }
}
