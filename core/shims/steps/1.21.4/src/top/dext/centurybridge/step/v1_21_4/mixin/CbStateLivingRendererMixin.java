package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/** 1.21.3 LivingEntityRenderer.extractRenderState filled the state's item fields (see {@link CbRenderStates}) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_922.class)
public abstract class CbStateLivingRendererMixin {

    @Inject(method = "method_62355", at = @At("TAIL"))
    private void cb$legacyItems(net.minecraft.class_1309 entity, net.minecraft.class_10042 state, float partialTick, CallbackInfo ci) {
        CbRenderStates.extractLiving(entity, state);
    }
}
