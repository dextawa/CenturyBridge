package top.dext.centurybridge.res;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Set;

/**
 * Worldgen and dimension files as old data packs wrote them, in the target's shape. These
 * are read by vanilla codecs into registries that load all-or-nothing: one file the codec
 * rejects stops the server ("Failed to load registries"), so a mod's feature is not the
 * only thing lost.
 *
 * <ul>
 * <li>1.20.5: int and float providers lost the "value" wrapper ({"type": "uniform",
 * "value": {"min_inclusive", "max_inclusive"}} -> the bounds beside the type).</li>
 * <li>1.21.2: a biome's carvers are one list (the old per-step {"air": [...], "liquid":
 * [...]} map; the liquid step had no vanilla carvers and is gone).</li>
 * </ul>
 */
public final class LegacyWorldgen {

    /** provider types whose parameters sat under "value" before 1.20.5 */
    private static final Set<String> WRAPPED = Set.of(
        "minecraft:uniform", "minecraft:biased_to_bottom", "minecraft:clamped", "minecraft:clamped_normal",
        "minecraft:trapezoid");

    public static JsonElement upgrade(JsonElement in, String file, String from, String to, LegacyItems.Losses losses) {
        JsonElement out = in.deepCopy();
        if (LegacyLoot.crosses(from, to, "1.20.5")) {
            out = unwrapProviders(out);
        }
        if (LegacyLoot.crosses(from, to, "1.21.2") && file.contains("/worldgen/biome/") && out.isJsonObject()) {
            JsonObject b = out.getAsJsonObject();
            if (b.has("carvers") && b.get("carvers").isJsonObject()) {
                JsonObject steps = b.getAsJsonObject("carvers");
                JsonArray list = new JsonArray();
                for (var step : steps.entrySet()) {
                    if (!step.getKey().equals("air")) {
                        if (!(step.getValue().isJsonArray() && step.getValue().getAsJsonArray().isEmpty())) {
                            losses.lost(file + ": " + step.getKey() + " carvers (1.21.2 has one carving step)");
                        }
                        continue;
                    }
                    if (step.getValue().isJsonArray()) {
                        step.getValue().getAsJsonArray().forEach(list::add);
                    } else {
                        list.add(step.getValue());
                    }
                }
                // a holder set: one element is written as the id itself
                b.add("carvers", list.size() == 1 ? list.get(0) : list);
            }
        }
        return out;
    }

    private static JsonElement unwrapProviders(JsonElement e) {
        if (e.isJsonArray()) {
            JsonArray a = e.getAsJsonArray();
            for (int i = 0; i < a.size(); i++) {
                a.set(i, unwrapProviders(a.get(i)));
            }
            return a;
        }
        if (!e.isJsonObject()) {
            return e;
        }
        JsonObject o = e.getAsJsonObject();
        for (var en : o.entrySet()) {
            en.setValue(unwrapProviders(en.getValue()));
        }
        String type = LegacyLoot.id(o, "type");
        if (type != null && WRAPPED.contains(type) && o.size() == 2 && o.has("value") && o.get("value").isJsonObject()) {
            JsonObject flat = new JsonObject();
            flat.add("type", o.get("type"));
            for (var v : o.getAsJsonObject("value").entrySet()) {
                flat.add(v.getKey(), v.getValue());
            }
            return flat;
        }
        return o;
    }

    private LegacyWorldgen() {
    }
}
