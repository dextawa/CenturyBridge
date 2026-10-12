package top.dext.centurybridge.compat.fapi.impl.recipe;

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
import java.util.stream.Stream;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

/**
 * Fabric API recipe API v1's custom ingredient serializers changed from JSON read / write (1.20.1) to a
 * codec (1.20.2's getCodec(allowEmpty)).
 *
 * <ul>
 * <li>A 1.20.1 serializer gets getCodec synthesized (overrideHooks bridge mode): a MAP codec, because
 * 1.20.2 dispatches custom ingredients on "fabric:type" and hands a map codec the whole JSON object --
 * the type key included, exactly the object 1.20.1 handed read -- while a plain codec would only be
 * handed a "value" entry. Decoding is the serializer's read, encoding its write into a fresh object.
 * allowEmpty is not passed on: 1.20.1's read and write had no such parameter.</li>
 * <li>A 1.20.1 call of read / write on a serializer (CustomIngredientSerializer-typed) reaches a 1.20.1
 * serializer's own read / write, and any other serializer through its codec, with empty ingredients
 * allowed as 1.20.1's built-in serializers read nested ingredients (Ingredient.fromJson(json) is
 * fromJson(json, true) in 1.20.1); a parse failure is the JsonSyntaxException 1.20.1's readers threw.</li>
 * </ul>
 */
public final class CbFapiIngredients {

    private CbFapiIngredients() {
    }

    /** the getCodec(allowEmpty) synthesized for a 1.20.1 serializer */
    public static Codec<CustomIngredient> getCodec(CbLegacyIngredientSerializer serializer, boolean allowEmpty) {
        return new LegacyCodec(serializer).codec();
    }

    /** CustomIngredientSerializer.read(JsonObject) */
    public static CustomIngredient read(CustomIngredientSerializer<?> serializer, JsonObject json) {
        if (serializer instanceof CbLegacyIngredientSerializer legacy) {
            return legacy.read(json);
        }
        DataResult<? extends CustomIngredient> result = serializer.getCodec(true).parse(JsonOps.INSTANCE, json);
        return result.result().orElseThrow(() -> new JsonSyntaxException(result.error().get().message()));
    }

    /** CustomIngredientSerializer.write(JsonObject, ingredient) */
    @SuppressWarnings("unchecked")
    public static void write(CustomIngredientSerializer<?> serializer, JsonObject json, CustomIngredient ingredient) {
        if (serializer instanceof CbLegacyIngredientSerializer legacy) {
            legacy.write(json, ingredient);
            return;
        }
        Codec<CustomIngredient> codec = (Codec<CustomIngredient>) serializer.getCodec(true);
        DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, ingredient);
        JsonElement encoded = result.result().orElseThrow(() -> new IllegalStateException(
            "CenturyBridge: " + serializer.getIdentifier() + " cannot write this ingredient: " + result.error().get().message()));
        if (!(encoded instanceof JsonObject object)) {
            throw new IllegalStateException("CenturyBridge: " + serializer.getIdentifier() + " writes " + encoded + ", not a JSON object");
        }
        for (var entry : object.entrySet()) {
            json.add(entry.getKey(), entry.getValue());
        }
    }

    private static final class LegacyCodec extends MapCodec<CustomIngredient> {

        private final CbLegacyIngredientSerializer serializer;

        LegacyCodec(CbLegacyIngredientSerializer serializer) {
            this.serializer = serializer;
        }

        @Override
        public <O> Stream<O> keys(DynamicOps<O> ops) {
            return Stream.empty();
        }

        @Override
        public <O> DataResult<CustomIngredient> decode(DynamicOps<O> ops, MapLike<O> input) {
            JsonElement element = ops.convertTo(JsonOps.INSTANCE, ops.createMap(input.entries()));
            if (!(element instanceof JsonObject object)) {
                return DataResult.error(() -> "Expected a JSON object for a custom ingredient, got " + element);
            }
            try {
                return DataResult.success(serializer.read(object));
            } catch (RuntimeException e) {
                return DataResult.error(e::getMessage);
            }
        }

        @Override
        public <O> RecordBuilder<O> encode(CustomIngredient value, DynamicOps<O> ops, RecordBuilder<O> prefix) {
            JsonObject out = new JsonObject();
            try {
                serializer.write(out, value);
            } catch (RuntimeException e) {
                return prefix.withErrorsFrom(DataResult.error(e::getMessage));
            }
            for (var entry : out.entrySet()) {
                prefix.add(entry.getKey(), JsonOps.INSTANCE.convertTo(ops, entry.getValue()));
            }
            return prefix;
        }

        @Override
        public String toString() {
            return "CenturyBridge 1.20.1 ingredient serializer " + serializer.getClass().getName();
        }
    }
}
