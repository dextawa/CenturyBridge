package top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

/**
 * 1.21.11's net.fabricmc.fabric.api.renderer.v1.render.BlockVertexConsumerProvider, which 26.1's Fabric API
 * removed (blocks render through an AltModelBlockRenderer into a QuadEmitter): the consumer for a chunk layer.
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface BlockVertexConsumerProvider {
    VertexConsumer getBuffer(ChunkSectionLayer layer);
}
