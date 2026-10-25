package top.dext.centurybridge.step.v26_2.facade;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 26.1.2's com.mojang.blaze3d.vertex.VertexMultiConsumer, which 26.2 deleted with its buffer sources: one vertex
 * consumer writing every call into several (two: Double; more: Multiple; one: that consumer itself; none: an
 * IllegalArgumentException), refusing the same delegate twice. Restored as it was (javap of 26.1.2's classes) over
 * 26.2's VertexConsumer, whose abstract members are unchanged. classRenames send a 26.1.2 mod's references here.
 */
@Environment(EnvType.CLIENT)
public class VertexMultiConsumer {
    public static VertexConsumer create() {
        throw new IllegalArgumentException();
    }

    public static VertexConsumer create(VertexConsumer consumer) {
        return consumer;
    }

    public static VertexConsumer create(VertexConsumer first, VertexConsumer second) {
        return new Double(first, second);
    }

    public static VertexConsumer create(VertexConsumer... delegates) {
        return new Multiple(delegates);
    }

    @Environment(EnvType.CLIENT)
    static class Double implements VertexConsumer {
        private final VertexConsumer first;
        private final VertexConsumer second;

        Double(VertexConsumer first, VertexConsumer second) {
            if (first == second) {
                throw new IllegalArgumentException("Duplicate delegates");
            }
            this.first = first;
            this.second = second;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            this.first.addVertex(x, y, z);
            this.second.addVertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            this.first.setColor(r, g, b, a);
            this.second.setColor(r, g, b, a);
            return this;
        }

        @Override
        public VertexConsumer setColor(int color) {
            this.first.setColor(color);
            this.second.setColor(color);
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            this.first.setUv(u, v);
            this.second.setUv(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            this.first.setUv1(u, v);
            this.second.setUv1(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            this.first.setUv2(u, v);
            this.second.setUv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            this.first.setNormal(x, y, z);
            this.second.setNormal(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setLineWidth(float width) {
            this.first.setLineWidth(width);
            this.second.setLineWidth(width);
            return this;
        }

        @Override
        public void addVertex(float x, float y, float z, int color, float u, float v, int overlayCoords, int lightCoords,
                              float nx, float ny, float nz) {
            this.first.addVertex(x, y, z, color, u, v, overlayCoords, lightCoords, nx, ny, nz);
            this.second.addVertex(x, y, z, color, u, v, overlayCoords, lightCoords, nx, ny, nz);
        }
    }

    @Environment(EnvType.CLIENT)
    record Multiple(VertexConsumer[] delegates) implements VertexConsumer {
        Multiple {
            for (int i = 0; i < delegates.length; i++) {
                for (int j = i + 1; j < delegates.length; j++) {
                    if (delegates[i] == delegates[j]) {
                        throw new IllegalArgumentException("Duplicate delegates");
                    }
                }
            }
        }

        private void forEach(Consumer<VertexConsumer> action) {
            for (VertexConsumer delegate : this.delegates) {
                action.accept(delegate);
            }
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            this.forEach(c -> c.addVertex(x, y, z));
            return this;
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            this.forEach(c -> c.setColor(r, g, b, a));
            return this;
        }

        @Override
        public VertexConsumer setColor(int color) {
            this.forEach(c -> c.setColor(color));
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            this.forEach(c -> c.setUv(u, v));
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            this.forEach(c -> c.setUv1(u, v));
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            this.forEach(c -> c.setUv2(u, v));
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            this.forEach(c -> c.setNormal(x, y, z));
            return this;
        }

        @Override
        public VertexConsumer setLineWidth(float width) {
            this.forEach(c -> c.setLineWidth(width));
            return this;
        }

        @Override
        public void addVertex(float x, float y, float z, int color, float u, float v, int overlayCoords, int lightCoords,
                              float nx, float ny, float nz) {
            this.forEach(c -> c.addVertex(x, y, z, color, u, v, overlayCoords, lightCoords, nx, ny, nz));
        }
    }
}
