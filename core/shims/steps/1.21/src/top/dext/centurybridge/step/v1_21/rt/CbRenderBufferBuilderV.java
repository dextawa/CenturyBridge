package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderRenderedBuffer;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderSortState;

/**
 * 1.20.6's BufferBuilder API on 1.21's BufferBuilder (class_287), which CbRenderBufferBuilderMixin gives every
 * builder: 1.20.6's long-lived builder (begin / end / endOrDiscardIfEmpty, sorting and its saved state, clear /
 * discard / release), its element cursor (currentElement / nextElement / putByte / putShort / putFloat) and the
 * VertexConsumer calls 1.21 dropped (vertex(double..), overlayCoords(int, int), uv2(int), normal(pose, ..), the
 * whole vertex of fourteen arguments, endVertex, defaultColor / unsetDefaultColor). The names are 1.20.6's, so
 * 1.20.6 calls on a BufferBuilder link to them (covers); CenturyBridge code reaches them through this interface.
 *
 * vertex(double..), endVertex, vertex(matrix, ..) and vertex(pose, ..) come twice: the 1.20.6 name every caller
 * reaches, and the body under CenturyBridge's name (cbLegacy...). A 1.20.6 BufferBuilder subclass's own override
 * of one of them is renamed to the body's name (overrideRenames), so it stays reached from both and its
 * super calls land on the body.
 */
@Environment(EnvType.CLIENT)
public interface CbRenderBufferBuilderV {

    // ------------------------------------------------------------------ the builder

    /** begin(mode, format) */
    void method_1328(net.minecraft.class_293.class_5596 mode, net.minecraft.class_293 format);

    /** end() */
    BufferBuilderRenderedBuffer method_1326();

    /** endOrDiscardIfEmpty() */
    BufferBuilderRenderedBuffer method_43575();

    /** isCurrentBatchEmpty() */
    boolean method_43574();

    /** building() */
    boolean method_22893();

    /** clear() */
    void method_1343();

    /** discard() */
    void method_23477();

    /** release() */
    void method_54558();

    /** setQuadSorting(sorting) */
    void method_49904(net.minecraft.class_8251 sorting);

    /** getSortState() */
    BufferBuilderSortState method_1334();

    /** restoreSortState(state) */
    void method_1324(BufferBuilderSortState state);

    // ------------------------------------------------------------------ the element cursor

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

    /** uvShort(u, v, index) */
    net.minecraft.class_4588 method_22899(short u, short v, int index);

    // ------------------------------------------------------------------ 1.20.6's VertexConsumer calls

    /** vertex(x, y, z) */
    net.minecraft.class_4588 method_22912(double x, double y, double z);

    /** vertex(x, y, z): the body */
    net.minecraft.class_4588 cbLegacyVertex(double x, double y, double z);

    /** vertex(matrix, x, y, z): the body (the 1.20.6 name is the 1.21 one, method_22918) */
    net.minecraft.class_4588 cbLegacyVertexMatrix(org.joml.Matrix4f matrix, float x, float y, float z);

    /** vertex(pose, x, y, z): the body (the 1.20.6 name is the 1.21 one, method_56824) */
    net.minecraft.class_4588 cbLegacyVertexPose(net.minecraft.class_4587.class_4665 pose, float x, float y, float z);

    /** overlayCoords(u, v) */
    net.minecraft.class_4588 method_22917(int u, int v);

    /** uv2(packed) */
    net.minecraft.class_4588 method_22916(int packed);

    /** normal(pose, x, y, z) */
    net.minecraft.class_4588 method_23763(net.minecraft.class_4587.class_4665 pose, float x, float y, float z);

    /** vertex(x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz) */
    void method_23919(float x, float y, float z, float r, float g, float b, float a, float u, float v, int overlay, int light,
                      float nx, float ny, float nz);

    /** endVertex() */
    void method_1344();

    /** endVertex(): the body */
    void cbLegacyEndVertex();

    /** defaultColor(r, g, b, a) */
    void method_22901(int r, int g, int b, int a);

    /** unsetDefaultColor() */
    void method_35666();

    // ------------------------------------------------------------------ CenturyBridge's own

    /** the ByteBufferBuilder the builder writes into */
    net.minecraft.class_9799 cb$byteBuffer();

    /** made by CenturyBridge as 1.20.6 made a builder: idle until begun (1.21's constructor leaves it building) */
    void cb$idle();

    /** the mode the builder builds in */
    net.minecraft.class_293.class_5596 cb$mode();

    /** the format the builder builds in */
    net.minecraft.class_293 cb$format();
}
