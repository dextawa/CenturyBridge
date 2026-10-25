package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * 26.1.2's immediate draw of one built mesh, RenderType.draw(MeshData), and the per-format immediate buffers it
 * uploads into (VertexFormat.uploadImmediateVertexBuffer / uploadImmediateIndexBuffer). 26.2 split the draw into
 * RenderType.prepare() (the pipeline, output target, dynamic transforms with the layering and texture transforms,
 * the scissor state and the textures, exactly the state 26.1.2's draw gathered) and PreparedRenderType.drawFromBuffer
 * (vertex buffer, index buffer, index type, base vertex, first index, index count: the render pass 26.1.2's draw
 * opened). Vanilla 26.2 draws staged buffers; a mod's own mesh is drawn here the way 26.1.2 did: uploaded into the
 * format's immediate buffer (grown when too small, rewritten through the command encoder; 26.1.2 usage
 * VERTEX|COPY_DST and INDEX|COPY_DST), indexed through the topology's shared sequential buffer when the mesh has no
 * index buffer of its own, drawn from index 0, and the mesh closed.
 */
@Environment(EnvType.CLIENT)
public final class CbS26ImmediateDraw {
    private CbS26ImmediateDraw() {}

    /** per format: [vertex buffer, index buffer], as 26.1.2's VertexFormat held them (render thread only) */
    private static final Map<VertexFormat, GpuBuffer[]> IMMEDIATE = new IdentityHashMap<>();

    public static void draw(RenderType self, MeshData mesh) {
        PreparedRenderType prepared = self.prepare();
        try (mesh) {
            MeshData.DrawState ds = mesh.drawState();
            // 26.1.2 keyed the immediate buffers on the pipeline's vertex format; 26.2 pipelines list one format per
            // vertex buffer binding (getVertexFormatBinding), and the mesh carries the format it was built in
            VertexFormat format = ds.format();
            GpuBuffer vertices = uploadImmediateVertexBuffer(format, mesh.vertexBuffer());
            GpuBuffer indices;
            IndexType type;
            ByteBuffer own = mesh.indexBuffer();
            if (own == null) {
                RenderSystem.AutoStorageIndexBuffer sequential = RenderSystem.getSequentialBuffer(ds.primitiveTopology());
                indices = sequential.getBuffer(ds.indexCount());
                type = sequential.type();
            } else {
                indices = uploadImmediateIndexBuffer(format, own);
                type = ds.indexType();
            }
            prepared.drawFromBuffer(vertices, indices, type, 0, 0, ds.indexCount());
        }
    }

    public static GpuBuffer uploadImmediateVertexBuffer(VertexFormat self, ByteBuffer data) {
        GpuBuffer[] b = IMMEDIATE.computeIfAbsent(self, f -> new GpuBuffer[2]);
        b[0] = upload(b[0], data, GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST, () -> "Immediate vertex buffer for " + self);
        return b[0];
    }

    public static GpuBuffer uploadImmediateIndexBuffer(VertexFormat self, ByteBuffer data) {
        GpuBuffer[] b = IMMEDIATE.computeIfAbsent(self, f -> new GpuBuffer[2]);
        b[1] = upload(b[1], data, GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST, () -> "Immediate index buffer for " + self);
        return b[1];
    }

    /** 26.1.2 VertexFormat.uploadToBuffer: a new buffer when there is none or it is too small, else rewritten in place */
    private static GpuBuffer upload(GpuBuffer buffer, ByteBuffer data, int usage, Supplier<String> label) {
        GpuDevice device = RenderSystem.getDevice();
        if (buffer == null) {
            return device.createBuffer(label, usage, data);
        }
        if (buffer.size() < data.remaining()) {
            buffer.close();
            return device.createBuffer(label, usage, data);
        }
        device.createCommandEncoder().writeToBuffer(buffer.slice(), data);
        return buffer;
    }
}
