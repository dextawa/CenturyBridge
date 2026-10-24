package top.dext.centurybridge.step.v26_1.facade;

/**
 * 1.21.11's com.mojang.blaze3d.vertex.VertexFormatElement.Usage, gone in 26.1, whose elements say instead whether
 * the GPU normalizes them. Same constants, order and names; 1.21.11's GL setup normalized NORMAL and COLOR
 * elements and no others (rt/CbWallVertexFormats).
 */
public enum VertexFormatElement$Usage {
    POSITION("Position"),
    NORMAL("Normal"),
    COLOR("Vertex Color"),
    UV("UV"),
    GENERIC("Generic");

    public final String name;

    VertexFormatElement$Usage(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
