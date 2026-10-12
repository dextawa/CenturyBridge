package top.dext.centurybridge.step.v1_20_5.compat.fapi.resource.conditions.v1;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Objects;

/**
 * Fabric API 0.97.3's (1.20.4) ConditionJsonProvider, removed in 1.20.5: a resource condition as the JSON a
 * data generator writes ({"condition": id, parameters...} under "fabric:load_conditions"). 1.20.5 reads the
 * same JSON shape through ResourceCondition's codec, so the conditions such JSON names load -- 1.20.4's
 * default ids that 1.20.5 renamed or dropped included (impl/resource/conditions/CbLegacyConditions). Bodies
 * as in 1.20.4 (Fabric API, Apache-2.0).
 */
public interface ConditionJsonProvider {

    static void write(JsonObject conditionalObject, ConditionJsonProvider... conditions) {
        if (conditions == null) {
            return;
        }
        Preconditions.checkArgument(conditions.length > 0, "Must write at least one condition.");
        if (conditionalObject.has(net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions.CONDITIONS_KEY)) {
            throw new IllegalArgumentException("Object already has a condition entry: " + conditionalObject);
        }
        JsonArray array = new JsonArray();
        for (ConditionJsonProvider condition : conditions) {
            Objects.requireNonNull(condition, "condition cannot be null");
            array.add(condition.toJson());
        }
        conditionalObject.add(net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions.CONDITIONS_KEY, array);
    }

    default JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("condition", getConditionId().toString());
        this.writeParameters(jsonObject);
        return jsonObject;
    }

    net.minecraft.class_2960 getConditionId();

    void writeParameters(JsonObject object);
}
