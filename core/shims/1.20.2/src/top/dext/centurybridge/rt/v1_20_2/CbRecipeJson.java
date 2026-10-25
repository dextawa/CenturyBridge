package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1860;
import net.minecraft.class_1865;
import net.minecraft.class_2371;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_7709;
import net.minecraft.class_7923;
import net.minecraft.class_8785;

/**
 * 1.20.1's JSON recipe readers on 1.20.2. The vanilla serializers get their 1.20.1 read(id, json) back as members
 * (CbRecipeJson* mixins, 1.20.1 bodies), so a 1.20.1 mod's mixin on a serializer's read(id, json) can change what
 * vanilla loads, as on 1.20.1.
 *
 * Vanilla's path stays 1.20.2's: RecipeManager.fromJson decodes every recipe with its serializer's codec(), so a
 * 1.20.2-era mod's mixin on that path (codec(), the codec's lambdas, RecipeManager.fromJson) acts as it does without
 * CenturyBridge. The restored reader is consulted inside that path, after the codec has decoded the recipe
 * (CbRecipeJsonVanillaMixin -> {@link #consult}): the serializer's restored member, which a converted mod's mixin may
 * change, and an unmixable copy of the same 1.20.1 body (Reader.cb$goldenJson, a CenturyBridge name no 1.20.1 mixin
 * names) both read the JSON; only where their answers differ -- a mixin on the restored member changed it -- is the
 * restored member's answer what vanilla gets. Unmixed, vanilla gets exactly the codec's recipe.
 *
 * The private statics 1.20.1's readers called (ShapedRecipe.keyFromJson / patternFromJson / shrink /
 * dissolvePattern / itemStackFromJson, ShapelessRecipe.Serializer.itemsFromJson, Ingredient.fromJson) are gone from
 * 1.20.2, which keeps their code in the serializers' codecs; they are here with 1.20.1's checks and messages, the
 * pieces 1.20.2 still has as codecs (Ingredient, the result stack) decoded with those codecs.
 *
 * Only serializers whose codec() no non-CenturyBridge subclass overrides are consulted (a subclass's own codec is
 * what it reads with), and none with -Dcenturybridge.legacyJsonRoute=false.
 */
public final class CbRecipeJson {


    /** -Dcenturybridge.legacyJsonRoute=false: the restored readers are never consulted */
    public static final boolean ROUTE = !"false".equals(System.getProperty("centurybridge.legacyJsonRoute"));

    private static final Map<Class<?>, Boolean> ROUTED = Collections.synchronizedMap(new WeakHashMap<>());

    /** a vanilla serializer with its 1.20.1 read(id, json) back */
    public interface Reader {

        /** the restored member, called virtually (a subclass's override answers; a mod's mixin on it acts) */
        class_1860<?> cb$readJson(class_2960 id, JsonObject json);

        /** the same 1.20.1 body, under a name no 1.20.1 mixin selects: what the restored member answers unmixed */
        class_1860<?> cb$goldenJson(class_2960 id, JsonObject json);
    }

    /** SimpleCookingSerializer: 1.20.1's defaultCookingTime field, set from the constructor argument 1.20.2 keeps in its codec only */
    public interface CookingTime {

        void cb$setDefaultCookingTime(int defaultCookingTime);
    }

    /**
     * RecipeManager.fromJson(id, json) has decoded {@code read} with the serializer's codec. The answer vanilla keeps:
     * {@code read}, unless the serializer's restored read(id, json) answers otherwise than its unmixed copy -- then
     * that answer (or its exception), as 1.20.1's RecipeManager.fromJson returned serializer.fromJson(id, json).
     */
    public static class_1860<?> consult(class_1860<?> read, class_2960 id, JsonObject json) {
        if (!ROUTE || read == null) {
            return read;
        }
        class_1865<?> serializer = read.method_8119();
        if (!(serializer instanceof Reader reader) || !routed(serializer.getClass())) {
            return read;
        }
        Object mixed;
        try {
            mixed = reader.cb$readJson(id, json);
        } catch (RuntimeException e) {
            mixed = e;
        }
        Object golden;
        try {
            golden = reader.cb$goldenJson(id, json);
        } catch (RuntimeException e) {
            golden = e;
        }
        if (same(serializer, mixed, golden)) {
            return read;
        }
        if (mixed instanceof RuntimeException e) {
            throw e;
        }
        return (class_1860<?>) mixed;
    }

    /** two answers of a reader: the same exception (class, message) or recipes of one class with the same network form */
    private static boolean same(class_1865<?> serializer, Object a, Object b) {
        if (a instanceof RuntimeException ea || b instanceof RuntimeException) {
            return a instanceof RuntimeException x && b instanceof RuntimeException y && x.getClass() == y.getClass()
                && java.util.Objects.equals(x.getMessage(), y.getMessage());
        }
        if (a == null || b == null || a.getClass() != b.getClass()) {
            return a == b;
        }
        return java.util.Arrays.equals(network(serializer, (class_1860<?>) a), network(serializer, (class_1860<?>) b));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static byte[] network(class_1865 serializer, class_1860<?> recipe) {
        net.minecraft.class_2540 buf = new net.minecraft.class_2540(io.netty.buffer.Unpooled.buffer());
        try {
            serializer.method_8124(buf, recipe);
            byte[] out = new byte[buf.readableBytes()];
            buf.readBytes(out);
            return out;
        } finally {
            buf.release();
        }
    }

    private static boolean routed(Class<?> cls) {
        return ROUTED.computeIfAbsent(cls, CbRecipeJson::decide);
    }

    private static boolean decide(Class<?> cls) {
        Class<?> vanilla = cls;
        while (vanilla != null && !vanilla.getName().startsWith("net.minecraft.")) {
            vanilla = vanilla.getSuperclass();
        }
        if (vanilla == null) {
            return false;
        }
        // a subclass's own codec(): what it reads with -- unless it is the one CenturyBridge synthesized into a
        // converted 1.20.1 subclass, which calls that subclass's read(id, json)
        for (Class<?> c = cls; c != vanilla; c = c.getSuperclass()) {
            try {
                c.getDeclaredMethod("method_53736");
                if (!isLegacyJsonAdapter(c)) {
                    return false;
                }
            } catch (NoSuchMethodException absent) {
                // not overridden here
            }
        }
        return true;
    }

    private static boolean isLegacyJsonAdapter(Class<?> c) {
        for (Class<?> i : c.getInterfaces()) {
            if (i.getEnclosingClass() == CbLegacySerializers.class && i.getSimpleName().endsWith("Json")) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ 1.20.1's private statics

    /** 1.20.1 Ingredient.fromJson(json, canBeEmpty): 1.20.2's ingredient codec (Statics, the converted call's target) */
    public static class_1856 ingredient(JsonElement json, boolean canBeEmpty) {
        return Statics.method_8102(json, canBeEmpty);
    }

    /** 1.20.1 ShapedRecipe.keyFromJson(json) */
    public static Map<String, class_1856> keyFromJson(JsonObject json) {
        Map<String, class_1856> map = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            map.put(entry.getKey(), ingredient(entry.getValue(), false));
        }
        map.put(" ", class_1856.field_9017);
        return map;
    }

    /** 1.20.1 ShapedRecipe.patternFromJson(array) */
    public static String[] patternFromJson(JsonArray array) {
        String[] rows = new String[array.size()];
        if (rows.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        }
        if (rows.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }
        for (int i = 0; i < rows.length; ++i) {
            String row = class_3518.method_15287(array.get(i), "pattern[" + i + "]");
            if (row.length() > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
            }
            if (i > 0 && rows[0].length() != row.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }
            rows[i] = row;
        }
        return rows;
    }

    /** 1.20.1 ShapedRecipe.shrink(rows): the rows without the blank border */
    public static String[] shrink(String... rows) {
        int first = Integer.MAX_VALUE;
        int last = 0;
        int blankTop = 0;
        int blankBottom = 0;
        for (int i = 0; i < rows.length; ++i) {
            String row = rows[i];
            first = Math.min(first, firstNonSpace(row));
            int rowLast = lastNonSpace(row);
            last = Math.max(last, rowLast);
            if (rowLast < 0) {
                if (blankTop == i) {
                    ++blankTop;
                }
                ++blankBottom;
            } else {
                blankBottom = 0;
            }
        }
        if (rows.length == blankBottom) {
            return new String[0];
        }
        String[] out = new String[rows.length - blankBottom - blankTop];
        for (int i = 0; i < out.length; ++i) {
            out[i] = rows[i + blankTop].substring(first, last + 1);
        }
        return out;
    }

    private static int firstNonSpace(String row) {
        int i = 0;
        while (i < row.length() && row.charAt(i) == ' ') {
            ++i;
        }
        return i;
    }

    private static int lastNonSpace(String row) {
        int i = row.length() - 1;
        while (i >= 0 && row.charAt(i) == ' ') {
            --i;
        }
        return i;
    }

    /** 1.20.1 ShapedRecipe.dissolvePattern(rows, key, width, height) */
    public static class_2371<class_1856> dissolvePattern(String[] rows, Map<String, class_1856> key, int width, int height) {
        class_2371<class_1856> list = class_2371.method_10213(width * height, class_1856.field_9017);
        Set<String> unused = Sets.newHashSet(key.keySet());
        unused.remove(" ");
        for (int i = 0; i < rows.length; ++i) {
            for (int j = 0; j < rows[i].length(); ++j) {
                String symbol = rows[i].substring(j, j + 1);
                class_1856 ingredient = key.get(symbol);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + symbol + "' but it's not defined in the key");
                }
                unused.remove(symbol);
                list.set(j + width * i, ingredient);
            }
        }
        if (!unused.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + unused);
        }
        return list;
    }

    /**
     * 1.20.1 ShapedRecipe.itemStackFromJson(json): 1.20.1 refused a "data" entry; the item (not air) and count
     * (at least 1, default 1) are read by 1.20.2's result-stack codec, the one its crafting recipes decode with
     */
    public static class_1799 itemStackFromJson(JsonObject json) {
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        return class_8785.field_46092.parse(JsonOps.INSTANCE, json).result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid result stack: " + json));
    }

    /** 1.20.1 ShapelessRecipe.Serializer.itemsFromJson(array): the non-empty ingredients */
    public static class_2371<class_1856> itemsFromJson(JsonArray array) {
        class_2371<class_1856> list = class_2371.method_10211();
        for (int i = 0; i < array.size(); ++i) {
            class_1856 ingredient = ingredient(array.get(i), false);
            if (!ingredient.method_8103()) {
                list.add(ingredient);
            }
        }
        return list;
    }

    // ------------------------------------------------------------------ the package-private factories

    /**
     * SimpleCookingSerializer's factory.create(group, category, ingredient, result, experience, cookingTime). The
     * factory interface is package-private and 1.20.2 calls it only from the serializer's codec, so the values go
     * to it through that codec ({@code codec}, the serializer's own, never a subclass's codec()), each in the form
     * the codec reads back as the same value: the ingredient in its codec's form, the result as its item (1.20.1's
     * cooking result was the item alone).
     */
    public static <T> T cooking(Codec<T> codec, String group, class_7709 category, class_1856 ingredient, class_1799 result,
                                float experience, int cookingTime) {
        JsonObject json = new JsonObject();
        json.addProperty("group", group);
        json.addProperty("category", category.method_15434());
        json.add("ingredient", encodeIngredient(ingredient));
        json.addProperty("result", class_7923.field_41178.method_10221(result.method_7909()).toString());
        json.addProperty("experience", experience);
        json.addProperty("cookingtime", cookingTime);
        return decodeWith(codec, json);
    }

    /**
     * SingleItemRecipe.Serializer's factory.create(group, ingredient, result), through the serializer's own codec as
     * {@link #cooking} (the result as its item and count, the form 1.20.2's stonecutting codec reads)
     */
    public static <T> T singleItem(Codec<T> codec, String group, class_1856 ingredient, class_1799 result) {
        JsonObject json = new JsonObject();
        json.addProperty("group", group);
        json.add("ingredient", encodeIngredient(ingredient));
        json.addProperty("result", class_7923.field_41178.method_10221(result.method_7909()).toString());
        json.addProperty("count", result.method_7947());
        return decodeWith(codec, json);
    }

    private static JsonElement encodeIngredient(class_1856 ingredient) {
        return class_1856.field_46096.encodeStart(JsonOps.INSTANCE, ingredient).result()
            .orElseThrow(() -> new JsonSyntaxException("Ingredient has no JSON form: " + ingredient));
    }

    private static <T> T decodeWith(Codec<T> codec, JsonObject json) {
        com.mojang.serialization.DataResult<T> r = codec.parse(JsonOps.INSTANCE, json);
        return r.result().orElseThrow(() -> new JsonSyntaxException(
            r.error().map(com.mojang.serialization.DataResult.PartialResult::message).orElse(String.valueOf(json))));
    }

    private CbRecipeJson() {
    }
}
