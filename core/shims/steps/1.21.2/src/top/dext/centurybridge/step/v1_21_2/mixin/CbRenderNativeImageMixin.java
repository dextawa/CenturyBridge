package top.dext.centurybridge.step.v1_21_2.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.1 NativeImage members 1.21.2 dropped, 1.21.1's bodies on the same fields and private helpers 1.21.2 keeps
 * (format, width, height, pixel pointer, bounds / allocation checks, writeToChannel, the RGBA pixel accessors):
 * asByteArray(), setPixelLuminance, getRedOrLuminance / getGreenOrLuminance / getBlueOrLuminance, blendPixel (with
 * 1.21.1's ABGR32 channel arithmetic: alpha c>>>24, blue (c>>16)&255, green (c>>8)&255, red c&255).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1011.class)
public abstract class CbRenderNativeImageMixin {

    @Shadow
    @Final
    private net.minecraft.class_1011.class_1012 field_4986;

    @Shadow
    @Final
    private int field_4991;

    @Shadow
    @Final
    private int field_4989;

    @Shadow
    private long field_4988;

    @Shadow
    private boolean method_36559(int x, int y) {
        throw new AssertionError();
    }

    @Shadow
    private void method_4320() {
        throw new AssertionError();
    }

    @Shadow
    private int method_4315(int x, int y) {
        throw new AssertionError();
    }

    @Shadow
    private void method_4305(int x, int y, int abgr) {
        throw new AssertionError();
    }

    @Shadow
    private boolean method_24032(WritableByteChannel channel) throws IOException {
        throw new AssertionError();
    }

    /** asByteArray() */
    public byte[] method_24036() throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); WritableByteChannel channel = Channels.newChannel(out)) {
            if (!this.method_24032(channel)) {
                throw new IOException("Could not write image to byte array: " + org.lwjgl.stb.STBImage.stbi_failure_reason());
            }
            return out.toByteArray();
        }
    }

    private IllegalArgumentException cb$outside(int x, int y) {
        return new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", x, y, this.field_4991, this.field_4989));
    }

    /** setPixelLuminance(x, y, luminance) */
    public void method_35621(int x, int y, byte luminance) {
        RenderSystem.assertOnRenderThread();
        if (!this.field_4986.method_35631()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "setPixelLuminance only works on image with luminance; have %s", this.field_4986));
        }
        if (this.method_36559(x, y)) {
            throw this.cb$outside(x, y);
        }
        this.method_4320();
        long offset = ((long) x + (long) y * (long) this.field_4991) * (long) this.field_4986.method_4335() + (long) (this.field_4986.method_35635() / 8);
        MemoryUtil.memPutByte(this.field_4988 + offset, luminance);
    }

    /** getRedOrLuminance(x, y) */
    public byte method_35623(int x, int y) {
        RenderSystem.assertOnRenderThread();
        if (!this.field_4986.method_35636()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "no red or luminance in %s", this.field_4986));
        }
        if (this.method_36559(x, y)) {
            throw this.cb$outside(x, y);
        }
        int offset = (x + y * this.field_4991) * this.field_4986.method_4335() + this.field_4986.method_35639() / 8;
        return MemoryUtil.memGetByte(this.field_4988 + (long) offset);
    }

    /** getGreenOrLuminance(x, y) */
    public byte method_35625(int x, int y) {
        RenderSystem.assertOnRenderThread();
        if (!this.field_4986.method_35637()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "no green or luminance in %s", this.field_4986));
        }
        if (this.method_36559(x, y)) {
            throw this.cb$outside(x, y);
        }
        int offset = (x + y * this.field_4991) * this.field_4986.method_4335() + this.field_4986.method_35640() / 8;
        return MemoryUtil.memGetByte(this.field_4988 + (long) offset);
    }

    /** getBlueOrLuminance(x, y) */
    public byte method_35626(int x, int y) {
        RenderSystem.assertOnRenderThread();
        if (!this.field_4986.method_35638()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "no blue or luminance in %s", this.field_4986));
        }
        if (this.method_36559(x, y)) {
            throw this.cb$outside(x, y);
        }
        int offset = (x + y * this.field_4991) * this.field_4986.method_4335() + this.field_4986.method_35641() / 8;
        return MemoryUtil.memGetByte(this.field_4988 + (long) offset);
    }

    /** blendPixel(x, y, abgr): source-over blending of an ABGR colour onto the pixel, RGBA images only */
    public void method_35624(int x, int y, int abgr) {
        if (this.field_4986 != net.minecraft.class_1011.class_1012.field_4997) {
            throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
        }
        int dst = this.method_4315(x, y);
        float sa = (float) (abgr >>> 24) / 255.0F;
        float sb = (float) (abgr >> 16 & 255) / 255.0F;
        float sg = (float) (abgr >> 8 & 255) / 255.0F;
        float sr = (float) (abgr & 255) / 255.0F;
        float da = (float) (dst >>> 24) / 255.0F;
        float db = (float) (dst >> 16 & 255) / 255.0F;
        float dg = (float) (dst >> 8 & 255) / 255.0F;
        float dr = (float) (dst & 255) / 255.0F;
        float s = sa;
        float d = 1.0F - sa;
        float a = sa * s + da * d;
        float b = sb * s + db * d;
        float g = sg * s + dg * d;
        float r = sr * s + dr * d;
        if (a > 1.0F) {
            a = 1.0F;
        }
        if (b > 1.0F) {
            b = 1.0F;
        }
        if (g > 1.0F) {
            g = 1.0F;
        }
        if (r > 1.0F) {
            r = 1.0F;
        }
        int ia = (int) (a * 255.0F);
        int ib = (int) (b * 255.0F);
        int ig = (int) (g * 255.0F);
        int ir = (int) (r * 255.0F);
        this.method_4305(x, y, ia << 24 | ib << 16 | ig << 8 | ir);
    }
}
