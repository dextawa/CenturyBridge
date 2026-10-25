package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import java.util.Map;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1860;
import net.minecraft.class_1869;
import net.minecraft.class_2371;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_7710;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeIds;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * ShapedRecipe.Serializer's 1.20.1 read(id, json), 1.20.1's body: the same GsonHelper reads in the same order, the
 * private statics it called answered by CbRecipeJson, the recipe built with 1.20.2's constructor (the id kept for
 * getId(), CbRecipeIds). 1.20.2's RecipeManager reads shaped recipes through it (CbRecipeJsonVanillaMixin).
 */
@Mixin(value = class_1869.class_1870.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeJsonShaped implements CbRecipeJson.Reader {

    @CbVanillaReads("CbRecipeJsonVanillaMixin: RecipeManager.fromJson consults it, against its unmixed copy cb$goldenJson, for every shaped recipe the codec decoded")
    public class_1869 method_8164(class_2960 id, JsonObject json) {
        String group = class_3518.method_15253(json, "group", "");
        class_7710 category = class_7710.field_40252.method_47920(class_3518.method_15253(json, "category", null), class_7710.field_40251);
        Map<String, class_1856> key = CbRecipeJsonShapedAccess.cb$keyFromJson(class_3518.method_15296(json, "key"));
        String[] pattern = CbRecipeJsonShapedAccess.cb$shrink(java.util.Arrays.asList(CbRecipeJsonShapedAccess.cb$patternFromJson(class_3518.method_15261(json, "pattern"))));
        int width = pattern[0].length();
        int height = pattern.length;
        class_2371<class_1856> ingredients = CbRecipeJsonShapedAccess.cb$dissolvePattern(pattern, key, width, height);
        class_1799 result = CbRecipeJsonShapedAccess.cb$itemStackFromJson(class_3518.method_15296(json, "result"));
        boolean showNotification = class_3518.method_15258(json, "show_notification", true);
        return CbRecipeIds.remember(new class_1869(group, category, width, height, ingredients, result, showNotification), id);
    }

    /** the same 1.20.1 body under a CenturyBridge name: what method_8164 answers unmixed (CbRecipeJson.consult) */
    @Override
    public net.minecraft.class_1860<?> cb$goldenJson(class_2960 id, JsonObject json) {
        String group = class_3518.method_15253(json, "group", "");
        class_7710 category = class_7710.field_40252.method_47920(class_3518.method_15253(json, "category", null), class_7710.field_40251);
        Map<String, class_1856> key = CbRecipeJson.keyFromJson(class_3518.method_15296(json, "key"));
        String[] pattern = CbRecipeJsonShapedAccess.cb$shrink(java.util.Arrays.asList(CbRecipeJson.patternFromJson(class_3518.method_15261(json, "pattern"))));
        int width = pattern[0].length();
        int height = pattern.length;
        class_2371<class_1856> ingredients = CbRecipeJson.dissolvePattern(pattern, key, width, height);
        class_1799 result = CbRecipeJson.itemStackFromJson(class_3518.method_15296(json, "result"));
        boolean showNotification = class_3518.method_15258(json, "show_notification", true);
        return new class_1869(group, category, width, height, ingredients, result, showNotification);
    }

    @Override
    public class_1860<?> cb$readJson(class_2960 id, JsonObject json) {
        return this.method_8164(id, json);
    }
}
