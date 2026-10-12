package top.dext.centurybridge.rt.v1_20_2;

import java.io.DataInput;
import java.io.IOException;

/**
 * 1.20.1 NBT reading on 1.20.2's. 1.20.2 moved nesting depth into NbtAccounter (class_2505: a
 * byte quota and a depth limit, pushDepth/popDepth around every nested read) and made the
 * accounter an argument of every read, skip and scan. 1.20.1 readers counted bytes only when
 * handed an accounter, scans and skips not at all, and checked depth themselves against the same
 * 512 that 1.20.2's accounter enforces.
 */
public final class CbNbt {

    /** NbtAccounter.UNLIMITED (1.20.1 field_11556, a shared no-limit accounter): an accounter with no byte limit */
    public static net.minecraft.class_2505 field_11556() {
        return net.minecraft.class_2505.method_53898();
    }

    /**
     * The accounter a 1.20.1 scan or skip ran without: no byte limit and no depth limit. 1.20.1's
     * TagType.skip / parse / parseRoot took no accounter and recursed without a depth argument
     * (CompoundTag.TYPE.skip, ListTag.TYPE.skip and their parse read nested types straight away); only
     * loads (load(input, depth, accounter)) checked 512 themselves. 1.20.2's unlimitedHeap() would hold
     * a scan to 512 levels, which 1.20.1's never was.
     */
    public static net.minecraft.class_2505 unlimited() {
        return new net.minecraft.class_2505(Long.MAX_VALUE, Integer.MAX_VALUE);
    }

    /** the depth limit a 1.20.1 NbtAccounter(maxBytes) subclass gets from 1.20.2's two-argument constructor */
    public static int maxDepth() {
        return 512;
    }

    /**
     * load(input, accounter), the shape 1.20.2 calls on a tag type, synthesized into a converted
     * TagType that still implements load(input, depth, accounter): the depth 1.20.1 passed down is
     * the nesting depth the accounter now tracks.
     */
    public static net.minecraft.class_2520 load(CbLegacyTagType self, DataInput input, net.minecraft.class_2505 accounter)
            throws IOException {
        return self.method_23262(input, accounter.method_53902(), accounter);
    }

    private CbNbt() {
    }
}
