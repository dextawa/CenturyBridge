package cb.s6gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import top.dext.centurybridge.step.v1_21_6.rt.CbS6GlUniforms;

/** Memory-backed no-GL checks for legacy uniform arrays and their native slices. */
public final class CbS6GlUniformsHarness {
    private static int checks;

    private static void check(boolean condition, String label) {
        checks++;
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    private static void expectIllegalState(String label, Runnable action) {
        checks++;
        try {
            action.run();
            throw new AssertionError(label + ": no exception");
        } catch (IllegalStateException expected) {
            // Expected: an unrepresentable value must fail here, not as a later checkcast CCE.
        }
    }

    private static final class MemoryBuffer extends GpuBuffer {
        private final byte[] bytes;
        private boolean closed;

        MemoryBuffer(int usage, int size) {
            super(usage, size);
            bytes = new byte[size];
        }

        MemoryBuffer(int usage, ByteBuffer initial) {
            this(usage, initial.remaining());
            initial.get(bytes);
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    /** Proxies leave every unrelated GpuDevice / CommandEncoder operation unimplemented, with no GL class loading. */
    private static final class MemoryDevice {
        private final GpuDevice device;
        private final CommandEncoder encoder;

        MemoryDevice() {
            encoder = (CommandEncoder) Proxy.newProxyInstance(CbS6GlUniformsHarness.class.getClassLoader(),
                new Class<?>[] {CommandEncoder.class}, (proxy, method, args) -> {
                    if (method.getName().equals("writeToBuffer")) {
                        GpuBufferSlice slice = (GpuBufferSlice) args[0];
                        ByteBuffer source = ((ByteBuffer) args[1]).duplicate();
                        MemoryBuffer target = memory(slice.buffer());
                        check(!target.closed, "write target is open");
                        check((target.usage() & GpuBuffer.USAGE_COPY_DST) != 0, "write target has COPY_DST");
                        check(source.remaining() == slice.length(), "write length equals slice length");
                        source.get(target.bytes, slice.offset(), slice.length());
                        return null;
                    }
                    if (method.getName().equals("mapBuffer")) {
                        GpuBufferSlice slice = (GpuBufferSlice) args[0];
                        MemoryBuffer buffer = memory(slice.buffer());
                        boolean read = (Boolean) args[1];
                        check(!buffer.closed, "map target is open");
                        if (read) {
                            check((buffer.usage() & GpuBuffer.USAGE_MAP_READ) != 0, "read target has MAP_READ");
                        }
                        ByteBuffer data = ByteBuffer.wrap(buffer.bytes, slice.offset(), slice.length()).slice()
                            .order(ByteOrder.nativeOrder());
                        return new GpuBuffer.MappedView() {
                            @Override
                            public ByteBuffer data() {
                                return data;
                            }

                            @Override
                            public void close() {
                            }
                        };
                    }
                    return objectMethod(proxy, method, args);
                });
            device = (GpuDevice) Proxy.newProxyInstance(CbS6GlUniformsHarness.class.getClassLoader(),
                new Class<?>[] {GpuDevice.class}, (proxy, method, args) -> {
                    if (method.getName().equals("createCommandEncoder")) {
                        return encoder;
                    }
                    if (method.getName().equals("createBuffer")) {
                        int usage = (Integer) args[1];
                        return args[2] instanceof ByteBuffer initial
                            ? new MemoryBuffer(usage, initial) : new MemoryBuffer(usage, (Integer) args[2]);
                    }
                    return objectMethod(proxy, method, args);
                });
        }

        private static Object objectMethod(Object proxy, java.lang.reflect.Method method, Object[] args) {
            return switch (method.getName()) {
                case "toString" -> "memory-" + proxy.getClass().getInterfaces()[0].getSimpleName();
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> throw new UnsupportedOperationException(method.toString());
            };
        }
    }

    private static MemoryBuffer memory(GpuBuffer buffer) {
        return (MemoryBuffer) buffer;
    }

    private static int word(GpuBufferSlice slice, int index) {
        return ByteBuffer.wrap(memory(slice.buffer()).bytes).order(ByteOrder.nativeOrder())
            .getInt(slice.offset() + index * 4);
    }

    private static void word(GpuBufferSlice slice, int index, int value) {
        ByteBuffer.wrap(memory(slice.buffer()).bytes).order(ByteOrder.nativeOrder())
            .putInt(slice.offset() + index * 4, value);
    }

    private static GpuBufferSlice nativeSlice(MemoryDevice memory, int... words) {
        ByteBuffer bytes = ByteBuffer.allocateDirect(words.length * 4).order(ByteOrder.nativeOrder());
        for (int word : words) {
            bytes.putInt(word);
        }
        bytes.flip();
        int usage = GpuBuffer.USAGE_MAP_READ | GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_UNIFORM;
        return memory.device.createBuffer(() -> "native control", usage, bytes).slice();
    }

    public static void main(String[] args) {
        MemoryDevice memory = new MemoryDevice();
        HashMap<String, GpuBufferSlice> nativeMap = new HashMap<>();
        HashMap<String, Object> legacy = CbS6GlUniforms.uniformMap(nativeMap, memory.device);

        int[] ints = {1, -2, Integer.MIN_VALUE};
        check(legacy.put("ints", ints) == null, "first array put has no previous value");
        check(legacy.get("ints") == ints, "read returns the identical int array");
        GpuBufferSlice intSlice = nativeMap.get("ints");
        check(intSlice.length() == 12, "native slice covers every array element");
        check(word(intSlice, 0) == 1 && word(intSlice, 1) == -2 && word(intSlice, 2) == Integer.MIN_VALUE,
            "initial int bits reach native storage");
        check((intSlice.buffer().usage() & GpuBuffer.USAGE_UNIFORM) != 0, "native slice is a valid uniform buffer");

        ints[1] = 0x12345678;
        CbS6GlUniforms.flush(legacy);
        check(word(intSlice, 1) == 0x12345678, "draw-boundary flush sees an in-place edit without map.get");
        check(legacy.get("ints") == ints, "flush preserves array identity");

        word(intSlice, 0, 99);
        check(((int[]) legacy.get("ints"))[0] == 99, "a clean legacy read observes native storage edits");
        ints[1] = 77;
        word(intSlice, 1, 55);
        check(((int[]) legacy.get("ints"))[1] == 77 && word(intSlice, 1) == 77,
            "a dirty legacy array wins instead of being overwritten by readback");

        int nanBits = 0x7fa12345;
        float[] floats = {Float.intBitsToFloat(nanBits), -0.0f, Float.intBitsToFloat(0xffcabcde)};
        legacy.put("floats", floats);
        GpuBufferSlice floatSlice = nativeMap.get("floats");
        check(word(floatSlice, 0) == nanBits, "NaN payload is not canonicalized on upload");
        check(word(floatSlice, 1) == 0x80000000, "negative zero bits survive");
        check(word(floatSlice, 2) == 0xffcabcde, "negative NaN payload survives");
        check(legacy.get("floats") == floats, "float read returns the identical array");
        check(Float.floatToRawIntBits(floats[0]) == nanBits && Float.floatToRawIntBits(floats[2]) == 0xffcabcde,
            "float array bits survive the round trip");

        GpuBufferSlice oldFloatSlice = floatSlice;
        GpuBufferSlice nativeReplacement = nativeSlice(memory, 0x3f800000, 0x40000000, 0x40400000);
        nativeMap.put("floats", nativeReplacement);
        check(legacy.get("floats") == floats, "same-length native replacement retains legacy array identity");
        check(floats[0] == 1.0f && floats[1] == 2.0f && floats[2] == 3.0f,
            "native replacement values decode under the known legacy kind");
        check(memory(oldFloatSlice.buffer()).closed, "replaced layer-owned buffer is closed");
        floats[1] = 4.0f;
        CbS6GlUniforms.flush(legacy);
        check(word(nativeReplacement, 1) == 0x40800000, "edits after native adoption write through to its slice");

        float[] subviewSource = {5.0f, 6.0f, 7.0f};
        legacy.put("subview", subviewSource);
        GpuBufferSlice whole = nativeMap.get("subview");
        nativeMap.put("subview", whole.slice(4, 4));
        float[] subview = (float[]) legacy.get("subview");
        check(subview.length == 1 && subview[0] == 6.0f, "a native sub-slice keeps offset and known element kind");
        check(!memory(whole.buffer()).closed, "adopting a sub-slice does not close its shared owned buffer");

        GpuBufferSlice longer = nativeSlice(memory, 10, 20, 30, 40);
        nativeMap.put("ints", longer);
        int[] adopted = (int[]) legacy.get("ints");
        check(adopted != ints && adopted.length == 4, "native length change allocates the known kind, never the wrong type");
        check(adopted[0] == 10 && adopted[3] == 40, "native length change preserves every native value");
        ints[1] = 88; // The detached old array must not contaminate the replacement.

        int[] empty = new int[0];
        legacy.put("empty", empty);
        check(legacy.get("empty") == empty, "V's valid zero-length array retains identity");
        check(nativeMap.get("empty").length() == 0 && nativeMap.get("empty").buffer().size() == 4,
            "zero-length array uses a zero-length slice, not a fabricated element");
        CbS6GlUniforms.flush(legacy);
        check(legacy.get("empty") == empty, "empty array flush and readback do not require an empty GL mapping");

        GpuBufferSlice unknown = nativeSlice(memory, 0x3f800000);
        nativeMap.put("unknown", unknown);
        expectIllegalState("native-only slice has no legacy primitive kind", () -> legacy.get("unknown"));
        check(nativeMap.get("unknown") == unknown, "rejected native-only value remains intact for native consumers");
        nativeMap.remove("unknown"); // Whole-map legacy operations below intentionally require legacy values.

        GpuBufferSlice unreadable = memory.device.createBuffer(() -> "unreadable", GpuBuffer.USAGE_UNIFORM, 4).slice();
        nativeMap.put("floats", unreadable);
        expectIllegalState("unreadable native replacement is not guessed", () -> legacy.get("floats"));
        nativeMap.put("floats", nativeReplacement);
        check(legacy.get("floats") == floats, "known readable value recovers after an unreadable counterexample");

        Map.Entry<String, Object> entry = null;
        for (Map.Entry<String, Object> candidate : legacy.entrySet()) {
            if (candidate.getKey().equals("ints")) {
                entry = candidate;
            }
        }
        check(entry != null && entry.getValue() == adopted, "entry iteration returns live legacy values");
        int[] entryReplacement = {7, 8};
        check(entry.setValue(entryReplacement) == adopted, "entry setValue returns the previous array");
        check(nativeMap.get("ints").length() == 8 && legacy.get("ints") == entryReplacement,
            "entry setValue writes a replacement array through");

        legacy.put(null, ints);
        check(legacy.get(null) == ints, "HashMap's null key remains supported");
        check(legacy.remove(null) == ints && !nativeMap.containsKey(null), "null-key removal writes through");
        legacy.put("null", null);
        check(legacy.containsKey("null") && legacy.get("null") == null && nativeMap.containsKey("null"),
            "null values remain representable");
        check(legacy.remove("null", null), "null two-arg remove");
        check(legacy.computeIfAbsent("absent", key -> null) == null && !legacy.containsKey("absent"),
            "computeIfAbsent null does not insert");
        check(legacy.entrySet().contains(new SimpleEntry<>("ints", entryReplacement)),
            "entrySet contains uses legacy values");
        check(legacy.values().contains(entryReplacement), "values contains uses identity like a primitive array");
        check(legacy.keySet().contains("ints"), "keySet follows native keys");

        @SuppressWarnings("unchecked")
        HashMap<String, Object> clone = (HashMap<String, Object>) legacy.clone();
        check(clone != legacy && clone.get("ints") == entryReplacement, "clone is shallow, like V's HashMap");
        clone.remove("ints");
        check(legacy.containsKey("ints"), "clone structural mutation is independent");

        // The unknown native entry is already gone; discard the externally replaced float value as a native consumer would.
        nativeMap.remove("floats");
        GpuBufferSlice removedSlice = nativeMap.get("ints");
        Iterator<String> keys = legacy.keySet().iterator();
        String removed = null;
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.equals("ints")) {
                removed = key;
                keys.remove();
                break;
            }
        }
        check("ints".equals(removed) && !nativeMap.containsKey("ints"), "key iterator removal writes through");
        check(memory(removedSlice.buffer()).closed, "removed layer-owned buffer is closed");

        legacy.clear();
        check(nativeMap.isEmpty() && legacy.isEmpty(), "clear writes through");
        System.out.println("CB_S6GL_UNIFORMS_HARNESS OK " + checks + " checks");
    }
}
