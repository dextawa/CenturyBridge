package top.dext.centurybridge.step.v26_1.rt;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.Checks;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;

/**
 * LWJGL 3.3.3's (1.21.11) STBImageResize -- stb_image_resize v0.97 -- for converted 1.21.11 code on LWJGL 3.4.1 (26.1), whose
 * STBImageResize is stb_image_resize2: a different resizer (other output for the same call, other edge-mode numbers, no
 * >4-channel / mid-alpha layouts, no uint32; measured in the slib report). v1 is given back with its own code: CenturyBridge's
 * library cb_lwjgl_stbir1 (core/natives/stbir1: the v0.97 header byte-identical to LWJGL 3.3.3's copy, a thin JNI file, a
 * reproducible build), loaded as LWJGL loads its own JNI libraries (Library.loadSystem from the jar's
 * {os}/{arch}/top/dext/centurybridge/stbir1/; loadSystem looks in that per-platform path only for names containing
 * "lwjgl", hence the name). Its v1 symbols are private (STB_IMAGE_RESIZE_STATIC): it cannot clash with
 * 3.4.1's lwjgl_stb.
 * <p>
 * Each method is 3.3.3's: the buffer forms check the buffers as 3.3.3 did and pass their addresses; the array forms copy
 * the arrays through direct memory and the whole output array back, as 3.3.3's JNI Get/Release&lt;Type&gt;ArrayElements
 * did; the constants 3.4.1 lost are 3.3.3's values (v1's numbering, which the v1 library reads).
 * <p>
 * Droppable as a unit: this class, core/segments/steps/1.21.11__26.1.json.d/lib-stbir1.json, core/shims/steps/26.1/natives/.
 */
@Environment(EnvType.CLIENT)
public final class CbLibStbir1 {
    private CbLibStbir1() {
    }

    /** the v1 entry points; the library loads on first use (a constant read does not load it) */
    static final class Native {
        static {
            Library.loadSystem(System::load, System::loadLibrary, Native.class, "top.dext.centurybridge.stbir1",
                Platform.mapLibraryNameBundled("cb_lwjgl_stbir1"));
        }

        private Native() {
        }

        static native int nstbir_resize_uint8(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels);

        static native int nstbir_resize_float(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels);

        static native int nstbir_resize_uint8_srgb(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels,
            int alpha, int flags);

        static native int nstbir_resize_uint8_srgb_edgemode(long in, int iw, int ih, int is, long out, int ow, int oh, int os,
            int channels, int alpha, int flags, int edge);

        static native int nstbir_resize_uint8_generic(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels,
            int alpha, int flags, int edge, int filter, int space, long ctx);

        static native int nstbir_resize_uint16_generic(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels,
            int alpha, int flags, int edge, int filter, int space, long ctx);

        static native int nstbir_resize_float_generic(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels,
            int alpha, int flags, int edge, int filter, int space, long ctx);

        static native int nstbir_resize(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int datatype, int channels,
            int alpha, int flags, int edgeH, int edgeV, int filterH, int filterV, int space, long ctx);

        static native int nstbir_resize_subpixel(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int datatype,
            int channels, int alpha, int flags, int edgeH, int edgeV, int filterH, int filterV, int space, long ctx, float xScale,
            float yScale, float xOffset, float yOffset);

        static native int nstbir_resize_region(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int datatype,
            int channels, int alpha, int flags, int edgeH, int edgeV, int filterH, int filterV, int space, long ctx, float s0,
            float t0, float s1, float t1);
    }

    // ------------------------------------------------------------------ constants 3.4.1 dropped (3.3.3's values)

    public static int STBIR_ALPHA_CHANNEL_NONE() {
        return -1;
    }

    public static int STBIR_FLAG_ALPHA_PREMULTIPLIED() {
        return 1;
    }

    public static int STBIR_FLAG_ALPHA_USES_COLORSPACE() {
        return 2;
    }

    public static int STBIR_COLORSPACE_LINEAR() {
        return 0;
    }

    public static int STBIR_COLORSPACE_SRGB() {
        return 1;
    }

    public static int STBIR_TYPE_UINT32() {
        return 2;
    }

    // ------------------------------------------------------------------ the n-forms (addresses)

    public static int nstbir_resize_uint8(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels) {
        return Native.nstbir_resize_uint8(in, iw, ih, is, out, ow, oh, os, channels);
    }

    public static int nstbir_resize_float(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels) {
        return Native.nstbir_resize_float(in, iw, ih, is, out, ow, oh, os, channels);
    }

    public static int nstbir_resize_uint8_srgb(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels,
            int alpha, int flags) {
        return Native.nstbir_resize_uint8_srgb(in, iw, ih, is, out, ow, oh, os, channels, alpha, flags);
    }

    public static int nstbir_resize_uint8_srgb_edgemode(long in, int iw, int ih, int is, long out, int ow, int oh, int os,
            int channels, int alpha, int flags, int edge) {
        return Native.nstbir_resize_uint8_srgb_edgemode(in, iw, ih, is, out, ow, oh, os, channels, alpha, flags, edge);
    }

    public static int nstbir_resize_uint8_generic(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels,
            int alpha, int flags, int edge, int filter, int space, long ctx) {
        return Native.nstbir_resize_uint8_generic(in, iw, ih, is, out, ow, oh, os, channels, alpha, flags, edge, filter, space, ctx);
    }

    public static int nstbir_resize_uint16_generic(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels,
            int alpha, int flags, int edge, int filter, int space, long ctx) {
        return Native.nstbir_resize_uint16_generic(in, iw, ih, is, out, ow, oh, os, channels, alpha, flags, edge, filter, space, ctx);
    }

    public static int nstbir_resize_float_generic(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int channels,
            int alpha, int flags, int edge, int filter, int space, long ctx) {
        return Native.nstbir_resize_float_generic(in, iw, ih, is, out, ow, oh, os, channels, alpha, flags, edge, filter, space, ctx);
    }

    public static int nstbir_resize(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int datatype, int channels,
            int alpha, int flags, int edgeH, int edgeV, int filterH, int filterV, int space, long ctx) {
        return Native.nstbir_resize(in, iw, ih, is, out, ow, oh, os, datatype, channels, alpha, flags, edgeH, edgeV, filterH, filterV,
            space, ctx);
    }

    public static int nstbir_resize_subpixel(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int datatype,
            int channels, int alpha, int flags, int edgeH, int edgeV, int filterH, int filterV, int space, long ctx, float xScale,
            float yScale, float xOffset, float yOffset) {
        return Native.nstbir_resize_subpixel(in, iw, ih, is, out, ow, oh, os, datatype, channels, alpha, flags, edgeH, edgeV, filterH,
            filterV, space, ctx, xScale, yScale, xOffset, yOffset);
    }

    public static int nstbir_resize_region(long in, int iw, int ih, int is, long out, int ow, int oh, int os, int datatype,
            int channels, int alpha, int flags, int edgeH, int edgeV, int filterH, int filterV, int space, long ctx, float s0,
            float t0, float s1, float t1) {
        return Native.nstbir_resize_region(in, iw, ih, is, out, ow, oh, os, datatype, channels, alpha, flags, edgeH, edgeV, filterH,
            filterV, space, ctx, s0, t0, s1, t1);
    }

    /** the array n-forms: the arrays through direct memory, the whole output array copied back (JNI Release mode 0) */
    public static int nstbir_resize_float(float[] in, int iw, int ih, int is, float[] out, int ow, int oh, int os, int channels) {
        FloatBuffer bin = MemoryUtil.memAllocFloat(in.length).put(in).flip();
        FloatBuffer bout = MemoryUtil.memAllocFloat(out.length).put(out).flip();
        try {
            int r = Native.nstbir_resize_float(MemoryUtil.memAddress(bin), iw, ih, is, MemoryUtil.memAddress(bout), ow, oh, os, channels);
            bout.get(out);
            return r;
        } finally {
            MemoryUtil.memFree(bout);
            MemoryUtil.memFree(bin);
        }
    }

    public static int nstbir_resize_uint16_generic(short[] in, int iw, int ih, int is, short[] out, int ow, int oh, int os,
            int channels, int alpha, int flags, int edge, int filter, int space, long ctx) {
        ShortBuffer bin = MemoryUtil.memAllocShort(in.length).put(in).flip();
        ShortBuffer bout = MemoryUtil.memAllocShort(out.length).put(out).flip();
        try {
            int r = Native.nstbir_resize_uint16_generic(MemoryUtil.memAddress(bin), iw, ih, is, MemoryUtil.memAddress(bout), ow, oh, os,
                channels, alpha, flags, edge, filter, space, ctx);
            bout.get(out);
            return r;
        } finally {
            MemoryUtil.memFree(bout);
            MemoryUtil.memFree(bin);
        }
    }

    public static int nstbir_resize_float_generic(float[] in, int iw, int ih, int is, float[] out, int ow, int oh, int os,
            int channels, int alpha, int flags, int edge, int filter, int space, long ctx) {
        FloatBuffer bin = MemoryUtil.memAllocFloat(in.length).put(in).flip();
        FloatBuffer bout = MemoryUtil.memAllocFloat(out.length).put(out).flip();
        try {
            int r = Native.nstbir_resize_float_generic(MemoryUtil.memAddress(bin), iw, ih, is, MemoryUtil.memAddress(bout), ow, oh, os,
                channels, alpha, flags, edge, filter, space, ctx);
            bout.get(out);
            return r;
        } finally {
            MemoryUtil.memFree(bout);
            MemoryUtil.memFree(bin);
        }
    }

    // ------------------------------------------------------------------ the checked forms (3.3.3's bodies)

    /** 3.3.3's getTypeShift: bytes per value of a stbir_datatype as a shift (UINT8 0, UINT16 1, UINT32 / FLOAT 2) */
    private static int typeShift(int datatype) {
        switch (datatype) {
            case 0:
                return 0;
            case 1:
                return 1;
            default:
                return 2;
        }
    }

    public static boolean stbir_resize_uint8(ByteBuffer in, int iw, int ih, int is, ByteBuffer out, int ow, int oh, int os, int channels) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? iw * channels : is));
            Checks.check(out, oh * (os == 0 ? ow * channels : os));
        }
        return nstbir_resize_uint8(MemoryUtil.memAddress(in), iw, ih, is, MemoryUtil.memAddress(out), ow, oh, os, channels) != 0;
    }

    public static boolean stbir_resize_float(FloatBuffer in, int iw, int ih, int is, FloatBuffer out, int ow, int oh, int os,
            int channels) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? iw * channels : is >> 2));
            Checks.check(out, oh * (os == 0 ? ow * channels : os >> 2));
        }
        return nstbir_resize_float(MemoryUtil.memAddress(in), iw, ih, is, MemoryUtil.memAddress(out), ow, oh, os, channels) != 0;
    }

    public static boolean stbir_resize_uint8_srgb(ByteBuffer in, int iw, int ih, int is, ByteBuffer out, int ow, int oh, int os,
            int channels, int alpha, int flags) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? iw * channels : is));
            Checks.check(out, oh * (os == 0 ? ow * channels : os));
        }
        return nstbir_resize_uint8_srgb(MemoryUtil.memAddress(in), iw, ih, is, MemoryUtil.memAddress(out), ow, oh, os, channels, alpha,
            flags) != 0;
    }

    public static boolean stbir_resize_uint8_srgb_edgemode(ByteBuffer in, int iw, int ih, int is, ByteBuffer out, int ow, int oh,
            int os, int channels, int alpha, int flags, int edge) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? iw * channels : is));
            Checks.check(out, oh * (os == 0 ? ow * channels : os));
        }
        return nstbir_resize_uint8_srgb_edgemode(MemoryUtil.memAddress(in), iw, ih, is, MemoryUtil.memAddress(out), ow, oh, os, channels,
            alpha, flags, edge) != 0;
    }

    public static boolean stbir_resize_uint8_generic(ByteBuffer in, int iw, int ih, int is, ByteBuffer out, int ow, int oh, int os,
            int channels, int alpha, int flags, int edge, int filter, int space) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? iw * channels : is));
            Checks.check(out, oh * (os == 0 ? ow * channels : os));
        }
        return nstbir_resize_uint8_generic(MemoryUtil.memAddress(in), iw, ih, is, MemoryUtil.memAddress(out), ow, oh, os, channels, alpha,
            flags, edge, filter, space, MemoryUtil.NULL) != 0;
    }

    public static boolean stbir_resize_uint16_generic(ShortBuffer in, int iw, int ih, int is, ShortBuffer out, int ow, int oh, int os,
            int channels, int alpha, int flags, int edge, int filter, int space) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? iw * channels : is >> 1));
            Checks.check(out, oh * (os == 0 ? ow * channels : os >> 1));
        }
        return nstbir_resize_uint16_generic(MemoryUtil.memAddress(in), iw, ih, is, MemoryUtil.memAddress(out), ow, oh, os, channels, alpha,
            flags, edge, filter, space, MemoryUtil.NULL) != 0;
    }

    public static boolean stbir_resize_float_generic(FloatBuffer in, int iw, int ih, int is, FloatBuffer out, int ow, int oh, int os,
            int channels, int alpha, int flags, int edge, int filter, int space) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? iw * channels : is >> 2));
            Checks.check(out, oh * (os == 0 ? ow * channels : os >> 2));
        }
        return nstbir_resize_float_generic(MemoryUtil.memAddress(in), iw, ih, is, MemoryUtil.memAddress(out), ow, oh, os, channels, alpha,
            flags, edge, filter, space, MemoryUtil.NULL) != 0;
    }

    public static boolean stbir_resize(ByteBuffer in, int iw, int ih, int is, ByteBuffer out, int ow, int oh, int os, int datatype,
            int channels, int alpha, int flags, int edgeH, int edgeV, int filterH, int filterV, int space) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? (iw * channels) << typeShift(datatype) : is));
            Checks.check(out, oh * (os == 0 ? (ow * channels) << typeShift(datatype) : os));
        }
        return nstbir_resize(MemoryUtil.memAddress(in), iw, ih, is, MemoryUtil.memAddress(out), ow, oh, os, datatype, channels, alpha,
            flags, edgeH, edgeV, filterH, filterV, space, MemoryUtil.NULL) != 0;
    }

    public static boolean stbir_resize_subpixel(ByteBuffer in, int iw, int ih, int is, ByteBuffer out, int ow, int oh, int os,
            int datatype, int channels, int alpha, int flags, int edgeH, int edgeV, int filterH, int filterV, int space, float xScale,
            float yScale, float xOffset, float yOffset) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? (iw * channels) << typeShift(datatype) : is));
            Checks.check(out, oh * (os == 0 ? (ow * channels) << typeShift(datatype) : os));
        }
        return nstbir_resize_subpixel(MemoryUtil.memAddress(in), iw, ih, is, MemoryUtil.memAddress(out), ow, oh, os, datatype, channels,
            alpha, flags, edgeH, edgeV, filterH, filterV, space, MemoryUtil.NULL, xScale, yScale, xOffset, yOffset) != 0;
    }

    public static boolean stbir_resize_region(ByteBuffer in, int iw, int ih, int is, ByteBuffer out, int ow, int oh, int os,
            int datatype, int channels, int alpha, int flags, int edgeH, int edgeV, int filterH, int filterV, int space, float s0,
            float t0, float s1, float t1) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? (iw * channels) << typeShift(datatype) : is));
            Checks.check(out, oh * (os == 0 ? (ow * channels) << typeShift(datatype) : os));
        }
        return nstbir_resize_region(MemoryUtil.memAddress(in), iw, ih, is, MemoryUtil.memAddress(out), ow, oh, os, datatype, channels,
            alpha, flags, edgeH, edgeV, filterH, filterV, space, MemoryUtil.NULL, s0, t0, s1, t1) != 0;
    }

    public static boolean stbir_resize_float(float[] in, int iw, int ih, int is, float[] out, int ow, int oh, int os, int channels) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? iw * channels : is >> 2));
            Checks.check(out, oh * (os == 0 ? ow * channels : os >> 2));
        }
        return nstbir_resize_float(in, iw, ih, is, out, ow, oh, os, channels) != 0;
    }

    public static boolean stbir_resize_uint16_generic(short[] in, int iw, int ih, int is, short[] out, int ow, int oh, int os,
            int channels, int alpha, int flags, int edge, int filter, int space) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? iw * channels : is >> 1));
            Checks.check(out, oh * (os == 0 ? ow * channels : os >> 1));
        }
        return nstbir_resize_uint16_generic(in, iw, ih, is, out, ow, oh, os, channels, alpha, flags, edge, filter, space,
            MemoryUtil.NULL) != 0;
    }

    public static boolean stbir_resize_float_generic(float[] in, int iw, int ih, int is, float[] out, int ow, int oh, int os,
            int channels, int alpha, int flags, int edge, int filter, int space) {
        if (Checks.CHECKS) {
            Checks.check(in, ih * (is == 0 ? iw * channels : is >> 2));
            Checks.check(out, oh * (os == 0 ? ow * channels : os >> 2));
        }
        return nstbir_resize_float_generic(in, iw, ih, is, out, ow, oh, os, channels, alpha, flags, edge, filter, space,
            MemoryUtil.NULL) != 0;
    }
}
