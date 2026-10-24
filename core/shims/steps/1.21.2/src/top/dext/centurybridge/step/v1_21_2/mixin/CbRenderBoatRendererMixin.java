package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.facade.BoatType;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBoats;

/**
 * A BoatRenderer built through 1.21.1's BoatRenderer(context, chest) draws each boat with its variant's model and
 * render type, as 1.21.1's did (CbRenderBoats.legacyRenderer); a raft variant has no water patch (1.21.1 drew it for
 * boat models only). A renderer 1.21.2 built keeps its own.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_881.class)
public abstract class CbRenderBoatRendererMixin implements CbRenderBoats.Legacy {

    @Unique
    private Map<BoatType, CbRenderBoats.Variant> cb$variants;

    @Override
    public void cb$variants(Map<BoatType, CbRenderBoats.Variant> variants) {
        this.cb$variants = variants;
    }

    @Unique
    private CbRenderBoats.Variant cb$variant() {
        if (this.cb$variants == null) {
            return null;
        }
        BoatType type = CbRenderBoats.drawingVariant();
        return type == null ? null : this.cb$variants.get(type);
    }

    @Inject(method = "method_64517", at = @At("HEAD"), cancellable = true)
    private void cb$model(CallbackInfoReturnable<net.minecraft.class_583<net.minecraft.class_10004>> cir) {
        CbRenderBoats.Variant variant = this.cb$variant();
        if (variant != null) {
            cir.setReturnValue(variant.model());
        }
    }

    @Inject(method = "method_64520", at = @At("HEAD"), cancellable = true)
    private void cb$renderType(CallbackInfoReturnable<net.minecraft.class_1921> cir) {
        CbRenderBoats.Variant variant = this.cb$variant();
        if (variant != null) {
            cir.setReturnValue(variant.renderType());
        }
    }

    @Inject(method = "method_64521", at = @At("HEAD"), cancellable = true)
    private void cb$waterPatch(net.minecraft.class_10004 state, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light, CallbackInfo ci) {
        CbRenderBoats.Variant variant = this.cb$variant();
        if (variant != null && variant.raft()) {
            ci.cancel();
        }
    }
}
