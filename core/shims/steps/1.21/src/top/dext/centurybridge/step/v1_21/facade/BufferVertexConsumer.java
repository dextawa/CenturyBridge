package top.dext.centurybridge.step.v1_21.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.rt.CbRenderLegacyConsumer;

/**
 * 1.20.6's BufferVertexConsumer (class_4584, gone in 1.21): a VertexConsumer that writes each element at an
 * element cursor -- currentElement(), the element it stands on; nextElement(), on to the next; putByte / putShort
 * / putFloat at an offset within the current element -- with 1.20.6's defaults writing position, color, UVs and
 * normal there when the cursor stands on them, of the type and count 1.20.6 checked. A 1.20.6 class implementing
 * it is a 1.20.6 consumer (CbRenderLegacyConsumer); its vertex(double..) default is the body
 * (cbLegacyVertex) every vertex call reaches. 1.21's BufferBuilder answers the same calls with its own cursor
 * (CbRenderBufferBuilderMixin), which 1.20.6 calls through this type reach (CbRenderVertex.cbBuffer..).
 */
@Environment(EnvType.CLIENT)
public interface BufferVertexConsumer extends CbRenderLegacyConsumer {

    /** currentElement() */
    net.minecraft.class_296 method_22900();

    /** nextElement() */
    void method_1325();

    /** putByte(index, value) */
    void method_22896(int index, byte value);

    /** putShort(index, value) */
    void method_22898(int index, short value);

    /** putFloat(index, value) */
    void method_22897(int index, float value);

    /** vertex(x, y, z) of 1.20.6: three floats at a POSITION cursor */
    @Override
    default net.minecraft.class_4588 cbLegacyVertex(double x, double y, double z) {
        net.minecraft.class_296 e = this.method_22900();
        if (e.comp_2845() != net.minecraft.class_296.class_298.field_1633) {
            return this;
        }
        if (e.comp_2844() != net.minecraft.class_296.class_297.field_1623 || e.comp_2846() != 3) {
            throw new IllegalStateException();
        }
        this.method_22897(0, (float) x);
        this.method_22897(4, (float) y);
        this.method_22897(8, (float) z);
        this.method_1325();
        return this;
    }

    /** color(r, g, b, a) of 1.20.6: four unsigned bytes at a COLOR cursor */
    @Override
    default net.minecraft.class_4588 method_1336(int r, int g, int b, int a) {
        net.minecraft.class_296 e = this.method_22900();
        if (e.comp_2845() != net.minecraft.class_296.class_298.field_1632) {
            return this;
        }
        if (e.comp_2844() != net.minecraft.class_296.class_297.field_1624 || e.comp_2846() != 4) {
            throw new IllegalStateException();
        }
        this.method_22896(0, (byte) r);
        this.method_22896(1, (byte) g);
        this.method_22896(2, (byte) b);
        this.method_22896(3, (byte) a);
        this.method_1325();
        return this;
    }

    /** uv(u, v) of 1.20.6: two floats at the UV0 cursor */
    @Override
    default net.minecraft.class_4588 method_22913(float u, float v) {
        net.minecraft.class_296 e = this.method_22900();
        if (e.comp_2845() != net.minecraft.class_296.class_298.field_1636 || e.comp_2843() != 0) {
            return this;
        }
        if (e.comp_2844() != net.minecraft.class_296.class_297.field_1623 || e.comp_2846() != 2) {
            throw new IllegalStateException();
        }
        this.method_22897(0, u);
        this.method_22897(4, v);
        this.method_1325();
        return this;
    }

    /** overlayCoords(u, v) of 1.20.6: the UV1 shorts */
    @Override
    default net.minecraft.class_4588 method_22917(int u, int v) {
        return this.method_22899((short) u, (short) v, 1);
    }

    /** uv2(u, v) of 1.20.6: the UV2 shorts */
    @Override
    default net.minecraft.class_4588 method_22921(int u, int v) {
        return this.method_22899((short) u, (short) v, 2);
    }

    /** uvShort(u, v, index) of 1.20.6: two shorts at the UV cursor of that index */
    default net.minecraft.class_4588 method_22899(short u, short v, int index) {
        net.minecraft.class_296 e = this.method_22900();
        if (e.comp_2845() != net.minecraft.class_296.class_298.field_1636 || e.comp_2843() != index) {
            return this;
        }
        if (e.comp_2844() != net.minecraft.class_296.class_297.field_1625 || e.comp_2846() != 2) {
            throw new IllegalStateException();
        }
        this.method_22898(0, u);
        this.method_22898(2, v);
        this.method_1325();
        return this;
    }

    /** normal(x, y, z) of 1.20.6: three signed bytes at a NORMAL cursor */
    @Override
    default net.minecraft.class_4588 method_22914(float x, float y, float z) {
        net.minecraft.class_296 e = this.method_22900();
        if (e.comp_2845() != net.minecraft.class_296.class_298.field_1635) {
            return this;
        }
        if (e.comp_2844() != net.minecraft.class_296.class_297.field_1621 || e.comp_2846() != 3) {
            throw new IllegalStateException();
        }
        this.method_22896(0, method_24212(x));
        this.method_22896(1, method_24212(y));
        this.method_22896(2, method_24212(z));
        this.method_1325();
        return this;
    }

    /** normalIntValue(f) of 1.20.6: the clamped component as a signed byte */
    static byte method_24212(float f) {
        return (byte) ((int) (net.minecraft.class_3532.method_15363(f, -1.0F, 1.0F) * 127.0F) & 255);
    }
}
