package top.dext.centurybridge.step.v1_21_6.rt;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.GpuDevice;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The 1.21.5 uniform-array view of a 1.21.6 GlRenderPass.
 *
 * <p>1.21.5 stored the caller's actual int[] / float[] object and read it when a dirty uniform was set up for a draw.
 * 1.21.6 stores only GpuBufferSlice(buffer, offset, length), which has no primitive-array kind. This class therefore
 * keeps the original array and its native slice together for every value introduced through the legacy view. An array
 * is never replaced by a snapshot: in-place edits are uploaded before native draw setup, and map reads return that same
 * object. Raw float bits are packed explicitly, so NaN payloads survive the array -> slice -> array round trip.
 *
 * <p>A native replacement under a known legacy key is read back into the same-kind array (retaining its identity when
 * its length is unchanged). A wholly unknown native slice has no lossless legacy array type; reading it through the
 * legacy view fails explicitly instead of returning the slice for a later ClassCastException or guessing int versus
 * float. Native-only consumers continue to use the underlying native map and never see this view or its arrays.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbS6GlUniforms {

    private static final int BUFFER_USAGE = GpuBuffer.USAGE_MAP_READ | GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_UNIFORM;

    /* Values hold the native map and only a weak device reference, so this cache cannot retain a closed pass. */
    private static final Map<net.minecraft.class_10866, HashMap<String, Object>> VIEWS = new WeakHashMap<>();

    private CbS6GlUniforms() {
    }

    /** GlRenderPass.uniforms (field_57873), as the live int[] / float[] map 1.21.5 exposed. */
    public static HashMap<String, Object> field_57873(net.minecraft.class_10866 pass) {
        synchronized (VIEWS) {
            return VIEWS.computeIfAbsent(pass,
                p -> new UniformMapView(p.field_57873, com.mojang.blaze3d.systems.RenderSystem.getDevice()));
        }
    }

    /** The same conversion with an explicit device, for the no-GL memory-buffer harness. */
    public static HashMap<String, Object> uniformMap(HashMap<String, GpuBufferSlice> nativeMap, GpuDevice device) {
        return nativeMap == null ? null : new UniformMapView(nativeMap, Objects.requireNonNull(device));
    }

    /** Uploads changed legacy arrays at the head of GlCommandEncoder.trySetup, before it binds any uniform slice. */
    public static void flush(net.minecraft.class_10866 pass) {
        HashMap<String, Object> view;
        synchronized (VIEWS) {
            view = VIEWS.get(pass);
        }
        if (view != null) {
            flush(view);
        }
    }

    /** Flushes one legacy view; also used by the no-GL harness to exercise the exact draw-boundary operation. */
    public static void flush(HashMap<String, Object> legacyMap) {
        if (legacyMap instanceof UniformMapView view) {
            view.flush();
        }
    }

    /** Releases buffers owned by a closed pass without discarding its legacy array objects or map metadata. */
    public static void closed(net.minecraft.class_10866 pass) {
        HashMap<String, Object> view;
        synchronized (VIEWS) {
            view = VIEWS.get(pass);
        }
        if (view instanceof UniformMapView uniforms) {
            uniforms.closeBuffers();
        }
    }

    private static byte[] encode(Object array) {
        byte[] bytes;
        if (array instanceof int[] ints) {
            ByteBuffer data = ByteBuffer.allocate(Math.multiplyExact(ints.length, 4)).order(ByteOrder.nativeOrder());
            for (int value : ints) {
                data.putInt(value);
            }
            bytes = data.array();
        } else if (array instanceof float[] floats) {
            ByteBuffer data = ByteBuffer.allocate(Math.multiplyExact(floats.length, 4)).order(ByteOrder.nativeOrder());
            for (float value : floats) {
                data.putInt(Float.floatToRawIntBits(value));
            }
            bytes = data.array();
        } else {
            throw new ClassCastException("1.21.5 uniform values must be int[] or float[], got "
                + (array == null ? "null" : array.getClass().getTypeName()));
        }
        return bytes;
    }

    private static void decode(Object array, byte[] bytes) {
        ByteBuffer data = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());
        if (array instanceof int[] ints) {
            if (bytes.length != ints.length * 4) {
                throw new IllegalArgumentException("Native uniform byte length does not match its int[]");
            }
            for (int i = 0; i < ints.length; i++) {
                ints[i] = data.getInt();
            }
        } else if (array instanceof float[] floats) {
            if (bytes.length != floats.length * 4) {
                throw new IllegalArgumentException("Native uniform byte length does not match its float[]");
            }
            for (int i = 0; i < floats.length; i++) {
                floats[i] = Float.intBitsToFloat(data.getInt());
            }
        } else {
            throw new ClassCastException("Not a legacy uniform array: " + array);
        }
    }

    private static ByteBuffer direct(byte[] bytes) {
        return ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder()).put(bytes).flip();
    }

    private static IllegalStateException noLegacyType(String key, GpuBufferSlice slice) {
        return new IllegalStateException("Native uniform '" + key + "' is " + slice
            + "; it has no legacy int[]/float[] provenance, so its primitive array type cannot be recovered losslessly");
    }

    private static final class UniformMapView extends HashMap<String, Object> {
        private final HashMap<String, GpuBufferSlice> nativeMap;
        private final WeakReference<GpuDevice> deviceRef;
        private final HashMap<String, UniformValue> uniforms = new HashMap<>();
        private final Set<Map.Entry<String, Object>> entries = new EntrySet();
        private final Set<String> keys = new KeySet();
        private final Collection<Object> values = new Values();

        UniformMapView(HashMap<String, GpuBufferSlice> nativeMap, GpuDevice device) {
            this.nativeMap = nativeMap;
            this.deviceRef = new WeakReference<>(device);
        }

        private GpuDevice device() {
            GpuDevice device = deviceRef.get();
            if (device == null) {
                throw new IllegalStateException("The GPU device for this legacy uniform view is no longer available");
            }
            return device;
        }

        private GpuBuffer createBuffer(byte[] bytes) {
            // W rejects an empty source and a zero-sized buffer; V can nevertheless retain an empty array. A
            // zero-length slice over the smallest word-sized backing buffer represents it without inventing an element.
            return bytes.length == 0
                ? device().createBuffer(() -> "CenturyBridge 1.21.5 empty uniform array", BUFFER_USAGE, 4)
                : device().createBuffer(() -> "CenturyBridge 1.21.5 uniform array", BUFFER_USAGE, direct(bytes));
        }

        private byte[] readSlice(GpuBufferSlice slice) {
            if (slice.length() == 0) {
                return new byte[0];
            }
            if (slice.buffer().isClosed()) {
                throw new IllegalStateException("Cannot read a closed native uniform buffer as a legacy array");
            }
            if ((slice.buffer().usage() & GpuBuffer.USAGE_MAP_READ) == 0) {
                throw new IllegalStateException("Native uniform buffer is not readable; its legacy array value cannot be recovered");
            }
            GpuBuffer.MappedView mapped;
            if ((Object) device() instanceof net.minecraft.class_10865 gl
                    && (Object) slice.buffer() instanceof net.minecraft.class_10859 glBuffer) {
                // CommandEncoder.mapBuffer rejects an open render pass. This is the same map operation it delegates to,
                // without that command-queue check; V permits array reads while its pass is open.
                mapped = gl.method_71032().method_71016(gl.method_68401(), glBuffer,
                    slice.offset(), slice.length(), 1);
            } else {
                mapped = device().createCommandEncoder().mapBuffer(slice, true, false);
            }
            try (mapped) {
                ByteBuffer data = mapped.data().duplicate();
                if (data.remaining() != slice.length()) {
                    throw new IllegalStateException("Mapped native uniform length " + data.remaining()
                        + " differs from slice length " + slice.length());
                }
                byte[] bytes = new byte[data.remaining()];
                data.get(bytes);
                return bytes;
            }
        }

        private void writeSlice(GpuBufferSlice slice, byte[] bytes) {
            if (bytes.length == 0) {
                return;
            }
            if ((Object) device() instanceof net.minecraft.class_10865 gl
                    && (Object) slice.buffer() instanceof net.minecraft.class_10859 glBuffer) {
                // The encoder rejects writes during an open pass; its underlying storage operation does not. This is
                // the writeToBuffer body after its closed / usage / bounds checks (already checked by UniformValue).
                gl.method_68401().method_71027(glBuffer.field_57842, slice.offset(), direct(bytes));
            } else {
                device().createCommandEncoder().writeToBuffer(slice, direct(bytes));
            }
        }

        private Object legacy(String key) {
            GpuBufferSlice slice = nativeMap.get(key);
            UniformValue uniform = uniforms.get(key);
            if (slice == null) {
                if (uniform != null) {
                    uniforms.remove(key);
                    uniform.close();
                }
                return null;
            }
            if (uniform == null) {
                throw noLegacyType(key, slice);
            }
            if (uniform.slice != slice) {
                uniform = adoptNative(key, uniform, slice);
            }
            uniform.readNativeIfClean();
            return uniform.array;
        }

        /** A native replacement keeps the known kind; only a length change can require a different array object. */
        private UniformValue adoptNative(String key, UniformValue previous, GpuBufferSlice slice) {
            byte[] bytes = readSlice(slice);
            if ((bytes.length & 3) != 0) {
                throw new IllegalStateException("Native uniform '" + key + "' has " + bytes.length
                    + " bytes, not a whole number of 32-bit legacy array elements");
            }
            Object array = previous.array;
            int oldLength = array instanceof int[] ints ? ints.length : ((float[]) array).length;
            if (oldLength != bytes.length / 4) {
                array = array instanceof int[] ? new int[bytes.length / 4] : new float[bytes.length / 4];
            }
            decode(array, bytes);
            boolean sameOwnedBuffer = previous.owned && previous.slice.buffer() == slice.buffer();
            UniformValue adopted = new UniformValue(key, array, slice, sameOwnedBuffer, bytes);
            uniforms.put(key, adopted);
            if (sameOwnedBuffer) {
                previous.owned = false; // Ownership moves to the adopted slice of the same buffer.
            } else {
                previous.close();
            }
            return adopted;
        }

        private void discard(String key) {
            UniformValue previous = uniforms.remove(key);
            if (previous != null) {
                previous.close();
            }
        }

        @Override
        public int size() {
            return nativeMap.size();
        }

        @Override
        public boolean isEmpty() {
            return nativeMap.isEmpty();
        }

        @Override
        public Object get(Object key) {
            return key == null || key instanceof String ? legacy((String) key) : null;
        }

        @Override
        public Object getOrDefault(Object key, Object fallback) {
            Object value = get(key);
            return value != null || nativeMap.containsKey(key) ? value : fallback;
        }

        @Override
        public boolean containsKey(Object key) {
            return nativeMap.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            for (String key : nativeMap.keySet()) {
                if (Objects.equals(legacy(key), value)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Object put(String key, Object value) {
            Object previous = get(key);
            if (value == null || value instanceof GpuBufferSlice) {
                nativeMap.put(key, (GpuBufferSlice) value);
                discard(key);
                return previous;
            }
            byte[] bytes = encode(value);
            GpuBuffer buffer = createBuffer(bytes);
            GpuBufferSlice slice = buffer.slice(0, bytes.length);
            nativeMap.put(key, slice);
            UniformValue old = uniforms.put(key, new UniformValue(key, value, slice, true, bytes));
            if (old != null) {
                old.close();
            }
            return previous;
        }

        @Override
        public Object putIfAbsent(String key, Object value) {
            Object previous = get(key);
            if (previous == null) {
                put(key, value);
            }
            return previous;
        }

        @Override
        public void putAll(Map<? extends String, ?> map) {
            for (Map.Entry<? extends String, ?> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public Object remove(Object key) {
            Object previous = get(key);
            if (key == null || key instanceof String) {
                nativeMap.remove(key);
                discard((String) key);
            }
            return previous;
        }

        @Override
        public boolean remove(Object key, Object value) {
            if (!nativeMap.containsKey(key) || !Objects.equals(get(key), value)) {
                return false;
            }
            remove(key);
            return true;
        }

        @Override
        public boolean replace(String key, Object expected, Object value) {
            if (!nativeMap.containsKey(key) || !Objects.equals(get(key), expected)) {
                return false;
            }
            put(key, value);
            return true;
        }

        @Override
        public Object replace(String key, Object value) {
            return nativeMap.containsKey(key) ? put(key, value) : null;
        }

        @Override
        public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remapping) {
            Objects.requireNonNull(remapping);
            Objects.requireNonNull(value);
            Object previous = get(key);
            Object next = previous == null ? value : remapping.apply(previous, value);
            if (next == null) {
                remove(key);
            } else {
                put(key, next);
            }
            return next;
        }

        @Override
        public Object computeIfAbsent(String key, Function<? super String, ?> mapping) {
            Objects.requireNonNull(mapping);
            Object previous = get(key);
            if (previous != null) {
                return previous;
            }
            Object next = mapping.apply(key);
            if (next != null) {
                put(key, next);
            }
            return next;
        }

        @Override
        public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remapping) {
            Objects.requireNonNull(remapping);
            Object previous = get(key);
            if (previous == null) {
                return null;
            }
            Object next = remapping.apply(key, previous);
            if (next == null) {
                remove(key);
            } else {
                put(key, next);
            }
            return next;
        }

        @Override
        public Object compute(String key, BiFunction<? super String, ? super Object, ?> remapping) {
            Objects.requireNonNull(remapping);
            Object next = remapping.apply(key, get(key));
            if (next == null) {
                remove(key);
            } else {
                put(key, next);
            }
            return next;
        }

        @Override
        public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
            Objects.requireNonNull(function);
            for (Map.Entry<String, Object> entry : entrySet()) {
                entry.setValue(function.apply(entry.getKey(), entry.getValue()));
            }
        }

        @Override
        public void forEach(BiConsumer<? super String, ? super Object> action) {
            Objects.requireNonNull(action);
            for (String key : nativeMap.keySet()) {
                action.accept(key, legacy(key));
            }
        }

        @Override
        public void clear() {
            nativeMap.clear();
            for (UniformValue uniform : uniforms.values()) {
                uniform.close();
            }
            uniforms.clear();
        }

        @Override
        public Set<String> keySet() {
            return keys;
        }

        @Override
        public Collection<Object> values() {
            return values;
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            return entries;
        }

        @Override
        public Object clone() {
            return new HashMap<String, Object>(this);
        }

        void flush() {
            for (Map.Entry<String, UniformValue> entry : uniforms.entrySet()) {
                String key = entry.getKey();
                UniformValue uniform = entry.getValue();
                if (nativeMap.get(key) == uniform.slice) {
                    uniform.writeIfDirty();
                }
            }
        }

        void closeBuffers() {
            for (UniformValue uniform : uniforms.values()) {
                uniform.close();
            }
        }

        private final class UniformValue {
            private final String key;
            private final Object array;
            private GpuBufferSlice slice;
            private boolean owned;
            private byte[] legacyBytes;
            private byte[] nativeBytes;

            UniformValue(String key, Object array, GpuBufferSlice slice, boolean owned, byte[] bytes) {
                this.key = key;
                this.array = array;
                this.slice = slice;
                this.owned = owned;
                this.legacyBytes = bytes.clone();
                this.nativeBytes = bytes.clone();
            }

            void writeIfDirty() {
                byte[] current = encode(array);
                if (Arrays.equals(current, legacyBytes)) {
                    return;
                }
                if (slice.buffer().isClosed() || (slice.buffer().usage() & GpuBuffer.USAGE_COPY_DST) == 0) {
                    GpuBufferSlice oldSlice = slice;
                    boolean closeOld = owned;
                    slice = createBuffer(current).slice(0, current.length);
                    owned = true;
                    nativeMap.put(key, slice);
                    if (closeOld && !oldSlice.buffer().isClosed()) {
                        oldSlice.buffer().close();
                    }
                } else {
                    writeSlice(slice, current);
                }
                legacyBytes = current.clone();
                nativeBytes = current.clone();
            }

            void readNativeIfClean() {
                byte[] current = encode(array);
                if (!Arrays.equals(current, legacyBytes)) {
                    writeIfDirty();
                    return;
                }
                if (slice.buffer().isClosed()) {
                    return; // A closed pass still answers V's retained array object.
                }
                byte[] nativeNow = readSlice(slice);
                if (!Arrays.equals(nativeNow, nativeBytes)) {
                    if (nativeNow.length != current.length) {
                        throw new IllegalStateException("Native uniform '" + key + "' changed byte length without changing its slice");
                    }
                    decode(array, nativeNow);
                    legacyBytes = nativeNow.clone();
                    nativeBytes = nativeNow;
                }
            }

            void close() {
                if (owned && !slice.buffer().isClosed()) {
                    slice.buffer().close();
                }
            }
        }

        private final class EntryView implements Map.Entry<String, Object> {
            private final String key;

            EntryView(String key) {
                this.key = key;
            }

            @Override
            public String getKey() {
                return key;
            }

            @Override
            public Object getValue() {
                return legacy(key);
            }

            @Override
            public Object setValue(Object value) {
                return put(key, value);
            }

            @Override
            public boolean equals(Object other) {
                return other instanceof Map.Entry<?, ?> entry
                    && Objects.equals(getKey(), entry.getKey()) && Objects.equals(getValue(), entry.getValue());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
            }

            @Override
            public String toString() {
                return getKey() + "=" + getValue();
            }
        }

        private final class EntrySet extends AbstractSet<Map.Entry<String, Object>> {
            @Override
            public int size() {
                return nativeMap.size();
            }

            @Override
            public boolean isEmpty() {
                return nativeMap.isEmpty();
            }

            @Override
            public void clear() {
                UniformMapView.this.clear();
            }

            @Override
            public Iterator<Map.Entry<String, Object>> iterator() {
                Iterator<String> iterator = nativeMap.keySet().iterator();
                return new Iterator<>() {
                    private String current;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Map.Entry<String, Object> next() {
                        current = iterator.next();
                        return new EntryView(current);
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                        discard(current);
                    }
                };
            }

            @Override
            public boolean contains(Object object) {
                if (!(object instanceof Map.Entry<?, ?> entry)) {
                    return false;
                }
                return nativeMap.containsKey(entry.getKey()) && Objects.equals(get(entry.getKey()), entry.getValue());
            }

            @Override
            public boolean remove(Object object) {
                if (!contains(object)) {
                    return false;
                }
                UniformMapView.this.remove(((Map.Entry<?, ?>) object).getKey());
                return true;
            }
        }

        private final class KeySet extends AbstractSet<String> {
            @Override
            public int size() {
                return nativeMap.size();
            }

            @Override
            public boolean isEmpty() {
                return nativeMap.isEmpty();
            }

            @Override
            public boolean contains(Object key) {
                return nativeMap.containsKey(key);
            }

            @Override
            public void clear() {
                UniformMapView.this.clear();
            }

            @Override
            public boolean remove(Object key) {
                if (!nativeMap.containsKey(key)) {
                    return false;
                }
                nativeMap.remove(key);
                if (key == null || key instanceof String) {
                    discard((String) key);
                }
                return true;
            }

            @Override
            public Iterator<String> iterator() {
                Iterator<String> iterator = nativeMap.keySet().iterator();
                return new Iterator<>() {
                    private String current;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public String next() {
                        current = iterator.next();
                        return current;
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                        discard(current);
                    }
                };
            }
        }

        private final class Values extends AbstractCollection<Object> {
            @Override
            public int size() {
                return nativeMap.size();
            }

            @Override
            public boolean isEmpty() {
                return nativeMap.isEmpty();
            }

            @Override
            public void clear() {
                UniformMapView.this.clear();
            }

            @Override
            public boolean contains(Object value) {
                return containsValue(value);
            }

            @Override
            public Iterator<Object> iterator() {
                Iterator<String> iterator = nativeMap.keySet().iterator();
                return new Iterator<>() {
                    private String current;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Object next() {
                        current = iterator.next();
                        return legacy(current);
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                        discard(current);
                    }
                };
            }
        }
    }
}
