package top.dext.centurybridge.step.v1_21.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's BufferBuilder.RenderedBuffer (class_287$class_7433, gone in 1.21): a finished batch of a builder,
 * drawn once (BufferUploader / VertexBuffer.upload) and then released. On 1.21 a batch is a MeshData (vertices,
 * and the sorted indices when the builder sorted its quads), or -- for the index-only batch a restored sort
 * state ends in -- the ByteBufferBuilder.Result of the re-sorted indices; an empty batch has neither.
 * vertexBuffer() / indexBuffer() answer the bytes 1.20.6 answered (null for an index-only batch's vertices and
 * a sequential batch's indices), release() closes the 1.21 memory once, as 1.20.6 released it once.
 *
 * The 1.20.6 fields stand as they were (the batch's start in the builder's buffer is 0 here: the 1.21 memory is
 * the batch's own); they are public because a mod's access widener may name them, and wideners never reach a
 * CenturyBridge class.
 */
@Environment(EnvType.CLIENT)
public class BufferBuilderRenderedBuffer {

    /** pointer: where the batch starts in the builder's buffer */
    public final int field_39064;

    /** drawState */
    public final BufferBuilderDrawState field_39065;

    /** released */
    public boolean field_39066;

    /** the builder that ended the batch */
    public final net.minecraft.class_287 field_39063;

    /** the batch's vertices (and sorted indices), or null */
    private final net.minecraft.class_9801 cbMesh;

    /** the re-sorted indices of an index-only batch, or null */
    private final net.minecraft.class_9799.class_9800 cbIndices;

    /**
     * 1.20.6's (package-private) constructor: a batch of the builder's buffer at the pointer. A 1.21 builder has
     * no such buffer to point into, so a batch made this way carries no data of its own (a 1.20.6 mod could not
     * call it: it was package-private).
     */
    public BufferBuilderRenderedBuffer(net.minecraft.class_287 builder, int pointer, BufferBuilderDrawState drawState) {
        this(builder, pointer, drawState, null, null);
    }

    private BufferBuilderRenderedBuffer(net.minecraft.class_287 builder, int pointer, BufferBuilderDrawState drawState,
                                        net.minecraft.class_9801 mesh, net.minecraft.class_9799.class_9800 indices) {
        this.field_39063 = builder;
        this.field_39064 = pointer;
        this.field_39065 = drawState;
        this.cbMesh = mesh;
        this.cbIndices = indices;
    }

    /** a batch 1.21's builder finished: its mesh (null when empty) or the re-sorted indices of an index-only one */
    public static BufferBuilderRenderedBuffer cbOf(net.minecraft.class_287 builder, BufferBuilderDrawState drawState,
                                                   net.minecraft.class_9801 mesh, net.minecraft.class_9799.class_9800 indices) {
        return new BufferBuilderRenderedBuffer(builder, 0, drawState, mesh, indices);
    }

    /** vertexBuffer(): the vertices (none of an index-only batch) */
    public java.nio.ByteBuffer method_43581() {
        if (this.field_39065.comp_754) {
            return null;
        }
        return this.cbMesh != null ? this.cbMesh.method_60818() : cbEmpty();
    }

    /** indexBuffer(): the indices (none when the sequential buffer serves) */
    public java.nio.ByteBuffer method_43582() {
        if (this.field_39065.comp_755) {
            return null;
        }
        if (this.cbIndices != null) {
            return this.cbIndices.method_60817();
        }
        java.nio.ByteBuffer b = this.cbMesh != null ? this.cbMesh.method_60821() : null;
        return b != null ? b : cbEmpty();
    }

    /** drawState() */
    public BufferBuilderDrawState method_43583() {
        return this.field_39065;
    }

    /** isEmpty() */
    public boolean method_43584() {
        return this.field_39065.comp_750 == 0;
    }

    /** release(): once; the 1.21 memory goes back to its builder */
    public void method_43585() {
        if (this.field_39066) {
            throw new IllegalStateException("Buffer has already been released!");
        }
        this.cbClose();
        this.field_39066 = true;
    }

    // ------------------------------------------------------------------ CenturyBridge's own

    /** the mesh 1.21 draws (null when empty or index-only) */
    public net.minecraft.class_9801 cbMesh() {
        return this.cbMesh;
    }

    /** the re-sorted indices of an index-only batch (else null) */
    public net.minecraft.class_9799.class_9800 cbIndices() {
        return this.cbIndices;
    }

    /** 1.21 consumed the batch (an upload closes what it uploads): released as 1.20.6's upload released it */
    public void cbConsumed() {
        this.field_39066 = true;
    }

    private void cbClose() {
        if (this.cbMesh != null) {
            this.cbMesh.close();
        }
        if (this.cbIndices != null) {
            this.cbIndices.close();
        }
    }

    /** 1.20.6 sliced an empty batch out of its native buffer: a zero-length direct buffer in native order */
    private static java.nio.ByteBuffer cbEmpty() {
        return java.nio.ByteBuffer.allocateDirect(0).order(java.nio.ByteOrder.nativeOrder());
    }
}
