package top.dext.centurybridge.facade.v26_2;

import com.google.gson.JsonObject;

/**
 * Facade for 1.20.1 net/minecraft/class_195 (AbstractCriterionConditions),
 * deleted in the 1.20.3 criterion codec rework. Legacy mods' custom criterion
 * conditions extend this; the facade keeps their classes loadable and their
 * own accessor calls working. Vanilla no longer consumes these objects on
 * 1.21.1 -- registration/trigger paths for legacy custom criteria are
 * ledgered (degraded), which matches the tombstone philosophy: load, degrade
 * lazily, never crash the boot.
 */
public abstract class class_195 implements net.minecraft.advancements.CriterionTriggerInstance {

    private final net.minecraft.resources.Identifier id;
    private final net.minecraft.advancements.predicates.ContextAwarePredicate playerPredicate;

    public class_195(net.minecraft.resources.Identifier id, net.minecraft.advancements.predicates.ContextAwarePredicate playerPredicate) {
        this.id = id;
        this.playerPredicate = playerPredicate;
    }

    /** legacy getId */
    public net.minecraft.resources.Identifier method_806() {
        return id;
    }

    public net.minecraft.advancements.predicates.ContextAwarePredicate method_27790() {
        return playerPredicate;
    }

    /** legacy toJson(serializer) -- serializer type is itself a facade */
    public JsonObject method_807(class_5267 serializer) {
        return method_807();
    }

    /** minimal legacy toJson: player predicate only (subclasses typically override and extend) */
    public JsonObject method_807() {
        JsonObject json = new JsonObject();
        // player predicate serialization dropped (toJson removed in the 1.20.3 codec wave)
        return json;
    }
}
