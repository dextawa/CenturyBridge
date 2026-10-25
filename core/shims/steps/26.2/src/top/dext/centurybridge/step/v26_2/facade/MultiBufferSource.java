package top.dext.centurybridge.step.v26_2.facade;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.HashMap;
import java.util.Map;
import java.util.SequencedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderType;
import top.dext.centurybridge.step.v26_2.rt.CbS26ImmediateDraw;

/**
 * 26.1.2's net.minecraft.client.renderer.MultiBufferSource and its BufferSource, which 26.2 deleted (26.2's renderers
 * write staged vertex buffers). Restored as they were (javap of 26.1.2's classes): a source hands out one
 * BufferBuilder per RenderType, over the type's fixed ByteBufferBuilder when it has one, else over the shared one
 * (ending the previous shared batch first); a type that cannot consolidate consecutive geometry ends its open batch
 * when asked again. Ending a batch builds the mesh, sorts its quads for the current projection when the type sorts on
 * upload, and draws it as 26.1.2's RenderType.draw did (rt/CbS26ImmediateDraw). classRenames send a 26.1.2 mod's
 * references here (the types are the mod's own; 26.2's vanilla does not read them).
 */
@Environment(EnvType.CLIENT)
public interface MultiBufferSource {
    static BufferSource immediate(ByteBufferBuilder sharedBuffer) {
        return immediateWithBuffers(Object2ObjectSortedMaps.emptyMap(), sharedBuffer);
    }

    static BufferSource immediateWithBuffers(SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers, ByteBufferBuilder sharedBuffer) {
        return new BufferSource(sharedBuffer, fixedBuffers);
    }

    VertexConsumer getBuffer(RenderType renderType);

    @Environment(EnvType.CLIENT)
    class BufferSource implements MultiBufferSource {
        protected final ByteBufferBuilder sharedBuffer;
        protected final SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers;
        protected final Map<RenderType, BufferBuilder> startedBuilders = new HashMap<>();
        protected RenderType lastSharedType;

        protected BufferSource(ByteBufferBuilder sharedBuffer, SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers) {
            this.sharedBuffer = sharedBuffer;
            this.fixedBuffers = fixedBuffers;
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            BufferBuilder started = this.startedBuilders.get(renderType);
            if (started != null && !renderType.canConsolidateConsecutiveGeometry()) {
                this.endBatch(renderType, started);
                started = null;
            }
            if (started != null) {
                return started;
            }
            BufferBuilder builder;
            ByteBufferBuilder fixed = this.fixedBuffers.get(renderType);
            if (fixed != null) {
                builder = new BufferBuilder(fixed, renderType.primitiveTopology(), renderType.format());
            } else {
                if (this.lastSharedType != null) {
                    this.endBatch(this.lastSharedType);
                }
                builder = new BufferBuilder(this.sharedBuffer, renderType.primitiveTopology(), renderType.format());
                this.lastSharedType = renderType;
            }
            this.startedBuilders.put(renderType, builder);
            return builder;
        }

        public void endLastBatch() {
            if (this.lastSharedType != null) {
                this.endBatch(this.lastSharedType);
                this.lastSharedType = null;
            }
        }

        public void endBatch() {
            this.endLastBatch();
            for (RenderType type : this.fixedBuffers.keySet()) {
                this.endBatch(type);
            }
        }

        public void endBatch(RenderType renderType) {
            BufferBuilder builder = this.startedBuilders.remove(renderType);
            if (builder != null) {
                this.endBatch(renderType, builder);
            }
        }

        private void endBatch(RenderType renderType, BufferBuilder builder) {
            MeshData mesh = builder.build();
            if (mesh != null) {
                if (renderType.sortOnUpload()) {
                    ByteBufferBuilder buffer = this.fixedBuffers.getOrDefault(renderType, this.sharedBuffer);
                    mesh.sortQuads(buffer, RenderSystem.getProjectionType().vertexSorting());
                }
                CbS26ImmediateDraw.draw(renderType, mesh);
            }
            if (renderType.equals(this.lastSharedType)) {
                this.lastSharedType = null;
            }
        }
    }
}
