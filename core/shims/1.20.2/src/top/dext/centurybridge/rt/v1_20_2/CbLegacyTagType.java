package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 TagType.load(input, depth, accounter) shape (1.20.2: load(input, accounter), depth
 * kept by the accounter). A converted TagType implementation gets this interface added and the
 * 1.20.2 shape synthesized to call it (overrideHooks bridge, CbNbt.load).
 */
public interface CbLegacyTagType {

    /** load(input, depth, accounter) */
    net.minecraft.class_2520 method_23262(java.io.DataInput input, int depth, net.minecraft.class_2505 accounter)
        throws java.io.IOException;
}
