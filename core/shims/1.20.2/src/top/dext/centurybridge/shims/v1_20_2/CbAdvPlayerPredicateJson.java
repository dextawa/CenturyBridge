package top.dext.centurybridge.shims.v1_20_2;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Map;
import net.minecraft.class_1934;
import net.minecraft.class_2048;
import net.minecraft.class_2096;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3445;
import net.minecraft.class_3448;
import net.minecraft.class_3518;
import net.minecraft.class_4553;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * PlayerPredicate's 1.20.1 JSON readers, gone from 1.20.2 (its MapCodec reads it), restored on PlayerPredicate as private
 * statics with 1.20.1's bodies: fromJson(json) (level, gamemode, stats -- each through getStat --, recipes, advancements --
 * each through the advancement predicate reader --, looking_at through EntityPredicate's restored reader), the advancement
 * predicate reader (a boolean "done", else criterion -> boolean) and getStat (1.20.1's messages). The values go into
 * 1.20.2's record (CbAdvRecords.playerPredicate: the stats as 1.20.2's stat matchers). 1.20.1 reached fromJson through
 * EntitySubPredicate.fromJson's "player" type; the restored EntityPredicate reader dispatches type_specific the same way
 * (CbAdvPredicateJson.subPredicateFromJson, CbAdvPlayerPredicateAccess), so 1.20.2's EntityPredicate readers consult it.
 */
@Mixin(value = class_4553.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvPlayerPredicateJson {

    /** 1.20.1 PlayerPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored EntityPredicate.fromJson reaches it through type_specific \"player\";"
        + " 1.20.2's EntityPredicate readers consult that against its unmixed copy")
    private static class_4553 method_22499(JsonObject json) {
        class_2096.class_2100 level = class_2096.class_2100.method_9056(json.get("level"));
        class_1934 gamemode = class_1934.method_8378(class_3518.method_15253(json, "gamemode", ""), null);
        Map<class_3445<?>, class_2096.class_2100> stats = Maps.newHashMap();
        JsonArray array = class_3518.method_15292(json, "stats", null);
        if (array != null) {
            for (JsonElement e : array) {
                JsonObject entry = class_3518.method_15295(e, "stats entry");
                class_2960 typeId = new class_2960(class_3518.method_15265(entry, "type"));
                class_3448<?> type = class_7923.field_41193.method_10223(typeId);
                if (type == null) {
                    throw new JsonParseException("Invalid stat type: " + typeId);
                }
                class_2960 statId = new class_2960(class_3518.method_15265(entry, "stat"));
                class_3445<?> stat = method_22496(type, statId);
                class_2096.class_2100 value = class_2096.class_2100.method_9056(entry.get("value"));
                stats.put(stat, value);
            }
        }
        Object2BooleanMap<class_2960> recipes = new Object2BooleanOpenHashMap<>();
        for (Map.Entry<String, JsonElement> e : class_3518.method_15281(json, "recipes", new JsonObject()).entrySet()) {
            recipes.put(new class_2960(e.getKey()), class_3518.method_15268(e.getValue(), "recipe present"));
        }
        Map<class_2960, class_4553.class_4556> advancements = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> e : class_3518.method_15281(json, "advancements", new JsonObject()).entrySet()) {
            advancements.put(new class_2960(e.getKey()), method_22503(e.getValue()));
        }
        class_2048 lookingAt = CbAdvEntityPredicateAccess.cb$fromJson(json.get("looking_at"));
        return CbAdvRecords.playerPredicate(level, gamemode, stats, recipes, advancements, lookingAt);
    }

    /** 1.20.1 PlayerPredicate.advancementPredicateFromJson(json) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored PlayerPredicate.fromJson calls it")
    private static class_4553.class_4556 method_22503(JsonElement json) {
        if (json.isJsonPrimitive()) {
            return new class_4553.class_4555(json.getAsBoolean());
        }
        Object2BooleanMap<String> criteria = new Object2BooleanOpenHashMap<>();
        for (Map.Entry<String, JsonElement> e : class_3518.method_15295(json, "criterion data").entrySet()) {
            boolean test = class_3518.method_15268(e.getValue(), "criterion test");
            criteria.put(e.getKey(), test);
        }
        return new class_4553.class_4554(criteria);
    }

    /** 1.20.1 PlayerPredicate.getStat(type, id) */
    @CbVanillaReads("CbAdvEntityPredicateConsult: the restored PlayerPredicate.fromJson calls it")
    private static <T> class_3445<T> method_22496(class_3448<T> type, class_2960 id) {
        class_2378<T> registry = type.method_14959();
        T value = registry.method_10223(id);
        if (value == null) {
            throw new JsonParseException("Unknown object " + id + " for stat type " + class_7923.field_41193.method_10221(type));
        }
        return type.method_14956(value);
    }
}
