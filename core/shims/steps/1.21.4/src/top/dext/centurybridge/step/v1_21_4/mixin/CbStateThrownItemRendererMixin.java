package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/** 1.21.3 ThrownItemRenderer.extractRenderState: the item's model (none when empty) and the item (a copy) (see {@link CbRenderStates}) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_953.class)
public abstract class CbStateThrownItemRendererMixin {

    @Inject(method = "method_62548", at = @At("TAIL"))
    private void cb$legacyItem(net.minecraft.class_1297 entity, net.minecraft.class_10072 state, float partialTick, CallbackInfo ci) {
        ((CbRenderStates.CbLegacyExtract) state).cb$extract(entity);
    }
}
