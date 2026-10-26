package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

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
 * clear() forgets them, as 1.21.11's clear reset the fields. The reads in 26.1's clear / submit are
 * mixin/CbWallItemLayerReadsMixin.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = ItemStackRenderState.LayerRenderState.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallItemLayerMixin implements CbWallLegacyCalls.ItemLayerLegacy {

    @Unique
    private RenderType cb$renderType;
    @Unique
    private int[] cb$tints;

    @Shadow
    public abstract void setParticleMaterial(Material.Baked material);

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

    @Override
    public void cb$clearLegacy() {
        cb$renderType = null;
        cb$tints = null;
    }

    @Override
    public int[] cb$legacyTints() {
        return cb$tints;
    }

    @Override
    public RenderType cb$legacyRenderType() {
        return cb$renderType;
    }
}
