package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's CompoundTag save data on 1.21.6's ValueInput / ValueOutput (1.21.6 moved every
 * readAdditionalSaveData / addAdditionalSaveData / loadAdditional / saveAdditional ... from a CompoundTag
 * to a view of one). Vanilla's only implementations are TagValueInput and TagValueOutput, each a view of
 * one CompoundTag (field_60382 / field_60404, opened by the layer's access widener): the converters hand
 * a 1.21.5 override that very tag, so what the old method reads is what vanilla stored and what it puts
 * is what vanilla saves -- the same object 1.21.5 passed.
 *
 * <p>The other direction (a 1.21.5 call or super call holding a tag) gives vanilla a view of the tag. When
 * the tag is one this thread just took out of vanilla's own view -- the old override's super call, inside
 * the hook vanilla called -- that view goes back ({@link #recent}): vanilla sees exactly what it passed,
 * its problem reporter included. Otherwise a new view of the tag: on the lookup 1.21.5 passed or the
 * receiver's registries, reporting to a collector that logs as 1.21.6's own callers do
 * ({@link #reporter} / {@link #close}).
 */
public final class CbValueIo {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("CenturyBridge/ValueIo");

    private CbValueIo() {
    }

    // ---- views vanilla handed out, most recent first (per thread, weakly held, bounded) ----

    private static final int RECENT = 16;
    private static final ThreadLocal<java.lang.ref.WeakReference<?>[]> RING =
        ThreadLocal.withInitial(() -> new java.lang.ref.WeakReference<?>[RECENT]);
    private static final ThreadLocal<int[]> HEAD = ThreadLocal.withInitial(() -> new int[1]);

    private static void remember(Object view) {
        java.lang.ref.WeakReference<?>[] ring = RING.get();
        int[] head = HEAD.get();
        head[0] = (head[0] + 1) % RECENT;
        ring[head[0]] = new java.lang.ref.WeakReference<>(view);
    }

    /** the view of {@code tag} this thread most recently took a tag out of, or null */
    private static Object recent(net.minecraft.class_2487 tag) {
        java.lang.ref.WeakReference<?>[] ring = RING.get();
        int h = HEAD.get()[0];
        for (int i = 0; i < RECENT; i++) {
            java.lang.ref.WeakReference<?> r = ring[(h - i + RECENT) % RECENT];
            Object v = r == null ? null : r.get();
            if (v instanceof net.minecraft.class_11352 in && in.field_60382 == tag
                    || v instanceof net.minecraft.class_11362 out && out.field_60404 == tag) {
                return v;
            }
        }
        return null;
    }

    // ---- vanilla's view -> 1.21.5's tag (override hook wraps) ----

    /** every entry of a map, as a CompoundTag: reads a ValueInput that is not a TagValueInput whole */
    private static final com.mojang.serialization.MapCodec<net.minecraft.class_2487> WHOLE =
        new com.mojang.serialization.MapCodec<>() {
            @Override
            public <T> java.util.stream.Stream<T> keys(com.mojang.serialization.DynamicOps<T> ops) {
                return java.util.stream.Stream.empty();
            }

            @Override
            public <T> com.mojang.serialization.DataResult<net.minecraft.class_2487> decode(
                    com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.MapLike<T> input) {
                net.minecraft.class_2487 tag = new net.minecraft.class_2487();
                input.entries().forEach(e -> ops.getStringValue(e.getFirst()).result().ifPresent(
                    k -> tag.method_10566(k, ops.convertTo(net.minecraft.class_2509.field_11560, e.getSecond()))));
                return com.mojang.serialization.DataResult.success(tag);
            }

            @Override
            public <T> com.mojang.serialization.RecordBuilder<T> encode(net.minecraft.class_2487 tag,
                    com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.RecordBuilder<T> prefix) {
                for (String k : tag.method_10541()) {
                    prefix.add(k, net.minecraft.class_2509.field_11560.method_29146(ops, tag.method_10580(k)));
                }
                return prefix;
            }
        };

    /** the tag a 1.21.5 reader gets from vanilla's input: the TagValueInput's own tag */
    public static net.minecraft.class_2487 tag(net.minecraft.class_11368 in) {
        if (in instanceof net.minecraft.class_11352 view) {
            remember(view);
            return view.field_60382;
        }
        // ValueInputContextHelper's empty input reads nothing: a new empty tag, as 1.21.5's getCompound gave
        return in.method_71418(WHOLE).orElseGet(net.minecraft.class_2487::new);
    }

    /** the tag a 1.21.5 writer puts into: the TagValueOutput's own tag, which vanilla saves */
    public static net.minecraft.class_2487 tag(net.minecraft.class_11372 out) {
        if (out instanceof net.minecraft.class_11362 view) {
            remember(view);
            return view.field_60404;
        }
        // 1.21.6 has no other ValueOutput (vanilla and Fabric API checked): a tag written here would be lost
        throw new IllegalArgumentException("CenturyBridge: no CompoundTag stands behind " + out.getClass().getName()
            + "; a 1.21.5 save method has nowhere to write");
    }

    /** the registry lookup a 1.21.5 reader was passed with its tag */
    public static net.minecraft.class_7225.class_7874 lookup(net.minecraft.class_11368 in) {
        return in.method_71414();
    }

    /** the registry lookup a 1.21.5 writer was passed: the one vanilla made the output's RegistryOps from */
    public static net.minecraft.class_7225.class_7874 lookup(net.minecraft.class_11372 out) {
        if (out instanceof net.minecraft.class_11362 view && view.field_60403 instanceof net.minecraft.class_6903<?> ops
                && ops.field_40852 instanceof net.minecraft.class_6903.class_9683 adapter) {
            return adapter.field_51501;
        }
        // an output without registries (TagValueOutput.createWithoutContext): a lookup knowing none
        return net.minecraft.class_7225.class_7874.method_46761(java.util.stream.Stream.empty());
    }

    // ---- 1.21.5's tag -> a view for vanilla ----

    /** the reporter a new view reports to: logs on {@link #close}, as 1.21.6's callers' collectors do */
    public static net.minecraft.class_8942.class_11340 reporter() {
        return new net.minecraft.class_8942.class_11340(LOGGER);
    }

    public static void close(net.minecraft.class_8942.class_11340 reporter) {
        reporter.close();
    }

    /** a lookup with no registries, for a view whose receiver knows none */
    private static net.minecraft.class_7225.class_7874 none() {
        return net.minecraft.class_7225.class_7874.method_46761(java.util.stream.Stream.empty());
    }

    /** the registries 1.21.5 code on this object read through: its level's, or the entity's */
    public static net.minecraft.class_7225.class_7874 lookupOf(Object receiver) {
        if (receiver instanceof net.minecraft.class_1297 e) {
            return e.method_56673();
        }
        if (receiver instanceof net.minecraft.class_2586 be && be.method_10997() != null) {
            return be.method_10997().method_30349();
        }
        if (receiver instanceof net.minecraft.class_1937 level) {
            return level.method_30349();
        }
        return null;
    }

    /** vanilla's view of the tag: the one it handed out, else a new TagValueInput over it */
    public static net.minecraft.class_11368 input(net.minecraft.class_2487 tag, net.minecraft.class_7225.class_7874 lookup,
            net.minecraft.class_8942 reporter) {
        if (recent(tag) instanceof net.minecraft.class_11352 in) {
            return in;
        }
        return net.minecraft.class_11352.method_71417(reporter, lookup != null ? lookup : none(), tag);
    }

    /** vanilla's view of the tag: the one it handed out, else a new TagValueOutput writing into it */
    public static net.minecraft.class_11362 output(net.minecraft.class_2487 tag, net.minecraft.class_7225.class_7874 lookup,
            net.minecraft.class_8942 reporter) {
        if (recent(tag) instanceof net.minecraft.class_11362 out) {
            return out;
        }
        return new net.minecraft.class_11362(reporter, lookup != null
            ? lookup.method_57093(net.minecraft.class_2509.field_11560)
            : net.minecraft.class_2509.field_11560, tag);
    }

    /** a new output into a new tag, as 1.21.6's createWithContext / createWithoutContext */
    public static net.minecraft.class_11362 fresh(net.minecraft.class_7225.class_7874 lookup, net.minecraft.class_8942 reporter) {
        return lookup != null ? net.minecraft.class_11362.method_71459(reporter, lookup)
            : net.minecraft.class_11362.method_71458(reporter);
    }

    /** the tag an output writes into */
    public static net.minecraft.class_2487 result(net.minecraft.class_11362 out) {
        return out.method_71475();
    }
}
