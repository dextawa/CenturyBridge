package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_2540;
import net.minecraft.class_4076;

/**
 * FriendlyByteBuf.readSectionPos / writeSectionPos (class_2540.method_19456 / method_36131), which 1.21.9 removed
 * (javap: absent from the 1.21.9 class_2540; SectionPos got a STREAM_CODEC instead). 1.21.8's bodies (javap -c):
 * read is SectionPos.of(readLong()), write is writeLong(pos.asLong()) and answers the buffer. Both calls are still in
 * 1.21.9 (SectionPos.of(long) method_18677, asLong method_18694, writeLong method_52974).
 */
public final class CbSectionPosBuf {

    private CbSectionPosBuf() {
    }

    /** readSectionPos() */
    public static class_4076 method_19456(class_2540 buf) {
        return class_4076.method_18677(buf.readLong());
    }

    /** writeSectionPos(pos) */
    public static class_2540 method_36131(class_2540 buf, class_4076 pos) {
        buf.method_52974(pos.method_18694());
        return buf;
    }
}
