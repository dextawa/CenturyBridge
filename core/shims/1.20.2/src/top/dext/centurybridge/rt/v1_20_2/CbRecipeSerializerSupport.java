package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.class_1860;
import net.minecraft.class_1866;
import net.minecraft.class_2960;
import net.minecraft.class_3955;
import net.minecraft.class_7710;

/**
 * The shared pieces of the 1.20.1 recipe-reader contract on 1.20.2 (the per-serializer bridges
 * are generated into {@link CbRecipeSerializers}). 1.20.2 reads recipe JSON through
 * RecipeSerializer.codec() and the network form through read(buf), neither of which carries the
 * recipe id a 1.20.1 reader takes: the id is the one RecipeManager / the recipe-sync packet is
 * decoding ({@link CbRecipes#CURRENT_ID}, published by RecipeLoadIdBridge / RecipeSyncIdBridge),
 * or the one a converted call passed explicitly.
 */
public final class CbRecipeSerializerSupport {

    private static final Map<Object, Codec<?>> CODECS = Collections.synchronizedMap(new WeakHashMap<>());

    /** the id of the recipe being decoded right now, for a 1.20.1 reader vanilla reached without one */
    public static class_2960 currentId() {
        class_2960 id = CbRecipes.CURRENT_ID.get();
        if (id == null) {
            throw new IllegalStateException("CenturyBridge: a 1.20.1 recipe reader needs the recipe id; 1.20.2 has"
                + " one only while RecipeManager loads a recipe or the recipe-sync packet reads one");
        }
        return id;
    }

    /** runs a 1.20.2 read with {@code id} as the id being decoded, and keeps the id with the result */
    public static <T> T withId(class_2960 id, Supplier<T> read) {
        class_2960 outer = CbRecipes.CURRENT_ID.get();
        CbRecipes.CURRENT_ID.set(id);
        try {
            return CbRecipeIds.remember(read.get(), id);
        } finally {
            if (outer == null) {
                CbRecipes.CURRENT_ID.remove();
            } else {
                CbRecipes.CURRENT_ID.set(outer);
            }
        }
    }

    /** a 1.20.1 read(id, json) answered by a 1.20.2 codec; malformed JSON throws as the 1.20.1 readers did */
    public static <T> T decode(Codec<T> codec, class_2960 id, JsonObject json) {
        return withId(id, () -> {
            DataResult<T> r = codec.parse(JsonOps.INSTANCE, json);
            return r.result().orElseThrow(() -> new JsonSyntaxException("Invalid recipe " + id + ": "
                + r.error().map(DataResult.PartialResult::message).orElse(String.valueOf(json))));
        });
    }

    /**
     * codec() of a serializer whose JSON reader is a 1.20.1 read(id, json). A map codec, so the
     * recipe type dispatch hands it the whole object ("type" included) as 1.20.1 handed the
     * reader; the id is the recipe being decoded. 1.20.1 readers had no JSON writer.
     */
    @SuppressWarnings("unchecked")
    public static <T extends class_1860<?>> Codec<T> codecOf(Object serializer, BiFunction<class_2960, JsonObject, T> reader) {
        return (Codec<T>) CODECS.computeIfAbsent(serializer, s -> new MapCodec<T>() {
            @Override
            public <O> Stream<O> keys(DynamicOps<O> ops) {
                return Stream.empty();
            }

            @Override
            public <O> DataResult<T> decode(DynamicOps<O> ops, MapLike<O> input) {
                class_2960 id = CbRecipes.CURRENT_ID.get();
                if (id == null) {
                    return DataResult.error(() -> "CenturyBridge: a 1.20.1 recipe reader needs the recipe id; 1.20.2"
                        + " has one only while RecipeManager loads a recipe or the recipe-sync packet reads one");
                }
                JsonElement json = ops.convertTo(JsonOps.INSTANCE, ops.createMap(input.entries()));
                if (!json.isJsonObject()) {
                    return DataResult.error(() -> "CenturyBridge: recipe " + id + " is not a JSON object");
                }
                try {
                    return DataResult.success(CbRecipeIds.remember(reader.apply(id, json.getAsJsonObject()), id));
                } catch (RuntimeException e) {
                    return DataResult.error(() -> "CenturyBridge: recipe " + id + ": " + e.getMessage());
                }
            }

            @Override
            public <O> RecordBuilder<O> encode(T recipe, DynamicOps<O> ops, RecordBuilder<O> prefix) {
                return prefix.withErrorsFrom(DataResult.error(
                    () -> "CenturyBridge: a 1.20.1 recipe reader has no JSON writer"));
            }
        }.codec());
    }

    // ---- SpecialRecipeSerializer.Factory: create(id, category) became create(category)

    /** a 1.20.1 factory lambda or method reference (lambdaAdapters), as the 1.20.2 factory */
    public static class_1866.class_7711<class_3955> specialFactory(CbLegacySpecialRecipeFactory legacy) {
        return category -> legacy.create(currentId(), category);
    }

    /** create(category) synthesized into a class implementing the 1.20.1 factory (overrideHooks bridge) */
    public static class_3955 specialCreate(CbLegacySpecialRecipeFactory self, class_7710 category) {
        return self.create(currentId(), category);
    }

    private CbRecipeSerializerSupport() {
    }
}
