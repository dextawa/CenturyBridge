package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/** 1.21.3 DisplayRenderer.ItemDisplayRenderer.extractRenderState: the display's item render state and its item's model (see {@link CbRenderStates}) */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.class_8138$class_8140")
public abstract class CbStateItemDisplayRendererMixin {

    @Inject(method = "method_62408", at = @At("TAIL"))
    private void cb$legacyItem(net.minecraft.class_8113.class_8122 entity, net.minecraft.class_10038 state, float partialTick, CallbackInfo ci) {
        ((CbRenderStates.CbLegacyExtract) state).cb$extract(entity);
    }
}
