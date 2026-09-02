package top.dext.centurybridge.compat.fapi;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;

/**
 * fabric-resource-conditions-api-v1's 1.20.x registration
 * {@code ResourceConditions.register(id, Predicate<JsonObject>)} (8 corpus mods).
 * Since 1.21 a condition is a codec-typed ResourceConditionType; here the codec
 * is a pass-through that hands the condition object's raw JSON to the old
 * predicate, so the mod's data-pack conditions keep evaluating unchanged
 * (staticRedirect on the surviving ResourceConditions class).
 */
public final class CbResourceConditions {

    private static final AtomicBoolean WARNED_GET = new AtomicBoolean();

    /** one registered legacy condition: the raw JSON object it was declared with */
    record JsonCondition(ResourceConditionType<JsonCondition> type, Predicate<JsonObject> predicate, JsonObject json) implements ResourceCondition {
        @Override
        public ResourceConditionType<?> getType() {
            return type;
        }

        @Override
        public boolean test(RegistryOps.RegistryInfoLookup registryLookup) {
            return predicate.test(json);
        }
    }

    /** decodes the whole condition map into a JsonObject and encodes it back verbatim */
    static final class JsonMapCodec extends MapCodec<JsonCondition> {
        private final ResourceConditionType<JsonCondition>[] typeRef;
        private final Predicate<JsonObject> predicate;

        JsonMapCodec(ResourceConditionType<JsonCondition>[] typeRef, Predicate<JsonObject> predicate) {
            this.typeRef = typeRef;
            this.predicate = predicate;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }

        @Override
        public <T> DataResult<JsonCondition> decode(DynamicOps<T> ops, MapLike<T> input) {
            JsonObject json = new JsonObject();
            input.entries().forEach(e -> {
                String key = ops.getStringValue(e.getFirst()).result().orElse(null);
                if (key != null) {
                    json.add(key, ops.convertTo(JsonOps.INSTANCE, e.getSecond()));
                }
            });
            return DataResult.success(new JsonCondition(typeRef[0], predicate, json));
        }

        @Override
        public <T> RecordBuilder<T> encode(JsonCondition input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            for (Map.Entry<String, JsonElement> e : input.json().entrySet()) {
                if (!"condition".equals(e.getKey())) {
                    prefix.add(e.getKey(), JsonOps.INSTANCE.convertTo(ops, e.getValue()));
                }
            }
            return prefix;
        }
    }

    /** was ResourceConditions.register(Identifier, Predicate&lt;JsonObject&gt;) */
    @SuppressWarnings("unchecked")
    public static void register(Identifier id, Predicate<JsonObject> predicate) {
        ResourceConditionType<JsonCondition>[] ref = new ResourceConditionType[1];
        ref[0] = ResourceConditionType.create(id, new JsonMapCodec(ref, predicate));
        ResourceConditions.register(ref[0]);
    }

    /** was ResourceConditions.get(Identifier): a legacy-registered condition's predicate, else null (codec-typed conditions cannot be viewed as JSON predicates) */
    public static Predicate<JsonObject> get(Identifier id) {
        ResourceConditionType<?> type = ResourceConditions.getConditionType(id);
        if (type != null && type.codec() instanceof JsonMapCodec jm) {
            return jm.predicate;
        }
        if (WARNED_GET.compareAndSet(false, true)) {
            System.err.println("[CenturyBridge] ResourceConditions.get(" + id + "): 26.x conditions are codec-typed; only conditions registered through the 1.20.1 predicate API can be returned");
        }
        return null;
    }

    private CbResourceConditions() {
    }
}
