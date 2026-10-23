package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render.ItemRenderTypeGetter;
import top.dext.centurybridge.step.v26_1.rt.CbFapiLayerRenderTypes;

/** 1.21.11's per-layer item render type getter, kept on the layer and applied at its submit (rt/CbFapiLayerRenderTypes) */
@Environment(EnvType.CLIENT)
@Mixin(ItemStackRenderState.LayerRenderState.class)
public abstract class CbFapiLayerRenderStateMixin implements CbFapiLayerRenderTypes.Holder {
    @Unique
    private ItemRenderTypeGetter cb$renderTypeGetter;

    @Override
    public void cb$setRenderTypeGetter(ItemRenderTypeGetter getter) {
        this.cb$renderTypeGetter = getter;
    }

    @Inject(method = "clear", at = @At("RETURN"))
    private void cb$clearRenderTypeGetter(CallbackInfo ci) {
        this.cb$renderTypeGetter = null;
    }

    @Inject(method = "submit", at = @At("HEAD"))
    private void cb$applyRenderTypeGetter(CallbackInfo ci) {
        if (this.cb$renderTypeGetter != null) {
            CbFapiLayerRenderTypes.apply((ItemStackRenderState.LayerRenderState) (Object) this, this.cb$renderTypeGetter);
        }
    }
}
