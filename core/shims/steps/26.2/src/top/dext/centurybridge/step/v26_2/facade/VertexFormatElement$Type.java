package top.dext.centurybridge.step.v26_2.facade;

/**
 * 26.1.2's com.mojang.blaze3d.vertex.VertexFormatElement.Type, gone in 26.2, whose elements carry a GpuFormat instead.
 * Same constants, order, sizes and names (javap -c 26.1.2 VertexFormatElement$Type.&lt;clinit&gt;); 26.1.2's
 * GlConst.toGl(Type) answers each constant's GL type (rt/CbS205fBlaze3d#toGl). classRenames in s205f-blaze3d.json send
 * references here.
 */
public enum VertexFormatElement$Type {
    FLOAT(4, "Float"),
    UBYTE(1, "Unsigned Byte"),
    BYTE(1, "Byte"),
    USHORT(2, "Unsigned Short"),
    SHORT(2, "Short"),
    UINT(4, "Unsigned Int"),
    INT(4, "Int");

    private final int size;
    private final String name;

    VertexFormatElement$Type(int size, String name) {
        this.size = size;
        this.name = name;
    }

    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
