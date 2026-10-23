package top.dext.centurybridge.step.v1_21.facade;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's BufferBuilder.DrawState (record class_287$class_4574, gone in 1.21, which keeps a five-component
 * MeshData.DrawState): format, vertexCount, indexCount, mode, indexType, indexOnly (the buffer holds only the
 * re-sorted indices of a restored sort state), sequentialIndex (no index data: the shared sequential buffer
 * serves), and the byte layout 1.20.6 computed from them -- vertices first, then indices. A plain final class
 * with record semantics (equals / hashCode / toString over the seven components) because members 1.20.6 kept
 * private must be public here: a mod's access widener may name them, and wideners never reach a CenturyBridge
 * class.
 */
@Environment(EnvType.CLIENT)
public final class BufferBuilderDrawState {

    /** format */
    public final net.minecraft.class_293 comp_749;

    /** vertexCount */
    public final int comp_750;

    /** indexCount */
    public final int comp_751;

    /** mode */
    public final net.minecraft.class_293.class_5596 comp_752;

    /** indexType */
    public final net.minecraft.class_293.class_5595 comp_753;

    /** indexOnly */
    public final boolean comp_754;

    /** sequentialIndex */
    public final boolean comp_755;

    public BufferBuilderDrawState(net.minecraft.class_293 format, int vertexCount, int indexCount, net.minecraft.class_293.class_5596 mode,
                                  net.minecraft.class_293.class_5595 indexType, boolean indexOnly, boolean sequentialIndex) {
        this.comp_749 = format;
        this.comp_750 = vertexCount;
        this.comp_751 = indexCount;
        this.comp_752 = mode;
        this.comp_753 = indexType;
        this.comp_754 = indexOnly;
        this.comp_755 = sequentialIndex;
    }

    public net.minecraft.class_293 comp_749() {
        return this.comp_749;
    }

    public int comp_750() {
        return this.comp_750;
    }

    public int comp_751() {
        return this.comp_751;
    }

    public net.minecraft.class_293.class_5596 comp_752() {
        return this.comp_752;
    }

    public net.minecraft.class_293.class_5595 comp_753() {
        return this.comp_753;
    }

    public boolean comp_754() {
        return this.comp_754;
    }

    public boolean comp_755() {
        return this.comp_755;
    }

    /** vertexBufferSize(): vertexCount * format.getVertexSize() */
    public int method_31957() {
        return this.comp_750 * this.comp_749.method_1362();
    }

    /** vertexBufferStart() */
    public int method_43429() {
        return 0;
    }

    /** vertexBufferEnd() */
    public int method_43430() {
        return this.method_31957();
    }

    /** indexBufferStart(): 0 for an index-only buffer, else right after the vertices */
    public int method_43431() {
        return this.comp_754 ? 0 : this.method_43430();
    }

    /** indexBufferEnd() */
    public int method_43432() {
        return this.method_43431() + this.method_31961();
    }

    /** indexBufferSize(): none when the sequential buffer serves */
    public int method_31961() {
        return this.comp_755 ? 0 : this.comp_751 * this.comp_753.field_27375;
    }

    /** bufferSize() */
    public int method_31958() {
        return this.method_43432();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BufferBuilderDrawState d)) {
            return false;
        }
        return this.comp_750 == d.comp_750 && this.comp_751 == d.comp_751 && this.comp_754 == d.comp_754 && this.comp_755 == d.comp_755
            && Objects.equals(this.comp_749, d.comp_749) && this.comp_752 == d.comp_752 && this.comp_753 == d.comp_753;
    }

    @Override
    public int hashCode() {
        int h = Objects.hashCode(this.comp_749);
        h = 31 * h + Integer.hashCode(this.comp_750);
        h = 31 * h + Integer.hashCode(this.comp_751);
        h = 31 * h + Objects.hashCode(this.comp_752);
        h = 31 * h + Objects.hashCode(this.comp_753);
        h = 31 * h + Boolean.hashCode(this.comp_754);
        h = 31 * h + Boolean.hashCode(this.comp_755);
        return h;
    }

    /** the record's string as 1.20.6 printed it under intermediary names */
    @Override
    public String toString() {
        return "class_4574[comp_749=" + this.comp_749 + ", comp_750=" + this.comp_750 + ", comp_751=" + this.comp_751 + ", comp_752="
            + this.comp_752 + ", comp_753=" + this.comp_753 + ", comp_754=" + this.comp_754 + ", comp_755=" + this.comp_755 + "]";
    }
}
