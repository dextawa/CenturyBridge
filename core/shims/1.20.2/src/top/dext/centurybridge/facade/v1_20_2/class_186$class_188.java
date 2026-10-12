package top.dext.centurybridge.facade.v1_20_2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

/**
 * Facade for 1.20.1 net/minecraft/class_186$class_188 (CompositeLootItemCondition.Serializer),
 * deleted in 1.20.2's loot codec rework; the any_of / all_of serializers (and a mod's own
 * composite condition serializer) extend it and implement create(terms). The 1.20.1 bodies are
 * ported: "terms" written from the condition's terms, read back into a LootCondition[] through
 * the caller's context and handed to create(). The 1.20.1 synthetic bridges method_516 /
 * method_517 are ordinary non-final methods here, so a subclass's own javac bridges override them
 * as they did.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class class_186$class_188 implements class_5335 {

    public class_186$class_188() {
    }

    /** toJson(json, condition, ctx) */
    public void method_828(JsonObject json, net.minecraft.class_186 condition, JsonSerializationContext context) {
        java.util.List<net.minecraft.class_5341> terms =
            ((top.dext.centurybridge.shims.v1_20_2.CbLootCompositeConditionTerms) condition).cb$terms();
        json.add("terms", context.serialize(terms.toArray(new net.minecraft.class_5341[0])));
    }

    /** fromJson(json, ctx) */
    public net.minecraft.class_186 method_829(JsonObject json, JsonDeserializationContext context) {
        net.minecraft.class_5341[] terms = (net.minecraft.class_5341[]) net.minecraft.class_3518.method_15272(json, "terms",
            context, net.minecraft.class_5341[].class);
        return method_51726(terms);
    }

    /** create(terms) */
    public abstract net.minecraft.class_186 method_51726(net.minecraft.class_5341[] terms);

    @Override
    public Object method_517(JsonObject json, JsonDeserializationContext context) {
        return method_829(json, context);
    }

    @Override
    public void method_516(JsonObject json, Object value, JsonSerializationContext context) {
        method_828(json, (net.minecraft.class_186) value, context);
    }
}
