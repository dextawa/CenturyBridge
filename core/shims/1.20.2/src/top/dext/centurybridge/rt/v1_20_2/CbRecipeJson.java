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
 * The statics 1.20.1's readers called (ShapedRecipe.keyFromJson / patternFromJson / dissolvePattern /
 * itemStackFromJson / itemFromJson, ShapelessRecipe.Serializer.itemsFromJson, Ingredient.fromJson) are gone from
 * 1.20.2, which keeps their code in the serializers' codecs. They are restored on their classes as private statics
 * (CbRecipeJsonShapedStatics, CbRecipeJsonShapeless, CbRecipeJsonIngredientStatics), which the restored readers call;
 * the copies here, the same bodies, are what the readers' unmixable copies call. Ingredient entries are decoded with
 * 1.20.2's ingredient codec in both. ShapedRecipe.shrink stays 1.20.2's (shrink(List), the same code): both call it.
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
    /** the JSON RecipeManager.fromJson is decoding, carried from its HEAD to its RETURN (CbRecipeJsonVanillaMixin) */
    private static final ThreadLocal<JsonObject> CURRENT_JSON = new ThreadLocal<>();

    public static void carryJson(JsonObject json) {
        CURRENT_JSON.set(json);
    }

    /**
     * the RecipeHolder RecipeManager.fromJson returns: {@link #consult} on its recipe, with its id (holder.id(), a call the
     * later steps carry) and the carried JSON; the same holder unless the restored reader answered otherwise
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_8786<?> consultHolder(net.minecraft.class_8786<?> holder) {
        JsonObject json = CURRENT_JSON.get();
        CURRENT_JSON.remove();
        if (holder == null || json == null) {
            return holder;
        }
        class_1860<?> recipe = holder.comp_1933();
        class_1860<?> answer = consult(recipe, holder.comp_1932(), json);
        return answer == recipe ? holder : new net.minecraft.class_8786(holder.comp_1932(), answer);
    }

    /**
     * The classes the layer restores the recipe readers' members on (CbRecipeJson* mixins: the serializers' fromJson, ShapedRecipe's
     * and Ingredient's JSON statics). Only when a converted mod's mixin injects into one of those members (CbMixedMembers) can a
     * restored reader answer otherwise than its unmixed copy, so only then is it consulted: unmixed, 1.20.2's recipe is kept
     * without reading the JSON a second time.
     */
    private static final java.util.Set<String> RESTORED_ON = java.util.Set.of(
        "net/minecraft/class_1856", "net/minecraft/class_1866", "net/minecraft/class_1867$class_1868", "net/minecraft/class_1869",
        "net/minecraft/class_1869$class_1870", "net/minecraft/class_3957", "net/minecraft/class_3972$class_3973",
        "net/minecraft/class_8060$class_8061", "net/minecraft/class_8062$class_8063");

    public static class_1860<?> consult(class_1860<?> read, class_2960 id, JsonObject json) {
        if (!ROUTE || read == null || !CbMixedMembers.mixedOn(RESTORED_ON)) {
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
        java.lang.reflect.Method codec = codecMethod();
        for (Class<?> c = cls; c != vanilla; c = c.getSuperclass()) {
            try {
                c.getDeclaredMethod(codec.getName(), codec.getParameterTypes());
                if (!isLegacyJsonAdapter(c)) {
                    return false;
                }
            } catch (NoSuchMethodException absent) {
                // not overridden here
            }
        }
        return true;
    }

    /**
     * RecipeSerializer.codec(), looked up on RecipeSerializer itself: the owner is a class constant, so the wall's
     * reflective-string pass translates the name through that owner's table entry (an override found by this name on a
     * subclass is the override of this member, whatever the release spells it)
     */
    private static java.lang.reflect.Method codecMethod() {
        try {
            return class_1865.class.getMethod("method_53736");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("RecipeSerializer.codec() not found", e);
        }
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

    /** the entries of an ingredient read by 1.20.2's ingredient codec (Statics, the converted Ingredient.fromJson call's target) */
    public static class_1856 ingredient(JsonElement json, boolean canBeEmpty) {
        return Statics.method_8102(json, canBeEmpty);
    }

    /** the unmixable copy of Ingredient's restored fromJson(json, canBeEmpty) (CbRecipeJsonIngredientStatics.method_8102) */
    public static class_1856 goldenIngredient(JsonElement json, boolean canBeEmpty) {
        if (json == null || json.isJsonNull()) {
            throw new JsonSyntaxException("Item cannot be null");
        }
        if (json.isJsonArray() && json.getAsJsonArray().size() == 0 && !canBeEmpty) {
            throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
        }
        if (!json.isJsonObject() && !json.isJsonArray()) {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
        }
        return ingredient(json, canBeEmpty);
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
            map.put(entry.getKey(), goldenIngredient(entry.getValue(), false));
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

    /** 1.20.1 ShapedRecipe.shrink(rows): the pattern without its blank border rows and columns */
    public static String[] shrink(String... rows) {
        int first = Integer.MAX_VALUE;
        int last = 0;
        int blankTop = 0;
        int blankTail = 0;
        for (int i = 0; i < rows.length; ++i) {
            String row = rows[i];
            first = Math.min(first, firstNonSpace(row));
            int end = lastNonSpace(row);
            last = Math.max(last, end);
            if (end < 0) {
                if (blankTop == i) {
                    ++blankTop;
                }
                ++blankTail;
            } else {
                blankTail = 0;
            }
        }
        if (rows.length == blankTail) {
            return new String[0];
        }
        String[] out = new String[rows.length - blankTail - blankTop];
        for (int i = 0; i < out.length; ++i) {
            out[i] = rows[i + blankTop].substring(first, last + 1);
        }
        return out;
    }

    /** 1.20.1 ShapedRecipe.firstNonSpace(row) */
    public static int firstNonSpace(String row) {
        int i = 0;
        while (i < row.length() && row.charAt(i) == ' ') {
            ++i;
        }
        return i;
    }

    /** 1.20.1 ShapedRecipe.lastNonSpace(row) */
    public static int lastNonSpace(String row) {
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

    /** 1.20.1 ShapedRecipe.itemStackFromJson(json) */
    public static class_1799 itemStackFromJson(JsonObject json) {
        net.minecraft.class_1792 item = itemFromJson(json);
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        int count = class_3518.method_15282(json, "count", 1);
        if (count < 1) {
            throw new JsonSyntaxException("Invalid output count: " + count);
        }
        return new class_1799(item, count);
    }

    /** 1.20.1 ShapedRecipe.itemFromJson(json) */
    public static net.minecraft.class_1792 itemFromJson(JsonObject json) {
        String id = class_3518.method_15265(json, "item");
        net.minecraft.class_1792 item = class_7923.field_41178.method_17966(class_2960.method_12829(id))
            .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + id + "'"));
        if (item == net.minecraft.class_1802.field_8162) {
            throw new JsonSyntaxException("Empty ingredient not allowed here");
        }
        return item;
    }

    /** 1.20.1 ShapelessRecipe.Serializer.itemsFromJson(array): the non-empty ingredients */
    public static class_2371<class_1856> itemsFromJson(JsonArray array) {
        class_2371<class_1856> list = class_2371.method_10211();
        for (int i = 0; i < array.size(); ++i) {
            class_1856 ingredient = goldenIngredient(array.get(i), false);
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

    /**
     * SimpleCraftingRecipeSerializer's factory.create(category), through the serializer's own codec as {@link #cooking}:
     * its codec reads exactly the category
     */
    public static <T> T special(Codec<T> codec, net.minecraft.class_7710 category) {
        JsonObject json = new JsonObject();
        json.addProperty("category", category.method_15434());
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
