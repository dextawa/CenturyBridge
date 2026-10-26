package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;
import top.dext.centurybridge.step.v1_21_4.rt.CbPrioritySplitClient;

/**
 * The injector into 1.21.4's own BlockModel.Deserializer.deserialize that goes with
 * {@link CbModelBlockModelDeserializerMixin} (its restored 1.21.3 member stays there, at {@code CbVanillaReads.PRIORITY}):
 * the model keeps the overrides 1.21.3's getOverrides answers.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_793.class_795.class)
public abstract class CbModelBlockModelDeserializerMixinHooks {

    @Inject(method = "method_3451", at = @At("RETURN"))
    private void cb$overrides(com.google.gson.JsonElement element, java.lang.reflect.Type type, com.google.gson.JsonDeserializationContext context,
            CallbackInfoReturnable<net.minecraft.class_793> cir) {
        ((CbModelBlockModel.CbLegacyBlockModel) cir.getReturnValue()).cb$setOverrides(
            ((CbPrioritySplitClient.ModelOverrides) (Object) this).cb$v1_21_4$getOverrides(context, element.getAsJsonObject()));
    }
}
