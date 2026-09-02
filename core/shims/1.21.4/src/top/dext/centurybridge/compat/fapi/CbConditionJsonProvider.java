package top.dext.centurybridge.compat.fapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.class_2960;

/**
 * fabric-resource-conditions-api-v1's ConditionJsonProvider as it was before the 1.20.5 codec rework: a
 * condition is an id plus the parameters it writes into a JSON object, and {@code write} attaches a list of
 * them under "fabric:load_conditions". Pure JSON, still the format the 1.21.4 loader reads (built-in ids
 * see CbDefaultResourceConditions; custom ids resolve through CbResourceConditions.register).
 * classRename: net/fabricmc/fabric/api/resource/conditions/v1/ConditionJsonProvider -> this interface.
 */
public interface CbConditionJsonProvider {

    String CONDITIONS_KEY = "fabric:load_conditions";
    String CONDITION_ID_KEY = "condition";

    static void write(JsonObject baseObject, CbConditionJsonProvider... conditions) {
        if (conditions.length == 0) {
            return;
        }
        if (baseObject.has(CONDITIONS_KEY)) {
            throw new IllegalArgumentException("Object already has a condition entry: " + baseObject);
        }
        JsonArray array = new JsonArray();
        for (CbConditionJsonProvider condition : conditions) {
            array.add(condition.toJson());
        }
        baseObject.add(CONDITIONS_KEY, array);
    }

    default JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty(CONDITION_ID_KEY, getConditionId().toString());
        writeParameters(object);
        return object;
    }

    class_2960 getConditionId();

    void writeParameters(JsonObject object);
}
