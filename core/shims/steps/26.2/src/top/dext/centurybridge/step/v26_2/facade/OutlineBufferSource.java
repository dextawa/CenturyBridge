package top.dext.centurybridge.step.v26_2.facade;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * 26.1.2's OutlineBufferSource (26.2 deleted it; entity outlines are a feature phase there), as 26.1.2 wrote it
 * (javap): an immediate source over its own 1536-byte buffer; getBuffer hands an outline render type its buffer, any
 * other type the buffer of its outline() variant, each wrapped so every vertex takes the source's outline colour and
 * keeps only position, colour and uv; a type with no outline variant is refused. endOutlineBatch draws them.
 */
@Environment(EnvType.CLIENT)
public class OutlineBufferSource implements MultiBufferSource {
    private final MultiBufferSource.BufferSource outlineBufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
    private int outlineColor = -1;

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        if (renderType.isOutline()) {
            VertexConsumer delegate = this.outlineBufferSource.getBuffer(renderType);
            return new EntityOutlineGenerator(delegate, this.outlineColor);
        }
        if (renderType.outline().isPresent()) {
            VertexConsumer delegate = this.outlineBufferSource.getBuffer(renderType.outline().get());
            return new EntityOutlineGenerator(delegate, this.outlineColor);
        }
        throw new IllegalStateException("Can't render an outline for this rendertype!");
    }

    public void setColor(int color) {
        this.outlineColor = color;
    }

    public void endOutlineBatch() {
        this.outlineBufferSource.endBatch();
    }

    /** 26.1.2's OutlineBufferSource.EntityOutlineGenerator: position, the fixed colour and uv; the rest dropped */
    record EntityOutlineGenerator(VertexConsumer delegate, int color) implements VertexConsumer {
        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            this.delegate.addVertex(x, y, z).setColor(this.color);
            return this;
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            return this;
        }

        @Override
        public VertexConsumer setColor(int argb) {
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            this.delegate.setUv(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            return this;
        }

        @Override
        public VertexConsumer setLineWidth(float width) {
            return this;
        }
    }
}
