package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBabyLayers;

/**
 * EntityModelSet.bakeLayer for a baby layer CbRenderBabyLayers derived from an adult layer: the adult's definition with
 * the transformer, baked (1.21.2's LayerDefinitions makes its own baby layers the same way); an absent adult fails as
 * bakeLayer fails for any absent layer.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5599.class)
public abstract class CbRenderBabyLayersMixin {

    @Shadow
    private Map<net.minecraft.class_5601, net.minecraft.class_5607> field_27542;

    @Inject(method = "method_32072", at = @At("HEAD"), cancellable = true)
    private void cb$bakeBaby(net.minecraft.class_5601 location, CallbackInfoReturnable<net.minecraft.class_630> cir) {
        CbRenderBabyLayers.Derived derived = CbRenderBabyLayers.derived(location);
        if (derived == null) {
            return;
        }
        net.minecraft.class_5607 adult = this.field_27542.get(derived.adult());
        if (adult == null) {
            throw new IllegalArgumentException("No model for layer " + derived.adult());
        }
        cir.setReturnValue(adult.method_62137(derived.transformer()).method_32109());
    }
}
