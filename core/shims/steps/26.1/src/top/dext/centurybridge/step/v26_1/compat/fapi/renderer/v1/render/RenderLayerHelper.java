package top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.fabricmc.fabric.api.client.renderer.v1.render.ChunkSectionLayerHelper;

/**
 * 1.21.11's net.fabricmc.fabric.api.renderer.v1.render.RenderLayerHelper, which 26.1's Fabric API replaced with
 * ChunkSectionLayerHelper; member for member 1.21.11's (the moving-block types are ChunkSectionLayerHelper's,
 * the same switch over 26.1's layers).
 */
@Environment(EnvType.CLIENT)
public final class RenderLayerHelper {
    private RenderLayerHelper() {
    }

    public static RenderType getMovingBlockLayer(ChunkSectionLayer layer) {
        return ChunkSectionLayerHelper.getMovingBlockRenderType(layer);
    }

    public static RenderType getEntityBlockLayer(ChunkSectionLayer layer) {
        return layer == ChunkSectionLayer.TRANSLUCENT ? Sheets.translucentBlockItemSheet() : Sheets.cutoutBlockSheet();
    }

    public static BlockVertexConsumerProvider movingDelegate(MultiBufferSource vertexConsumers) {
        return layer -> vertexConsumers.getBuffer(getMovingBlockLayer(layer));
    }

    public static BlockVertexConsumerProvider entityDelegate(MultiBufferSource vertexConsumers) {
        return layer -> vertexConsumers.getBuffer(getEntityBlockLayer(layer));
    }
}
