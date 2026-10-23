package top.dext.centurybridge.step.v1_21_4.rt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.minecraft.class_3255;
import net.minecraft.class_3262;
import net.minecraft.class_3518;
import net.minecraft.class_7368;
import net.minecraft.class_7662;
import net.minecraft.class_7677;
import top.dext.centurybridge.step.v1_21_4.facade.MetadataSectionSerializer;

/**
 * 1.21.3's pack metadata API on 1.21.4. 1.21.3 named a section by a MetadataSectionSerializer (name +
 * fromJson; a MetadataSectionType also had toJson); 1.21.4 by a MetadataSectionType record (name, codec).
 * Both directions convert one object to one object and back:
 * <ul>
 * <li>{@link #type}: a 1.21.3 serializer to the record. A wrapper made here gives back the record it wraps;
 *     a mod's own serializer gets one record (its name, a codec over its fromJson/toJson), remembered, so
 *     1.21.4's maps (BuiltInMetadata, ResourceMetadata.Builder) find it again;</li>
 * <li>{@link #serializer}: the record to a 1.21.3 serializer: the mod's own object for a record made from
 *     one, otherwise one wrapper per record (vanilla's section types), with 1.21.3's fromCodec bodies.</li>
 * </ul>
 * The codec over a mod serializer reads exactly as 1.21.3's callers did -- fromJson of
 * GsonHelper.getAsJsonObject(root, name), whose exceptions propagate as they did (1.21.4's getMetadataFromStream
 * catches and logs them as 1.21.3's did, its ResourceMetadata.getSection lets them through as 1.21.3's did).
 * Section types are static singletons in practice; the maps are weak in their keys and live as long as them.
 */
public final class CbRestMetadata {

    private CbRestMetadata() {
    }

    private static final Map<MetadataSectionSerializer<?>, class_7677<?>> RECORDS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<class_7677<?>, MetadataSectionSerializer<?>> SERIALIZERS = Collections.synchronizedMap(new WeakHashMap<>());

    /** a 1.21.3 serializer (or type) as 1.21.4's record */
    @SuppressWarnings("unchecked")
    public static <T> class_7677<T> type(MetadataSectionSerializer<T> serializer) {
        if (serializer == null) {
            return null;
        }
        Object o = serializer;
        if (o instanceof class_7677<?> record) {
            // a 1.21.4 record handed on where 1.21.3 typed a serializer (interfaces are not checked by the verifier)
            return (class_7677<T>) record;
        }
        if (o instanceof Wrapped<?> w) {
            return (class_7677<T>) w.record;
        }
        synchronized (RECORDS) {
            class_7677<?> r = RECORDS.get(serializer);
            if (r == null) {
                r = new class_7677<>(serializer.method_14420(), codec(serializer));
                RECORDS.put(serializer, r);
                SERIALIZERS.put(r, serializer);
            }
            return (class_7677<T>) r;
        }
    }

    /** 1.21.4's record as a 1.21.3 serializer */
    @SuppressWarnings("unchecked")
    public static <T> MetadataSectionSerializer<T> serializer(class_7677<T> type) {
        if (type == null) {
            return null;
        }
        synchronized (RECORDS) {
            return (MetadataSectionSerializer<T>) SERIALIZERS.computeIfAbsent(type, Wrapped::new);
        }
    }

    /** a codec over a 1.21.3 serializer: reads as 1.21.3's callers did */
    private static <T> Codec<T> codec(MetadataSectionSerializer<T> s) {
        String name = s.method_14420();
        return new Codec<T>() {
            @Override
            public <O> DataResult<Pair<T, O>> decode(DynamicOps<O> ops, O input) {
                JsonObject root = new JsonObject();
                root.add(name, ops.convertTo(JsonOps.INSTANCE, input));
                return DataResult.success(Pair.of(s.method_14421(class_3518.method_15296(root, name)), ops.empty()));
            }

            @Override
            public <O> DataResult<O> encode(T value, DynamicOps<O> ops, O prefix) {
                // 1.21.3's MetadataSectionSerializer only read; writing was MetadataSectionType's (toJson)
                return DataResult.error(() -> "1.21.3 metadata serializer '" + name + "' only reads");
            }

            @Override
            public String toString() {
                return "CbRestMetadata[" + name + "]";
            }
        };
    }

    /** a 1.21.4 record seen as 1.21.3's MetadataSectionType: 1.21.3's fromCodec bodies over its codec */
    static final class Wrapped<T> implements MetadataSectionSerializer<T> {
        final class_7677<T> record;

        Wrapped(class_7677<T> record) {
            this.record = record;
        }

        @Override
        public String method_14420() {
            return this.record.comp_3436();
        }

        @Override
        public T method_14421(JsonObject json) {
            return CbRestMetadata.method_14421(this.record, json);
        }

        /** toJson(value): 1.21.3's MetadataSectionType had it (a record wrapper is one) */
        public JsonObject method_45251(T value) {
            return CbRestMetadata.method_45251(this.record, value);
        }

        @Override
        public String toString() {
            return this.record.toString();
        }
    }

    // ---- calls (instanceRedirects / staticRedirects), 1.21.3 shapes over 1.21.4's ----

    /** PackResources.getMetadataSection(serializer) */
    public static <T> T method_14407(class_3262 pack, MetadataSectionSerializer<T> s) throws java.io.IOException {
        return pack.method_14407(type(s));
    }

    /** AbstractPackResources.getMetadataSection(serializer): the same virtual call */
    public static <T> T method_14407(class_3255 pack, MetadataSectionSerializer<T> s) throws java.io.IOException {
        return pack.method_14407(type(s));
    }

    /** CompositePackResources.getMetadataSection(serializer): the same virtual call */
    public static <T> T method_14407(net.minecraft.class_8614 pack, MetadataSectionSerializer<T> s) throws java.io.IOException {
        return pack.method_14407(type(s));
    }

    /** VanillaPackResources.getMetadataSection(serializer): the same virtual call */
    public static <T> T method_14407(net.minecraft.class_3268 pack, MetadataSectionSerializer<T> s) throws java.io.IOException {
        return pack.method_14407(type(s));
    }

    /** MetadataSectionType.fromCodec(name, codec): 1.21.4's record over the same name and codec */
    public static <T> class_7677<T> method_45252(String name, Codec<T> codec) {
        return new class_7677<>(name, codec);
    }

    /**
     * MetadataSectionSerializer.getMetadataSectionName(): a 1.21.3 serializer's own, or -- for a 1.21.4 record
     * the mod holds under the serializer type (1.21.3's MetadataSectionType was one) -- the record's name
     */
    public static String method_14420(MetadataSectionSerializer<?> s) {
        Object o = s;
        return o instanceof class_7677<?> t ? t.comp_3436() : s.method_14420();
    }

    /** MetadataSectionSerializer.fromJson(json), the record held as a serializer reading as 1.21.3's fromCodec did */
    @SuppressWarnings("unchecked")
    public static <T> T method_14421(MetadataSectionSerializer<T> s, JsonObject json) {
        Object o = s;
        return o instanceof class_7677<?> t ? method_14421((class_7677<T>) t, json) : s.method_14421(json);
    }

    /** MetadataSectionType.getMetadataSectionName() on 1.21.4's record: its name */
    public static String method_14420(class_7677<?> type) {
        return type.comp_3436();
    }

    /** MetadataSectionType.fromJson(json) on 1.21.4's record: 1.21.3's fromCodec body */
    public static <T> T method_14421(class_7677<T> type, JsonObject json) {
        return type.comp_3437().parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
    }

    /** MetadataSectionType.toJson(value) on 1.21.4's record: 1.21.3's fromCodec body */
    public static <T> JsonObject method_45251(class_7677<T> type, T value) {
        return type.comp_3437().encodeStart(JsonOps.INSTANCE, value).getOrThrow(IllegalArgumentException::new).getAsJsonObject();
    }

    /** ResourceMetadata.getSection(serializer) */
    public static <T> Optional<T> method_43041(class_7368 metadata, MetadataSectionSerializer<T> s) {
        return metadata.method_43041(type(s));
    }

    /** ResourceMetadata.Builder.put(serializer, value) */
    public static <T> class_7368.class_8622 method_52448(class_7368.class_8622 builder, MetadataSectionSerializer<T> s, T value) {
        return builder.method_52448(type(s), value);
    }

    /** BuiltInMetadata.get(serializer) */
    public static <T> T method_45173(class_7662 metadata, MetadataSectionSerializer<T> s) {
        return metadata.method_45173(type(s));
    }

    /** BuiltInMetadata.of(serializer, value) */
    public static <T> class_7662 method_45174(MetadataSectionSerializer<T> s, T value) {
        return class_7662.method_45174(type(s), value);
    }

    /** BuiltInMetadata.of(serializer1, value1, serializer2, value2) */
    public static <T1, T2> class_7662 method_45175(MetadataSectionSerializer<T1> s1, T1 v1, MetadataSectionSerializer<T2> s2, T2 v2) {
        return class_7662.method_45175(type(s1), v1, type(s2), v2);
    }

    /** AbstractPackResources.getMetadataFromStream(serializer, stream) */
    public static <T> T method_14392(MetadataSectionSerializer<T> s, InputStream in) {
        return class_3255.method_14392(type(s), in);
    }

    // ---- overrides (overrideHooks wrap + superBridge) ----

    /** the value 1.21.4 passes a mod override, as the 1.21.3 serializer the override was written for */
    public static MetadataSectionSerializer<?> wrap(class_7677<?> type) {
        return serializer(type);
    }

    /** the vanilla PackResources implementation above a mod class, reached by its super.getMetadataSection */
    public interface SuperPack {
        Object cb$super$method_14407(class_7677<?> type) throws java.io.IOException;
    }

    public static Object superGetMetadataSection(SuperPack self, MetadataSectionSerializer<?> s) throws java.io.IOException {
        return self.cb$super$method_14407(type(s));
    }

    public interface SuperBuilder {
        class_7368.class_8622 cb$super$method_52448(class_7677<?> type, Object value);
    }

    public static class_7368.class_8622 superPut(SuperBuilder self, MetadataSectionSerializer<?> s, Object value) {
        return self.cb$super$method_52448(type(s), value);
    }

    // ---- generic drift: ResourceMetadata.copySections(Collection<serializer>) -> Collection<record> ----

    /** a 1.21.3 collection of serializers as 1.21.4's records (adaptive: a record passes as it is) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Collection types(java.util.Collection sections) {
        if (sections == null) {
            return null;
        }
        java.util.List out = new java.util.ArrayList(sections.size());
        for (Object o : sections) {
            out.add(o instanceof MetadataSectionSerializer<?> s ? type(s) : o);
        }
        return out;
    }

    /** 1.21.4's records as 1.21.3 serializers (adaptive: a serializer passes as it is) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Collection serializers(java.util.Collection sections) {
        if (sections == null) {
            return null;
        }
        java.util.List out = new java.util.ArrayList(sections.size());
        for (Object o : sections) {
            out.add(o instanceof class_7677<?> t ? serializer(t) : o);
        }
        return out;
    }

    // ---- lambdas (lambdaAdapters) ----

    /** a 1.21.3 ResourceMetadata lambda: getSection(serializer) */
    public interface LegacyResourceMetadata {
        Optional<?> method_43041(MetadataSectionSerializer<?> s);
    }

    public static class_7368 resourceMetadata(LegacyResourceMetadata legacy) {
        return new class_7368() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> Optional<T> method_43041(class_7677<T> type) {
                return (Optional<T>) legacy.method_43041(serializer(type));
            }
        };
    }
}
