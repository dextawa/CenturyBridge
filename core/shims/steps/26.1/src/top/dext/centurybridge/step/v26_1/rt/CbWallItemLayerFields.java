package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;

/**
 * 1.21.11 ItemStackRenderState.LayerRenderState.particleIcon (class_10444$class_10446.field_56966, opened by access
 * wideners): 1.21.11's setParticleIcon stored the sprite, clear() reset it to null and pickParticleIcon read it from a
 * random layer. 26.1 (javap) keeps the same lifecycle in particleMaterial (Material.Baked: sprite + forceTranslucent;
 * setParticleMaterial, clear() nulls it, pickParticleMaterial reads it). Read only: a write would have to invent a
 * forceTranslucent value 1.21.11 never had, so the aw-put row stays open.
 */
@Environment(EnvType.CLIENT)
public final class CbWallItemLayerFields {
    private CbWallItemLayerFields() {
    }

    public static TextureAtlasSprite particleIcon(ItemStackRenderState.LayerRenderState l) {
        Material.Baked m = l.particleMaterial;
        return m == null ? null : m.sprite();
    }
}
