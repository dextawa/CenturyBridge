package top.dext.centurybridge.step.v26_1.rt;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.linux.IOVec;
import org.lwjgl.system.linux.liburing.IOCQRingOffsets;
import org.lwjgl.system.linux.liburing.IOSQRingOffsets;
import org.lwjgl.system.linux.liburing.IOURingGeteventsArg;
import org.lwjgl.system.linux.liburing.IOURingSQE;
import org.lwjgl.system.linux.liburing.IOURingSyncCancelReg;
import org.lwjgl.system.linux.liburing.LibURing;

/**
 * LWJGL 3.3.3 (1.21.11) -> 3.4.1 (26.1), the liburing members core/tools/gen_lib_lwjgl_uring.py does not generate
 * (core/segments/damage-lib/1.21.11__26.1.tsv):
 * <ul>
 * <li>LibURing's prep functions whose offset (and fadvise's / madvise's length) parameter changed width. 3.3.3's natives
 * (objdump of liblwjgl.so from lwjgl-3.3.3-natives-linux.jar) sign-extended the int offset into the SQE's 64-bit off
 * (movslq) and stored only the low 32 bits of a long length into the SQE's 32-bit len (movd): the same values are passed
 * to 3.4.1's natives, which store a long offset and an int length as they are;</li>
 * <li>offset constants of reserved members newer kernel headers named (io_cqring_offsets / io_sqring_offsets resv2 is
 * user_addr, io_uring_getevents_arg pad is min_wait_usec: same offset and width), and IOURingSyncCancelReg's pad, which
 * 3.4.1 split into opcode + pad[7] + pad2[3] over the same 32 bytes.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbLibLwjglUringCalls {
    private CbLibLwjglUringCalls() {
    }

    // ------------------------------------------------------------------ LibURing prep functions

    /** 3.3.3 nio_uring_prep_read_fixed(sqe, fd, buf, nbytes, int offset, buf_index): offset sign-extended */
    public static void nio_uring_prep_read_fixed(long sqe, int fd, long buf, int nbytes, int offset, int bufIndex) {
        LibURing.nio_uring_prep_read_fixed(sqe, fd, buf, nbytes, (long) offset, bufIndex);
    }

    /** 3.3.3 io_uring_prep_read_fixed(sqe, fd, buf, int offset, buf_index) */
    public static void io_uring_prep_read_fixed(IOURingSQE sqe, int fd, ByteBuffer buf, int offset, int bufIndex) {
        LibURing.io_uring_prep_read_fixed(sqe, fd, buf, (long) offset, bufIndex);
    }

    /** 3.3.3 nio_uring_prep_writev(sqe, fd, iovecs, nr_vecs, int offset) */
    public static void nio_uring_prep_writev(long sqe, int fd, long iovecs, int nrVecs, int offset) {
        LibURing.nio_uring_prep_writev(sqe, fd, iovecs, nrVecs, (long) offset);
    }

    /** 3.3.3 io_uring_prep_writev(sqe, fd, iovecs, int offset) */
    public static void io_uring_prep_writev(IOURingSQE sqe, int fd, IOVec.Buffer iovecs, int offset) {
        LibURing.io_uring_prep_writev(sqe, fd, iovecs, (long) offset);
    }

    /** 3.3.3 nio_uring_prep_writev2(sqe, fd, iovecs, nr_vecs, int offset, flags) */
    public static void nio_uring_prep_writev2(long sqe, int fd, long iovecs, int nrVecs, int offset, int flags) {
        LibURing.nio_uring_prep_writev2(sqe, fd, iovecs, nrVecs, (long) offset, flags);
    }

    /** 3.3.3 io_uring_prep_writev2(sqe, fd, iovecs, int offset, flags) */
    public static void io_uring_prep_writev2(IOURingSQE sqe, int fd, IOVec.Buffer iovecs, int offset, int flags) {
        LibURing.io_uring_prep_writev2(sqe, fd, iovecs, (long) offset, flags);
    }

    /** 3.3.3 nio_uring_prep_write_fixed(sqe, fd, buf, nbytes, int offset, buf_index) */
    public static void nio_uring_prep_write_fixed(long sqe, int fd, long buf, int nbytes, int offset, int bufIndex) {
        LibURing.nio_uring_prep_write_fixed(sqe, fd, buf, nbytes, (long) offset, bufIndex);
    }

    /** 3.3.3 io_uring_prep_write_fixed(sqe, fd, buf, int offset, buf_index) */
    public static void io_uring_prep_write_fixed(IOURingSQE sqe, int fd, ByteBuffer buf, int offset, int bufIndex) {
        LibURing.io_uring_prep_write_fixed(sqe, fd, buf, (long) offset, bufIndex);
    }

    /** 3.3.3 nio_uring_prep_read(sqe, fd, buf, nbytes, int offset) */
    public static void nio_uring_prep_read(long sqe, int fd, long buf, int nbytes, int offset) {
        LibURing.nio_uring_prep_read(sqe, fd, buf, nbytes, (long) offset);
    }

    /** 3.3.3 io_uring_prep_read(sqe, fd, buf, int offset) */
    public static void io_uring_prep_read(IOURingSQE sqe, int fd, ByteBuffer buf, int offset) {
        LibURing.io_uring_prep_read(sqe, fd, buf, (long) offset);
    }

    /** 3.3.3 nio_uring_prep_write(sqe, fd, buf, nbytes, int offset) */
    public static void nio_uring_prep_write(long sqe, int fd, long buf, int nbytes, int offset) {
        LibURing.nio_uring_prep_write(sqe, fd, buf, nbytes, (long) offset);
    }

    /** 3.3.3 io_uring_prep_write(sqe, fd, buf, int offset) */
    public static void io_uring_prep_write(IOURingSQE sqe, int fd, ByteBuffer buf, int offset) {
        LibURing.io_uring_prep_write(sqe, fd, buf, (long) offset);
    }

    /** 3.3.3 nio_uring_prep_fadvise(sqe, fd, int offset, long len, advice): offset sign-extended, len's low 32 bits */
    public static void nio_uring_prep_fadvise(long sqe, int fd, int offset, long len, int advice) {
        LibURing.nio_uring_prep_fadvise(sqe, fd, (long) offset, (int) len, advice);
    }

    /** 3.3.3 io_uring_prep_fadvise(sqe, fd, int offset, long len, advice) */
    public static void io_uring_prep_fadvise(IOURingSQE sqe, int fd, int offset, long len, int advice) {
        LibURing.nio_uring_prep_fadvise(sqe.address(), fd, (long) offset, (int) len, advice);
    }

    /** 3.3.3 nio_uring_prep_madvise(sqe, addr, long length, advice): length's low 32 bits */
    public static void nio_uring_prep_madvise(long sqe, long addr, long length, int advice) {
        LibURing.nio_uring_prep_madvise(sqe, addr, (int) length, advice);
    }

    /** 3.3.3 nio_uring_prep_sync_file_range(sqe, fd, len, int offset, flags): offset sign-extended */
    public static void nio_uring_prep_sync_file_range(long sqe, int fd, int len, int offset, int flags) {
        LibURing.nio_uring_prep_sync_file_range(sqe, fd, len, (long) offset, flags);
    }

    /** 3.3.3 io_uring_prep_sync_file_range(sqe, fd, len, int offset, flags) */
    public static void io_uring_prep_sync_file_range(IOURingSQE sqe, int fd, int len, int offset, int flags) {
        LibURing.nio_uring_prep_sync_file_range(sqe.address(), fd, len, (long) offset, flags);
    }

    /**
     * 3.3.3 io_uring_enter2(fd, to_submit, min_complete, flags, long sig, long sz): sig checked non-null (Checks.check), then
     * the native, which 3.4.1 keeps with the same (void *arg, size_t sz)
     */
    public static int io_uring_enter2(int fd, int toSubmit, int minComplete, int flags, long sig, long sz) {
        if (Checks.CHECKS) {
            Checks.check(sig);
        }
        return LibURing.nio_uring_enter2(fd, toSubmit, minComplete, flags, sig, sz);
    }

    // ------------------------------------------------------------------ offsets

    /** 3.3.3 IOCQRingOffsets.RESV2: 3.4.1's USER_ADDR, the same __u64 */
    public static int IOCQRingOffsets_RESV2() {
        return IOCQRingOffsets.USER_ADDR;
    }

    /** 3.3.3 IOSQRingOffsets.RESV2: 3.4.1's USER_ADDR, the same __u64 */
    public static int IOSQRingOffsets_RESV2() {
        return IOSQRingOffsets.USER_ADDR;
    }

    /** 3.3.3 IOURingGeteventsArg.PAD: 3.4.1's MIN_WAIT_USEC, the same __u32 */
    public static int IOURingGeteventsArg_PAD() {
        return IOURingGeteventsArg.MIN_WAIT_USEC;
    }

    /**
     * 3.3.3 IOURingSyncCancelReg.PAD, the start of its __u64 pad[4]: 3.4.1's OPCODE (3.4.1's own PAD is one byte further, at
     * its __u8 pad[7], so a 1.21.11 mod reading PAD gets 3.3.3's offset)
     */
    public static int IOURingSyncCancelReg_PAD() {
        return IOURingSyncCancelReg.OPCODE;
    }

    // ------------------------------------------------------------------ IOURingSyncCancelReg pad[4] (3.4.1: the same 32 bytes)

    /** 3.3.3 npad(struct): the four longs */
    public static LongBuffer npad(long struct) {
        return MemoryUtil.memLongBuffer(struct + IOURingSyncCancelReg.OPCODE, 4);
    }

    /** 3.3.3 npad(struct, index) */
    public static long npad(long struct, int index) {
        return MemoryUtil.memGetLong(struct + IOURingSyncCancelReg.OPCODE + Checks.check(index, 4) * 8L);
    }

    /** 3.3.3 npad(struct, value): at most four longs copied in */
    public static void npad(long struct, LongBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 4);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + IOURingSyncCancelReg.OPCODE, value.remaining() * 8L);
    }

    /** 3.3.3 npad(struct, index, value) */
    public static void npad(long struct, int index, long value) {
        MemoryUtil.memPutLong(struct + IOURingSyncCancelReg.OPCODE + Checks.check(index, 4) * 8L, value);
    }
}
