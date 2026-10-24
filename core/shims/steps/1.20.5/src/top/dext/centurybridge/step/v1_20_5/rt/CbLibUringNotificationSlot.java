/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * LWJGL 3.3.2's org.lwjgl.system.linux.liburing.CbLibUringNotificationSlot (3.3.3 dropped it), ported to 3.3.3's Struct by
 * core/tools/port_uring.py: same layout and members; 3.3.2's wrap factories are 3.3.3's constructors.
 */
package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.system.*;

import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct io_uring_notification_slot {
 *     __u64 tag;
 *     __u64 resv[3];
 * }</code></pre>
 */
@NativeType("struct io_uring_notification_slot")
@Environment(EnvType.CLIENT)
public class CbLibUringNotificationSlot extends Struct<CbLibUringNotificationSlot> implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        TAG,
        RESV;

    static {
        Layout layout = __struct(
            __member(8),
            __array(8, 3)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        TAG = layout.offsetof(0);
        RESV = layout.offsetof(1);
    }

    /**
     * Creates a {@code CbLibUringNotificationSlot} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public CbLibUringNotificationSlot(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    protected CbLibUringNotificationSlot(long address, ByteBuffer container) {
        super(address, container);
    }

    /** 3.3.2's StructBuffer made its elements as Struct.wrap did: instances of the factory's own class */
    @Override
    protected CbLibUringNotificationSlot create(long address, ByteBuffer container) {
        return (CbLibUringNotificationSlot) CbLibLwjgl.create(this, address, container);
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** @return the value of the {@code tag} field. */
    @NativeType("__u64")
    public long tag() { return ntag(address()); }
    /** @return a {@link LongBuffer} view of the {@code resv} field. */
    @NativeType("__u64[3]")
    public LongBuffer resv() { return nresv(address()); }
    /** @return the value at the specified index of the {@code resv} field. */
    @NativeType("__u64")
    public long resv(int index) { return nresv(address(), index); }

    /** Sets the specified value to the {@code tag} field. */
    public CbLibUringNotificationSlot tag(@NativeType("__u64") long value) { ntag(address(), value); return this; }
    /** Copies the specified {@link LongBuffer} to the {@code resv} field. */
    public CbLibUringNotificationSlot resv(@NativeType("__u64[3]") LongBuffer value) { nresv(address(), value); return this; }
    /** Sets the specified value at the specified index of the {@code resv} field. */
    public CbLibUringNotificationSlot resv(int index, @NativeType("__u64") long value) { nresv(address(), index, value); return this; }

    /** Initializes this struct with the specified values. */
    public CbLibUringNotificationSlot set(
        long tag,
        LongBuffer resv
    ) {
        tag(tag);
        resv(resv);

        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     *
     * @return this struct
     */
    public CbLibUringNotificationSlot set(CbLibUringNotificationSlot src) {
        memCopy(src.address(), address(), SIZEOF);
        return this;
    }

    // -----------------------------------

    /** Returns a new {@code CbLibUringNotificationSlot} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static CbLibUringNotificationSlot malloc() {
        return new CbLibUringNotificationSlot(nmemAllocChecked(SIZEOF), null);
    }

    /** Returns a new {@code CbLibUringNotificationSlot} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static CbLibUringNotificationSlot calloc() {
        return new CbLibUringNotificationSlot(nmemCallocChecked(1, SIZEOF), null);
    }

    /** Returns a new {@code CbLibUringNotificationSlot} instance allocated with {@link BufferUtils}. */
    public static CbLibUringNotificationSlot create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new CbLibUringNotificationSlot(memAddress(container), container);
    }

    /** Returns a new {@code CbLibUringNotificationSlot} instance for the specified memory address. */
    public static CbLibUringNotificationSlot create(long address) {
        return new CbLibUringNotificationSlot(address, null);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    public static CbLibUringNotificationSlot createSafe(long address) {
        return address == NULL ? null : new CbLibUringNotificationSlot(address, null);
    }

    /**
     * Returns a new {@link CbLibUringNotificationSlot.Buffer} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationSlot.Buffer malloc(int capacity) {
        return new Buffer(nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
    }

    /**
     * Returns a new {@link CbLibUringNotificationSlot.Buffer} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationSlot.Buffer calloc(int capacity) {
        return new Buffer(nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    /**
     * Returns a new {@link CbLibUringNotificationSlot.Buffer} instance allocated with {@link BufferUtils}.
     *
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationSlot.Buffer create(int capacity) {
        ByteBuffer container = __create(capacity, SIZEOF);
        return new Buffer(memAddress(container), container, -1, 0, capacity, capacity);
    }

    /**
     * Create a {@link CbLibUringNotificationSlot.Buffer} instance at the specified memory.
     *
     * @param address  the memory address
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationSlot.Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    /** Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    public static CbLibUringNotificationSlot.Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : new Buffer(address, capacity);
    }

    /**
     * Returns a new {@code CbLibUringNotificationSlot} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static CbLibUringNotificationSlot malloc(MemoryStack stack) {
        return new CbLibUringNotificationSlot(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code CbLibUringNotificationSlot} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static CbLibUringNotificationSlot calloc(MemoryStack stack) {
        return new CbLibUringNotificationSlot(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    /**
     * Returns a new {@link CbLibUringNotificationSlot.Buffer} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationSlot.Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    /**
     * Returns a new {@link CbLibUringNotificationSlot.Buffer} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationSlot.Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    // -----------------------------------

    /** Unsafe version of {@link #tag}. */
    public static long ntag(long struct) { return UNSAFE.getLong(null, struct + CbLibUringNotificationSlot.TAG); }
    /** Unsafe version of {@link #resv}. */
    public static LongBuffer nresv(long struct) { return memLongBuffer(struct + CbLibUringNotificationSlot.RESV, 3); }
    /** Unsafe version of {@link #resv(int) resv}. */
    public static long nresv(long struct, int index) {
        return UNSAFE.getLong(null, struct + CbLibUringNotificationSlot.RESV + check(index, 3) * 8);
    }

    /** Unsafe version of {@link #tag(long) tag}. */
    public static void ntag(long struct, long value) { UNSAFE.putLong(null, struct + CbLibUringNotificationSlot.TAG, value); }
    /** Unsafe version of {@link #resv(LongBuffer) resv}. */
    public static void nresv(long struct, LongBuffer value) {
        if (CHECKS) { checkGT(value, 3); }
        memCopy(memAddress(value), struct + CbLibUringNotificationSlot.RESV, value.remaining() * 8);
    }
    /** Unsafe version of {@link #resv(int, long) resv}. */
    public static void nresv(long struct, int index, long value) {
        UNSAFE.putLong(null, struct + CbLibUringNotificationSlot.RESV + check(index, 3) * 8, value);
    }

    // -----------------------------------

    /** An array of {@link CbLibUringNotificationSlot} structs. */
    @Environment(EnvType.CLIENT)
    public static class Buffer extends StructBuffer<CbLibUringNotificationSlot, Buffer> implements NativeResource {

        private static final CbLibUringNotificationSlot ELEMENT_FACTORY = CbLibUringNotificationSlot.create(-1L);

        /**
         * Creates a new {@code CbLibUringNotificationSlot.Buffer} instance backed by the specified container.
         *
         * Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link CbLibUringNotificationSlot#SIZEOF}, and its mark will be undefined.
         *
         * <p>The created buffer instance holds a strong reference to the container object.</p>
         */
        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected CbLibUringNotificationSlot getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** @return the value of the {@code tag} field. */
        @NativeType("__u64")
        public long tag() { return CbLibUringNotificationSlot.ntag(address()); }
        /** @return a {@link LongBuffer} view of the {@code resv} field. */
        @NativeType("__u64[3]")
        public LongBuffer resv() { return CbLibUringNotificationSlot.nresv(address()); }
        /** @return the value at the specified index of the {@code resv} field. */
        @NativeType("__u64")
        public long resv(int index) { return CbLibUringNotificationSlot.nresv(address(), index); }

        /** Sets the specified value to the {@code tag} field. */
        public CbLibUringNotificationSlot.Buffer tag(@NativeType("__u64") long value) { CbLibUringNotificationSlot.ntag(address(), value); return this; }
        /** Copies the specified {@link LongBuffer} to the {@code resv} field. */
        public CbLibUringNotificationSlot.Buffer resv(@NativeType("__u64[3]") LongBuffer value) { CbLibUringNotificationSlot.nresv(address(), value); return this; }
        /** Sets the specified value at the specified index of the {@code resv} field. */
        public CbLibUringNotificationSlot.Buffer resv(int index, @NativeType("__u64") long value) { CbLibUringNotificationSlot.nresv(address(), index, value); return this; }

    }

}