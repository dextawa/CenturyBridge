package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import net.minecraft.class_161;
import net.minecraft.class_170;
import net.minecraft.class_175;
import net.minecraft.class_185;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_5257;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvBuilders;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * Advancement.Builder.fromJson(json, context), gone from 1.20.2 (Advancement.fromJson reads the advancement itself),
 * restored on the builder as a private static (Mixin merges a class mixin's statics only when private; mods' plain calls
 * keep their staticRedirect) with 1.20.1's body: parent, display (DisplayInfo.fromJson), rewards
 * (AdvancementRewards.deserialize), criteria (Criterion.criteriaFromJson, 1.20.2's, which reads each trigger's
 * conditions), 1.20.1's requirement checks and messages, the telemetry flag. 1.20.1's private constructor taking all of
 * them is gone; the same values go into 1.20.2's builder (CbAdvBuilders.builderOf).
 *
 * ServerAdvancementManager consults it for every advancement 1.20.2 read (CbAdvJsonVanillaMixin, CbAdvBuilders.consult),
 * against its unmixable copy CbAdvBuilders.goldenFromJson.
 */
@Mixin(value = class_161.class_162.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvBuilderJson {

    /** 1.20.1 Advancement.Builder.fromJson(json, context) */
    @CbVanillaReads("CbAdvJsonVanillaMixin: ServerAdvancementManager consults it, against its unmixed copy, for every advancement 1.20.2 read")
    private static class_161.class_162 method_692(JsonObject json, class_5257 context) {
        class_2960 parent = json.has("parent") ? new class_2960(class_3518.method_15265(json, "parent")) : null;
        class_185 display = json.has("display") ? class_185.method_809(class_3518.method_15296(json, "display")) : null;
        class_170 rewards = json.has("rewards") ? class_170.method_27912(class_3518.method_15296(json, "rewards")) : class_170.field_1167;
        Map<String, class_175<?>> criteria = class_175.method_772(class_3518.method_15296(json, "criteria"), context);
        if (criteria.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
        }
        JsonArray groups = class_3518.method_15292(json, "requirements", new JsonArray());
        String[][] requirements = new String[groups.size()][];
        for (int i = 0; i < groups.size(); ++i) {
            JsonArray group = class_3518.method_15252(groups.get(i), "requirements[" + i + "]");
            requirements[i] = new String[group.size()];
            for (int j = 0; j < group.size(); ++j) {
                requirements[i][j] = class_3518.method_15287(group.get(j), "requirements[" + i + "][" + j + "]");
            }
        }
        if (requirements.length == 0) {
            requirements = new String[criteria.size()][];
            int k = 0;
            for (String name : criteria.keySet()) {
                requirements[k++] = new String[] {name};
            }
        }
        for (String[] group : requirements) {
            if (group.length == 0 && criteria.isEmpty()) {
                throw new JsonSyntaxException("Requirement entry cannot be empty");
            }
            for (String name : group) {
                if (!criteria.containsKey(name)) {
                    throw new JsonSyntaxException("Unknown required criterion '" + name + "'");
                }
            }
        }
        for (String name : criteria.keySet()) {
            boolean required = false;
            for (String[] group : requirements) {
                if (ArrayUtils.contains(group, name)) {
                    required = true;
                    break;
                }
            }
            if (!required) {
                throw new JsonSyntaxException("Criterion '" + name
                    + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
            }
        }
        boolean telemetry = class_3518.method_15258(json, "sends_telemetry_event", false);
        return CbAdvBuilders.builderOf(parent, display, rewards, criteria, requirements, telemetry);
    }
}
