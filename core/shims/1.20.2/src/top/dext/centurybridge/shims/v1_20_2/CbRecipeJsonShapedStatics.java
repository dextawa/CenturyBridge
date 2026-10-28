package top.dext.centurybridge.shims.v1_20_2;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1856;
import net.minecraft.class_1869;
import net.minecraft.class_2371;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * ShapedRecipe's 1.20.1 JSON statics, gone from 1.20.2 (its codecs hold that code), restored on ShapedRecipe with
 * 1.20.1's bodies as private statics (Mixin merges a class mixin's statics only when private; mods' plain calls of the
 * public ones keep their staticRedirects). The restored shaped / smithing transform readers call them
 * (CbRecipeJsonShapedAccess), and RecipeManager.fromJson consults those readers (CbRecipeJson.consult): a 1.20.1 mod's
 * mixin on one of these changes what vanilla loads as on 1.20.1; unmixed, the readers answer as their unmixable copies
 * (the same bodies in CbRecipeJson) and vanilla keeps 1.20.2's recipe. Ingredient.fromJson is Ingredient's restored
 * static (CbRecipeJsonIngredientAccess). shrink(String...) is restored too (1.20.2 has only shrink(List), which 1.20.3
 * moves into ShapedRecipePattern, so a reader calling it would stop linking once the layer climbs past 1.20.2). Its
 * helpers firstNonSpace / lastNonSpace stand unchanged in 1.20.2 (1.20.2's own shrink and codec call them): the
 * restored shrink computes them with CbRecipeJson's copies, so a mod's mixin on them acts on 1.20.2's codec path itself
 * and the consulted readers answer as their unmixed copies there.
 */
@Mixin(value = class_1869.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeJsonShapedStatics {

    /** 1.20.1 ShapedRecipe.keyFromJson(json) */
    @CbVanillaReads("CbRecipeJsonVanillaMixin: the restored shaped reader calls it; RecipeManager.fromJson consults that reader against its unmixed copy")
    private static Map<String, class_1856> method_8157(JsonObject json) {
        Map<String, class_1856> map = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            map.put(entry.getKey(), CbRecipeJsonIngredientAccess.cb$fromJson(entry.getValue(), false));
        }
        map.put(" ", class_1856.field_9017);
        return map;
    }

    /** 1.20.1 ShapedRecipe.patternFromJson(array) */
    @CbVanillaReads("CbRecipeJsonVanillaMixin: the restored shaped reader calls it; RecipeManager.fromJson consults that reader against its unmixed copy")
    private static String[] method_8145(JsonArray array) {
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

    /** 1.20.1 ShapedRecipe.shrink(rows) (package-private static on 1.20.1; Mixin merges a class mixin's static only when private) */
    @CbVanillaReads("CbRecipeJsonVanillaMixin: the restored shaped reader calls it; RecipeManager.fromJson consults that reader against its unmixed copy")
    private static String[] method_8146(String... rows) {
        int first = Integer.MAX_VALUE;
        int last = 0;
        int blankTop = 0;
        int blankTail = 0;
        for (int i = 0; i < rows.length; ++i) {
            String row = rows[i];
            first = Math.min(first, CbRecipeJson.firstNonSpace(row));
            int end = CbRecipeJson.lastNonSpace(row);
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

    /** 1.20.1 ShapedRecipe.dissolvePattern(rows, key, width, height) */
    @CbVanillaReads("CbRecipeJsonVanillaMixin: the restored shaped reader calls it; RecipeManager.fromJson consults that reader against its unmixed copy")
    private static class_2371<class_1856> method_8148(String[] rows, Map<String, class_1856> key, int width, int height) {
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
    @CbVanillaReads("CbRecipeJsonVanillaMixin: the restored shaped / smithing transform readers call it; RecipeManager.fromJson consults them against their unmixed copies")
    private static class_1799 method_35228(JsonObject json) {
        class_1792 item = method_8155(json);
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
    @CbVanillaReads("CbRecipeJsonVanillaMixin: itemStackFromJson above calls it, which the consulted restored readers call")
    private static class_1792 method_8155(JsonObject json) {
        String id = class_3518.method_15265(json, "item");
        class_1792 item = class_7923.field_41178.method_17966(class_2960.method_12829(id))
            .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + id + "'"));
        if (item == class_1802.field_8162) {
            throw new JsonSyntaxException("Empty ingredient not allowed here");
        }
        return item;
    }
}
