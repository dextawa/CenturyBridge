/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * LWJGL 3.3.2's org.lwjgl.system.linux.liburing.CbLibUringNotificationRegister (3.3.3 dropped it), ported to 3.3.3's Struct by
 * core/tools/port_uring.py: same layout and members; 3.3.2's wrap factories are 3.3.3's constructors.
 */
package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.system.*;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct io_uring_notification_register {
 *     __u32 nr_slots;
 *     __u32 resv;
 *     __u64 resv2;
 *     __u64 data;
 *     __u64 resv3;
 * }</code></pre>
 */
@NativeType("struct io_uring_notification_register")
@Environment(EnvType.CLIENT)
public class CbLibUringNotificationRegister extends Struct<CbLibUringNotificationRegister> implements NativeResource {

    /**
     * LWJGL 3.3.3's Pointer.Default.UNSAFE, which this copy of 3.3.3's struct read as an inherited field; LWJGL 3.4.1
     * (26.1) dropped it. The same JDK singleton, held here (as CbLibLwjgl holds it).
     */
    private static final sun.misc.Unsafe UNSAFE;

    static {
        try {
            java.lang.reflect.Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (sun.misc.Unsafe) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        NR_SLOTS,
        RESV,
        RESV2,
        DATA,
        RESV3;

    static {
        Layout layout = __struct(
            __member(4),
            __member(4),
            __member(8),
            __member(8),
            __member(8)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        NR_SLOTS = layout.offsetof(0);
        RESV = layout.offsetof(1);
        RESV2 = layout.offsetof(2);
        DATA = layout.offsetof(3);
        RESV3 = layout.offsetof(4);
    }

    /**
     * Creates a {@code CbLibUringNotificationRegister} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public CbLibUringNotificationRegister(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    protected CbLibUringNotificationRegister(long address, ByteBuffer container) {
        super(address, container);
    }

    /** 3.3.2's StructBuffer made its elements as Struct.wrap did: instances of the factory's own class */
    @Override
    protected CbLibUringNotificationRegister create(long address, ByteBuffer container) {
        return (CbLibUringNotificationRegister) CbLibLwjgl.create(this, address, container);
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** @return the value of the {@code nr_slots} field. */
    @NativeType("__u32")
    public int nr_slots() { return nnr_slots(address()); }
    /** @return the value of the {@code resv} field. */
    @NativeType("__u32")
    public int resv() { return nresv(address()); }
    /** @return the value of the {@code resv2} field. */
    @NativeType("__u64")
    public long resv2() { return nresv2(address()); }
    /** @return the value of the {@code data} field. */
    @NativeType("__u64")
    public long data() { return ndata(address()); }
    /** @return the value of the {@code resv3} field. */
    @NativeType("__u64")
    public long resv3() { return nresv3(address()); }

    /** Sets the specified value to the {@code nr_slots} field. */
    public CbLibUringNotificationRegister nr_slots(@NativeType("__u32") int value) { nnr_slots(address(), value); return this; }
    /** Sets the specified value to the {@code resv} field. */
    public CbLibUringNotificationRegister resv(@NativeType("__u32") int value) { nresv(address(), value); return this; }
    /** Sets the specified value to the {@code resv2} field. */
    public CbLibUringNotificationRegister resv2(@NativeType("__u64") long value) { nresv2(address(), value); return this; }
    /** Sets the specified value to the {@code data} field. */
    public CbLibUringNotificationRegister data(@NativeType("__u64") long value) { ndata(address(), value); return this; }
    /** Sets the specified value to the {@code resv3} field. */
    public CbLibUringNotificationRegister resv3(@NativeType("__u64") long value) { nresv3(address(), value); return this; }

    /** Initializes this struct with the specified values. */
    public CbLibUringNotificationRegister set(
        int nr_slots,
        int resv,
        long resv2,
        long data,
        long resv3
    ) {
        nr_slots(nr_slots);
        resv(resv);
        resv2(resv2);
        data(data);
        resv3(resv3);

        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     *
     * @return this struct
     */
    public CbLibUringNotificationRegister set(CbLibUringNotificationRegister src) {
        memCopy(src.address(), address(), SIZEOF);
        return this;
    }

    // -----------------------------------

    /** Returns a new {@code CbLibUringNotificationRegister} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static CbLibUringNotificationRegister malloc() {
        return new CbLibUringNotificationRegister(nmemAllocChecked(SIZEOF), null);
    }

    /** Returns a new {@code CbLibUringNotificationRegister} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static CbLibUringNotificationRegister calloc() {
        return new CbLibUringNotificationRegister(nmemCallocChecked(1, SIZEOF), null);
    }

    /** Returns a new {@code CbLibUringNotificationRegister} instance allocated with {@link BufferUtils}. */
    public static CbLibUringNotificationRegister create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new CbLibUringNotificationRegister(memAddress(container), container);
    }

    /** Returns a new {@code CbLibUringNotificationRegister} instance for the specified memory address. */
    public static CbLibUringNotificationRegister create(long address) {
        return new CbLibUringNotificationRegister(address, null);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    public static CbLibUringNotificationRegister createSafe(long address) {
        return address == NULL ? null : new CbLibUringNotificationRegister(address, null);
    }

    /**
     * Returns a new {@link CbLibUringNotificationRegister.Buffer} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationRegister.Buffer malloc(int capacity) {
        return new Buffer(nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
    }

    /**
     * Returns a new {@link CbLibUringNotificationRegister.Buffer} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationRegister.Buffer calloc(int capacity) {
        return new Buffer(nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    /**
     * Returns a new {@link CbLibUringNotificationRegister.Buffer} instance allocated with {@link BufferUtils}.
     *
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationRegister.Buffer create(int capacity) {
        ByteBuffer container = __create(capacity, SIZEOF);
        return new Buffer(memAddress(container), container, -1, 0, capacity, capacity);
    }

    /**
     * Create a {@link CbLibUringNotificationRegister.Buffer} instance at the specified memory.
     *
     * @param address  the memory address
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationRegister.Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    /** Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    public static CbLibUringNotificationRegister.Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : new Buffer(address, capacity);
    }

    /**
     * Returns a new {@code CbLibUringNotificationRegister} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static CbLibUringNotificationRegister malloc(MemoryStack stack) {
        return new CbLibUringNotificationRegister(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code CbLibUringNotificationRegister} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static CbLibUringNotificationRegister calloc(MemoryStack stack) {
        return new CbLibUringNotificationRegister(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    /**
     * Returns a new {@link CbLibUringNotificationRegister.Buffer} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationRegister.Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    /**
     * Returns a new {@link CbLibUringNotificationRegister.Buffer} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static CbLibUringNotificationRegister.Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    // -----------------------------------

    /** Unsafe version of {@link #nr_slots}. */
    public static int nnr_slots(long struct) { return UNSAFE.getInt(null, struct + CbLibUringNotificationRegister.NR_SLOTS); }
    /** Unsafe version of {@link #resv}. */
    public static int nresv(long struct) { return UNSAFE.getInt(null, struct + CbLibUringNotificationRegister.RESV); }
    /** Unsafe version of {@link #resv2}. */
    public static long nresv2(long struct) { return UNSAFE.getLong(null, struct + CbLibUringNotificationRegister.RESV2); }
    /** Unsafe version of {@link #data}. */
    public static long ndata(long struct) { return UNSAFE.getLong(null, struct + CbLibUringNotificationRegister.DATA); }
    /** Unsafe version of {@link #resv3}. */
    public static long nresv3(long struct) { return UNSAFE.getLong(null, struct + CbLibUringNotificationRegister.RESV3); }

    /** Unsafe version of {@link #nr_slots(int) nr_slots}. */
    public static void nnr_slots(long struct, int value) { UNSAFE.putInt(null, struct + CbLibUringNotificationRegister.NR_SLOTS, value); }
    /** Unsafe version of {@link #resv(int) resv}. */
    public static void nresv(long struct, int value) { UNSAFE.putInt(null, struct + CbLibUringNotificationRegister.RESV, value); }
    /** Unsafe version of {@link #resv2(long) resv2}. */
    public static void nresv2(long struct, long value) { UNSAFE.putLong(null, struct + CbLibUringNotificationRegister.RESV2, value); }
    /** Unsafe version of {@link #data(long) data}. */
    public static void ndata(long struct, long value) { UNSAFE.putLong(null, struct + CbLibUringNotificationRegister.DATA, value); }
    /** Unsafe version of {@link #resv3(long) resv3}. */
    public static void nresv3(long struct, long value) { UNSAFE.putLong(null, struct + CbLibUringNotificationRegister.RESV3, value); }

    // -----------------------------------

    /** An array of {@link CbLibUringNotificationRegister} structs. */
    @Environment(EnvType.CLIENT)
    public static class Buffer extends StructBuffer<CbLibUringNotificationRegister, Buffer> implements NativeResource {

        private static final CbLibUringNotificationRegister ELEMENT_FACTORY = CbLibUringNotificationRegister.create(-1L);

        /**
         * Creates a new {@code CbLibUringNotificationRegister.Buffer} instance backed by the specified container.
         *
         * Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link CbLibUringNotificationRegister#SIZEOF}, and its mark will be undefined.
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
        protected CbLibUringNotificationRegister getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** @return the value of the {@code nr_slots} field. */
        @NativeType("__u32")
        public int nr_slots() { return CbLibUringNotificationRegister.nnr_slots(address()); }
        /** @return the value of the {@code resv} field. */
        @NativeType("__u32")
        public int resv() { return CbLibUringNotificationRegister.nresv(address()); }
        /** @return the value of the {@code resv2} field. */
        @NativeType("__u64")
        public long resv2() { return CbLibUringNotificationRegister.nresv2(address()); }
        /** @return the value of the {@code data} field. */
        @NativeType("__u64")
        public long data() { return CbLibUringNotificationRegister.ndata(address()); }
        /** @return the value of the {@code resv3} field. */
        @NativeType("__u64")
        public long resv3() { return CbLibUringNotificationRegister.nresv3(address()); }

        /** Sets the specified value to the {@code nr_slots} field. */
        public CbLibUringNotificationRegister.Buffer nr_slots(@NativeType("__u32") int value) { CbLibUringNotificationRegister.nnr_slots(address(), value); return this; }
        /** Sets the specified value to the {@code resv} field. */
        public CbLibUringNotificationRegister.Buffer resv(@NativeType("__u32") int value) { CbLibUringNotificationRegister.nresv(address(), value); return this; }
        /** Sets the specified value to the {@code resv2} field. */
        public CbLibUringNotificationRegister.Buffer resv2(@NativeType("__u64") long value) { CbLibUringNotificationRegister.nresv2(address(), value); return this; }
        /** Sets the specified value to the {@code data} field. */
        public CbLibUringNotificationRegister.Buffer data(@NativeType("__u64") long value) { CbLibUringNotificationRegister.ndata(address(), value); return this; }
        /** Sets the specified value to the {@code resv3} field. */
        public CbLibUringNotificationRegister.Buffer resv3(@NativeType("__u64") long value) { CbLibUringNotificationRegister.nresv3(address(), value); return this; }

    }

}