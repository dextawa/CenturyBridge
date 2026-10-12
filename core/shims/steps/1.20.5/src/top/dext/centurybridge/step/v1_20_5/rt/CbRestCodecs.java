package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 1.20.4 ExtraCodecs helpers 1.20.5 removed when DataFixerUpper 7 took them over. Each answers what
 * 1.20.4's did: DataFixerUpper 7's own helper where it is 1.20.4's code moved there (compared
 * instruction by instruction: xor's XorCodec, either's EitherCodec, recursive / lazyInitializedCodec's
 * RecursiveCodec, the strict optional field), 1.20.4's body otherwise (validate keeps a map codec a
 * map codec, which a dispatch inlines; the string, alternative and JSON helpers keep their messages).
 */
public final class CbRestCodecs {

    /** ExtraCodecs.xor: exactly one of the two must read */
    public static <F, S> Codec<Either<F, S>> method_33817(Codec<F> first, Codec<S> second) {
        return Codec.xor(first, second);
    }

    /** ExtraCodecs.either: the first, else the second (DataFixerUpper 7's EitherCodec is 1.20.4's) */
    public static <F, S> com.mojang.serialization.codecs.EitherCodec<F, S> method_53702(Codec<F> first, Codec<S> second) {
        return new com.mojang.serialization.codecs.EitherCodec<>(first, second);
    }

    /**
     * 1.20.4 EitherCodec.encode(either, ops, prefix), the specialised method intermediary named
     * method_37945 on Mojang's class; DataFixerUpper 7's EitherCodec (the renamed class) calls it encode
     */
    public static <F, S, T> DataResult<T> method_37945(com.mojang.serialization.codecs.EitherCodec<F, S> codec,
                                                       Either<F, S> input, com.mojang.serialization.DynamicOps<T> ops, T prefix) {
        return codec.encode(input, ops, prefix);
    }

    /** ExtraCodecs.stringResolverCodec(toString, fromString), 1.20.4's messages */
    public static <E> Codec<E> method_39508(Function<E, String> toString, Function<String, E> fromString) {
        return Codec.STRING.flatXmap(
            name -> Optional.ofNullable(fromString.apply(name)).map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Unknown element name:" + name)),
            e -> Optional.ofNullable(toString.apply(e)).map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Element with unknown name: " + e)));
    }

    /** ExtraCodecs.validate(codec, checker): a map codec's codec stays one (its map codec validated) */
    public static <T> Codec<T> method_48112(Codec<T> codec, Function<T, DataResult<T>> checker) {
        if (codec instanceof MapCodec.MapCodecCodec<T> mapCodec) {
            return method_51699(mapCodec.codec(), checker).codec();
        }
        return codec.flatXmap(checker, checker);
    }

    /** ExtraCodecs.validate(mapCodec, checker) */
    public static <T> MapCodec<T> method_51699(MapCodec<T> codec, Function<T, DataResult<T>> checker) {
        return codec.flatXmap(checker, checker);
    }

    /** ExtraCodecs.recursive(name, wrapped) */
    public static <T> Codec<T> method_53052(String name, Function<Codec<T>, Codec<T>> wrapped) {
        return Codec.recursive(name, wrapped);
    }

    /** ExtraCodecs.lazyInitializedCodec(supplier) */
    public static <A> Codec<A> method_39240(Supplier<Codec<A>> supplier) {
        return Codec.lazyInitialized(supplier);
    }

    /** ExtraCodecs.strictOptionalField(codec, name): a present field must read (DataFixerUpper 7's optionalFieldOf) */
    public static <A> MapCodec<Optional<A>> method_53048(Codec<A> codec, String name) {
        return codec.optionalFieldOf(name);
    }

    /** ExtraCodecs.strictOptionalField(codec, name, default) */
    public static <A> MapCodec<A> method_53049(Codec<A> codec, String name, A fallback) {
        return codec.optionalFieldOf(name, fallback);
    }

    /** ExtraCodecs.sizeLimitedString(min, max), 1.20.4's messages */
    public static Codec<String> method_48109(int min, int max) {
        return method_48112(Codec.STRING, s -> {
            int length = s.length();
            if (length < min) {
                return DataResult.error(() -> "String \"" + s + "\" is too short: " + length + ", expected range [" + min + "-" + max + "]");
            }
            if (length > max) {
                return DataResult.error(() -> "String \"" + s + "\" is too long: " + length + ", expected range [" + min + "-" + max + "]");
            }
            return DataResult.success(s);
        });
    }

    /** ExtraCodecs.withAlternative(primary, alternative): either one reads, the primary writes */
    public static <T> Codec<T> method_52465(Codec<T> primary, Codec<? extends T> alternative) {
        return Codec.either(primary, alternative).xmap(e -> e.map(l -> l, r -> r), Either::left);
    }

    /** ExtraCodecs.withAlternative(primary, alternative, converter) */
    public static <T, U> Codec<T> method_52458(Codec<T> primary, Codec<U> alternative, Function<U, T> converter) {
        return Codec.either(primary, alternative).xmap(e -> e.map(l -> l, converter), Either::left);
    }

    private static final Codec<com.google.gson.JsonElement> FLAT_JSON = Codec.STRING.flatXmap(s -> {
        try {
            return DataResult.success(com.google.gson.JsonParser.parseString(s));
        } catch (com.google.gson.JsonParseException e) {
            return DataResult.error(e::getMessage);
        }
    }, json -> {
        try {
            return DataResult.success(net.minecraft.class_3518.method_43680(json));
        } catch (IllegalArgumentException e) {
            return DataResult.error(e::getMessage);
        }
    });

    /** ExtraCodecs.FLAT_JSON: JSON as its stable string */
    public static Codec<com.google.gson.JsonElement> field_43173() {
        return FLAT_JSON;
    }

    private CbRestCodecs() {
    }
}
