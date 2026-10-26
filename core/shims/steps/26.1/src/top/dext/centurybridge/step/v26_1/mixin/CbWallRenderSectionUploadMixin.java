package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallSectionUploads;

/**
 * 1.21.11's RenderSection.upload / uploadSectionIndexBuffer (rt/CbWallSectionUploads), and the uber buffer entry
 * 26.1's own tasks use, for them.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = SectionRenderDispatcher.RenderSection.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallRenderSectionUploadMixin implements CbWallSectionUploads.Section {

    @Shadow
    private boolean addSectionBuffersToUberBuffer(ChunkSectionLayer layer, CompiledSectionMesh mesh, ByteBuffer vertices,
                                                  ByteBuffer indices) {
        throw new AssertionError();
    }

    @Override
    public boolean cb$addToUberBuffer(ChunkSectionLayer layer, CompiledSectionMesh mesh, ByteBuffer vertices, ByteBuffer indices) {
        return addSectionBuffersToUberBuffer(layer, mesh, vertices, indices);
    }

    public CompletableFuture<Void> upload(Map<ChunkSectionLayer, MeshData> layers, CompiledSectionMesh mesh) {
        return CbWallSectionUploads.upload((SectionRenderDispatcher.RenderSection) (Object) this, layers, mesh);
    }

    public CompletableFuture<Void> uploadSectionIndexBuffer(CompiledSectionMesh mesh, ByteBufferBuilder.Result indices,
                                                            ChunkSectionLayer layer) {
        return CbWallSectionUploads.uploadIndices((SectionRenderDispatcher.RenderSection) (Object) this, mesh, indices, layer);
    }
}
