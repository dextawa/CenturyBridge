package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.facade.ItemOverride;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;

/**
 * BlockModel.Deserializer's 1.21.3 getOverrides(context, json): the model file's item overrides, which 1.21.4 no longer
 * reads; 1.21.3's deserialize asked it (virtually: a subclass's override counts) and the model kept the list.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_793.class_795.class)
public abstract class CbModelBlockModelDeserializerMixin {

    /** was getOverrides(context, json) (method_3452) */
    protected List<ItemOverride> method_3452(com.google.gson.JsonDeserializationContext context, com.google.gson.JsonObject json) {
        return CbModelBlockModel.overrides(json);
    }

    @Inject(method = "method_3451", at = @At("RETURN"))
    private void cb$overrides(com.google.gson.JsonElement element, java.lang.reflect.Type type, com.google.gson.JsonDeserializationContext context,
            CallbackInfoReturnable<net.minecraft.class_793> cir) {
        ((CbModelBlockModel.CbLegacyBlockModel) cir.getReturnValue()).cb$setOverrides(this.method_3452(context, element.getAsJsonObject()));
    }
}
