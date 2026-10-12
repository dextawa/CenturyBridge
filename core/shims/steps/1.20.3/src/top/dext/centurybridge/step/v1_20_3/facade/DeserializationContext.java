package top.dext.centurybridge.step.v1_20_3.facade;

import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import java.util.List;

/**
 * Facade for 1.20.2 DeserializationContext (class_5257), gone in 1.20.3: criteria decode
 * through codecs there, and the advancement id and loot data it carried are no longer handed
 * to a trigger. A 1.20.2 trigger's createInstance(json, player, context) still takes one; the
 * codec CenturyBridge synthesizes into the mod class passes the context of the advancement
 * being loaded (CbAdvContext, filled by CbAdvServerAdvancementManagerMixin exactly where 1.20.2
 * built it: ServerAdvancementManager's per-advancement load, with that advancement's id and the
 * server's loot data).
 *
 * Every member keeps its 1.20.2 shape and behaviour. Members are public (a facade is not a game
 * class, so no access widener reaches it).
 */
public class DeserializationContext {

    /** LOGGER */
    public static final org.slf4j.Logger field_24383 = com.mojang.logging.LogUtils.getLogger();
    /** id: the advancement being deserialized */
    public final net.minecraft.class_2960 field_24384;
    /** lootData */
    public final net.minecraft.class_60 field_44474;

    public DeserializationContext(net.minecraft.class_2960 id, net.minecraft.class_60 lootData) {
        this.field_24384 = id;
        this.field_44474 = lootData;
    }

    /**
     * deserializeConditions(json, context, params), as 1.20.2: the conditions decoded with
     * LootItemConditions' codec (a malformed list is a JsonParseException), then each validated
     * against the parameter set and the loot data, every problem logged as a warning naming the
     * trigger. 1.20.2 logged the whole collected problem set after each condition; that loop is
     * kept as it was. 1.20.3's ValidationContext reports into a ProblemReporter instead of
     * holding the problems itself: a Collector plays the part of 1.20.2's problem map. A context
     * with no loot data (a decode outside an advancement load, which 1.20.2 never did) has
     * nothing to resolve references against: its conditions are decoded, not validated.
     */
    public final List<net.minecraft.class_5341> method_27796(JsonArray json, String context, net.minecraft.class_176 params) {
        List<net.minecraft.class_5341> conditions = net.minecraft.class_156.method_47526(
            net.minecraft.class_217.field_45869.listOf().parse(JsonOps.INSTANCE, json), JsonParseException::new);
        if (field_44474 == null) {
            return conditions;
        }
        net.minecraft.class_8942.class_8943 problems = new net.minecraft.class_8942.class_8943();
        net.minecraft.class_58 validation = new net.minecraft.class_58(problems, params, field_44474);
        for (net.minecraft.class_5341 condition : conditions) {
            condition.method_292(validation);
            problems.method_54948().forEach((path, problem) ->
                field_24383.warn("Found validation problem in advancement trigger {}/{}: {}", context, path, problem));
        }
        return conditions;
    }

    /** getAdvancementId() */
    public net.minecraft.class_2960 method_27795() {
        return field_24384;
    }
}
