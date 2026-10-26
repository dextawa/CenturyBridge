package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallSectionUploads;

/** 1.21.11's CompiledSectionMesh.uploadMeshLayer / uploadLayerIndexBuffer (rt/CbWallSectionUploads). */
@Environment(EnvType.CLIENT)
@Mixin(value = CompiledSectionMesh.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallCompiledSectionMeshUploadMixin {

    public void uploadMeshLayer(ChunkSectionLayer layer, MeshData data, long sectionNode) {
        CbWallSectionUploads.uploadMeshLayer((CompiledSectionMesh) (Object) this, layer, data, sectionNode);
    }

    public void uploadLayerIndexBuffer(ChunkSectionLayer layer, ByteBufferBuilder.Result indices, long sectionNode) {
        CbWallSectionUploads.uploadLayerIndexBuffer((CompiledSectionMesh) (Object) this, layer, indices, sectionNode);
    }
}
