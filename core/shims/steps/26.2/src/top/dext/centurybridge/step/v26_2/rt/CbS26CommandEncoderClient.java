package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuQuery;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ARGB;
import org.joml.Vector4fc;
import top.dext.centurybridge.step.v26_2.facade.GpuBufferViews;

/**
 * 26.1.2 command-encoder clear colours were one ARGB int (GlCommandEncoder: glClearColor(ARGB.redFloat(c),
 * greenFloat, blueFloat, alphaFloat)); 26.2 takes the same four floats as a Vector4fc (r, g, b, a), and a render
 * pass's optional clear colour is an Optional of it where 26.1.2's was an OptionalInt. ARGB.vector4fFromARGB32 is
 * 26.2's own conversion of that int into those floats (redFloat, greenFloat, blueFloat, alphaFloat).
 */
@Environment(EnvType.CLIENT)
public final class CbS26CommandEncoderClient {
    private CbS26CommandEncoderClient() {}

    public static Vector4fc color(int argb) {
        return ARGB.vector4fFromARGB32(argb);
    }

    public static Optional<Vector4fc> clearColor(OptionalInt argb) {
        return argb.isPresent() ? Optional.of(ARGB.vector4fFromARGB32(argb.getAsInt())) : Optional.empty();
    }

    private static void requireNoRenderPass(CommandEncoder self) {
        if (self.isInRenderPass()) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
    }

    /** 26.1.2 mapBuffer(buffer, read, write): no render pass open, then 26.2's GpuBuffer.map (the same checks) */
    public static GpuBufferViews.MappedView mapBuffer(CommandEncoder self, GpuBuffer buffer, boolean read, boolean write) {
        requireNoRenderPass(self);
        return GpuBufferViews.of(buffer.map(read, write));
    }

    /** 26.1.2 mapBuffer(slice, read, write): no render pass open, then 26.2's GpuBufferSlice.map */
    public static GpuBufferViews.MappedView mapBuffer(CommandEncoder self, GpuBufferSlice slice, boolean read, boolean write) {
        requireNoRenderPass(self);
        return GpuBufferViews.of(slice.map(read, write));
    }

    /**
     * 26.1.2 writeToTexture(texture, image, mipLevel, depthOrLayer, destX, destY, width, height, sourceX, sourceY):
     * the image's width x height region at (sourceX, sourceY) written at (destX, destY). 26.2 writes a whole image;
     * the whole image is written as it is, a region through a copy of it (NativeImage.copyRect), after 26.1.2's own
     * check that the region lies in the image.
     */
    public static void writeToTexture(CommandEncoder self, GpuTexture texture, NativeImage image, int mipLevel, int depthOrLayer,
                                      int destX, int destY, int width, int height, int sourceX, int sourceY) {
        if (sourceX == 0 && sourceY == 0 && width == image.getWidth() && height == image.getHeight()) {
            self.writeToTexture(texture, image, mipLevel, depthOrLayer, destX, destY);
            return;
        }
        if (sourceX + width > image.getWidth() || sourceY + height > image.getHeight()) {
            throw new IllegalArgumentException("Copy source (" + image.getWidth() + "x" + image.getHeight() + ") is not large enough to read a "
                + width + "x" + height + " area from " + sourceX + "x" + sourceY);
        }
        try (NativeImage region = new NativeImage(image.format(), width, height, false)) {
            image.copyRect(region, sourceX, sourceY, 0, 0, width, height, false, false);
            self.writeToTexture(texture, region, mipLevel, depthOrLayer, destX, destY);
        }
    }

    /**
     * 26.1.2 writeToTexture(texture, buffer, format, mipLevel, depthOrLayer, destX, destY, width, height): 26.2 takes
     * the pixels in the texture's own format (its block size per pixel). A buffer in a format of the same pixel size
     * is written as it is; another format is refused, as 26.2 has no upload conversion.
     */
    public static void writeToTexture(CommandEncoder self, GpuTexture texture, ByteBuffer buffer, NativeImage.Format format, int mipLevel,
                                      int depthOrLayer, int destX, int destY, int width, int height) {
        if (format.components() != texture.getFormat().blockSize()) {
            throw new IllegalArgumentException("26.2 writes a texture only from pixels in its own format: " + format + " ("
                + format.components() + " bytes per pixel) into " + texture.getFormat());
        }
        self.writeToTexture(texture, buffer, mipLevel, depthOrLayer, destX, destY, width, height);
    }

    /** the timer query begun and not yet ended (26.1.2's GlCommandEncoder held one; render thread only) */
    private static TimerQuery active;

    /**
     * 26.1.2 timerQueryBegin(): a GL_TIME_ELAPSED query, one at a time. 26.2 measures time with timestamp query pools:
     * the query is a pool of two timestamps, the first written now, the second at timerQueryEnd; its value, once both
     * are available, is the time between them in nanoseconds (what GL_TIME_ELAPSED answered).
     */
    public static GpuQuery timerQueryBegin(CommandEncoder self) {
        RenderSystem.assertOnRenderThread();
        if (active != null) {
            throw new IllegalStateException("A GL_TIME_ELAPSED query is already active");
        }
        GpuQueryPool pool = RenderSystem.getDevice().createTimestampQueryPool(2);
        self.writeTimestamp(pool, 0);
        active = new TimerQuery(pool);
        return active;
    }

    /** 26.1.2 timerQueryEnd(query) */
    public static void timerQueryEnd(CommandEncoder self, GpuQuery query) {
        RenderSystem.assertOnRenderThread();
        if (active == null || active != query) {
            throw new IllegalStateException("Mismatched or duplicate GpuQuery when ending timerQuery");
        }
        self.writeTimestamp(active.pool, 1);
        active = null;
    }

    private static final class TimerQuery implements GpuQuery {
        final GpuQueryPool pool;
        private boolean closed;
        private OptionalLong result = OptionalLong.empty();

        TimerQuery(GpuQueryPool pool) {
            this.pool = pool;
        }

        @Override
        public OptionalLong getValue() {
            RenderSystem.assertOnRenderThread();
            if (this.closed) {
                throw new IllegalStateException("GlTimerQuery is closed");
            }
            if (this.result.isPresent()) {
                return this.result;
            }
            OptionalLong[] stamps = this.pool.getValues(0, 2);
            if (stamps[0].isPresent() && stamps[1].isPresent()) {
                this.result = OptionalLong.of(stamps[1].getAsLong() - stamps[0].getAsLong());
            }
            return this.result;
        }

        @Override
        public void close() {
            RenderSystem.assertOnRenderThread();
            if (!this.closed) {
                this.closed = true;
                this.pool.close();
            }
        }
    }
}
