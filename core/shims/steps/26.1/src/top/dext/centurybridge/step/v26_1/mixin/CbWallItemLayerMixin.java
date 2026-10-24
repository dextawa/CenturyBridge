package top.dext.centurybridge.step.v26_1.mixin;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallItemSubmits;

/**
 * 1.21.11's ItemStackRenderState.LayerRenderState setters 26.1 replaced:
 * <ul>
 * <li>setRenderType(renderType) (class_10444$class_10446.method_67992): 1.21.11 drew the layer's quads under it; 26.1
 *     draws each quad under its own. A render type set here is given to the quads the layer submits
 *     (rt/CbWallItemSubmits.retype); none set (26.1's own layers) leaves them as they are.</li>
 * <li>setParticleIcon(sprite) (method_67994): 26.1's particle material of that sprite (not forced translucent).</li>
 * <li>prepareTintLayers(count) (method_65613): 1.21.11 handed out a fresh array of count tints for the caller to fill
 *     and drew with it; 26.1 keeps an int list. The array handed out becomes the layer's tints when it submits.</li>
 * </ul>
 * clear() forgets them, as 1.21.11's clear reset the fields.
 */
@Environment(EnvType.CLIENT)
@Mixin(ItemStackRenderState.LayerRenderState.class)
public abstract class CbWallItemLayerMixin {

    @Unique
    private RenderType cb$renderType;
    @Unique
    private int[] cb$tints;

    @Shadow
    public abstract void setParticleMaterial(Material.Baked material);

    @Shadow
    public abstract IntList tintLayers();

    /** 1.21.11 LayerRenderState.setRenderType (class_10444$class_10446.method_67992) */
    public void setRenderType(RenderType renderType) {
        cb$renderType = renderType;
    }

    /** 1.21.11 LayerRenderState.setParticleIcon (method_67994) */
    public void setParticleIcon(TextureAtlasSprite sprite) {
        setParticleMaterial(sprite == null ? null : new Material.Baked(sprite, false));
    }

    /** 1.21.11 LayerRenderState.prepareTintLayers (method_65613) */
    public int[] prepareTintLayers(int count) {
        int[] tints = new int[count];
        cb$tints = tints;
        return tints;
    }

    @Inject(method = "clear", at = @At("HEAD"))
    private void cb$clearLegacy(CallbackInfo ci) {
        cb$renderType = null;
        cb$tints = null;
    }

    @Inject(method = "submit", at = @At("HEAD"))
    private void cb$legacyTints(CallbackInfo ci) {
        if (cb$tints != null) {
            IntList list = tintLayers();
            list.clear();
            for (int t : cb$tints) {
                list.add(t);
            }
        }
    }

    @ModifyArg(method = "submit", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitItem(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemDisplayContext;III[ILjava/util/List;Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;)V"),
        index = 6)
    private List<BakedQuad> cb$legacyRenderType(List<BakedQuad> quads) {
        return CbWallItemSubmits.retype(quads, cb$renderType);
    }
}
