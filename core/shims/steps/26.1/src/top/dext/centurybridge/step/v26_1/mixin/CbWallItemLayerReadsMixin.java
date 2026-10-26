package top.dext.centurybridge.step.v26_1.mixin;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallItemSubmits;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * What 1.21.11's LayerRenderState setters (CbWallItemLayerMixin) set, read where 26.1 draws the layer: clear() forgets
 * it, submit() takes the tints array prepareTintLayers handed out as the layer's tints and gives the quads it submits
 * the render type setRenderType set. A layer no 1.21.11 setter touched submits as 26.1's. Default priority: injectors
 * into 26.1 methods.
 */
@Environment(EnvType.CLIENT)
@Mixin(ItemStackRenderState.LayerRenderState.class)
public abstract class CbWallItemLayerReadsMixin {

    @Shadow
    public abstract IntList tintLayers();

    @Inject(method = "clear", at = @At("HEAD"))
    private void cb$clearLegacy(CallbackInfo ci) {
        ((CbWallLegacyCalls.ItemLayerLegacy) (Object) this).cb$clearLegacy();
    }

    @Inject(method = "submit", at = @At("HEAD"))
    private void cb$legacyTints(CallbackInfo ci) {
        int[] tints = ((CbWallLegacyCalls.ItemLayerLegacy) (Object) this).cb$legacyTints();
        if (tints != null) {
            IntList list = tintLayers();
            list.clear();
            for (int t : tints) {
                list.add(t);
            }
        }
    }

    @ModifyArg(method = "submit", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitItem(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemDisplayContext;III[ILjava/util/List;Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;)V"),
        index = 6)
    private List<BakedQuad> cb$legacyRenderType(List<BakedQuad> quads) {
        return CbWallItemSubmits.retype(quads, ((CbWallLegacyCalls.ItemLayerLegacy) (Object) this).cb$legacyRenderType());
    }
}
