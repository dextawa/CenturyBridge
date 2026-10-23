package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Stream;
import top.dext.centurybridge.facade.v1_20_2.class_7376$class_7377;

/**
 * 1.20.1's EntitySubPredicate.Type on 1.20.2.
 *
 * 1.20.1 named a sub-predicate's kind by a Type, a reader of the kind's JSON (the predicate wrote its own
 * fields with serializeCustomData; Types.TYPES named the vanilla kinds). 1.20.2 names it by a record holding
 * the kind's MapCodec. The two map one-to-one:
 * <ul>
 * <li>a 1.20.2 kind is handed to 1.20.1 code as one wrapper per kind ({@link #of}), so identity holds
 *     (sub.type() == Types.PLAYER); the wrapper reads through the kind's codec;</li>
 * <li>a 1.20.1 mod's own kind (its Type implementation) becomes one 1.20.2 kind per Type object
 *     ({@link #typeOf}), whose MapCodec decodes with the mod's reader and encodes with the predicate's own
 *     serializeCustomData (or its serialize() override, as 1.20.1's EntityPredicate wrote it).</li>
 * </ul>
 * Neither 1.20.1 nor 1.20.2 lets a mod add a kind to the vanilla name table, so a mod's kind has no name
 * there in either: 1.20.1 wrote "type": null for it, 1.20.2's dispatch codec refuses to encode it.
 */
public final class CbAdvSubTypes {

    private static final Map<Object, class_7376$class_7377> WRAPPERS = Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<Object, net.minecraft.class_7376.class_8745> LEGACY = Collections.synchronizedMap(new IdentityHashMap<>());

    /** a 1.20.2 kind as the 1.20.1 Type 1.20.1 code compares and reads with (one wrapper per kind) */
    public static class_7376$class_7377 of(net.minecraft.class_7376.class_8745 type) {
        if (type == null) {
            return null;
        }
        return WRAPPERS.computeIfAbsent(type, t -> new Vanilla((net.minecraft.class_7376.class_8745) t));
    }

    /** the 1.20.2 kind of a 1.20.1 Type: a wrapped vanilla kind's own, a mod's a kind built on its reader */
    public static net.minecraft.class_7376.class_8745 typeOf(class_7376$class_7377 type) {
        if (type == null) {
            return null;
        }
        if (type instanceof Vanilla v) {
            return v.type;
        }
        return LEGACY.computeIfAbsent(type, t -> new net.minecraft.class_7376.class_8745(new LegacyCodec((class_7376$class_7377) t)));
    }

    // ------------------------------------------------------------------ Types constants and table

    /** Types.ANY */
    public static final class_7376$class_7377 field_38724 = of(net.minecraft.class_7376.class_7378.field_38724);
    /** Types.LIGHTNING */
    public static final class_7376$class_7377 field_38725 = of(net.minecraft.class_7376.class_7378.field_38725);
    /** Types.FISHING_HOOK */
    public static final class_7376$class_7377 field_38726 = of(net.minecraft.class_7376.class_7378.field_38726);
    /** Types.PLAYER */
    public static final class_7376$class_7377 field_38727 = of(net.minecraft.class_7376.class_7378.field_38727);
    /** Types.SLIME */
    public static final class_7376$class_7377 field_38728 = of(net.minecraft.class_7376.class_7378.field_38728);

    private static volatile BiMap<String, class_7376$class_7377> types;

    /** Types.TYPES: 1.20.2's name table with each kind as its 1.20.1 Type (fieldGetters; built once) */
    public static BiMap<String, class_7376$class_7377> field_38731() {
        BiMap<String, class_7376$class_7377> t = types;
        if (t == null) {
            ImmutableBiMap.Builder<String, class_7376$class_7377> b = ImmutableBiMap.builder();
            net.minecraft.class_7376.class_7378.field_38731.forEach((name, kind) -> b.put(name, of(kind)));
            types = t = b.build();
        }
        return t;
    }

    // ------------------------------------------------------------------ EntitySubPredicate / EntityVariantPredicate

    /** EntitySubPredicate.type(): a 1.20.1 implementation's own answer, else its 1.20.2 kind */
    public static class_7376$class_7377 method_43099(net.minecraft.class_7376 self) {
        if ((Object) self instanceof CbAdvLegacy.SubType own) {
            return own.method_43099();
        }
        return of(self.comp_1776());
    }

    private static final Map<Object, net.minecraft.class_7376.class_8745> OWN = Collections.synchronizedMap(new IdentityHashMap<>());

    /**
     * the 1.20.2 kind of a 1.20.1 implementation (adapterHooks synthesize comp_1776 with it): always a kind
     * of its own, read with its Type and written with its own serializeCustomData -- even when the mod
     * answers a vanilla Type, whose 1.20.2 codec could not write the mod's class
     */
    public static net.minecraft.class_7376.class_8745 legacyType(CbAdvLegacy.SubType own) {
        class_7376$class_7377 type = own.method_43099();
        return OWN.computeIfAbsent(type, t -> new net.minecraft.class_7376.class_8745(new LegacyCodec((class_7376$class_7377) t)));
    }

    /** EntityVariantPredicate.type() */
    public static class_7376$class_7377 method_43114(net.minecraft.class_7380<?> self) {
        return of(self.method_43114());
    }

    // ------------------------------------------------------------------ the two directions

    /** a 1.20.2 kind read the 1.20.1 way: its codec over the JSON object */
    private static final class Vanilla implements class_7376$class_7377 {
        final net.minecraft.class_7376.class_8745 type;

        Vanilla(net.minecraft.class_7376.class_8745 type) {
            this.type = type;
        }

        @Override
        @SuppressWarnings("unchecked")
        public net.minecraft.class_7376 deserialize(JsonObject json) {
            MapCodec<net.minecraft.class_7376> codec = (MapCodec<net.minecraft.class_7376>) type.comp_1774();
            return codec.codec().parse(JsonOps.INSTANCE, json).result().orElseThrow(() -> new com.google.gson.JsonSyntaxException(
                "Invalid entity sub-predicate: " + codec.codec().parse(JsonOps.INSTANCE, json).error().map(Object::toString).orElse("")));
        }

        @Override
        public String toString() {
            return "CenturyBridge 1.20.1 view of " + type;
        }
    }

    /** a 1.20.1 mod's kind as a MapCodec: the mod's reader, the predicate's own JSON */
    private static final class LegacyCodec extends MapCodec<net.minecraft.class_7376> {
        private final class_7376$class_7377 reader;

        LegacyCodec(class_7376$class_7377 reader) {
            this.reader = reader;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }

        @Override
        public <T> DataResult<net.minecraft.class_7376> decode(DynamicOps<T> ops, MapLike<T> input) {
            JsonObject json = new JsonObject();
            input.entries().forEach(p -> ops.getStringValue(p.getFirst()).result()
                .ifPresent(k -> json.add(k, ops.convertTo(JsonOps.INSTANCE, p.getSecond()))));
            try {
                return DataResult.success(reader.deserialize(json));
            } catch (RuntimeException e) {
                return DataResult.error(() -> "CenturyBridge: 1.20.1 entity sub-predicate reader failed: " + e.getMessage());
            }
        }

        @Override
        public <T> RecordBuilder<T> encode(net.minecraft.class_7376 input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            // the predicate's own fields, as its serializeCustomData writes them (abstract in 1.20.1: every
            // implementation of a mod's kind declares it)
            JsonElement e = (Object) input instanceof CbAdvLegacy.SubJson own ? own.method_22494() : null;
            if (e != null && e.isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : e.getAsJsonObject().entrySet()) {
                    if (!entry.getKey().equals("type")) {
                        prefix.add(entry.getKey(), JsonOps.INSTANCE.convertTo(ops, entry.getValue()));
                    }
                }
            }
            return prefix;
        }

        @Override
        public String toString() {
            return "CenturyBridge 1.20.1 entity sub-predicate kind " + reader;
        }
    }

    private CbAdvSubTypes() {
    }
}
