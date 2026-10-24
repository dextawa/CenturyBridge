package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;

/**
 * 1.21.11's chunk section mesh uploads on 26.1.
 * <p>
 * 1.21.11 gave every compiled section its own vertex / index GPU buffers: RenderSection.upload(layers, mesh) and
 * uploadSectionIndexBuffer(mesh, indices, layer) scheduled CompiledSectionMesh.uploadMeshLayer /
 * uploadLayerIndexBuffer(layer, data, sectionNode) on the render thread, which wrote them. 26.1 packs every section
 * of a layer into one "uber" buffer; its rebuild and resort tasks hand a section's vertex and index bytes to
 * RenderSection.addSectionBuffersToUberBuffer(layer, mesh, vertices, indices) (null for "unchanged") under the
 * dispatcher's copy lock, spinning once off the render thread when the buffer is full. These do the same with the
 * bytes a 1.21.11 caller hands in; the section of a node is the level renderer's (its view area).
 */
@Environment(EnvType.CLIENT)
public final class CbWallSectionUploads {

    private CbWallSectionUploads() {
    }

    /** implemented on RenderSection by mixin/CbWallRenderSectionUploadMixin */
    public interface Section {
        boolean cb$addToUberBuffer(ChunkSectionLayer layer, CompiledSectionMesh mesh, ByteBuffer vertices, ByteBuffer indices);
    }

    private static void add(SectionRenderDispatcher.RenderSection section, ChunkSectionLayer layer, CompiledSectionMesh mesh,
                            ByteBuffer vertices, ByteBuffer indices) {
        if (!((Section) section).cb$addToUberBuffer(layer, mesh, vertices, indices) && !RenderSystem.isOnRenderThread()) {
            Thread.onSpinWait();
        }
    }

    /** RenderSection.upload(layers, mesh): every layer's bytes into its uber buffer, the mesh data released */
    public static CompletableFuture<Void> upload(SectionRenderDispatcher.RenderSection section, Map<ChunkSectionLayer, MeshData> layers,
                                                 CompiledSectionMesh mesh) {
        for (Map.Entry<ChunkSectionLayer, MeshData> e : layers.entrySet()) {
            MeshData data = e.getValue();
            try {
                add(section, e.getKey(), mesh, data.vertexBuffer(), data.indexBuffer());
            } finally {
                data.close();
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    /** RenderSection.uploadSectionIndexBuffer(mesh, indices, layer): the re-sorted indices, released */
    public static CompletableFuture<Void> uploadIndices(SectionRenderDispatcher.RenderSection section, CompiledSectionMesh mesh,
                                                        ByteBufferBuilder.Result indices, ChunkSectionLayer layer) {
        try {
            add(section, layer, mesh, null, indices.byteBuffer());
        } finally {
            indices.close();
        }
        return CompletableFuture.completedFuture(null);
    }

    private static SectionRenderDispatcher.RenderSection section(long sectionNode) {
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
        return renderer == null || renderer.viewArea == null ? null : renderer.viewArea.getRenderSection(sectionNode);
    }

    /** CompiledSectionMesh.uploadMeshLayer(layer, data, sectionNode): the caller keeps (and closes) the data */
    public static void uploadMeshLayer(CompiledSectionMesh mesh, ChunkSectionLayer layer, MeshData data, long sectionNode) {
        SectionRenderDispatcher.RenderSection section = section(sectionNode);
        if (section == null) {
            throw new IllegalStateException("no render section at node " + sectionNode + " to upload a " + layer + " mesh to");
        }
        add(section, layer, mesh, data.vertexBuffer(), data.indexBuffer());
    }

    /** CompiledSectionMesh.uploadLayerIndexBuffer(layer, indices, sectionNode): the caller keeps the indices */
    public static void uploadLayerIndexBuffer(CompiledSectionMesh mesh, ChunkSectionLayer layer, ByteBufferBuilder.Result indices,
                                              long sectionNode) {
        SectionRenderDispatcher.RenderSection section = section(sectionNode);
        if (section == null) {
            throw new IllegalStateException("no render section at node " + sectionNode + " to upload " + layer + " indices to");
        }
        add(section, layer, mesh, null, indices.byteBuffer());
    }
}
