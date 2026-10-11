package top.dext.centurybridge.res;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Set;

/**
 * An advancement file as an old data pack wrote it, in the target's shape: the display
 * icon is an item stack (1.20.5: "id" and components), and every predicate inside the
 * criteria -- items, entities ("player" and the rest, an entity predicate or a list of
 * loot conditions), locations, damage sources -- goes through the same rules as in loot
 * tables. Trigger conditions keep their field names across these releases; the fields
 * that hold predicates are told by name.
 */
public final class LegacyAdvancements {

    private static final Set<String> ITEM = Set.of("item", "rod", "tool");
    private static final Set<String> ITEM_LIST = Set.of("items", "ingredients");
    private static final Set<String> ENTITY = Set.of(
        "player", "entity", "parent", "partner", "child", "zombie", "villager", "projectile", "shooter",
        "lightning", "source", "direct_entity", "source_entity", "killer", "cause");
    private static final Set<String> ENTITY_LIST = Set.of("victims", "bystander");
    private static final Set<String> DAMAGE = Set.of("damage", "killing_blow", "type");

    public static JsonObject upgrade(JsonObject in, String from, String to, LegacyItems.Losses losses) {
        JsonObject a = in.deepCopy();
        if (a.has("display") && a.get("display").isJsonObject()) {
            JsonObject d = a.getAsJsonObject("display");
            if (d.has("icon") && d.get("icon").isJsonObject() && LegacyLoot.crosses(from, to, "1.20.5")) {
                d.add("icon", LegacyItems.stack(d.getAsJsonObject("icon"), to, losses));
            }
        }
        if (a.has("criteria") && a.get("criteria").isJsonObject()) {
            for (var c : a.getAsJsonObject("criteria").entrySet()) {
                if (c.getValue().isJsonObject() && c.getValue().getAsJsonObject().has("conditions")
                        && c.getValue().getAsJsonObject().get("conditions").isJsonObject()) {
                    JsonObject crit = c.getValue().getAsJsonObject();
                    crit.add("conditions", conditions(crit.getAsJsonObject("conditions"), from, to, losses));
                }
            }
        }
        return a;
    }

    /** a trigger's conditions object */
    public static JsonObject conditions(JsonObject in, String from, String to, LegacyItems.Losses losses) {
        JsonObject out = new JsonObject();
        for (var e : in.entrySet()) {
            String k = e.getKey();
            JsonElement v = e.getValue();
            if (ITEM.contains(k) && v.isJsonObject()) {
                v = LegacyLoot.itemPredicate(v.getAsJsonObject(), from, to, losses);
            } else if (ITEM_LIST.contains(k) && v.isJsonArray()) {
                JsonArray l = new JsonArray();
                for (JsonElement x : v.getAsJsonArray()) {
                    l.add(x.isJsonObject() ? LegacyLoot.itemPredicate(x.getAsJsonObject(), from, to, losses) : x);
                }
                v = l;
            } else if (ENTITY.contains(k)) {
                v = contextPredicate(v, from, to, losses);
            } else if (ENTITY_LIST.contains(k) && v.isJsonArray()) {
                JsonArray l = new JsonArray();
                for (JsonElement x : v.getAsJsonArray()) {
                    l.add(contextPredicate(x, from, to, losses));
                }
                v = l;
            } else if (k.equals("location") && v.isJsonArray()) {
                v = LegacyLoot.conditions(v.getAsJsonArray(), from, to, losses);
            } else if (k.equals("location") && v.isJsonObject()) {
                v = LegacyLoot.locationPredicate(v.getAsJsonObject(), from, to, losses);
            } else if (DAMAGE.contains(k) && v.isJsonObject()) {
                v = conditions(v.getAsJsonObject(), from, to, losses); // damage (source) predicates nest entities
            }
            out.add(k, v);
        }
        return out;
    }

    /** an entity slot of a criterion: an entity predicate (the old form) or a list of loot conditions */
    private static JsonElement contextPredicate(JsonElement v, String from, String to, LegacyItems.Losses losses) {
        if (v.isJsonArray()) {
            return LegacyLoot.conditions(v.getAsJsonArray(), from, to, losses);
        }
        if (v.isJsonObject()) {
            return LegacyLoot.entityPredicate(v.getAsJsonObject(), from, to, losses);
        }
        return v;
    }

    private LegacyAdvancements() {
    }
}
