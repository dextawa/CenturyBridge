package top.dext.centurybridge.facade.v1_20_4;

import com.google.gson.JsonObject;

/**
 * Facade for 1.20.1 net/minecraft/class_195 (AbstractCriterionConditions),
 * deleted in the 1.20.3 criterion codec rework. Legacy mods' custom criterion
 * conditions extend this; the facade keeps their classes loadable and their
 * own accessor calls working. Vanilla no longer consumes these objects on
 * 1.20.4 -- registration/trigger paths for legacy custom criteria are
 * ledgered (degraded), which matches the tombstone philosophy: load, degrade
 * lazily, never crash the boot.
 */
public abstract class class_195 implements net.minecraft.class_184 {

    private final net.minecraft.class_2960 id;
    private final net.minecraft.class_5258 playerPredicate;

    public class_195(net.minecraft.class_2960 id, net.minecraft.class_5258 playerPredicate) {
        this.id = id;
        this.playerPredicate = playerPredicate;
    }

    /** legacy getId */
    public net.minecraft.class_2960 method_806() {
        return id;
    }

    protected net.minecraft.class_5258 method_27790() {
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
