package top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * 1.21.11's net.fabricmc.fabric.api.renderer.v1.render.ItemRenderTypeGetter, which 26.1's Fabric API removed (each
 * quad carries its item render type): the render type of an item layer's mesh quad from its atlas and chunk layer,
 * null for the layer's default (rt/CbFapiLayerRenderTypes applies it).
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ItemRenderTypeGetter {
    RenderType renderType(QuadAtlas atlas, ChunkSectionLayer layer);
}
