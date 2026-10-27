package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26SubmitStorage;

/**
 * 26.1.2 drew its one submit storage wherever vanilla called renderAllFeatures() (hand, picture-in-picture renderers,
 * GuiItemAtlas.drawToSlot; javap). 26.2 calls renderAllFeatures(storage) at those places with each context's own
 * storage (GameRenderer.renderItemInHand / renderLevel with handAndScreenSubmitNodeStorage, PictureInPictureRenderer.prepare,
 * GuiItemAtlas.drawToSlot). At its RETURN (the prepared frame is closed) what 26.1.2 code put in this layer's storage is
 * drawn too (rt/CbS26SubmitStorage.afterVanillaDraw). Injected at RETURN only; with nothing in this layer's storage
 * nothing happens.
 */
@Environment(EnvType.CLIENT)
@Mixin(FeatureRenderDispatcher.class)
public abstract class CbS26SubmitDrainMixin {
    @Inject(method = "renderAllFeatures(Lnet/minecraft/client/renderer/SubmitNodeStorage;)V", at = @At("RETURN"))
    private void cbS26$drainLegacyStorage(SubmitNodeStorage storage, CallbackInfo ci) {
        CbS26SubmitStorage.afterVanillaDraw((FeatureRenderDispatcher) (Object) this, storage);
    }
}
