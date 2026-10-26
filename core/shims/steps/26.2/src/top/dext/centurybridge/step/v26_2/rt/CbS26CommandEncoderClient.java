package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuQuery;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuSurface;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.GpuFormat;
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
     * 26.1.2 writeToTexture(texture, buffer, format, mipLevel, depthOrLayer, destX, destY, width, height) uploaded
     * unsigned bytes in the buffer's NativeImage format (javap: GL_UNPACK_ROW_LENGTH width, no skips, alignment the
     * format's component count, glTexSubImage2D with GlConst.toGl(format): RGBA -> GL_RGBA, RGB -> GL_RGB,
     * LUMINANCE_ALPHA -> GL_RG, LUMINANCE -> GL_RED, GL_UNSIGNED_BYTE) and let GL convert to the texture's format.
     * 26.2 takes the pixels already in the texture's own GpuFormat (row length width, block size per pixel). The
     * layer does GL's unpack conversion on the CPU (OpenGL 3.3 core 8.4.4 / 8.5): each pixel becomes (R, G, B, A)
     * with a missing colour component 0 and a missing alpha 1 (GL_RG: R = luminance, G = alpha), and the texture keeps
     * its first componentCount components. For an 8-bit normalised texture that is the byte itself (UNORM_8) or
     * round(f * 127) of f = byte / 255 (SNORM_8). Any other texture format is refused: GL refused unsigned-byte colour
     * data into an integer or depth texture too (GL_INVALID_OPERATION), and 26.1.2 created no wider formats.
     */
    public static void writeToTexture(CommandEncoder self, GpuTexture texture, ByteBuffer buffer, NativeImage.Format format, int mipLevel,
                                      int depthOrLayer, int destX, int destY, int width, int height) {
        GpuFormat target = texture.getFormat();
        GpuFormat.ComponentType type = target.componentType();
        if (!target.hasColorAspect() || type != GpuFormat.ComponentType.UNORM_8 && type != GpuFormat.ComponentType.SNORM_8) {
            throw new IllegalArgumentException("26.1.2 uploaded unsigned-byte " + format + " pixels, which GL converts only into a"
                + " normalised 8-bit colour texture, not " + target);
        }
        int in = format.components();
        int out = target.componentCount();
        int pixels = width * height;
        if (buffer.remaining() < pixels * in) {
            throw new IllegalArgumentException("buffer holds " + buffer.remaining() + " bytes, " + width + "x" + height + " " + format
                + " needs " + pixels * in);
        }
        ByteBuffer converted = org.lwjgl.system.MemoryUtil.memAlloc(pixels * out);
        try {
            int base = buffer.position();
            byte[] rgba = new byte[4];
            for (int p = 0; p < pixels; p++) {
                int at = base + p * in;
                unpack(format, buffer, at, rgba);
                for (int c = 0; c < out; c++) {
                    converted.put(p * out + c, type == GpuFormat.ComponentType.UNORM_8 ? rgba[c] : snorm(rgba[c]));
                }
            }
            self.writeToTexture(texture, converted, mipLevel, depthOrLayer, destX, destY, width, height);
        } finally {
            org.lwjgl.system.MemoryUtil.memFree(converted);
        }
    }

    /** GL's unpack of one unsigned-byte pixel of a 26.1.2 upload format to (R, G, B, A): missing colour 0, missing alpha 1 */
    private static void unpack(NativeImage.Format format, ByteBuffer buffer, int at, byte[] rgba) {
        rgba[0] = buffer.get(at);
        rgba[1] = 0;
        rgba[2] = 0;
        rgba[3] = (byte) 0xFF;
        switch (format) {
            case RGBA -> {
                rgba[1] = buffer.get(at + 1);
                rgba[2] = buffer.get(at + 2);
                rgba[3] = buffer.get(at + 3);
            }
            case RGB -> {
                rgba[1] = buffer.get(at + 1);
                rgba[2] = buffer.get(at + 2);
            }
            case LUMINANCE_ALPHA -> rgba[1] = buffer.get(at + 1); // GL_RG: (luminance, alpha, 0, 1)
            case LUMINANCE -> { } // GL_RED: (luminance, 0, 0, 1)
        }
    }

    /** an unsigned normalised byte as GL stores it in a signed normalised component: round(clamp(f, -1, 1) * 127) */
    private static byte snorm(byte unorm) {
        return (byte) Math.round((unorm & 0xFF) / 255.0f * 127.0f);
    }

    /**
     * 26.1.2 CommandEncoder.presentTexture(view): with no render pass open, a single-layer colour texture usable as a
     * render attachment was blitted at its size onto the window's framebuffer (GlCommandEncoder: draw FBO 0,
     * blitFrameBuffers, nearest), at the moment of the call; Minecraft's own blitToScreen later in the frame drew over
     * it. 26.2 draws to the window through the GpuSurface of the frame (Minecraft.windowSurface: acquired, blitted once,
     * presented). The layer blits through that surface's backend, the same backend call GpuSurface.blitFromTexture
     * makes (GlSurface -> GlCommandEncoder.presentTexture(view, w, h)), without marking the frame blitted: 26.2's own
     * blit of the main target still runs afterwards and draws over it, as on 26.1.2, and 26.2's checks stay in place
     * (also USAGE_COPY_SRC, which 26.2's presentation needs). Outside an acquired frame 26.2 has no window image to
     * draw into: refused with that reason.
     */
    public static void presentTexture(CommandEncoder self, GpuTextureView view) {
        if (self.isInRenderPass()) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        presentThrough(self.backend(), view);
    }

    /** 26.1.2 CommandEncoderBackend.presentTexture(view), as {@link #presentTexture} after its render pass check */
    public static void presentTexture(CommandEncoderBackend backend, GpuTextureView view) {
        presentThrough(backend, view);
    }

    private static void presentThrough(CommandEncoderBackend backend, GpuTextureView view) {
        GpuTexture texture = view.texture();
        if (!texture.getFormat().hasColorAspect()) {
            throw new IllegalStateException("Cannot present a non-color texture!");
        }
        if ((texture.usage() & GpuTexture.USAGE_RENDER_ATTACHMENT) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_RENDER_ATTACHMENT to presented to the screen");
        }
        if ((texture.usage() & GpuTexture.USAGE_COPY_SRC) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_COPY_SRC to presented to the screen");
        }
        if (texture.getDepthOrLayers() > 1) {
            throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for presentation");
        }
        GpuSurface surface = net.minecraft.client.Minecraft.getInstance().windowSurface();
        if (!surface.isAcquired()) {
            throw new IllegalStateException("26.2 draws to the window only within a frame whose surface image is acquired;"
                + " 26.1.2's presentTexture outside one has no window image to draw into");
        }
        surface.backend.blitFromTexture(backend, view);
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
