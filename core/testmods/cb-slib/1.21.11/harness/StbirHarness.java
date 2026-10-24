package cb.slib;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.security.MessageDigest;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImageResize;

/**
 * A 1.21.11-era program (compiled against LWJGL 3.3.3) resizing the same random images through STBImageResize in every
 * filter / edge mode / colorspace / channel count, plus every other entry point (array forms, n-forms, sRGB forms,
 * stbir_resize with UINT32 and 6 channels, subpixel, region). Writes every output's bytes to <out>.bin and one line per call
 * (label, return value, sha256 of the output) to <out>.txt. Run once on LWJGL 3.3.3 with its own natives (the reference) and
 * once converted by CenturyBridge's wall step on LWJGL 3.4.1 with the layer: the two must be byte-identical.
 */
public final class StbirHarness {
    static DataOutputStream bin;
    static PrintStream txt;
    static int calls;

    static void record(String label, boolean ok, byte[] out) throws Exception {
        bin.write(out);
        StringBuilder h = new StringBuilder();
        for (byte b : MessageDigest.getInstance("SHA-256").digest(out)) {
            h.append(String.format("%02x", b));
        }
        txt.println(label + "\t" + ok + "\t" + h);
        calls++;
    }

    static byte[] bytes(ByteBuffer b) {
        byte[] a = new byte[b.remaining()];
        b.duplicate().get(a);
        return a;
    }

    static byte[] bytes(FloatBuffer f) {
        ByteBuffer b = ByteBuffer.allocate(f.remaining() * 4);
        b.asFloatBuffer().put(f.duplicate());
        return b.array();
    }

    static byte[] bytes(ShortBuffer s) {
        ByteBuffer b = ByteBuffer.allocate(s.remaining() * 2);
        b.asShortBuffer().put(s.duplicate());
        return b.array();
    }

    static byte[] bytes(float[] f) {
        ByteBuffer b = ByteBuffer.allocate(f.length * 4);
        b.asFloatBuffer().put(f);
        return b.array();
    }

    static byte[] bytes(short[] s) {
        ByteBuffer b = ByteBuffer.allocate(s.length * 2);
        b.asShortBuffer().put(s);
        return b.array();
    }

    public static void main(String[] args) throws Exception {
        bin = new DataOutputStream(new FileOutputStream(args[0] + ".bin"));
        txt = new PrintStream(new FileOutputStream(args[0] + ".txt"), true, "UTF-8");
        int[][] sizes = {{37, 23, 64, 40}, {37, 23, 16, 11}, {64, 64, 32, 32}, {20, 20, 20, 20}, {17, 9, 51, 27}};
        Random rnd = new Random(12345);
        for (int[] s : sizes) {
            int iw = s[0], ih = s[1], ow = s[2], oh = s[3];
            for (int ch = 1; ch <= 4; ch++) {
                ByteBuffer in = BufferUtils.createByteBuffer(iw * ih * ch);
                FloatBuffer fin = BufferUtils.createFloatBuffer(iw * ih * ch);
                for (int i = 0; i < iw * ih * ch; i++) {
                    int v = rnd.nextInt(256);
                    in.put(i, (byte) v);
                    fin.put(i, v / 255.0f);
                }
                int alpha = ch == 4 ? 3 : ch == 2 ? 1 : STBImageResize.STBIR_ALPHA_CHANNEL_NONE;
                for (int f = STBImageResize.STBIR_FILTER_DEFAULT; f <= STBImageResize.STBIR_FILTER_MITCHELL; f++) {
                    for (int e = STBImageResize.STBIR_EDGE_CLAMP; e <= STBImageResize.STBIR_EDGE_ZERO; e++) {
                        for (int cs = STBImageResize.STBIR_COLORSPACE_LINEAR; cs <= STBImageResize.STBIR_COLORSPACE_SRGB; cs++) {
                            ByteBuffer out = BufferUtils.createByteBuffer(ow * oh * ch);
                            boolean ok = STBImageResize.stbir_resize_uint8_generic(in, iw, ih, 0, out, ow, oh, 0, ch, alpha, 0, e, f, cs);
                            record("u8 " + iw + "x" + ih + ">" + ow + "x" + oh + " ch" + ch + " f" + f + " e" + e + " cs" + cs, ok, bytes(out));
                        }
                        FloatBuffer fout = BufferUtils.createFloatBuffer(ow * oh * ch);
                        boolean ok = STBImageResize.stbir_resize_float_generic(fin, iw, ih, 0, fout, ow, oh, 0, ch, alpha, 0, e, f,
                            STBImageResize.STBIR_COLORSPACE_LINEAR);
                        record("f32 " + iw + "x" + ih + ">" + ow + "x" + oh + " ch" + ch + " f" + f + " e" + e, ok, bytes(fout));
                    }
                }
            }
        }
        int configs = calls;
        // every other entry point, on one image
        int iw = 37, ih = 23, ow = 64, oh = 40;
        ByteBuffer in4 = BufferUtils.createByteBuffer(iw * ih * 4);
        float[] fa = new float[iw * ih * 4];
        short[] sa = new short[iw * ih * 4];
        ShortBuffer sb = BufferUtils.createShortBuffer(iw * ih * 4);
        for (int i = 0; i < iw * ih * 4; i++) {
            int v = rnd.nextInt(65536);
            in4.put(i, (byte) v);
            fa[i] = v / 65535.0f;
            sa[i] = (short) v;
            sb.put(i, (short) v);
        }
        ByteBuffer out = BufferUtils.createByteBuffer(ow * oh * 4);
        record("uint8", STBImageResize.stbir_resize_uint8(in4, iw, ih, 0, out, ow, oh, 0, 4), bytes(out));
        out = BufferUtils.createByteBuffer(ow * oh * 4);
        record("uint8 n", STBImageResize.nstbir_resize_uint8(org.lwjgl.system.MemoryUtil.memAddress(in4), iw, ih, 0,
            org.lwjgl.system.MemoryUtil.memAddress(out), ow, oh, 0, 4) != 0, bytes(out));
        out = BufferUtils.createByteBuffer(ow * oh * 4);
        record("uint8 srgb", STBImageResize.stbir_resize_uint8_srgb(in4, iw, ih, 0, out, ow, oh, 0, 4, 3,
            STBImageResize.STBIR_FLAG_ALPHA_PREMULTIPLIED), bytes(out));
        out = BufferUtils.createByteBuffer(ow * oh * 4);
        record("uint8 srgb edgemode", STBImageResize.stbir_resize_uint8_srgb_edgemode(in4, iw, ih, 0, out, ow, oh, 0, 4, 3,
            STBImageResize.STBIR_FLAG_ALPHA_USES_COLORSPACE, STBImageResize.STBIR_EDGE_WRAP), bytes(out));
        FloatBuffer fb = BufferUtils.createFloatBuffer(iw * ih * 4).put(fa).flip();
        FloatBuffer fout = BufferUtils.createFloatBuffer(ow * oh * 4);
        record("float", STBImageResize.stbir_resize_float(fb, iw, ih, 0, fout, ow, oh, 0, 4), bytes(fout));
        float[] faout = new float[ow * oh * 4];
        faout[faout.length - 1] = 7f;
        record("float[]", STBImageResize.stbir_resize_float(fa, iw, ih, 0, faout, ow, oh, 0, 4), bytes(faout));
        faout = new float[ow * oh * 4 + 3];
        faout[faout.length - 1] = 9f; // beyond the image: kept, as JNI Release copied the whole array back
        record("float[] generic", STBImageResize.stbir_resize_float_generic(fa, iw, ih, 0, faout, ow, oh, 0, 4, 3, 0,
            STBImageResize.STBIR_EDGE_REFLECT, STBImageResize.STBIR_FILTER_CATMULLROM, STBImageResize.STBIR_COLORSPACE_LINEAR), bytes(faout));
        ShortBuffer sout = BufferUtils.createShortBuffer(ow * oh * 4);
        record("uint16", STBImageResize.stbir_resize_uint16_generic(sb, iw, ih, 0, sout, ow, oh, 0, 4, 3, 0,
            STBImageResize.STBIR_EDGE_CLAMP, STBImageResize.STBIR_FILTER_MITCHELL, STBImageResize.STBIR_COLORSPACE_SRGB), bytes(sout));
        short[] saout = new short[ow * oh * 4];
        record("uint16[]", STBImageResize.stbir_resize_uint16_generic(sa, iw, ih, 0, saout, ow, oh, 0, 4, 3, 0,
            STBImageResize.STBIR_EDGE_ZERO, STBImageResize.STBIR_FILTER_BOX, STBImageResize.STBIR_COLORSPACE_LINEAR), bytes(saout));
        // stbir_resize: uint32 (v1 only) and 6 channels with alpha in the middle (v1 only)
        ByteBuffer in32 = BufferUtils.createByteBuffer(iw * ih * 4 * 4);
        for (int i = 0; i < in32.capacity(); i++) {
            in32.put(i, (byte) rnd.nextInt(256));
        }
        ByteBuffer out32 = BufferUtils.createByteBuffer(ow * oh * 4 * 4);
        record("resize uint32", STBImageResize.stbir_resize(in32, iw, ih, 0, out32, ow, oh, 0, STBImageResize.STBIR_TYPE_UINT32, 4, 3, 0,
            STBImageResize.STBIR_EDGE_CLAMP, STBImageResize.STBIR_EDGE_WRAP, STBImageResize.STBIR_FILTER_TRIANGLE,
            STBImageResize.STBIR_FILTER_CUBICBSPLINE, STBImageResize.STBIR_COLORSPACE_LINEAR), bytes(out32));
        ByteBuffer in6 = BufferUtils.createByteBuffer(iw * ih * 6);
        for (int i = 0; i < in6.capacity(); i++) {
            in6.put(i, (byte) rnd.nextInt(256));
        }
        ByteBuffer out6 = BufferUtils.createByteBuffer(ow * oh * 6);
        record("resize 6ch alpha2", STBImageResize.stbir_resize(in6, iw, ih, 0, out6, ow, oh, 0, STBImageResize.STBIR_TYPE_UINT8, 6, 2, 0,
            STBImageResize.STBIR_EDGE_REFLECT, STBImageResize.STBIR_EDGE_REFLECT, STBImageResize.STBIR_FILTER_DEFAULT,
            STBImageResize.STBIR_FILTER_DEFAULT, STBImageResize.STBIR_COLORSPACE_SRGB), bytes(out6));
        out = BufferUtils.createByteBuffer(ow * oh * 4);
        record("subpixel", STBImageResize.stbir_resize_subpixel(in4, iw, ih, 0, out, ow, oh, 0, STBImageResize.STBIR_TYPE_UINT8, 4, 3, 0,
            STBImageResize.STBIR_EDGE_CLAMP, STBImageResize.STBIR_EDGE_CLAMP, STBImageResize.STBIR_FILTER_MITCHELL,
            STBImageResize.STBIR_FILTER_MITCHELL, STBImageResize.STBIR_COLORSPACE_LINEAR, 1.7f, 1.7f, 0.25f, 0.5f), bytes(out));
        out = BufferUtils.createByteBuffer(ow * oh * 4);
        record("region", STBImageResize.stbir_resize_region(in4, iw, ih, 0, out, ow, oh, 0, STBImageResize.STBIR_TYPE_UINT8, 4, 3, 0,
            STBImageResize.STBIR_EDGE_CLAMP, STBImageResize.STBIR_EDGE_CLAMP, STBImageResize.STBIR_FILTER_BOX,
            STBImageResize.STBIR_FILTER_BOX, STBImageResize.STBIR_COLORSPACE_LINEAR, 0.1f, 0.2f, 0.8f, 0.9f), bytes(out));
        bin.close();
        System.out.println("STBIR-HARNESS " + configs + " configurations + " + (calls - configs) + " entry-point calls");
    }
}
