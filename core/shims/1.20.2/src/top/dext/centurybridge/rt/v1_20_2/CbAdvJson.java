package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.util.Optional;

/**
 * 1.20.1's JSON API of the advancement predicates on 1.20.2.
 *
 * 1.20.1 read and wrote each predicate by hand: a static fromJson(JsonElement) that answered the
 * type's ANY for null or JSON null, and a serializeToJson() that answered JsonNull for ANY. 1.20.2
 * turned the predicates into records with codecs and wrote the same JSON through them (the vanilla
 * advancement files of 1.20.1 and 1.20.2 differ only in requirement order and one reward). Each
 * bridge here keeps 1.20.1's contract -- null / JSON null is ANY (CbAdvAny's sentinel), ANY writes
 * JsonNull -- and goes through the record's own codec for the rest, failing as 1.20.1 failed (a
 * JsonSyntaxException naming what was wrong) where the JSON does not parse.
 *
 * The receiver-typed statics serve instance calls (instanceRedirects) and super calls (superRedirects)
 * alike: the predicates are final records in 1.20.2, so no mod subclass reaches them otherwise.
 */
public final class CbAdvJson {

    // ------------------------------------------------------------------ the shared shape

    private static boolean absent(JsonElement json) {
        return json == null || json.isJsonNull();
    }

    private static <T> T decode(Codec<T> codec, JsonElement json, String what) {
        return codec.parse(JsonOps.INSTANCE, json).result().orElseThrow(() -> new JsonSyntaxException(
            "Invalid " + what + ": " + codec.parse(JsonOps.INSTANCE, json).error().map(Object::toString).orElse(String.valueOf(json))));
    }

    private static <T> JsonElement encode(Codec<T> codec, T value, String what) {
        return codec.encodeStart(JsonOps.INSTANCE, value).result().orElseThrow(() -> new IllegalStateException(
            "CenturyBridge: " + what + " does not encode: " + value));
    }

    private static <T> JsonObject encodeMap(MapCodec<T> codec, T value, String what) {
        JsonElement e = encode(codec.codec(), value, what);
        return e.isJsonObject() ? e.getAsJsonObject() : new JsonObject();
    }

    // ------------------------------------------------------------------ EnchantmentPredicate

    /** EnchantmentPredicate.NONE, 1.20.1's empty array (a fresh copy, as 1.20.1 handed out the shared one) */
    public static final net.minecraft.class_2035[] field_20687 = new net.minecraft.class_2035[0];

    /** EnchantmentPredicate.fromJson(json) */
    public static net.minecraft.class_2035 method_8882(JsonElement json) {
        return absent(json) ? CbAdvAny.field_9571 : decode(net.minecraft.class_2035.field_45741, json, "enchantment predicate");
    }

    /** EnchantmentPredicate.fromJsonArray(json): JSON null is NONE, else every element through fromJson */
    public static net.minecraft.class_2035[] method_8879(JsonElement json) {
        if (absent(json)) {
            return field_20687;
        }
        JsonArray array = net.minecraft.class_3518.method_15252(json, "enchantments");
        net.minecraft.class_2035[] out = new net.minecraft.class_2035[array.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = method_8882(array.get(i));
        }
        return out;
    }

    /** EnchantmentPredicate.serializeToJson() */
    public static JsonElement method_8881(net.minecraft.class_2035 self) {
        return (Object) self instanceof CbAdvLegacy.EnchantmentJson own ? own.method_8881() : super_method_8881(self);
    }

    /** the same, without dispatching to a 1.20.1 subclass's override (that subclass's own super call) */
    public static JsonElement super_method_8881(net.minecraft.class_2035 self) {
        return self == CbAdvAny.field_9571 ? JsonNull.INSTANCE : encode(net.minecraft.class_2035.field_45741, self, "enchantment predicate");
    }

    // ------------------------------------------------------------------ EntityFlagsPredicate

    public static net.minecraft.class_2040 method_8893(JsonElement json) {
        return absent(json) ? CbAdvAny.field_9581 : decode(net.minecraft.class_2040.field_45745, json, "entity flags predicate");
    }

    public static JsonElement method_8894(net.minecraft.class_2040 self) {
        return (Object) self instanceof CbAdvLegacy.FlagsJson own ? own.method_8894() : super_method_8894(self);
    }

    /** the same, without dispatching to a 1.20.1 subclass's override (that subclass's own super call) */
    public static JsonElement super_method_8894(net.minecraft.class_2040 self) {
        return self == CbAdvAny.field_9581 ? JsonNull.INSTANCE : encode(net.minecraft.class_2040.field_45745, self, "entity flags predicate");
    }

    // ------------------------------------------------------------------ EntityTypePredicate

    /** EntityTypePredicate.fromJson(json): a type id, or "#tag" */
    public static net.minecraft.class_2050 method_8928(JsonElement json) {
        if (absent(json)) {
            return CbAdvAny.field_9609;
        }
        String s = net.minecraft.class_3518.method_15287(json, "type");
        if (s.startsWith("#")) {
            return net.minecraft.class_2050.method_8926(net.minecraft.class_6862.method_40092(
                net.minecraft.class_7924.field_41266, new net.minecraft.class_2960(s.substring(1))));
        }
        net.minecraft.class_2960 id = new net.minecraft.class_2960(s);
        net.minecraft.class_1299<?> type = net.minecraft.class_7923.field_41177.method_17966(id).orElseThrow(() ->
            new JsonSyntaxException("Unknown entity type '" + id + "', valid types are: "
                + com.google.common.base.Joiner.on(", ").join(net.minecraft.class_7923.field_41177.method_10235())));
        return net.minecraft.class_2050.method_8929(type);
    }

    /** EntityTypePredicate.serializeToJson(): ANY writes JsonNull, a type its id, a tag "#tag" */
    public static JsonElement method_8927(net.minecraft.class_2050 self) {
        return (Object) self instanceof CbAdvLegacy.EntityTypeJson own ? own.method_8927() : super_method_8927(self);
    }

    /** the same, without dispatching to a 1.20.1 subclass's override (that subclass's own super call) */
    public static JsonElement super_method_8927(net.minecraft.class_2050 self) {
        return self == CbAdvAny.field_9609 ? JsonNull.INSTANCE : encode(net.minecraft.class_2050.field_45749, self, "entity type predicate");
    }

    // ------------------------------------------------------------------ ItemPredicate

    /** ItemPredicate.fromJsonArray(json): JSON null is an empty array, else "items" elements through fromJson */
    public static net.minecraft.class_2073[] method_8972(JsonElement json) {
        if (absent(json)) {
            return new net.minecraft.class_2073[0];
        }
        JsonArray array = net.minecraft.class_3518.method_15252(json, "items");
        net.minecraft.class_2073[] out = new net.minecraft.class_2073[array.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = Statics.method_8969(array.get(i));
        }
        return out;
    }

    // ------------------------------------------------------------------ MobEffectsPredicate.MobEffectInstancePredicate

    /** MobEffectInstancePredicate.fromJson(object): amplifier, duration, ambient, visible -- 1.20.1 had no ANY */
    public static net.minecraft.class_2102.class_2103 method_9070(JsonObject json) {
        return decode(net.minecraft.class_2102.class_2103.field_45766, json, "mob effect instance predicate");
    }

    public static JsonElement method_9071(net.minecraft.class_2102.class_2103 self) {
        return (Object) self instanceof CbAdvLegacy.EffectInstanceJson own ? own.method_9071() : super_method_9071(self);
    }

    /** the same, without dispatching to a 1.20.1 subclass's override (that subclass's own super call) */
    public static JsonElement super_method_9071(net.minecraft.class_2102.class_2103 self) {
        return encode(net.minecraft.class_2102.class_2103.field_45766, self, "mob effect instance predicate");
    }

    // ------------------------------------------------------------------ NbtPredicate

    /** NbtPredicate.fromJson(json): the SNBT string "nbt" (1.20.1: "Invalid nbt tag: <message>") */
    public static net.minecraft.class_2105 method_9073(JsonElement json) {
        if (absent(json)) {
            return CbAdvAny.field_9716;
        }
        try {
            return new net.minecraft.class_2105(net.minecraft.class_2522.method_10718(net.minecraft.class_3518.method_15287(json, "nbt")));
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid nbt tag: " + e.getMessage());
        }
    }

    /** NbtPredicate.serializeToJson(): ANY and a predicate without a tag write JsonNull, else the SNBT string */
    public static JsonElement method_9075(net.minecraft.class_2105 self) {
        return (Object) self instanceof CbAdvLegacy.NbtJson own ? own.method_9075() : super_method_9075(self);
    }

    /** the same, without dispatching to a 1.20.1 subclass's override (that subclass's own super call) */
    public static JsonElement super_method_9075(net.minecraft.class_2105 self) {
        if (self == CbAdvAny.field_9716 || self.comp_1816() == null) {
            return JsonNull.INSTANCE;
        }
        return new com.google.gson.JsonPrimitive(self.comp_1816().toString());
    }

    // ------------------------------------------------------------------ EntityEquipmentPredicate

    public static net.minecraft.class_3735 method_16224(JsonElement json) {
        return absent(json) ? CbAdvAny.field_16485 : decode(net.minecraft.class_3735.field_45744, json, "entity equipment predicate");
    }

    public static JsonElement method_16225(net.minecraft.class_3735 self) {
        return (Object) self instanceof CbAdvLegacy.EquipmentJson own ? own.method_16225() : super_method_16225(self);
    }

    /** the same, without dispatching to a 1.20.1 subclass's override (that subclass's own super call) */
    public static JsonElement super_method_16225(net.minecraft.class_3735 self) {
        return self == CbAdvAny.field_16485 ? JsonNull.INSTANCE : encode(net.minecraft.class_3735.field_45744, self, "entity equipment predicate");
    }

    // ------------------------------------------------------------------ BlockPredicate

    public static net.minecraft.class_4550 method_22453(JsonElement json) {
        return absent(json) ? CbAdvAny.field_20692 : decode(net.minecraft.class_4550.field_45723, json, "block predicate");
    }

    public static JsonElement method_22452(net.minecraft.class_4550 self) {
        return (Object) self instanceof CbAdvLegacy.BlockJson own ? own.method_22452() : super_method_22452(self);
    }

    /** the same, without dispatching to a 1.20.1 subclass's override (that subclass's own super call) */
    public static JsonElement super_method_22452(net.minecraft.class_4550 self) {
        return self == CbAdvAny.field_20692 ? JsonNull.INSTANCE : encode(net.minecraft.class_4550.field_45723, self, "block predicate");
    }

    // ------------------------------------------------------------------ FluidPredicate

    public static net.minecraft.class_4551 method_22474(JsonElement json) {
        return absent(json) ? CbAdvAny.field_20708 : decode(net.minecraft.class_4551.field_45753, json, "fluid predicate");
    }

    public static JsonElement method_22473(net.minecraft.class_4551 self) {
        return (Object) self instanceof CbAdvLegacy.FluidJson own ? own.method_22473() : super_method_22473(self);
    }

    /** the same, without dispatching to a 1.20.1 subclass's override (that subclass's own super call) */
    public static JsonElement super_method_22473(net.minecraft.class_4551 self) {
        return self == CbAdvAny.field_20708 ? JsonNull.INSTANCE : encode(net.minecraft.class_4551.field_45753, self, "fluid predicate");
    }

    // ------------------------------------------------------------------ LightPredicate

    public static net.minecraft.class_4552 method_22482(JsonElement json) {
        return absent(json) ? CbAdvAny.field_20712 : decode(net.minecraft.class_4552.field_45757, json, "light predicate");
    }

    public static JsonElement method_22481(net.minecraft.class_4552 self) {
        return self == CbAdvAny.field_20712 ? JsonNull.INSTANCE : encode(net.minecraft.class_4552.field_45757, self, "light predicate");
    }

    // ------------------------------------------------------------------ StatePropertiesPredicate

    /** StatePropertiesPredicate.fromJson(json): ANY for null / JSON null (1.20.2 answers an Optional) */
    public static net.minecraft.class_4559 method_22519(JsonElement json) {
        return net.minecraft.class_4559.method_22519(json).orElse(CbAdvAny.field_20736);
    }

    // ------------------------------------------------------------------ TagPredicate

    /** TagPredicate.fromJson(json, registry): {"id": tag, "expected": bool} */
    public static <T> net.minecraft.class_8129<T> method_48966(JsonElement json,
            net.minecraft.class_5321<? extends net.minecraft.class_2378<T>> registry) {
        if (json == null) {
            throw new com.google.gson.JsonParseException("Expected a tag predicate");
        }
        JsonObject o = net.minecraft.class_3518.method_15295(json, "Tag Predicate");
        net.minecraft.class_2960 id = new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(o, "id"));
        boolean expected = net.minecraft.class_3518.method_15270(o, "expected");
        return new net.minecraft.class_8129<>(net.minecraft.class_6862.method_40092(registry, id), expected);
    }

    /** TagPredicate.serializeToJson() */
    public static JsonElement method_48964(net.minecraft.class_8129<?> self) {
        return (Object) self instanceof CbAdvLegacy.TagJson own ? own.method_48964() : super_method_48964(self);
    }

    /** the same, without dispatching to a 1.20.1 subclass's override (that subclass's own super call) */
    public static JsonElement super_method_48964(net.minecraft.class_8129<?> self) {
        JsonObject o = new JsonObject();
        o.addProperty("id", self.comp_1836().comp_327().toString());
        o.addProperty("expected", self.comp_1837());
        return o;
    }

    // ------------------------------------------------------------------ MinMaxBounds

    /**
     * MinMaxBounds.serializeToJson(): any -> JsonNull, an exact value -> the number, else {min, max}.
     * A 1.20.1 subclass (the MinMaxBounds facade) answers with its own method, as 1.20.1's call did.
     */
    public static JsonElement method_9036(net.minecraft.class_2096<?> self) {
        if (self instanceof top.dext.centurybridge.facade.v1_20_2.class_2096<?> own) {
            return own.method_9036();
        }
        if (self.method_9041()) {
            return JsonNull.INSTANCE;
        }
        Optional<? extends Number> min = self.comp_1805();
        Optional<? extends Number> max = self.comp_1806();
        if (min.isPresent() && min.equals(max)) {
            return new com.google.gson.JsonPrimitive(min.get());
        }
        JsonObject o = new JsonObject();
        min.ifPresent(v -> o.addProperty("min", v));
        max.ifPresent(v -> o.addProperty("max", v));
        return o;
    }

    /** MinMaxBounds.getMin(): the bound, null when absent (a 1.20.1 subclass answers itself) */
    public static Number method_9038(net.minecraft.class_2096<?> self) {
        if (self instanceof top.dext.centurybridge.facade.v1_20_2.class_2096<?> own) {
            return own.method_9038();
        }
        return self.comp_1805().orElse(null);
    }

    /** MinMaxBounds.getMax() */
    public static Number method_9042(net.minecraft.class_2096<?> self) {
        if (self instanceof top.dext.centurybridge.facade.v1_20_2.class_2096<?> own) {
            return own.method_9042();
        }
        return self.comp_1806().orElse(null);
    }

    /**
     * MinMaxBounds.fromJson(json, any, reader, factory), the protected helper 1.20.1 subclasses parsed
     * themselves with: JSON null is {@code any}, a number is min = max, else the "min" / "max" members
     * (each optional). {@code factory} is 1.20.2's BoundsFactory (a 1.20.1 lambda is adapted to it:
     * lambdaAdapters), handed the bounds as Optionals.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_2096 method_9039(JsonElement json, net.minecraft.class_2096 any,
            java.util.function.BiFunction reader, net.minecraft.class_2096.class_2097 factory) {
        if (absent(json)) {
            return any;
        }
        if (net.minecraft.class_3518.method_15275(json)) {
            Number v = (Number) reader.apply(json, "value");
            return factory.create(Optional.of(v), Optional.of(v));
        }
        JsonObject o = net.minecraft.class_3518.method_15295(json, "value");
        Number min = o.has("min") ? (Number) reader.apply(o.get("min"), "min") : null;
        Number max = o.has("max") ? (Number) reader.apply(o.get("max"), "max") : null;
        return factory.create(Optional.ofNullable(min), Optional.ofNullable(max));
    }

    /** a 1.20.1 BoundsFactory lambda (create(min, max), nullable) as 1.20.2's (create(Optional, Optional)) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_2096.class_2097 boundsFactory(CbAdvLegacy.BoundsFactory legacy) {
        return (min, max) -> (net.minecraft.class_2096) legacy.create((Number) ((Optional) min).orElse(null),
            (Number) ((Optional) max).orElse(null));
    }

    /**
     * WrappedMinMaxBounds.getMin(): a 1.20.1 subclass's own override answers, as 1.20.1's call did.
     * (1.20.1's own methods read the fields, never getMin, so an override changed only what callers of
     * getMin saw -- the same here.)
     */
    public static Float method_9175(net.minecraft.class_2152 self) {
        return (Object) self instanceof CbAdvLegacy.WrappedMin own ? own.method_9175() : self.comp_1840();
    }

    /** WrappedMinMaxBounds.getMax() */
    public static Float method_9177(net.minecraft.class_2152 self) {
        return (Object) self instanceof CbAdvLegacy.WrappedMax own ? own.method_9177() : self.comp_1841();
    }

    /** super.getMin() in a 1.20.1 subclass: the stored bound */
    public static Float super_method_9175(net.minecraft.class_2152 self) {
        return self.comp_1840();
    }

    /** super.getMax() */
    public static Float super_method_9177(net.minecraft.class_2152 self) {
        return self.comp_1841();
    }

    // ------------------------------------------------------------------ EntitySubPredicate

    /**
     * EntitySubPredicate.ANY: matches anything, of type "any" (Types.ANY). Its own sentinel, built without the codecs
     * (1.20.1's ANY was a constant too): {"type": "any"} through EntitySubPredicate.CODEC stops decoding from 1.20.5
     * (javap client-1.20.5-intermediary-code.jar class_7378: no "any" registered), and Types.ANY is touched only when its
     * type is asked (comp_1776), so nothing that builds this predicate depends on a later release's types table. Bridges
     * that hand it to 1.20.2 records map it to Optional.empty() (isAny), as for CbAdvAny's sentinels.
     */
    private static final net.minecraft.class_7376 ANY = new net.minecraft.class_7376() {
        @Override
        public boolean method_22497(net.minecraft.class_1297 entity, net.minecraft.class_3218 level, net.minecraft.class_243 pos) {
            return true;
        }

        @Override
        public net.minecraft.class_7376.class_8745 comp_1776() {
            return net.minecraft.class_7376.class_7378.field_38724;
        }
    };

    /** EntitySubPredicate.ANY (fieldGetters) */
    public static net.minecraft.class_7376 field_38723() {
        return ANY;
    }

    /** whether {@code p} is EntitySubPredicate.ANY (this sentinel) */
    public static boolean isAny(net.minecraft.class_7376 p) {
        return p == ANY;
    }

    /** EntitySubPredicate.fromJson(json): {"type": ..., the type's fields} through 1.20.2's dispatch codec */
    public static net.minecraft.class_7376 method_43097(JsonElement json) {
        if (absent(json)) {
            return field_38723();
        }
        JsonObject o = net.minecraft.class_3518.method_15295(json, "type_specific");
        String type = net.minecraft.class_3518.method_15253(o, "type", null);
        if (type == null) {
            return field_38723();
        }
        if (!net.minecraft.class_7376.class_7378.field_38731.containsKey(type)) {
            throw new JsonSyntaxException("Unknown sub-predicate type: " + type);
        }
        return decode(net.minecraft.class_7376.field_45747, o, "entity sub-predicate");
    }

    /**
     * EntitySubPredicate.serializeCustomData(): the predicate's own fields, without "type". A 1.20.1
     * implementation answers itself; a 1.20.2 predicate is what its kind's MapCodec writes.
     */
    public static JsonObject method_22494(net.minecraft.class_7376 self) {
        if ((Object) self instanceof CbAdvLegacy.SubJson own) {
            return own.method_22494();
        }
        @SuppressWarnings("unchecked")
        MapCodec<net.minecraft.class_7376> codec = (MapCodec<net.minecraft.class_7376>) self.comp_1776().comp_1774();
        return encodeMap(codec, self, "entity sub-predicate");
    }

    /** EntitySubPredicate.serialize(): a 1.20.1 implementation's own override, else 1.20.1's default */
    public static JsonElement method_43098(net.minecraft.class_7376 self) {
        return (Object) self instanceof CbAdvLegacy.SubSerialize own ? own.method_43098() : super_method_43098(self);
    }

    /**
     * 1.20.1's default serialize() (and a 1.20.1 implementation's super call to it): ANY's kind writes
     * JsonNull, any other serializeCustomData() plus "type" -- the kind's name in Types.TYPES (null for a
     * kind no table names, as 1.20.1 wrote it)
     */
    public static JsonElement super_method_43098(net.minecraft.class_7376 self) {
        top.dext.centurybridge.facade.v1_20_2.class_7376$class_7377 type = CbAdvSubTypes.method_43099(self);
        if (type == CbAdvSubTypes.field_38724) {
            return JsonNull.INSTANCE;
        }
        JsonObject o = method_22494(self);
        o.addProperty("type", CbAdvSubTypes.field_38731().inverse().get(type));
        return o;
    }

    /** PlayerPredicate.fromJson(object) */
    public static net.minecraft.class_4553 method_22499(JsonObject json) {
        return decode(net.minecraft.class_4553.field_45770.codec(), json, "player predicate");
    }

    /** FishingHookPredicate.fromJson(object) */
    public static net.minecraft.class_4965 method_26094(JsonObject json) {
        return decode(net.minecraft.class_4965.field_45750.codec(), json, "fishing hook predicate");
    }

    /** LightningBoltPredicate.fromJson(object) */
    public static net.minecraft.class_6404 method_37238(JsonObject json) {
        return decode(net.minecraft.class_6404.field_45759.codec(), json, "lightning bolt predicate");
    }

    /** SlimePredicate.fromJson(object) */
    public static net.minecraft.class_7383 method_43158(JsonObject json) {
        return decode(net.minecraft.class_7383.field_45776.codec(), json, "slime predicate");
    }

    private CbAdvJson() {
    }
}
